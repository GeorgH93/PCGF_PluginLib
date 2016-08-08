/*
 *   Copyright (C) 2016 GeorgH93
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
import at.pcgamingfreaks.Bukkit.NMSReflection;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * This abstract class is used as base class for all senders.
 * This way we can tell the {@link Message} class which sender should get used without using reflection.
 */
public abstract class BaseSender
{
	//region Reflection stuff
	protected final static Class<?> CHAT_SERIALIZER = NMSReflection.getNMSClass((NMSReflection.getVersion().equalsIgnoreCase("v1_8_R2") || NMSReflection.getVersion().equalsIgnoreCase("v1_8_R3")) ? "IChatBaseComponent$ChatSerializer" : "ChatSerializer");
	protected final static Class<?> I_CHAT_BASE_COMPONENT = NMSReflection.getNMSClass("IChatBaseComponent");
	protected final static Method CHAT_SERIALIZER_METHOD_A = NMSReflection.getMethod(CHAT_SERIALIZER, "a", String.class);
	//endregion

	public abstract void doSend(@NotNull Player player, @NotNull String json);

	public abstract void doSend(@NotNull Player player, @NotNull String json, @Nullable Object optional);

	public abstract void doSend(@NotNull Collection<? extends Player> players, @NotNull String json);

	public abstract void doSend(@NotNull Collection<? extends Player> players, @NotNull String json, @Nullable Object optional);

	public abstract void doBroadcast(@NotNull String json);

	public abstract void doBroadcast(@NotNull String json, @Nullable Object optional);

	public static @Nullable Object finalizeJson(@NotNull String json) throws InvocationTargetException, IllegalAccessException
	{
		return CHAT_SERIALIZER_METHOD_A != null ? CHAT_SERIALIZER_METHOD_A.invoke(null, json) : null;
	}
}