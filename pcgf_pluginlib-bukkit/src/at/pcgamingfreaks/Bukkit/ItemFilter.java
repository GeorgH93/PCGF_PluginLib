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

package at.pcgamingfreaks.Bukkit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ItemFilter
{
	private final boolean whitelistMode;
	private final Set<MinecraftMaterial> filteredMaterials = new HashSet<>();
	private final Set<String> filteredNames = new HashSet<>(), filteredLore = new HashSet<>();

	public ItemFilter(final boolean whitelistMode)
	{
		this.whitelistMode = whitelistMode;
	}

	public void addFilteredNames(Collection<String> filteredNamesToBeAdded)
	{
		filteredNames.addAll(filteredNamesToBeAdded);
	}

	public void addFilteredLore(Collection<String> filteredLoreToBeAdded)
	{
		this.filteredLore.addAll(filteredLoreToBeAdded);
	}

	public void addFilteredMaterials(final Collection<MinecraftMaterial> filteredMaterialsToBeAdded)
	{
		this.filteredMaterials.addAll(filteredMaterialsToBeAdded);
	}

	public boolean isItemBlocked(final @Nullable ItemStack item)
	{
		if(item == null) return false;
		if(filteredMaterials.contains(new MinecraftMaterial(item))) return !whitelistMode;
		if(item.hasItemMeta())
		{
			ItemMeta meta = item.getItemMeta();
			if(meta.hasDisplayName() && filteredNames.contains(meta.getDisplayName())) return !whitelistMode;
			if(meta.hasLore() && !filteredLore.isEmpty())
			{
				StringBuilder loreBuilder = new StringBuilder();
				for(String loreLine : meta.getLore())
				{
					if(filteredLore.contains(loreLine)) return !whitelistMode;
					if(loreBuilder.length() > 0) loreBuilder.append("\n");
					loreBuilder.append(loreLine);
				}
				if(filteredLore.contains(loreBuilder.toString())) return !whitelistMode;
			}
		}
		return whitelistMode;
	}
}