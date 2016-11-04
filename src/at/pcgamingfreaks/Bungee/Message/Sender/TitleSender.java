/*
 * Copyright (C) 2016 GeorgH93
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bungee.Message.Sender;

import at.pcgamingfreaks.Bungee.Message.Message;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Title;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class TitleSender extends BaseSender
{
	private static final TitleMetadata METADATA = new TitleMetadata(); // Default metadata object

	/**
	 * Sends a JSON message to a player shown as title.
	 *
	 * @param player The player that should receive the message
	 * @param json   The message in JSON format to be sent
	 */
	public static void send(@NotNull ProxiedPlayer player, @NotNull String json)
	{
		send(player, json, METADATA);
	}

	/**
	 * Sends a JSON message to a player shown as title.
	 *
	 * @param player  The player that should receive the message
	 * @param message The message to be sent
	 */
	public static void send(@NotNull ProxiedPlayer player, @NotNull Message message)
	{
		send(player, message.toString());
	}

	/**
	 * Sends a JSON message to a player shown as title.
	 *
	 * @param player   The player that should receive the message
	 * @param message  The message to be sent
	 * @param metadata The metadata object giving more details on how the message should be displayed
	 */
	public static void send(@NotNull ProxiedPlayer player, @NotNull Message message, @NotNull TitleMetadata metadata)
	{
		send(player, message.toString(), metadata);
	}

	/**
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players The players that should receive the message
	 * @param json    The message in JSON format to be sent
	 */
	public static void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json)
	{
		send(players, json, METADATA);
	}

	/**
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players The players that should receive the message
	 * @param message The message to be sent
	 */
	public static void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull Message message)
	{
		send(players, message.toString());
	}

	/**
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players  The players that should receive the message
	 * @param message  The message to be sent
	 * @param metadata The metadata object giving more details on how the message should be displayed
	 */
	public static void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull Message message, @NotNull TitleMetadata metadata)
	{
		send(players, message.toString(), metadata);
	}

	/**
	 * Sends a JSON message shown as title to all online players.
	 *
	 * @param json The message in JSON format to be sent
	 */
	public static void broadcast(@NotNull String json)
	{
		broadcast(json, METADATA);
	}

	/**
	 * Sends a JSON message shown as title to all online players.
	 *
	 * @param message  The message to be sent
	 */
	public static void broadcast(@NotNull Message message)
	{
		broadcast(message.toString());
	}

	/**
	 * Sends a JSON message shown as title to all online players.
	 *
	 * @param message  The message to be sent
	 * @param metadata The metadata object giving more details on how the message should be displayed
	 */
	public static void broadcast(@NotNull Message message, @NotNull TitleMetadata metadata)
	{
		broadcast(message.toString(), metadata);
	}

	@Override
	public void doSend(@NotNull ProxiedPlayer player, @NotNull String json)
	{
		send(player, json);
	}

	@Override
	public void doSend(@NotNull ProxiedPlayer player, @NotNull String json, @Nullable Object optional)
	{
		send(player, json, (optional instanceof TitleMetadata) ? (TitleMetadata) optional : METADATA);
	}

	@Override
	public void doSend(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json)
	{
		send(players, json);
	}

	@Override
	public void doSend(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json, @Nullable Object optional)
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
	 * @param player   The player that should receive the message
	 * @param json     The message in JSON format to be sent
	 * @param metadata The metadata object giving more details on how the message should be displayed
	 */
	public static void send(@NotNull ProxiedPlayer player, @NotNull String json, @NotNull TitleMetadata metadata)
	{
		Title titleTimes = new Title(), titleSend = new Title();
		titleTimes.setAction(Title.Action.TIMES);
		titleTimes.setFadeIn(metadata.getFadeIn());
		titleTimes.setStay(metadata.getStay());
		titleTimes.setFadeOut(metadata.getFadeOut());
		titleSend.setAction(metadata.getTitleType());
		titleSend.setText(json);
		player.unsafe().sendPacket(titleTimes);
		player.unsafe().sendPacket(titleSend);
	}

	/**
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players  The player that should receive the message
	 * @param json     The message in JSON format to be sent
	 * @param metadata The metadata object giving more details on how the message should be displayed
	 */
	public static void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json, @NotNull TitleMetadata metadata)
	{
		Title titleTimes = new Title(), titleSend = new Title();
		titleTimes.setAction(Title.Action.TIMES);
		titleTimes.setFadeIn(metadata.getFadeIn());
		titleTimes.setStay(metadata.getStay());
		titleTimes.setFadeOut(metadata.getFadeOut());
		titleSend.setAction(metadata.getTitleType());
		titleSend.setText(json);
		for(ProxiedPlayer player : players)
		{
			player.unsafe().sendPacket(titleTimes);
			player.unsafe().sendPacket(titleSend);
		}
	}

	/**
	 * Sends a JSON message shown as title to all online players.
	 *
	 * @param json     The message in JSON format to be sent
	 * @param metadata The metadata object giving more details on how the message should be displayed
	 */
	public static void broadcast(@NotNull String json, @NotNull TitleMetadata metadata)
	{
		send(ProxyServer.getInstance().getPlayers(), json, metadata);
	}
}