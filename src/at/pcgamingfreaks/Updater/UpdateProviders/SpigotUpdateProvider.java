/*
 *   Copyright (C) 2018 GeorgH93
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

import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * This update provider allows to query spigotmc.org for plugin updates.
 * Only plugins hosted on spigotmc.org can be downloaded automatically. Plugins hosted externally, that are only listed on spigotmc.org will only provide metadata.
 */
@SuppressWarnings("RedundantThrows")
public class SpigotUpdateProvider extends BaseOnlineProvider
{
	private final int projectID;
	private final String filename;
	private boolean downloadable = false;
	private UpdateFile lastResult = null;

	/**
	 * Creates an update provider for spigotmc.org.
	 * Spigot doesn't provide a filename. It will be assumed that the file is a .jar file
	 *
	 * @param projectId The id of the resource on spigotmc.org
	 * @param logger The logger used for outputs
	 */
	public SpigotUpdateProvider(int projectId, @NotNull Logger logger)
	{
		this(projectId, logger, projectId + ".jar");
	}

	/**
	 * Creates an update provider for spigotmc.org.
	 *
	 * @param projectId The id of the resource on spigotmc.org
	 * @param logger The logger used for outputs
	 * @param filename Spigot doesn't provide a filename. This parameter can be used to allow files like .zip to be downloaded correctly
	 */
	public SpigotUpdateProvider(int projectId, @NotNull Logger logger, @NotNull String filename)
	{
		super(logger);
		this.filename = filename;
		this.projectID = projectId;
	}

	@Override
	public @NotNull UpdateResult query()
	{
		try
		{
			UpdateFile result = new UpdateFile();
			HttpURLConnection connection = connect(new URL("http://api.spiget.org/v2/resources/" + projectID));
			if(connection == null) return UpdateResult.FAIL_FILE_NOT_FOUND;
			JsonParser parser = new JsonParser();
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
			{
				JsonObject resObject = parser.parse(reader).getAsJsonObject();
				downloadable = !resObject.get("external").getAsBoolean();
				if(downloadable)
				{
					result.setDownloadURL(new URL("http://api.spiget.org/v2/resources/" + projectID + "/download"));
				}
				else
				{
					// The file can not be downloaded automatically :(
					result.setDownloadURL(new URL("https://spigotmc.org/" + resObject.get("file").getAsJsonObject().get("url").getAsString()));
				}
				result.setName(resObject.get("name").getAsString());
				result.setFileName(filename);
				result.setGameVersion(resObject.get("testedVersions").getAsJsonArray().toString());
			}
			connection.disconnect();
			//region get version
			connection = connect(new URL("http://api.spiget.org/v2/resources/" + projectID + "/versions/latest"));
			if(connection == null) return UpdateResult.FAIL_FILE_NOT_FOUND;
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
			{
				result.setVersion(new Version(parser.parse(reader).getAsJsonObject().get("name").getAsString()));
			}
			connection.disconnect();
			lastResult = result;
			//endregion
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return UpdateResult.FAIL_FILE_NOT_FOUND;
		}
		return UpdateResult.SUCCESS;
	}

	//region getter for the latest version
	@Override
	public @NotNull String getLatestFileName() throws NotSuccessfullyQueriedException
	{
		if(lastResult == null) { throw new NotSuccessfullyQueriedException(); }
		return lastResult.getFileName();
	}

	@Override
	public @NotNull String getLatestName() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		if(lastResult == null) { throw new NotSuccessfullyQueriedException(); }
		return lastResult.getName();
	}

	@Override
	public @NotNull Version getLatestVersion() throws NotSuccessfullyQueriedException
	{
		if(lastResult == null) throw new NotSuccessfullyQueriedException();
		return lastResult.getVersion();
	}

	@Override
	public @NotNull URL getLatestFileURL() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		if(lastResult == null) throw new NotSuccessfullyQueriedException();
		if(!downloadable) throw new RequestTypeNotAvailableException("The spigot update provider only allows to download resources hosted on spigotmc.org!");
		return lastResult.getDownloadURL();
	}

	@Override
	public @NotNull String getLatestMinecraftVersion() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		if(lastResult == null) throw new NotSuccessfullyQueriedException();
		return lastResult.getGameVersion();
	}

	@Override
	public @NotNull String getLatestChecksum() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The spigot update provider does not provide a md5 checksum!");
	}

	@Override
	public @NotNull String getLatestChangelog() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The spigot update provider does not provide a changelog!");
	}

	@Override
	public @NotNull UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The spigot update provider does not provide a list of dependencies to download!");
	}

	@Override
	public @NotNull UpdateFile[] getUpdateHistory() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The spigot update provider does not provide an update history!");
	}
	//endregion

	//region provider property's
	@Override
	public final boolean providesDownloadURL()
	{
		return downloadable;
	}

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
		return ChecksumType.NONE;
	}

	@Override
	public boolean providesUpdateHistory()
	{
		return false; //TODO
	}

	@Override
	public boolean providesDependencies()
	{
		return false;
	}
	//endregion
}