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

package at.pcgamingfreaks.Message.Sender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ISenderGeneric<PLAYER>
{
	void doSend(@NotNull PLAYER player, @NotNull String json);

	void doSend(@NotNull PLAYER player, @NotNull String json, @Nullable Object optional);

	void doSend(@NotNull Collection<? extends PLAYER> players, @NotNull String json);

	void doSend(@NotNull Collection<? extends PLAYER> players, @NotNull String json, @Nullable Object optional);

	void doBroadcast(@NotNull String json);

	void doBroadcast(@NotNull String json, @Nullable Object optional);
}