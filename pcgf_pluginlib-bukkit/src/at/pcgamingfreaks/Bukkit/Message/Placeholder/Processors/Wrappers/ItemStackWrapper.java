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

package at.pcgamingfreaks.Bukkit.Message.Placeholder.Processors.Wrappers;

import at.pcgamingfreaks.Bukkit.ItemNameResolver;
import at.pcgamingfreaks.Bukkit.Util.InventoryUtils;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

import java.util.logging.Logger;

public class ItemStackWrapper
{
	@Getter private final @NotNull ItemStack itemStack;
	private final @NotNull Logger logger;
	private final @NotNull ItemNameResolver itemNameResolver;
	private String cachedName = null, cachedDisplayName = null, cachedMetadata = null;

	public ItemStackWrapper(final @NotNull ItemStack itemStack, final @NotNull Logger logger, final @NotNull ItemNameResolver resolver)
	{
		this.itemStack = itemStack;
		this.logger = logger;
		this.itemNameResolver = resolver;
	}

	public String getItemName()
	{
		if (cachedName == null)
		{
			cachedName = itemNameResolver.getName(itemStack);
		}
		return cachedName;
	}

	public String getItemDisplayName()
	{
		if (cachedDisplayName == null)
		{
			cachedDisplayName = itemNameResolver.getDisplayName(itemStack);
		}
		return cachedDisplayName;
	}

	public String getItemMetadata()
	{
		if (cachedMetadata == null)
		{
			cachedMetadata = InventoryUtils.convertItemStackToJson(itemStack, logger);
		}
		return cachedMetadata;
	}

	public int getAmount()
	{
		return itemStack.getAmount();
	}
}