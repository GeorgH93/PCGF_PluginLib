/*
 *   Copyright (C) 2016, 2018 GeorgH93
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

	/**
	 * Gets an net.minecraft.server class reference.
	 *
	 * @param className The name of the class.
	 * @return The class reference. Null if it was not found.
	 */
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

	/**
	 * Gets a method reference from a net.minecraft.server class.
	 *
	 * @param className The name of the class.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	public static @Nullable Method getNMSMethod(@NotNull String className, @NotNull String name, @Nullable Class<?>... args)
	{
		Class<?> clazz = getNMSClass(className);
		return (clazz == null) ? null : getMethod(clazz, name, args);
	}

	/**
	 * Gets a field reference from a net.minecraft.server class.
	 *
	 * @param className The name of the class.
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	public static @Nullable Field getNMSField(@NotNull String className, @NotNull String name)
	{
		Class<?> clazz = getNMSClass(className);
		return (clazz == null) ? null : getField(clazz, name);
	}

	public static @Nullable Enum<?> getNMSEnum(@NotNull String enumClassAndEnumName)
	{
		return getEnum(NMS_CLASS_PATH + enumClassAndEnumName);
	}

	public static @Nullable Enum<?> getNMSEnum(@NotNull String enumClass, @NotNull String enumName)
	{
		return getEnum(NMS_CLASS_PATH + enumClass + "." + enumName);
	}

	/**
	 * Gets an org.bukkit.craftbukkit class reference.
	 *
	 * @param className The name of the class.
	 * @return The class reference. Null if it was not found.
	 */
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

	/**
	 * Gets a method reference from an org.bukkit.craftbukkit class.
	 *
	 * @param className The name of the class.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	public static @Nullable Method getOBCMethod(@NotNull String className, @NotNull String name, @Nullable Class<?>... args)
	{
		Class<?> clazz = getOBCClass(className);
		return (clazz == null) ? null : getMethod(clazz, name, args);
	}

	/**
	 * Gets a field reference from an org.bukkit.craftbukkit class.
	 *
	 * @param className The name of the class.
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	public static @Nullable Field getOBCField(@NotNull String className, @NotNull String name)
	{
		Class<?> clazz = getOBCClass(className);
		return (clazz == null) ? null : getField(clazz, name);
	}

	public static @Nullable Object getHandle(@NotNull Object obj)
	{
		try
		{
			//noinspection ConstantConditions
			return getMethod(obj.getClass(), "getHandle").invoke(obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}