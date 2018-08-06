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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.logging.Logger;

/**
 * Handles YAML files.
 */
public class YamlFileManager
{
	protected final Logger logger; // The logger instance of the using plugin
	protected final String inJarPrefix, path;
	protected final int expectedVersion, upgradeThreshold;
	protected final File baseDir;
	protected boolean extracted = false; // Flag to check whether the file has been extracted or not. It is used to prevent endless loops when the file version in the jar is outdated.
	protected String file;
	protected YAML yaml; // The object holding the parsed content of the yaml file
	protected File yamlFile; // The loaded yaml file
	protected YamlFileUpdateMethod updateMode = YamlFileUpdateMethod.UPDATE; // Defines the update behavior for yaml files
	private String fileDescription = "config", fileDescriptionCapitalized = "Config"; // Used to allow customisation of log messages based on what the yaml file is used for

	YamlFileManager(@NotNull Logger logger, @NotNull File baseDir, int version, int upgradeThreshold, @Nullable String path, @Nullable String file, @NotNull String inJarPrefix, @Nullable YAML oldConfig)
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
	 * Allows to set the description/purpose of the yaml file.
	 * Will be used to clarify log messages.
	 * e.g.:
	 *  "Failed to load [fileDescription] file!"
	 *  "[fileDescriptionCapitalized] file successfully loaded."
	 *
	 * @param description The description of the file (e.g.: config or language). Should be all lowercase (the capitalized version will be automatically created).
	 */
	protected void setFileDescription(@NotNull String description)
	{
		fileDescription = description;
		fileDescriptionCapitalized = description.substring(0, 1).toUpperCase() + description.substring(1);
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
		return yaml.getInt("Version", -1);
	}

	//region file handling stuff for inheriting classes
	/**
	 * Allows inheriting classes to implement own code for the yaml file upgrade
	 * If no special code is implemented all keys (which exists in both files) will be copied 1:1 into the new yaml file
	 *
	 * @param oldYamlFile the old yaml file
	 */
	protected void doUpgrade(@NotNull YamlFileManager oldYamlFile)
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
	public @Nullable YAML getYaml()
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
		else if(expectedVersion < getVersion())
		{
			logger.info(fileDescriptionCapitalized + " file version newer than expected! Expected: " + expectedVersion + ". Is: " + getVersion());
		}
		else
		{
			logger.info(ConsoleColor.GREEN + fileDescriptionCapitalized + " file successfully loaded." + ConsoleColor.RESET);
		}
	}

	protected void update()
	{
		logger.info(fileDescriptionCapitalized + " version: " + getVersion() + " => " + fileDescriptionCapitalized + " outdated! Updating ...");
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
		logger.info(fileDescriptionCapitalized + " version: " + getVersion() + " => " + fileDescriptionCapitalized + " outdated! Upgrading ...");
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