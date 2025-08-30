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

package at.pcgamingfreaks.PluginLib.Database;

import at.pcgamingfreaks.Database.ConnectionProvider.ConnectionProvider;
import at.pcgamingfreaks.Database.ConnectionProvider.MySQLConnectionProvider;
import at.pcgamingfreaks.Database.ConnectionProvider.SQLiteConnectionProvider;
import at.pcgamingfreaks.Database.DatabaseConnectionConfiguration;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public final class DatabaseConnectionPoolImpl implements DatabaseConnectionPool, ConnectionProvider
{
	private ConnectionProvider dataSource;

	public static DatabaseConnectionPoolImpl startPool(DatabaseConnectionConfiguration configuration, Logger logger, File dataFolder)
	{
		ConnectionProvider connectionPool;
		switch(configuration.getConfigE().getString("Database.Type", "off").toLowerCase(Locale.ROOT))
		{
			case "mysql": connectionPool = new MySQLConnectionProvider(logger, "PCGF_PluginLib", configuration); break;
			case "sqlite": {
				File legacyFile = new File(dataFolder, "backpack.db");
				dataFolder = new File(dataFolder, "data.db");
				if (legacyFile.exists()) legacyFile.renameTo(dataFolder);
				connectionPool = new SQLiteConnectionProvider(logger, "PCGF_PluginLib", dataFolder.getAbsolutePath());
			} break;
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
		return new DatabaseConnectionPoolImpl(connectionPool);
	}

	public void shutdown()
	{
		this.dataSource.close();
	}

	public void close() {} // This is empty on a purpose! Other plugins should not be able to close the connection pool if they no longer need it.

	@Override
	public boolean isAvailable()
	{
		return dataSource.isAvailable();
	}

	@Override
	public @NotNull String getDatabaseType()
	{
		return dataSource.getDatabaseType();
	}

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