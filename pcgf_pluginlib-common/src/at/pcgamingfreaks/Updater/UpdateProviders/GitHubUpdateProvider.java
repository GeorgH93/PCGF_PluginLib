/*
 *   Copyright (C) 2022 GeorgH93
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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.intellij.lang.annotations.Language;
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

/**
 * This update provider allows to check GitHub for new releases.
 */
@SuppressWarnings("RedundantThrows")
public class GitHubUpdateProvider extends BaseOnlineProviderWithDownload
{ //TODO allow to chose checksum type
	private static final Pattern IN_MD_5_SEARCH_PATTERN = Pattern.compile("(?<hash>[\\da-fA-F]{32})\\s+\\*?(?<file>.*)");
	private final URL url;
	private final @NotNull Pattern assetJarPattern;
	private final @Nullable Pattern assetMD5Pattern;
	private final String projectRepo;

	public GitHubUpdateProvider(@NotNull String githubProjectOwner, @NotNull String githubProjectRepo, @NotNull Logger logger)
	{
		this(githubProjectOwner, githubProjectRepo, githubProjectRepo, ".*\\.jar", ".*\\.md5", logger);
	}

	//TODO add api key
	public GitHubUpdateProvider(@NotNull String githubProjectOwner, @NotNull String githubProjectRepo, @NotNull String userAgent, @NotNull @Language("RegExp") String jarSearchRegex, @Nullable @Language("RegExp") String md5SearchRegex, @NotNull Logger logger)
	{
		super(userAgent, logger);
		StringBuilder apiUrlBuilder = new StringBuilder("https://api.github.com/repos/");
		apiUrlBuilder.append(githubProjectOwner);
		apiUrlBuilder.append('/');
		apiUrlBuilder.append(githubProjectRepo);
		apiUrlBuilder.append("/releases/latest");
		URL url = null;
		try
		{
			url = new URL(apiUrlBuilder.toString());
		}
		catch(MalformedURLException e)
		{
			logger.warning(ConsoleColor.RED + "Failed to build github api url!\nBuild URL:" + ConsoleColor.WHITE + ' ' + apiUrlBuilder.toString() + ' ' + ConsoleColor.RESET);
		}
		this.url = url;
		projectRepo = githubProjectRepo;
		assetJarPattern = Pattern.compile(jarSearchRegex, Pattern.CASE_INSENSITIVE);
		assetMD5Pattern = (md5SearchRegex != null) ? Pattern.compile(md5SearchRegex, Pattern.CASE_INSENSITIVE) : null;
	}

	@Override
	public @NotNull String getName()
	{
		return "GitHub";
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
				try
				{
					UpdateFile result = new UpdateFile();
					result.setName(projectRepo);
					JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
					JsonArray assets = object.getAsJsonArray("assets");
					boolean foundDl = false;
					for(int i = 0; i < assets.size(); i++)
					{
						JsonObject asset = assets.get(i).getAsJsonObject();
						String name = asset.get("name").getAsString();
						if(assetJarPattern.matcher(name).matches())
						{
							result.setFileName(name);
							result.setDownloadURL(new URL(asset.get("browser_download_url").getAsString()));
							foundDl = true;
						}
						else if(assetMD5Pattern != null && assetMD5Pattern.matcher(name).matches())
						{
							result.setChecksum(getMD5FromUrl(asset.get("browser_download_url").getAsString()));
						}
					}
					if(!foundDl) return UpdateResult.FAIL_FILE_NOT_FOUND;
					result.setVersion(new Version(object.get("tag_name").getAsString()));
					lastResult = result;
				}
				catch(Exception ignored)
				{
					logger.warning(ConsoleColor.RED + "Failed to parse the result from the server!" + ConsoleColor.RESET);
					return UpdateResult.FAIL_NO_VERSION_FOUND;
				}
			}
			connection.disconnect();
		}
		catch(IOException e)
		{
			if(e.getMessage().contains("HTTP response code: 403"))
			{
				logger.severe(ConsoleColor.RED + "api.github.com rejected the provided API key!" + ConsoleColor.RESET);
				logger.severe(ConsoleColor.RED + "Please double-check your configuration to ensure it is correct." + ConsoleColor.RESET);
				logger.log(Level.SEVERE, "", e);
				return UpdateResult.FAIL_API_KEY;
			}
			else
			{
				logErrorOffline("api.github.com", e.getMessage());
				return UpdateResult.FAIL_SERVER_OFFLINE;
			}
		}
		return UpdateResult.SUCCESS;
	}

	/**
	 * Downloads a md5 file and searches for the md5 of the update
	 *
	 * @param url The url to the md5 file.
	 * @return The md5 hash. Empty string if no hash was found.
	 */
	private @NotNull String getMD5FromUrl(@NotNull String url)
	{
		try
		{
			HttpURLConnection connection = connect(new URL(url));
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
			{
				String line;
				while((line = reader.readLine()) != null)
				{
					Matcher matcher = IN_MD_5_SEARCH_PATTERN.matcher(line);
					if(matcher.matches() && assetJarPattern.matcher(matcher.group("file")).matches())
					{
						return matcher.group("hash");
					}
				}
			}
		}
		catch(Exception ignored) {}
		return "";
	}

	//region getter for the latest version
	@Override
	public @NotNull String getLatestMinecraftVersion() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The github update provider does not provide any information's about the minecraft version!");
	}

	@Override
	public @NotNull String getLatestChecksum() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		if(assetMD5Pattern == null) throw new RequestTypeNotAvailableException("The github update provider does not provide a md5 checksum!");
		if(lastResult == null || lastResult.getChecksum().isEmpty()) throw new NotSuccessfullyQueriedException();
		return lastResult.getChecksum();
	}

	@Override
	public @NotNull String getLatestChangelog() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		return ""; //TODO
	}

	@Override
	public @NotNull UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The github update provider does not provide a list of dependencies to download!");
	}

	@Override
	public @NotNull UpdateFile[] getUpdateHistory() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The github update provider does not provide an update history."); //TODO
	}

	@Override
	public @NotNull String[] getLatestMinecraftVersions() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The github update provider does not provide a list of supported minecraft versions!");
	}

	@Override
	public boolean providesMinecraftVersions()
	{
		return false;
	}
	//endregion

	//region provider property's
	@Override
	public boolean providesMinecraftVersion()
	{
		return false;
	}

	@Override
	public boolean providesChangelog()
	{
		return false; //TODO
	}

	@Override
	public @NotNull ChecksumType providesChecksum()
	{
		return (assetMD5Pattern != null && (lastResult == null || (lastResult.getChecksum() != null && !lastResult.getChecksum().isEmpty()))) ? ChecksumType.MD5 : ChecksumType.NONE;
	}

	@Override
	public boolean providesUpdateHistory()
	{
		return false;
	}

	@Override
	public boolean providesDependencies()
	{
		return false;
	}
	//endregion
}