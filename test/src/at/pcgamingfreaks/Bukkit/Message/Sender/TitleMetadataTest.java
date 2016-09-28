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
import at.pcgamingfreaks.TestClasses.NMS.PacketPlayOutTitle;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import com.google.gson.Gson;

import org.bukkit.Bukkit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class TitleMetadataTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
	}

	@Test
	public void testTitleMetadata()
	{
		TitleMetadata titleMetadata = TitleMetadata.fromJson("{\"fadeIn\":10,\"fadeOut\":20,\"stay\":30,\"subtitle\":true}");
		assertEquals("The title metadata should match", 10, titleMetadata.getFadeIn());
		assertEquals("The title metadata should match", 20, titleMetadata.getFadeOut());
		assertEquals("The title metadata should match", 30, titleMetadata.getStay());
		assertTrue("The title metadata should match", titleMetadata.isSubtitle());
		assertEquals("The title metadata should match", PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleMetadata.getTitleType());
		titleMetadata.setFadeIn(30);
		titleMetadata.setFadeOut(50);
		titleMetadata.setStay(80);
		titleMetadata.setSubtitle(false);
		assertEquals("The title metadata should match", 30, titleMetadata.getFadeIn());
		assertEquals("The title metadata should match", 50, titleMetadata.getFadeOut());
		assertEquals("The title metadata should match", 80, titleMetadata.getStay());
		assertFalse("The title metadata should match", titleMetadata.isSubtitle());
		assertEquals("The title metadata should match", PacketPlayOutTitle.EnumTitleAction.TITLE, titleMetadata.getTitleType());
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection")
	public void testFromJsonWithError() throws NoSuchFieldException, IllegalAccessException
	{
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		Field gsonField = TitleMetadata.class.getDeclaredField("GSON");
		gsonField.setAccessible(true);
		modifiers.set(gsonField, gsonField.getModifiers() & ~Modifier.FINAL);
		Gson gson = (Gson) gsonField.get(null);
		gsonField.set(null, null);
		assertNotNull("The metadata object should be a new one", TitleMetadata.fromJson(""));
		gsonField.set(null, gson);
		modifiers.set(gsonField, gsonField.getModifiers() | Modifier.FINAL);
		gsonField.setAccessible(false);
		modifiers.setAccessible(false);
	}
}