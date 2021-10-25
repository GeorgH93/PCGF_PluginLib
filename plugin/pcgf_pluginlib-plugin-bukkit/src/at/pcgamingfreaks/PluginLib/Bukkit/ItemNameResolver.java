/*
 *   Copyright (C) 2019 GeorgH93
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

package at.pcgamingfreaks.PluginLib.Bukkit;

import at.pcgamingfreaks.Bukkit.Language;
import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

/**
 * This class allows translating minecraft items names.
 */
public final class ItemNameResolver extends at.pcgamingfreaks.Bukkit.ItemNameResolver
{
	//region static stuff
	private static ItemNameResolver instance = null;

	public static ItemNameResolver getInstance()
	{
		return instance;
	}
	//endregion

	/**
	 * Packet local so that only the lib itself can create an instance.
	 */
	ItemNameResolver(@NotNull PluginLib plugin)
	{
		if(MCVersion.isOlderThan(MCVersion.MC_1_13))
		{
			Language itemNameLanguage = new Language(plugin, new Version(1), new Version(1), File.separator + "lang", "items_", "legacy_items_");
			itemNameLanguage.setFileDescription("item name language");
			itemNameLanguage.load(plugin.getConfiguration());
			super.loadLegacy(itemNameLanguage, plugin.getLogger());
		}
		else
		{
			Language itemNameLanguage = new Language(plugin, new Version(2), File.separator + "lang", "items_");
			itemNameLanguage.setFileDescription("item name language");
			itemNameLanguage.load(plugin.getConfiguration());
			super.load(itemNameLanguage, plugin.getLogger());
		}
		ItemNameResolver.instance = this;
	}

	@Override
	public void load(@NotNull Language language, @NotNull Logger logger)
	{
		// Prevents other plugins from loading item names in the shared ItemNameResolver
	}

	@Override
	public void loadLegacy(@NotNull Language language, @NotNull Logger logger)
	{
		// Prevents other plugins from loading item names in the shared ItemNameResolver
	}
}