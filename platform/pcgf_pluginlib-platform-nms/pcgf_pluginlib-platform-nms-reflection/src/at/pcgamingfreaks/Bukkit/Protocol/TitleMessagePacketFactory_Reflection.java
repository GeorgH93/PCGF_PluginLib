/*
 *   Copyright (C) 2024 GeorgH93
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

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Bukkit.Util.IUtils;
import at.pcgamingfreaks.Reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class TitleMessagePacketFactory_Reflection implements ITitleMessagePacketFactory
{
	//region Reflection stuff
	private static final Class<?> I_CHAT_BASE_COMPONENT = NmsReflector.INSTANCE.getNmsClass("IChatBaseComponent");
	private static final Enum<?> ENUM_TITLE = MCVersion.isOlderThan(MCVersion.MC_1_17) ? NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.TITLE") : null;
	private static final Enum<?> ENUM_SUBTITLE = MCVersion.isOlderThan(MCVersion.MC_1_17) ? NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.SUBTITLE") : null;
	private static final Enum<?> ENUM_TIME = MCVersion.isOlderThan(MCVersion.MC_1_17) ? NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.TIMES") : null;
	private static final Enum<?> ENUM_ACTION_BAR = (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_11) && MCVersion.isOlderThan(MCVersion.MC_1_17)) ? NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.ACTIONBAR") : null;
	private static final Class<?> PACKET_PLAY_OUT_TITLE = MCVersion.isOlderThan(MCVersion.MC_1_17) ? NmsReflector.INSTANCE.getNmsClass("PacketPlayOutTitle") : null;
	private static final Constructor<?> PACKET_PLAY_OUT_TITLE_CONSTRUCTOR = MCVersion.isOlderThan(MCVersion.MC_1_17) ? Reflection.getConstructor(Objects.requireNonNull(PACKET_PLAY_OUT_TITLE), NmsReflector.INSTANCE.getNmsClass("PacketPlayOutTitle$EnumTitleAction"), I_CHAT_BASE_COMPONENT, int.class, int.class, int.class) : null;

	private static final Constructor<?> PACKET_TITLE_CONSTRUCTOR = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_17) ? NmsReflector.INSTANCE.getNmsConstructor("ClientboundSetTitleTextPacket", Objects.requireNonNull(I_CHAT_BASE_COMPONENT)) : null;
	private static final Constructor<?> PACKET_SUB_TITLE_CONSTRUCTOR = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_17) ? NmsReflector.INSTANCE.getNmsConstructor("ClientboundSetSubtitleTextPacket", Objects.requireNonNull(I_CHAT_BASE_COMPONENT)) : null;
	private static final Constructor<?> PACKET_ACTIONBAR_CONSTRUCTOR = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_17) ? NmsReflector.INSTANCE.getNmsConstructor("ClientboundSetActionBarTextPacket", Objects.requireNonNull(I_CHAT_BASE_COMPONENT)) : null;
	private static final Constructor<?> PACKET_TIMING_CONSTRUCTOR = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_17) ? NmsReflector.INSTANCE.getNmsConstructor("ClientboundSetTitlesAnimationPacket", int.class, int.class, int.class) : null;
	//endregion

	@Override
	public Object makeTitlePacket(@NotNull String json)
	{
		try
		{
			Object component = IUtils.INSTANCE.jsonToIChatComponent(json);
			if(PACKET_PLAY_OUT_TITLE_CONSTRUCTOR != null)
				return PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(ENUM_TITLE, component, -1, -1, -1);
			else if(PACKET_TITLE_CONSTRUCTOR != null)
				return PACKET_TITLE_CONSTRUCTOR.newInstance(component);
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object makeSubTitlePacket(@NotNull String json)
	{
		try
		{
			Object component = IUtils.INSTANCE.jsonToIChatComponent(json);
			if(PACKET_PLAY_OUT_TITLE_CONSTRUCTOR != null)
				return PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(ENUM_SUBTITLE, component, -1, -1, -1);
			else if(PACKET_SUB_TITLE_CONSTRUCTOR != null)
				return PACKET_SUB_TITLE_CONSTRUCTOR.newInstance(component);
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object makeTitlePacketTime(int fadeIn, int stay, int fadeOut)
	{
		try
		{
			if(PACKET_PLAY_OUT_TITLE_CONSTRUCTOR != null)
				return PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(ENUM_TIME, null, fadeIn, stay, fadeOut);
			else if(PACKET_TIMING_CONSTRUCTOR != null)
				return PACKET_TIMING_CONSTRUCTOR.newInstance(fadeIn, stay, fadeOut);
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object makeTitlePacketActionBar(@NotNull String json)
	{
		if(ENUM_ACTION_BAR == null && PACKET_ACTIONBAR_CONSTRUCTOR == null) return IChatMessagePacketFactory.INSTANCE.makeChatPacketActionBar(json);
		try
		{
			Object component = IUtils.INSTANCE.jsonToIChatComponent(json);
			if(PACKET_PLAY_OUT_TITLE_CONSTRUCTOR != null)
				return PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(ENUM_ACTION_BAR, component, -1, -1, -1);
			else if(PACKET_ACTIONBAR_CONSTRUCTOR != null)
				return PACKET_ACTIONBAR_CONSTRUCTOR.newInstance(component);
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}