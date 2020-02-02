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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.lang.reflect.Method;

import static at.pcgamingfreaks.Bukkit.MCVersion.MC_1_8;

public enum SendMethod implements ISendMethod
{
	CHAT_CLASSIC(null, null, null),
	CHAT(MCVersion.isOlderThan(MC_1_8) ? null : new ChatSender(), null, CHAT_CLASSIC),
	TITLE(MCVersion.isOlderThan(MC_1_8) ? null : new TitleSender(), TitleMetadata.class, CHAT),
	ACTION_BAR(MCVersion.isOlderThan(MC_1_8) ? null : new ActionBarSender(), null, CHAT),
	//BOSS_BAR(new BossBarSender(), BossBarMetadata.class), //TODO
	DISABLED(new DisabledSender(), null, null);

	@Getter @Nullable private final Sender sender;
	@Getter @Nullable private final Class<?> metadataClass;
	@Getter @Nullable private final Method metadataFromJsonMethod;
	@Getter @NotNull private final SendMethod fallbackSendMethod;

	/**
	 * @param sender An instance to an implementation of the Sender class
	 * @param metadataClass An optional class defining the metadata for this send method
	 * @param fallback A fallback send method that should be used if the used send method is not available on the used MC version.
	 *                    There should always be one available if the send method is not available on all MC versions.
	 */
	SendMethod(@Nullable Sender sender, @Nullable Class<?> metadataClass, @Nullable SendMethod fallback)
	{
		this.sender = sender;
		this.metadataClass = metadataClass;
		this.metadataFromJsonMethod = (metadataClass != null) ? Reflection.getMethod(this.metadataClass, "fromJson", String.class) : null;
		if(fallback != null)
		{
			if(!fallback.isAvailable()) fallback = fallback.getFallbackSendMethod();
		}
		else fallback = this;
		fallbackSendMethod = fallback;
	}

	/**
	 * Checks if the send method is available on the used server version
	 *
	 * @return True if the send method is available on the used server version. False if not.
	 */
	public boolean isAvailable()
	{
		return sender != null;
	}

	/**
	 * Checks if the send method allows to set additional parameters via a metadata class.
	 *
	 * @return True if the send method supports additional send method parameters. False if not.
	 */
	public boolean hasMetadata()
	{
		return metadataClass != null;
	}
}