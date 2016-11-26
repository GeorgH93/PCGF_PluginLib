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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BukkitItemStackSerializer.class, BukkitObjectInputStream.class, BukkitObjectOutputStream.class })
@SuppressWarnings("SpellCheckingInspection")
public class BukkitItemStackSerializerTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
		TestUtils.initReflection();
	}

	@Test
	public void testDeserialize() throws Exception
	{
		BukkitItemStackSerializer deserializer = new BukkitItemStackSerializer();
		assertNull("Deserialized data should be null", deserializer.deserialize(null));
		assertNull("Deserialized data should be null when an error occurs", deserializer.deserialize(new byte[] { 2, 5, 6 }));
		BukkitObjectInputStream mockedInputStream = mock(BukkitObjectInputStream.class);
		doReturn(new ItemStack[] {}).when(mockedInputStream).readObject();
		whenNew(BukkitObjectInputStream.class).withAnyArguments().thenReturn(mockedInputStream);
		assertNotNull("Deserialized data should not be null", deserializer.deserialize(new byte[] { 22, 25, 65 }));
	}

	@Test
	public void testSerialize() throws Exception
	{
		BukkitItemStackSerializer serializer = new BukkitItemStackSerializer();
		assertNull("Serialized data should be null", serializer.serialize(null));
		assertNull("Serialized data should be null when an error occurs", serializer.serialize(new ItemStack[] { new ItemStack(Material.APPLE, 10) }));
		BukkitObjectOutputStream mockedOutputStream = mock(BukkitObjectOutputStream.class);
		doNothing().when(mockedOutputStream).writeObject(any(at.pcgamingfreaks.TestClasses.NMS.ItemStack[].class));
		doNothing().when(mockedOutputStream).flush();
		whenNew(BukkitObjectOutputStream.class).withArguments(any(ByteArrayOutputStream.class)).thenReturn(mockedOutputStream);
		assertNotNull("Serialized data should not be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.APPLE, 10) }));
		doThrow(new IOException()).when(mockedOutputStream).writeObject(any(at.pcgamingfreaks.TestClasses.NMS.ItemStack[].class));
		assertNull("Serialized data should be null when an error occurs", serializer.serialize(new ItemStack[] { new ItemStack(Material.APPLE, 10) }));
	}

	@Test
	public void testIsMCVersionCompatible() throws NoSuchFieldException, IllegalAccessException
	{
		Field field = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.MC_1_8);
		assertTrue("It should be MCVersion compatible", BukkitItemStackSerializer.isMCVersionCompatible());
		TestUtils.setUnaccessible(field, null, true);
	}

	@Test
	public void testCheckIsMCVersionCompatible() throws NoSuchFieldException, IllegalAccessException
	{
		Field field = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.UNKNOWN);
		assertFalse("It should not be MCVersion compatible", new BukkitItemStackSerializer().checkIsMCVersionCompatible());
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.MC_1_9);
		assertTrue("It should be MCVersion compatible", new BukkitItemStackSerializer().checkIsMCVersionCompatible());
		TestUtils.setUnaccessible(field, null, true);
	}
}