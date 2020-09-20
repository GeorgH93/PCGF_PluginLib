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

import at.pcgamingfreaks.Reflection;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public interface NmsReflector
{
	static boolean isCauldron()
	{
		return Bukkit.getServer().getName().toLowerCase(Locale.ROOT).contains("cauldron") || Bukkit.getServer().getName().toLowerCase(Locale.ROOT).contains("uranium");
	}

	NmsReflector INSTANCE = isCauldron() ? new NMSReflectionCauldron() : new NMSReflection();

	/**
	 * Gets a net.minecraft.server class reference.
	 *
	 * @param className The name of the class.
	 * @return The class reference. Null if it was not found.
	 */
	@Nullable Class<?> getNmsClass(@NotNull String className);

	/**
	 * Gets a method reference from a net.minecraft.server class.
	 *
	 * @param className The name of the class.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	@Nullable Method getNmsMethod(@NotNull String className, @NotNull String name, @NotNull Class<?>... args);

	/**
	 * Gets a method reference from a net.minecraft.server class.
	 *
	 * @param clazz The class. Must be a net.minecraft.server class!!!
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	@Contract("null, _, _ -> null")
	@Nullable Method getNmsMethod(@Nullable Class<?> clazz, @NotNull String name, @NotNull Class<?>... args);

	/**
	 * Gets a field reference from a net.minecraft.server class.
	 *
	 * @param className The name of the class.
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	@Nullable Field getNmsField(@NotNull String className, @NotNull String name);

	/**
	 * Gets a field reference from a net.minecraft.server class.
	 *
	 * @param clazz The class. Must be a net.minecraft.server class!!!
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	@Contract("null, _ -> null")
	@Nullable Field getNmsField(@Nullable Class<?> clazz, @NotNull String name);

	@Nullable Enum<?> getNmsEnum(@NotNull String enumClassAndEnumName);

	@Nullable Enum<?> getNmsEnum(@NotNull String enumClass, @NotNull String enumName);

	@Nullable Object getNmsHandle(@NotNull Object obj);

	static @Nullable Object getHandle(@NotNull Object obj)
	{
		try
		{
			//noinspection ConstantConditions
			return Reflection.getMethod(obj.getClass(), "getHandle").invoke(obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}