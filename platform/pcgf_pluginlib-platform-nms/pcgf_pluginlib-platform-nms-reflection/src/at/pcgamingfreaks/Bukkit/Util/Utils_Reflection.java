/*
 *   Copyright (C) 2024 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Bukkit.OBCReflection;
import at.pcgamingfreaks.Reflection;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
public class Utils_Reflection implements IUtils
{
	//region Reflection constants for the sendPacket method
	static final Class<?> ENTITY_PLAYER = MCVersion.isOlderThan(MCVersion.MC_NMS_1_20_R4) ? NmsReflector.INSTANCE.getNmsClass("EntityPlayer") : null;
	static final Class<?> PACKET = NmsReflector.INSTANCE.getNmsClass("Packet");
	static final Method SEND_PACKET = MCVersion.isOlderThan(MCVersion.MC_NMS_1_20_R4) ? NmsReflector.INSTANCE.getNmsMethod("PlayerConnection", "sendPacket", PACKET) : null;
	static final Field PLAYER_CONNECTION = MCVersion.isOlderThan(MCVersion.MC_NMS_1_20_R4) ? NmsReflector.INSTANCE.getNmsField(ENTITY_PLAYER, "playerConnection") : null;
	//endregion
	private static final Field PLAYER_PING = MCVersion.isOlderThan(MCVersion.MC_1_18) ? NmsReflector.INSTANCE.getNmsField(ENTITY_PLAYER, "ping") : null;
	private static final Method GET_PLAYER_PING = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_18) ? Reflection.getMethod(Player.class, "getPing") : null;
	//region Reflection constants for the json to IChatComponent converter
	private static final Class<?> CHAT_SERIALIZER = MCVersion.isOlderThan(MCVersion.MC_NMS_1_20_R4) ? NmsReflector.INSTANCE.getNmsClass((MCVersion.is(MCVersion.MC_NMS_1_8_R1)) ? "ChatSerializer" : "IChatBaseComponent$ChatSerializer") : null;
	private static final Method CHAT_SERIALIZER_METHOD_A = MCVersion.isOlderThan(MCVersion.MC_NMS_1_20_R4) ? NmsReflector.INSTANCE.getNmsMethod(CHAT_SERIALIZER, "a", String.class) : OBCReflection.getOBCMethod("util.CraftChatMessage", "fromJSON", String.class);
	//endregion

	@Override
	public Object getHandle(final @NotNull Player player)
	{
		return NmsReflector.getHandle(player);
	}

	@Override
	public int getPing(final @NotNull Player player)
	{
		if(PLAYER_PING != null)
		{
			Object handle = getHandle(player);
			if(handle != null && handle.getClass() == ENTITY_PLAYER) // If it's not a real player we can't send him the packet
			{
				try
				{
					return PLAYER_PING.getInt(handle);
				}
				catch(IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			try
			{
				return (int) GET_PLAYER_PING.invoke(player);
			}
			catch(IllegalAccessException | InvocationTargetException ignored) {}
		}
		return -1;
	}

	@Override
	public void sendPacket(@NotNull Player player, @NotNull Object packet)
	{
		Object handle = getHandle(player);
		if(handle != null && (ENTITY_PLAYER == null || handle.getClass() == ENTITY_PLAYER)) // If it's not a real player we can't send him the packet
		{
			try
			{
				if (MCVersion.isOlderThan(MCVersion.MC_NMS_1_20_R4))
				{
					SEND_PACKET.invoke(PLAYER_CONNECTION.get(handle), packet);
				}
				else
				{
					Object conn = handle.getClass().getField("connection").get(handle);
					conn.getClass().getMethod("sendPacket", PACKET).invoke(conn, packet);
				}
			}
			catch(IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
			catch(NoSuchFieldException e)
			{
				throw new RuntimeException(e);
			}
			catch(NoSuchMethodException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Object jsonToIChatComponent(final @NotNull String json)
	{
		try
		{
			return CHAT_SERIALIZER_METHOD_A.invoke(null, json);
		}
		catch(IllegalAccessException | InvocationTargetException | NullPointerException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}