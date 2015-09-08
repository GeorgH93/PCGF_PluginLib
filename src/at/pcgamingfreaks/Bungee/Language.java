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

import at.pcgamingfreaks.LanguageUpdateMethod;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Language
{
	protected Plugin plugin;
	protected Configuration lang = null;
	protected String language = "en";

	private LanguageUpdateMethod updateMode = LanguageUpdateMethod.OVERWRITE;
	private ConfigurationProvider langprovider;
	private String PATH = File.separator + "lang", PREFIX = "";
	private int LANG_VERSION = 1;

	/**
	 * @param plugin the instance of the plugin
	 * @param version the current version of the language file
	 */
	public Language(Plugin plugin, int version)
	{
		this.plugin = plugin;
		LANG_VERSION = version;
		langprovider = ConfigurationProvider.getProvider(YamlConfiguration.class);
	}

	/**
	 * @param plugin the instance of the plugin
	 * @param version the current version of the language file
	 * @param path the sub-folder for the language file
	 * @param prefix the prefix for the language file
	 */
	public Language(Plugin plugin, int version, String path, String prefix)
	{
		this(plugin, version);
		PATH = path;
		PREFIX = prefix;
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
		return lang.getString("Language." + path);
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
		loadLang();
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
		loadLang();
	}
	
	private void loadLang()
	{
		File file = new File(plugin.getDataFolder() + PATH, PREFIX + language + ".yml");
		if(!file.exists())
		{
			extractLangFile(file);
		}
		try
		{
			lang = langprovider.load(file);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		updateLangFile(file);
	}
	
	private void extractLangFile(File file)
	{
		try
		{
			if(file.exists())
			{
				file.delete();
	        }
            file.createNewFile();
            try (InputStream is = plugin.getResourceAsStream("lang/bungee_" + language + ".yml"); OutputStream os = new FileOutputStream(file))
            {
                ByteStreams.copy(is, os);
            }
            catch(Exception e)
            {
            	try (InputStream is = plugin.getResourceAsStream("lang/bungee_en.yml"); OutputStream os = new FileOutputStream(file))
                {
                    ByteStreams.copy(is, os);
                }
            }
            plugin.getLogger().info("Language file extracted successfully!");
        }
		catch (IOException e)
		{
            e.printStackTrace();
        }
	}
	
	private boolean updateLangFile(File file)
	{
		if(lang.getInt("Version") < LANG_VERSION)
		{
			if(updateMode == LanguageUpdateMethod.OVERWRITE)
			{
				extractLangFile(file);
				loadLang();
				plugin.getLogger().info(getString("Language file has been updated."));
			}
			else
			{
				doUpdate(lang.getInt("Version"));
				lang.set("Version", LANG_VERSION);
				try
				{
					langprovider.save(lang, file);
					plugin.getLogger().info("Language file has been updated.");
				}
				catch(IOException e)
				{
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
		if(LANG_VERSION < lang.getInt("Version"))
		{
			plugin.getLogger().warning("Language file version newer than expected!");
		}
		return false;
	}
	
	protected void doUpdate(int currentVersion) {}
	
	//Geter
	public String getString(String Option)
	{
		return ChatColor.translateAlternateColorCodes('&', lang.getString("Language." + Option));
	}
	
	public BaseComponent[] getReady(String Option)
	{
		return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', lang.getString("Language." + Option)));
	}
}