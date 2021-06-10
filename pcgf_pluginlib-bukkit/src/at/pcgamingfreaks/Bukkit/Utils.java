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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Bukkit.Util.InventoryUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * Collection of static functions that are may be useful for plugins.
 * @deprecated Moved to {@link at.pcgamingfreaks.Bukkit.Util.Utils}!
 */
@Deprecated
public class Utils extends at.pcgamingfreaks.Bukkit.Util.Utils
{
	/**
	 * Converts an item stack into a json string used for chat messages.
	 *
	 * @param itemStack The item stack that should be converted into a json string
	 * @param logger The logger that should display the error message in case of an problem
	 * @return The item stack as a json string. empty string if the conversation failed
	 *
	 * @deprecated Moved to {@link InventoryUtils}.
	 */
	@Deprecated
	public static String convertItemStackToJson(@NotNull ItemStack itemStack, @NotNull Logger logger)
	{
		return InventoryUtils.convertItemStackToJson(itemStack, logger);
	}

	/**
	 * Drops the content of an inventory.
	 * The inventory will be cleared after it has been dropped successful.
	 *
	 * @param inventory The inventory to be dropped
	 * @param location The location the inventory should be dropped to
	 *
	 * @deprecated Moved to {@link InventoryUtils}.
	 */
	@Deprecated
	public static void dropInventory(@NotNull Inventory inventory, @NotNull Location location)
	{
		dropInventory(inventory, location, true);
	}

	/**
	 * Drops the content of an inventory.
	 *
	 * @param inventory The inventory to be dropped
	 * @param location The location the inventory should be dropped to
	 * @param clearInventory Defines if the inventory should be cleared after dropping it or not
	 *
	 * @deprecated Moved to {@link InventoryUtils}.
	 */
	@Deprecated
	public static void dropInventory(@NotNull Inventory inventory, @NotNull Location location, boolean clearInventory)
	{
		InventoryUtils.dropInventory(inventory, location, clearInventory);
	}

	@Deprecated
	public static @Nullable Inventory getClickedInventory(final @NotNull InventoryClickEvent event)
	{
		return InventoryUtils.getClickedInventory(event);
	}

	/**
	 * Changes the inventory title for the currently opened inventory.
	 * Does nothing on Minecraft versions older than 1.14 (they do not support it).
	 *
	 * @param player The player for whom the inventory title should be updated
	 * @param newTitle The new title that should be set
	 *
	 * @deprecated Moved to {@link InventoryUtils}.
	 */
	@Deprecated
	public static void updateInventoryTitle(final @NotNull Player player, final @NotNull String newTitle)
	{
		InventoryUtils.updateInventoryTitle(player, newTitle);
	}
}
