/*
 *   Copyright (C) 2024 GeorgH93
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

package at.pcgamingfreaks.Util;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternPreservingStringSplitter
{
	@Getter private final Pattern placeholderPattern;

	public PatternPreservingStringSplitter(final @NotNull @Language("RegExp") String pattern)
	{
		placeholderPattern = Pattern.compile(pattern);
	}

	public ArrayList<String> split(final @NotNull String input)
	{
		ArrayList<String> split = new ArrayList<>();

		Matcher matcher = placeholderPattern.matcher(input);

		int last_match = 0;
		while (matcher.find())
		{
			String sub = input.substring(last_match, matcher.start());
			if(!sub.isEmpty()) split.add(sub);
			split.add(matcher.group());
			last_match = matcher.end();
		}

		split.add(input.substring(last_match));
		return split;
	}
}