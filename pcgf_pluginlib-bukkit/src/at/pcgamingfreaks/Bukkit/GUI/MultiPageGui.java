/*
 *   Copyright (C) 2022 GeorgH93
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

package at.pcgamingfreaks.Bukkit.GUI;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MultiPageGui implements IGui
{
	private MultiPageGuiPage[] pages;

	MultiPageGui() {}

	public MultiPageGui(final @NotNull MultiPageGuiPage[] pages)
	{
		setPages(pages);
	}

	void setPages(final @NotNull MultiPageGuiPage[] pages)
	{
		this.pages = pages;
		for(MultiPageGuiPage page : pages)
		{
			page.setOwner(this);
		}
	}

	public void show(final @NotNull Player player, final int page)
	{
		pages[page - 1].show(player);
	}

	@Override
	public void show(final @NotNull Player player)
	{
		show(player, 1);
	}

	@Override
	public void onClick(final @NotNull InventoryClickEvent event) { /* Can be overwritten for custom logic */ }

	@Override
	public void onOpen(final @NotNull Player player) { /* Can be overwritten for custom logic */ }

	@Override
	public void onClose(final @NotNull Player player) { /* Can be overwritten for custom logic */ }

	@Override
	public @NotNull Inventory getInventory()
	{
		return pages[0].getInventory();
	}
}