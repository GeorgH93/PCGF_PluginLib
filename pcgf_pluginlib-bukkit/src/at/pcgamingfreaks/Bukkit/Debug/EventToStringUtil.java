/*
 *   Copyright (C) 2021 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Debug;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * A class that is useful for debugging. It converts bukkit events into short and readable strings that can be logged into a log file
 */
public final class EventToStringUtil
{
	private EventToStringUtil() {}

	public static String toString(final @NotNull LivingEntity player)
	{
		return String.format("%s (%s)", player.getName(), player.getUniqueId().toString());
	}

	public static String toString(final @NotNull ItemStack itemStack)
	{
		return String.format("{ type: %s; amount: %d; durability: %d }", itemStack.getType().name(), itemStack.getAmount(), itemStack.getDurability());
	}

	public static String toString(final @NotNull Item item)
	{
		return String.format("{ pickupDelay: %d; itemStack: %s}",
		                     item.getPickupDelay(), toString(item.getItemStack()));
	}

	public static void logEvent(final @NotNull Logger logger, final @NotNull InventoryClickEvent event) { logger.info(() -> toString(event)); }

	public static String toString(final @NotNull InventoryClickEvent event)
	{
		return String.format("InventoryClickEvent { action: %s; click: %s; currentItem: %s; cursor: %s; slot: %d; rawSlot: %d; slotType: %s; whoClicked: %s; hotbarButton: %d }",
		                     event.getAction().name(), event.getClick().name(),
		                     event.getCurrentItem() != null ? event.getCurrentItem().getType().name() : "null", event.getCursor() != null ? event.getCursor().getType().name() : "null",
		                     event.getSlot(), event.getRawSlot(), event.getSlotType().name(), toString(event.getWhoClicked()), event.getHotbarButton());
	}

	public static void logEvent(final @NotNull Logger logger, final @NotNull InventoryDragEvent event) { logger.info(() -> toString(event)); }

	public static String toString(final @NotNull InventoryDragEvent event)
	{
		return String.format("InventoryDragEvent { cursor: %s; oldCursor: %s; slots: %s; raw-slots: %s whoClicked: %s }",
		                     event.getCursor() != null ? event.getCursor().getType().name() : "null", event.getOldCursor().getType().name(), event.getInventorySlots().toString(),
		                     event.getRawSlots().toString(), toString(event.getWhoClicked()));
	}


	public static void logEvent(final @NotNull Logger logger, final @NotNull EntityPickupItemEvent event) { logger.info(() -> toString(event)); }

	public static String toString(final @NotNull EntityPickupItemEvent event)
	{
		return String.format("EntityPickupItemEvent { item: %s; remaining: %d; entity: %s }",
		                     toString(event.getItem()), event.getRemaining(), toString(event.getEntity()));
	}

	public static void logEvent(final @NotNull Logger logger, final @NotNull PlayerInteractEvent event) { logger.info(() -> toString(event)); }

	public static String toString(final @NotNull PlayerInteractEvent event)
	{
		return String.format("PlayerInteractEvent { action: %s; player: %s; item: %s; hand: %s }",
		                     event.getAction().name(), toString(event.getPlayer()), toString(event.getItem()), event.getHand());
	}

	public static void logEvent(final @NotNull Logger logger, final @NotNull PlayerSwapHandItemsEvent event)
	{
		logger.info(() -> toString(event));
	}

	public static String toString(final @NotNull PlayerSwapHandItemsEvent event)
	{
		return String.format("PlayerSwapHandItemsEvent { player: %s; mainHand: %s; offHand: %s }",
		                     toString(event.getPlayer()), toString(event.getMainHandItem()), toString(event.getOffHandItem()));
	}

	public static void logEvent(final @NotNull Logger logger, final @NotNull AsyncPlayerPreLoginEvent event)
	{
		logger.info(() -> toString(event));
	}

	public static String toString(final @NotNull AsyncPlayerPreLoginEvent event)
	{
		return String.format("AsyncPreLoginEvent { name: %s; uuid: %s; ip: %s; kickMessage: %s; result: %s }",
		                     event.getName(), event.getUniqueId().toString(), event.getAddress().toString(), event.getKickMessage(), event.getLoginResult().name());
	}
	
	public static void logEvent(final @NotNull Logger logger, final @NotNull PlayerLoginEvent event)
	{
		logger.info(() -> toString(event));
	}

	public static String toString(final @NotNull PlayerLoginEvent event)
	{
		return String.format("PlayerLoginEvent { player: %s; ip: %s (%s); kickMessage: %s; result: %s }",
		                     toString(event.getPlayer()), event.getAddress().toString(), event.getHostname(), event.getKickMessage(), event.getResult().name());
	}

	public static void logEvent(final @NotNull Logger logger, final @NotNull PlayerLocaleChangeEvent event)
	{
		logger.info(() -> toString(event));
	}

	public static String toString(final @NotNull PlayerLocaleChangeEvent event)
	{
		return String.format("PlayerLocaleChangeEvent { player: %s; locale: %s }",
		                     toString(event.getPlayer()), event.getLocale());
	}

	public static void logEvent(final @NotNull Logger logger, final @NotNull PlayerQuitEvent event)
	{
		logger.info(() -> toString(event));
	}

	public static String toString(final @NotNull PlayerQuitEvent event)
	{
		return String.format("PlayerQuitEvent { player: %s; quitMessage: %s }", toString(event.getPlayer()), event.getQuitMessage());
	}
}