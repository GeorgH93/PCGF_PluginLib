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

import net.md_5.bungee.api.plugin.Plugin;

public class Configuration extends at.pcgamingfreaks.Configuration
{
	protected Plugin plugin;

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 */
	public Configuration(Plugin plugin, int version)
	{
		this(plugin, version, -1, "config.yml");
	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 * @param path the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(Plugin plugin, int version, String path)
	{
		this(plugin, version, -1, path);

	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 */
	public Configuration(Plugin plugin, int version, int upgradeThreshold)
	{
		this(plugin, version, upgradeThreshold, "config.yml");
	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(Plugin plugin, int version, int upgradeThreshold, String path)
	{
		super(plugin.getLogger(), plugin.getDataFolder(), version, upgradeThreshold, path);
		this.plugin = plugin;
	}
}