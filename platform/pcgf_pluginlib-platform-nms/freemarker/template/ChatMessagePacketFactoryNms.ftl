package at.pcgamingfreaks.Bukkit.Protocol;

import at.pcgamingfreaks.Bukkit.Util.IUtils;

<#if mcVersion < 100170000>
import net.minecraft.server.v${nmsVersion}.ChatMessageType;
import net.minecraft.server.v${nmsVersion}.IChatBaseComponent;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutChat;
<#else>
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
</#if>

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: https://freemarker.apache.org/
 * See template: ${.main_template_name}
 */
public final class ChatMessagePacketFactory_${nmsVersion} implements IChatMessagePacketFactory
{
	@Override
	public Object makeChatPacket(final @NotNull String json, final @NotNull UUID sender)
	{
		<#if mcVersion < 100160000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.CHAT);
		<#elseif mcVersion < 100170000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.CHAT, sender);
		<#else>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.a, sender);
		</#if>
	}

	@Override
	public Object makeChatPacketSystem(final @NotNull String json)
	{
		<#if mcVersion < 100160000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.SYSTEM);
		<#elseif mcVersion < 100170000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.SYSTEM, EMPTY_UUID);
		<#else>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.b, EMPTY_UUID);
		</#if>
	}

	@Override
	public Object makeChatPacketActionBar(final @NotNull String json)
	{
		<#if mcVersion < 100160000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.GAME_INFO);
		<#elseif mcVersion < 100170000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.GAME_INFO, EMPTY_UUID);
		<#else>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.c, EMPTY_UUID);
		</#if>
	}
}