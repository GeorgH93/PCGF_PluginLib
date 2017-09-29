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

package at.pcgamingfreaks.PluginLib.Database;

import at.pcgamingfreaks.Configuration;

import com.zaxxer.hikari.HikariConfig;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SQLiteConnectionPool extends DatabaseConnectionPoolBase
{
	public SQLiteConnectionPool(Configuration config, File dataFolder)
	{
		super(config, dataFolder);
	}

	@Override
	protected HikariConfig getPoolConfig()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		HikariConfig poolConfig = new HikariConfig();
		poolConfig.setMaximumPoolSize(1);
		poolConfig.setJdbcUrl("jdbc:sqlite:" + dataFolder.getAbsolutePath() + File.separator + "backpack.db");
		poolConfig.setConnectionTestQuery("SELECT 1;");
		return poolConfig;
	}

	@Override
	public @NotNull String getDatabaseType()
	{
		return "SQLite";
	}
}