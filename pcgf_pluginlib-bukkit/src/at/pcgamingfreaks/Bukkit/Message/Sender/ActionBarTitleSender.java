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

import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.Util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ActionBarTitleSender extends BaseSender
{
	/**
	 * Sends a JSON message to a player shown as title.
	 *
	 * @param player The player that should receive the message.
	 * @param json   The message in JSON format to be sent.
	 */
	public static void send(@NotNull Player player, @NotNull String json)
	{
		try
		{
			Utils.sendPacket(player, TitleSender.PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(TitleSender.ENUM_ACTION_BAR, finalizeJson(json), -1, -1, -1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players The players that should receive the message.
	 * @param json    The message in JSON format to be sent.
	 */
	public static void send(@NotNull Collection<? extends Player> players, @NotNull String json)
	{
		try
		{
			Object titlePacket = TitleSender.PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(TitleSender.ENUM_ACTION_BAR, finalizeJson(json), -1, -1, -1);
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
	 * Sends a JSON message to players shown as title.
	 *
	 * @param players The players that should receive the message.
	 * @param message The message to be sent.
	 */
	public static void send(@NotNull Collection<? extends Player> players, @NotNull Message message)
	{
		send(players, message.toString());
	}

	@Override
	public void doSend(@NotNull Player player, @NotNull String json)
	{
		send(player, json);
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json)
	{
		send(players, json);
	}


	@Override
	public void doBroadcast(@NotNull String json)
	{
		broadcast(json);
	}

	/**
	 * Sends a JSON message shown as title to all online players.
	 *
	 * @param json     The message in JSON format to be sent.
	 */
	public static void broadcast(@NotNull String json)
	{
		send(Bukkit.getOnlinePlayers(), json);
	}
}