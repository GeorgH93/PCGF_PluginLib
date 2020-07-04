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

import com.google.gson.*;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public final class MessageColor
{
	private static final Map<String, MessageColor> BY_NAME = new HashMap<>();
	private static final MessageColor[] COLORS = new MessageColor[17];

	public static final char COLOR_CHAR = '\u00A7';
	public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";

	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-F]");
	private static final Pattern STRIP_COLOR_AND_FORMAT_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

	//region default colors
	/**
	 * Represents black.
	 */
	public static final MessageColor BLACK = new MessageColor('0', "black", "000000");
	/**
	 * Represents dark blue.
	 */
	public static final MessageColor DARK_BLUE = new MessageColor('1', "dark_blue", "0000AA");
	/**
	 * Represents dark green.
	 */
	public static final MessageColor DARK_GREEN = new MessageColor('2', "dark_green", "00AA00");
	/**
	 * Represents dark blue (aqua).
	 */
	public static final MessageColor DARK_AQUA = new MessageColor('3', "dark_aqua", "00AAAA");
	/**
	 * Represents dark red.
	 */
	public static final MessageColor DARK_RED = new MessageColor('4', "dark_red", "AA0000");
	/**
	 * Represents dark purple.
	 */
	public static final MessageColor DARK_PURPLE = new MessageColor('5', "dark_purple", "AA00AA");
	/**
	 * Represents gold.
	 */
	public static final MessageColor GOLD = new MessageColor('6', "gold", "FFAA00");
	/**
	 * Represents gray.
	 */
	public static final MessageColor GRAY = new MessageColor('7', "gray", "AAAAAA");
	/**
	 * Represents dark gray.
	 */
	public static final MessageColor DARK_GRAY = new MessageColor('8', "dark_gray", "555555");
	/**
	 * Represents blue.
	 */
	public static final MessageColor BLUE = new MessageColor('9', "blue", "5555FF");
	/**
	 * Represents green.
	 */
	public static final MessageColor GREEN = new MessageColor('a', "green", "55FF55");
	/**
	 * Represents aqua.
	 */
	public static final MessageColor AQUA = new MessageColor('b', "aqua", "55FFFF");
	/**
	 * Represents red.
	 */
	public static final MessageColor RED = new MessageColor('c', "red", "FF5555");
	/**
	 * Represents light purple.
	 */
	public static final MessageColor LIGHT_PURPLE = new MessageColor('d', "light_purple", "FF55FF");
	/**
	 * Represents yellow.
	 */
	public static final MessageColor YELLOW = new MessageColor('e', "yellow", "FFFF55");
	/**
	 * Represents white.
	 */
	public static final MessageColor WHITE = new MessageColor('f', "white", "FFFFFF");
	/**
	 * Resets all previous chat colors.
	 */
	public static final MessageColor RESET = new MessageColor('r', null, null);
	//endregion

	@Getter private final char code;
	private final String toString;
	@Getter private final String rgbColor, name;
	private final int rgb;
	@Getter private final MessageColor fallbackColor;

	private MessageColor(final char code, String name, final String rgbColor)
	{
		this.code = code;
		this.name = name;
		this.toString = new String(new char[]{ COLOR_CHAR, code});
		this.fallbackColor = this;
		COLORS[ordinal()] = this;
		if(name == null)
		{
			this.rgbColor = null;
			this.rgb = -1;
		}
		else
		{
			this.rgbColor = '#' + rgbColor;
			this.rgb = Integer.parseInt(rgbColor, 16);
			BY_NAME.put(name, this);
			BY_NAME.put(name.toUpperCase(Locale.ROOT), this);
			if(name.contains("_"))
			{
				name = name.replaceAll("_", "");
				BY_NAME.put(name, this);
				BY_NAME.put(name.toUpperCase(Locale.ROOT), this);
			}
		}
	}

	private MessageColor(final String rgbColor)
	{
		this.code = 'x';
		StringBuilder builder = new StringBuilder().append(COLOR_CHAR).append(code);
		for(char code : rgbColor.toCharArray())
		{
			builder.append(COLOR_CHAR).append(code);
		}
		this.toString = builder.toString();
		this.rgbColor = '#' + rgbColor;
		this.name = null;
		this.rgb = Integer.parseInt(rgbColor, 16);
		fallbackColor = getNearestColor(rgb);
	}

	//region override methods
	@Override
	public String toString()
	{
		return toString;
	}

	@Override
	public int hashCode()
	{
		return 371 + Objects.hashCode(this.toString());
	}

	@Override
	public boolean equals(Object other)
	{
		if(other == this) return true;
		if(other == null || getClass() != other.getClass()) return false;
		return toString.equals(other.toString());
	}
	//endregion

	public static @NotNull MessageColor getFromCode(char code) throws IllegalArgumentException
	{
		if(code >= '0' && code <= '9') return values()[code - '0'];
		if(code >= 'A' && code <= 'R') code = (char)(code - 'A' + 'a'); // convert to lower case
		if(code >= 'a' && code <= 'f') return values()[code - 'a' + 10];
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
		color = color.toUpperCase(Locale.ENGLISH);
		try
		{
			return valueOf(color);
		}
		catch(IllegalArgumentException ignored) {}
		return null;
	}

	public static MessageColor getDefaultColor(@NotNull String rgbColor)
	{
		if(rgbColor.length() == 7 && rgbColor.charAt(0) == '#') rgbColor = rgbColor.substring(1);
		return getNearestColor(Integer.parseInt(rgbColor, 16));
	}

	private static MessageColor getNearestColor(int rgb)
	{
		int nearest = Integer.MAX_VALUE;
		MessageColor nearestColor = null;
		for(int i = 0; i < 16; i++)
		{
			MessageColor c = values()[i];
			int r = (c.rgb >> 16) - (rgb >> 16), g = ((c.rgb >> 8) & 0xff) - ((rgb >> 8) & 0xff), b = (c.rgb & 0xff) - (rgb & 0xff);
			int dist = r * r + g * g + b * b;
			if(dist < nearest)
			{
				nearest = dist;
				nearestColor = c;
			}
		}
		return nearestColor;
	}

	//region enum methods
	public static MessageColor valueOf(final @NotNull String name)
	{
		if(name.equals("RESET")) return RESET;
		if(name.length() == 7 && name.charAt(0) == '#') return new MessageColor(name.substring(1));
		MessageColor color = BY_NAME.get(name);
		if(color == null) throw new IllegalArgumentException(name + " is not a MessageColor!");
		return color;
	}

	public static MessageColor[] values()
	{
		return COLORS;
	}

	public int ordinal()
	{
		if(code == 'x') return Integer.MAX_VALUE;
		if(code >= '0' && code <= '9') return code - '0';
		if(code >= 'a' && code <= 'f') return code - 'a' + 10;
		return 16;
	}

	public String name()
	{
		return name;
	}
	//endregion

	public boolean isRGB()
	{
		return this != fallbackColor;
	}

	//region strip code
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
	//endregion


	//region translate code
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
	//endregion

	//region deprecated methods (will be removed at some point)
	@Deprecated
	public boolean isFormat()
	{
		return false;
	}

	@Deprecated
	public boolean isColor()
	{
		return this != RESET;
	}
	//endregion

	public static class MessageColorSerializer implements JsonSerializer<MessageColor>, JsonDeserializer<MessageColor>
	{
		@Override
		public JsonElement serialize(MessageColor src, Type typeOfSrc, JsonSerializationContext context)
		{
			if(src.getName() != null) return new JsonPrimitive(src.getName());
			if(src.getRgbColor() != null) return  new JsonPrimitive(src.getRgbColor());
			return null;
		}

		@Override
		public MessageColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			return valueOf(json.getAsString());
		}
	}
}