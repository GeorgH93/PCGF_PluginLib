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
import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.TestClasses.NMS.NBTCompressedStreamTools;
import at.pcgamingfreaks.TestClasses.NMS.NBTTagCompound;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class NBTItemStackSerializerTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
		TestUtils.initReflection();
	}

	@Test
	public void testWithLogger() throws NoSuchFieldException, IllegalAccessException
	{
		Logger mockedLogger = mock(Logger.class);
		NBTItemStackSerializer serializer = new NBTItemStackSerializer(mockedLogger);
		assertNotNull("The NBTItemStackSerializer should not be null", serializer);
		Field setIntField = TestUtils.setAccessible(NBTItemStackSerializer.class, serializer, "METHOD_NBT_TAG_C_SET_INT", null);
		serializer = new NBTItemStackSerializer(mockedLogger);
		TestUtils.setUnaccessible(setIntField, serializer, true);
		assertNotNull("The NBTItemStackSerializer should not be null", serializer);
	}

	@Test
	public void testDeserialize() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException
	{
		NBTItemStackSerializer deserializer = new NBTItemStackSerializer();
		assertNull("Deserialized data should be null", deserializer.deserialize(null));
		assertNotNull("Deserialized data should not be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		Field field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_NBT_COMP_STREAM_A2", TestBukkitServer.class.getDeclaredMethod("shutdown"));
		assertNull("Deserialized data should be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		field.set(null, null);
		assertNull("Deserialized data should be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_GET_INT", null);
		assertNull("Deserialized data should be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_HAS_KEY_OF_TYPE", null);
		assertNull("Deserialized data should be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		TestUtils.setUnaccessible(field, null, true);
		//noinspection SpellCheckingInspection
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_AS_BUKKIT_COPY", null);
		assertNotNull("Deserialized data should be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_GET_COMPOUND", null);
		assertNull("Deserialized data should not be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_CREATE_STACK", null);
		assertNotNull("Deserialized data should not be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		Field field2 = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "CONSTRUCTOR_NMS_ITEM_STACK", NMSReflection.getConstructor(at.pcgamingfreaks.TestClasses.NMS.ItemStack.class, NBTTagCompound.class));
		assertNotNull("Deserialized data should not be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		TestUtils.setUnaccessible(field, null, true);
		TestUtils.setUnaccessible(field2, null, true);
		TestObjects.setBukkitVersion("1_11");
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "CONSTRUCTOR_NMS_ITEM_STACK", NMSReflection.getConstructor(at.pcgamingfreaks.TestClasses.NMS.ItemStack.class, NBTTagCompound.class));
		field2 = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.MC_1_11);
		deserializer = new NBTItemStackSerializer();
		assertNotNull("Deserialized data should be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		TestUtils.setUnaccessible(field, null, true);
		TestUtils.setUnaccessible(field2, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_NBT_COMP_STREAM_A2", NMSReflection.getMethod(NBTCompressedStreamTools.class, "fakeFunction", InputStream.class));
		assertNotNull("Deserialized data should not be null", deserializer.deserialize(new byte[] { 1, 2, 3 }));
		assertNull("Deserialized object data should be null", deserializer.deserialize(new byte[] { 1, 2, 3 })[0]);
		TestUtils.setUnaccessible(field, null, true);
	}

	@Test
	public void testSerialize() throws NoSuchFieldException, IllegalAccessException
	{
		NBTItemStackSerializer serializer = new NBTItemStackSerializer();
		assertNull("Serialized data should be null", serializer.serialize(null));
		assertNotNull("Serialized data should not be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.DIRT, 45), null }));
		Field field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "CLASS_NBT_TAG_COMPOUND", TestBukkitServer.class);
		assertNull("Serialized data should be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.DIRT, 45), null }));
		field.set(null, null);
		assertNull("Serialized data should be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.DIRT, 45), null }));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_NBT_TAG_C_SET2", null);
		assertNull("Serialized data should be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.DIRT, 45), null }));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_NBT_TAG_C_SET_INT", null);
		assertNull("Serialized data should be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.DIRT, 45), null }));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_SAVE", null);
		assertNull("Serialized data should be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.DIRT, 45), null }));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_AS_NMS_COPY", null);
		assertNull("Serialized data should be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.DIRT, 45), null }));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(NBTItemStackSerializer.class, null, "METHOD_NBT_COMP_STEAM_A", null);
		assertNull("Serialized data should be null", serializer.serialize(new ItemStack[] { new ItemStack(Material.DIRT, 45), null }));
		TestUtils.setUnaccessible(field, null, true);
	}

	@Test
	public void testIsMCVersionCompatible() throws NoSuchFieldException, IllegalAccessException
	{
		Field field = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.MC_1_8);
		assertTrue("It should be MCVersion compatible", NBTItemStackSerializer.isMCVersionCompatible());
		TestUtils.setUnaccessible(field, null, true);
	}

	@Test
	public void testCheckIsMCVersionCompatible() throws NoSuchFieldException, IllegalAccessException
	{
		Field field = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.UNKNOWN);
		assertFalse("It should not be MCVersion compatible", new NBTItemStackSerializer().checkIsMCVersionCompatible());
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(MCVersion.class, null, "CURRENT_VERSION", MCVersion.MC_1_8);
		assertTrue("It should be MCVersion compatible", new NBTItemStackSerializer().checkIsMCVersionCompatible());
		TestUtils.setUnaccessible(field, null, true);
	}
}