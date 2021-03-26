/*
 *   Copyright (C) 2020 GeorgH93
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
import at.pcgamingfreaks.StringUtils;
import at.pcgamingfreaks.Updater.UpdateProviders.NotSuccessfullyQueriedException;
import at.pcgamingfreaks.Updater.UpdateProviders.RequestTypeNotAvailableException;
import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;
import at.pcgamingfreaks.Utils;
import at.pcgamingfreaks.Version;
import at.pcgamingfreaks.yaml.YAML;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is a very raw implementation of a plugin updater only using java functions.
 * This way we can use the same code on Bukkit/Spigot and BungeeCord.
 */
public abstract class Updater implements IUpdater
{
	private static final int BUFFER_SIZE = 1024;

	private final File pluginsFolder, updateFolder;
	protected final UpdateProvider[] updateProviders;
	protected UpdateProvider updateProvider;
	private final boolean announceDownloadProgress, downloadDependencies;
	protected final Logger logger;
	private final String targetFileName;
	private final Version localVersion;
	@Getter @Setter	private boolean checkMinecraftVersion = false;

	private UpdateResult result;

	protected Updater(final File pluginsFolder, final boolean announceProgress, final boolean downloadDependencies, final Logger logger, final UpdateProvider updateProvider, final String localVersion, final String targetFileName)
	{
		this(pluginsFolder, new File(pluginsFolder, "updates"), announceProgress, downloadDependencies, logger, updateProvider, localVersion, targetFileName);
	}

	protected Updater(final File pluginsFolder, final File updateFolder, final boolean announceProgress, final boolean downloadDependencies, final Logger logger, final UpdateProvider updateProvider, final String localVersion, final String targetFileName)
	{
		this(pluginsFolder, updateFolder, announceProgress, downloadDependencies, logger, new UpdateProvider[] {updateProvider}, localVersion, targetFileName);
	}

	protected Updater(final File pluginsFolder, final boolean announceProgress, final boolean downloadDependencies, final Logger logger, final UpdateProvider[] updateProviders, final String localVersion, final String targetFileName)
	{
		this(pluginsFolder, new File(pluginsFolder, "updates"), announceProgress, downloadDependencies, logger, updateProviders, localVersion, targetFileName);
	}

	protected Updater(final File pluginsFolder, final File updateFolder, final boolean announceProgress, final boolean downloadDependencies, final Logger logger, final UpdateProvider[] updateProviders, final String localVersion, final String targetFileName)
	{
		assert updateProviders.length > 0;
		this.pluginsFolder = pluginsFolder;
		this.updateFolder = updateFolder;
		this.updateProviders = updateProviders;
		this.updateProvider = updateProviders[0];
		this.announceDownloadProgress = announceProgress;
		this.downloadDependencies = downloadDependencies;
		this.logger = logger;
		this.localVersion = new Version(localVersion);
		this.targetFileName = targetFileName;

		//region Check if updater is disabled globally
		// Check if there is an updater config and if updating is globally disabled.
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
		if(updateProvider.providesMinecraftVersions() && isCheckMinecraftVersion()) // Update provider provides supported MC version. Let's check it.
		{
			return checkCompatibility();
		}
		return true;
	}
	//endregion

	protected void download(URL url, String fileName) // Saves file into servers update directory
	{
		if(!updateFolder.exists() && !updateFolder.mkdirs())
		{
			logger.warning(ConsoleColor.RED + "Failed to create folder for updates!" + ConsoleColor.RESET);
		}
		try
		{
			//region Connect to server
			HttpURLConnection connection = updateProvider.connect(url);
			if(connection == null) // connection failed with redirect loop
			{
				logger.warning("Target url redirected to often. Abort.");
				result = UpdateResult.FAIL_DOWNLOAD;
				return;
			}
			//endregion
			long fileLength = connection.getContentLengthLong();
			File downloadFile = new File(updateFolder.getAbsolutePath() + File.separator + fileName);
			MessageDigest hashGenerator = updateProvider.providesChecksum().getInstanceOrNull();
			try(InputStream inputStream = (hashGenerator != null) ? new DigestInputStream(new BufferedInputStream(connection.getInputStream()), hashGenerator) : new BufferedInputStream(url.openStream());
			    FileOutputStream outputStream = new FileOutputStream(downloadFile))
			{
				if(announceDownloadProgress)
				{
					logger.info("Start downloading update: " + updateProvider.getLatestVersion().toString());
				}
				int count, downloaded = 0, progress, lastProgress = 1;
				float percentPerByte = 100f / fileLength;
				byte[] buffer = new byte[BUFFER_SIZE];
				String size = (announceDownloadProgress && fileLength > 0) ? StringUtils.formatByteCountHumanReadable(fileLength) : "";
				while((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1)
				{
					downloaded += count;
					outputStream.write(buffer, 0, count);
					if(announceDownloadProgress && fileLength > 0) // ignore invalid file sizes
					{
						progress = (int) (downloaded * percentPerByte);
						if(progress % 10 == 0 && progress > lastProgress)
						{
							lastProgress = progress;
							logger.info("Downloading update: " + progress + "% of " + size);
						}
					}
				}
				outputStream.flush();
			}
			connection.disconnect();
			if(hashGenerator != null)
			{
				String MD5Download = Utils.byteArrayToHex(hashGenerator.digest()).toLowerCase(Locale.ROOT), MD5Target = updateProvider.getLatestChecksum().toLowerCase(Locale.ROOT);
				if(!MD5Download.equals(MD5Target))
				{
					logger.warning("The auto-updater was able to download the file, but the checksum did not match! Deleting file.");
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
			if(result != UpdateResult.FAIL_DOWNLOAD)
			{
				result = UpdateResult.SUCCESS;
				if(announceDownloadProgress) logger.info("Finished updating.");
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
			result = UpdateResult.FAIL_NO_VERSION_FOUND;
		}
		catch(IOException e)
		{
			logger.warning("The auto-updater tried to download a new update, but was unsuccessful.\n\t\tReason: " + e.toString());
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
				if(!entry.getName().toLowerCase(Locale.ROOT).endsWith(".jar"))
					continue;
				destinationFilePath = new File(updateFolder, entry.getName());
				try(BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry)); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destinationFilePath), BUFFER_SIZE))
				{
					Utils.streamCopy(bis, bos);
					bos.flush();
				}
			}
		}
		catch(IOException e)
		{
			logger.log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", e);
			result = UpdateResult.FAIL_DOWNLOAD;
		}
		if(!file.delete()) logger.info("Failed to delete " + file.getName());
	}

	protected boolean isPluginFile(String name)
	{
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
	protected @Nullable Version getRemoteVersion()
	{
		try
		{
			return updateProvider.getLatestVersion();
		}
		catch(NotSuccessfullyQueriedException ignored) {}
		return null;
	}

	@Override
	public void update(final @Nullable UpdateResponseCallback response)
	{
		prepUpdateOrCheck(response, () -> doUpdate(response, 0));
	}

	protected void prepUpdateOrCheck(final @Nullable UpdateResponseCallback response, final @NotNull Runnable runnable)
	{
		if(isRunning())
		{
			if(response != null) response.onDone(UpdateResult.FAIL_UPDATE_ALREADY_IN_PROGRESS);
			return;
		}
		if(result == UpdateResult.DISABLED) return;
		runAsync(runnable);
	}

	private void query()
	{
		if(result == UpdateResult.DISABLED) return;
		result = updateProvider.query();
		if(result == UpdateResult.SUCCESS)
		{
			result = versionCheck(getRemoteVersion()) ? UpdateResult.UPDATE_AVAILABLE : UpdateResult.NO_UPDATE;
		}
	}

	protected void doUpdate(final @Nullable UpdateResponseCallback responseCallback, final int updaterId)
	{
		updateProvider = updateProviders[updaterId];
		query();
		if(result == UpdateResult.UPDATE_AVAILABLE)
		{
			try
			{
				if(updateProvider.providesDownloadURL())
				{
					download(updateProvider.getLatestFileURL(), (updateProvider.getLatestFileName().toLowerCase(Locale.ROOT).endsWith(".zip")) ? updateProvider.getLatestFileName() : targetFileName);
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
		if((result.name().startsWith("FAIL") || result == UpdateResult.UPDATE_AVAILABLE || (result == UpdateResult.NO_UPDATE && updaterId > 0)) && updaterId + 1 < updateProviders.length)
		{
			doUpdate(responseCallback, updaterId + 1);
		}
		else
		{
			if(responseCallback != null) runSync(() -> responseCallback.onDone(result));
		}
	}

	public void doCheckForUpdate(final @Nullable UpdateResponseCallback responseCallback, final int updaterId)
	{
		updateProvider = updateProviders[updaterId];
		query();
		if(result.name().startsWith("FAIL") && updaterId + 1 < updateProviders.length)
		{
			doCheckForUpdate(responseCallback, updaterId + 1);
		}
		else
		{
			if(responseCallback != null) runSync(() -> responseCallback.onDone(result));
		}
	}

	@Override
	public void checkForUpdate(final @Nullable UpdateResponseCallback response)
	{
		prepUpdateOrCheck(response, () -> doCheckForUpdate(response, 0));
	}

	@Override
	public void update(final @NotNull UpdateMode updateMode, final @Nullable UpdateResponseCallback response)
	{
		if(updateMode == UpdateMode.UPDATE)
			update(response);
		else if(updateMode == UpdateMode.CHECK)
			checkForUpdate(response);
	}

	protected boolean checkCompatibility()
	{
		return true;
	}
}