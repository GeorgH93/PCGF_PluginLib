/*
 *   Copyright (C) 2024 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.PlatformResolver;

import org.bukkit.Location;
import org.bukkit.Material;
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
		return INSTANCE.getClickedInventory(event);
	}

	/**
	 * Get the item in the players hand.
	 * If the player does not have an item in its main hand, the offhand will be used.
	 *
	 * @param player The player for whom the item should be obtained.
	 * @return The item in the players hand.
	 */
	public static @Nullable ItemStack getItemInHand(final @NotNull Player player)
	{
		if(MCVersion.isDualWieldingMC())
		{
			ItemStack item = player.getInventory().getItemInMainHand();
			if(item != null && item.getType() != Material.AIR) return item;
			return player.getInventory().getItemInOffHand();
		}
		else
		{
			//noinspection deprecation
			return player.getItemInHand();
		}
	}

	/**
	 * Get the item in the players main hand.
	 *
	 * @param player The player for whom the item should be obtained.
	 * @return The item in the players main hand.
	 */
	public static @Nullable ItemStack getItemInMainHand(final @NotNull Player player)
	{
		if(MCVersion.isDualWieldingMC())
		{
			return player.getInventory().getItemInMainHand();
		}
		else
		{
			//noinspection deprecation
			return player.getItemInHand();
		}
	}

	/**
	 * Get the item in the players off hand.
	 *
	 * @param player The player for whom the item should be obtained.
	 * @return The item in the players off hand.
	 */
	public static @Nullable ItemStack getItemInOffHand(final @NotNull Player player)
	{
		if(MCVersion.isDualWieldingMC())
		{
			return player.getInventory().getItemInOffHand();
		}
		else
		{
			//noinspection deprecation
			return player.getItemInHand();
		}
	}

	/**
	 * Sets the item in the players main hand.
	 *
	 * @param player The player for whom the item in the main hand should be set.
	 * @param item The item that should be set for the main hand.
	 */
	public static void setItemInMainHand(final @NotNull Player player, final @Nullable ItemStack item)
	{
		if(MCVersion.isDualWieldingMC())
		{
			player.getInventory().setItemInMainHand(item);
		}
		else
		{
			//noinspection deprecation
			player.setItemInHand(item);
		}
	}

	/**
	 * Sets the item in the players off hand.
	 * On MC versions that do not have dual wielding the main hand will be used.
	 *
	 * @param player The player for whom the item in the off hand should be set.
	 * @param item The item that should be set for the off hand.
	 */
	public static void setItemInOffHand(final @NotNull Player player, final @Nullable ItemStack item)
	{
		if(MCVersion.isDualWieldingMC())
		{
			player.getInventory().setItemInOffHand(item);
		}
		else
		{
			//noinspection deprecation
			player.setItemInHand(item);
		}
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

	/**
	 * Opens an inventory for a player with a different title than was sued when creating the inventory.
	 *
	 * @param player The player for whom the inventory should be opened.
	 * @param inventory The inventory that should be opened.
	 * @param title The title of the inventory that should be used on the client.
	 */
	public static void openInventoryWithCustomTitle(final @NotNull Player player, final @NotNull Inventory inventory, final  @NotNull String title)
	{
		INSTANCE.openInventoryWithCustomTitle(player, inventory, title);
	}

	/**
	 * Opens an inventory for a player with a different title than was sued when creating the inventory.
	 *
	 * @param player The player for whom the inventory should be opened.
	 * @param inventory The inventory that should be opened.
	 * @param title The title of the inventory that should be used on the client.
	 * @param args The values that should be used to fill the placeholders of the title.
	 */
	public static void openInventoryWithCustomTitle(final @NotNull Player player, final @NotNull Inventory inventory, final  @NotNull Message title, final Object... args)
	{
		INSTANCE.openInventoryWithCustomTitlePrepared(player, inventory, prepareTitleForOpenInventoryWithCustomTitle(title, args));
	}

	/**
	 * Opens an inventory for a player with a different title than was sued when creating the inventory.
	 *
	 * @param player The player for whom the inventory should be opened.
	 * @param inventory The inventory that should be opened.
	 * @param title The title of the inventory that should be used on the client. <b>Must have been generated with the {@link InventoryUtils#prepareTitleForOpenInventoryWithCustomTitle(String)} method!</b>
	 */
	public static void openInventoryWithCustomTitlePrepared(final @NotNull Player player, final @NotNull Inventory inventory, final  @NotNull Object title)
	{
		INSTANCE.openInventoryWithCustomTitlePrepared(player, inventory, title);
	}

	/**
	 * Prepares a title to be used with the {@link InventoryUtils#openInventoryWithCustomTitlePrepared(Player, Inventory, Object)} method.
	 *
	 * @param title The title that should be prepared.
	 * @return The prepared title.
	 */
	public static Object prepareTitleForOpenInventoryWithCustomTitle(final @NotNull String title)
	{
		return INSTANCE.prepareTitleForOpenInventoryWithCustomTitle(title);
	}

	/**
	 * Prepares a title to be used with the {@link InventoryUtils#openInventoryWithCustomTitlePrepared(Player, Inventory, Object)} method.
	 *
	 * @param title The title that should be prepared.
	 * @param args The values that should be used to fill the placeholders with the title.
	 * @return The prepared title.
	 */
	public static Object prepareTitleForOpenInventoryWithCustomTitle(final @NotNull Message title, final Object... args)
	{
		if(MCVersion.isNewerOrEqualThan(MCVersion.MC_1_16))
		{
			return title.prepareChatComponent(args);
		}
		else
		{
			return prepareTitleForOpenInventoryWithCustomTitle(title.prepareChatLegacy(args));
		}
	}

	/**
	 * Changes the title of an existing inventory.
	 * <b>WARNING:</b>
	 * <ul>
	 *     <li>MC version &lt;= 1.13: If the inventory is currently opened by a player the title will only get updated for them once they reopen the inventory.</li>
	 *     <li>MC version &gt;= 1.14: If the inventory is currently opened by a player it will become invalid! All interactions with it will fail on the server with an {@link IndexOutOfBoundsException}. The content of the inventory might disappear on the client.</li>
	 * </ul>
	 *
	 * @param inventory The inventory for which the title should be changed.
	 * @param newTitle The new title that should be used.
	 */
	public static void setInventoryTitle(final @NotNull Inventory inventory, final @NotNull String newTitle)
	{
		INSTANCE.setInventoryTitle(inventory, newTitle);
	}

	public static @Nullable Inventory getPlayerTopInventory(final @NotNull Player player)
	{
		return INSTANCE.getPlayerTopInventory(player);
	}

	private InventoryUtils() {}
}