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

package at.pcgamingfreaks.Message.Sender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ISenderGeneric<PLAYER>
{
	void send(@NotNull PLAYER player, @NotNull String json);

	default void send(@NotNull PLAYER player, @NotNull String json, @Nullable IMetadata optionalMetadata)
	{
		send(player, json);
	}

	void send(@NotNull Collection<? extends PLAYER> players, @NotNull String json);

	default void send(@NotNull Collection<? extends PLAYER> players, @NotNull String json, @Nullable IMetadata optionalMetadata)
	{
		send(players, json);
	}

	void broadcast(@NotNull String json);

	default void broadcast(@NotNull String json, @Nullable IMetadata optionalMetadata)
	{
		broadcast(json);
	}
}