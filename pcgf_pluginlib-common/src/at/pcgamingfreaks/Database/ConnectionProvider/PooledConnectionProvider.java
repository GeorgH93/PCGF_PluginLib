/*
 *   Copyright (C) 2025 GeorgH93
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

package at.pcgamingfreaks.Database.ConnectionProvider;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Slf4jLoggerForcer;
import at.pcgamingfreaks.Slf4jToJavaLogger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class PooledConnectionProvider implements ConnectionProvider
{
	protected final Logger logger;
	private final String pluginName;
	private HikariDataSource dataSource; // SQL Connection Pool

	protected PooledConnectionProvider(@NotNull Logger logger, @NotNull String pluginName)
	{
		this.logger = logger;
		this.pluginName = pluginName;
	}

	public void init()
	{
		try (Slf4jLoggerForcer slf4jForcer = new Slf4jLoggerForcer(Slf4jToJavaLogger.class))
		{
			slf4jForcer.enable();
			Slf4jToJavaLogger.setTargetLogger(logger);
			try
			{
				HikariConfig poolConfig = getPoolConfig();
				poolConfig.setPoolName(pluginName + "-Connection-Pool");
				poolConfig.addDataSourceProperty("useUnicode", "true");
				poolConfig.addDataSourceProperty("characterEncoding", "utf-8");
				poolConfig.addDataSourceProperty("cachePrepStmts", "true");
				poolConfig.addDataSourceProperty("prepStmtCacheSize", "250");
				poolConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
				dataSource = new HikariDataSource(poolConfig);
			}
			catch(Exception e)
			{
				Throwable cause = e;
				while(cause.getCause() != null) { cause = cause.getCause(); }
				logger.severe(ConsoleColor.RED + "There was a problem creating the connection pool for the SQL server! Please check your configuration." + ConsoleColor.RESET + "\nError: " + cause.getMessage());
			}
			try
			{
				// Test if we can get a connection and close it
				getConnection().close();
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, "Failed to start connection pool.", e);
				close();
			}
		}
	}

	/**
	 * Gets a config for a Hikari connection pool.
	 *
	 * @return The config for the connection pool.
	 */
	protected abstract @NotNull HikariConfig getPoolConfig();

	@Override
	public Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}

	@Override
	public void close()
	{
		if (dataSource != null && !dataSource.isClosed())
		{
			dataSource.close();
			dataSource = null;
		}
	}

	@Override
	public boolean isAvailable()
	{
		return dataSource != null;
	}
}