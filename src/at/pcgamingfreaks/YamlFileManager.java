/*
 *   Copyright (C) 2018 GeorgH93
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

import java.io.*;
import java.util.logging.Logger;

public class YamlFileManager
{
	protected boolean extracted = false; // Flag to check whether the file has been extracted or not. It is used to prevent endless loops when the file version in the jar is outdated.
	protected final Logger logger; // The logger instance of the using plugin
	protected final String inJarPrefix, path;
	protected final int expectedVersion, upgradeThreshold;
	protected final File baseDir;
	protected String file, fileDescription = "config"; // Used to allow customisation of log messages based on what the yaml file is used for
	protected YAML yaml;
	protected File yamlFile;
	protected YamlFileUpdateMethod updateMode = YamlFileUpdateMethod.UPDATE;

	YamlFileManager(Logger logger, File baseDir, int version, int upgradeThreshold, String path, String file, String inJarPrefix, YAML oldConfig)
	{
		this.path = path;
		this.file = file;
		this.logger = logger;
		this.inJarPrefix = inJarPrefix;
		this.expectedVersion = version;
		this.upgradeThreshold = upgradeThreshold;
		this.baseDir = (path != null) ? new File(baseDir, path) : baseDir;
		if(file != null) this.yamlFile = new File(this.baseDir, file);
		if(oldConfig != null) yaml = oldConfig;
	}

	/**
	 * Checks if the yaml file is loaded or not
	 *
	 * @return true if the file is loaded, false if not
	 */
	public boolean isLoaded()
	{
		return yaml != null;
	}

	/**
	 * Gets the version of the configuration.
	 *
	 * @return The version of the configuration. -1 if there is no or an invalid "Version" value in the configuration file.
	 */
	public int getVersion()
	{
		try
		{
			return yaml.getInt("Version");
		}
		catch(Exception ignored)
		{
			return -1;
		}
	}

	//region file handling stuff for inheriting classes
	/**
	 * Allows inheriting classes to implement own code for the yaml file upgrade
	 * If no special code is implemented all keys (which exists in both files) will be copied 1:1 into the new yaml file
	 *
	 * @param oldYamlFile the old yaml file
	 */
	protected void doUpgrade(YamlFileManager oldYamlFile)
	{
		logger.info("No custom " + fileDescription + " upgrade code implemented! Copying all data from old file to new one.");
		for(String key : yaml.getKeys())
		{
			if(oldYamlFile.yaml.isSet(key))
			{
				if(key.equals("Version")) continue;
				yaml.set(key, oldYamlFile.yaml.getString(key, null));
			}
		}
	}

	/**
	 * Allows inheriting classes to implement code for the yaml update
	 */
	protected void doUpdate()
	{
		logger.info("No " + fileDescription + " update code implemented! Just updating version!");
	}

	/**
	 * Allows inheriting classes to implement code for setting values in new created yaml files
	 *
	 * @return true if values in the config have been changed
	 */
	protected boolean newConfigCreated()
	{
		return false;
	}
	//endregion

	//region handling file ops (load, save, update, upgrade, create)
	public YAML getYaml()
	{
		return yaml;
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
			yaml.save(yamlFile);
		}
		catch(YAMLNotInitializedException e)
		{
			logger.warning("Failed to save chances to file \"" + yamlFile.toString() + "\"!");
			e.printStackTrace();
		}
	}

	protected void load()
	{
		try
		{
			if(!yamlFile.exists() || yamlFile.length() == 0)
			{
				extractFile();
			}
			yaml = new YAML(yamlFile);
			validate();
			if(extracted && newConfigCreated())
			{
				save();
			}
		}
		catch(Exception e)
		{
			logger.warning("Failed to load " + fileDescription + " file!");
			e.printStackTrace();
		}
	}

	protected void validate()
	{
		if(expectedVersion > getVersion())
		{
			if(extracted)
			{
				logger.warning(ConsoleColor.YELLOW + "The version of the " + fileDescription + " file (" + file + ") is outdated in the jar! Please inform the plugin developer." + ConsoleColor.RESET);
			}
			if(updateMode == YamlFileUpdateMethod.OVERWRITE && !extracted)
			{
				extractFile();
				load();
				logger.info(ConsoleColor.GREEN + "Successful updated " + fileDescription + " file." + ConsoleColor.RESET);
			}
			else if((getVersion() < upgradeThreshold || updateMode == YamlFileUpdateMethod.UPGRADE) && !extracted)
			{
				upgrade();
			}
			else
			{
				update();
			}
		}
		if(expectedVersion < getVersion())
		{
			logger.info(fileDescription + " file version newer than expected! Expected: " + expectedVersion + ". Is: " + getVersion());
		}
	}

	protected void update()
	{
		logger.info(fileDescription + " version: " + getVersion() + " => " + fileDescription + " outdated! Updating ...");
		try
		{
			doUpdate();
			yaml.set("Version", expectedVersion);
			save();
			logger.info(ConsoleColor.GREEN + "Successful updated " + fileDescription + " file." + ConsoleColor.RESET);
		}
		catch(Exception e)
		{
			logger.warning("Failed to update " + fileDescription + "!");
			e.printStackTrace();
			yaml = null;
		}
	}

	protected void upgrade()
	{
		logger.info(fileDescription + " version: " + getVersion() + " => " + fileDescription + " outdated! Upgrading ...");
		try
		{
			int oldVersion = getVersion();
			File oldFile = new File(yamlFile + ".old_v" + oldVersion);
			if(oldFile.exists() && !oldFile.delete()) logger.warning("Failed to delete old " + fileDescription + " file backup!");
			if(!yamlFile.renameTo(oldFile))
			{
				logger.warning("Failed to rename old " + fileDescription + " file! Could not do upgrade!");
				return;
			}
			YAML oldYAML = yaml;
			load();
			if(isLoaded())
			{
				doUpgrade(new YamlFileManager(logger, baseDir, oldVersion, -1, path, file + ".old_v" + oldVersion, inJarPrefix, oldYAML));
			}
			yaml.set("Version", expectedVersion);
			save();
			logger.info(ConsoleColor.GREEN + "Successful upgraded " + fileDescription + " file." + ConsoleColor.RESET);
		}
		catch(Exception e)
		{
			logger.warning("Failed to upgrade " + fileDescription + " file.");
			e.printStackTrace();
			yaml = null;
		}
	}

	protected void extractFile()
	{
		Utils.extractFile(getClass(), logger, inJarPrefix + file, yamlFile);
		extracted = true;
	}
	//endregion
}