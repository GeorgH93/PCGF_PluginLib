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

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Bukkit.Protocol.IChatMessagePacketFactory;
import at.pcgamingfreaks.Bukkit.Util.Utils;
import at.pcgamingfreaks.Message.Sender.IMetadata;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

final class ChatSender implements ISender
{
	private static final IChatMessagePacketFactory CHAT_MESSAGE_PACKET_FACTORY = IChatMessagePacketFactory.INSTANCE;

	@Override
	public void doSend(@NotNull Player player, @NotNull String json)
	{
		Utils.sendPacket(player, CHAT_MESSAGE_PACKET_FACTORY.makeChatPacket(json));
	}

	@Override
	public void doSend(@NotNull Player player, @NotNull String json, @Nullable IMetadata optionalMetadata)
	{
		doSend(player, json); //TODO implement sender uuid as metadata
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json)
	{
		Object packet = CHAT_MESSAGE_PACKET_FACTORY.makeChatPacket(json);
		for(Player player : players)
		{
			Utils.sendPacket(player, packet);
		}
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json, @Nullable IMetadata optionalMetadata)
	{
		doSend(players, json); //TODO implement sender uuid as metadata
	}
}