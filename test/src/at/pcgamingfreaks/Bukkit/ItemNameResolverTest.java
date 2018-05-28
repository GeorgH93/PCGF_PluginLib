/*
 * Copyright (C) 2018 MarkusWME
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

import at.pcgamingfreaks.TestClasses.TestBlock;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YAMLKeyNotFoundException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ItemNameResolverTest
{
	@SuppressWarnings({ "ConstantConditions", "ResultOfMethodCallIgnored" })
	@Test
	public void testGetName() throws YAMLKeyNotFoundException
	{
		ItemNameResolver itemNameResolver = new ItemNameResolver();
		assertEquals("The name of the block should match", "air", itemNameResolver.getName(new TestBlock()));
		assertEquals("The name of the material should match", "stone", itemNameResolver.getName(Material.STONE));
		assertEquals("The name of the Minecraft material should match", "cobblestone", itemNameResolver.getName(MinecraftMaterial.fromInput("Cobblestone")));
		assertEquals("The name of the item stack should match", "birch_fence", itemNameResolver.getName(new ItemStack(Material.BIRCH_FENCE)));
		final int[] counts = { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				counts[0]++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
		Language mockedLanguage = mock(Language.class);
		doReturn(false).when(mockedLanguage).isLoaded();
		itemNameResolver.load(mockedLanguage, mockedLogger);
		assertEquals("The info count should match", 0, counts[0]);
		YAML mockedYAML = mock(YAML.class);
		HashSet<String> keys = new HashSet<>();
		keys.add("0");
		keys.add("1");
		keys.add("1.1");
		keys.add("1.2");
		keys.add("1:3");
		keys.add("1:4");
		keys.add("1.5");
		keys.add("1.6");
		keys.add("10.a");
		keys.add("5.appendDefault");
		keys.add("5");
		keys.add("5.0");
		keys.add("6.0");
		doReturn(keys).when(mockedYAML).getKeys(true);
		doReturn(false).when(mockedYAML).getBoolean(anyString(), anyBoolean());
		doReturn(true).when(mockedYAML).getBoolean("5.appendDefault", false);
		doReturn("Air").when(mockedYAML).getString("0");
		doReturn("Stone").when(mockedYAML).getString("1");
		doReturn("Granite").when(mockedYAML).getString("1.1");
		doReturn("Polished").when(mockedYAML).getString("1.2");
		doReturn("Diorite").when(mockedYAML).getString("1:3");
		doReturn("Polished Diorite").when(mockedYAML).getString("1:4");
		doReturn("Andesite").when(mockedYAML).getString("1.5");
		doReturn("Polished Andesite").when(mockedYAML).getString("1.6");
		doReturn("Wood Plank").when(mockedYAML).getString("5");
		doReturn("Oak").when(mockedYAML).getString("5.0");
		doReturn("Oak Sapling").when(mockedYAML).getString("6.0");
		doReturn(true).when(mockedLanguage).isLoaded();
		doReturn(mockedYAML).when(mockedLanguage).getLang();
		doReturn("Air").when(mockedLanguage).get("0");
		doReturn("Stone").when(mockedLanguage).get("1");
		doReturn("Granite").when(mockedLanguage).get("1.1");
		doReturn("Polished Granite").when(mockedLanguage).get("1.2");
		doReturn("Diorite").when(mockedLanguage).get("1:3");
		doReturn("Polished Diorite").when(mockedLanguage).get("1:4");
		doReturn("Andesite").when(mockedLanguage).get("1.5");
		doReturn("Polished Andesite").when(mockedLanguage).get("1.6");
		doReturn("Wood Plank").when(mockedLanguage).get("5");
		doReturn("Oak").when(mockedLanguage).get("5.0");
		doReturn("Oak Sapling").when(mockedLanguage).get("6.0");
		itemNameResolver.load(mockedLanguage, mockedLogger);
		assertEquals("The info count should match", 2, counts[0]);
		assertEquals("The name of the material should match", "Air", itemNameResolver.getName(Material.AIR));
		assertEquals("The name of the material should match", "Andesite", itemNameResolver.getName(Material.STONE, (short) 5));
		assertEquals("The name of the material should match", "Stone", itemNameResolver.getName(Material.STONE, (short) 100));
		assertEquals("The name of the material should match", "Stone", itemNameResolver.getName(Material.STONE, (short) -8));
		assertEquals("The name of the material should match", "sapling", itemNameResolver.getName(Material.SAPLING, (short) -1));
		assertEquals("The name of the material should match", "sapling", itemNameResolver.getName(Material.SAPLING, (short) -20));
	}
}