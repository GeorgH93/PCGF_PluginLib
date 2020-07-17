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

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.PlatformResolver;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class InventoryUtils
{
	private static final IInventoryUtils INSTANCE = PlatformResolver.createPlatformInstance(IInventoryUtils.class);

	/**
	 * Converts an item stack into a json string used for chat messages.
	 *
	 * @param itemStack The item stack that should be converted into a json string
	 * @param logger The logger that should display the error message in case of an problem
	 * @return The item stack as a json string. empty string if the conversation failed
	 */
	public static String convertItemStackToJson(final @NotNull ItemStack itemStack, final @NotNull Logger logger)
	{
		return INSTANCE.convertItemStackToJson(itemStack, logger);
	}

	/**
	 * Drops the content of an inventory.
	 * The inventory will be cleared after it has been dropped successful.
	 *
	 * @param inventory The inventory to be dropped
	 * @param location The location the inventory should be dropped to
	 */
	public static void dropInventory(final @NotNull Inventory inventory, final @NotNull Location location)
	{
		dropInventory(inventory, location, true);
	}

	/**
	 * Drops the content of an inventory.
	 *
	 * @param inventory The inventory to be dropped
	 * @param location The location the inventory should be dropped to
	 * @param clearInventory Defines if the inventory should be cleared after dropping it or not
	 */
	public static void dropInventory(final @NotNull Inventory inventory, final @NotNull Location location, boolean clearInventory)
	{
		for(ItemStack i : inventory.getContents())
		{
			if(i != null)
			{
				location.getWorld().dropItemNaturally(location, i);
			}
		}
		if(clearInventory) inventory.clear();
	}

	/**
	 * Gets the inventory that the player clicked on. This does the same as the InventoryClickEvent#getClickedInventory method found in newer versions of the Bukkit API.
	 *
	 * @param event The event that should be checked.
	 * @return The inventory the player clicked on.
	 */
	public static @Nullable Inventory getClickedInventory(final @NotNull InventoryClickEvent event)
	{
		if (event.getRawSlot() < 0) return null;

		return event.getRawSlot() < event.getView().getTopInventory().getSize() ? event.getView().getTopInventory() : event.getView().getBottomInventory();
	}

	/**
	 * Changes the inventory title for the currently opened inventory.
	 * Does nothing on Minecraft versions older than 1.14 (they do not support it).
	 *
	 * @param player The player for whom the inventory title should be updated
	 * @param newTitle The new title that should be set
	 */
	public static void updateInventoryTitle(final @NotNull Player player, final @NotNull String newTitle)
	{
		INSTANCE.updateInventoryTitle(player, newTitle);
	}
}