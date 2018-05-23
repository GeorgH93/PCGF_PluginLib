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
import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Version;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

abstract class BaseOnlineProvider implements UpdateProvider
{
	protected static final Gson GSON = new Gson();
	private static final String PROPERTY_USER_AGENT = "User-Agent";
	private static final int TIMEOUT = 5000;
	protected final Logger logger;
	private final String userAgent;

	protected BaseOnlineProvider(@NotNull Logger logger)
	{
		this("Plugin-Updater", logger);
	}

	protected BaseOnlineProvider(@NotNull String userAgent, @NotNull Logger logger)
	{
		this.logger = logger;
		this.userAgent = userAgent;
	}

	@Override
	public @NotNull ReleaseType getLatestReleaseType() throws NotSuccessfullyQueriedException
	{
		Version version = getLatestVersion();
		if(!version.isPreRelease()) return ReleaseType.RELEASE;
		String sVersion = version.toString().toLowerCase();
		if(sVersion.contains("alpha")) return ReleaseType.ALPHA;
		if(sVersion.contains("beta"))  return ReleaseType.BETA;
		if(sVersion.contains("-rc"))  return ReleaseType.RC;
		if(sVersion.contains("snapshot")) return ReleaseType.SNAPSHOT;
		return ReleaseType.UNKNOWN;
	}

	protected void logErrorOffline(String host, String message)
	{
		logger.severe(ConsoleColor.RED + "The updater could not contact " + host + " to check for updates!" + ConsoleColor.RESET);
		logger.severe(ConsoleColor.RED + "If this is the first time you are seeing this message, the site may be experiencing temporary downtime." + ConsoleColor.RESET);
		logger.severe(ConsoleColor.RED + "Message: " + message + " " + ConsoleColor.RESET);
	}

	protected HttpURLConnection connect(URL url) throws IOException
	{
		HttpURLConnection connection = null;
		int status = 0, redirects = 0;
		URL targetUrl = url;
		while(status != HttpURLConnection.HTTP_OK) // To handle http redirection responses
		{
			if(++redirects >= 5) return null; // To prevent endless redirection loops
			connection = (HttpURLConnection) targetUrl.openConnection();
			connection.setConnectTimeout(TIMEOUT);
			connection.setInstanceFollowRedirects(true);
			connection.addRequestProperty(PROPERTY_USER_AGENT, userAgent);
			setConnectionParameter(connection);
			connection.setDoOutput(true);
			status = connection.getResponseCode();
			if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER)
			{
				targetUrl = new URL(connection.getHeaderField("Location"));
			}
			else if(status != HttpURLConnection.HTTP_OK)
			{
				break; // There was a problem but no redirection, this should be handled somewhere else
			}
		}
		return connection;
	}

	protected void setConnectionParameter(HttpURLConnection connection) {}
}