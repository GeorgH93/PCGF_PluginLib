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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.TestClasses.FakeTestBukkitServer;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OBCReflectionTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		Field obcClassPathField = OBCReflection.class.getDeclaredField("OBC_CLASS_PATH");
		obcClassPathField.setAccessible(true);
		modifiersField.setInt(obcClassPathField, obcClassPathField.getModifiers() & ~Modifier.FINAL);
		obcClassPathField.set(null, "at.pcgamingfreaks.TestClasses.");
		obcClassPathField.setAccessible(false);
		modifiersField.setAccessible(false);
		new OBCReflection();
	}

	@Test
	public void testGetVersion()
	{
		assertEquals("The version should match", "unknown", OBCReflection.getVersion());
	}

	@Test
	public void testGetClass()
	{
		assertEquals("The OBC class should be correct", FakeTestBukkitServer.class, OBCReflection.getOBCClass("FakeTestBukkitServer"));
		assertNull("The OBC class should not be found", OBCReflection.getOBCClass(""));
	}

	@Test
	public void testGetMethod() throws NoSuchMethodException
	{
		assertEquals("The version method of the server should be found", FakeTestBukkitServer.class.getDeclaredMethod("getVersion"), OBCReflection.getOBCMethod("FakeTestBukkitServer", "getVersion"));
		assertNull("The version method of the server should not be found in an invalid class", OBCReflection.getOBCMethod("", "getVersion"));
	}

	@Test
	public void testGetField() throws NoSuchFieldException
	{
		assertEquals("The server field should be found", FakeTestBukkitServer.class.getDeclaredField("serverField"), OBCReflection.getOBCField("FakeTestBukkitServer", "serverField"));
		assertNull("The server field should not be found in an invalid class", OBCReflection.getOBCField("", "serverField"));
	}
}