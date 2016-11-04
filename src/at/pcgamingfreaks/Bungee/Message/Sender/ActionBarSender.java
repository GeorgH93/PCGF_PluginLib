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

import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ActionBarSender extends ChatSender
{
	private static final byte ACTION_BAR_ACTION = 2;

	/**
	 * Sends a JSON message to a players action bar.
	 *
	 * @param player The player that should receive the message.
	 * @param json   The message in JSON format to be sent.
	 */
	public static void send(@NotNull ProxiedPlayer player, @NotNull String json)
	{
		send(player, json, ACTION_BAR_ACTION);
	}

	/**
	 * Sends a JSON message to a players action bar.
	 *
	 * @param player  The player that should receive the message.
	 * @param message The message to be sent.
	 */
	public static void send(@NotNull ProxiedPlayer player, @NotNull Message message)
	{
		send(player, message.toString());
	}

	/**
	 * Sends a JSON message to a players action bar.
	 *
	 * @param players The players that should receive the message.
	 * @param json    The message in JSON format to be sent.
	 */
	public static void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json)
	{
		send(players, json, ACTION_BAR_ACTION);
	}

	/**
	 * Sends a JSON message to a players action bar.
	 *
	 * @param players The players that should receive the message.
	 * @param message The message to be sent.
	 */
	public static void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull Message message)
	{
		send(players, message.toString());
	}

	/**
	 * Sends a JSON message to the action bar of all online players.
	 *
	 * @param json The message in JSON format to be sent.
	 */
	public static void broadcast(@NotNull String json)
	{
		broadcast(json, ACTION_BAR_ACTION);
	}

	/**
	 * Sends a JSON message to the action bar of all online players.
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
	public void doBroadcast(@NotNull String json, @Nullable Object optional)
	{
		broadcast(json);
	}

	@Override
	public void doSend(@NotNull ProxiedPlayer player, @NotNull String json)
	{
		send(player, json);
	}

	@Override
	public void doSend(@NotNull ProxiedPlayer player, @NotNull String json, @Nullable Object optional)
	{
		send(player, json);
	}

	@Override
	public void doSend(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json)
	{
		send(players, json);
	}

	@Override
	public void doSend(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json, @Nullable Object optional)
	{
		send(players, json);
	}
}