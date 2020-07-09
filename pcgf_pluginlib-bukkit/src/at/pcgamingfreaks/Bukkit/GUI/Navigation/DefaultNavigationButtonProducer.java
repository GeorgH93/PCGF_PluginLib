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

package at.pcgamingfreaks.Bukkit.GUI.Navigation;

import at.pcgamingfreaks.Bukkit.GUI.GuiButton;
import at.pcgamingfreaks.Bukkit.GUI.MultiPageGui;
import at.pcgamingfreaks.Bukkit.HeadUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DefaultNavigationButtonProducer implements INavigationButtonProducer
{
	@Override
	public @NotNull GuiButton produceButton(final @NotNull MultiPageGui gui, final int page, final int pages)
	{
		ItemStack item = new ItemStack(Material.PAPER, page);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Page " + page);
		item.setItemMeta(meta);
		return new GuiButton(item, (player, clickType, cursor) -> { gui.show(player, page); });
	}

	@Override
	public @NotNull GuiButton produceButtonCurrentPage(final @NotNull MultiPageGui gui, final int page, final int pages)
	{
		ItemStack item = new ItemStack(Material.MAP, page);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GREEN + "Page " + page);
		item.setItemMeta(meta);
		return new GuiButton(item, (player, clickType, cursor) -> { gui.show(player, page); }, null);
	}

	@Override
	public @NotNull GuiButton produceNextButton(final @NotNull MultiPageGui gui, final int currentPage, final int pages)
	{
		ItemStack item = HeadUtils.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEzYjhmNjgxZGFhZDhiZjQzNmNhZThkYTNmZTgxMzFmNjJhMTYyYWI4MWFmNjM5YzNlMDY0NGFhNmFiYWMyZiJ9fX0=", ChatColor.GOLD + "Next Page", UUID.randomUUID());
		return new GuiButton(item, (player, clickType, cursor) -> { gui.show(player, (currentPage < pages) ? currentPage + 1 : 1); });
	}

	@Override
	public @NotNull GuiButton producePreviousButton(final @NotNull MultiPageGui gui, final int currentPage, final int pages)
	{
		ItemStack item = HeadUtils.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY1MmUyYjkzNmNhODAyNmJkMjg2NTFkN2M5ZjI4MTlkMmU5MjM2OTc3MzRkMThkZmRiMTM1NTBmOGZkYWQ1ZiJ9fX0=", ChatColor.GOLD + "Previous Page", UUID.randomUUID());
		return new GuiButton(item, (player, clickType, cursor) -> { gui.show(player, (currentPage > 1) ? currentPage - 1 : pages); });
	}
}