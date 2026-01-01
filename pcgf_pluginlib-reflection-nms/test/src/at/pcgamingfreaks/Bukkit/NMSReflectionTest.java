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

import at.pcgamingfreaks.TestClasses.FakeEntityPlayer;
import at.pcgamingfreaks.TestClasses.FakePlayer;
import at.pcgamingfreaks.TestClasses.FakeTestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestEnum;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import sun.misc.Unsafe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NMSReflectionTest
{
	private static boolean skipTests = false;
	
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		skipTests = System.getProperty("java.specification.version").compareTo("16") >= 0;
		if (skipTests) return;
		
		Field serverField = Bukkit.class.getDeclaredField("server");
		serverField.setAccessible(true);
		Server mockedServer = mock(Server.class);
		when(mockedServer.getName()).thenReturn("mockedServer");
		serverField.set(null, mockedServer);
		serverField.setAccessible(false);
		
		Field nmsClassPathField = NMSReflection.class.getDeclaredField("NMS_CLASS_PATH");
		nmsClassPathField.setAccessible(true);
		
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(nmsClassPathField, nmsClassPathField.getModifiers() & ~Modifier.FINAL);
		nmsClassPathField.set(null, "at.pcgamingfreaks.TestClasses.");
		modifiersField.setAccessible(false);
		nmsClassPathField.setAccessible(false);
		
		new NMSReflection();
	}

	@Test
	public void testGetVersion()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		assertEquals("The version should match", "unknown", NMSReflection.getVersion());
	}

	@Test
	public void testGetClass()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		assertEquals("The NMS class should be correct", FakeTestBukkitServer.class, NMSReflection.getNMSClass("FakeTestBukkitServer"));
		assertNull("The NMS class should not be found", NMSReflection.getNMSClass(""));
	}

	@Test
	public void testGetMethod() throws NoSuchMethodException
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		assertEquals("The version method of the server should be found", FakeTestBukkitServer.class.getDeclaredMethod("getVersion"), NMSReflection.getNMSMethod("FakeTestBukkitServer", "getVersion"));
		assertNull("The version method of the server should not be found in an invalid class", NMSReflection.getNMSMethod("", "getVersion"));
	}

	@Test
	public void testGetField() throws NoSuchFieldException
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		assertEquals("The server field should be found", FakeTestBukkitServer.class.getDeclaredField("serverField"), NMSReflection.getNMSField("FakeTestBukkitServer", "serverField"));
		assertNull("The server field should not be found in an invalid class", NMSReflection.getNMSField("", "serverField"));
	}

	@Test
	public void testGetEnum()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		assertEquals("The enum should be found", TestEnum.Value1, NMSReflection.getNMSEnum("TestEnum.Value1"));
		assertEquals("The enum should be found", TestEnum.Value2, NMSReflection.getNMSEnum("TestEnum", "Value2"));
	}

	@Test
	public void testGetHandle()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		assertEquals("The handle should be get correctly", FakeEntityPlayer.class, Objects.requireNonNull(NMSReflection.getHandle(new FakePlayer())).getClass());
		assertNull("The handle should not be found", NMSReflection.getHandle(this));
	}
}