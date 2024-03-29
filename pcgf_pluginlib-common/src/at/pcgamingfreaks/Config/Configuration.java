/*
 *   Copyright (C) 2022 GeorgH93
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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Config;

import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.Version;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlGetter;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

public class Configuration extends YamlFileManager
{
	protected static final String DEFAULT_CONFIG_FILE_NAME = "config" + YAML_FILE_EXT;

	private final Object plugin;

	//region constructors
	//region alternative constructors
	/**
	 * @param plugin           The plugin instance
	 * @param version          The current version of the config
	 */
	public Configuration(final @NotNull IPlugin plugin, final @NotNull Version version)
	{
		this(plugin, version, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param plugin           The plugin instance
	 * @param version          The current version of the config
	 * @param path             The name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(final @NotNull IPlugin plugin, final @NotNull Version version, final @Nullable String path)
	{
		this(plugin, version, path, "");
	}

	/**
	 * @param plugin           The plugin instance
	 * @param version          The current version of the config
	 * @param path             The name/path to a config not named "config.yml" or not placed in the plugins folders root
	 * @param inJarPrefix      The prefix for the file in the jar (e.g. bungee_)
	 */
	public Configuration(final @NotNull IPlugin plugin, final @NotNull Version version, final @Nullable String path, final @NotNull String inJarPrefix)
	{
		this(plugin, plugin.getLogger(), plugin.getDataFolder(), version, path, inJarPrefix);
	}
	//endregion

	public Configuration(final @NotNull Object plugin, final @NotNull Logger logger, final @NotNull File baseDir, final @NotNull Version version, final @Nullable String path, final @NotNull String inJarPrefix)
	{
		super(logger, baseDir, version, null, path, inJarPrefix, null);
		this.plugin = plugin;
		load();
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

	//region General setter
	/**
	 * Sets an option in the configuration.
	 *
	 * @param path  The path to the configuration option inside the configuration file.
	 * @param value The value it should be set to.
	 */
	public void set(final @NotNull String path, final @NotNull String value)
	{
		yaml.set(path, value);
	}

	/**
	 * Sets an option in the configuration.
	 *
	 * @param path  The path to the configuration option inside the configuration file.
	 * @param value The value it should be set to.
	 */
	public void set(final @NotNull String path, final int value)
	{
		yaml.set(path, value);
	}

	/**
	 * Sets an option in the configuration.
	 *
	 * @param path  The path to the configuration option inside the configuration file.
	 * @param value The value it should be set to.
	 */
	public void set(final @NotNull String path, final double value)
	{
		yaml.set(path, value);
	}

	/**
	 * Sets an option in the configuration.
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
		public ConfigNotInitializedException()
		{
			super("The config file has not been loaded successful");
		}
	}

	@Override
	protected Class<?> jarClass()
	{
		return plugin.getClass();
	}
}