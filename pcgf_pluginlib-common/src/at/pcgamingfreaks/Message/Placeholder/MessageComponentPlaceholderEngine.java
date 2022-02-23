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

import at.pcgamingfreaks.Message.Message;
import at.pcgamingfreaks.Message.MessageComponent;
import at.pcgamingfreaks.Message.Placeholder.Processors.IFormattedPlaceholderProcessor;
import at.pcgamingfreaks.Message.Placeholder.Processors.IPlaceholderProcessor;
import at.pcgamingfreaks.Util.PatternPreservingStringSplitter;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MessageComponentPlaceholderEngine
{
	private StringPlaceholderEngine placeholderEngine;
	private final Message message;
	private final List<PlaceholderData> tmpPlaceholderList = new ArrayList<>();

	public MessageComponentPlaceholderEngine(Message message)
	{
		this.message = message;
	}

	public void registerPlaceholder(final @NotNull String placeholder, final int placeholderIndex, final @Nullable IPlaceholderProcessor placeholderProcessor)
	{
		tmpPlaceholderList.add(new PlaceholderData(false, placeholder, placeholderIndex, placeholderProcessor));
	}

	public void registerPlaceholderRegex(final @NotNull @Language("RegExp") String placeholderRegex, final int placeholderIndex, final @Nullable IPlaceholderProcessor placeholderProcessor)
	{
		tmpPlaceholderList.add(new PlaceholderData(true, placeholderRegex, placeholderIndex, placeholderProcessor));
	}

	public void prepare()
	{
		String json = message.getJson();
		MessageComponent[] messageComponents = null;
		List<String> placeholdersToReplace = new ArrayList<>();
		for(PlaceholderData placeholderData : tmpPlaceholderList)
		{
			if(placeholderData.isFormatted())
			{
				if(messageComponents == null) messageComponents = message.getMessageComponents();
				String placeholder = placeholderData.placeholder;
				if(!placeholderData.isRegex()) placeholder = "\\" + placeholder; // Escape the { at the start of the placeholder

				PatternPreservingStringSplitter splitter = new PatternPreservingStringSplitter(placeholder);
				for(MessageComponent component : messageComponents)
				{
					component.split(splitter);
				}
				placeholdersToReplace.add(placeholder);
			}
		}
		if(messageComponents != null)
		{
			json = MessageComponent.toJSON(messageComponents);
			for(String toReplace : placeholdersToReplace)
			{
				String pattern = "\\{\"text\":\"(" + toReplace + ")\"}";
				json = json.replaceAll(pattern, "$1");
			}
		}

		placeholderEngine = new StringPlaceholderEngine(json);
		for(PlaceholderData placeholderData : tmpPlaceholderList)
		{
			if(placeholderData.isRegex())
			{
				placeholderEngine.registerPlaceholderRegex(placeholderData.getPlaceholder(), placeholderData.getPlaceholderIndex(), placeholderData.getPlaceholderProcessor());
			}
			else
			{
				placeholderEngine.registerPlaceholder(placeholderData.getPlaceholder(), placeholderData.getPlaceholderIndex(), placeholderData.getPlaceholderProcessor());
			}
		}
	}

	public String processPlaceholders(Object... parameters)
	{
		if (placeholderEngine == null) prepare();
		return placeholderEngine.processPlaceholders(parameters);
	}


	@Getter
	private static class PlaceholderData
	{
		private final boolean regex, formatted;
		private final String placeholder;
		private final int placeholderIndex;
		private final IPlaceholderProcessor placeholderProcessor;

		public PlaceholderData(final boolean regex, final @NotNull String placeholder, final int placeholderIndex, final @Nullable IPlaceholderProcessor placeholderProcessor)
		{
			this.regex = regex;
			this.placeholder = placeholder;
			this.placeholderIndex = placeholderIndex;
			if(placeholderProcessor instanceof IFormattedPlaceholderProcessor)
			{
				this.placeholderProcessor = new MessageComponentToStringPlaceholderProcessor((IFormattedPlaceholderProcessor) placeholderProcessor);
				this.formatted = true;
			}
			else
			{
				this.placeholderProcessor = new StringPlaceholderJsonEscapeProcessor(placeholderProcessor);
				this.formatted = false;
			}
		}
	}
}