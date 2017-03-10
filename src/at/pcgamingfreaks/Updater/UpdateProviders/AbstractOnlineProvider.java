/*
 *   Copyright (C) 2017 GeorgH93
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

import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Version;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

abstract class AbstractOnlineProvider implements UpdateProvider
{
	protected static final Gson GSON = new Gson();
	protected static final String USER_AGENT = "Plugin-Updater", PROPERTY_USER_AGENT = "User-Agent";
	protected static final int TIMEOUT = 5000;
	protected final Logger logger;

	protected AbstractOnlineProvider(Logger logger)
	{
		this.logger = logger;
	}

	@Override
	public @NotNull ReleaseType getLatestReleaseType() throws NotSuccessfullyQueriedException
	{
		Version version = getLatestVersion();
		if(!version.isPreRelease()) return ReleaseType.RELEASE;
		String sVersion = version.toString().toLowerCase();
		if(sVersion.contains("alpha")) return ReleaseType.ALPHA;
		if(sVersion.contains("beta"))  return ReleaseType.BETA;
		if(sVersion.contains("alpha")) return ReleaseType.SNAPSHOT;
		return ReleaseType.UNKNOWN;
	}
}