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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.PluginLib.Database;

import at.pcgamingfreaks.Config.Configuration;
import at.pcgamingfreaks.Database.ConnectionProvider.ConnectionProvider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DatabaseConnectionPoolBase implements DatabaseConnectionPool, ConnectionProvider
{
	protected final Configuration config;
	protected final File dataFolder;
	private final HikariDataSource dataSource;

	public static DatabaseConnectionPoolBase startPool(Configuration configuration, Logger logger, File dataFolder)
	{
		final String slf4jPropBackup = System.getProperty(org.slf4j.LoggerFactory.PROVIDER_PROPERTY_KEY);
		final String slf4jInternalVerbosityBackup = System.getProperty(org.slf4j.helpers.Reporter.SLF4J_INTERNAL_VERBOSITY_KEY);
		System.setProperty(org.slf4j.LoggerFactory.PROVIDER_PROPERTY_KEY, org.slf4j.jul.JULServiceProvider.class.getName());
		System.setProperty(org.slf4j.helpers.Reporter.SLF4J_INTERNAL_VERBOSITY_KEY, "ERROR");
		final DatabaseConnectionPoolBase connectionPool;
		switch(configuration.getString("Database.Type", "off").toLowerCase(Locale.ROOT))
		{
			case "mysql": connectionPool = new MySQLConnectionPool(configuration, dataFolder); break;
			case "sqlite": connectionPool = new SQLiteConnectionPool(configuration, dataFolder); break;
			default: return null;
		}
		try
		{
			// Test if we can get a connection and close it
			connectionPool.getConnection().close();
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, "Failed to start connection pool.", e);
			connectionPool.close();
			return null;
		}
		System.setProperty(org.slf4j.LoggerFactory.PROVIDER_PROPERTY_KEY, slf4jPropBackup);
		System.setProperty(org.slf4j.helpers.Reporter.SLF4J_INTERNAL_VERBOSITY_KEY, slf4jInternalVerbosityBackup);
		return connectionPool;
	}

	public DatabaseConnectionPoolBase(Configuration config, File dataFolder)
	{
		this.config = config;
		this.dataFolder = dataFolder;
		HikariConfig poolConfig = getPoolConfig();
		poolConfig.setPoolName("PCGF_PluginLib-Connection-Pool");
		poolConfig.addDataSourceProperty("useUnicode", "true");
		poolConfig.addDataSourceProperty("characterEncoding", "utf-8");
		poolConfig.addDataSourceProperty("cachePrepStmts", "true");
		poolConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		poolConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		this.dataSource = new HikariDataSource(poolConfig);
	}

	protected abstract HikariConfig getPoolConfig();

	public void shutdown()
	{
		this.dataSource.close();
	}

	public void close() {} // This is empty on a purpose! Other plugins should not be able to close the connection pool if they no longer need it.

	@Override
	public @NotNull Connection getConnection() throws SQLException
	{
		return this.dataSource.getConnection();
	}

	@Override
	public @NotNull ConnectionProvider getConnectionProvider()
	{
		return this;
	}
}