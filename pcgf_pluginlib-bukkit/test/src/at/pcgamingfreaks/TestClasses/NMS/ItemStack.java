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

package at.pcgamingfreaks.TestClasses.NMS;

@SuppressWarnings("unused")
public class ItemStack
{
	public org.bukkit.inventory.ItemStack itemStack;

	public ItemStack(org.bukkit.inventory.ItemStack itemStack)
	{
		this.itemStack = itemStack;
	}

	public ItemStack(NBTTagCompound nbtTagCompound)
	{
		//noinspection deprecation
		this.itemStack = new org.bukkit.inventory.ItemStack(nbtTagCompound.getInt("id"), nbtTagCompound.getInt("Count"));
	}

	public static ItemStack createStack(NBTTagCompound nbtTagCompound)
	{
		//noinspection deprecation
		return new ItemStack(new org.bukkit.inventory.ItemStack(nbtTagCompound.getInt("id"), nbtTagCompound.getInt("Count")));
	}

	public NBTTagCompound save(NBTTagCompound nbtTagCompound)
	{
		nbtTagCompound.add("id", itemStack.getType());
		nbtTagCompound.add("Count", itemStack.getAmount());
		return nbtTagCompound;
	}
}