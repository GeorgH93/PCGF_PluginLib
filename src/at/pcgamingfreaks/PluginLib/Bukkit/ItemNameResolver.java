/*
 *   Copyright (C) 2017, 2018 GeorgH93
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

package at.pcgamingfreaks.PluginLib.Bukkit;

import at.pcgamingfreaks.Bukkit.Language;
import at.pcgamingfreaks.YamlFileUpdateMethod;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

/**
 * This class allows to translate minecraft items names.
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
		super();
		instance = this;
		Language itemNameLanguage = new Language(plugin, 1, File.separator + "lang", "items_");
		itemNameLanguage.load("en", YamlFileUpdateMethod.UPGRADE);
		super.load(itemNameLanguage, plugin.getLogger());
	}

	@Override
	public at.pcgamingfreaks.Bukkit.ItemNameResolver load(@NotNull Language language, @NotNull Logger logger)
	{
		// Prevents other plugins from loading item names in the shared ItemNameResolver
		return this;
	}
}