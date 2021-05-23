/*
 *   Copyright (C) 2021 GeorgH93
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
import at.pcgamingfreaks.yaml.YamlGetter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Handles YAML files.
 */
public class YamlFileManager
{
	protected static final String KEY_YAML_VERSION = "Version", YAML_FILE_EXT = ".yml";

	protected final Logger logger; // The logger instance of the using plugin
	protected final String inJarPrefix, path;
	@Deprecated protected final int expectedVersion, upgradeThreshold;
	protected final Version versionExpected;
	protected final File baseDir;
	protected boolean extracted = false; // Flag to check whether the file has been extracted or not. It is used to prevent endless loops when the file version in the jar is outdated.
	protected String file;
	protected YAML yaml; // The object holding the parsed content of the yaml file
	protected File yamlFile; // The loaded yaml file
	@Deprecated protected YamlFileUpdateMethod updateMode = null; // Defines the update behavior for yaml files
	@Getter protected String fileDescription = "config", fileDescriptionCapitalized = "Config"; // Used to allow customisation of log messages based on what the yaml file is used for

	YamlFileManager(final @NotNull Logger logger, final @NotNull File baseDir, final int version, final int upgradeThreshold, final @Nullable String path,
	                final @Nullable String file, final @NotNull String inJarPrefix, final @Nullable YAML oldConfig)
	{
		this(logger, baseDir, new Version(version), new Version(upgradeThreshold), path, file, inJarPrefix, oldConfig);
	}

	YamlFileManager(final @NotNull Logger logger, final @NotNull File baseDir, final Version version, final Version upgradeThreshold, final @Nullable String path,
	                final @Nullable String file, final @NotNull String inJarPrefix, final @Nullable YAML oldConfig)
	{
		this.path = path;
		this.file = file;
		this.logger = logger;
		this.inJarPrefix = inJarPrefix;
		this.versionExpected = version;
		this.expectedVersion = version.getMajor();
		this.upgradeThreshold = upgradeThreshold.getMajor();
		this.baseDir = (path != null && !path.isEmpty()) ? new File(baseDir, path) : baseDir;
		if(file != null) this.yamlFile = new File(this.baseDir, file);
		if(oldConfig != null) yaml = oldConfig;
	}

	public @NotNull Logger getLogger()
	{
		return logger;
	}

	protected @Nullable YamlFileUpdateMethod getYamlUpdateMode()
	{
		return updateMode;
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
	public void setFileDescription(final @NotNull String description)
	{
		fileDescription = description;
		//noinspection StringToUpperCaseOrToLowerCaseWithoutLocale
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
	 * Deprecated! use version() instead
	 *
	 * @return The version of the configuration. -1 if there is no or an invalid "Version" value in the configuration file.
	 */
	@Deprecated
	public int getVersion()
	{
		return yaml.getInt(KEY_YAML_VERSION, -1);
	}

	/**
	 * Gets the version of the configuration file.
	 *
	 * @return The version of the configuration. 0 if there is no version in the file.
	 * @throws Version.InvalidVersionStringException If the version in the file is not a valid version string.
	 */
	public Version version() throws Version.InvalidVersionStringException
	{
		return new Version(yaml.getString(KEY_YAML_VERSION, "0"));
	}

	/**
	 * Gets the expected version of the configuration.
	 *
	 * @return The expected version of the configuration.
	 */
	public Version getExpectedVersion()
	{
		return versionExpected;
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
		doUpgrade(oldYamlFile, new HashMap<>());
	}

	/**
	 * Allows inheriting classes to implement own code for the yaml file upgrade
	 * If no special code is implemented all keys (which exists in both files) will be copied 1:1 into the new yaml file
	 *
	 * @param oldYamlFile the old yaml file
	 * @param configKeyReMappings map of keys that should get remapped, key = new key, value = old key
	 */
	protected void doUpgrade(@NotNull YamlFileManager oldYamlFile, @NotNull Map<String, String> configKeyReMappings)
	{
		doUpgrade(oldYamlFile, configKeyReMappings, new ArrayList<>());
	}

	/**
	 * Allows inheriting classes to implement own code for the yaml file upgrade
	 * If no special code is implemented all keys (which exists in both files) will be copied 1:1 into the new yaml file
	 *
	 * @param oldYamlFile the old yaml file
	 * @param configKeyReMappings map of keys that should get remapped, key = new key, value = old key
	 * @param keysToKeep a collection of keys that should be keep even if they do not exist in the new config
	 */
	protected void doUpgrade(@NotNull YamlFileManager oldYamlFile, @NotNull Map<String, String> configKeyReMappings, @NotNull Collection<String> keysToKeep)
	{
		List<String> keysToProcess = new ArrayList<>(yaml.getKeys());
		keysToProcess.addAll(keysToKeep);
		for(String key : keysToProcess)
		{
			if(key.equals(KEY_YAML_VERSION)) continue;
			String oldKey = configKeyReMappings.getOrDefault(key, key);
			if(oldYamlFile.yaml.isSet(oldKey))
			{
				if(oldYamlFile.yaml.isList(oldKey))
				{
					yaml.set(key, oldYamlFile.yaml.getStringList(oldKey, new LinkedList<>()));
				}
				else
				{
					yaml.set(key, oldYamlFile.yaml.getString(oldKey, null));
				}
			}
		}
	}

	/**
	 * Allows inheriting classes to implement code for the yaml update
	 */
	protected void doUpdate()
	{
		logger.info("No " + getFileDescription() + " update code implemented! Just updating version!");
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

	protected void loaded()
	{
		logger.info(ConsoleColor.GREEN + getFileDescriptionCapitalized() + " file successfully loaded." + ConsoleColor.RESET);
	}
	//endregion

	//region handling file ops (load, save, update, upgrade, create)
	public @Nullable YAML getYaml()
	{
		return yaml;
	}

	public @NotNull YAML getYamlE() throws YamlFileNotInitializedException
	{
		if(yaml == null) throw new YamlFileNotInitializedException();
		return yaml;
	}

	public @NotNull YamlGetter getYamlReadOnly() throws YamlFileNotInitializedException
	{
		return getYamlE();
	}

	/**
	 * Saves all changes to the file.
	 *
	 * @throws FileNotFoundException If the file the should be saved to does not exist.
	 */
	public void save() throws FileNotFoundException
	{
		yaml.save(yamlFile);
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
			logger.warning("Failed to load " + getFileDescription() + " file!");
			e.printStackTrace();
		}
	}

	protected void validate()
	{
		if(getExpectedVersion().newerThan(version()))
		{
			if(extracted)
			{
				logger.warning(ConsoleColor.YELLOW + "The " + getFileDescription() + " file (" + file + ") is outdated in the jar!" + ConsoleColor.RESET);
				update();
			}
			else
			{
				switch(decideYamlUpdateMode())
				{
					case OVERWRITE:
						extractFile();
						load();
						logger.info(ConsoleColor.GREEN + "Successful updated " + getFileDescription() + " file." + ConsoleColor.RESET);
						break;
					case UPDATE:
						update();
						break;
					case UPGRADE:
						upgrade();
						break;
				}
			}
		}
		else
		{
			if(getExpectedVersion().olderThan(version())) logger.info(getFileDescriptionCapitalized() + " file version newer than expected! Expected: " + getExpectedVersion() + " Is: " + version());
			loaded();
		}
	}

	protected void update()
	{
		logger.info(getFileDescriptionCapitalized() + " version: " + version() + " => " + getFileDescriptionCapitalized() + " outdated! Updating ...");
		try
		{
			doUpdate();
			yaml.set(KEY_YAML_VERSION, getExpectedVersion());
			save();
			logger.info(ConsoleColor.GREEN + "Successful updated " + getFileDescription() + " file." + ConsoleColor.RESET);
		}
		catch(Exception e)
		{
			logger.warning("Failed to update " + getFileDescription() + "!");
			e.printStackTrace();
			yaml = null;
		}
	}

	protected void upgrade()
	{
		logger.info(getFileDescriptionCapitalized() + " version: " + version() + " => " + getFileDescriptionCapitalized() + " outdated! Upgrading ...");
		try
		{
			Version oldVersion = version();
			String oldExt = ".old_v" + ((oldVersion.toString().startsWith("v") || oldVersion.toString().startsWith("V")) ? oldVersion.toString().substring(1) : oldVersion.toString());
			File oldFile = new File(yamlFile + oldExt);
			if(oldFile.exists() && !oldFile.delete()) logger.warning("Failed to delete old " + getFileDescription() + " file backup!");
			if(!yamlFile.renameTo(oldFile))
			{
				logger.warning("Failed to rename old " + getFileDescription() + " file! Could not do upgrade!");
				return;
			}
			YAML oldYAML = yaml;
			load();
			if(isLoaded())
			{
				doUpgrade(new YamlFileManager(logger, baseDir, oldVersion, new Version(0), path, file + oldExt, inJarPrefix, oldYAML));
			}
			yaml.set(KEY_YAML_VERSION, getExpectedVersion());
			save();
			logger.info(ConsoleColor.GREEN + "Successful upgraded " + getFileDescription() + " file." + ConsoleColor.RESET);
		}
		catch(Exception e)
		{
			logger.warning("Failed to upgrade " + getFileDescription() + " file.");
			e.printStackTrace();
			yaml = null;
		}
	}

	protected @NotNull YamlFileUpdateMethod decideYamlUpdateMode()
	{
		if(extracted) return YamlFileUpdateMethod.UPDATE;
		if(getYamlUpdateMode() != null) return getYamlUpdateMode();
		if(version().olderThan(new Version(upgradeThreshold))) return YamlFileUpdateMethod.UPGRADE;
		return YamlFileUpdateMethod.UPDATE;
	}

	protected void extractFile()
	{
		Utils.extractFile(JarClass(), logger, inJarPrefix + file, yamlFile);
		extracted = true;
	}

	protected Class<?> JarClass()
	{
		return getClass();
	}
	//endregion

	public static class YamlFileNotInitializedException extends RuntimeException
	{
		private YamlFileNotInitializedException()
		{
			super("The yaml file has not been loaded successful");
		}
	}
}