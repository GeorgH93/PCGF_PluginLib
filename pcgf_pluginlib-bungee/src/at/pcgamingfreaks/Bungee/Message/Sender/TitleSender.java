/*
 *   Copyright (C) 2023 GeorgH93
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

import at.pcgamingfreaks.Message.Sender.IMetadata;
import at.pcgamingfreaks.Reflection;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.packet.Title;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

final class TitleSender implements ISender
{
	private static final Method SET_TEXT;
	private static final boolean BASE_COMPONENT_TITLE;
	private static final TitleMetadata METADATA = new TitleMetadata(); // Default metadata object
	private static final Title EMPTY_TITLE;

	static
	{
		Method setText;
		boolean baseComp = false;
		try
		{
			setText = Title.class.getMethod("setText", String.class);
		}
		catch(Exception ignored)
		{
			setText = Reflection.getMethod(Title.class, "setText", Reflection.getClass("net.md_5.bungee.api.chat.BaseComponent"));
			baseComp = true;
		}
		SET_TEXT = setText;
		BASE_COMPONENT_TITLE = baseComp;
		EMPTY_TITLE = mkTitlePacket("", METADATA);
	}

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
		try
		{
			if (BASE_COMPONENT_TITLE)
			{
				BaseComponent sendComponent;
				BaseComponent[] components = ComponentSerializer.parse(json);
				if (components.length == 0) return titleSend;
				else if (components.length == 1) sendComponent = components[0];
				else sendComponent = new TextComponent(components);
				SET_TEXT.invoke(titleSend, sendComponent);
			}
			else
			{
				SET_TEXT.invoke(titleSend, json);
			}
		}
		catch(IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
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