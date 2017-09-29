/*
 *   Copyright (C) 2016, 2017 GeorgH93
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

package at.pcgamingfreaks.PluginLib.Database;

import at.pcgamingfreaks.Configuration;

import com.zaxxer.hikari.HikariConfig;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MySQLConnectionPool extends DatabaseConnectionPoolBase
{
	public MySQLConnectionPool(Configuration config, File dataFolder)
	{
		super(config, dataFolder);
	}

	@Override
	protected HikariConfig getPoolConfig()
	{
		HikariConfig poolConfig = new HikariConfig();
		poolConfig.setJdbcUrl("jdbc:mysql://" + config.getString("Database.SQL.Host", "localhost:3306") + "/" + config.getString("Database.SQL.Database", "minecraft") + "?allowMultiQueries=true&autoReconnect=true" + getSQLConnectionProperties());
		poolConfig.setUsername(config.getString("Database.SQL.User", "minecraft"));
		poolConfig.setPassword(config.getString("Database.SQL.Password", "minecraft"));
		poolConfig.setMinimumIdle(1);
		poolConfig.setMaximumPoolSize(config.getInt("Database.SQL.MaxConnections", 4));
		return poolConfig;
	}

	private String getSQLConnectionProperties()
	{
		List<String> list = config.getConfig().getStringList("Database.SQL.Properties", new LinkedList<String>());
		StringBuilder str = new StringBuilder();
		if(list != null)
		{
			for(String s : list)
			{
				str.append("&").append(s);
			}
		}
		return str.toString();
	}

	@Override
	public @NotNull String getDatabaseType()
	{
		return "MySQL";
	}
}