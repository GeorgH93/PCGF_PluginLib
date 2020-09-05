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

import at.pcgamingfreaks.Bukkit.Protocol.ITitleMessagePacketFactory;
import at.pcgamingfreaks.Bukkit.Util.Utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

final class ActionBarTitleSender implements ISender
{
	private static final ITitleMessagePacketFactory TITLE_MESSAGE_PACKET_FACTORY = ITitleMessagePacketFactory.INSTANCE;

	@Override
	public void doSend(@NotNull Player player, @NotNull String json)
	{
		Utils.sendPacket(player, TITLE_MESSAGE_PACKET_FACTORY.makeTitlePacketActionBar(json));
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json)
	{
		Object titlePacket = TITLE_MESSAGE_PACKET_FACTORY.makeTitlePacketActionBar(json);
		for(Player player : players)
		{
			Utils.sendPacket(player, titlePacket);
		}
	}
}