/*
 *   Copyright (C) 2014-2016 GeorgH93
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
	protected final Logger logger; // The logger instance of the using plugin
	protected YAML lang = null;
	protected String language = "en";

	private LanguageUpdateMethod updateMode = LanguageUpdateMethod.OVERWRITE;
	private final String prefix, inJarPrefix, path;
	private final File baseDir;
	private File langFile;
	private final int expectedVersion, upgradeThreshold;

	//region constructors
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
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 */
	public Language(Logger logger, File baseDir, int version, int upgradeThreshold)
	{
		this(logger, baseDir, version, upgradeThreshold, File.separator + "lang", "");
	}

	/**
	 * @param logger  The logger instance of the plugin
	 * @param baseDir The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version The current version of the language file
	 * @param path    The sub-folder for the language file
	 * @param prefix  The prefix for the language file
	 */
	public Language(Logger logger, File baseDir, int version, String path, String prefix)
	{
		this(logger, baseDir, version, path, prefix, "");
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 * @param path             The sub-folder for the language file
	 * @param prefix           The prefix for the language file
	 */
	public Language(Logger logger, File baseDir, int version, int upgradeThreshold, String path, String prefix)
	{
		this(logger, baseDir, version, upgradeThreshold, path, prefix, "");
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
		this(logger, baseDir, version, -1, path, prefix, inJarPrefix);
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 * @param path             The sub-folder for the language file
	 * @param prefix           The prefix for the language file
	 * @param inJarPrefix      The prefix for the language file within the jar (e.g.: bungee_)
	 */
	public Language(Logger logger, File baseDir, int version, int upgradeThreshold, String path, String prefix, String inJarPrefix)
	{
		this.logger = logger;
		this.baseDir = new File(baseDir, path);
		this.path = path;
		expectedVersion = version;
		this.upgradeThreshold = upgradeThreshold;
		this.prefix = prefix;
		this.inJarPrefix = inJarPrefix;
	}

	private Language(Logger logger, File baseDir, int version, int upgradeThreshold, String path, String prefix, String inJarPrefix, YAML yaml)
	{
		this(logger, baseDir, version, upgradeThreshold, path, prefix, inJarPrefix);
		lang = yaml;
	}
	//endregion

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
	 * @param language   the language to load
	 * @param updateMode how the language file should be updated
	 * @return True if it's loaded successfully. False if not.
	 */
	public boolean load(String language, String updateMode)
	{
		return load(language, (updateMode.equalsIgnoreCase("overwrite")) ? LanguageUpdateMethod.OVERWRITE : LanguageUpdateMethod.UPDATE);
	}

	/**
	 * Loads the language file
	 *
	 * @param language   the language to load
	 * @param updateMode how the language file should be updated
	 * @return True if it's loaded successfully. False if not.
	 */
	public boolean load(String language, LanguageUpdateMethod updateMode)
	{
		this.language = language;
		this.updateMode = updateMode;
		loadLang();
		return isLoaded();
	}

	private void loadLang()
	{
		langFile = new File(baseDir, prefix + language + ".yml");
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
				logger.info("Failed deleting old language file.");
			}
			if(!baseDir.exists() && !baseDir.mkdirs())
			{
				logger.info("Failed creating directory's!");
			}
			if(!langFile.createNewFile())
			{
				logger.info("Failed create new language file.");
			}
			try(InputStream is = getClass().getResourceAsStream("/lang/" + inJarPrefix + language + ".yml"); OutputStream os = new FileOutputStream(langFile))
			{
				ByteStreams.copy(is, os);
				os.flush();
			}
			catch(Exception e)
			{
				try(InputStream is = getClass().getResourceAsStream("/lang/" + inJarPrefix + "en.yml"); OutputStream os = new FileOutputStream(langFile))
				{
					ByteStreams.copy(is, os);
					os.flush();
				}
			}
			logger.info("Language file extracted successfully!");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private boolean updateLang()
	{
		if(getVersion() < expectedVersion)
		{
			if(getVersion() < upgradeThreshold || updateMode == LanguageUpdateMethod.UPGRADE)
			{
				logger.info("Language version: " + getVersion() + " => Language outdated! Upgrading ...");
				try
				{
					int oldVersion = getVersion();
					File oldLang = new File(langFile + ".old_v" + oldVersion);
					if(oldLang.exists() && !oldLang.delete())
					{
						logger.warning("Failed to delete old language file backup!");
						return false;
					}
					if(!langFile.renameTo(oldLang))
					{
						logger.warning("Failed to rename old language file! Could not do upgrade!");
						return false;
					}
					YAML oldYAML = lang;
					loadLang();
					if(isLoaded())
					{
						doUpgrade(new Language(logger, baseDir, oldVersion, -1, path + ".old_v" + oldVersion, prefix, inJarPrefix, oldYAML));
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					lang = null;
				}
			}
			else
			{
				logger.info("Language version: " + getVersion() + " => Language outdated! Updating ...");
				if(updateMode == LanguageUpdateMethod.OVERWRITE)
				{
					extractLangFile();
					loadLang();
					logger.info("Language file has been updated.");
				}
				else
				{
					doUpdate();
					lang.set("Version", expectedVersion);
					try
					{
						save();
						logger.info("Language file has been updated.");
					}
					catch(Exception e)
					{
						e.printStackTrace();
						return false;
					}
				}
			}
			return true;
		}
		if(expectedVersion < getVersion())
		{
			logger.warning("Language file version newer than expected!");
		}
		return false;
	}

	/**
	 * Allows inheriting classes to implement own code for the language upgrade
	 * If no special code is implemented all keys will be copied 1:1 into the new language file
	 *
	 * @param oldLang the old language file
	 */
	protected void doUpgrade(Language oldLang)
	{
		logger.info("No custom language upgrade code implemented! Copying all data from old config to new one.");
		for(String key : lang.getKeys())
		{
			if(oldLang.lang.isSet(key))
			{
				if(key.equals("Version"))
					continue;
				lang.set(key, oldLang.lang.getString(key, null));
			}
		}
	}

	/**
	 * Allows inheriting classes to implement code for the config update
	 */
	protected void doUpdate()
	{
		logger.info("No language update code implemented! Just updating version!");
	}

	/**
	 * Saves all changes to the file.
	 *
	 * @throws FileNotFoundException If the file the should be saved to does not exist.
	 */
	public void save() throws FileNotFoundException
	{
		try
		{
			lang.save(langFile);
		}
		catch(YAMLNotInitializedException e)
		{
			e.printStackTrace();
		}
	}

	public YAML getLang()
	{
		return lang;
	}
}