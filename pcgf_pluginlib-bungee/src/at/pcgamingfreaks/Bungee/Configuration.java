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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bungee;

import at.pcgamingfreaks.Version;

import net.md_5.bungee.api.plugin.Plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Configuration extends at.pcgamingfreaks.Configuration
{
	protected static final String DEFAULT_BUNGEE_CORD_PREFIX = "bungee_";
	protected final Plugin plugin;

	/**
	 * @param plugin  the instance of the plugin
	 * @param version current version of the config
	 */
	@Deprecated
	public Configuration(final @NotNull Plugin plugin, final int version)
	{
		this(plugin, version, 0, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param plugin  the instance of the plugin
	 * @param version current version of the config
	 * @param path    the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	@Deprecated
	public Configuration(final @NotNull Plugin plugin, final int version, final @Nullable String path)
	{
		this(plugin, version, 0, path);
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 */
	@Deprecated
	public Configuration(final @NotNull Plugin plugin, final int version, final int upgradeThreshold)
	{
		this(plugin, version, upgradeThreshold, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	@Deprecated
	public Configuration(final @NotNull Plugin plugin, final int version, final int upgradeThreshold, final @Nullable String path)
	{
		this(plugin, version, upgradeThreshold, path, DEFAULT_BUNGEE_CORD_PREFIX);
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param upgradeThreshold versions below this will be upgraded (settings copied into a new config file) instead of updated
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 * @param inJarPrefix      the prefix for the config file within the jar (e.g.: bungee_)
	 */
	@Deprecated
	public Configuration(final @NotNull Plugin plugin, final int version, final int upgradeThreshold, final @Nullable String path, final @NotNull String inJarPrefix)
	{
		super(plugin, plugin.getLogger(), plugin.getDataFolder(), version, upgradeThreshold, path, inJarPrefix);
		this.plugin = plugin;
	}

	/**
	 * @param plugin  the instance of the plugin
	 * @param version current version of the config
	 */
	public Configuration(final @NotNull Plugin plugin, final @NotNull Version version)
	{
		this(plugin, version, DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 */
	public Configuration(final @NotNull Plugin plugin, final @NotNull Version version, final @Nullable String path)
	{
		this(plugin, version, path, DEFAULT_BUNGEE_CORD_PREFIX);
	}

	/**
	 * @param plugin           the instance of the plugin
	 * @param version          current version of the config
	 * @param path             the name/path to a config not named "config.yml" or not placed in the plugins folders root
	 * @param inJarPrefix      the prefix for the config file within the jar (e.g.: bungee_)
	 */
	public Configuration(final @NotNull Plugin plugin, final @NotNull Version version, final @Nullable String path, final @NotNull String inJarPrefix)
	{
		super(plugin, plugin.getLogger(), plugin.getDataFolder(), version, new Version(99999), path, inJarPrefix);
		this.plugin = plugin;
	}
}