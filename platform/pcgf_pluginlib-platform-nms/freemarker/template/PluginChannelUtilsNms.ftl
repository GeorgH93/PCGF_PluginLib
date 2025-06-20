/*
 *   Copyright (C) 2021 GeorgH93
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

<#if mojangMapped>
import net.minecraft.network.FriendlyByteBuf;
	<#if mcVersion < 100200002>
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
	<#else>
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
	</#if>
<#else>
	<#if mcVersion < 100170000>
		<#if 100130000 <= mcVersion>
import net.minecraft.server.v${nmsVersion}.MinecraftKey;
import net.minecraft.server.v${nmsVersion}.PacketDataSerializer;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutCustomPayload;
		</#if>
	<#else>
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import net.minecraft.resources.MinecraftKey;
	</#if>
</#if>

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import io.netty.buffer.Unpooled;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: https://freemarker.apache.org/
 * See template: ${.main_template_name}
 */
public class PluginChannelUtils_${nmsVersion}${nmsPatchLevel}${nmsExtension} extends PluginChannelUtils_Reflection
{
	<#if mcVersion < 100200002>
	@Override
	public void sendPluginMessageUnchecked(final @NotNull Plugin plugin, final @NotNull Player player, final @NotNull String channel, final @NotNull byte[] message)
	{
		<#if mojangMapped>
		FriendlyByteBuf serializer = new FriendlyByteBuf(Unpooled.wrappedBuffer(message));
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(new ResourceLocation(channel), serializer);
		IUtils.INSTANCE.sendPacket(player, packet);
		<#else>
			<#if 100130000 <= mcVersion>
		PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.wrappedBuffer(message));
		PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(new MinecraftKey(channel), serializer);
		IUtils.INSTANCE.sendPacket(player, packet);
			<#else>
		player.sendPluginMessage(plugin, channel, message);
			</#if>
		</#if>
	}
	</#if>
}