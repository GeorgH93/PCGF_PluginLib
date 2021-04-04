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

package at.pcgamingfreaks.Bukkit.Debug;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

/**
 * An class that is useful for debugging. It converts bukkit events into short and readable strings that can be logged into a log file
 */
public class EventToStringUtil
{
	public static String toString(LivingEntity player)
	{
		return String.format("%s (%s)", player.getName(), player.getUniqueId().toString());
	}

	public static void logEvent(Logger logger, InventoryClickEvent event) { logger.info(toString(event)); }

	public static String toString(InventoryClickEvent event)
	{
		return String.format("InventoryClickEvent { action: %s; click: %s; currentItem: %s; cursor: %s; slot: %d; rawSlot: %d; slotType: %s; whoClicked: %s; hotbarButton: %d }", event.getAction().name(), event.getClick().name(),
		                     event.getCurrentItem() != null ? event.getCurrentItem().getType().name() : "null", event.getCursor() != null ? event.getCursor().getType().name() : "null",
		                     event.getSlot(), event.getRawSlot(), event.getSlotType().name(), toString(event.getWhoClicked()), event.getHotbarButton());
	}

	public static void logEvent(Logger logger, InventoryDragEvent event) { logger.info(toString(event)); }

	public static String toString(InventoryDragEvent event)
	{
		return String.format("InventoryDragEvent { cursor: %s; oldCursor: %s; slots: %s; raw-slots: %s whoClicked: %s }",
		                     event.getCursor() != null ? event.getCursor().getType().name() : "null", event.getOldCursor().getType().name(), event.getInventorySlots().toString(),
		                     event.getRawSlots().toString(), toString(event.getWhoClicked()));
	}


	public static void logEvent(Logger logger, EntityPickupItemEvent event) { logger.info(toString(event)); }

	public static String toString(EntityPickupItemEvent event)
	{
		return String.format("EntityPickupItemEvent { item: %s; remaining: %d; entity: %s }",
		                     toString(event.getItem()), event.getRemaining(), toString(event.getEntity()));
	}

	public static String toString(Item item)
	{
		return String.format("{ pickupDelay: %d; itemStack: %s}",
		                     item.getPickupDelay(), toString(item.getItemStack())); //TODO add material data and meta
	}

	public static String toString(ItemStack itemStack)
	{
		return String.format("{ type: %s; amount: %d; durability: %d }", itemStack.getType().name(), itemStack.getAmount(), itemStack.getDurability());
	}

	public static void logEvent(Logger logger, PlayerInteractEvent event) { logger.info(toString(event)); }

	public static String toString(PlayerInteractEvent event)
	{
		return String.format("PlayerInteractEvent { action: %s; player: %s (%s); item: %s; hand: %s }",
		                     event.getAction().name(), toString(event.getPlayer()), toString(event.getItem()), event.getHand());
	}

	public static void logEvent(Logger logger, PlayerSwapHandItemsEvent event)
	{
		logger.info(toSting(event));
	}
}