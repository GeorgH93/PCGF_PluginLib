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

import at.pcgamingfreaks.Message.Sender.ISendMethod;
import at.pcgamingfreaks.Reflection;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Collection;

public enum SendMethod implements ISendMethod, Sender
{
	CHAT(new ChatSender(), null),
	TITLE(new TitleSender(), TitleMetadata.class),
	ACTION_BAR(new ActionBarSender(), null),
	//BOSS_BAR(new BossBarSender(), null), //TODO
	DISABLED(new DisabledSender(), null);

	@Getter @NotNull private final Sender sender;
	@Getter @Nullable private final Class<?> metadataClass;
	@Getter @Nullable private final Method metadataFromJsonMethod;

	SendMethod(@NotNull Sender sender, @Nullable Class<?> metadataClass)
	{
		this.sender = sender;
		this.metadataClass = metadataClass;
		this.metadataFromJsonMethod = (metadataClass != null) ? Reflection.getMethod(metadataClass, "fromJson", String.class) : null;
	}

	@Override
	public void doSend(@NotNull ProxiedPlayer proxiedPlayer, @NotNull String json)
	{
		sender.doSend(proxiedPlayer, json);
	}

	@Override
	public void doSend(@NotNull ProxiedPlayer proxiedPlayer, @NotNull String json, @Nullable Object optionalMetadata)
	{
		sender.doSend(proxiedPlayer, json, optionalMetadata);
	}

	@Override
	public void doSend(@NotNull Collection<? extends ProxiedPlayer> proxiedPlayers, @NotNull String json)
	{
		sender.doSend(proxiedPlayers, json);
	}

	@Override
	public void doSend(@NotNull Collection<? extends ProxiedPlayer> proxiedPlayers, @NotNull String json, @Nullable Object optionalMetadata)
	{
		sender.doSend(proxiedPlayers, json, optionalMetadata);
	}

	@Override
	public void doBroadcast(@NotNull String json)
	{
		sender.doBroadcast(json);
	}

	@Override
	public void doBroadcast(@NotNull String json, @Nullable Object optionalMetadata)
	{
		sender.doBroadcast(json, optionalMetadata);
	}
}