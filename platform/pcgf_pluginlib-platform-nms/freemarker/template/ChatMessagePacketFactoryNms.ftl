package at.pcgamingfreaks.Bukkit.Protocol;

import at.pcgamingfreaks.Bukkit.Util.IUtils;

import net.minecraft.server.v${nmsVersion}.ChatMessageType;
import net.minecraft.server.v${nmsVersion}.IChatBaseComponent;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutChat;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: http://freemarker.org
 * See template: ${.main_template_name}
 */
public class ChatMessagePacketFactory_${nmsVersion} implements IChatMessagePacketFactory
{
	@Override
	public Object makeChatPacket(final @NotNull String json, final @NotNull UUID sender)
	{
		<#if mcVersion < 100160000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.CHAT);
		<#else>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.CHAT, sender);
		</#if>
	}

	@Override
	public Object makeChatPacketSystem(final @NotNull String json)
	{
		<#if mcVersion < 100160000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.SYSTEM);
		<#else>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.SYSTEM, EMPTY_UUID);
		</#if>
	}

	@Override
	public Object makeChatPacketActionBar(final @NotNull String json)
	{
		<#if mcVersion < 100160000>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.GAME_INFO);
		<#else>
		return new PacketPlayOutChat((IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json), ChatMessageType.GAME_INFO, EMPTY_UUID);
		</#if>
	}
}