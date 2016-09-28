/*
 * Copyright (C) 2016 MarkusWME
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Effects;

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class MaterialEffectsTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
	}

	@Before
	public void prepareTestObjects()
	{
		/*mockStatic(NMSReflection.class);
		doAnswer(new Answer<Enum<?>>() {
			@Override
			public Enum<?> answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				String name = (String) invocationOnMock.getArguments()[0];
				//noinspection SpellCheckingInspection
				return NMSReflection.getNMSEnum(name.replace("net.minecraft.server", "at.pcgamingfreaks.TestClasses.NMS"));
			}
		}).when(NMSReflection.getEnum(anyString()));*/
	}

	//@Test
	public void testMaterialEffects() throws NoSuchFieldException, IllegalAccessException
	{
		TestObjects.setBukkitVersion("1_8");
		MaterialEffects itemCrack = MaterialEffects.ITEM_CRACK;
		//noinspection SpellCheckingInspection
		assertEquals("The name of item crack should match", "iconcrack", itemCrack.getName());
		//noinspection SpellCheckingInspection
		assertEquals("The uppercase name of item crack should match", "ICONCRACK", itemCrack.getNameUpperCase());
		assertEquals("The new name of item crack should match", "ITEM_CRACK", itemCrack.getNewName());
		assertNotNull("The enum of item crack should not be null", itemCrack.getEnum());
	}
}