/*
 *   Copyright (C) 2014-2015 GeorgH93
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

package at.pcgamingfreaks.Bungee;

import java.io.File;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

public class Language extends at.pcgamingfreaks.Language
{
	protected Plugin plugin;

	/**
	 * @param plugin the instance of the plugin
	 * @param version the current version of the language file
	 */
	public Language(Plugin plugin, int version)
	{
		this(plugin, version, File.separator + "lang", "");
	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version the current version of the language file
	 * @param path the sub-folder for the language file
	 * @param prefix the prefix for the language file
	 */
	public Language(Plugin plugin, int version, String path, String prefix)
	{
		super(plugin.getLogger(), plugin.getDataFolder(), version, path, prefix, "bungee_");
		this.plugin = plugin;
	}
	
	// Getter
	public String getString(String option)
	{
		return ChatColor.translateAlternateColorCodes('&', get(option));
	}
	
	public BaseComponent[] getReady(String option)
	{
		return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', get(option)));
	}
}