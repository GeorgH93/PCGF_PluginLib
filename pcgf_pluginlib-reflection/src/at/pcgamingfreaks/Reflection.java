/*
 *   Copyright (C) 2023 GeorgH93
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

package at.pcgamingfreaks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Reflection
{
	/**
	 * Gets a class reference.
	 *
	 * @param classPath The path + name of the class.
	 * @return The class reference. Null if it was not found.
	 */
	public static @Nullable Class<?> getClass(final @NotNull String classPath)
	{
		try
		{
			return Class.forName(classPath);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a class reference. Does not print an exception if there is an exception.
	 *
	 * @param classPath The path + name of the class.
	 * @return The class reference. Null if it was not found.
	 */
	public static @Nullable Class<?> getClassSilent(final @NotNull String classPath)
	{
		try
		{
			return Class.forName(classPath);
		}
		catch(Exception ignored) {}
		return null;
	}

	/**
	 * Gets a class reference for a class contained within another class.
	 *
	 * @param clazz The class in which the search should take place.
	 * @param className The name of the class.
	 * @return The class reference. Null if it was not found.
	 */
	public static @Nullable Class<?> getInnerClass(final @NotNull Class<?> clazz, final @NotNull String className)
	{
		try
		{
			Class<?>[] classes = clazz.getClasses();
			for(Class<?> innerClass : classes)
			{
				if(innerClass.getSimpleName().equals(className)) return innerClass;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sets the value of a static field.
	 *
	 * @param clazz The class in which the field is located.
	 * @param field The name of field to be set.
	 * @param value The value to be set.
	 */
	public static void setStaticField(final @NotNull Class<?> clazz, final @NotNull String field, final @Nullable Object value)
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
	public static void setStaticField(final @NotNull Field field, final @Nullable Object value)
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
	public static void setValue(final @NotNull Field field, final @Nullable Object instance, final @Nullable Object value)
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
	public static void setValue(final @NotNull Object instance, final @NotNull String fieldName, final @Nullable Object value)
	{
		//noinspection ConstantConditions
		setValue(getField(instance.getClass(), fieldName), instance, value);
	}

	/**
	 * Gets an enum reference.
	 *
	 * @param enumFullName The full path to the enum.
	 * @return The enum reference. Null if it was not found.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static @Nullable Enum<?> getEnum(final @NotNull String enumFullName)
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
	@SuppressWarnings("rawtypes")
	public static @Nullable Enum<?> getEnum(final @NotNull Class clazz, final @NotNull String enumName)
	{
		try
		{
			//noinspection unchecked
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
	public static @Nullable Field getField(final @NotNull Class<?> clazz, final @NotNull String name)
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
	 * Gets a field reference from a class or one of its parent classes.
	 *
	 * @param clazz The class that should be searched.
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	public static @Nullable Field getFieldIncludeParents(final @NotNull Class<?> clazz, final @NotNull String name)
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
	 * Gets all the fields of a class including them of the super classes.
	 *
	 * @param clazz The class for which the fields should be retrieved.
	 * @return List of fields.
	 */
	public static @NotNull Collection<Field> getFieldsIncludeParents(@NotNull Class<?> clazz)
	{
		List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
		while((clazz = clazz.getSuperclass()) != null)
		{
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		}
		return fields;
	}

	/**
	 * Gets a method reference from a class.
	 *
	 * @param clazz The class containing the method.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	public static @Nullable Method getMethod(final @NotNull Class<?> clazz, final @NotNull String name, final @Nullable Class<?>... args)
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
	 * Gets a method reference from a class. Does not print an exception if there is an exception.
	 *
	 * @param clazz The class containing the method.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	public static @Nullable Method getMethodSilent(final @NotNull Class<?> clazz, final @NotNull String name, final @Nullable Class<?>... args)
	{
		Method method = null;
		try
		{
			method = clazz.getDeclaredMethod(name, args);
			method.setAccessible(true);
		}
		catch(Exception ignored) {}
		return method;
	}

	public static @Nullable Method getMethodFromReturnType(final @NotNull Class<?> clazz, final @NotNull Class<?> returnType, final @Nullable Class<?>... args)
	{
		try
		{
			for(Method method : clazz.getDeclaredMethods())
			{
				if (method.getReturnType().equals(returnType) && (args.length == 0 || Arrays.equals(method.getParameterTypes(), args)))
				{
					return method;
				}
			}
			throw new NoSuchMethodException(clazz.getName() + " does not contain a method that returns '" + returnType.getName() + "' and accepts '" + Arrays.toString(args) + "' as parameters.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a method reference from a class or one of its parent classes.
	 *
	 * @param clazz The class that should be searched for the method.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	public static @Nullable Method getMethodIncludeParents(final @NotNull Class<?> clazz, final @NotNull String name, final @Nullable Class<?>... args)
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
	public static @Nullable Constructor<?> getConstructor(final @NotNull Class<?> clazz, final @Nullable Class<?>... args)
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
	public static boolean classListEqual(final @NotNull Class<?>[] l1, final @NotNull Class<?>[] l2)
	{
		if(l1.length != l2.length) return false;
		for(int i = 0; i < l1.length; i++)
		{
			if(l1[i] != l2[i]) return false;
		}
		return true;
	}

	/**
	 * Sets the value of a final field!
	 *
	 * @param field The final Field that should be set.
	 * @param value The value that should be set.
	 * @deprecated Does not work on Java 16 and up
	 */
	@Deprecated
	public static void setFinalField(final @NotNull Field field, final @Nullable Object instance, final @Nullable Object value) throws NoSuchFieldException, IllegalAccessException
	{
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(instance, value);
		modifiers.set(field, field.getModifiers() | Modifier.FINAL);
		field.setAccessible(accessible);
		modifiers.setAccessible(false);
	}
}