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
import at.pcgamingfreaks.Util.StringUtils;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("RedundantThrows")
public class JenkinsUpdateProvider extends BaseOnlineProviderWithDownload
{
	private static final Pattern VERSION_PATTERN = Pattern.compile(".*-(?<VersionString>" + Version.VERSION_STING_FORMAT + ")\\.(jar|zip)");
	private static final String API_FILTER = "tree=artifacts[relativePath,fileName],fingerprint[hash],number,timestamp,url,fullDisplayName,changeSet[items[comment]]";
	private static final String[] IGNORE_ARTIFACTS = { "javadoc", "sources" };

	private final String host, token, artifactSearchRegex;
	private final URL url;

	public JenkinsUpdateProvider(@NotNull String host, @NotNull String job, @NotNull Logger logger)
	{
		this(host, job, null, logger);
	}

	public JenkinsUpdateProvider(@NotNull String host, @NotNull String job, @NotNull Logger logger, @Nullable @Language("RegExp") String artifactSearchRegex)
	{
		this(host, job, null, logger, artifactSearchRegex);
	}

	public JenkinsUpdateProvider(@NotNull String host, @NotNull String job, @Nullable String token, @NotNull Logger logger)
	{
		this(host, job, token, logger, null);
	}

	public JenkinsUpdateProvider(@NotNull String host, @NotNull String job, @Nullable String token, @NotNull Logger logger, @Nullable @Language("RegExp") String artifactSearchRegex)
	{
		super(logger);
		StringBuilder urlBuilder = new StringBuilder((host.contains("://") ? "" : "https://"));
		urlBuilder.append(host);
		if(!host.endsWith("/")) urlBuilder.append('/');
		urlBuilder.append("job/");
		urlBuilder.append(job.replace(" ", "%20"));
		urlBuilder.append("/lastSuccessfulBuild/api/json");
		urlBuilder.append('?');
		urlBuilder.append(API_FILTER);
		if(token != null)
		{
			urlBuilder.append("&token=");
			urlBuilder.append(token);
		}
		URL tmpUrl = null;
		try
		{
			tmpUrl = new URL(urlBuilder.toString());
		}
		catch(MalformedURLException e)
		{
			logger.severe(ConsoleColor.RED + "Failed to build jenkins api url!\nHost:" + ConsoleColor.WHITE + ' ' + host + "    " + ConsoleColor.RED + "Job:" + ConsoleColor.WHITE + ' ' + job + '\n' + ConsoleColor.RED + "Built URL:" + ConsoleColor.WHITE + ' ' + urlBuilder + ' ' + ConsoleColor.RESET);
		}
		this.url = tmpUrl;
		this.host = host;
		this.token = token;
		this.artifactSearchRegex = artifactSearchRegex;
	}

	@Override
	public @NotNull String getName()
	{
		return "Jenkins";
	}

	private boolean filteredFile(String filename)
	{
		if(artifactSearchRegex != null)
		{
			return !filename.matches(artifactSearchRegex);
		}
		return StringUtils.containsIgnoreCase(filename, IGNORE_ARTIFACTS);
	}

	String readChangelog(JsonArray changeSetItems)
	{
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < changeSetItems.size(); i++)
		{
			if(i > 0) stringBuilder.append("\n");
			stringBuilder.append(changeSetItems.get(i).getAsJsonObject().getAsJsonPrimitive("comment").getAsString());
		}
		return stringBuilder.toString();
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
					//TODO deserialize the response and add the reading for the version history
					UpdateFile result = new UpdateFile();
					JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
					JsonArray artifacts = object.getAsJsonArray("artifacts");
					int artifactId = -1;
					String filename = "", relativePath = "";
					for(int i = 0; i < artifacts.size(); i++)
					{
						JsonObject artifact = artifacts.get(i).getAsJsonObject();
						filename = artifact.getAsJsonPrimitive("fileName").getAsString();
						relativePath = artifact.getAsJsonPrimitive("relativePath").getAsString();
						if(filteredFile(filename)) continue;
						artifactId = i;
						break; // Found a valid artifact, no reason to check the others
					}
					if(artifactId < 0) return UpdateResult.FAIL_FILE_NOT_FOUND;
					result.setFileName(filename);
					result.setName(object.getAsJsonPrimitive("fullDisplayName").getAsString());
					result.setDownloadURL(new URL(object.getAsJsonPrimitive("url").getAsString() + "artifact/" + relativePath));
					result.setChecksum(object.getAsJsonArray("fingerprint").get(artifactId).getAsJsonObject().getAsJsonPrimitive("hash").getAsString());
					result.setChangelog(readChangelog(object.getAsJsonObject("changeSet").getAsJsonArray("items")));
					//endregion
					Matcher matcher = VERSION_PATTERN.matcher(result.getFileName());
					if(matcher.matches())
					{
						StringBuilder versionBuilder = new StringBuilder(matcher.group("VersionString"));
						versionBuilder.append("-T");
						Date buildTime = new Date(object.getAsJsonPrimitive("timestamp").getAsLong());
						SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
						format.setTimeZone(TimeZone.getTimeZone("UTC"));
						versionBuilder.append(format.format(buildTime));
						versionBuilder.append("-b");
						versionBuilder.append(object.getAsJsonPrimitive("number").getAsString());
						result.setVersion(new Version(versionBuilder.toString()));
						lastResult = result;
					}
				}
				catch(Exception ignored)
				{
					logger.warning(ConsoleColor.RED + "Failed to parse the result from the server!" + ConsoleColor.RESET);
					return UpdateResult.FAIL_NO_VERSION_FOUND;
				}
			}
			connection.disconnect();
		}
		catch(final IOException e)
		{
			if(e.getMessage().contains("HTTP response code: 403"))
			{
				if(token == null)
				{
					logger.severe(ConsoleColor.RED + "The jenkins server requires a token for the given job!" + ConsoleColor.RESET);
					logger.severe(ConsoleColor.RED + "Please add a token to your configuration and try again." + ConsoleColor.RESET);
				}
				else
				{
					logger.severe(ConsoleColor.RED + "The jenkins server rejected the provided token!" + ConsoleColor.RESET);
					logger.severe(ConsoleColor.RED + "Please double-check your configuration to ensure it is correct." + ConsoleColor.RESET);
				}
				return UpdateResult.FAIL_API_KEY;
			}
			else
			{
				logErrorOffline(host, e.getMessage());
				return UpdateResult.FAIL_SERVER_OFFLINE;
			}
		}
		return UpdateResult.SUCCESS;
	}

	//region getter for the latest version
	@Override
	public @NotNull String getLatestMinecraftVersion() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The jenkins API does not provide any information's about the minecraft version!");
	}

	@Override
	public @NotNull String getLatestChecksum() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		if(lastResult == null) { throw new NotSuccessfullyQueriedException(); }
		return lastResult.getChecksum();
	}

	@Override
	public @NotNull String getLatestChangelog() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		if(lastResult == null) { throw new NotSuccessfullyQueriedException(); }
		return lastResult.getChangelog();
	}

	@Override
	public @NotNull UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The jenkins API does not provide a list of dependencies to download!");
	}

	@Override
	public @NotNull UpdateFile[] getUpdateHistory() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		//TODO add it!
		throw new RequestTypeNotAvailableException("The jenkins API does not provide an update history.");
	}

	@Override
	public @NotNull String[] getLatestMinecraftVersions() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("The jenkins update provider does not provide a list of supported minecraft versions!");
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
		return true;
	}

	@Override
	public @NotNull ChecksumType providesChecksum()
	{
		return ChecksumType.MD5;
	}

	@Override
	public boolean providesUpdateHistory()
	{
		return false; // TODO
	}

	@Override
	public boolean providesDependencies()
	{
		return false;
	}
	//endregion
}