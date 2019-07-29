/*
 *   Copyright (C) 2019 GeorgH93
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.lang.reflect.Method;

public enum SendMethod implements ISendMethod
{
	CHAT(new ChatSender(), null),
	TITLE(new TitleSender(), TitleMetadata.class),
	ACTION_BAR(new ActionBarSender(), null),
	//BOSS_BAR(new BossBarSender(), null), //TODO
	DISABLED(new DisabledSender(), null);

	@Getter private final BaseSender sender;
	@Getter private final Class<?> metadataClass;
	@Getter private final Method metadataFromJsonMethod;

	SendMethod(@NotNull BaseSender sender, @Nullable Class<?> metadataClass)
	{
		this.sender = sender;
		this.metadataClass = metadataClass;
		this.metadataFromJsonMethod = (metadataClass != null) ? Reflection.getMethod(metadataClass, "fromJson", String.class) : null;
	}
}