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

package at.pcgamingfreaks.Bukkit.GUI;

import at.pcgamingfreaks.Bukkit.Util.InventoryUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class GuiListener implements Listener
{
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(final InventoryClickEvent event)
	{
		Inventory clickedInventory = InventoryUtils.getClickedInventory(event);
		if(clickedInventory == null) return;
		InventoryHolder holder = clickedInventory.getHolder();
		if(!(holder instanceof IGui)) return;
		event.setCancelled(true);
		((IGui) holder).onClick(event);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDrag(final InventoryDragEvent event)
	{
		if(event.getInventory().getHolder() instanceof IGui)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryInteract(final InventoryInteractEvent event)
	{
		if(event.getInventory().getHolder() instanceof IGui)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryOpen(final InventoryOpenEvent event)
	{
		InventoryHolder holder = event.getInventory().getHolder();
		if(holder instanceof IGui && event.getPlayer() instanceof Player)
		{
			((IGui) holder).onOpen((Player) event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClose(final InventoryCloseEvent event)
	{
		InventoryHolder holder = event.getInventory().getHolder();
		if(holder instanceof IGui && event.getPlayer() instanceof Player)
		{
			((IGui) holder).onClose((Player) event.getPlayer());
		}
	}
}