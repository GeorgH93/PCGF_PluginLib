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

package at.pcgamingfreaks.Bungee.Message.Sender;

import at.pcgamingfreaks.Message.Sender.IMetadata;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Title;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

final class TitleSender implements ISender
{
	private static final TitleMetadata METADATA = new TitleMetadata(); // Default metadata object
	private static final Title EMPTY_TITLE = mkTitlePacket("", METADATA);

	private static Title mkTimesPacket(final @NotNull TitleMetadata metadata)
	{
		Title titleTimes = new Title();
		titleTimes.setAction(Title.Action.TIMES);
		titleTimes.setFadeIn(metadata.getFadeIn());
		titleTimes.setStay(metadata.getStay());
		titleTimes.setFadeOut(metadata.getFadeOut());
		return titleTimes;
	}

	private static Title mkTitlePacket(final @NotNull String json, final @NotNull TitleMetadata metadata)
	{
		Title titleSend = new Title();
		titleSend.setAction(metadata.getTitleType());
		titleSend.setText(json);
		return titleSend;
	}

	@Override
	public void send(@NotNull ProxiedPlayer player, @NotNull String json)
	{
		send(player, json, METADATA);
	}

	@Override
	public void send(@NotNull ProxiedPlayer player, @NotNull String json, @Nullable IMetadata optional)
	{
		TitleMetadata metadata = (optional instanceof TitleMetadata) ? (TitleMetadata) optional : METADATA;
		if(metadata.isActionBar()) player.unsafe().sendPacket(mkTitlePacket(json, metadata));
		else
		{
			player.unsafe().sendPacket(mkTitlePacket(json, metadata));
			if(metadata.isSubtitle())
			{
				player.unsafe().sendPacket(EMPTY_TITLE);
			}
		}
	}

	@Override
	public void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json)
	{
		send(players, json, METADATA);
	}

	@Override
	public void send(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json, @Nullable IMetadata optional)
	{
		TitleMetadata metadata = (optional instanceof TitleMetadata) ? (TitleMetadata) optional : METADATA;
		Title titleTimes = mkTimesPacket(metadata), titleSend = mkTitlePacket(json, metadata), title = metadata.isSubtitle() ? EMPTY_TITLE : null;

		for(ProxiedPlayer player : players)
		{
			player.unsafe().sendPacket(titleTimes);
			player.unsafe().sendPacket(titleSend);
			if(title != null) player.unsafe().sendPacket(title);
		}
	}

	@Override
	public void broadcast(@NotNull String json)
	{
		broadcast(json, METADATA);
	}
}