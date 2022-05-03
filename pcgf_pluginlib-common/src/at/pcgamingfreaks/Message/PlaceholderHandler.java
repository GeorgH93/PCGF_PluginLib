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

package at.pcgamingfreaks.Message;

import at.pcgamingfreaks.Message.Placeholder.MessageComponentPlaceholderEngine;
import at.pcgamingfreaks.Message.Placeholder.Placeholder;
import at.pcgamingfreaks.Message.Placeholder.Processors.IPlaceholderProcessor;
import at.pcgamingfreaks.Message.Placeholder.StringPlaceholderEngine;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

class PlaceholderHandler
{
	private final StringPlaceholderEngine legacyPlaceholderEngine;
	private final MessageComponentPlaceholderEngine messageComponentPlaceholderEngine;
	private int nextParameterIndex = 0;
	@Getter private boolean prepared = false;

	public PlaceholderHandler(Message message)
	{
		legacyPlaceholderEngine = new StringPlaceholderEngine(message.fallback);
		messageComponentPlaceholderEngine = new MessageComponentPlaceholderEngine(message);
	}

	public void register(Placeholder... placeholders)
	{
		int patchAutoIndex = -1, lastIndexRequested = -99, lastIndexUsed = Math.max(nextParameterIndex - 1, 0);
		for(Placeholder placeholder : placeholders)
		{
			int parameterIndex = placeholder.getParameterIndex();
			switch(parameterIndex)
			{
				case Placeholder.AUTO_INCREMENT_INDIVIDUALLY:
					parameterIndex = nextParameterIndex;
					break;
				case Placeholder.AUTO_INCREMENT_GROUP:
					if (lastIndexRequested != placeholder.getParameterIndex()) parameterIndex = nextParameterIndex;
					else parameterIndex = lastIndexUsed;
					break;
				case Placeholder.AUTO_INCREMENT_PATCH:
					if (patchAutoIndex == -1) patchAutoIndex = nextParameterIndex;
					parameterIndex = patchAutoIndex;
					break;
				case Placeholder.USE_LAST:
					parameterIndex = lastIndexUsed;
					break;
				case Placeholder.HIGHEST_USED:
					parameterIndex = Math.max(nextParameterIndex - 1, 0);
					break;
			}
			lastIndexRequested = placeholder.getParameterIndex();
			lastIndexUsed = parameterIndex;
			if(placeholder.isRegex())
			{
				registerPlaceholderRegex(placeholder.getName(), parameterIndex, placeholder.getProcessor());
			}
			else
			{
				registerPlaceholder(placeholder.getName(), parameterIndex, placeholder.getProcessor());
			}
		}
	}

	private void registerPlaceholder(@NotNull String placeholder, int parameterIndex, IPlaceholderProcessor placeholderProcessor)
	{
		if(parameterIndex < 0) throw new IllegalArgumentException("Placeholder parameter index must be a positive number!");
		legacyPlaceholderEngine.registerPlaceholder(placeholder, parameterIndex, placeholderProcessor);
		messageComponentPlaceholderEngine.registerPlaceholder(placeholder, parameterIndex, placeholderProcessor);
		nextParameterIndex = Math.max(nextParameterIndex, parameterIndex + 1);
	}

	private void registerPlaceholderRegex(@NotNull @Language("RegExp") String placeholder, int parameterIndex, IPlaceholderProcessor placeholderProcessor)
	{
		if(parameterIndex < 0) throw new IllegalArgumentException("Placeholder parameter index must be a positive number!");
		legacyPlaceholderEngine.registerPlaceholderRegex(placeholder, parameterIndex, placeholderProcessor);
		messageComponentPlaceholderEngine.registerPlaceholderRegex(placeholder, parameterIndex, placeholderProcessor);
		nextParameterIndex = Math.max(nextParameterIndex, parameterIndex + 1);
	}

	public String formatLegacy(Object... parameters)
	{
		return legacyPlaceholderEngine.processPlaceholders(parameters);
	}

	public String format(Object... parameters)
	{
		return messageComponentPlaceholderEngine.processPlaceholders(parameters);
	}

	public void prepare()
	{
		messageComponentPlaceholderEngine.prepare();
		prepared = true;
	}
}