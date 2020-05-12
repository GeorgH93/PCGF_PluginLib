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

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

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
	public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";

	private static final Pattern STRIP_FORMAT_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[K-OR]");
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-F]");
	private static final Pattern STRIP_COLOR_AND_FORMAT_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

	@Getter private final char code;
	private final String codeString;
	private final boolean isFormat;

	MessageColor(char code)
	{
		this(code, false);
	}

	MessageColor(char code, boolean isFormat)
	{
		this.code = code;
		this.isFormat = isFormat;
		this.codeString = new String(new char[]{COLOR_CHAR, code});
	}

	/**
	 * Mass converts multiple Bukkit or BungeeCord ChatColor elements to the corresponding {@link MessageColor} elements.
	 *
	 * @param styles The style elements to be converted.
	 * @return The converted {@link MessageColor} elements.
	 */
	public static @Nullable MessageColor[] messageColorArrayFromStylesArray(@Nullable Enum<?>... styles)
	{
		if(styles != null && styles.length > 0)
		{
			MessageColor[] msgStyles = new MessageColor[styles.length];
			int i = 0;
			for(Enum<?> style : styles)
			{
				if(style != null)
				{
					msgStyles[i++] = valueOf(style.name().toUpperCase(Locale.ROOT));
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
	public static @NotNull MessageColor messageColorFromStyle(@NotNull Enum<?> style)
	{
		return valueOf(style.name().toUpperCase(Locale.ROOT));
	}

	@Override
	public String toString()
	{
		return codeString;
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

	public static @NotNull MessageColor getFromCode(char code) throws IllegalArgumentException
	{
		if(code >= '0' && code <= '9') return values()[code - '0'];
		if(code >= 'A' && code <= 'R') code = (char)(code - 'A' + 'a'); // convert to lower case
		if(code >= 'a' && code <= 'f') return values()[code - 'a' + 10];
		if(code >= 'k' && code <= 'o') return values()[code - 'k' + 16];
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
	public static @Nullable String stripColor(final @Nullable String input)
	{
		if(input == null) return null;
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	@Contract("!null->!null; null->null")
	public static @Nullable String stripFormat(final @Nullable String input)
	{
		if(input == null) return null;
		return STRIP_FORMAT_PATTERN.matcher(input).replaceAll("");
	}

	@Contract("!null->!null; null->null")
	public static @Nullable String translateAlternateColorCodes(final @Nullable String textToTranslate)
	{
		return translateAlternateColorCodes('&', textToTranslate);
	}

	@Contract("_,!null->!null; _,null->null")
	public static @Nullable String translateAlternateColorCodes(char altColorChar, @Nullable String textToTranslate)
	{
		return translateColorCode(altColorChar, COLOR_CHAR, textToTranslate);
	}

	@Contract("!null->!null; null->null")
	public static @Nullable String translateToAlternateColorCodes(final @Nullable String textToTranslate)
	{
		return translateToAlternateColorCodes('&', textToTranslate);
	}

	@Contract("_,!null->!null; _,null->null")
	public static @Nullable String translateToAlternateColorCodes(char altColorChar, @Nullable String textToTranslate)
	{
		return translateColorCode(COLOR_CHAR, altColorChar, textToTranslate);
	}

	@Contract("_,_,!null->!null; _,_,null->null")
	private static @Nullable String translateColorCode(char from, char to, @Nullable String textToTranslate)
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