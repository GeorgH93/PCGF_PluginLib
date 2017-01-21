/*
 * Copyright (C) 2016 GeorgH93
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Message;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum  MessageColor
{
	/**
	 * Represents black.
	 */
	@SerializedName("black")
	BLACK('0'),
	/**
	 * Represents dark blue.
	 */
	@SerializedName("dark_blue")
	DARK_BLUE('1'),
	/**
	 * Represents dark green.
	 */
	@SerializedName("dark_green")
	DARK_GREEN('2'),
	/**
	 * Represents dark blue (aqua).
	 */
	@SerializedName("dark_aqua")
	DARK_AQUA('3'),
	/**
	 * Represents dark red.
	 */
	@SerializedName("dark_red")
	DARK_RED('4'),
	/**
	 * Represents dark purple.
	 */
	@SerializedName("dark_purple")
	DARK_PURPLE('5'),
	/**
	 * Represents gold.
	 */
	@SerializedName("gold")
	GOLD('6'),
	/**
	 * Represents gray.
	 */
	@SerializedName("gray")
	GRAY('7'),
	/**
	 * Represents dark gray.
	 */
	@SerializedName("dark_gray")
	DARK_GRAY('8'),
	/**
	 * Represents blue.
	 */
	@SerializedName("blue")
	BLUE('9'),
	/**
	 * Represents green.
	 */
	@SerializedName("green")
	GREEN('a'),
	/**
	 * Represents aqua.
	 */
	@SerializedName("aqua")
	AQUA('b'),
	/**
	 * Represents red.
	 */
	@SerializedName("red")
	RED('c'),
	/**
	 * Represents light purple.
	 */
	@SerializedName("light_purple")
	LIGHT_PURPLE('d'),
	/**
	 * Represents yellow.
	 */
	@SerializedName("yellow")
	YELLOW('e'),
	/**
	 * Represents white.
	 */
	@SerializedName("white")
	WHITE('f'),
	/**
	 * Represents magical characters that change around randomly.
	 */
	MAGIC('k', true),
	/**
	 * Makes the text bold.
	 */
	BOLD('l', true),
	/**
	 * Makes a line appear through the text.
	 */
	STRIKETHROUGH('m', true),
	/**
	 * Makes the text appear underlined.
	 */
	UNDERLINE('n', true),
	/**
	 * Makes the text italic.
	 */
	ITALIC('o', true),
	/**
	 * Resets all previous chat colors or formats.
	 */
	RESET('r');

	public static final char COLOR_CHAR = '\u00A7';
	private final char code;
	private final boolean isFormat;

	MessageColor(char code)
	{
		this(code, false);
	}

	MessageColor(char code, boolean isFormat)
	{
		this.code = code;
		this.isFormat = isFormat;
	}

	/**
	 * Mass converts multiple Bukkit or BungeeCord ChatColor elements to the corresponding {@link MessageColor} elements.
	 *
	 * @param styles The style elements to be converted.
	 * @return The converted {@link MessageColor} elements.
	 */
	public static @Nullable MessageColor[] messageColorArrayFromStylesArray(@Nullable Enum... styles)
	{
		if(styles != null && styles.length > 0)
		{
			MessageColor[] msgStyles = new MessageColor[styles.length];
			int i = 0;
			for(Enum style : styles)
			{
				if(style != null)
				{
					msgStyles[i++] = valueOf(style.name().toUpperCase());
				}
			}
			if(msgStyles.length > i)
			{
				return Arrays.copyOf(msgStyles, i);
			}
			return msgStyles;
		}
		return null;
	}

	/**
	 * Converts a Bukkit or BungeeCord ChatColor element to the corresponding {@link MessageColor} element.
	 *
	 * @param style The style element to be converted.
	 * @return The converted {@link MessageColor} element.
	 */
	public static @Nullable MessageColor messageColorFromStyle(@NotNull Enum style)
	{
		return valueOf(style.name().toUpperCase());
	}

	@Override
	public String toString()
	{
		return new String(new char[]{COLOR_CHAR, code});
	}

	/**
	 * Checks if this code is a format code.
	 *
	 * @return True if the instance is a format code.
	 */
	public boolean isFormat()
	{
		return isFormat;
	}

	/**
	 * Checks if this code is a color code.
	 *
	 * @return True if the instance is a color code.
	 */
	public boolean isColor()
	{
		return !isFormat && this != RESET;
	}
}