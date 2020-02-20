/*
 *   Copyright (C) 2020 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.TestClasses.TestBlock;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Test;

import java.util.HashSet;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ItemNameResolverTest
{
	@SuppressWarnings({ "ConstantConditions" })
	@Test
	public void testGetName() throws YamlKeyNotFoundException
	{
		ItemNameResolver itemNameResolver = new ItemNameResolver();
		assertEquals("The name of the block should match", "air", itemNameResolver.getName(new TestBlock()));
		assertEquals("The name of the material should match", "stone", itemNameResolver.getName(Material.STONE));
		assertEquals("The name of the Minecraft material should match", "cobblestone", itemNameResolver.getName(MinecraftMaterial.fromInput("Cobblestone")));
		final int[] counts = { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(invocationOnMock -> {
			counts[0]++;
			return null;
		}).when(mockedLogger).info(anyString());
		Language mockedLanguage = mock(Language.class);
		doReturn(false).when(mockedLanguage).isLoaded();
		itemNameResolver.loadLegacy(mockedLanguage, mockedLogger);
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
		doReturn("Air").when(mockedLanguage).getRaw(eq("0"), anyString());
		doReturn("Stone").when(mockedLanguage).getRaw(eq("1"), anyString());
		doReturn("Granite").when(mockedLanguage).getRaw(eq("1.1"), anyString());
		doReturn("Polished Granite").when(mockedLanguage).getRaw(eq("1.2"), anyString());
		doReturn("Diorite").when(mockedLanguage).getRaw(eq("1:3"), anyString());
		doReturn("Polished Diorite").when(mockedLanguage).getRaw(eq("1:4"), anyString());
		doReturn("Andesite").when(mockedLanguage).getRaw(eq("1.5"), anyString());
		doReturn("Polished Andesite").when(mockedLanguage).getRaw(eq("1.6"), anyString());
		doReturn("Wood Plank").when(mockedLanguage).getRaw(eq("5"), anyString());
		doReturn("Oak").when(mockedLanguage).getRaw(eq("5.0"), anyString());
		doReturn("Oak Sapling").when(mockedLanguage).getRaw(eq("6.0"), anyString());
		itemNameResolver.loadLegacy(mockedLanguage, mockedLogger);
		assertEquals("The info count should match", 2, counts[0]);
		assertEquals("The name of the material should match", "Air", itemNameResolver.getName(Material.AIR));
		assertEquals("The name of the material should match", "Andesite", itemNameResolver.getName(Material.STONE, (short) 5));
		assertEquals("The name of the material should match", "Stone", itemNameResolver.getName(Material.STONE, (short) 100));
		assertEquals("The name of the material should match", "Stone", itemNameResolver.getName(Material.STONE, (short) -8));
		assertEquals("The name of the material should match", "sapling", itemNameResolver.getName(Material.SAPLING, (short) -1));
		assertEquals("The name of the material should match", "sapling", itemNameResolver.getName(Material.SAPLING, (short) -20));
	}

	@Test
	public void testGetNameFromItemStack()
	{
		ItemNameResolver itemNameResolver = new ItemNameResolver();
		// Setup item
		ItemStack stack = mock(ItemStack.class);
		doReturn(Material.BIRCH_FENCE).when(stack).getType();
		doReturn((short) 1).when(stack).getDurability();
		doReturn(false).when(stack).hasItemMeta();
		assertEquals("The name of the item stack should match", "birch_fence", itemNameResolver.getName(stack)); // Test without custom item name
		// Setup item meta
		doReturn(true).when(stack).hasItemMeta();
		ItemMeta meta = mock(ItemMeta.class);
		doReturn(meta).when(stack).getItemMeta();
		doReturn(false).when(meta).hasDisplayName();
		assertEquals("The name of the item stack should match", "birch_fence", itemNameResolver.getName(stack)); // Test item with meta, but without custom item name
		doReturn(true).when(meta).hasDisplayName();
		doReturn("Magic Fence").when(meta).getDisplayName();
		assertEquals("The name of the item stack should match", "Magic Fence", itemNameResolver.getName(stack)); // Test item with custom name without color
		doReturn(ChatColor.BLUE + "Magic Fence").when(meta).getDisplayName();
		assertEquals("The name of the item stack should match", "Magic Fence", itemNameResolver.getName(stack)); // Test item with custom name with color
	}

	@Test
	public void testGetDisplayName()
	{
		ItemNameResolver itemNameResolver = new ItemNameResolver();
		// Setup item
		ItemStack stack = mock(ItemStack.class);
		doReturn(Material.BIRCH_FENCE).when(stack).getType();
		doReturn((short) 1).when(stack).getDurability();
		doReturn(false).when(stack).hasItemMeta();
		assertEquals("The name of the item stack should match", ChatColor.GRAY + "birch_fence", itemNameResolver.getDisplayName(stack)); // Test without custom item name
		// Setup item meta
		doReturn(true).when(stack).hasItemMeta();
		ItemMeta meta = mock(ItemMeta.class);
		doReturn(meta).when(stack).getItemMeta();
		doReturn(false).when(meta).hasDisplayName();
		assertEquals("The name of the item stack should match", ChatColor.GRAY + "birch_fence", itemNameResolver.getDisplayName(stack)); // Test item with meta, but without custom item name
		doReturn(true).when(meta).hasDisplayName();
		doReturn("Magic Fence").when(meta).getDisplayName();
		assertEquals("The name of the item stack should match", "Magic Fence", itemNameResolver.getDisplayName(stack)); // Test item with custom name without color
		doReturn(ChatColor.BLUE + "Magic Fence").when(meta).getDisplayName();
		assertEquals("The name of the item stack should match", ChatColor.BLUE + "Magic Fence", itemNameResolver.getDisplayName(stack)); // Test item with custom name with color
	}
}