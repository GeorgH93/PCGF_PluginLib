/*
 *   Copyright (C) 2014-2018 GeorgH93
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

package at.pcgamingfreaks.Bungee;

import at.pcgamingfreaks.Bungee.Message.Message;
import at.pcgamingfreaks.Bungee.Message.Sender.SendMethod;
import at.pcgamingfreaks.Reflection;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Language extends at.pcgamingfreaks.Language
{
	protected Plugin plugin;

	static
	{
		messageClasses = new MessageClassesReflectionDataHolder(Reflection.getConstructor(Message.class, String.class), Reflection.getMethod(Message.class, "setSendMethod", SendMethod.class),
		                                                        Reflection.getMethod(SendMethod.class, "getMetadataFromJsonMethod"), SendMethod.class);
	}

	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 */
	public Language(@NotNull Plugin plugin, int version)
	{
		this(plugin, version, File.separator + "lang", "");
	}

	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 * @param path    The sub-folder for the language file
	 * @param prefix  The prefix for the language file
	 */
	public Language(@NotNull Plugin plugin, int version, @Nullable String path, @NotNull String prefix)
	{
		this(plugin, version, path, prefix, prefix);
	}

	/**
	 * @param plugin      The instance of the plugin
	 * @param version     The current version of the language file
	 * @param path        The sub-folder for the language file
	 * @param prefix      The prefix for the language file
	 * @param inJarPrefix The prefix for the language file within the jar (e.g.: bungee_)
	 */
	public Language(@NotNull Plugin plugin, int version, @Nullable String path, @NotNull String prefix, @NotNull String inJarPrefix)
	{
		super(plugin.getLogger(), plugin.getDataFolder(), version, path, prefix, inJarPrefix);
		this.plugin = plugin;
	}

	// Getter
	public @NotNull String getString(@NotNull String option)
	{
		return ChatColor.translateAlternateColorCodes('&', get(option));
	}

	public @NotNull BaseComponent[] getReady(@NotNull String option)
	{
		return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', get(option)));
	}

	/**
	 * Gets the message from the language file and replaces bukkit color codes (&amp;) to minecraft color codes (§)
	 *
	 * @param path the path to the searched language value
	 * @return returns the language data
	 */
	@Override
	public @NotNull String getTranslated(@NotNull String path)
	{
		return ChatColor.translateAlternateColorCodes('&', get(path));
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
}