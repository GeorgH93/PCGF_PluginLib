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

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class ChatSender extends BaseSender
{
	//region Reflection stuff
	private static final Class<?> PACKET_PLAY_OUT_CHAT = NMSReflection.getNMSClass("PacketPlayOutChat");
	private static final Constructor<?> PACKET_PLAY_OUT_CHAT_CONSTRUCTOR;
	private static final Method BYTE_TO_MESSAGE_TYPE_ENUM;

	static
	{
		if(MCVersion.isOlderThan(MCVersion.MC_1_12))
		{
			PACKET_PLAY_OUT_CHAT_CONSTRUCTOR = NMSReflection.getConstructor(PACKET_PLAY_OUT_CHAT, I_CHAT_BASE_COMPONENT, Byte.TYPE);
			BYTE_TO_MESSAGE_TYPE_ENUM = null;
		}
		else
		{
			Class<?> chatMessageType = NMSReflection.getNMSClass("ChatMessageType");
			PACKET_PLAY_OUT_CHAT_CONSTRUCTOR = NMSReflection.getConstructor(PACKET_PLAY_OUT_CHAT, I_CHAT_BASE_COMPONENT, chatMessageType);
			BYTE_TO_MESSAGE_TYPE_ENUM = NMSReflection.getMethod(chatMessageType, "a", Byte.TYPE);
		}
	}
	//endregion

	private static final byte CHAT_ACTION = 0;

	/**
	 * Sends a JSON message to a players chat.
	 *
	 * @param player The player that should receive the message.
	 * @param json   The message in JSON format to be sent.
	 */
	public static void send(@NotNull Player player, @NotNull String json)
	{
		send(player, json, CHAT_ACTION);
	}

	/**
	 * Sends a JSON message to a players chat.
	 *
	 * @param player  The player that should receive the message.
	 * @param message The message to be sent.
	 */
	public static void send(@NotNull Player player, @NotNull Message message)
	{
		send(player, message.toString());
	}

	/**
	 * Sends a JSON message to a players chat.
	 *
	 * @param players The players that should receive the message.
	 * @param json    The message in JSON format to be sent.
	 */
	public static void send(@NotNull Collection<? extends Player> players, @NotNull String json)
	{
		send(players, json, CHAT_ACTION);
	}

	/**
	 * Sends a JSON message to a players chat.
	 *
	 * @param players The players that should receive the message.
	 * @param message The message to be sent.
	 */
	public static void send(@NotNull Collection<? extends Player> players, @NotNull Message message)
	{
		send(players, message.toString());
	}

	/**
	 * Sends a JSON message to the chat of all online players.
	 *
	 * @param json The message in JSON format to be sent.
	 */
	public static void broadcast(@NotNull String json)
	{
		broadcast(json, CHAT_ACTION);
	}

	/**
	 * Sends a JSON message to the chat of all online players.
	 *
	 * @param message The message to be sent.
	 */
	public static void broadcast(@NotNull Message message)
	{
		broadcast(message.toString());
	}

	@Override
	public void doBroadcast(@NotNull String json)
	{
		broadcast(json);
	}

	@Override
	public void doBroadcast(@NotNull String json, Object optional)
	{
		broadcast(json);
	}

	@Override
	public void doSend(@NotNull Player player, @NotNull String json)
	{
		send(player, json);
	}

	@Override
	public void doSend(@NotNull Player player, @NotNull String json, @Nullable Object optional)
	{
		send(player, json);
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json)
	{
		send(players, json);
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json, @Nullable Object optional)
	{
		send(players, json);
	}

	private static Object createPacket(@NotNull String json, byte action) throws InvocationTargetException, IllegalAccessException, InstantiationException
	{
		return PACKET_PLAY_OUT_CHAT_CONSTRUCTOR.newInstance(finalizeJson(json), (MCVersion.isOlderThan(MCVersion.MC_1_12)) ? action : BYTE_TO_MESSAGE_TYPE_ENUM.invoke(null, action));
	}

	protected static void send(@NotNull Player player, @NotNull String json, byte action)
	{
		if(CHAT_SERIALIZER_METHOD_A == null || PACKET_PLAY_OUT_CHAT_CONSTRUCTOR == null) return; // The class isn't initialized correctly! May it's not running on a bukkit/spigot server.
		try
		{
			Utils.sendPacket(player, createPacket(json, action));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected static void send(@NotNull Collection<? extends Player> players, @NotNull String json, byte action)
	{
		if(CHAT_SERIALIZER_METHOD_A == null || PACKET_PLAY_OUT_CHAT_CONSTRUCTOR == null) return; // The class isn't initialized correctly! May it's not running on a bukkit/spigot server.
		try
		{
			Object packet = createPacket(json, action);
			for(Player player : players)
			{
				Utils.sendPacket(player, packet);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected static void broadcast(@NotNull String json, byte action)
	{
		send(Bukkit.getOnlinePlayers(), json, action);
	}
}