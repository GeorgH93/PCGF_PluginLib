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

import at.pcgamingfreaks.Bukkit.IPlatformDependent;
import at.pcgamingfreaks.Bukkit.PlatformResolver;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

interface IInventoryUtils extends IPlatformDependent
{
	IInventoryUtils INSTANCE = PlatformResolver.createPlatformInstance(IInventoryUtils.class);

	String convertItemStackToJson(final @NotNull ItemStack itemStack, final @NotNull Logger logger);

	Object prepareTitleForUpdateInventoryTitle(final @NotNull String title);

	void updateInventoryTitle(final @NotNull Player player, final @NotNull String newTitle);

	void updateInventoryTitlePrepared(final @NotNull Player player, final @NotNull Object newTitle);

	Object prepareTitleForOpenInventoryWithCustomTitle(final @NotNull String title);

	void openInventoryWithCustomTitle(final @NotNull Player player, final @NotNull Inventory inventory, final  @NotNull String title);

	void openInventoryWithCustomTitlePrepared(final @NotNull Player player, final @NotNull Inventory inventory, final  @NotNull Object title);

	Object prepareTitleForSetInventoryTitle(final @NotNull String title);

	Object getInventoryTitle(final @NotNull Inventory inventory);

	void setInventoryTitle(final @NotNull Inventory inventory, final @NotNull String newTitle);

	void setInventoryTitlePrepared(final @NotNull Inventory inventory, final @NotNull Object newTitle);

	@Nullable Inventory getClickedInventory(final @NotNull InventoryClickEvent event);

	@Nullable Inventory getPlayerTopInventory(final @NotNull Player player);
}