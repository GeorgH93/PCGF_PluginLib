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

package at.pcgamingfreaks.Bungee.Config;

import at.pcgamingfreaks.Bungee.Message.Message;
import at.pcgamingfreaks.Config.LanguageWithMessageGetter;
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.Version;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Language extends LanguageWithMessageGetter<Message>
{
	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 */
	public Language(final @NotNull IPlugin plugin, final @NotNull Version version)
	{
		super(plugin, version);
	}

	/**
	 * @param plugin  The instance of the plugin
	 * @param version The current version of the language file
	 * @param path    The sub-folder for the language file
	 * @param prefix  The prefix for the language file
	 */
	public Language(final @NotNull IPlugin plugin, final @NotNull Version version, final @Nullable String path, final @NotNull String prefix)
	{
		super(plugin, version, path, prefix);
	}

	/**
	 * @param plugin      The instance of the plugin
	 * @param version     The current version of the language file
	 * @param path        The sub-folder for the language file
	 * @param prefix      The prefix for the language file
	 * @param inJarPrefix The prefix for the language file within the jar (e.g.: bungee_)
	 */
	public Language(final @NotNull IPlugin plugin, final @NotNull Version version, final @Nullable String path, final @NotNull String prefix, final @NotNull String inJarPrefix)
	{
		super(plugin, version, path, prefix, inJarPrefix);
	}

	// Getter
	public @NotNull BaseComponent[] getBungeeMessageComponent(@NotNull String option)
	{
		return TextComponent.fromLegacyText(MessageColor.translateAlternateColorAndFormatCodes(get(option)));
	}

	@Override
	public @NotNull Message getMessage(final @NotNull String path)
	{
		return super.getMessage(path);
	}
}