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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Reflection;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class OBCReflection extends Reflection
{
	protected static final String BUKKIT_VERSION = getBukkitVersion();
	private static final String OBC_CLASS_PATH = Bukkit.getServer().getClass().getPackage().getName() + ".";

	private static String getBukkitVersion()
	{
		try
		{
			return Bukkit.getServer().getClass().getName().split("\\.")[3];
		}
		catch(Throwable ignored) {}
		return "unknown";
	}

	public static String getVersion()
	{
		return BUKKIT_VERSION;
	}

	/**
	 * Gets an org.bukkit.craftbukkit class reference.
	 *
	 * @param className The name of the class.
	 * @return The class reference. Null if it was not found.
	 */
	public static @Nullable Class<?> getOBCClass(@NotNull String className)
	{
		return getClass(OBC_CLASS_PATH + className);
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
}