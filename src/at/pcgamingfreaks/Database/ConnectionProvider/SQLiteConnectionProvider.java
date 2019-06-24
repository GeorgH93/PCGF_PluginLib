/*
 *   Copyright (C) 2019 GeorgH93
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

package at.pcgamingfreaks.Database.ConnectionProvider;

import at.pcgamingfreaks.ConsoleColor;

import com.zaxxer.hikari.HikariConfig;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class SQLiteConnectionProvider extends PooledConnectionProvider
{
	private final String databasePath;

	public SQLiteConnectionProvider(@NotNull Logger logger, @NotNull String pluginName, @NotNull String databasePath)
	{
		super(logger, pluginName);
		this.databasePath = databasePath;
		init();
	}

	@Override
	protected @NotNull HikariConfig getPoolConfig()
	{
		try
		{
			Class.forName("org.sqlite.JDBC"); // JDBC is unable to load the SQLite JDBC driver on its own.
		}
		catch(ClassNotFoundException e)
		{
			logger.severe(ConsoleColor.RED + " Failed to load SQLite JDBC driver!" + ConsoleColor.RESET);
		}
		HikariConfig poolConfig = new HikariConfig();
		poolConfig.setMaximumPoolSize(1);
		poolConfig.setJdbcUrl("jdbc:sqlite:" + databasePath);
		poolConfig.setConnectionTestQuery("SELECT 1;"); // HikariCP doesn't support connection tests on it's own.
		return poolConfig;
	}

	@Override
	public String getDatabaseType()
	{
		return "sqlite";
	}
}