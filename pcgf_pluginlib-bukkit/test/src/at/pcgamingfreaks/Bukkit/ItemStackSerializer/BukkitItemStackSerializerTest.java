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

package at.pcgamingfreaks.Bukkit.ItemStackSerializer;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BukkitItemStackSerializerTest
{
	private static boolean skipTests = false;
	
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		skipTests = !TestUtils.canMockJdkClasses();
		if (!skipTests)
		{
			Bukkit.setServer(new TestBukkitServer());
			TestObjects.initNMSReflection();
			TestUtils.initReflection();
		}
	}

	@Test
	public void testDeserialize() throws Exception
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		BukkitItemStackSerializer deserializer = new BukkitItemStackSerializer();
		assertNull("Deserialized data should be null", deserializer.deserialize(null));
		assertNull("Deserialized data should be null when an error occurs", deserializer.deserialize(new byte[] { 2, 5, 6 }));
		try (MockedConstruction<BukkitObjectInputStream> mockedInputStream = Mockito.mockConstruction(BukkitObjectInputStream.class,
				(mock, context) -> when(mock.readObject()).thenReturn(new ItemStack[] {})))
		{
			assertNotNull("Deserialized data should not be null", deserializer.deserialize(new byte[] { 22, 25, 65 }));
		}
	}

	@Test
	public void testSerialize() throws Exception
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		BukkitItemStackSerializer serializer = new BukkitItemStackSerializer();
		assertNull("Serialized data should be null", serializer.serialize(null));
		assertNull("Serialized data should be null when an error occurs", serializer.serialize(new ItemStack[] { new ItemStack(Material.APPLE, 10) }));
		try (MockedConstruction<BukkitObjectOutputStream> mockedOutputStream = Mockito.mockConstruction(BukkitObjectOutputStream.class,
				(mock, context) -> {
					doNothing().when(mock).writeObject(any(at.pcgamingfreaks.TestClasses.NMS.ItemStack[].class));
					doNothing().when(mock).flush();
				}))
		{
			assertNotNull("Serialized data should not be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.APPLE, 10) }));
		}
		try (MockedConstruction<BukkitObjectOutputStream> mockedOutputStream = Mockito.mockConstruction(BukkitObjectOutputStream.class,
				(mock, context) -> doThrow(new IOException()).when(mock).writeObject(any(ItemStack[].class))))
		{
			assertNull("Serialized data should be null when an error occurs", serializer.serialize(new ItemStack[] { new ItemStack(Material.APPLE, 10) }));
		}
	}

	@Test
	public void testIsMCVersionCompatible() throws NoSuchFieldException, IllegalAccessException
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		Field field = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.MC_1_8);
		assertTrue("The serializer should be compatible with all MC versions", BukkitItemStackSerializer.isMCVersionCompatible());
		TestUtils.setUnaccessible(field, null, true);
	}

	@Test
	public void testCheckIsMCVersionCompatible() throws NoSuchFieldException, IllegalAccessException
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		Field field = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.UNKNOWN);
		assertTrue("The serializer should be compatible with all MC versions", new BukkitItemStackSerializer().checkIsMCVersionCompatible());
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.MC_1_9);
		assertTrue("The serializer should be compatible with all MC versions", new BukkitItemStackSerializer().checkIsMCVersionCompatible());
		TestUtils.setUnaccessible(field, null, true);
	}
}