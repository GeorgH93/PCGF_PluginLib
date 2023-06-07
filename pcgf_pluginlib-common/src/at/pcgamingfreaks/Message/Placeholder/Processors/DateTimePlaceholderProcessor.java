/*
 *   Copyright (C) 2023 GeorgH93
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
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

@AllArgsConstructor
public final class DateTimePlaceholderProcessor implements IPlaceholderProcessor
{
	private final DateTimeFormatter formatter;

	public DateTimePlaceholderProcessor(final @NotNull String format)
	{
		formatter = DateTimeFormatter.ofPattern(format);
	}

	@Override
	public @NotNull String process(@Nullable Object parameter)
	{
		if (parameter instanceof TemporalAccessor)
			return formatter.format((TemporalAccessor) parameter);
		return "unknown";
	}
}