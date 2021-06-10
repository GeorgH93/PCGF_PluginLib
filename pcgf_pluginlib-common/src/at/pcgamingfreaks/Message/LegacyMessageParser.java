/*
 *   Copyright (C) 2021 GeorgH93
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


import at.pcgamingfreaks.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

final class LegacyMessageParser
{
	private final MessageBuilder<?,?,?> builder;
	private final StringBuilder stringBuilder = new StringBuilder(), wordBuilder = new StringBuilder();
	private final EnumSet<MessageFormat> formats = EnumSet.noneOf(MessageFormat.class);
	private MessageColor color;
	private String url;

	LegacyMessageParser(final MessageBuilder<?,?,?> builder)
	{
		this.builder = builder;
	}

	public void parse(final @NotNull String legacyMessage)
	{
		if(legacyMessage.length() == 0) return;
		reset();
		for(int i = 0; i < legacyMessage.length(); i++)
		{
			char c = legacyMessage.charAt(i);
			if(c == MessageColor.COLOR_CHAR && i + 1 < legacyMessage.length())
			{
				char formatChar = legacyMessage.charAt(++i);
				String rgbCode;
				if(MessageColor.isColorChar(formatChar) || MessageFormat.isFormatChar(formatChar) || formatChar == 'r' || formatChar == 'R') // handle single char formatting
				{
					append();
					processFormatting(formatChar);
				}
				else if(formatChar == 'x' || formatChar == 'X') // handle rgb colors
				{
					if(i + 12 < legacyMessage.length() && (rgbCode = toColorString(legacyMessage.substring(i + 1, i + 13))) != null)
					{
						i += 12;
						append();
						color = MessageColor.valueOf(rgbCode);
					}
					else if(i + 6 < legacyMessage.length() && (rgbCode = toColorString(legacyMessage.substring(i + 1, i + 7))) != null)
					{
						i += 6;
						append();
						color = MessageColor.valueOf(rgbCode);
					}
					else wordBuilder.append(c).append(formatChar);
				}
				else wordBuilder.append(c).append(formatChar);
			}
			else if(c == ' ' || c == '\n' || c == '\r' || c == '\t')
			{
				endWord();
				stringBuilder.append(c);
			}
			else wordBuilder.append(c);
		}
		append();
	}

	private void reset()
	{
		color = null;
		formats.clear();
		url = null;
		stringBuilder.setLength(0);
		wordBuilder.setLength(0);
	}

	private @Nullable String toColorString(final @NotNull String legacyRGB)
	{
		if(legacyRGB.length() == 12 && legacyRGB.matches("(" + MessageColor.COLOR_CHAR + "[\\da-fA-F]){6}"))
		{
			return '#' + legacyRGB.replaceAll(MessageColor.COLOR_CHAR + "", "");
		}
		else if(legacyRGB.length() == 6 && legacyRGB.matches("[\\da-fA-F]{6}"))
		{
			return '#' + legacyRGB.replaceAll(MessageColor.COLOR_CHAR + "", "");
		}
		return null;
	}

	private void processFormatting(final char formatChar)
	{
		if(formatChar == 'r' || formatChar == 'R') // Reset
		{
			color = null;
			formats.clear();
		}
		else if(MessageColor.isColorChar(formatChar)) // Color
		{
			color = MessageColor.getFromCode(formatChar);
		}
		else if(MessageFormat.isFormatChar(formatChar)) // Format
		{
			formats.add(MessageFormat.getFromCode(formatChar));
		}
	}

	private void endWord()
	{
		if(wordBuilder.length() == 0) return;
		String word = wordBuilder.toString();
		wordBuilder.setLength(0);
		if(StringUtils.URL_PATTERN.matcher(word).matches()) //URL
		{
			append();
			url = word;
			append();
			return;
		}
		stringBuilder.append(word);
	}

	private void append()
	{
		if(wordBuilder.length() > 0) endWord();
		if(stringBuilder.length() == 0) return;
		builder.append(stringBuilder.toString(), color, formats.toArray(new MessageFormat[0]));
		if(url != null)
		{
			builder.onClick(MessageClickEvent.ClickEventAction.OPEN_URL, url);
			url = null;
		}
		stringBuilder.setLength(0);
	}
}