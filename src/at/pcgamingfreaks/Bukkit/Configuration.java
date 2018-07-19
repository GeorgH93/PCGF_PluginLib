/*
 *   Copyright (C) 2014-2016, 2018 GeorgH93
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

package at.pcgamingfreaks.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration extends at.pcgamingfreaks.Configuration
{
	protected JavaPlugin plugin;

	/**
	 * @param plugin  the instance of the plugin
	 * @param version current version of the config
	 */
	public Configuration(JavaPlugin plugin, int version)
	{
		this(plugin, version, -1, "config.yml");
	}

	/**
	 * @param plugin  the instance of the plugin
	 * @param version current version of the config
	 * @param path    the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(JavaPlugin plugin, int version, String path)
	{
		this(plugin, version, -1, path);

	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 */
	public Configuration(JavaPlugin plugin, int version, int upgradeThreshold)
	{
		this(plugin, version, upgradeThreshold, "config.yml");
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(JavaPlugin plugin, int version, int upgradeThreshold, String path)
	{
		this(plugin, version, upgradeThreshold, path, "");
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 * @param inJarPrefix      the prefix for the config file within the jar (e.g.: bungee_)
	 */
	public Configuration(JavaPlugin plugin, int version, int upgradeThreshold, String path, String inJarPrefix)
	{
		super(plugin.getLogger(), plugin.getDataFolder(), version, upgradeThreshold, path, inJarPrefix);
		this.plugin = plugin;
	}

	/**
	 * Checks if the used bukkit version supports UUIDs
	 *
	 * @return true if the used bukkit version is uuid compatible, false if not
	 */
	protected boolean isBukkitVersionUUIDCompatible()
	{
		try
		{
			String[] GameVersion = Bukkit.getBukkitVersion().split("-");
			GameVersion = GameVersion[0].split("\\.");
			return (Integer.parseInt(GameVersion[0]) > 1 || Integer.parseInt(GameVersion[1]) > 7 || (Integer.parseInt(GameVersion[1]) == 7 && Integer.parseInt(GameVersion[2]) > 5));
		}
		catch(Exception e)
		{
			return false;
		}
	}
}