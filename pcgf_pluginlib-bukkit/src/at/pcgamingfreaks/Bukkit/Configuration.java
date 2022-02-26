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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Config.ILanguageConfiguration;
import at.pcgamingfreaks.LanguageConfiguration;
import at.pcgamingfreaks.Version;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class has been deprecated! Do not use it for new plugins!
 * @deprecated Implement {@link at.pcgamingfreaks.Plugin.IPlugin} in your plugin and use the {@link at.pcgamingfreaks.Config.Configuration} class instead!
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.0.40")
public class Configuration extends at.pcgamingfreaks.Configuration implements LanguageConfiguration
{
	protected final JavaPlugin plugin;

	@Deprecated protected String languageKey = "Language", languageUpdateKey = "LanguageUpdateMode"; // Allow to change the keys for the language and the language update mode setting

	public @NotNull String getLanguageKey()
	{
		return languageKey;
	}

	public @NotNull String getLanguageUpdateModeKey()
	{
		return languageUpdateKey;
	}

	/**
	 * @param plugin  the instance of the plugin
	 * @param version current version of the config
	 */
	public Configuration(@NotNull JavaPlugin plugin, int version)
	{
		this(plugin, version, 0, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param plugin  the instance of the plugin
	 * @param version current version of the config
	 * @param path    the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(@NotNull JavaPlugin plugin, int version, @Nullable String path)
	{
		this(plugin, version, 0, path);
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 */
	public Configuration(@NotNull JavaPlugin plugin, int version, int upgradeThreshold)
	{
		this(plugin, version, upgradeThreshold, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(@NotNull JavaPlugin plugin, int version, int upgradeThreshold, @Nullable String path)
	{
		this(plugin, version, upgradeThreshold, path, "");
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 * @param inJarPrefix      the prefix for the config file within the jar (e.g.: bungee_)
	 */
	public Configuration(@NotNull JavaPlugin plugin, int version, int upgradeThreshold, @Nullable String path, @NotNull String inJarPrefix)
	{
		super(plugin, plugin.getLogger(), plugin.getDataFolder(), version, upgradeThreshold, path, inJarPrefix);
		this.plugin = plugin;
	}

	/**
	 * @param plugin  the instance of the plugin
	 * @param version current version of the config
	 */
	public Configuration(@NotNull JavaPlugin plugin, Version version)
	{
		this(plugin, version, new Version(99999), DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param plugin  the instance of the plugin
	 * @param version current version of the config
	 * @param path    the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(@NotNull JavaPlugin plugin, Version version, @Nullable String path)
	{
		this(plugin, version, new Version(99999), path);
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 */
	public Configuration(@NotNull JavaPlugin plugin, Version version, Version upgradeThreshold)
	{
		this(plugin, version, upgradeThreshold, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(@NotNull JavaPlugin plugin, Version version, Version upgradeThreshold, @Nullable String path)
	{
		this(plugin, version, upgradeThreshold, path, "");
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 * @param inJarPrefix      the prefix for the config file within the jar (e.g.: bungee_)
	 */
	public Configuration(@NotNull JavaPlugin plugin, Version version, Version upgradeThreshold, @Nullable String path, @NotNull String inJarPrefix)
	{
		super(plugin, plugin.getLogger(), plugin.getDataFolder(), version, upgradeThreshold, path, inJarPrefix);
		this.plugin = plugin;
	}

	/**
	 * Checks if the used bukkit version supports UUIDs.
	 *
	 * @return true if the used bukkit version is uuid compatible, false if not
	 * @deprecated Please use {@link MCVersion#isUUIDsSupportAvailable()} instead.
	 */
	@Deprecated
	protected boolean isBukkitVersionUUIDCompatible()
	{
		return MCVersion.isUUIDsSupportAvailable();
	}





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
		return yaml.getString(getLanguageKey(), "en");
	}

	/**
	 * Gets how the language file should be updated, defined in the configuration.
	 *
	 * @return The update method for the language file.
	 * @deprecated implement {@link ILanguageConfiguration} instead in your config handler
	 */
	@Override
	@Deprecated
	public @NotNull at.pcgamingfreaks.YamlFileUpdateMethod getLanguageUpdateMode()
	{
		return at.pcgamingfreaks.YamlFileUpdateMethod.fromString(yaml.getString(getLanguageUpdateModeKey(), "upgrade"));
	}
	//endregion
}