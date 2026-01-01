/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.TestClasses;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class TestUtils
{
	private static final Unsafe UNSAFE;
	private static Map<String, Object> fields;
	private static final boolean STATIC_MOCKING_AVAILABLE = checkStaticMockingAvailable();

	static
	{
		try
		{
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			UNSAFE = (Unsafe) f.get(null);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to get Unsafe instance", e);
		}
	}

	private static boolean checkStaticMockingAvailable()
	{
		try
		{
			// Check if mockito-inline is on the classpath by looking for its MockMaker configuration
			return TestUtils.class.getClassLoader()
				.getResource("mockito-extensions/org.mockito.plugins.MockMaker") != null;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static boolean isStaticMockingAvailable()
	{
		return STATIC_MOCKING_AVAILABLE;
	}

	public static boolean canMockJdkClasses()
	{
		return STATIC_MOCKING_AVAILABLE && System.getProperty("java.specification.version").compareTo("16") < 0;
	}

	public static void initReflection()
	{
		fields = new TreeMap<>();
	}

	public static Field setAccessible(Class clazz, Object object, String name, Object value) throws NoSuchFieldException, IllegalAccessException
	{
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		fields.put(name, field.get(object));
		setFieldValue(object, field, value);
		return field;
	}

	@SuppressWarnings("SpellCheckingInspection")
	public static void setUnaccessible(Field field, Object object, boolean isFinal) throws IllegalAccessException
	{
		setFieldValue(object, field, fields.get(field.getName()));
		fields.remove(field.getName());
		field.setAccessible(false);
	}

	public static void setFieldValue(Object target, Field field, Object value)
	{
		if (target == null)
		{
			Object base = UNSAFE.staticFieldBase(field);
			long offset = UNSAFE.staticFieldOffset(field);
			UNSAFE.putObject(base, offset, value);
		}
		else
		{
			long offset = UNSAFE.objectFieldOffset(field);
			UNSAFE.putObject(target, offset, value);
		}
	}
}