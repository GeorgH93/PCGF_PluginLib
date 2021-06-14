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

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Message.Sender.TitleLocation;
import at.pcgamingfreaks.Message.Sender.TitleMetadata;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import org.bukkit.Bukkit;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class TitleMetadataTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		if (Bukkit.getServer() == null) {
			Bukkit.setServer(new TestBukkitServer());
		}
		//TestObjects.initNMSReflection();
	}

	@Test
	public void testTitleMetadata()
	{
		TitleMetadata titleMetadata = (new TitleMetadata()).parseJson("{\"fadeIn\":10,\"fadeOut\":20,\"stay\":30,\"subtitle\":true}");
		assertEquals("The title metadata should match", 10, titleMetadata.getFadeIn());
		assertEquals("The title metadata should match", 20, titleMetadata.getFadeOut());
		assertEquals("The title metadata should match", 30, titleMetadata.getStay());
		assertTrue("The title metadata should match", titleMetadata.isSubtitle());
		assertEquals("The title metadata should match", TitleLocation.SUBTITLE, titleMetadata.getLocation());
		titleMetadata.setFadeIn(30);
		titleMetadata.setFadeOut(50);
		titleMetadata.setStay(80);
		titleMetadata.setTitle();
		assertEquals("The title metadata should match", 30, titleMetadata.getFadeIn());
		assertEquals("The title metadata should match", 50, titleMetadata.getFadeOut());
		assertEquals("The title metadata should match", 80, titleMetadata.getStay());
		assertFalse("The title metadata should match", titleMetadata.isSubtitle());
		assertEquals("The title metadata should match", TitleLocation.TITLE, titleMetadata.getLocation());
	}
}