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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Database.Cache;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ICacheablePlayer
{
	/**
	 * @return The uuid of the player.
	 */
	@NotNull UUID getUUID();

	/**
	 * @return True if the player is online. False if not.
	 */
	boolean isOnline();

	/**
	 * @return The time in milliseconds since 1970-01-01 00:00:00 when the player was last online.
	 */
	long getLastPlayed();

	/**
	 * @return True if the cached player is no longer needed and can be purged from the cache.
	 */
	boolean canBeUncached();
}