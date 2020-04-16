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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

public class SimpleGui implements IGui
{
	@Getter private final Inventory inventory;
	private final GuiButton[] buttons;

	public SimpleGui(String title, int rows)
	{
		assert rows > 0 && rows <= 7;
		inventory = Bukkit.createInventory(this, rows * 9, title);
		buttons = new GuiButton[9 * rows];
	}

	public void setButton(final int slot, final @Nullable GuiButton button)
	{
		assert slot < buttons.length;
		buttons[slot] = button;
		inventory.setItem(slot, (button == null) ? null : button.getItem());
	}

	public void setButton(final int column, final int row, final @Nullable GuiButton button)
	{
		setButton(column + 9 * row, button);
	}

	public boolean addButton(final @NotNull GuiButton button)
	{
		for(int i = 0; i < inventory.getSize(); i++)
		{
			if(buttons[i] == null)
			{
				setButton(i, button);
				return true;
			}
		}
		return false;
	}

	public GuiButton getButton(final int slot)
	{
		assert slot < buttons.length;
		return buttons[slot];
	}

	public GuiButton getButton(final int column, final int row)
	{
		return getButton(column + 9 * row);
	}

	@Override
	public void show(final @NotNull Player player)
	{
		player.openInventory(inventory);
	}

	@Override
	public void onClick(final @NotNull InventoryClickEvent event)
	{
		if(event.getRawSlot() >= buttons.length || !(event.getWhoClicked() instanceof Player)) return;
		GuiButton button = buttons[event.getRawSlot()];
		if(button == null) return;
		button.onClick((Player) event.getWhoClicked(), event.getClick(), event.getCursor());
	}

	@Override
	public void onOpen(final @NotNull Player player) {}

	@Override
	public void onClose(final @NotNull Player player) {}
}