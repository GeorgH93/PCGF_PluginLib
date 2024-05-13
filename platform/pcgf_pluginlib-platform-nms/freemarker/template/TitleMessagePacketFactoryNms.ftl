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

package at.pcgamingfreaks.Bukkit.Protocol;

import at.pcgamingfreaks.Bukkit.Util.IUtils;

<#if mcVersion < 100170000>
import net.minecraft.server.v${nmsVersion}.IChatBaseComponent;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutTitle;
<#else>
import net.minecraft.network.chat.<#if !mojangMapped>IChatBase</#if>Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
</#if>

import org.jetbrains.annotations.NotNull;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: https://freemarker.apache.org/
 * See template: ${.main_template_name}
 */
public final class TitleMessagePacketFactory_${nmsVersion}${nmsPatchLevel}${nmsExtension} implements ITitleMessagePacketFactory
{
	@Override
	public Object makeTitlePacket(final @NotNull String json)
	{
		<#if mcVersion < 100170000>
		return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, (IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json));
		<#else>
		return new ClientboundSetTitleTextPacket((<#if !mojangMapped>IChatBase</#if>Component) IUtils.INSTANCE.jsonToIChatComponent(json));
		</#if>
	}

	@Override
	public Object makeSubTitlePacket(final @NotNull String json)
	{
		<#if mcVersion < 100170000>
		return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, (IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json));
		<#else>
		return new ClientboundSetSubtitleTextPacket((<#if !mojangMapped>IChatBase</#if>Component) IUtils.INSTANCE.jsonToIChatComponent(json));
		</#if>
	}

	@Override
	public Object makeTitlePacketTime(final int fadeIn, final int stay, final int fadeOut)
	{
		<#if mcVersion < 100170000>
		return new PacketPlayOutTitle(fadeIn, stay, fadeOut);
		<#else>
		return new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
		</#if>
	}

	@Override
	public Object makeTitlePacketActionBar(final @NotNull String json)
	{
		<#if mcVersion < 100110000>
		return IChatMessagePacketFactory.INSTANCE.makeChatPacketActionBar(json);
		<#elseif mcVersion < 100170000>
		return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, (IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json));
		<#else>
		return new ClientboundSetActionBarTextPacket((<#if !mojangMapped>IChatBase</#if>Component) IUtils.INSTANCE.jsonToIChatComponent(json));
		</#if>
	}
}