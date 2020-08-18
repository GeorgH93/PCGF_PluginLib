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

package at.pcgamingfreaks.Database;

import at.pcgamingfreaks.Config.IConfig;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface DatabaseConnectionConfiguration extends IConfig
{
	default @NotNull String getSQLHost()
	{
		return getConfigE().getString("Database.SQL.Host", "localhost:3306");
	}

	default @NotNull String getSQLDatabase()
	{
		return getConfigE().getString("Database.SQL.Database", "minecraft");
	}

	default @NotNull List<String> getSQLConnectionPropertiesList()
	{
		return getConfigE().getStringList("Database.SQL.Properties", new ArrayList<>());
	}

	/**
	 * Gets the properties for the connection. Must start with a ?
	 *
	 * @return The Connection properties
	 */
	default @NotNull String getSQLConnectionProperties()
	{
		List<String> list = getSQLConnectionPropertiesList();
		StringBuilder str = new StringBuilder();
		char separator = '?';
		for(String s : list)
		{
			str.append(separator).append(s);
			separator = '&';
		}
		return str.toString();
	}

	default @NotNull String getSQLUser()
	{
		return getConfigE().getString("Database.SQL.User", "minecraft");
	}

	default @NotNull String getSQLPassword()
	{
		return getConfigE().getString("Database.SQL.Password", "minecraft");
	}

	default int getSQLMaxConnections()
	{
		return Math.max(1, getConfigE().getInt("Database.SQL.MaxConnections", 2));
	}

	default long getSQLMaxLifetime()
	{
		return getConfigE().getLong("Database.SQL.MaxLifetime", -1) * 1000;
	}

	default long getSQLIdleTimeout()
	{
		return getConfigE().getLong("Database.SQL.IdleTimeout", -1) * 1000;
	}
}