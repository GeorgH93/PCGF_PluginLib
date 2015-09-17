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

import java.io.*;
import java.util.Set;

import at.pcgamingfreaks.LanguageUpdateMethod;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration
{
	protected JavaPlugin plugin;
	protected FileConfiguration config;
	
	private final String CONFIG_PATH;
	private final int CONFIG_VERSION, UPGRADE_THRESHOLD;
	private final File configFile;

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 */
	public Configuration(JavaPlugin plugin, int version)
	{
		this(plugin, version, -1, "/config.yml");
	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 * @param path the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(JavaPlugin plugin, int version, String path)
	{
		this(plugin, version, -1, path);

	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 */
	public Configuration(JavaPlugin plugin, int version, int upgradeThreshold)
	{
		this(plugin, version, upgradeThreshold, "config.yml");
	}

	/**
	 * @param javaPlugin the instance of the plugin
	 * @param version current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(JavaPlugin javaPlugin, int version, int upgradeThreshold, String path)
	{
		plugin = javaPlugin;
		UPGRADE_THRESHOLD = upgradeThreshold;
		CONFIG_VERSION = version;
		CONFIG_PATH = path;

		configFile = new File(plugin.getDataFolder(), CONFIG_PATH);
		loadConfig();
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
	 * Allows inheriting classes to implement own code for the config upgrade
	 * If no special code is implemented all keys will be copied 1:1 into the new config file
	 * @param oldConfiguration the old config file
	 */
	protected void doUpgrade(Configuration oldConfiguration)
	{
		plugin.getLogger().info("No custom config upgrade code implemented! Copying all data from old config to new one.");
		Set<String> keys = oldConfiguration.getConfig().getKeys(true);
		for(String key : keys)
		{
			if(key.equals("Version")) continue;
			config.set(key, oldConfiguration.getConfig().get(key));
		}
	}

	/**
	 * Allows inheriting classes to implement code for the config update
	 */
	protected void doUpdate()
	{
		plugin.getLogger().info("No config update code implemented! Just updating version!");
	}

	/**
	 * Allows inheriting classes to implement code for setting config values in new created config files
	 * @return if values in the config have been changed
	 */
	protected boolean newConfigCreated() { return false; }

	/**
	 * Checks if the used bukkit version supports UUIDs
	 * @return if the used bukkit version is uuid compatible
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

	public void saveConfig()
	{
		try
		{
			config.save(configFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private boolean loadConfig()
	{
		if(!configFile.exists())
		{
			plugin.getLogger().info("No config found. Create new one ...");
			newConfig();
		}
		else
		{
			try
			{
				config = YamlConfiguration.loadConfiguration(configFile);
				updateConfig();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				config = null;
			}
		}
		return config != null;
	}
	
	private void newConfig()
	{
		try
		{
			if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdir())
			{
				plugin.getLogger().warning("Couldn't create directory. " + plugin.getDataFolder().toString());
			}
			plugin.saveResource(CONFIG_PATH, true);
            plugin.getLogger().info("Configuration extracted successfully!");
			config = YamlConfiguration.loadConfiguration(configFile);
			if(newConfigCreated())
			{
				saveConfig();
			}
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
	}
	
	private boolean updateConfig()
	{
		if(CONFIG_VERSION > getVersion())
		{
			if(UPGRADE_THRESHOLD > 0 && getVersion() < UPGRADE_THRESHOLD)
			{
				plugin.getLogger().info("Configuration Version: " + getVersion() + " => Configuration outdated! Upgrading ...");
				upgradeConfig();
			}
			else
			{
				plugin.getLogger().info("Configuration Version: " + getVersion() + " => Configuration outdated! Updating ...");
				doUpdate();
				config.set("Version", CONFIG_VERSION);
			}
			try
			{
				if(config != null)
				{
					config.save(configFile);
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
		if(CONFIG_VERSION < getVersion())
		{
			plugin.getLogger().info("Configuration File Version newer than expected!");
		}
		return false;
	}
	
	private void upgradeConfig()
	{
		try
		{
			int oldVersion = getVersion();
			File oldConfig = new File(configFile + ".old_v" + oldVersion);
			if(oldConfig.exists() && !oldConfig.delete())
			{
				plugin.getLogger().warning("Failed to delete old config backup!");
			}
			if(!configFile.renameTo(oldConfig))
			{
				plugin.getLogger().warning("Failed to rename old config! Could not do upgrade!");
				return;
			}
			loadConfig();
			doUpgrade(new Configuration(plugin, oldVersion, CONFIG_PATH + ".old_v" + oldVersion));
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

	public int getVersion()
	{
		return config.getInt("Version");
	}

	// Getter for language settings
	public String getLanguage()
	{
		return config.getString("Language");
	}

	public LanguageUpdateMethod getLanguageUpdateMode()
	{
		return ((config.getString("LanguageUpdateMode").equalsIgnoreCase("overwrite")) ? LanguageUpdateMethod.OVERWRITE : LanguageUpdateMethod.UPDATE);
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