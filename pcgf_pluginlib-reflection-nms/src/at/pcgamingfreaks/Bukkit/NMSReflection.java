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

package at.pcgamingfreaks.Bukkit;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public class NMSReflection extends OBCReflection implements NmsReflector
{
	private static final String NMS_CLASS_PATH = "net.minecraft.server." + BUKKIT_VERSION + ".";

	static
	{
		if(Bukkit.getServer().getName().toLowerCase(Locale.ROOT).contains("cauldron") || Bukkit.getServer().getName().toLowerCase(Locale.ROOT).contains("uranium"))
		{
			throw new RuntimeException("Using Bukkit Reflections on Cauldron / Uranium based server!");
		}
	}

	/**
	 * Gets a net.minecraft.server class reference.
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
		return getNMSMethod(getNMSClass(className), name, args);
	}

	/**
	 * Gets a method reference from a net.minecraft.server class.
	 *
	 * @param clazz The class.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	@Contract("null, _, _ -> null")
	public static @Nullable Method getNMSMethod(@Nullable Class<?> clazz, @NotNull String name, @Nullable Class<?>... args)
	{
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
		return getNMSField(getNMSClass(className), name);
	}

	/**
	 * Gets a field reference from a net.minecraft.server class.
	 *
	 * @param clazz The class.
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	@Contract("null, _ -> null")
	public static @Nullable Field getNMSField(@Nullable Class<?> clazz, @NotNull String name)
	{
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

	@Override
	public @Nullable Class<?> getNmsClass(@NotNull String className)
	{
		return getNMSClass(className);
	}

	@Override
	public @Nullable Method getNmsMethod(@NotNull String className, @NotNull String name, @NotNull Class<?>... args)
	{
		return getNMSMethod(className, name, args);
	}

	@Override
	public @Nullable Method getNmsMethod(@Nullable Class<?> clazz, @NotNull String name, @NotNull Class<?>... args)
	{
		return getNMSMethod(clazz, name, args);
	}

	@Override
	public @Nullable Field getNmsField(@NotNull String className, @NotNull String name)
	{
		return getNMSField(className, name);
	}

	@Override
	public @Nullable Field getNmsField(@Nullable Class<?> clazz, @NotNull String name)
	{
		return getNMSField(clazz, name);
	}

	@Override
	public @Nullable Enum<?> getNmsEnum(@NotNull String enumClassAndEnumName)
	{
		return getNMSEnum(enumClassAndEnumName);
	}

	@Override
	public @Nullable Enum<?> getNmsEnum(@NotNull String enumClass, @NotNull String enumName)
	{
		return getNMSEnum(enumClass, enumName);
	}

	@Override
	public @Nullable Object getNmsHandle(@NotNull Object obj)
	{
		return getHandle(obj);
	}
}