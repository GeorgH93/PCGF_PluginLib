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
		HikariConfig poolConfig = new HikariConfig();
		poolConfig.setJdbcUrl("jdbc:mysql://" + connectionConfiguration.getSQLHost() + "/" + connectionConfiguration.getSQLDatabase() + connectionConfiguration.getSQLConnectionProperties());
		poolConfig.setUsername(connectionConfiguration.getSQLUser());
		poolConfig.setPassword(connectionConfiguration.getSQLPassword());
		poolConfig.setMinimumIdle(1);
		poolConfig.setMaximumPoolSize(connectionConfiguration.getSQLMaxConnections());
		return poolConfig;
	}
}