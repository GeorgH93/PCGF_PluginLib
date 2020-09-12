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

package at.pcgamingfreaks.Bungee.Message.Sender;

import at.pcgamingfreaks.Message.Sender.IMetadata;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class DisabledSender implements ISender
{
	@Override
	public void send(@NotNull ProxiedPlayer player, @NotNull String json)	{}

	@Override
	public void send(@NotNull ProxiedPlayer player, @NotNull String json, @Nullable IMetadata optional) {}

	@Override
	public void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json) {}

	@Override
	public void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json, @Nullable IMetadata optional) {}

	@Override
	public void broadcast(@NotNull String json) {}

	@Override
	public void broadcast(@NotNull String json, @Nullable IMetadata optional) {}
}
