/*
 *   Copyright (C) 2022 GeorgH93
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

package at.pcgamingfreaks.Bungee.Message.Sender;

import at.pcgamingfreaks.Reflection;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.Chat;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

final class ChatSender implements ISender
{
	private static final Constructor<? extends DefinedPacket> SYSTEM_CHAT_CONSTRUCTOR;
	private static final byte CHAT_ACTION = 0;

	static
	{
		Class<?> systemChatClass = Reflection.getClassSilent("net.md_5.bungee.protocol.packet.SystemChat");
		if (systemChatClass != null)
		{
			SYSTEM_CHAT_CONSTRUCTOR = (Constructor<? extends DefinedPacket>) Reflection.getConstructor(systemChatClass, String.class, int.class);
		}
		else
		{
			SYSTEM_CHAT_CONSTRUCTOR = null;
		}
	}

	@Override
	public void send(final @NotNull ProxiedPlayer player, final @NotNull String json)
	{
		if (player.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_19)
		{
			player.unsafe().sendPacket(createSystemChatPacket(json));
		}
		else
		{
			player.unsafe().sendPacket(new Chat(json, CHAT_ACTION));
		}
	}

	@Override
	public void send(final @NotNull Collection<? extends ProxiedPlayer> players, final @NotNull String json)
	{
		DefinedPacket legacyChatPacket = new Chat(json, CHAT_ACTION);
		DefinedPacket newChatPacket = null;
		for(ProxiedPlayer player : players)
		{
			if (player.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_19)
			{
				if (newChatPacket == null)
				{
					newChatPacket = createSystemChatPacket(json);
				}
				player.unsafe().sendPacket(newChatPacket);
			}
			else
			{
				player.unsafe().sendPacket(legacyChatPacket);
			}
		}
	}

	private static DefinedPacket createSystemChatPacket(final @NotNull String json)
	{
		try
		{
			return SYSTEM_CHAT_CONSTRUCTOR.newInstance(json, 1);
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}