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

package at.pcgamingfreaks.PluginLib.Bukkit;

import at.pcgamingfreaks.Config.ILanguageConfiguration;
import at.pcgamingfreaks.Plugin.IPlugin;

import org.jetbrains.annotations.NotNull;

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
		super.load(plugin, plugin.getConfiguration());
		ItemNameResolver.instance = this;
	}

	@Override
	public void load(@NotNull IPlugin plugin, @NotNull ILanguageConfiguration configuration)
	{
		// Prevent other plugins from loading item names into the shared ItemNameResolver
	}
}