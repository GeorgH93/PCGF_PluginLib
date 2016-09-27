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

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class BaseSenderTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
	}

	@Test
	public void testFinalizeJSON() throws IllegalAccessException, NoSuchFieldException, InvocationTargetException
	{
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		Field chatSerializerMethodAField = BaseSender.class.getDeclaredField("CHAT_SERIALIZER_METHOD_A");
		chatSerializerMethodAField.setAccessible(true);
		modifiers.set(chatSerializerMethodAField, chatSerializerMethodAField.getModifiers() & ~Modifier.FINAL);
		Method chatSerializerMethodA = (Method) chatSerializerMethodAField.get(null);
		chatSerializerMethodAField.set(null, null);
		assertNull("Null should be returned when an error occurs", BaseSender.finalizeJson(""));
		chatSerializerMethodAField.set(null, chatSerializerMethodA);
		modifiers.set(chatSerializerMethodAField, chatSerializerMethodAField.getModifiers() | Modifier.FINAL);
		chatSerializerMethodAField.setAccessible(false);
		modifiers.setAccessible(false);
	}
}