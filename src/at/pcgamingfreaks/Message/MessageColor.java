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

public enum  MessageColor
{
	/**
	 * Represents black.
	 */
	BLACK('0'),
	/**
	 * Represents dark blue.
	 */
	DARK_BLUE('1'),
	/**
	 * Represents dark green.
	 */
	DARK_GREEN('2'),
	/**
	 * Represents dark blue (aqua).
	 */
	DARK_AQUA('3'),
	/**
	 * Represents dark red.
	 */
	DARK_RED('4'),
	/**
	 * Represents dark purple.
	 */
	DARK_PURPLE('5'),
	/**
	 * Represents gold.
	 */
	GOLD('6'),
	/**
	 * Represents gray.
	 */
	GRAY('7'),
	/**
	 * Represents dark gray.
	 */
	DARK_GRAY('8'),
	/**
	 * Represents blue.
	 */
	BLUE('9'),
	/**
	 * Represents green.
	 */
	GREEN('a'),
	/**
	 * Represents aqua.
	 */
	AQUA('b'),
	/**
	 * Represents red.
	 */
	RED('c'),
	/**
	 * Represents light purple.
	 */
	LIGHT_PURPLE('d'),
	/**
	 * Represents yellow.
	 */
	YELLOW('e'),
	/**
	 * Represents white.
	 */
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

	@Override
	public String toString()
	{
		return new String(new char[]{COLOR_CHAR, code});
	}

	/**
	 * Checks if this code is a format code.
	 */
	public boolean isFormat()
	{
		return isFormat;
	}

	/**
	 * Checks if this code is a color code.
	 */
	public boolean isColor()
	{
		return !isFormat && this != RESET;
	}
}