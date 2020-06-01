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

import at.pcgamingfreaks.Reflection;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum  MessageColor
{
	/**
	 * Represents black.
	 */
	@SerializedName("black")
	BLACK('0', "000000"),
	/**
	 * Represents dark blue.
	 */
	@SerializedName(value = "dark_blue")
	DARK_BLUE('1', "0000AA"),
	/**
	 * Represents dark green.
	 */
	@SerializedName("dark_green")
	DARK_GREEN('2', "00AA00"),
	/**
	 * Represents dark blue (aqua).
	 */
	@SerializedName("dark_aqua")
	DARK_AQUA('3', "00AAAA"),
	/**
	 * Represents dark red.
	 */
	@SerializedName("dark_red")
	DARK_RED('4', "AA0000"),
	/**
	 * Represents dark purple.
	 */
	@SerializedName("dark_purple")
	DARK_PURPLE('5', "AA00AA"),
	/**
	 * Represents gold.
	 */
	@SerializedName("gold")
	GOLD('6', "FFAA00"),
	/**
	 * Represents gray.
	 */
	@SerializedName("gray")
	GRAY('7', "AAAAAA"),
	/**
	 * Represents dark gray.
	 */
	@SerializedName("dark_gray")
	DARK_GRAY('8', "555555"),
	/**
	 * Represents blue.
	 */
	@SerializedName("blue")
	BLUE('9', "5555FF"),
	/**
	 * Represents green.
	 */
	@SerializedName("green")
	GREEN('a', "55FF55"),
	/**
	 * Represents aqua.
	 */
	@SerializedName("aqua")
	AQUA('b', "55FFFF"),
	/**
	 * Represents red.
	 */
	@SerializedName("red")
	RED('c', "FF5555"),
	/**
	 * Represents light purple.
	 */
	@SerializedName("light_purple")
	LIGHT_PURPLE('d', "FF55FF"),
	/**
	 * Represents yellow.
	 */
	@SerializedName("yellow")
	YELLOW('e', "FFFF55"),
	/**
	 * Represents white.
	 */
	@SerializedName("white")
	WHITE('f', "FFFFFF"),
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
	RESET('r', false);

	public static final char COLOR_CHAR = '\u00A7';
	public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";

	private static final Pattern STRIP_FORMAT_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[K-OR]");
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-F]");
	private static final Pattern STRIP_COLOR_AND_FORMAT_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
	private static final Pattern TRY_TO_READ_COLOR = Pattern.compile("(?<descriptor>LIGHT|DARK)(?<color>[A-Z]+)");

	@Getter private final char code;
	private final String codeString;
	@Getter private final String rgbColor;
	private final boolean isFormat;
	private final int[] rgb;

	MessageColor(char code, String rgbColor)
	{
		this.code = code;
		this.isFormat = false;
		this.codeString = new String(new char[]{COLOR_CHAR, code});
		this.rgbColor = '#' + rgbColor;
		this.rgb = new int[] { Integer.parseInt(rgbColor.substring(0, 2), 16), Integer.parseInt(rgbColor.substring(2, 4), 16), Integer.parseInt(rgbColor.substring(4, 6), 16) };
	}

	MessageColor(char code, boolean isFormat)
	{
		this.code = code;
		this.isFormat = isFormat;
		this.codeString = new String(new char[]{COLOR_CHAR, code});
		this.rgbColor = null;
		this.rgb = null;
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
	@Deprecated
	public static @NotNull MessageColor messageColorFromStyle(final @NotNull Object style)
	{
		if(!style.getClass().getSimpleName().equals("ChatColor")) throw new IllegalArgumentException("style musst be of type ChatColor!");
		try
		{
			return valueOf(((String) Reflection.getMethodIncludeParents(style.getClass(), "name").invoke(style)).toUpperCase(Locale.ROOT));
		}
		catch(IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalArgumentException("The style object is not a valide ChatColor object.", e);
		}
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

	public static @Nullable MessageColor getColor(@NotNull String color)
	{
		if(color.length() == 0) return null;
		if(color.charAt(0) == '&' || color.charAt(0) == COLOR_CHAR) color = color.substring(1);
		if(color.length() == 0) return null;
		if(color.length() == 1)
		{
			try
			{
				return getFromCode(color.charAt(0));
			}
			catch(IllegalArgumentException ignored)
			{
				return null;
			}
		}
		else if(color.length() == 2)
		{
			try
			{
				return values()[Integer.parseInt(color)];
			}
			catch(NumberFormatException | IndexOutOfBoundsException ignored)
			{
				return null;
			}
		}
		else if(color.length() == 7 && color.matches("#[\\da-fA-F]{6}"))
		{
			int[] newRGB = new int[] { Integer.parseInt(color.substring(1, 3), 16), Integer.parseInt(color.substring(3, 5), 16), Integer.parseInt(color.substring(5, 7), 16) };
			int nearest = Integer.MAX_VALUE;
			MessageColor nearestColor = null;
			for(int i = 0; i < 16; i++)
			{
				MessageColor c = values()[i];
				int r = c.rgb[0] - newRGB[0], g = c.rgb[1] - newRGB[1], b = c.rgb[2] - newRGB[2];
				int dist = r * r + g * g + b * b;
				if(dist < nearest)
				{
					nearest = dist;
					nearestColor = c;
				}
			}
			return nearestColor;
		}
		color = color.toUpperCase(Locale.ENGLISH);
		try
		{
			return valueOf(color);
		}
		catch(IllegalArgumentException ignored) {}
		Matcher matcher = TRY_TO_READ_COLOR.matcher(color);
		if(matcher.matches())
		{
			try
			{
				return valueOf(matcher.group("descriptor") + '_' + matcher.group("color"));
			}
			catch(IllegalArgumentException ignored) {}
		}
		return null;
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