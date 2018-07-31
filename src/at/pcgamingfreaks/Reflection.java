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

package at.pcgamingfreaks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection
{
	/**
	 * Sets the value of a static field.
	 *
	 * @param clazz The class in which the field is located.
	 * @param field The name of field to be set.
	 * @param value The value to be set.
	 */
	public static void setStaticField(@NotNull Class clazz, @NotNull String field, @Nullable Object value)
	{
		//noinspection ConstantConditions
		setStaticField(getField(clazz, field), value);
	}

	/**
	 * Sets the value of a static field.
	 *
	 * @param field The field to be set.
	 * @param value The value to be set.
	 */
	public static void setStaticField(@NotNull Field field, @Nullable Object value)
	{
		setValue(field, (Object) null, value);
	}

	/**
	 * Sets the value of a field.
	 *
	 * @param field The field to be set.
	 * @param instance The object instance to be edited. Null for static field.
	 * @param value The value to be set.
	 */
	public static void setValue(@NotNull Field field, @Nullable Object instance, @Nullable Object value)
	{
		try
		{
			field.setAccessible(true);
			field.set(instance, value);
			field.setAccessible(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sets the value of a field.
	 *
	 * @param instance The object instance to be edited.
	 * @param fieldName The name of field to be set.
	 * @param value The value to be set.
	 */
	public static void setValue(@NotNull Object instance, @NotNull String fieldName, @Nullable Object value) throws Exception
	{
		setValue(instance.getClass().getDeclaredField(fieldName), instance, value);
	}

	/**
	 * Gets an enum reference.
	 *
	 * @param enumFullName The full path to the enum.
	 * @return The enum reference. Null if it was not found.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static @Nullable Enum<?> getEnum(@NotNull String enumFullName)
	{
		String[] x = enumFullName.split("\\.(?=[^.]+$)");
		if(x.length == 2)
		{
			try
			{
				return Enum.valueOf((Class<Enum>) Class.forName(x[0]), x[1]);
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Gets an enum reference.
	 *
	 * @param clazz The enum class.
	 * @param enumName The name of the enum.
	 * @return The enum reference. Null if it was not found.
	 */
	public static @Nullable Enum<?> getEnum(@NotNull Class clazz, @NotNull String enumName)
	{
		try
		{
			return Enum.valueOf(clazz, enumName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a field reference from a class.
	 *
	 * @param clazz The class containing the field.
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	public static @Nullable Field getField(@NotNull Class<?> clazz, @NotNull String name)
	{
		try
		{
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a field reference from a class or one of it's parent classes.
	 *
	 * @param clazz The class that should be searched.
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	public static @Nullable Field getFieldIncludeParents(@NotNull Class<?> clazz, @NotNull String name)
	{
		try
		{
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		}
		catch(NoSuchFieldException ignored)
		{
			if(clazz.getSuperclass() != null)
			{
				return getFieldIncludeParents(clazz.getSuperclass(), name);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a method reference from a class.
	 *
	 * @param clazz The class containing the method.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	public static @Nullable Method getMethod(@NotNull Class<?> clazz, @NotNull String name, @Nullable Class<?>... args)
	{
		Method method = null;
		try
		{
			method = clazz.getDeclaredMethod(name, args);
			method.setAccessible(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return method;
	}

	/**
	 * Gets a method reference from a class or one of it's parent classes.
	 *
	 * @param clazz The class that should be searched for the method.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	public static @Nullable Method getMethodIncludeParents(@NotNull Class<?> clazz, @NotNull String name, @Nullable Class<?>... args)
	{
		Method method = null;
		try
		{
			method = clazz.getDeclaredMethod(name, args);
			method.setAccessible(true);
		}
		catch(NoSuchMethodException ignored)
		{
			if(clazz.getSuperclass() != null)
			{
				return getMethodIncludeParents(clazz.getSuperclass(), name);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return method;
	}

	/**
	 * Gets the constructor reference of a class.
	 *
	 * @param clazz The class containing the constructor.
	 * @param args The types of the parameters of the constructor.
	 * @return The constructor reference. Null if it was not found.
	 */
	public static @Nullable Constructor<?> getConstructor(@NotNull Class<?> clazz, @Nullable Class<?>... args)
	{
		try
		{
			return clazz.getConstructor(args);
		}
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Compares if two class lists are equal.
	 *
	 * @param l1 The first array containing classes.
	 * @param l2 The second array containing classes.
	 * @return True if they are the same. False if they are not.
	 */
	public static boolean classListEqual(@NotNull Class<?>[] l1, @NotNull Class<?>[] l2)
	{
		if(l1.length != l2.length) return false;
		for(int i = 0; i < l1.length; i++)
		{
			if(l1[i] != l2[i]) return false;
		}
		return true;
	}
}