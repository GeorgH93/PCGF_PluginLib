/*
 *   Copyright (C) 2020 GeorgH93
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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
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
	private static final Enum<?> ENUM_TITLE = NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.TITLE");
	private static final Enum<?> ENUM_SUBTITLE = NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.SUBTITLE");
	private static final Enum<?> ENUM_ACTION_BAR = (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_11)) ? NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.ACTIONBAR") : ENUM_SUBTITLE;
	private static final Class<?> PACKET_PLAY_OUT_TITLE = NmsReflector.INSTANCE.getNmsClass("PacketPlayOutTitle");
	private static final Constructor<?> PACKET_PLAY_OUT_TITLE_CONSTRUCTOR = Reflection.getConstructor(Objects.requireNonNull(PACKET_PLAY_OUT_TITLE), NmsReflector.INSTANCE.getNmsClass("PacketPlayOutTitle$EnumTitleAction"), I_CHAT_BASE_COMPONENT, int.class, int.class, int.class);
	private static final Enum<?> ENUM_TIME = NmsReflector.INSTANCE.getNmsEnum("PacketPlayOutTitle$EnumTitleAction.TIMES");
	//endregion

	@Override
	public Object makeTitlePacket(@NotNull String json)
	{
		try
		{
			return PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(ENUM_TITLE, IUtils.INSTANCE.jsonToIChatComponent(json), -1, -1, -1);
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
			return PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(ENUM_SUBTITLE, IUtils.INSTANCE.jsonToIChatComponent(json), -1, -1, -1);
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
			return PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(ENUM_TIME, null, fadeIn, stay, fadeOut);
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
		if(ENUM_ACTION_BAR == null) return IChatMessagePacketFactory.INSTANCE.makeChatPacketActionBar(json);
		try
		{
			return PACKET_PLAY_OUT_TITLE_CONSTRUCTOR.newInstance(ENUM_ACTION_BAR, IUtils.INSTANCE.jsonToIChatComponent(json), -1, -1, -1);
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}