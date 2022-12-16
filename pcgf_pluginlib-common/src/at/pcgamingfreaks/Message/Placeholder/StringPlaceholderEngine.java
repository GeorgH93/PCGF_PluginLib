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
import at.pcgamingfreaks.Util.StringUtils;
import at.pcgamingfreaks.Util.PatternPreservingStringSplitter;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

public class StringPlaceholderEngine
{
	@AllArgsConstructor
	static class Placeholder
	{
		int parameterIndex;
		IPlaceholderProcessor processor;
	}

	private static final PatternPreservingStringSplitter STRING_SPLITTER = new PatternPreservingStringSplitter("(?<!\\\\)\\{[\\w-]+}");

	private final String[] components;
	private final int[] componentsMap;
	private final ArrayList<Placeholder> placeholderProcessors = new ArrayList<>();

	public StringPlaceholderEngine(final @NotNull String input)
	{
		ArrayList<String> split = STRING_SPLITTER.split(input), components = new ArrayList<>();
		componentsMap = new int[split.size()];
		for(int componentId = 0, i = 0; i < split.size(); i++)
		{
			String val = split.get(i);
			if(val == null) continue;
			components.add(val);
			componentsMap[i] = componentId;
			for(int j = i + 1; j < split.size(); j++)
			{
				if(val.equals(split.get(j)))
				{
					split.set(j, null);
					componentsMap[j] = componentId;
				}
			}
			componentId++;
		}
		this.components = components.toArray(new String[0]);
	}

	public void registerPlaceholder(final @NotNull String placeholder, final int parameterIndex, final @Nullable IPlaceholderProcessor placeholderProcessor)
	{
		int placeholderIndexInMap = placeholderProcessors.size() | Integer.MIN_VALUE;
		int id = StringUtils.indexOf(components, placeholder);
		boolean contains = false;
		for(int i = 0; i < componentsMap.length; i++)
		{
			if(componentsMap[i] == id)
			{
				componentsMap[i] = placeholderIndexInMap;
				contains = true;
			}
		}
		if(contains)
		{
			placeholderProcessors.add(new Placeholder(parameterIndex, placeholderProcessor));
		}
	}

	public void registerPlaceholderRegex(final @NotNull @Language("RegExp") String placeholderRegex, final int parameterIndex, final @Nullable IPlaceholderProcessor placeholderProcessor)
	{
		int placeholderIndexInMap = placeholderProcessors.size() | Integer.MIN_VALUE;
		Collection<Integer> ids = StringUtils.indexOfAllMatching(components, placeholderRegex);
		boolean contains = false;
		for(int i = 0; i < componentsMap.length; i++)
		{
			if(ids.contains(componentsMap[i]))
			{
				componentsMap[i] = placeholderIndexInMap;
				contains = true;
			}
		}
		if(contains)
		{
			placeholderProcessors.add(new Placeholder(parameterIndex, placeholderProcessor));
		}
	}

	public String processPlaceholders(Object... parameters)
	{
		Object[] resolvedPlaceholders = new Object[placeholderProcessors.size()];

		for(int i = 0; i < placeholderProcessors.size(); i++)
		{
			Placeholder placeholder = placeholderProcessors.get(i);
			Object parameter = placeholder.parameterIndex >= parameters.length ? null : parameters[placeholder.parameterIndex];
			resolvedPlaceholders[i] = placeholder.processor != null ? placeholder.processor.process(parameter) : parameter;
		}

		return buildOutputString(resolvedPlaceholders);
	}

	private String buildOutputString(final Object[] resolvedPlaceholders)
	{
		StringBuilder stringBuilder = new StringBuilder();
		for(int componentId : componentsMap)
		{
			if(componentId < 0)
			{
				int placeholderId = componentId & Integer.MAX_VALUE;
				stringBuilder.append(resolvedPlaceholders[placeholderId]);
			}
			else
			{
				stringBuilder.append(components[componentId]);
			}
		}
		return stringBuilder.toString();
	}
}