/*
 *   Copyright (C) 2016 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Updater;

import at.pcgamingfreaks.Updater.UpdateProviders.NotSuccessfullyQueriedException;
import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;
import at.pcgamingfreaks.Utils;
import at.pcgamingfreaks.yaml.YAML;

import com.google.common.io.ByteStreams;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is a very raw implementation of a plugin updater only using java functions.
 * This way we can use the same code on Bukkit/Spigot and BungeeCord
 */
public abstract class Updater
{
	private final static int BUFFER_SIZE = 1024;

	private final File pluginsFolder, updateFolder;
	private final UpdateProvider updateProvider;
	private final boolean announceDownloadProgress, downloadDependencies;
	private final Logger logger;
	private final String localVersion, targetFileName;

	private UpdateResult result;

	protected Updater(File pluginsFolder, boolean announceDownloadProgress, boolean downloadDependencies, Logger logger, UpdateProvider updateProvider, String localVersion, String targetFileName)
	{
		this(pluginsFolder, new File(pluginsFolder, "updates"), announceDownloadProgress, downloadDependencies, logger, updateProvider, localVersion, targetFileName);
	}

	protected Updater(File pluginsFolder, File updateFolder, boolean announceDownloadProgress, boolean downloadDependencies, Logger logger, UpdateProvider updateProvider, String localVersion, String targetFileName)
	{
		this.pluginsFolder = pluginsFolder;
		this.updateFolder = updateFolder;
		this.updateProvider = updateProvider;
		this.announceDownloadProgress = announceDownloadProgress;
		this.downloadDependencies = downloadDependencies;
		this.logger = logger;
		this.localVersion = localVersion;
		this.targetFileName = targetFileName;

		//region Check if updater is disabled globally
		// We check if there is a gravity updater config and if updating is globally disabled. We don't create it if it doesn't exists!
		final File gravityUpdaterConfigFile = new File(pluginsFolder, "Updater" + File.separator + "config.yml");
		if(gravityUpdaterConfigFile.exists())
		{
			try
			{
				YAML gravityUpdaterGlobalConfig = new YAML(gravityUpdaterConfigFile);
				if(gravityUpdaterGlobalConfig.getBoolean("disable", false))
				{
					result = UpdateResult.DISABLED;
				}
			}
			catch(Exception ignored) {}
		}
		//endregion
	}

	/**
	 * Used to sync back after async update or update check.
	 *
	 * @param runnable The runnable that will be synced.
	 */
	protected abstract void runSync(Runnable runnable);

	/**
	 * Used to run the update or check functions async.
	 *
	 * @param runnable The runnable that should run async.
	 */
	protected abstract void runAsync(Runnable runnable);

	/**
	 * Gets the plugin author for the info output.
	 *
	 * @return The plugin author.
	 */
	protected abstract @NotNull String getAuthor();

	/**
	 * Waits for the async worker to finish.
	 * We need to prevent the server from closing while we still work.
	 */
	public abstract void waitForAsyncOperation();

	//region version checking logic

	/**
	 * This function prepares a given version string to be interpreted by the updater.
	 * The version therefor will be split on each "." to get the individual parts of the version string.
	 * If it's a snapshot/alpha/beta build we reduce her last digit by 1 so that the updater will kick in as soon as the final of the version is released.
	 *
	 * @param version The version to prepare for interpretation
	 * @return The prepared version
	 */
	protected String[] prepareVersion(String version)
	{
		String[] v = version.toLowerCase().split("-")[0].split(Pattern.quote("."));
		try
		{
			if(version.contains("snapshot") || version.contains("alpha") || version.contains("beta"))
			{
				if(v.length == 1)
				{
					v = new String[] { Integer.toString(Integer.parseInt(v[0]) - 1), Integer.toString(Integer.MAX_VALUE) };
				}
				else
				{
					for(int i = v.length - 1; i > 0; i--)
					{
						if(Integer.parseInt(v[i]) > 0)
						{
							v[i] = Integer.toString(Integer.parseInt(v[i]) - 1);
							break;
						}
					}
				}
			}
		}
		catch(Exception ignored) {}
		return v;
	}

	/**
	 * This method provides a basic version comparison. If you don't like it's behavior please Override it in!
	 * <p> With default behavior, the Updater only supports this format: <b>\d(.\d)*(-SOMETHING)*</b>
	 * If the version string doesn't match this scheme the fallback of comparing local and remote version will be used.</p>
	 *
	 * @param remoteVersion the remote version
	 * @return true if the updater should consider the remote version an update, false if not.
	 */
	protected boolean shouldUpdate(String remoteVersion)
	{
		String[] locVersion = prepareVersion(localVersion), remVersion = prepareVersion(remoteVersion);
		try
		{
			int c = Math.min(locVersion.length, remVersion.length);
			for(int i = 0; i < c; i++)
			{
				int r = Integer.parseInt(remVersion[i]), l = Integer.parseInt(locVersion[i]);
				if(r > l)
				{
					return true;
				}
				else if(r < l)
				{
					return false;
				}
			}
			// If both version are the same for the length of the shorter version the version that has more digits probably is the newer one.
			if(remVersion.length > locVersion.length)
			{
				return true;
			}
		}
		catch(Exception e)
		{
			// There was a problem parsing the version. Use the fallback (if they don't match the remote version is the newer one)
			logger.warning("Failed to determine the newer version between local version \"" + localVersion +
					"\" and remote version \"" + remoteVersion + "\"! Using fallback method (if they don't match the remote version is the newer one)!");
			return !localVersion.equalsIgnoreCase(remoteVersion);
		}
		return false;
	}

	/**
	 * Check to see if the program should continue by evaluating whether the plugin is already updated, or shouldn't be updated.
	 *
	 * @param remoteVersion The version to compare against
	 * @return true if the version was located and is newer then the local version
	 */
	protected boolean versionCheck(String remoteVersion)
	{
		if(remoteVersion != null)
		{
			if(!this.shouldUpdate(remoteVersion))
			{
				// We already have the latest version, or this build is tagged for no-update
				result = UpdateResult.NO_UPDATE;
				return false;
			}
		}
		else
		{
			// OMG!!! We have no version to work with!
			logger.warning("There was a problem retrieving the remote version of the plugin!");
			logger.warning("You should contact the plugin author (" + getAuthor() + ") about this!");
			result = UpdateResult.FAIL_NO_VERSION_FOUND;
			return false;
		}
		return true;
	}
	//endregion

	protected void download(URL url, String fileName) // Saves file into servers update directory
	{
		download(url, fileName, 0);
	}

	protected void download(URL url, String fileName, int movedCount) // Saves file into servers update directory
	{
		if(!updateFolder.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			updateFolder.mkdirs();
		}
		try
		{
			//region Allow url redirect
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(15000);
			switch (connection.getResponseCode())
			{
				case HttpURLConnection.HTTP_MOVED_PERM:
				case HttpURLConnection.HTTP_MOVED_TEMP:
					if(movedCount == 5)
					{
						logger.warning("Target url moved more than 5 times. Abort.");
						result = UpdateResult.FAIL_DOWNLOAD;
						return;
					}
					download(new URL(url, connection.getHeaderField("Location")), fileName, ++movedCount);
					return;
			}
			//endregion
			long fileLength = connection.getContentLengthLong();
			int count, percent, percentHelper = -1;
			File downloadFile = new File(updateFolder.getAbsolutePath() + File.separator + fileName);
			MessageDigest md5HashGenerator = updateProvider.provideMD5Checksum() ? MessageDigest.getInstance("MD5") : null;
			try(InputStream inputStream = (md5HashGenerator != null) ? new DigestInputStream(new BufferedInputStream(connection.getInputStream()), md5HashGenerator) : new BufferedInputStream(url.openStream());
			    FileOutputStream outputStream = new FileOutputStream(downloadFile))
			{
				byte[] buffer = new byte[BUFFER_SIZE];
				if(announceDownloadProgress)
				{
					logger.info("Start downloading update: " + updateProvider.getLatestVersion());
				}
				long downloaded = 0;
				while((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1)
				{
					downloaded += count;
					outputStream.write(buffer, 0, count);
					percent = (int) ((downloaded * 100) / fileLength);
					if(announceDownloadProgress && percent % 10 == 0 && percent / 10 > percentHelper)
					{
						percentHelper++;
						logger.info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
					}
				}
				outputStream.flush();
			}
			if(md5HashGenerator != null)
			{
				String MD5Download = Utils.byteArrayToHex(md5HashGenerator.digest()).toLowerCase(), MD5Target = updateProvider.getLatestChecksum().toLowerCase();
				if(!MD5Download.equals(MD5Target))
				{
					logger.warning("The auto-updater was able to download the file, but the checksum did not match! Delete file.");
					logger.warning("Checksum expected: " + MD5Target + " Checksum download: " + MD5Download);
					result = UpdateResult.FAIL_DOWNLOAD;
					//noinspection ResultOfMethodCallIgnored
					downloadFile.delete();
					return;
				}
			}
			if(downloadFile.getName().endsWith(".zip"))
			{
				unzip(downloadFile);
			}
			if(result != UpdateResult.FAIL_DOWNLOAD && announceDownloadProgress)
			{
				result = UpdateResult.SUCCESS;
				logger.info("Finished updating.");
			}
		}
		catch(Exception ignored)
		{
			logger.warning("The auto-updater tried to download a new update, but was unsuccessful.");
			ignored.printStackTrace();
			result = UpdateResult.FAIL_DOWNLOAD;
		}
	}

	/**
	 * This is just a very basic implementation to unzip zip files.
	 * It will just extract all the .jar files from the .zip file and ignore all other files.
	 * If you like to do more please override this function with your own code to process the zip file.
	 *
	 * @param file The downloaded zip file
	 */
	protected void unzip(File file)
	{
		try(ZipFile zipFile = new ZipFile(file))
		{
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			ZipEntry entry;
			File destinationFilePath;
			while(e.hasMoreElements())
			{
				entry = e.nextElement();
				if(!entry.getName().toLowerCase().endsWith(".jar"))
					continue;
				destinationFilePath = new File(updateFolder, entry.getName());
				try(BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry)); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destinationFilePath), BUFFER_SIZE))
				{
					ByteStreams.copy(bis, bos);
					bos.flush();
				}
			}
		}
		catch(IOException e)
		{
			logger.log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", e);
			result = UpdateResult.FAIL_DOWNLOAD;
		}
		//noinspection ResultOfMethodCallIgnored
		file.delete();
	}

	protected boolean isPluginFile(String name)
	{
		//noinspection ConstantConditions
		for(final File file : pluginsFolder.listFiles())
		{
			if(file.getName().equals(name)) return true;
		}
		return false;
	}

	/**
	 * Gets the latest remote version of the last query.
	 *
	 * @return The latest remote version of the last query. Null if there wasn't a successful query before.
	 */
	protected String getRemoteVersion()
	{
		try
		{
			return updateProvider.getLatestVersion();
		}
		catch(NotSuccessfullyQueriedException ignored) {}
		return null;
	}

	public void update()
	{
		update(null);
	}

	public void update(final UpdaterResponse response)
	{
		if(result == UpdateResult.DISABLED) return;
		runAsync(new Runnable()
		{
			@Override
			public void run()
			{
				result = updateProvider.query(logger);
				if(result == UpdateResult.SUCCESS)
				{
					if(versionCheck(getRemoteVersion()))
					{
						result = UpdateResult.UPDATE_AVAILABLE;
						try
						{
							if(updateProvider.provideDownloadURL() && updateProvider.getLatestFileURL() != null)
							{
								download(updateProvider.getLatestFileURL(), (updateProvider.getLatestVersionFileName().toLowerCase().endsWith(".zip")) ? updateProvider.getLatestVersionFileName() : targetFileName);
								if(result == UpdateResult.SUCCESS && downloadDependencies && updateProvider.provideDependencies() && updateProvider.getLatestDependencies() != null)
								{
									for(UpdateProvider.UpdateFile update : updateProvider.getLatestDependencies())
									{
										download(update.getDownloadURL(), update.getFileName());
									}
									result = (result == UpdateResult.SUCCESS) ? UpdateResult.SUCCESS : UpdateResult.SUCCESS_DEPENDENCY_DOWNLOAD_FAILED;
								}
								if(response != null) runSync(new Runnable()
								{
									@Override
									public void run()
									{
										response.onDone(result);
									}
								});
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	public void checkForUpdate(final UpdaterResponse response)
	{
		if(result == UpdateResult.DISABLED) return;
		runAsync(new Runnable()
		{
			@Override
			public void run()
			{
				result = updateProvider.query(logger);
				if(result == UpdateResult.SUCCESS)
				{
					result = versionCheck(getRemoteVersion()) ? UpdateResult.UPDATE_AVAILABLE : UpdateResult.NO_UPDATE;
				}
				runSync(new Runnable()
				{
					@Override
					public void run()
					{
						response.onDone(result);
					}
				});
			}
		});
	}

	public interface UpdaterResponse
	{
		void onDone(UpdateResult result);
	}
}