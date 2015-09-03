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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;

public class Language
{
	protected JavaPlugin plugin;
	protected FileConfiguration lang = null;
	protected String language = "en", updateMode = "overwrite";
	
	private int LANG_VERSION = 1;

	public Language(JavaPlugin pl, int version) 
	{
		plugin = pl;
		LANG_VERSION = version;
	}
	
	public boolean isLoaded()
	{
		return lang != null;
	}
	
	public String get(String path)
	{
		return lang.getString("Language." + path);
	}
	
	protected void set(String path, String value)
	{
		lang.set(path, value);
	}
	
	public void load(String Language, String UpdateMode)
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
		plugin.getLogger().info("Language Version: " + lang.getInt("Version") + " => " + ((lang.getInt("Version") == LANG_VERSION) ? "no updated needed" : "update needed"));
		if(lang.getInt("Version") != LANG_VERSION)
		{
			if(updateMode.equalsIgnoreCase("overwrite"))
			{
				extractLangFile(file);
				loadFile();
				plugin.getLogger().info(get("Console.LangUpdated"));
				return true;
			}
			else
			{
				if(LANG_VERSION > lang.getInt("Version"))
				{
					doUpdate(lang.getInt("Version"));
					lang.set("Version", LANG_VERSION);
				}
				else
				{
					if(LANG_VERSION < lang.getInt("Version"))
					{
						plugin.getLogger().warning("Language File Version newer than expected!");
					}
					return false;
				}
				try
				{
					lang.save(file);
					plugin.getLogger().info(get("Console.LangUpdated"));
				}
		  	  	catch (Exception e) 
		  	  	{
		  	  		e.printStackTrace();
		  	  	}
				return true;
			}
		}
		return false;
	}
	
	protected void doUpdate(int currentVersion) {}
}