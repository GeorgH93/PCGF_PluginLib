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

package at.pcgamingfreaks.Util;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;

/**
 * This class provides some useful methods around strings as well as some useful strings (and char arrays) as well as regex pattern
 */
@SuppressWarnings("unused")
public class StringUtils extends BaseStringUtils
{
	//region Enabled / Disabled messages
	private static final String ENABLED_MESSAGE = ConsoleColor.GREEN + " %s has been enabled! " + ConsoleColor.YELLOW + " :) " + ConsoleColor.RESET;
	private static final String DISABLED_MESSAGE = ConsoleColor.RED + " %s has been disabled. " + ConsoleColor.YELLOW + " :( " + ConsoleColor.RESET;

	public static @NotNull String getPluginEnabledMessage(@NotNull String pluginName)
	{
		return String.format(ENABLED_MESSAGE, pluginName);
	}

	public static @NotNull String getPluginEnabledMessage(@NotNull String pluginName, @NotNull Version version)
	{
		return getPluginEnabledMessage(pluginName + " v" + version);
	}

	public static @NotNull String getPluginDisabledMessage(@NotNull String pluginName)
	{
		return String.format(DISABLED_MESSAGE, pluginName);
	}

	public static @NotNull String getPluginDisabledMessage(@NotNull String pluginName, @NotNull Version version)
	{
		return getPluginDisabledMessage(pluginName + " v" + version);
	}
	//endregion
}