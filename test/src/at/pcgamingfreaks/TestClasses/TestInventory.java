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

package at.pcgamingfreaks.TestClasses;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class TestInventory implements Inventory
{
	@Override
	public int getSize() { return 0; }

	@Override
	public int getMaxStackSize() { return 0; }

	@Override
	public void setMaxStackSize(int i) { }

	@Override
	public String getName() { return null; }

	@Override
	public ItemStack getItem(int i) { return null; }

	@Override
	public void setItem(int i, ItemStack itemStack) { }

	@Override
	public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException { return null; }

	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException { return null; }

	@Override
	public ItemStack[] getContents()
	{
		return new ItemStack[] { null, new ItemStack(Material.WOOD) };
	}

	@Override
	public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException { }

	@Override
	public ItemStack[] getStorageContents() { return new ItemStack[0]; }

	@Override
	public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException { }

	@Override
	public boolean contains(int i) { return false; }

	@Override
	public boolean contains(Material material) throws IllegalArgumentException { return false; }

	@Override
	public boolean contains(ItemStack itemStack) { return false; }

	@Override
	public boolean contains(int i, int i1) { return false; }

	@Override
	public boolean contains(Material material, int i) throws IllegalArgumentException { return false; }

	@Override
	public boolean contains(ItemStack itemStack, int i) { return false; }

	@Override
	public boolean containsAtLeast(ItemStack itemStack, int i) { return false; }

	@Override
	public HashMap<Integer, ? extends ItemStack> all(int i) { return null; }

	@Override
	public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException { return null; }

	@Override
	public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) { return null; }

	@Override
	public int first(int i) { return 0; }

	@Override
	public int first(Material material) throws IllegalArgumentException { return 0; }

	@Override
	public int first(ItemStack itemStack) { return 0; }

	@Override
	public int firstEmpty() { return 0; }

	@Override
	public void remove(int i) { }

	@Override
	public void remove(Material material) throws IllegalArgumentException { }

	@Override
	public void remove(ItemStack itemStack) { }

	@Override
	public void clear(int i) { }

	@Override
	public void clear() { }

	@Override
	public List<HumanEntity> getViewers() { return null; }

	@Override
	public String getTitle() { return null; }

	@Override
	public InventoryType getType() { return null; }

	@Override
	public InventoryHolder getHolder() { return null; }

	@Override
	public ListIterator<ItemStack> iterator() { return null; }

	@Override
	public ListIterator<ItemStack> iterator(int i) { return null; }

	@Override
	public Location getLocation() { return null; }
}