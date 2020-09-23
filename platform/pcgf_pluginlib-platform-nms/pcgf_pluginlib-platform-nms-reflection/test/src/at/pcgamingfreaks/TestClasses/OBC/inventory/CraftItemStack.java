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

package at.pcgamingfreaks.TestClasses.OBC.inventory;

import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class CraftItemStack
{
	public static at.pcgamingfreaks.TestClasses.NMS.ItemStack asNMSCopy(ItemStack itemStack)
	{
		return new at.pcgamingfreaks.TestClasses.NMS.ItemStack(itemStack);
	}

	@SuppressWarnings("SpellCheckingInspection")
	public static ItemStack asBukkitCopy(at.pcgamingfreaks.TestClasses.NMS.ItemStack itemStack)
	{
		return new ItemStack(itemStack.itemStack.getType(), itemStack.itemStack.getAmount());
	}
}