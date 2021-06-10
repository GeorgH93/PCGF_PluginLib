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

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider
{
	/**
	 * Gets a connection from the connection provider.
	 * If you take one, close it when you are done!
	 *
	 * @return The connection provided by the provider.
	 * @throws SQLException If there was a problem with the connection.
	 */
	Connection getConnection() throws SQLException;

	String getDatabaseType();

	/**
	 * Closes the connection provider and frees resources used by it.
	 */
	void close();

	/**
	 * Checks if the connection provider was initialized correctly.
	 *
	 * @return true if the connection provider was initialized correctly. false if not.
	 */
	boolean isAvailable();
}