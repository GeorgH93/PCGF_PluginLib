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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface IPlayerCache
{
	/**
	 * @param uuid The UUID of the player for which the cached player should be obtained.
	 * @return The {@link ICacheablePlayer} representing the player. Null if there is no cached {@link ICacheablePlayer} object for the given player.
	 */
	@Nullable ICacheablePlayer getCachedPlayer(@NotNull UUID uuid);

	/**
	 * @param player The cached player object that should be unloaded from the cache.
	 */
	void unCache(@NotNull ICacheablePlayer player);

	/**
	 * @return A collection of players currently in the cache.
	 */
	@NotNull Collection<? extends ICacheablePlayer> getCachedPlayers();
}