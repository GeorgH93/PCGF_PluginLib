package at.pcgamingfreaks.Bukkit.Protocol;

import net.minecraft.server.v${nmsVersion}.ChatMessageType;

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
	public Object makeChatPackage(@NotNull String json)
	{
		return new ChatMessagePacket_${nmsVersion}(json);
	}

	@Override
	public Object makeChatPackage(@NotNull String json, @NotNull UUID sender)
	{
		<#if 100160000 <= mcVersion>
		return new ChatMessagePacket_${nmsVersion}(json, sender);
		<#else>
		return makeChatPackage(json);
		</#if>
	}

	@Override
	public Object makeChatPackageSystem(@NotNull String json)
	{
		return new ChatMessagePacket_${nmsVersion}(json, ChatMessageType.SYSTEM);
	}

	@Override
	public Object makeChatPackageActionBar(@NotNull String json)
	{
		return new ChatMessagePacket_${nmsVersion}(json, ChatMessageType.GAME_INFO);
	}
}