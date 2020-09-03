package at.pcgamingfreaks.Bukkit.Util;

import net.minecraft.server.v${nmsVersion}.EntityPlayer;
import net.minecraft.server.v${nmsVersion}.Packet;
import net.minecraft.server.v${nmsVersion}.IChatBaseComponent;

import org.bukkit.craftbukkit.v${nmsVersion}.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: http://freemarker.org
 * See template: ${.main_template_name}
 */
public final class Utils_${nmsVersion} extends Utils_Reflection
{
	static EntityPlayer getHandle(final @NotNull Player player)
	{
		return ((CraftPlayer) player).getHandle();
	}

	@Override
	public int getPing(final @NotNull Player player)
	{
		return getHandle(player).ping;
	}

	@Override
	public void sendPacket(final @NotNull Player player, final @NotNull Object packet)
	{
		getHandle(player).playerConnection.sendPacket((Packet<?>) packet);
	}

	@Override
	public Object jsonToIChatComponent(@NotNull String json)
	{
		return IChatBaseComponent.ChatSerializer.a(json);
	}
}