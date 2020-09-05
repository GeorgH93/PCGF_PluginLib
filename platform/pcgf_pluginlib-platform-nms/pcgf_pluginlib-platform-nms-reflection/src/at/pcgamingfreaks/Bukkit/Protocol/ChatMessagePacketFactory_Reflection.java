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

package at.pcgamingfreaks.Bukkit.Protocol;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Bukkit.Util.IUtils;
import at.pcgamingfreaks.Reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ChatMessagePacketFactory_Reflection implements IChatMessagePacketFactory
{
	private static final byte CHAT_TYPE = 0, SYSTEM_TYPE = 1, ACTION_BAR_TYPE = 2;

	private static final Class<?> I_CHAT_BASE_COMPONENT = NmsReflector.INSTANCE.getNmsClass("IChatBaseComponent");
	private static final Class<?> PACKET_PLAY_OUT_CHAT = NmsReflector.INSTANCE.getNmsClass("PacketPlayOutChat");
	private static final Constructor<?> PACKET_PLAY_OUT_CHAT_CONSTRUCTOR;
	private static final Method BYTE_TO_MESSAGE_TYPE_ENUM;
	private static final Object[] BYTE_TO_MESSAGE_MAP;

	static
	{
		if(MCVersion.isOlderThan(MCVersion.MC_1_12))
		{
			PACKET_PLAY_OUT_CHAT_CONSTRUCTOR = Reflection.getConstructor(PACKET_PLAY_OUT_CHAT, I_CHAT_BASE_COMPONENT, Byte.TYPE);
			BYTE_TO_MESSAGE_TYPE_ENUM = null;
			BYTE_TO_MESSAGE_MAP = null;
		}
		else
		{
			Class<?> chatMessageType = NmsReflector.INSTANCE.getNmsClass("ChatMessageType");
			BYTE_TO_MESSAGE_TYPE_ENUM = NmsReflector.INSTANCE.getNmsMethod(chatMessageType, "a", Byte.TYPE);
			if(MCVersion.isOlderThan(MCVersion.MC_1_16))
				PACKET_PLAY_OUT_CHAT_CONSTRUCTOR = Reflection.getConstructor(PACKET_PLAY_OUT_CHAT, I_CHAT_BASE_COMPONENT, chatMessageType);
			else
				PACKET_PLAY_OUT_CHAT_CONSTRUCTOR = Reflection.getConstructor(PACKET_PLAY_OUT_CHAT, I_CHAT_BASE_COMPONENT, chatMessageType, UUID.class);
			BYTE_TO_MESSAGE_MAP = new Object[3];
			try
			{
				BYTE_TO_MESSAGE_MAP[CHAT_TYPE] = BYTE_TO_MESSAGE_TYPE_ENUM.invoke(null, CHAT_TYPE);
				BYTE_TO_MESSAGE_MAP[SYSTEM_TYPE] = BYTE_TO_MESSAGE_TYPE_ENUM.invoke(null, SYSTEM_TYPE);
				BYTE_TO_MESSAGE_MAP[ACTION_BAR_TYPE] = BYTE_TO_MESSAGE_TYPE_ENUM.invoke(null, ACTION_BAR_TYPE);
			}
			catch(IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public Object makeChatPacket(@NotNull String json, @NotNull UUID sender)
	{
		return mkPacket(json, sender, CHAT_TYPE);
	}

	@Override
	public Object makeChatPacketSystem(@NotNull String json)
	{
		return mkPacket(json, EMPTY_UUID, SYSTEM_TYPE);
	}

	@Override
	public Object makeChatPacketActionBar(@NotNull String json)
	{
		return mkPacket(json, EMPTY_UUID, ACTION_BAR_TYPE);
	}

	private static Object mkPacket(@NotNull String json, @NotNull UUID sender, byte type)
	{
		try
		{
			Object chatComponent = IUtils.INSTANCE.jsonToIChatComponent(json);
			if(MCVersion.isOlderThan(MCVersion.MC_1_16))
				return PACKET_PLAY_OUT_CHAT_CONSTRUCTOR.newInstance(chatComponent, (MCVersion.isOlderThan(MCVersion.MC_1_12)) ? type : BYTE_TO_MESSAGE_MAP[type]);
			else
				return PACKET_PLAY_OUT_CHAT_CONSTRUCTOR.newInstance(chatComponent, BYTE_TO_MESSAGE_MAP[type], sender);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}