package at.pcgamingfreaks.Bukkit.Protocol;

import net.minecraft.server.v${nmsVersion}.ChatMessageType;
import net.minecraft.server.v${nmsVersion}.PacketDataSerializer;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutChat;

import org.jetbrains.annotations.NotNull;

<#if 100160000 <= mcVersion>
import java.util.UUID;
</#if>

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: http://freemarker.org
 * See template: ${.main_template_name}
 */
public class ChatMessagePacket_${nmsVersion} extends PacketPlayOutChat
{
	<#if 100160000 <= mcVersion>
	private static final UUID EMPTY_UUID = new UUID(0L, 0L);
	</#if>

	private final String json;
	private final ChatMessageType type;
	<#if 100160000 <= mcVersion>
	private final UUID sender;
	</#if>

	public ChatMessagePacket_${nmsVersion}(final @NotNull String json)
	{
		<#if 100160000 <= mcVersion>
		this(json, EMPTY_UUID);
		<#else>
		this(json, ChatMessageType.CHAT);
		</#if>
	}

	public ChatMessagePacket_${nmsVersion}(final @NotNull String json, final @NotNull ChatMessageType type)
	{
		<#if 100160000 <= mcVersion>
		super(null, type, EMPTY_UUID);
		this.sender = EMPTY_UUID;
		<#else>
		super(null, type);
		</#if>
		this.json = json;
		this.type = type;
	}


	<#if 100160000 <= mcVersion>
	public ChatMessagePacket_${nmsVersion}(final @NotNull String json, final @NotNull UUID sender)
	{
		super(null, ChatMessageType.CHAT, sender);
		this.json = json;
		type = ChatMessageType.CHAT;
		this.sender = sender;
	}
	</#if>

	@Override
	public void b(PacketDataSerializer packetdataserializer)
	{
		packetdataserializer.a(json);
		packetdataserializer.writeByte(type.a());
		<#if 100160000 <= mcVersion>
		packetdataserializer.a(sender);
		</#if>
	}
}