/*
 *   Copyright (C) 2016-2018 GeorgH93
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

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Updater.UpdateProviders.NotSuccessfullyQueriedException;
import at.pcgamingfreaks.Updater.UpdateProviders.RequestTypeNotAvailableException;
import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;
import at.pcgamingfreaks.Utils;
import at.pcgamingfreaks.Version;
import at.pcgamingfreaks.yaml.YAML;

import com.google.common.io.ByteStreams;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is a very raw implementation of a plugin updater only using java functions.
 * This way we can use the same code on Bukkit/Spigot and BungeeCord
 */
public abstract class Updater
{
	private static final int BUFFER_SIZE = 1024;

	private final File pluginsFolder, updateFolder;
	private final UpdateProvider updateProvider;
	private final boolean announceDownloadProgress, downloadDependencies;
	private final Logger logger;
	private final String targetFileName;
	private final Version localVersion;

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
		this.localVersion = new Version(localVersion);
		this.targetFileName = targetFileName;

		//region Check if updater is disabled globally
		// Check if there is a updater config and if updating is globally disabled.
		final File updaterConfigFile = new File(pluginsFolder, "Updater" + File.separator + "config.yml");
		if(updaterConfigFile.exists())
		{
			try
			{
				YAML gravityUpdaterGlobalConfig = new YAML(updaterConfigFile);
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
	 * Check to see if the program should continue by evaluating whether the plugin is already updated, or shouldn't be updated.
	 *
	 * @param remoteVersion The version to compare against
	 * @return true if the version was located and is newer then the local version
	 */
	protected boolean versionCheck(Version remoteVersion)
	{
		if(remoteVersion != null)
		{
			if(!this.localVersion.olderThan(remoteVersion))
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
		if(!updateFolder.exists() && !updateFolder.mkdirs())
		{
			logger.warning(ConsoleColor.RED + "Failed to create folder for updates!" + ConsoleColor.RESET);
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
				case HttpURLConnection.HTTP_SEE_OTHER:
					if(movedCount == 5) // Prevents endless loops
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
			MessageDigest hashGenerator = updateProvider.providesChecksum().getInstanceOrNull();
			try(InputStream inputStream = (hashGenerator != null) ? new DigestInputStream(new BufferedInputStream(connection.getInputStream()), hashGenerator) : new BufferedInputStream(url.openStream());
			    FileOutputStream outputStream = new FileOutputStream(downloadFile))
			{
				byte[] buffer = new byte[BUFFER_SIZE];
				if(announceDownloadProgress)
				{
					logger.info("Start downloading update: " + updateProvider.getLatestVersion().toString());
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
			connection.disconnect();
			if(hashGenerator != null)
			{
				String MD5Download = Utils.byteArrayToHex(hashGenerator.digest()).toLowerCase(), MD5Target = updateProvider.getLatestChecksum().toLowerCase();
				if(!MD5Download.equals(MD5Target))
				{
					logger.warning("The auto-updater was able to download the file, but the checksum did not match! Delete file.");
					logger.warning("Checksum expected: " + MD5Target + " Checksum download: " + MD5Download);
					result = UpdateResult.FAIL_DOWNLOAD;
					if(!downloadFile.delete())
					{
						logger.warning(ConsoleColor.RED + "Failed to delete file:" + ConsoleColor.WHITE + ' ' + downloadFile.getAbsolutePath() + ' ' + ConsoleColor.RESET);
					}
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
		catch(RequestTypeNotAvailableException e)
		{
			logger.warning(ConsoleColor.RED + "The update provider provide invalid data about it's capabilities!" + ConsoleColor.RESET);
			e.printStackTrace();
		}
		catch(NotSuccessfullyQueriedException e)
		{
			logger.warning(ConsoleColor.RED + "The update provider was not queried successfully!" + ConsoleColor.RESET);
			e.printStackTrace();
			result = UpdateResult.FAIL_NO_VERSION_FOUND;
		}
		catch(IOException e)
		{
			logger.warning("The auto-updater tried to download a new update, but was unsuccessful.");
			e.printStackTrace();
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
	protected Version getRemoteVersion()
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

	public void update(@Nullable final UpdaterResponse response)
	{
		if(result == UpdateResult.DISABLED) return;
		runAsync(new Runnable()
		{
			@Override
			public void run()
			{
				result = updateProvider.query();
				if(result == UpdateResult.SUCCESS)
				{
					if(versionCheck(getRemoteVersion()))
					{
						result = UpdateResult.UPDATE_AVAILABLE;
						try
						{
							if(updateProvider.providesDownloadURL())
							{
								download(updateProvider.getLatestFileURL(), (updateProvider.getLatestFileName().toLowerCase().endsWith(".zip")) ? updateProvider.getLatestFileName() : targetFileName);
								if(result == UpdateResult.SUCCESS && downloadDependencies && updateProvider.providesDependencies())
								{
									for(UpdateProvider.UpdateFile update : updateProvider.getLatestDependencies())
									{
										download(update.getDownloadURL(), update.getFileName());
									}
									result = (result == UpdateResult.SUCCESS) ? UpdateResult.SUCCESS : UpdateResult.SUCCESS_DEPENDENCY_DOWNLOAD_FAILED;
								}
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						result = UpdateResult.NO_UPDATE;
					}
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
				result = updateProvider.query();
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