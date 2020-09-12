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

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Chat;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

final class ChatSender implements ISender
{
	private static final byte CHAT_ACTION = 0;

	@Override
	public void send(final @NotNull ProxiedPlayer player, final @NotNull String json)
	{
		player.unsafe().sendPacket(new Chat(json, CHAT_ACTION));
	}

	@Override
	public void send(final @NotNull Collection<? extends ProxiedPlayer> players, final @NotNull String json)
	{
		Chat chatPacket = new Chat(json, CHAT_ACTION);
		for(ProxiedPlayer player : players)
		{
			player.unsafe().sendPacket(chatPacket);
		}
	}
}