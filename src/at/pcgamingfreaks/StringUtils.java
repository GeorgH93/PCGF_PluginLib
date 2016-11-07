/*
 *   Copyright (C) 2016 GeorgH93
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

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class StringUtils
{
	public static boolean arrayContains(@NotNull String[] strings, @Nullable String searchFor)
	{
		Validate.notNull(strings);
		for(String s : strings)
		{
			//noinspection ConstantConditions
			if((s != null && s.equals(searchFor)) || (s == null && searchFor == null))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean arrayContainsIgnoreCase(@NotNull String[] strings, @Nullable String searchFor)
	{
		Validate.notNull(strings);
		for(String s : strings)
		{
			//noinspection ConstantConditions
			if((s != null && s.equalsIgnoreCase(searchFor)) || (s == null && searchFor == null))
			{
				return true;
			}
		}
		return false;
	}

	@Contract("_, null -> false")
	public static boolean arrayContainsAnyIgnoreCase(@NotNull String[] strings, @Nullable String... searchFor)
	{
		if(searchFor != null && searchFor.length > 0)
		{
			for(String str : searchFor)
			{
				if(arrayContainsIgnoreCase(strings, str))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Contract("_, null -> false")
	public static boolean arrayContainsAny(@NotNull String[] strings, @Nullable String... searchFor)
	{
		if(searchFor != null && searchFor.length > 0)
		{
			for(String str : searchFor)
			{
				if(arrayContains(strings, str))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static @NotNull List<String> getAllContaining(@NotNull String[] source, @NotNull String searchFor)
	{
		Validate.notNull(source);
		Validate.notNull(searchFor);
		List<String> result = new LinkedList<>();
		for(String str : source)
		{
			//noinspection ConstantConditions
			if(str != null && str.contains(searchFor))
			{
				result.add(str);
			}
		}
		return result;
	}

	public static @NotNull List<String> getAllContainingIgnoreCase(@NotNull String[] source, @NotNull String searchFor)
	{
		Validate.notNull(source);
		Validate.notNull(searchFor);
		List<String> result = new LinkedList<>();
		for(String str : source)
		{
			//noinspection ConstantConditions
			if(str != null && containsIgnoreCase(str, searchFor))
			{
				result.add(str);
			}
		}
		return result;
	}

	public static boolean containsIgnoreCase(@NotNull String string, @NotNull String searchFor)
	{
		Validate.notNull(string);
		Validate.notNull(searchFor);
		return string.toLowerCase().contains(searchFor.toLowerCase());
	}

	/**
	 * Limits the length of a given string to a given amount of characters.
	 *
	 * @param text      The text that should be limited in it's length.
	 * @param maxLength The max amount of characters the text should be limited to.
	 * @return The text in it's limited length.
	 */
	public static String limitLength(@NotNull String text, int maxLength)
	{
		Validate.notNull(text, "The text must not be null.");
		Validate.isTrue(maxLength >= 0, "The max length must not be negative!");
		if(text.length() == 0 || maxLength == 0) return "";
		if(text.length() <= maxLength) return text; // No need to create a new object if the string has not changed
		return text.substring(0, maxLength -1);
	}

	/**
	 * Escapes special characters to allow the string to be placed inside a json string (e.g. to replace a text within an already built json).
	 *
	 * @param string The string to be escaped.
	 * @return The escaped string.
	 */
	public static String escapeJsonString(String string)
	{
		return string.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
	}

	//region Enabled / Disabled messages
	private static final String ENABLED_MESSAGE = ConsoleColor.GREEN + " %s has been enabled! " + ConsoleColor.YELLOW + " :) " + ConsoleColor.RESET;
	private static final String DISABLED_MESSAGE = ConsoleColor.RED + " %s has been disabled. " + ConsoleColor.YELLOW + " :( " + ConsoleColor.RESET;

	public static String getPluginEnabledMessage(String pluginName)
	{
		return String.format(ENABLED_MESSAGE, pluginName);
	}

	public static String getPluginEnabledMessage(String pluginName, Version version)
	{
		return getPluginEnabledMessage(pluginName + " v" + version);
	}

	public static String getPluginDisabledMessage(String pluginName)
	{
		return String.format(DISABLED_MESSAGE, pluginName);
	}

	public static String getPluginDisabledMessage(String pluginName, Version version)
	{
		return getPluginDisabledMessage(pluginName + " v" + version);
	}
	//endregion
}