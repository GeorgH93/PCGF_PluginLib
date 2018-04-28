/*
 *   Copyright (C) 2017, 2018 GeorgH93
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.md_5.bungee.api.ChatColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("RedundantThrows")
public class JenkinsUpdateProvider extends AbstractOnlineProvider
{
	private static final Pattern VERSION_PATTERN = Pattern.compile(".*-(?<version>" + Version.VERSION_STING_FORMAT + ")\\.(jar|zip)");
	private static final String API_FILTER = "tree=artifacts[*]{0,1},fingerprint[hash]{0,1},number,timestamp,url,fullDisplayName,changeSet[items[comment]]";

	private final String host, token;
	private final URL url;
	private UpdateFile lastResult = null;

	public JenkinsUpdateProvider(@NotNull String host, @NotNull String job, @NotNull Logger logger)
	{
		this(host, job, null, logger);
	}

	public JenkinsUpdateProvider(@NotNull String host, @NotNull String job, @Nullable String token, @NotNull  Logger logger)
	{
		super(logger);
		StringBuilder urlBuilder = new StringBuilder((host.contains("://") ? "" : "http://"));
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
		URL url = null;
		try
		{
			url = new URL(urlBuilder.toString());
		}
		catch(MalformedURLException e)
		{
			logger.warning(ConsoleColor.RED + "Failed to build jenkins api url!\nHost:" + ConsoleColor.WHITE + ' ' + host + "    " + ConsoleColor.RED + "Job:" + ConsoleColor.WHITE + ' ' + job + '\n' + ConsoleColor.RED + "Build URL:" + ConsoleColor.WHITE + ' ' + urlBuilder.toString() + ' ' + ConsoleColor.RESET);
		}
		this.url = url;
		this.host = host;
		this.token = token;
	}

	@Override
	public @NotNull UpdateResult query()
	{
		if(url == null) return UpdateResult.FAIL_FILE_NOT_FOUND;
		try
		{
			HttpURLConnection connection = null;
			int status = 0, failed = 0;
			URL targetUrl = url;
			while(status != HttpURLConnection.HTTP_OK) // To handel http redirection responses
			{
				if(++failed >= 5) return UpdateResult.FAIL_FILE_NOT_FOUND; // To prevent endless redirection loops
				connection = (HttpURLConnection) targetUrl.openConnection();
				connection.setConnectTimeout(TIMEOUT);
				connection.setInstanceFollowRedirects(true);
				connection.addRequestProperty(PROPERTY_USER_AGENT, USER_AGENT);
				connection.setDoOutput(true);
				status = connection.getResponseCode();
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER)
				{
					targetUrl = new URL(connection.getHeaderField("Location"));
				}
			}

			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
			{
				//TODO deserialize the response and add the reading for the version history
				try
				{
					UpdateFile result = new UpdateFile();

					JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
					JsonArray artifacts = object.getAsJsonArray("artifacts");
					JsonObject firstArtifacts = artifacts.get(0).getAsJsonObject();
					String relativePath = firstArtifacts.getAsJsonPrimitive("relativePath").getAsString();
					result.setFileName(firstArtifacts.getAsJsonPrimitive("fileName").getAsString());
					result.setName(object.getAsJsonPrimitive("fullDisplayName").getAsString());
					result.setDownloadURL(new URL(object.getAsJsonPrimitive("url").getAsString() + "artifact/" + relativePath));
					result.setChecksum(object.getAsJsonArray("fingerprint").get(0).getAsJsonObject().getAsJsonPrimitive("hash").getAsString());
					//region read the changelog
					JsonArray items = object.getAsJsonObject("changeSet").getAsJsonArray("items");
					StringBuilder stringBuilder = new StringBuilder();
					for(int i = 0; i < items.size(); i++)
					{
						if(i > 0) stringBuilder.append("\n");
						stringBuilder.append(items.get(i).getAsJsonObject().getAsJsonPrimitive("comment").getAsString());
					}
					result.setChangelog(stringBuilder.toString());
					//endregion
					Matcher matcher = VERSION_PATTERN.matcher(result.getFileName());
					if(matcher.matches())
					{
						StringBuilder versionBuilder = new StringBuilder(matcher.group("version"));
						versionBuilder.append("-T");
						Date buildTime = new Date(object.getAsJsonPrimitive("timestamp").getAsLong());
						//noinspection SpellCheckingInspection
						SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
						format.setTimeZone(TimeZone.getTimeZone("UTC"));
						versionBuilder.append(format.format(buildTime));
						versionBuilder.append("-b");
						versionBuilder.append(object.getAsJsonPrimitive("number").getAsString());
						result.setVersion(new Version(versionBuilder.toString()));
						lastResult = result;
					}
				}
				catch(Exception e)
				{
					logger.warning(ConsoleColor.RED + "Failed to parse the result from the server!" + ChatColor.RESET);
					e.printStackTrace();
					return UpdateResult.FAIL_NO_VERSION_FOUND;
				}
			}
		}
		catch(final IOException e)
		{
			if(e.getMessage().contains("HTTP response code: 403"))
			{
				if(token == null)
				{
					logger.severe(ConsoleColor.RED + "The jenkins server requires an token for the given job!" + ConsoleColor.RESET);
					logger.severe(ConsoleColor.RED + "Please please add a token to your configuration and try again." + ConsoleColor.RESET);
				}
				else
				{
					logger.severe(ConsoleColor.RED + "The jenkins server rejected the provided token!" + ConsoleColor.RESET);
					logger.severe(ConsoleColor.RED + "Please double-check your configuration to ensure it is correct." + ConsoleColor.RESET);
				}
				logger.log(Level.SEVERE, null, e);
				return UpdateResult.FAIL_API_KEY;
			}
			else
			{
				logger.severe(ConsoleColor.RED + "The updater could not contact " + host + " to check for updates!" + ConsoleColor.RESET);
				logger.severe(ConsoleColor.RED + "If this is the first time you are seeing this message, the site may be experiencing temporary downtime." + ConsoleColor.RESET);
				logger.log(Level.SEVERE, null, e);
				return UpdateResult.FAIL_SERVER_OFFLINE;
			}
		}
		return UpdateResult.SUCCESS;
	}

	@Override
	public @NotNull String getLatestVersionAsString() throws NotSuccessfullyQueriedException
	{
		return getLatestVersion().toString();
	}

	@Override
	public @NotNull Version getLatestVersion() throws NotSuccessfullyQueriedException
	{
		if(lastResult == null) { throw new NotSuccessfullyQueriedException(); }
		return lastResult.getVersion();
	}

	@Override
	public @NotNull URL getLatestFileURL() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		if(lastResult == null) { throw new NotSuccessfullyQueriedException(); }
		return lastResult.getDownloadURL();
	}

	@Override
	public @NotNull String getLatestVersionFileName() throws NotSuccessfullyQueriedException
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
		throw new RequestTypeNotAvailableException("This provider does not provide an update history.");
	}

	@Override
	public boolean provideDownloadURL()
	{
		return true;
	}

	@Override
	public boolean provideMinecraftVersion()
	{
		return false;
	}

	@Override
	public boolean provideChangelog()
	{
		return true;
	}

	@Override
	public boolean provideMD5Checksum()
	{
		return true;
	}

	@Override
	public boolean provideUpdateHistory()
	{
		return false; // TODO
	}

	@Override
	public boolean provideDependencies()
	{
		return false;
	}
}