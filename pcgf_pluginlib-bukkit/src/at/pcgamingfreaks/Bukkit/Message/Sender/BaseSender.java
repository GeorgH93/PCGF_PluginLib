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
import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.NmsReflector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This abstract class is used as base class for all senders.
 * This way we can tell the {@link Message} class which sender should get used without using reflection.
 */
public abstract class BaseSender implements Sender
{
	//region Reflection stuff
	protected static final Class<?> CHAT_SERIALIZER = NmsReflector.INSTANCE.getNmsClass((MCVersion.is(MCVersion.MC_NMS_1_8_R1)) ? "ChatSerializer" : "IChatBaseComponent$ChatSerializer");
	protected static final Class<?> I_CHAT_BASE_COMPONENT = NmsReflector.INSTANCE.getNmsClass("IChatBaseComponent");
	protected static final Method CHAT_SERIALIZER_METHOD_A = NmsReflector.INSTANCE.getNmsMethod(CHAT_SERIALIZER, "a", String.class);
	//endregion

	public static @Nullable Object finalizeJson(@NotNull String json) throws InvocationTargetException, IllegalAccessException
	{
		return CHAT_SERIALIZER_METHOD_A != null ? CHAT_SERIALIZER_METHOD_A.invoke(null, json) : null;
	}
}