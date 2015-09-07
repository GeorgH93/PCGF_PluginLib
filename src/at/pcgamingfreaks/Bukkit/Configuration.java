/*
* Copyright (C) 2014-2015 GeorgH93
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package at.pcgamingfreaks.Bukkit;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Configuration
{
	protected JavaPlugin plugin;
	protected FileConfiguration config;
	
	private String CONFIG_PATH = "config.yml";
	private int CONFIG_VERSION = 0, UPGRADE_THRESHOLD = -1;

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 */
	public Configuration(JavaPlugin plugin, int version)
	{
		this.plugin = plugin;
		CONFIG_VERSION = version;
		loadConfig();
	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 * @param path the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(JavaPlugin plugin, int version, String path)
	{
		this(plugin, version);
		CONFIG_PATH = path;
	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 */
	public Configuration(JavaPlugin plugin, int version, int upgradeThreshold)
	{
		this(plugin, version);
		UPGRADE_THRESHOLD = upgradeThreshold;
	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(JavaPlugin plugin, int version, int upgradeThreshold, String path)
	{
		this(plugin, version, path);
		UPGRADE_THRESHOLD = upgradeThreshold;
	}

	/**
	 * @return true if the config is loaded, false if not
	 */
	public boolean isLoaded()
	{
		return config != null;
	}

	/**
	 * Reloads the config file
	 */
	public void reload()
	{
		loadConfig();
	}

	/**
	 * Allows inheriting classes to implement code for the config upgrade
	 * @param oldVersion the old version of the config
	 * @param oldConfiguration the old config file
	 */
	protected void doUpgrade(int oldVersion, Configuration oldConfiguration) {}

	/**
	 * Allows inheriting classes to implement code for the config update
	 * @param currentVersion the old version of the config
	 */
	protected void doUpdate(int currentVersion) {}
	
	private boolean loadConfig()
	{
		File file = new File(plugin.getDataFolder(), CONFIG_PATH);
		if(!file.exists())
		{
			plugin.getLogger().info("No config found. Create new one ...");
			newConfig();
		}
		try
		{
			config = YamlConfiguration.loadConfiguration(file);
			updateConfig(file);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			config = null;
		}
		return config != null;
	}
	
	private void newConfig()
	{
		try
		{
			if (!plugin.getDataFolder().exists())
			{
				plugin.getDataFolder().mkdir();
	        }
			plugin.saveResource(CONFIG_PATH, true);
            plugin.getLogger().info("Configuration extracted successfully!");
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
	}
	
	private boolean updateConfig(File file)
	{
		if(CONFIG_VERSION > config.getInt("Version"))
		{
			if(UPGRADE_THRESHOLD > 0 && config.getInt("Version") < UPGRADE_THRESHOLD)
			{
				plugin.getLogger().info("Configuration Version: " + config.getInt("Version") + " => Configuration outdated! Upgrading ...");
				upgradeConfig(file);
			}
			else
			{
				plugin.getLogger().info("Configuration Version: " + config.getInt("Version") + " => Configuration outdated! Updating ...");
				doUpdate(config.getInt("Version"));
				config.set("Version", CONFIG_VERSION);
			}
			try
			{
				if(config != null)
				{
					config.save(file);
					plugin.getLogger().warning("Configuration has been updated.");
					return true;
				}
				else
				{
					return false;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				config = null;
			}
		}
		if(CONFIG_VERSION < config.getInt("Version"))
		{
			plugin.getLogger().info("Configuration File Version newer than expected!");
		}
		return false;
	}
	
	private void upgradeConfig(File file)
	{
		try
		{
			int oldVersion = config.getInt("Version");
			File oldConfig = new File(file, ".old" + oldVersion);
			if(oldConfig.exists())
			{
				oldConfig.delete();
			}
			file.renameTo(oldConfig);
			loadConfig();
			doUpgrade(oldVersion, new Configuration(plugin, oldVersion, CONFIG_PATH + ".old" + oldVersion));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			config = null;
		}
	}
	
	// General getter
	public FileConfiguration getConfig()
	{
		return config;
	}
	
	public int getInt(String path)
	{
		return config.getInt(path);
	}
	
	public double getDouble(String path)
	{
		return config.getDouble(path);
	}
	
	public String getString(String path)
	{
		return config.getString(path);
	}
	
	public boolean getBool(String path)
	{
		return config.getBoolean(path);
	}
	
	// General setter
	public void set(String path, String value)
	{
		config.set(path, value);
	}
	
	public void set(String path, int value)
	{
		config.set(path, value);
	}
	
	public void set(String path, double value)
	{
		config.set(path, value);
	}
	
	public void set(String path, boolean value)
	{
		config.set(path, value);
	}
}