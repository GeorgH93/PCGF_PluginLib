/*
 *   Copyright (C) 2014-2016 GeorgH93
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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Reflection;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSReflection extends Reflection
{
	private static final String BUKKIT_VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
	private static final String NMS_CLASS_PATH = "net.minecraft.server." + BUKKIT_VERSION + ".";
	private static final String OBC_CLASS_PATH = "org.bukkit.craftbukkit." + BUKKIT_VERSION + ".";

	public static String getVersion()
	{
		return BUKKIT_VERSION;
	}

	public static @Nullable Class<?> getNMSClass(@NotNull String className)
	{
		try
		{
			return Class.forName(NMS_CLASS_PATH + className);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static @Nullable Method getNMSMethod(@NotNull String className, @NotNull String name, @Nullable Class<?>... args)
	{
		Class<?> clazz = getOBCClass(className);
		return (clazz == null) ? null : getMethod(getNMSClass(className), name, args);
	}

	public static @Nullable Field getNMSField(@NotNull String className, @NotNull String name)
	{
		Class<?> clazz = getOBCClass(className);
		return (clazz == null) ? null : getField(getNMSClass(className), name);
	}

	public static @Nullable Enum<?> getNMSEnum(@NotNull String enumClassAndEnumName)
	{
		return getEnum(NMS_CLASS_PATH + enumClassAndEnumName);
	}

	public static @Nullable Enum<?> getNMSEnum(@NotNull String enumClass, @NotNull String enumName)
	{
		return getEnum(NMS_CLASS_PATH + enumClass + "." + enumName);
	}

	public static @Nullable Class<?> getOBCClass(@NotNull String className)
	{
		try
		{
			return Class.forName(OBC_CLASS_PATH + className);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static @Nullable Method getOBCMethod(@NotNull String className, @NotNull String name, @Nullable Class<?>... args)
	{
		Class<?> clazz = getOBCClass(className);
		return (clazz == null) ? null : getMethod(clazz, name, args);
	}

	public static @Nullable Field getOBCField(@NotNull String className, @NotNull String name)
	{
		Class<?> clazz = getOBCClass(className);
		return (clazz == null) ? null : getField(getOBCClass(className), name);
	}

	public static @Nullable Object getHandle(@NotNull Object obj)
	{
		try
		{
			//noinspection ConstantConditions
			return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}