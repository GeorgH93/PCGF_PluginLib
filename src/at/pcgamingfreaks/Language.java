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

package at.pcgamingfreaks;

import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YAMLNotInitializedException;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.logging.Logger;

public class Language
{
	protected Logger log;
	protected YAML lang = null;
	protected String language = "en";

	private LanguageUpdateMethod updateMode = LanguageUpdateMethod.OVERWRITE;
	private final String PATH, PREFIX, IN_JAR_PREFIX;
	private final File BASE_DIR;
	private File langFile;
	private final int LANG_VERSION;

	/**
	 * @param logger  The logger instance of the plugin
	 * @param baseDir The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version the current version of the language file
	 */
	public Language(Logger logger, File baseDir, int version)
	{
		this(logger, baseDir, version, File.separator + "lang", "");
	}

	/**
	 * @param logger  The logger instance of the plugin
	 * @param baseDir The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version the current version of the language file
	 * @param path    the sub-folder for the language file
	 * @param prefix  the prefix for the language file
	 */
	public Language(Logger logger, File baseDir, int version, String path, String prefix)
	{
		this(logger, baseDir, version, path, prefix, "");
	}

	/**
	 * @param logger      The logger instance of the plugin
	 * @param baseDir     The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version     the current version of the language file
	 * @param path        the sub-folder for the language file
	 * @param prefix      the prefix for the language file
	 * @param inJarPrefix the prefix for the language file within the jar (e.g.: bungee_)
	 */
	public Language(Logger logger, File baseDir, int version, String path, String prefix, String inJarPrefix)
	{
		log = logger;
		BASE_DIR = baseDir;
		LANG_VERSION = version;
		PATH = path;
		PREFIX = prefix;
		IN_JAR_PREFIX = inJarPrefix;
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
		return lang.getString("Language." + path, "§cMessage not found!");
	}

	public int getVersion()
	{
		return lang.getInt("Version", -1);
	}

	protected void set(String path, String value)
	{
		lang.set(path, value);
	}

	/**
	 * Loads the language file
	 *
	 * @param Language   the language to load
	 * @param UpdateMode how the language file should be updated
	 */
	public void load(String Language, String UpdateMode)
	{
		language = Language;
		updateMode = (UpdateMode.equalsIgnoreCase("overwrite")) ? LanguageUpdateMethod.OVERWRITE : LanguageUpdateMethod.UPDATE;
		loadLang();
	}

	/**
	 * Loads the language file
	 *
	 * @param Language   the language to load
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
		langFile = new File(BASE_DIR + PATH, PREFIX + language + ".yml");
		if(!langFile.exists())
		{
			extractLangFile();
		}
		try
		{
			lang = new YAML(langFile);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		updateLang();
	}

	private void extractLangFile()
	{
		try
		{
			if(langFile.exists() && !langFile.delete())
			{
				log.info("Failed deleting old language file.");
			}
			if(!langFile.createNewFile())
			{
				log.info("Failed create new language file.");
			}
			try(InputStream is = getClass().getResourceAsStream("/lang/" + IN_JAR_PREFIX + language + ".yml"); OutputStream os = new FileOutputStream(langFile))
			{
				ByteStreams.copy(is, os);
				os.flush();
				is.close();
				os.close();
			}
			catch(Exception e)
			{
				try(InputStream is = getClass().getResourceAsStream("/lang/" + IN_JAR_PREFIX + "en.yml"); OutputStream os = new FileOutputStream(langFile))
				{
					ByteStreams.copy(is, os);
					os.flush();
					is.close();
					os.close();
				}
			}
			log.info("Language file extracted successfully!");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private boolean updateLang()
	{
		if(getVersion() < LANG_VERSION)
		{
			log.info("Language version: " + getVersion() + " => Language outdated! Updating ...");
			if(updateMode == LanguageUpdateMethod.OVERWRITE)
			{
				extractLangFile();
				loadLang();
				log.info("Language file has been updated.");
			}
			else
			{
				doUpdate();
				lang.set("Version", LANG_VERSION);
				try
				{
					save();
					log.info("Language file has been updated.");
				}
				catch(Exception e)
				{
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
		if(LANG_VERSION < getVersion())
		{
			log.warning("Language file version newer than expected!");
		}
		return false;
	}

	protected void doUpdate() {}

	public void save() throws FileNotFoundException, YAMLNotInitializedException
	{
		lang.save(langFile);
	}
}