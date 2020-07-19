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

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Reflection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
class PluginChannelUtils_Reflection implements IPluginChannelUtils
{
	private static final Class<?> CLASS_MINECRAFT_KEY = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? NmsReflector.INSTANCE.getNmsClass("MinecraftKey") : null;
	private static final Class<?> CLASS_PACKET_DATA_SERIALIZER = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? NmsReflector.INSTANCE.getNmsClass("PacketDataSerializer") : null;
	private static final Class<?> CLASS_PACKET_PLAY_OUT_CUSTOM_PAYLOAD = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? NmsReflector.INSTANCE.getNmsClass("PacketPlayOutCustomPayload") : null;
	@SuppressWarnings("SpellCheckingInspection")
	private static final Class<?> CLASS_NETTY_UNPOOLED = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? Reflection.getClass("io.netty.buffer.Unpooled") : null;
	private static final Class<?> CLASS_NETTY_BYTE_BUF = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? Reflection.getClass("io.netty.buffer.ByteBuf") : null;
	private static final Constructor<?> CONSTRUCTOR_MINECRAFT_KEY = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? Reflection.getConstructor(CLASS_MINECRAFT_KEY, String.class) : null;
	private static final Constructor<?> CONSTRUCTOR_PACKED_DATA_SERIALIZER = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? Reflection.getConstructor(CLASS_PACKET_DATA_SERIALIZER, CLASS_NETTY_BYTE_BUF) : null;
	private static final Constructor<?> CONSTRUCTOR_PACKET_PLAY_OUT_CUSTOM_PAYLOAD = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? Reflection.getConstructor(CLASS_PACKET_PLAY_OUT_CUSTOM_PAYLOAD, CLASS_MINECRAFT_KEY, CLASS_PACKET_DATA_SERIALIZER) : null;
	private static final Method METHOD_ADD_TO_OUTGOING = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? Reflection.getMethod(StandardMessenger.class, "addToOutgoing", Plugin.class, String.class) : null;
	private static final Method METHOD_REMOVE_FROM_OUTGOING = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? Reflection.getMethod(StandardMessenger.class, "removeFromOutgoing", Plugin.class, String.class) : null;
	@SuppressWarnings("SpellCheckingInspection")
	private static final Method METHOD_UNPOOLED_WRAPPED_BUFFER = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? Reflection.getMethod(CLASS_NETTY_UNPOOLED, "wrappedBuffer", byte[].class) : null;

	@Override
	public void registerOutgoingChannelUnchecked(@NotNull Plugin plugin, @NotNull String channel)
	{
		if(METHOD_ADD_TO_OUTGOING != null)
		{
			try
			{
				METHOD_ADD_TO_OUTGOING.invoke(plugin.getServer().getMessenger(), plugin, channel);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
	}

	@Override
	public void unregisterOutgoingChannelUnchecked(@NotNull Plugin plugin, @NotNull String channel)
	{
		if(METHOD_REMOVE_FROM_OUTGOING != null)
		{
			try
			{
				METHOD_REMOVE_FROM_OUTGOING.invoke(plugin.getServer().getMessenger(), plugin, channel);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, channel);
	}

	@Override
	public void sendPluginMessageUnchecked(@NotNull Plugin plugin, @NotNull Player player, @NotNull String channel, @NotNull byte[] message)
	{
		if(CONSTRUCTOR_PACKED_DATA_SERIALIZER != null)
		{
			try
			{
				Object packedMsg = CONSTRUCTOR_PACKED_DATA_SERIALIZER.newInstance(METHOD_UNPOOLED_WRAPPED_BUFFER.invoke(null, message));
				Object pack = CONSTRUCTOR_PACKET_PLAY_OUT_CUSTOM_PAYLOAD.newInstance(CONSTRUCTOR_MINECRAFT_KEY.newInstance(channel), packedMsg);
				IUtils.INSTANCE.sendPacket(player, pack);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else player.sendPluginMessage(plugin, channel, message);
	}
}