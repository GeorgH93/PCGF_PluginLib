/*
 * Copyright (C) 2017 MarkusWME
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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
public class MinecraftMaterialTest
{
	@Test
	public void testFromInput()
	{
		MinecraftMaterial material = MinecraftMaterial.fromInput("120:10");
		assertNotNull("The material should not be null", material);
		assertEquals("The material should match", Material.ENDER_PORTAL_FRAME, material.getMaterial());
		assertEquals("The material data should match", 10, material.getDataValue());
		assertNull("Invalid input should lead to a null MinecraftMaterial", MinecraftMaterial.fromInput(""));
	}

	@Test
	public void testHashCode()
	{
		assertEquals("The Hash Code should match", Material.CAKE.hashCode(), new MinecraftMaterial(Material.CAKE).hashCode());
	}

	@Test
	public void testEquals()
	{
		MinecraftMaterial material = new MinecraftMaterial(Material.CAKE_BLOCK);
		assertTrue("The material should match", material.equals(Material.CAKE_BLOCK, (short) -1));
		assertFalse("The material should not match", material.equals(Material.CAKE_BLOCK, (short) 10));
		assertFalse("The material should not match", material.equals(Material.STONE, (short) -1));
	}

	@Test
	public void testEqualsMaterial()
	{
		MinecraftMaterial material = new MinecraftMaterial(Material.STONE);
		assertEquals("The material of the MinecraftMaterial should match", Material.STONE, material.getMaterial());
		assertEquals("The object data of the MinecraftMaterial should not be set", -1, material.getDataValue());
		assertTrue("The MinecraftMaterial should match with the given Material", material.equals(Material.STONE));
		assertTrue("The MinecraftMaterial should match with the given Material", material.equals(Material.STONE, (short) -1));
		assertFalse("The MinecraftMaterial should not match with the given Material", material.equals(Material.BED));
		assertFalse("The MinecraftMaterial should not match with the given Material", material.equals(Material.STONE, (short) 10));
	}

	@Test
	public void testEqualsItemStack()
	{
		ItemStack itemStack = new ItemStack(Material.WOOD, 12);
		MinecraftMaterial material = new MinecraftMaterial(itemStack);
		assertEquals("The material of the MinecraftMaterial should match", Material.WOOD, material.getMaterial());
		assertEquals("The object data of the MinecraftMaterial should match the durability of the ItemStack", 0, itemStack.getDurability());
		assertTrue("The MinecraftMaterial should match with the given ItemStack", material.equals(new ItemStack(Material.WOOD, 54)));
		assertTrue("The MinecraftMaterial should match with the given ItemStack", Objects.equals(MinecraftMaterial.fromInput("120:-1"), new ItemStack(Material.ENDER_PORTAL_FRAME, 12)));
		assertFalse("The MinecraftMaterial should not match with the given ItemStack", material.equals(new ItemStack(Material.ACACIA_DOOR, 12)));
		assertFalse("The MinecraftMaterial should not match with the given ItemStack", Objects.equals(MinecraftMaterial.fromInput("120:10"), new ItemStack(Material.ENDER_PORTAL_FRAME, 12)));
	}

	@Test
	public void testEqualsBlock()
	{
		TestBlock block = new TestBlock();
		TestBlock block1 = new TestBlock();
		block1.material = Material.STONE;
		MinecraftMaterial material = new MinecraftMaterial(block);
		assertEquals("The material of the MinecraftMaterial should match", block.getType(), material.getMaterial());
		assertEquals("The object data of the MinecraftMaterial should match the block data", block.getData(), material.getDataValue());
		assertTrue("The MinecraftMaterial should match with the given Block", material.equals(block));
		assertTrue("The MinecraftMaterial should match with the given Block", Objects.equals(MinecraftMaterial.fromInput("1:-1"), block1));
		assertFalse("The MinecraftMaterial should not match with the given Block", material.equals(block1));
		assertFalse("The MinecraftMaterial should not match with the given Block", Objects.equals(MinecraftMaterial.fromInput("1:2"), block1));
	}

	@Test
	public void testEqualsMinecraftBlock()
	{
		MinecraftMaterial material1 = new MinecraftMaterial(Material.STONE);
		MinecraftMaterial material2 = new MinecraftMaterial(Material.CAKE);
		MinecraftMaterial material3 = new MinecraftMaterial(Material.STONE, (short) 10);
		MinecraftMaterial material4 = new MinecraftMaterial(Material.CAKE, (short) 10);
		MinecraftMaterial material5 = new MinecraftMaterial(Material.STONE, (short) 12);
		assertEquals("The tow materials should match", material1, material1);
		assertEquals("The tow materials should match", material1, material3);
		assertNotEquals("The tow materials should not match", material1, material2);
		assertNotEquals("The tow materials should not match", material1, material4);
		assertEquals("The tow materials should match", material3, material1);
		assertNotEquals("The tow materials should not match", material2, material1);
		assertNotEquals("The tow materials should not match", material4, material1);
		assertEquals("The tow materials should match", material3, material3);
		assertNotEquals("The tow materials should not match", material3, material5);
	}

	@Test
	public void testEqualsObject()
	{
		assertNotEquals("The objects should not match", new MinecraftMaterial(Material.IRON_BLOCK), 100);
	}
}