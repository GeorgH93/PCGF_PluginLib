/*
 *   Copyright (C) 2022 GeorgH93
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

<#if mojangMapped>
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
	<#if mcVersion < 100190000>
import net.minecraft.network.protocol.game.ClientboundChatPacket;
	<#else>
//import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket; //TODO add support
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
	</#if>
<#else>
	<#if mcVersion < 100170000>
import net.minecraft.server.v${nmsVersion}.ChatMessageType;
import net.minecraft.server.v${nmsVersion}.IChatBaseComponent;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutChat;
	<#else>
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
	</#if>
</#if>

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: https://freemarker.apache.org/
 * See template: ${.main_template_name}
 */
public final class ChatMessagePacketFactory_${nmsVersion}${nmsPatchLevel}${nmsExtension} implements IChatMessagePacketFactory
{
	@Override
	public Object makeChatPacket(final @NotNull String json, final @NotNull UUID sender)
	{
		<#if mojangMapped>
			<#if mcVersion < 100190000>
		return new ClientboundChatPacket((Component) IUtils.INSTANCE.jsonToIChatComponent(json), ChatType.CHAT, sender);
			<#elseif mcVersion < 100190001>
		return new ClientboundSystemChatPacket((Component) IUtils.INSTANCE.jsonToIChatComponent(json), 1);
			<#else>
		return new ClientboundSystemChatPacket((Component) IUtils.INSTANCE.jsonToIChatComponent(json), false);
			</#if>
		<#else>
			<#if mcVersion < 100160000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.CHAT);
			<#elseif mcVersion < 100170000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.CHAT, sender);
			<#else>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.a, sender);
			</#if>
		</#if>
	}

	@Override
	public Object makeChatPacketSystem(final @NotNull String json)
	{
		<#if mojangMapped>
			<#if mcVersion < 100190000>
		return new ClientboundChatPacket((Component) IUtils.INSTANCE.jsonToIChatComponent(json), ChatType.SYSTEM, EMPTY_UUID);
			<#elseif mcVersion < 100190001>
		return new ClientboundSystemChatPacket((Component) IUtils.INSTANCE.jsonToIChatComponent(json), 1);
			<#else>
		return new ClientboundSystemChatPacket((Component) IUtils.INSTANCE.jsonToIChatComponent(json), false);
			</#if>
		<#else>
			<#if mcVersion < 100160000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.SYSTEM);
			<#elseif mcVersion < 100170000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.SYSTEM, EMPTY_UUID);
			<#else>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.b, EMPTY_UUID);
			</#if>
		</#if>
	}

	@Override
	public Object makeChatPacketActionBar(final @NotNull String json)
	{
		<#if mojangMapped>
			<#if mcVersion < 100190000>
		return new ClientboundChatPacket((Component) IUtils.INSTANCE.jsonToIChatComponent(json), ChatType.GAME_INFO, EMPTY_UUID);
			<#elseif mcVersion < 100190001>
		return new ClientboundSystemChatPacket((Component) IUtils.INSTANCE.jsonToIChatComponent(json), 2);
			<#else>
		return new ClientboundSystemChatPacket((Component) IUtils.INSTANCE.jsonToIChatComponent(json), true);
			</#if>
		<#else>
			<#if mcVersion < 100160000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.GAME_INFO);
			<#elseif mcVersion < 100170000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.GAME_INFO, EMPTY_UUID);
			<#else>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.c, EMPTY_UUID);
			</#if>
		</#if>
	}
}