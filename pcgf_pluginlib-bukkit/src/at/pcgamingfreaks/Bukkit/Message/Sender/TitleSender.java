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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Bukkit.Protocol.ITitleMessagePacketFactory;
import at.pcgamingfreaks.Bukkit.Util.Utils;
import at.pcgamingfreaks.Message.Sender.IMetadata;
import at.pcgamingfreaks.Message.Sender.ITitleMetadata;
import at.pcgamingfreaks.Message.Sender.TitleMetadata;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

final class TitleSender implements ISender
{
	private static final ITitleMessagePacketFactory TITLE_MESSAGE_PACKET_FACTORY = ITitleMessagePacketFactory.INSTANCE;
	private static final ITitleMetadata METADATA = new TitleMetadata(); // Default metadata object
	private static final Object PACKET_EMPTY_TITLE = TITLE_MESSAGE_PACKET_FACTORY == null ? null : TITLE_MESSAGE_PACKET_FACTORY.makeTitlePacket("[{\"text\":\"\"}]"); // TITLE_MESSAGE_PACKET_FACTORY will be null during unit tests!

	@Override
	public void send(final @NotNull Player player, final @NotNull String json)
	{
		send(player, json, METADATA);
	}

	@Override
	public void send(final @NotNull Player player, final @NotNull String json, @Nullable IMetadata optionalMetadata)
	{
		if(!(optionalMetadata instanceof ITitleMetadata)) optionalMetadata = METADATA;
		ITitleMetadata metadata = (ITitleMetadata) optionalMetadata;
		if(metadata.isActionBar()) Utils.sendPacket(player, TITLE_MESSAGE_PACKET_FACTORY.makeTitlePacketActionBar(json));
		else
		{
			Utils.sendPacket(player, TITLE_MESSAGE_PACKET_FACTORY.makeTitlePacketTime(metadata.getFadeIn(), metadata.getStay(), metadata.getFadeOut()));
			if(metadata.isTitle())
			{
				Utils.sendPacket(player, TITLE_MESSAGE_PACKET_FACTORY.makeTitlePacket(json));
			}
			else
			{
				Utils.sendPacket(player, TITLE_MESSAGE_PACKET_FACTORY.makeSubTitlePacket(json));
				Utils.sendPacket(player, PACKET_EMPTY_TITLE);
			}
		}
	}

	@Override
	public void send(final @NotNull Collection<? extends Player> players, final @NotNull String json)
	{
		send(players, json, METADATA);
	}

	@Override
	public void send(final @NotNull Collection<? extends Player> players, final @NotNull String json, @Nullable IMetadata optionalMetadata)
	{
		if(optionalMetadata == null) optionalMetadata = METADATA;
		ITitleMetadata metadata = (ITitleMetadata) optionalMetadata;
		if(metadata.isActionBar())
		{
			Object packet = TITLE_MESSAGE_PACKET_FACTORY.makeTitlePacketActionBar(json);
			for(Player player : players)
			{
				Utils.sendPacket(player, packet);
			}
		}
		else
		{
			Object packetTime = TITLE_MESSAGE_PACKET_FACTORY.makeTitlePacketTime(metadata.getFadeIn(), metadata.getStay(), metadata.getFadeOut());
			Object packetTitle = PACKET_EMPTY_TITLE, packetSubTitle = null;
			if(metadata.isTitle()) packetTitle = TITLE_MESSAGE_PACKET_FACTORY.makeTitlePacket(json);
			else packetSubTitle = TITLE_MESSAGE_PACKET_FACTORY.makeSubTitlePacket(json);
			for(Player player : players)
			{
				Utils.sendPacket(player, packetTime);
				Utils.sendPacket(player, packetTitle);
				if(packetSubTitle != null) Utils.sendPacket(player, packetSubTitle);
			}
		}
	}
}