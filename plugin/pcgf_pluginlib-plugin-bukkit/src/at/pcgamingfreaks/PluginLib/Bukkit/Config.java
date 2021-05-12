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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.PluginLib.Bukkit;

import at.pcgamingfreaks.Bukkit.Configuration;
import at.pcgamingfreaks.Config.ILanguageConfiguration;
import at.pcgamingfreaks.Updater.IUpdateConfiguration;
import at.pcgamingfreaks.Version;
import at.pcgamingfreaks.YamlFileUpdateMethod;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

final class Config extends Configuration implements IUpdateConfiguration, ILanguageConfiguration
{
	public Config(final @NotNull JavaPlugin plugin, final int version)
	{
		super(plugin, new Version(version));
	}

	@Override
	public @NotNull String getLanguageKey()
	{
		return "Language.Language";
	}

	@Override
	public @NotNull String getLanguageUpdateModeKey()
	{
		return "Language.UpdateMode";
	}

	public YamlFileUpdateMethod getItemLangUpdateMode()
	{
		String mode = yaml.getString("Language.ItemUpdateMode", "overwrite");
		try
		{
			return YamlFileUpdateMethod.valueOf(mode.toUpperCase(Locale.ROOT));
		}
		catch(IllegalArgumentException ignored)
		{
			logger.warning("Failed to read \"Language.ItemUpdateMode\" config option (Invalid value: " + mode + "). Using default value (\"overwrite\").");
		}
		return YamlFileUpdateMethod.UPDATE;
	}
}