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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.Message.Sender.SendMethod;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.Version;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Language extends at.pcgamingfreaks.Language
{
	protected final JavaPlugin plugin;

	static
	{
		messageClasses = new MessageClassesReflectionDataHolder(Reflection.getConstructor(Message.class, String.class), Reflection.getMethod(Message.class, "setSendMethod", SendMethod.class), SendMethod.class);
	}

	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 */
	@Deprecated
	public Language(@NotNull JavaPlugin plugin, int version)
	{
		this(plugin, version, 0);
	}

	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 * @param path    The sub-folder for the language file
	 * @param prefix  The prefix for the language file
	 */
	@Deprecated
	public Language(@NotNull JavaPlugin plugin, int version, @Nullable String path, @NotNull String prefix)
	{
		this(plugin, version, 0, path, prefix, prefix);
	}

	/**
	 * @param plugin           The instance of the plugin
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 */
	@Deprecated
	public Language(@NotNull JavaPlugin plugin, int version, int upgradeThreshold)
	{
		this(plugin, version, upgradeThreshold, File.separator + "lang", "");
	}

	/**
	 * @param plugin           The instance of the plugin
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 * @param path             The sub-folder for the language file
	 * @param prefix           The prefix for the language file
	 */
	@Deprecated
	public Language(@NotNull JavaPlugin plugin, int version, int upgradeThreshold, @Nullable String path, @NotNull String prefix)
	{
		this(plugin, version, upgradeThreshold, path, prefix, prefix);
	}

	/**
	 * @param plugin           The instance of the plugin
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 * @param path             The sub-folder for the language file
	 * @param prefix           The prefix for the language file
	 * @param inJarPrefix      The prefix for the language file within the jar (e.g.: bukkit_)
	 */
	@Deprecated
	public Language(@NotNull JavaPlugin plugin, int version, int upgradeThreshold, @Nullable String path, @NotNull String prefix, @NotNull String inJarPrefix)
	{
		super(plugin.getLogger(), plugin.getDataFolder(), version, upgradeThreshold, path, prefix, inJarPrefix);
		this.plugin = plugin;
	}

	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 */
	public Language(@NotNull JavaPlugin plugin, Version version)
	{
		this(plugin, version, new Version(99999));
	}

	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 * @param path    The sub-folder for the language file
	 * @param prefix  The prefix for the language file
	 */
	public Language(@NotNull JavaPlugin plugin, Version version, @Nullable String path, @NotNull String prefix)
	{
		this(plugin, version, new Version(99999), path, prefix, prefix);
	}

	/**
	 * @param plugin           The instance of the plugin
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 */
	public Language(@NotNull JavaPlugin plugin, Version version, Version upgradeThreshold)
	{
		this(plugin, version, upgradeThreshold, File.separator + "lang", "");
	}

	/**
	 * @param plugin           The instance of the plugin
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 * @param path             The sub-folder for the language file
	 * @param prefix           The prefix for the language file
	 */
	public Language(@NotNull JavaPlugin plugin, Version version, Version upgradeThreshold, @Nullable String path, @NotNull String prefix)
	{
		this(plugin, version, upgradeThreshold, path, prefix, prefix);
	}

	/**
	 * @param plugin           The instance of the plugin
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 * @param path             The sub-folder for the language file
	 * @param prefix           The prefix for the language file
	 * @param inJarPrefix      The prefix for the language file within the jar (e.g.: bukkit_)
	 */
	public Language(@NotNull JavaPlugin plugin, Version version, Version upgradeThreshold, @Nullable String path, @NotNull String prefix, @NotNull String inJarPrefix)
	{
		super(plugin.getLogger(), plugin.getDataFolder(), version, upgradeThreshold, path, prefix, inJarPrefix);
		this.plugin = plugin;
	}

	public @NotNull Message getMessage(@NotNull String path)
	{
		return getMessage(path, true);
	}

	public @NotNull Message getMessage(@NotNull String path, boolean escapeStringFormatCharacters)
	{
		// Only returns null when the messageClasses variable is not set correctly. It is set in this class so this will not return null.
		//noinspection ConstantConditions
		return super.getMessage(escapeStringFormatCharacters, path);
	}

	@Override
	protected Class<?> jarClass()
	{
		return plugin.getClass();
	}
}