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

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.NmsReflector;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
class Utils_Reflection implements IUtils
{
	//region Reflection constants for the send packet method
	static final Class<?> ENTITY_PLAYER = NmsReflector.INSTANCE.getNmsClass("EntityPlayer");
	static final Class<?> PACKET = NmsReflector.INSTANCE.getNmsClass("Packet");
	static final Method SEND_PACKET = NmsReflector.INSTANCE.getNmsMethod("PlayerConnection", "sendPacket", PACKET);
	static final Field PLAYER_CONNECTION = NmsReflector.INSTANCE.getNmsField(ENTITY_PLAYER, "playerConnection");
	//endregion
	private static final Field PLAYER_PING = NmsReflector.INSTANCE.getNmsField(ENTITY_PLAYER, "ping");

	@Override
	public int getPing(final @NotNull Player player)
	{
		Object handle = NmsReflector.getHandle(player);
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
		return -1;
	}

	@Override
	public void sendPacket(@NotNull Player player, @NotNull Object packet)
	{
		Object handle = NmsReflector.getHandle(player);
		if(handle != null && handle.getClass() == ENTITY_PLAYER) // If it's not a real player we can't send him the packet
		{
			try
			{
				SEND_PACKET.invoke(PLAYER_CONNECTION.get(handle), packet);
			}
			catch(IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
		}
	}
}