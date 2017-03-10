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

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BukkitUpdateProvider extends AbstractOnlineProvider
{
	//region static stuff
	private static final String HOST = "https://api.curseforge.com/servermods/files?projectIds=";
	private static final Pattern VERSION_PATTERN = Pattern.compile(Version.VERSION_STING_FORMAT); // Used for locating version numbers in file names, bukkit doesn't provide the version on it's own :(
	//endregion

	private final int projectID;
	private final String apiKey;
	private final URL url;

	private UpdateFile lastResult = null;

	public BukkitUpdateProvider(int projectID, @NotNull Logger logger)
	{
		this(projectID, null, logger);
	}

	public BukkitUpdateProvider(int projectID, @Nullable String apiKey, @NotNull Logger logger)
	{
		super(logger);
		this.projectID = projectID;
		this.apiKey = apiKey;
		URL url = null;
		try
		{
			url = new URL(HOST + projectID);
		}
		catch(MalformedURLException ignored) {}
		this.url = url;
	}

	@Override
	public @NotNull UpdateResult query()
	{
		if(url == null) return UpdateResult.FAIL_FILE_NOT_FOUND;
		try
		{
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(TIMEOUT);
			if(apiKey != null) connection.addRequestProperty("X-API-Key", apiKey);
			connection.addRequestProperty(PROPERTY_USER_AGENT, USER_AGENT);
			connection.setDoOutput(true);

			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
			{
				DevBukkitVersion[] devBukkitVersions = GSON.fromJson(reader, DevBukkitVersion[].class);
				if(devBukkitVersions == null || devBukkitVersions.length == 0)
				{
					logger.warning(ConsoleColor.RED + "The updater could not find any files for the project id " + projectID + " " + ConsoleColor.RESET);
					return UpdateResult.FAIL_FILE_NOT_FOUND;
				}
				else
				{
					try
					{
						DevBukkitVersion devBukkitVersion = devBukkitVersions[devBukkitVersions.length - 1];
						String latestName = devBukkitVersion.name;
						Matcher matcher = VERSION_PATTERN.matcher(latestName);
						if(matcher.find())
						{
							this.lastResult = new UpdateFile(new URL(devBukkitVersion.downloadUrl), latestName, new Version(matcher.group() + "-" + devBukkitVersion.releaseType),
							                                 devBukkitVersion.fileName, devBukkitVersion.md5, "", devBukkitVersion.gameVersion);
						}
						else
						{
							return UpdateResult.FAIL_NO_VERSION_FOUND;
						}
					}
					catch(MalformedURLException e)
					{
						logger.warning(ConsoleColor.RED + "Failed to interpret download url \"" + devBukkitVersions[devBukkitVersions.length - 1].downloadUrl + "\"!" + ConsoleColor.RESET);
						e.printStackTrace();
						return UpdateResult.FAIL_FILE_NOT_FOUND;
					}
				}
			}
		}
		catch(final IOException e)
		{
			if(e.getMessage().contains("HTTP response code: 403"))
			{
				logger.severe(ConsoleColor.RED + "dev.bukkit.org rejected the provided API key!" + ConsoleColor.RESET);
				logger.severe(ConsoleColor.RED + "Please double-check your configuration to ensure it is correct." + ConsoleColor.RESET);
				logger.log(Level.SEVERE, null, e);
				return UpdateResult.FAIL_API_KEY;
			}
			else
			{
				logger.severe(ConsoleColor.RED + "The updater could not contact dev.bukkit.org to check for updates!" + ConsoleColor.RESET);
				logger.severe(ConsoleColor.RED + "If this is the first time you are seeing this message, the site may be experiencing temporary downtime." + ConsoleColor.RESET);
				logger.log(Level.SEVERE, null, e);
				return UpdateResult.FAIL_SERVER_OFFLINE;
			}
		}
		return UpdateResult.SUCCESS;
	}

	private static class DevBukkitVersion
	{
		@SuppressWarnings("unused")
		public String name, downloadUrl, fileName, fileUrl, releaseType, gameVersion, md5, projectId;
	}

	//region provider property's
	@Override
	public boolean provideDownloadURL()
	{
		return true;
	}

	@Override
	public boolean provideMinecraftVersion()
	{
		return true;
	}

	@Override
	public boolean provideChangelog()
	{
		return false;
	}

	@Override
	public boolean provideMD5Checksum()
	{
		return true;
	}

	@Override
	public boolean provideUpdateHistory()
	{
		return true;
	}

	@Override
	public boolean provideDependencies()
	{
		return false;
	}
	//endregion

	//region getter for the latest version
	@Override
	public @NotNull String getLatestVersionAsString() throws NotSuccessfullyQueriedException
	{
		return getLatestVersion().toString();
	}

	@Override
	public @NotNull Version getLatestVersion() throws NotSuccessfullyQueriedException
	{
		if(lastResult == null) throw new NotSuccessfullyQueriedException();
		return lastResult.getVersion();
	}

	public @NotNull String getLatestVersionFileName() throws NotSuccessfullyQueriedException
	{
		if(lastResult == null) throw new NotSuccessfullyQueriedException();
		return lastResult.getFileName();
	}

	@Override
	public @NotNull URL getLatestFileURL() throws NotSuccessfullyQueriedException
	{
		if(lastResult == null) throw new NotSuccessfullyQueriedException();
		return lastResult.getDownloadURL();
	}

	@Override
	public @NotNull String getLatestName() throws NotSuccessfullyQueriedException
	{
		if(lastResult == null) throw new NotSuccessfullyQueriedException();
		return lastResult.getName();
	}

	@Override
	public @NotNull String getLatestMinecraftVersion() throws NotSuccessfullyQueriedException
	{
		if(lastResult == null) throw new NotSuccessfullyQueriedException();
		return lastResult.getGameVersion();
	}

	@Override
	public @NotNull String getLatestChecksum() throws NotSuccessfullyQueriedException
	{
		if(lastResult == null) throw new NotSuccessfullyQueriedException();
		return lastResult.getChecksum();
	}

	@Override
	public @NotNull String getLatestChangelog() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The dev.bukkit.org API does not provide a changelog!");
	}

	@Override
	public @NotNull UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The dev.bukkit.org API does not provide a list of dependencies to download!");
	}
	//endregion
}