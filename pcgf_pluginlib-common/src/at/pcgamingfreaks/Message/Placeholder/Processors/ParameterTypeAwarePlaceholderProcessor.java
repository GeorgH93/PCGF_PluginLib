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

import java.util.HashMap;
import java.util.Map;

public final class ParameterTypeAwarePlaceholderProcessor implements IPlaceholderProcessor
{
	private final Map<Class<?>, IPlaceholderProcessor> typeMap = new HashMap<>();

	public ParameterTypeAwarePlaceholderProcessor()
	{}

	public void add(Class<?> type, IPlaceholderProcessor processor)
	{
		typeMap.put(type, processor);
	}

	@Override
	public String process(Object parameter)
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