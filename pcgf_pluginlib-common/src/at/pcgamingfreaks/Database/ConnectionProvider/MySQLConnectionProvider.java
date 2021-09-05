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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Database.ConnectionProvider;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Database.DatabaseConnectionConfiguration;

import com.zaxxer.hikari.HikariConfig;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class MySQLConnectionProvider extends PooledConnectionProvider
{
	private final DatabaseConnectionConfiguration connectionConfiguration;

	public MySQLConnectionProvider(@NotNull Logger logger, @NotNull String pluginName, @NotNull DatabaseConnectionConfiguration connectionConfiguration)
	{
		super(logger, pluginName);
		this.connectionConfiguration = connectionConfiguration;
		init();
	}

	@Override
	protected @NotNull HikariConfig getPoolConfig()
	{
		//region force loading of jdbc driver
		// Some plugins, prevent the driver from auto-loading for some reason, this forces the driver to be loaded and registered.
		try
		{
			try
			{ // Check the new driver name first
				Class.forName("com.mysql.cj.jdbc.Driver");
			}
			catch(ClassNotFoundException ignored)
			{
				Class.forName("com.mysql.jdbc.Driver");
			}
		}
		catch(ClassNotFoundException e)
		{
			logger.severe(ConsoleColor.RED + " Failed to load MySQL JDBC driver!" + ConsoleColor.RESET);
		}
		//endregion
		HikariConfig poolConfig = new HikariConfig();
		poolConfig.setJdbcUrl("jdbc:mysql://" + connectionConfiguration.getSQLHost() + "/" + connectionConfiguration.getSQLDatabase() + connectionConfiguration.getSQLConnectionProperties());
		poolConfig.setUsername(connectionConfiguration.getSQLUser());
		poolConfig.setPassword(connectionConfiguration.getSQLPassword());
		poolConfig.setMinimumIdle(1);
		poolConfig.setMaximumPoolSize(connectionConfiguration.getSQLMaxConnections());
		if(connectionConfiguration.getSQLMaxLifetime() > 0) poolConfig.setMaxLifetime(connectionConfiguration.getSQLMaxLifetime());
		if(connectionConfiguration.getSQLIdleTimeout() > 0) poolConfig.setIdleTimeout(connectionConfiguration.getSQLIdleTimeout());
		return poolConfig;
	}

	@Override
	public String getDatabaseType()
	{
		return "mysql";
	}
}