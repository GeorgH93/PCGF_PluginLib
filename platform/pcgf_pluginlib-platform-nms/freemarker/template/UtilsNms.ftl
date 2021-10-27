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
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
<#else>
	<#if mcVersion < 100170000>
import net.minecraft.server.v${nmsVersion}.EntityPlayer;
import net.minecraft.server.v${nmsVersion}.Packet;
import net.minecraft.server.v${nmsVersion}.IChatBaseComponent;
	<#else>
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
	</#if>
</#if>

import org.bukkit.craftbukkit.v${nmsVersion}.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: https://freemarker.apache.org/
 * See template: ${.main_template_name}
 */
public final class Utils_${nmsVersion} implements IUtils
{
	static <#if mojangMapped>ServerPlayer<#else>EntityPlayer</#if> getHandle(final @NotNull Player player)
	{
		return ((CraftPlayer) player).getHandle();
	}

	@Override
	public int getPing(final @NotNull Player player)
	{
		<#if mcVersion < 100170000>
		return getHandle(player).ping;
		<#else>
		return player.getPing();
		</#if>
	}

	@Override
	public void sendPacket(final @NotNull Player player, final @NotNull Object packet)
	{
		getHandle(player).
		<#if mojangMapped>
			connection.send((Packet<?>) packet);
		<#else>
			<#if mcVersion < 100170000>playerConnection<#else>b</#if>.sendPacket((Packet<?>) packet);
		</#if>
	}

	@Override
	public Object jsonToIChatComponent(@NotNull String json)
	{
		<#if mojangMapped>
		return Component.Serializer.fromJson(json);
		<#else>
		return IChatBaseComponent.ChatSerializer.a(json);
		</#if>
	}
}