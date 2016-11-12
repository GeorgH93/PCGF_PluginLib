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

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnectionPool
{
	/**
	 * Gets the type of the database behind the connection pool. Use this to adjust your code for the used database.
	 *
	 * @return The type of the database the pool is connected with.
	 */
	@NotNull String getDatabaseType();

	/**
	 * Gets a connection from the pool.
	 * If you take one, close it when you are done!
	 *
	 * @return The connection from the pool.
	 * @throws SQLException If there was a problem with the connection.
	 */
	@NotNull Connection getConnection() throws SQLException;
}