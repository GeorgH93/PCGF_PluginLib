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

import com.google.common.io.Files;

public class Config
{
	protected JavaPlugin plugin;
	protected FileConfiguration config;
	
	private String CONFIG_PATH = "config.yml";
	private int CONFIG_VERSION = 0, UPGRADE_THRESHOLD = -1;
	
	public Config(JavaPlugin pl, int version)
	{
		plugin = pl;
		CONFIG_VERSION = version;
		loadConfig();
	}
	
	public Config(JavaPlugin pl, int version, String path)
	{
		this(pl, version);
		CONFIG_PATH = path;
	}
	
	public Config(JavaPlugin pl, int version, int upgradeThreshold)
	{
		this(pl, version);
		UPGRADE_THRESHOLD = upgradeThreshold;
	}
	
	public Config(JavaPlugin pl, int version, int upgradeThreshold, String path)
	{
		this(pl, version, path);
		UPGRADE_THRESHOLD = upgradeThreshold;
	}
	
	public boolean isLoaded()
	{
		return config != null;
	}
	
	public void reload()
	{
		loadConfig();
	}
	
	protected void doUpgrade(int oldVersion, Config oldConfig) {}
	
	protected void doUpdate(int currentVersion) {}
	
	private boolean loadConfig()
	{
		File file = new File(plugin.getDataFolder(), CONFIG_PATH);
		if(!file.exists())
		{
			plugin.getLogger().info("No config found. Create new one ...");
			newConfig(file);
		}
		else
		{
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
		}
		return config != null;
	}
	
	private void newConfig(File file)
	{
		try
		{
			if (!plugin.getDataFolder().exists())
			{
				plugin.getDataFolder().mkdir();
	        }
			plugin.saveResource(CONFIG_PATH, true);
            plugin.getLogger().info("Config extracted successfully!");
        }
		catch (Exception e)
		{
            e.printStackTrace();
            config = null;
        }
	}
	
	private boolean updateConfig(File file)
	{
		if(CONFIG_VERSION > config.getInt("Version"))
		{
			if(UPGRADE_THRESHOLD > 0 && config.getInt("Version") < UPGRADE_THRESHOLD)
			{
				plugin.getLogger().info("Config Version: " + config.getInt("Version") + " => Config outdated! Upgrading ...");
				upgradeConfig(file);
			}
			else
			{
				plugin.getLogger().info("Config Version: " + config.getInt("Version") + " => Config outdated! Updating ...");
				doUpdate(config.getInt("Version"));
				config.set("Version", CONFIG_VERSION);
			}
		}
		else
		{
			if(CONFIG_VERSION < config.getInt("Version"))
			{
				plugin.getLogger().info("Config File Version newer than expected!");
			}
			return false;
		}
		try 
		{
			if(config != null)
			{
				config.save(file);
				plugin.getLogger().warning("Config has been updated.");
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
  	  		return false;
  	  	}
		return true;
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
			Files.move(file, oldConfig);
			loadConfig();
			doUpgrade(oldVersion, new Config(plugin, oldVersion, CONFIG_PATH + ".old" + oldVersion));
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