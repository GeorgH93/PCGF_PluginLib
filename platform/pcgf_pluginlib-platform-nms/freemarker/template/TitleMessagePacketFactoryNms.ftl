package at.pcgamingfreaks.Bukkit.Protocol;

import at.pcgamingfreaks.Bukkit.Util.IUtils;

import net.minecraft.server.v${nmsVersion}.IChatBaseComponent;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutTitle;

import org.jetbrains.annotations.NotNull;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: http://freemarker.org
 * See template: ${.main_template_name}
 */
public final class TitleMessagePacketFactory_${nmsVersion} implements ITitleMessagePacketFactory
{
	@Override
	public Object makeTitlePacket(final @NotNull String json)
	{
		return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, (IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json));
	}

	@Override
	public Object makeSubTitlePacket(final @NotNull String json)
	{
		return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, (IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json));
	}

	@Override
	public Object makeTitlePacketTime(final int fadeIn, final int stay, final int fadeOut)
	{
		return new PacketPlayOutTitle(fadeIn, stay, fadeOut);
	}

	@Override
	public Object makeTitlePacketActionBar(final @NotNull String json)
	{
		<#if mcVersion < 100110000>
		return IChatMessagePacketFactory.INSTANCE.makeChatPacketActionBar(json);
		<#else>
		return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, (IChatBaseComponent) IUtils.INSTANCE.jsonToIChatComponent(json));
		</#if>
	}
}