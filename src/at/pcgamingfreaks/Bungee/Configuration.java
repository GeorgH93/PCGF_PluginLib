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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Configuration
{
	protected Plugin plugin;
	protected net.md_5.bungee.config.Configuration config;
	
	private ConfigurationProvider configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
	private final String CONFIG_PATH;
	private final int CONFIG_VERSION, UPGRADE_THRESHOLD;
	private final File CONFIG_FILE;

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
		this.plugin = plugin;
		CONFIG_VERSION = version;
		CONFIG_PATH = path;
		UPGRADE_THRESHOLD = upgradeThreshold;

		CONFIG_FILE = new File(plugin.getDataFolder(), CONFIG_PATH);
		loadConfig();
	}

	/**
	 * Reloads the config file
	 */
	public void reload()
	{
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
	 * Allows inheriting classes to implement code for the config upgrade
	 * @param oldConfiguration the old config file
	 */
	protected void doUpgrade(Configuration oldConfiguration)
	{
		plugin.getLogger().info("No custom config upgrade code implemented! Copying all data from old config to new one.");
		Collection<String> keys = oldConfiguration.getConfig().getKeys();
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

	public void saveConfig()
	{
		try
		{
			configProvider.save(config, CONFIG_FILE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void loadConfig()
	{
		try
		{
			if(!CONFIG_FILE.exists())
			{
				newConfig();
			}
			else
			{
				config = configProvider.load(CONFIG_FILE);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			config = null;
		}
		updateConfig(CONFIG_FILE);
	}
	
	private void newConfig()
	{
		try
		{
			if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdir())
			{
				plugin.getLogger().warning("Couldn't create directory. " + plugin.getDataFolder().toString());
	        }
			if (!CONFIG_FILE.createNewFile())
			{
				plugin.getLogger().warning("Couldn't create config file. " + CONFIG_FILE.toString());
			}
            try (InputStream is = plugin.getResourceAsStream("bungee_" + CONFIG_PATH); OutputStream os = new FileOutputStream(CONFIG_FILE))
            {
	            ByteStreams.copy(is, os);
            }
            plugin.getLogger().info("Configuration extracted successfully!");
			config = configProvider.load(CONFIG_FILE);
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
	
	private boolean updateConfig(File file)
	{
		if(CONFIG_VERSION > getVersion())
		{
			if(UPGRADE_THRESHOLD > 0 && getVersion() < UPGRADE_THRESHOLD)
			{
				plugin.getLogger().info("Configuration Version: " + config.getInt("Version") + " => Configuration outdated! Upgrading ...");
				upgradeConfig();
			}
			else
			{
				plugin.getLogger().info("Configuration Version: " + config.getInt("Version") + " => Configuration outdated! Updating ...");
				doUpdate();
				config.set("Version", CONFIG_VERSION);
			}
			try
			{
				configProvider.save(config, file);
				plugin.getLogger().info("Configuration File has been updated.");
				return true;
			}
			catch (IOException e)
			{
				e.printStackTrace();
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
			int oldVersion = config.getInt("Version");
			File oldConfig = new File(CONFIG_FILE + ".old_v" + oldVersion);
			if(oldConfig.exists() && !oldConfig.delete())
			{
				plugin.getLogger().warning("Failed to delete old config backup!");
			}
			if(!CONFIG_FILE.renameTo(oldConfig))
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
	public net.md_5.bungee.config.Configuration getConfig()
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