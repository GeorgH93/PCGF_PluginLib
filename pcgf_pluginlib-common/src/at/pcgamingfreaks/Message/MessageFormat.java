/*
 *   Copyright (C) 2020 GeorgH93
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

package at.pcgamingfreaks.Message;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.util.regex.Pattern;

public enum MessageFormat
{
	/**
	 * Represents magical characters that change around randomly.
	 */
	MAGIC('k'),
	/**
	 * Makes the text bold.
	 */
	BOLD('l'),
	/**
	 * Makes a line appear through the text.
	 */
	STRIKETHROUGH('m'),
	/**
	 * Makes the text appear underlined.
	 */
	UNDERLINE('n'),
	/**
	 * Makes the text italic.
	 */
	ITALIC('o'),
	/**
	 * Resets all previous chat colors or formats.
	 */
	RESET('r');

	public static final char FORMAT_CHAR = '\u00A7';
	public static final String ALL_CODES = "KkLlMmNnOoRr";

	private static final Pattern STRIP_FORMAT_PATTERN = Pattern.compile("(?i)" + FORMAT_CHAR + "[K-OR]");
	private static final Pattern STRIP_COLOR_AND_FORMAT_PATTERN = Pattern.compile("(?i)" + FORMAT_CHAR + "[0-9A-FK-OR]");

	@Getter	private final char code;
	private final String codeString;

	MessageFormat(char code)
	{
		this.code = code;
		this.codeString = new String(new char[]{ FORMAT_CHAR, code});
	}

	@Override
	public String toString()
	{
		return codeString;
	}

	public static boolean isFormatChar(char code)
	{
		return (code >= 'K' && code <= 'O') || (code >= 'k' && code <= 'o');
	}

	public static @NotNull MessageFormat getFromCode(char code) throws IllegalArgumentException
	{
		if(code >= 'K' && code <= 'R') code = (char)(code - 'A' + 'a'); // convert to lower case
		if(code >= 'k' && code <= 'o') return values()[code - 'k'];
		if(code == 'r') return RESET;
		throw new IllegalArgumentException("Unknown format code '" + code + "'!");
	}

	public @Nullable String strip(final @Nullable String input)
	{
		if(input == null) return null;
		return input.replaceAll(toString(), "");
	}

	@Contract("!null->!null; null->null")
	public static @Nullable String stripColorAndFormat(final @Nullable String input)
	{
		if(input == null) return null;
		return STRIP_COLOR_AND_FORMAT_PATTERN.matcher(input).replaceAll("");
	}

	@Contract("!null->!null; null->null")
	public static @Nullable String stripFormat(final @Nullable String input)
	{
		if(input == null) return null;
		return STRIP_FORMAT_PATTERN.matcher(input).replaceAll("");
	}

	@Contract("!null->!null; null->null")
	public static @Nullable String translateAlternateFormatCodes(final @Nullable String textToTranslate)
	{
		return translateAlternateFormatCodes('&', textToTranslate);
	}

	@Contract("_,!null->!null; _,null->null")
	public static @Nullable String translateAlternateFormatCodes(char altColorChar, @Nullable String textToTranslate)
	{
		return translateFormatCode(altColorChar, FORMAT_CHAR, textToTranslate);
	}

	@Contract("!null->!null; null->null")
	public static @Nullable String translateToAlternateFormatCodes(final @Nullable String textToTranslate)
	{
		return translateToAlternateFormatCodes('&', textToTranslate);
	}

	@Contract("_,!null->!null; _,null->null")
	public static @Nullable String translateToAlternateFormatCodes(char altColorChar, @Nullable String textToTranslate)
	{
		return translateFormatCode(FORMAT_CHAR, altColorChar, textToTranslate);
	}

	@Contract("_,_,!null->!null; _,_,null->null")
	private static @Nullable String translateFormatCode(char from, char to, @Nullable String textToTranslate)
	{
		if(textToTranslate == null || textToTranslate.length() < 2) return textToTranslate;
		char[] chars = textToTranslate.toCharArray();
		for(int i = 0; i + 1 < chars.length; i++)
		{
			if(chars[i] == from && ALL_CODES.indexOf(chars[i + 1]) > -1)
			{
				chars[i] = to;
				chars[i + 1] = Character.toLowerCase(chars[i + 1]);
			}
		}
		return new String(chars);
	}
}