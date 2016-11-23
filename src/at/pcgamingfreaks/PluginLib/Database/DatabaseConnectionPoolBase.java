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
import com.zaxxer.hikari.HikariDataSource;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public abstract class DatabaseConnectionPoolBase implements DatabaseConnectionPool
{
	protected final Configuration config;
	protected final File dataFolder;
	private final HikariDataSource dataSource;

	public static DatabaseConnectionPoolBase startPool(Configuration configuration, Logger logger, File dataFolder)
	{
		final DatabaseConnectionPoolBase connectionPool;
		switch(configuration.getString("Database.Type", "off").toLowerCase())
		{
			case "mysql": connectionPool = new MySQLConnectionPool(configuration, dataFolder); break;
			case "sqlite": connectionPool = new SQLiteConnectionPool(configuration, dataFolder); break;
			default: return null;
		}
		try
		{
			// Test if we can get a connection an close it
			connectionPool.getConnection().close();
		}
		catch(Exception e)
		{
			logger.warning("Failed to start connection pool.");
			e.printStackTrace();
			connectionPool.close();
			return null;
		}
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

	public void close()
	{
		this.dataSource.close();
	}

	@Override
	public @NotNull Connection getConnection() throws SQLException
	{
		return this.dataSource.getConnection();
	}
}