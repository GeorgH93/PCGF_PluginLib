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

import at.pcgamingfreaks.Config.ILanguageConfiguration;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlGetter;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

public class Configuration extends YamlFileManager implements ILanguageConfiguration
{
	protected static final String DEFAULT_CONFIG_FILE_NAME = "config" + YAML_FILE_EXT;
	private final Object plugin;
	@Deprecated protected String languageKey = "Language", languageUpdateKey = "LanguageUpdateMode"; // Allow to change the keys for the language and the language update mode setting

	@Override
	public @NotNull String getLanguageKey()
	{
		return languageKey;
	}

	@Override
	public @NotNull String getLanguageUpdateModeKey()
	{
		return languageUpdateKey;
	}

	//region constructors
	//region alternative constructors
	/**
	 * @param logger  The logger instance of the plugin
	 * @param baseDir The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version current version of the config
	 */
	@Deprecated
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, int version)
	{
		this(plugin, logger, baseDir, version, 0, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param logger  The logger instance of the plugin
	 * @param baseDir The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version The current version of the config
	 * @param path    The name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	@Deprecated
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, int version, final @Nullable String path)
	{
		this(plugin, logger, baseDir, version, 0, path);
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the config
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new config file) instead of updated
	 */
	@Deprecated
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, int version, int upgradeThreshold)
	{
		this(plugin, logger, baseDir, version, upgradeThreshold, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the config
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             The name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	@Deprecated
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, int version, int upgradeThreshold, final @Nullable String path)
	{
		this(plugin, logger, baseDir, version, upgradeThreshold, path, "");
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the config
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             The name/path to a config not named "config.yml" or not placed in the plugins folders root
	 * @param inJarPrefix      The prefix for the file in the jar (e.g. bungee_)
	 */
	@Deprecated
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, int version, int upgradeThreshold, final @Nullable String path, final @NotNull String inJarPrefix)
	{
		this(plugin, logger, baseDir, new Version(version), new Version(upgradeThreshold), path, inJarPrefix, null);
	}

	/**
	 * @param logger  The logger instance of the plugin
	 * @param baseDir The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version current version of the config
	 */
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, final @NotNull Version version)
	{
		this(plugin, logger, baseDir, version, new Version(99999), DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param logger  The logger instance of the plugin
	 * @param baseDir The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version The current version of the config
	 * @param path    The name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, final @NotNull Version version, final @Nullable String path)
	{
		this(plugin, logger, baseDir, version, new Version(99999), path);
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the config
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new config file) instead of updated
	 */
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, final @NotNull Version version, final @NotNull Version upgradeThreshold)
	{
		this(plugin, logger, baseDir, version, upgradeThreshold, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the config
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             The name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, final @NotNull Version version, final @NotNull Version upgradeThreshold, final @Nullable String path)
	{
		this(plugin, logger, baseDir, version, upgradeThreshold, path, "");
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the configs should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the config
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             The name/path to a config not named "config.yml" or not placed in the plugins folders root
	 * @param inJarPrefix      The prefix for the file in the jar (e.g. bungee_)
	 */
	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, final @NotNull Version version, final @NotNull Version upgradeThreshold, final @Nullable String path, final @NotNull String inJarPrefix)
	{
		this(plugin, logger, baseDir, version, upgradeThreshold, path, inJarPrefix, null);
	}
	//endregion

	private Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, final @NotNull Version version, final @NotNull Version upgradeThreshold, final @Nullable String path, final @NotNull String inJarPrefix, final @Nullable YAML oldConfig)
	{
		super(logger, baseDir, version, upgradeThreshold, null, path, inJarPrefix, oldConfig);
		this.plugin = plugin;
		if(oldConfig == null)
		{
			load();
		}
	}
	//endregion

	/**
	 * Reloads the config file
	 */
	public void reload()
	{
		extracted = false;
		load();
	}

	//region General getter
	/**
	 * Gets the {@link YAML} configuration instance for direct read/write.
	 *
	 * @return The configuration instance. null if the configuration file is not loaded.
	 */
	public @Nullable YAML getConfig()
	{
		return getYaml();
	}

	/**
	 * Gets the {@link YAML} configuration instance for direct read/write.
	 *
	 * @return The configuration instance
	 * @throws ConfigNotInitializedException If the configuration has not been loaded successful.
	 */
	public @NotNull YAML getConfigE() throws ConfigNotInitializedException
	{
		if(yaml == null) throw new ConfigNotInitializedException();
		return yaml;
	}

	public @NotNull YamlGetter getConfigReadOnly() throws ConfigNotInitializedException
	{
		return getConfigE();
	}

	/**
	 * Gets an {@link Integer} value from the configuration.
	 *
	 * @param path The path to the value in the configuration file.
	 * @return The {@link Integer} value from the configuration file.
	 * @throws YamlKeyNotFoundException When the given path could not be found in the configuration
	 * @throws NumberFormatException When the value on the given position can't be converted to an {@link Integer}
	 */
	public int getInt(final @NotNull String path) throws YamlKeyNotFoundException, NumberFormatException
	{
		return yaml.getInt(path);
	}

	/**
	 * Gets an {@link Integer} value from the configuration.
	 *
	 * @param path The path to the value in the configuration file.
	 * @param returnOnNotFound The value returned if the key was not found.
	 * @return The {@link Integer} value from the configuration file.
	 */
	public int getInt(final @NotNull String path, int returnOnNotFound)
	{
		return yaml.getInt(path, returnOnNotFound);
	}

	/**
	 * Gets an {@link Double} value from the configuration.
	 *
	 * @param path The path to the value in the configuration file.
	 * @return The {@link Double} value from the configuration file.
	 * @throws YamlKeyNotFoundException When the given path could not be found in the configuration
	 * @throws NumberFormatException When the value on the given position can't be converted to an {@link Double}
	 */
	public double getDouble(final @NotNull String path) throws YamlKeyNotFoundException, NumberFormatException
	{
		return yaml.getDouble(path);
	}

	/**
	 * Gets an {@link Double} value from the configuration.
	 *
	 * @param path The path to the value in the configuration file.
	 * @param returnOnNotFound The value returned if the key was not found.
	 * @return The {@link Double} value from the configuration file.
	 */
	public double getDouble(final @NotNull String path, double returnOnNotFound)
	{
		return yaml.getDouble(path, returnOnNotFound);
	}

	/**
	 * Gets a {@link String} value from the configuration.
	 *
	 * @param path The path to the value in the configuration file.
	 * @return The {@link String} value from the configuration file.
	 * @throws YamlKeyNotFoundException When the given path could not be found in the configuration
	 */
	public @NotNull String getString(final @NotNull String path) throws YamlKeyNotFoundException
	{
		return yaml.getString(path);
	}

	/**
	 * Gets a {@link String} value from the configuration.
	 *
	 * @param path The path to the value in the configuration file.
	 * @param returnOnNotFound The value returned if the key was not found.
	 * @return The {@link String} value from the configuration file.
	 */
	@Contract("_, !null -> !null")
	public @Nullable String getString(final @NotNull String path, final @Nullable String returnOnNotFound)
	{
		return yaml.getString(path, returnOnNotFound);
	}

	/**
	 * Gets an {@link Boolean} value from the configuration.
	 *
	 * @param path The path to the value in the configuration file.
	 * @return The {@link Boolean} value from the configuration file.
	 * @throws YamlKeyNotFoundException When the given path could not be found in the configuration
	 */
	public boolean getBool(final @NotNull String path) throws YamlKeyNotFoundException
	{
		return yaml.getBoolean(path);
	}

	/**
	 * Gets an {@link Boolean} value from the configuration.
	 *
	 * @param path The path to the value in the configuration file.
	 * @param returnOnNotFound The value returned if the key was not found.
	 * @return The {@link Boolean} value from the configuration file.
	 */
	public boolean getBool(final @NotNull String path, boolean returnOnNotFound)
	{
		return yaml.getBoolean(path, returnOnNotFound);
	}
	//endregion

	//region Getter for language settings
	/**
	 * Gets the language to use, defined in the configuration.
	 *
	 * @return The language to use.
	 * @deprecated implement {@link ILanguageConfiguration} instead in your config handler
	 */
	@Override
	@Deprecated
	public @NotNull String getLanguage()
	{
		return yaml.getString(languageKey, "en");
	}

	/**
	 * Gets how the language file should be updated, defined in the configuration.
	 *
	 * @return The update method for the language file.
	 * @deprecated implement {@link ILanguageConfiguration} instead in your config handler
	 */
	@Override
	@Deprecated
	public @NotNull YamlFileUpdateMethod getLanguageUpdateMode()
	{
		return YamlFileUpdateMethod.fromString(yaml.getString(languageUpdateKey, "upgrade"));
	}
	//endregion

	//region General setter
	/**
	 * Sets a option in the configuration.
	 *
	 * @param path  The path to the configuration option inside the configuration file.
	 * @param value The value it should be set to.
	 */
	public void set(final @NotNull String path, final @NotNull String value)
	{
		yaml.set(path, value);
	}

	/**
	 * Sets a option in the configuration.
	 *
	 * @param path  The path to the configuration option inside the configuration file.
	 * @param value The value it should be set to.
	 */
	public void set(final @NotNull String path, final int value)
	{
		yaml.set(path, value);
	}

	/**
	 * Sets a option in the configuration.
	 *
	 * @param path  The path to the configuration option inside the configuration file.
	 * @param value The value it should be set to.
	 */
	public void set(final @NotNull String path, final double value)
	{
		yaml.set(path, value);
	}

	/**
	 * Sets a option in the configuration.
	 *
	 * @param path  The path to the configuration option inside the configuration file.
	 * @param value The value it should be set to.
	 */
	public void set(final @NotNull String path, final boolean value)
	{
		yaml.set(path, value);
	}
	//endregion

	public static class ConfigNotInitializedException extends RuntimeException
	{
		private ConfigNotInitializedException()
		{
			super("The config file has not been loaded successful");
		}
	}

	@Override
	protected Class<?> JarClass()
	{
		return plugin.getClass();
	}
}