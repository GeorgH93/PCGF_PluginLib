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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection
{
	public static void setStaticField(@NotNull Class clazz, @NonNls String field, @Nullable Object value)
	{
		//noinspection ConstantConditions
		setStaticField(getField(clazz, field), value);
	}

	public static void setStaticField(@NotNull Field field, @Nullable Object value)
	{
		setValue(field, (Object) null, value);
	}

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

	public static void setValue(@NotNull Object instance, @NonNls String fieldName, @Nullable Object value) throws Exception
	{
		setValue(instance.getClass().getDeclaredField(fieldName), instance, value);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static @Nullable Enum<?> getEnum(@NonNls String enumFullName)
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

	public static @Nullable Enum<?> getEnum(@NotNull Class clazz, @NonNls String enumName)
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

	public static @Nullable Field getField(@NotNull Class<?> clazz, @NonNls String name)
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

	public static @Nullable Field getFieldIncludeParents(@NotNull Class<?> clazz, @NonNls String name)
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

	public static @Nullable Method getMethod(@NotNull Class<?> clazz, @NonNls String name, @Nullable Class<?>... args)
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

	public static @Nullable Method getMethodIncludeParents(@NotNull Class<?> clazz, @NonNls String name, @Nullable Class<?>... args)
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