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

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Message.Sender.ISendMethod;
import at.pcgamingfreaks.Reflection;

import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.lang.reflect.Method;

import static at.pcgamingfreaks.Bukkit.MCVersion.MC_1_8;

public enum SendMethod implements ISendMethod
{
	CHAT_CLASSIC(null, null),
	CHAT(MCVersion.isOlderThan(MC_1_8) ? null : new ChatSender(), null),
	TITLE(MCVersion.isOlderThan(MC_1_8) ? null : new TitleSender(), TitleMetadata.class),
	ACTION_BAR(MCVersion.isOlderThan(MC_1_8) ? null : new ActionBarSender(), null),
	//BOSS_BAR(new BossBarSender(), BossBarMetadata.class), //TODO
	DISABLED(new DisabledSender(), null);

	@Getter private final Sender sender;
	@Getter private final Class<?> metadataClass;
	@Getter private final Method metadataFromJsonMethod;

	SendMethod(Sender sender, @Nullable Class<?> metadataClass)
	{
		this.sender = sender;
		this.metadataClass = metadataClass;
		this.metadataFromJsonMethod = (metadataClass != null) ? Reflection.getMethod(this.metadataClass, "fromJson", String.class) : null;
	}
}