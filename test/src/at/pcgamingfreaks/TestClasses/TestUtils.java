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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.TestClasses;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

public class TestUtils
{
	private static Field modifiersField;
	private static Map<String, Object> fields;

	public static void initReflection() throws NoSuchFieldException
	{
		modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		fields = new TreeMap<>();
	}

	public static Field setAccessible(Class clazz, Object object, String name, Object value) throws NoSuchFieldException, IllegalAccessException
	{
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
		fields.put(name, field.get(object));
		field.set(object, value);
		return field;
	}

	@SuppressWarnings("SpellCheckingInspection")
	public static void setUnaccessible(Field field, Object object, boolean isFinal) throws IllegalAccessException
	{
		field.set(object, fields.get(field.getName()));
		fields.remove(field.getName());
		if (isFinal)
		{
			modifiersField.set(field, field.getModifiers() | Modifier.FINAL);
		}
		field.setAccessible(false);
	}
}