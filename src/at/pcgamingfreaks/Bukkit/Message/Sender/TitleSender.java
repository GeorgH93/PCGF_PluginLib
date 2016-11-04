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

import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class TitleSender extends BaseSender
{
	private static final TitleMetadata METADATA = new TitleMetadata(); // Default metadata object

	//region Reflection stuff
	private static final Class<?> PACKET_PLAY_OUT_TITLE = NMSReflection.getNMSClass("PacketPlayOutTitle");
	private static final Constructor<?> PACKET_PLAY_OUT_TITLE_CONSTRUCTOR = NMSReflection.getConstructor(PACKET_PLAY_OUT_TITLE, NMSReflection.getNMSClass("PacketPlayOutTitle$EnumTitleAction"), I_CHAT_BASE_COMPONENT, int.class, int.class, int.class);
	//endregion

	/**
	 * Sends a JSON message to a player shown as title.
	 *
	 * @param player The player that should receive the message.
	 * @param json   The message in JSON format to be sent.
	 */
	public static void send(@NotNull Player player, @NotNull String json)
	{
		send(player, json, METADATA);
	}

	/**
	 * Sends a JSON message to a player shown as title.
	 *
	 * @param player  The player that should receive the message.
	 * @param message The message to be sent.
	 */
	public static void send(@NotNull Player player, @NotNull Message message)
	{
		send(player, message.toString());
	}

	/**
	 * Sends a JSON message to a player shown as title.
	 *
	 * @param player   The player that should receive the message.
	 * @param message  The message to be sent.
	 * @param metadata The metadata object giving more details on how the message should be displayed.
	 */
	public static void send(@NotNull Player player, @NotNull Message message, @NotNull TitleMetadata metadata)
	{
		send(player, message.toString(), metadata);
	}

	/**
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players The players that should receive the message.
	 * @param json    The message in JSON format to be sent.
	 */
	public static void send(@NotNull Collection<? extends Player> players, @NotNull String json)
	{
		send(players, json, METADATA);
	}

	/**
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players The players that should receive the message.
	 * @param message The message to be sent.
	 */
	public static void send(@NotNull Collection<? extends Player> players, @NotNull Message message)
	{
		send(players, message.toString());
	}

	/**
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players  The players that should receive the message.
	 * @param message  The message to be sent.
	 * @param metadata The metadata object giving more details on how the message should be displayed.
	 */
	public static void send(@NotNull Collection<? extends Player> players, @NotNull Message message, @NotNull TitleMetadata metadata)
	{
		send(players, message.toString(), metadata);
	}

	/**
	 * Sends a JSON message shown as title to all online players.
	 *
	 * @param json The message in JSON format to be sent.
	 */
	public static void broadcast(@NotNull String json)
	{
		broadcast(json, METADATA);
	}

	/**
	 * Sends a JSON message shown as title to all online players.
	 *
	 * @param message  The message to be sent.
	 */
	public static void broadcast(@NotNull Message message)
	{
		broadcast(message.toString());
	}

	/**
	 * Sends a JSON message shown as title to all online players.
	 *
	 * @param message  The message to be sent.
	 * @param metadata The metadata object giving more details on how the message should be displayed.
	 */
	public static void broadcast(@NotNull Message message, @NotNull TitleMetadata metadata)
	{
		broadcast(message.toString(), metadata);
	}

	@Override
	public void doSend(@NotNull Player player, @NotNull String json)
	{
		send(player, json);
	}

	@Override
	public void doSend(@NotNull Player player, @NotNull String json, @Nullable Object optional)
	{
		send(player, json, (optional instanceof TitleMetadata) ? (TitleMetadata) optional : METADATA);
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json)
	{
		send(players, json);
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json, @Nullable Object optional)
	{
		send(players, json, (optional instanceof TitleMetadata) ? (TitleMetadata) optional : METADATA);
	}

	@Override
	public void doBroadcast(@NotNull String json)
	{
		broadcast(json, METADATA);
	}

	@Override
	public void doBroadcast(@NotNull String json, @Nullable Object optional)
	{
		broadcast(json, (optional instanceof TitleMetadata) ? (TitleMetadata) optional : METADATA);
	}

	/**
	 * Sends a JSON message to a player shown as title.
	 *
	 * @param player   The player that should receive the message.
	 * @param json     The message in JSON format to be sent.
	 * @param metadata The metadata object giving more details on how the message should be displayed.
	 */
	public static void send(@NotNull Player player, @NotNull String json, @NotNull TitleMetadata metadata)
	{
		if (CHAT_SERIALIZER_METHOD_A == null || PACKET_PLAY_OUT_TITLE_CONSTRUCTOR == null) return;
		try
		{
			Utils.sendPacket(player, PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(metadata.getTitleType(), finalizeJson(json), metadata.getFadeIn(), metadata.getStay(), metadata.getFadeOut()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players  The player that should receive the message.
	 * @param json     The message in JSON format to be sent.
	 * @param metadata The metadata object giving more details on how the message should be displayed.
	 */
	public static void send(@NotNull Collection<? extends Player> players, @NotNull String json, @NotNull TitleMetadata metadata)
	{
		if (CHAT_SERIALIZER_METHOD_A == null ||PACKET_PLAY_OUT_TITLE_CONSTRUCTOR == null) return;
		try
		{
			Object titlePacket = PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(metadata.getTitleType(), finalizeJson(json), metadata.getFadeIn(), metadata.getStay(), metadata.getFadeOut());
			for(Player player : players)
			{
				Utils.sendPacket(player, titlePacket);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sends a JSON message shown as title to all online players.
	 *
	 * @param json     The message in JSON format to be sent.
	 * @param metadata The metadata object giving more details on how the message should be displayed.
	 */
	public static void broadcast(@NotNull String json, @NotNull TitleMetadata metadata)
	{
		send(Bukkit.getOnlinePlayers(), json, metadata);
	}
}