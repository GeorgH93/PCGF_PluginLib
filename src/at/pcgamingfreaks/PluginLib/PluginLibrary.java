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

package at.pcgamingfreaks.PluginLib;

import at.pcgamingfreaks.PluginLib.Database.DatabaseConnectionPool;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PluginLibrary
{
	/**
	 * Gets the version of the plugin lib.
	 *
	 * @return The version of the plugin lib.
	 */
	@NotNull Version getVersion();

	/**
	 * Gets the provided database connection pool.
	 *
	 * @return The database connection pool. Null if failed to start up or disabled.
	 */
	@Nullable DatabaseConnectionPool getDatabaseConnectionPool();
}