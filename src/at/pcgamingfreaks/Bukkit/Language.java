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

package at.pcgamingfreaks.Bukkit;

import java.io.File;
import java.io.IOException;

import at.pcgamingfreaks.LanguageUpdateMethod;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;

public class Language
{
	protected JavaPlugin plugin;
	protected FileConfiguration lang = null;
	protected String language = "en";

	private LanguageUpdateMethod updateMode = LanguageUpdateMethod.OVERWRITE;
	private int LANG_VERSION = 1;

	/**
	 * @param plugin the instance of the plugin
	 * @param version the current version of the language file
	 */
	public Language(JavaPlugin plugin, int version)
	{
		this.plugin = plugin;
		LANG_VERSION = version;
	}

	/**
	 * @return true if a language file is loaded, false if not
	 */
	public boolean isLoaded()
	{
		return lang != null;
	}

	/**
	 * @param path the path to the searched language value
	 * @return returns the language data
	 */
	public String get(String path)
	{
		return lang.getString("Language." + path, ChatColor.RED + "Message not found!");
	}

	/**
	 * @param path the path to the searched language value
	 * @return returns the language data
	 */
	public String getTranslated(String path)
	{
		return ChatColor.translateAlternateColorCodes('&', get(path));
	}
	
	protected void set(String path, String value)
	{
		lang.set(path, value);
	}

	/**
	 * Loads the language file
	 * @param Language the language to load
	 * @param UpdateMode how the language file should be updated
	 */
	public void load(String Language, String UpdateMode)
	{
		language = Language;
		if(UpdateMode.equalsIgnoreCase("overwrite"))
		{
			updateMode = LanguageUpdateMethod.OVERWRITE;
		}
		else
		{
			updateMode = LanguageUpdateMethod.UPDATE;
		}
		loadFile();
	}

	/**
	 * Loads the language file
	 * @param Language the language to load
	 * @param UpdateMode how the language file should be updated
	 */
	public void load(String Language, LanguageUpdateMethod UpdateMode)
	{
		language = Language;
		updateMode = UpdateMode;
		loadFile();
	}
	
	private void loadFile()
	{
		File file = new File(plugin.getDataFolder() + File.separator + "lang", language + ".yml");
		if(!file.exists())
		{
			extractLangFile(file);
		}
		lang = YamlConfiguration.loadConfiguration(file);
		updateLangFile(file);
	}
	
	private void extractLangFile(File Target)
	{
		try
		{
			plugin.saveResource("lang" + File.separator + language + ".yml", true);
		}
		catch(Exception ex)
		{
			try
			{
				File file_en = new File(plugin.getDataFolder() + File.separator + "lang", "en.yml");
				if(!file_en.exists())
				{
					plugin.saveResource("lang" + File.separator + "en.yml", true);
				}
				Files.copy(file_en, Target);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private boolean updateLangFile(File file)
	{
		if(lang.getInt("Version") < LANG_VERSION)
		{
			plugin.getLogger().info("Language version: " + lang.getInt("Version") + " => Language outdated! Updating ...");
			if(updateMode == LanguageUpdateMethod.OVERWRITE)
			{
				extractLangFile(file);
				loadFile();
				plugin.getLogger().info(get("Language file has been updated."));
				return true;
			}
			else
			{
				doUpdate(lang.getInt("Version"));
				lang.set("Version", LANG_VERSION);
				try
				{
					lang.save(file);
					plugin.getLogger().info(get("Language file has been updated."));
					return true;
				}
		  	  	catch (Exception e) 
		  	  	{
		  	  		e.printStackTrace();
		  	  	}
			}
		}
		if(LANG_VERSION < lang.getInt("Version"))
		{
			plugin.getLogger().warning("Language file version newer than expected!");
		}
		return false;
	}
	
	protected void doUpdate(int currentVersion) {}
}