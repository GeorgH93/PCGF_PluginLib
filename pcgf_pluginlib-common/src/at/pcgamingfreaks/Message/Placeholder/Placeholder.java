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

package at.pcgamingfreaks.Message.Placeholder;

import at.pcgamingfreaks.Message.Placeholder.Processors.IPlaceholderProcessor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class Placeholder
{
	/**
	 * Will auto increment the parameter index. index = highest_index + 1. highest_index might be increased if parameter index has been set manually for one placeholder.
	 * If multiple Placeholders are registered with the same call, all placeholders with index -1 will be incremented individually.
	 */
	public static final int AUTO_INCREMENT_INDIVIDUALLY = -1;
	/**
	 * Will auto increment the parameter index. index = highest_index + 1. highest_index might be increased if parameter index has been set manually for one placeholder.
	 * If multiple Placeholders are registered with the same call, all placeholders with index -2 will use the same value, until another value is used, then it will autoincrement once and then continues to use that index for all following -2 indices.
	 */
	public static final int AUTO_INCREMENT_GROUP = -2;
	/**
	 * Will auto increment the parameter index. index = highest_index + 1. highest_index might be increased if parameter index has been set manually for one placeholder.
	 * If multiple Placeholders are registered with the same call, all placeholders with index -3 will use the same value, which will be calculated the first time a -3 index is encountered.
	 */
	public static final int AUTO_INCREMENT_PATCH = -3;
	/**
	 * Will use the last used parameter index.
	 */
	public static final int USE_LAST = -4;

	/**
	 * Will use the highest used parameter index.
	 */
	public static final int HIGHEST_USED = -5;

	public static final int MAX_PLACEHOLDERS = 256;

	private static final Pattern PATTERN_PLACEHOLDER_NAME = Pattern.compile("\\{[\\w-]+}");

	public Placeholder(final @NotNull String name)
	{
		this(name, null);
	}

	public Placeholder(final @NotNull String name, final @Nullable IPlaceholderProcessor processor)
	{
		this(name, processor, AUTO_INCREMENT_PATCH);
	}

	public Placeholder(final @NotNull String name, final @Nullable IPlaceholderProcessor processor, boolean regex)
	{
		this(name, processor, AUTO_INCREMENT_PATCH, regex);
	}

	public Placeholder(final @NotNull String name, final @Nullable IPlaceholderProcessor processor, int parameterIndex)
	{
		this(name, processor, parameterIndex, false);
	}

	public Placeholder(@NotNull String name, final @Nullable IPlaceholderProcessor processor, int parameterIndex, boolean regex)
	{
		if (parameterIndex < HIGHEST_USED)
		{
			throw new IllegalArgumentException("Invalid parameter index (" + parameterIndex + ") for placeholder '" + name + "'");
		}
		if (regex)
		{
			if(!(name.startsWith("\\{") && name.endsWith("}")))
			{ // All placeholders musst be between {}
				name = "\\{" + name + '}';
			}
		}
		else
		{
			if(!(name.startsWith("{") && name.endsWith("}")))
			{ // All placeholders musst be between {}
				name = '{' + name + '}';
			}
			if (!PATTERN_PLACEHOLDER_NAME.matcher(name).matches())
			{
				throw new IllegalArgumentException("All placeholder names musst match the pattern: " + PATTERN_PLACEHOLDER_NAME.pattern());
			}
		}
		this.name = name;
		this.processor = processor;
		this.parameterIndex = parameterIndex;
		this.regex = regex;
	}

	/**
	 * The name of the placeholder. If the value is not surrounded by {}, they will be added automatically.
	 */
	private final String name;

	/**
	 * The placeholder processor that should be used for this placeholder.
	 */
	private final IPlaceholderProcessor processor;

	/**
	 * The parameter index of the data source. Values < 0 will get special treatment, please check {@link Placeholder#AUTO_INCREMENT_INDIVIDUALLY}, {@link Placeholder#AUTO_INCREMENT_GROUP}, {@link Placeholder#AUTO_INCREMENT_PATCH}, {@link Placeholder#USE_LAST} and  {@link Placeholder#HIGHEST_USED} for more details.
	 */
	private final int parameterIndex;

	/**
	 * Defines if the placeholder name is to be interpreted as a regex or just a name.
	 */
	private final boolean regex;
}