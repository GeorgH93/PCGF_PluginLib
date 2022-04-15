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

package at.pcgamingfreaks.Message.Placeholder.Processors;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

abstract class BaseParameterTypeAwarePlaceholderProcessor<T extends IPlaceholderProcessor> implements IPlaceholderProcessor
{
	protected final Map<Class<?>, T> typeMap = new HashMap<>();

	protected BaseParameterTypeAwarePlaceholderProcessor()
	{}

	public void add(Class<?> type, T processor)
	{
		typeMap.put(type, processor);
	}

	@Override
	public @NotNull String process(Object parameter)
	{
		if(parameter == null) return "null";
		IPlaceholderProcessor processor = typeMap.get(parameter.getClass());
		if(processor != null)
		{
			return processor.process(parameter);
		}
		return parameter.toString();
	}
}