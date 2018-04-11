/*
 *   Copyright (C) 2017 GeorgH93
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

import at.pcgamingfreaks.Bukkit.Configuration;
import at.pcgamingfreaks.YamlFileUpdateMethod;

import org.bukkit.plugin.java.JavaPlugin;

final class Config extends Configuration
{
	public Config(JavaPlugin plugin, int version)
	{
		super(plugin, version);
		languageKey = "Language.Language";
		languageUpdateKey = "Language.UpdateMode";
	}

	public YamlFileUpdateMethod getItemLangUpdateMode()
	{
		String mode = yaml.getString("Language.ItemUpdateMode", "overwrite");
		try
		{
			return YamlFileUpdateMethod.valueOf(mode.toUpperCase());
		}
		catch(IllegalArgumentException ignored)
		{
			logger.warning("Failed to read \"Language.ItemUpdateMode\" config option (Invalid value: " + mode + "). Using default value (\"overwrite\").");
		}
		return YamlFileUpdateMethod.UPDATE;
	}
}