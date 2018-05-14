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

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("RedundantThrows")
public class BukkitUpdateProvider extends BaseOnlineProviderWithDownload
{
	//region static stuff
	private static final String HOST = "https://api.curseforge.com/servermods/files?projectIds=";
	private static final Pattern VERSION_PATTERN = Pattern.compile(Version.VERSION_STING_FORMAT); // Used for locating version numbers in file names, bukkit doesn't provide the version on it's own :(
	//endregion

	private final int projectID;
	private final String apiKey;
	private final URL url;

	private UpdateFile[] lastHistory = null;

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
		catch(MalformedURLException ignored) {} //Should never happen
		this.url = url;
	}

	@Override
	public @NotNull UpdateResult query()
	{
		if(url == null) return UpdateResult.FAIL_FILE_NOT_FOUND;
		try
		{
			HttpURLConnection connection = connect(url);
			if(connection == null) return UpdateResult.FAIL_FILE_NOT_FOUND;
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
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
						lastHistory = new UpdateFile[devBukkitVersions.length];
						for(int i = 0; i < devBukkitVersions.length; i++)
						{
							DevBukkitVersion devBukkitVersion = devBukkitVersions[devBukkitVersions.length - 1];
							String latestName = devBukkitVersion.name;
							URL url = new URL(devBukkitVersion.downloadUrl);
							Matcher matcher = VERSION_PATTERN.matcher(latestName);
							if(matcher.find())
							{
								this.lastResult = new UpdateFile(url, latestName, new Version(matcher.group() + "-" + devBukkitVersion.releaseType),
								                                 devBukkitVersion.fileName, devBukkitVersion.md5, "", devBukkitVersion.gameVersion);
								this.lastHistory[i] = lastResult;
							}
							else if(i == devBukkitVersions.length - 1)
							{
								return UpdateResult.FAIL_NO_VERSION_FOUND;
							}
							else
							{
								this.lastHistory[i] = new UpdateFile(url, latestName, new Version("0.0"), devBukkitVersion.fileName, devBukkitVersion.md5, "", devBukkitVersion.gameVersion);
							}
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
			connection.disconnect();
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
				logErrorOffline("dev.bukkit.org", e.getMessage());
				return UpdateResult.FAIL_SERVER_OFFLINE;
			}
		}
		return UpdateResult.SUCCESS;
	}

	@Override
	protected void setConnectionParameter(HttpURLConnection connection)
	{
		if(apiKey != null) connection.addRequestProperty("X-API-Key", apiKey);
	}

	//region provider property's
	@Override
	public boolean providesMinecraftVersion()
	{
		return true;
	}

	@Override
	public boolean providesChangelog()
	{
		return false;
	}

	@Override
	public @NotNull ChecksumType providesChecksum()
	{
		return ChecksumType.MD5;
	}

	@Override
	public boolean providesUpdateHistory()
	{
		return true;
	}

	@Override
	public boolean providesDependencies()
	{
		return false;
	}
	//endregion

	//region getter for the latest version
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

	@Override
	public @NotNull UpdateFile[] getUpdateHistory() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		if(lastHistory == null) throw new NotSuccessfullyQueriedException();
		return lastHistory;
	}
	//endregion

	private static class DevBukkitVersion
	{
		@SuppressWarnings("unused")
		public String name, downloadUrl, fileName, fileUrl, releaseType, gameVersion, md5, projectId;
	}
}