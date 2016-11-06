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
import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import com.google.gson.*;

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

public class BukkitUpdateProvider implements UpdateProvider
{
	//region static stuff
	private static final String USER_AGENT = "Plugin-Updater";
	private static final String HOST = "https://api.curseforge.com/servermods/files?projectIds=";
	private static final Pattern VERSION_PATTERN = Pattern.compile(Version.VERSION_STING_FORMAT); // Used for locating version numbers in file names, bukkit doesn't provide the version on it's own :(
	//endregion

	private final int projectID;
	private final String apiKey;
	private URL url = null;

	private DevBukkitVersion[] devBukkitVersions = null;

	public BukkitUpdateProvider(int projectID)
	{
		this(projectID, null);
	}

	public BukkitUpdateProvider(int projectID, String apiKey)
	{
		this.projectID = projectID;
		this.apiKey = apiKey;

		try
		{
			url = new URL(HOST + projectID);
		}
		catch(MalformedURLException ignored) {}
	}

	@Override
	public UpdateResult query(Logger logger)
	{
		if(url == null) return UpdateResult.FAIL_FILE_NOT_FOUND;
		try
		{
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(5000);
			if(apiKey != null) connection.addRequestProperty("X-API-Key", apiKey);
			connection.addRequestProperty("User-Agent", USER_AGENT);
			connection.setDoOutput(true);

			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
			{
				devBukkitVersions = new Gson().fromJson(reader, DevBukkitVersion[].class);
				if(devBukkitVersions == null || devBukkitVersions.length == 0)
				{
					logger.warning(ConsoleColor.RED + "The updater could not find any files for the project id " + projectID + " " + ConsoleColor.RESET);
					return UpdateResult.FAIL_FILE_NOT_FOUND;
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
				logger.severe(ConsoleColor.RED + "The updater could not contact dev.bukkit.org for updating!" + ConsoleColor.RESET);
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
	public boolean provideReleaseType()
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
	public String getLatestVersionAsString() throws NotSuccessfullyQueriedException
	{
		String latest = getLatestName();
		if(latest == null) return null;
		Matcher matcher = VERSION_PATTERN.matcher(latest);
		if(matcher.find())
		{
			return matcher.group();
		}
		return null;
	}

	@Override
	public Version getLatestVersion() throws NotSuccessfullyQueriedException
	{
		String latest = getLatestVersionAsString();
		if(latest == null) return null;
		return new Version(latest);
	}

	public String getLatestVersionFileName() throws NotSuccessfullyQueriedException
	{
		if(devBukkitVersions == null)
		{
			throw new NotSuccessfullyQueriedException();
		}
		return devBukkitVersions[devBukkitVersions.length - 1].fileName;
	}

	@Override
	public URL getLatestFileURL() throws NotSuccessfullyQueriedException
	{
		if(devBukkitVersions == null)
		{
			throw new NotSuccessfullyQueriedException();
		}
		try
		{
			return new URL(devBukkitVersions[devBukkitVersions.length - 1].downloadUrl);
		}
		catch(MalformedURLException e)
		{
			System.out.println(ConsoleColor.RED + "Failed to interpret download url \"" + devBukkitVersions[devBukkitVersions.length - 1].downloadUrl + "\"!" + ConsoleColor.RESET);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getLatestName() throws NotSuccessfullyQueriedException
	{
		if(devBukkitVersions == null)
		{
			throw new NotSuccessfullyQueriedException();
		}
		return devBukkitVersions[devBukkitVersions.length - 1].name;
	}

	@Override
	public String getLatestMinecraftVersion() throws NotSuccessfullyQueriedException
	{
		if(devBukkitVersions == null)
		{
			throw new NotSuccessfullyQueriedException();
		}
		return devBukkitVersions[devBukkitVersions.length - 1].gameVersion;
	}

	@Override
	public ReleaseType getLatestReleaseType() throws NotSuccessfullyQueriedException
	{
		if(devBukkitVersions == null)
		{
			throw new NotSuccessfullyQueriedException();
		}
		try
		{
			return ReleaseType.valueOf(devBukkitVersions[devBukkitVersions.length - 1].releaseType.toUpperCase());
		}
		catch(Exception ignored) {}
		return ReleaseType.UNKNOWN;
	}

	@Override
	public String getLatestChecksum() throws NotSuccessfullyQueriedException
	{
		if(devBukkitVersions == null)
		{
			throw new NotSuccessfullyQueriedException();
		}
		return devBukkitVersions[devBukkitVersions.length - 1].md5;
	}

	@Override
	public String getLatestChangelog() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The dev.bukkit.org API does not provide a changelog!");
	}

	@Override
	public UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The dev.bukkit.org API does not provide a list of dependencies to download!");
	}
	//endregion
}