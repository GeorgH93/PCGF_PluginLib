package at.pcgamingfreaks.Bukkit.Util;

<#if 100130000 <= mcVersion>
import net.minecraft.server.v${nmsVersion}.MinecraftKey;
import net.minecraft.server.v${nmsVersion}.PacketDataSerializer;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutCustomPayload;
</#if>

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import io.netty.buffer.Unpooled;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: http://freemarker.org
 * See template: ${.main_template_name}
 */
public class PluginChannelUtils_${nmsVersion} extends PluginChannelUtils_Reflection
{
	@Override
	public void sendPluginMessageUnchecked(final @NotNull Plugin plugin, final @NotNull Player player, final @NotNull String channel, final @NotNull byte[] message)
	{
		<#if 100130000 <= mcVersion>
		PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.wrappedBuffer(message));
		PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(new MinecraftKey(channel), serializer);
		IUtils.INSTANCE.sendPacket(player, packet);
		<#else>
		player.sendPluginMessage(plugin, channel, message);
		</#if>
	}
}