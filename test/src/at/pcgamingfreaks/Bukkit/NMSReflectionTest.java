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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.TestClasses.NMS.EntityPlayer;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestEnum;

import org.bukkit.Bukkit;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NMSReflectionTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		Field serverField = Bukkit.class.getDeclaredField("server");
		serverField.setAccessible(true);
		serverField.set(null, new TestBukkitServer());
		serverField.setAccessible(false);
		Field nmsClassPathField = NMSReflection.class.getDeclaredField("NMS_CLASS_PATH");
		nmsClassPathField.setAccessible(true);
		modifiersField.setInt(nmsClassPathField, nmsClassPathField.getModifiers() & ~Modifier.FINAL);
		//noinspection SpellCheckingInspection
		nmsClassPathField.set(null, "at.pcgamingfreaks.TestClasses.");
		nmsClassPathField.setAccessible(false);
		Field obcClassPathField = NMSReflection.class.getDeclaredField("OBC_CLASS_PATH");
		obcClassPathField.setAccessible(true);
		modifiersField.setInt(obcClassPathField, obcClassPathField.getModifiers() & ~Modifier.FINAL);
		//noinspection SpellCheckingInspection
		obcClassPathField.set(null, "at.pcgamingfreaks.TestClasses.");
		obcClassPathField.setAccessible(false);
		modifiersField.setAccessible(false);
		new NMSReflection();
	}

	@Test
	public void testGetVersion()
	{
		//noinspection SpellCheckingInspection
		assertEquals("The version should match", "TestBukkitServer", NMSReflection.getVersion());
	}

	@Test
	public void testGetClass()
	{
		//noinspection SpellCheckingInspection
		assertEquals("The NMS class should be correct", TestBukkitServer.class, NMSReflection.getNMSClass("TestBukkitServer"));
		assertNull("The NMS class should not be found", NMSReflection.getNMSClass(""));
		//noinspection SpellCheckingInspection
		assertEquals("The OBC class should be correct", TestBukkitServer.class, NMSReflection.getOBCClass("TestBukkitServer"));
		assertNull("The OBC class should not be found", NMSReflection.getOBCClass(""));
	}

	@Test
	public void testGetMethod() throws NoSuchMethodException
	{
		//noinspection SpellCheckingInspection
		assertEquals("The version method of the server should be found", TestBukkitServer.class.getDeclaredMethod("getVersion"), NMSReflection.getNMSMethod("TestBukkitServer", "getVersion"));
		assertNull("The version method of the server should not be found in an invalid class", NMSReflection.getNMSMethod("", "getVersion"));
		//noinspection SpellCheckingInspection
		assertEquals("The version method of the server should be found", TestBukkitServer.class.getDeclaredMethod("getVersion"), NMSReflection.getOBCMethod("TestBukkitServer", "getVersion"));
		assertNull("The version method of the server should not be found in an invalid class", NMSReflection.getOBCMethod("", "getVersion"));
	}

	@Test
	public void testGetField() throws NoSuchFieldException
	{
		//noinspection SpellCheckingInspection
		assertEquals("The server field should be found", TestBukkitServer.class.getDeclaredField("serverField"), NMSReflection.getNMSField("TestBukkitServer", "serverField"));
		assertNull("The server field should not be found in an invalid class", NMSReflection.getNMSField("", "serverField"));
		//noinspection SpellCheckingInspection
		assertEquals("The server field should be found", TestBukkitServer.class.getDeclaredField("serverField"), NMSReflection.getOBCField("TestBukkitServer", "serverField"));
		assertNull("The server field should not be found in an invalid class", NMSReflection.getOBCField("", "serverField"));
	}

	@Test
	public void testGetEnum()
	{
		//noinspection SpellCheckingInspection
		assertEquals("The enum should be found", TestEnum.Value1, NMSReflection.getNMSEnum("TestEnum.Value1"));
		//noinspection SpellCheckingInspection
		assertEquals("The enum should be found", TestEnum.Value2, NMSReflection.getNMSEnum("TestEnum", "Value2"));
	}

	@Test
	public void testGetHandle()
	{
		//noinspection deprecation,ConstantConditions
		assertEquals("The handle should be get correctly", EntityPlayer.class, NMSReflection.getHandle(Bukkit.getPlayer("")).getClass());
		assertNull("The handle should not be found", NMSReflection.getHandle(Bukkit.getServer()));
	}
}