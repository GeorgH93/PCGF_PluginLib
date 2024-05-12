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

package at.pcgamingfreaks.Calendar;

import org.jetbrains.annotations.NotNull;

public class BasicTimeSpanFormat implements TimeSpanFormat
{
	final private String[] unitNames;

	public BasicTimeSpanFormat()
	{
		this(new String[] { "year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds" });
	}

	public BasicTimeSpanFormat(final @NotNull String[] timeUnitNames)
	{
		if(timeUnitNames.length != 12) throw new IllegalArgumentException("Wrong amount of unit names given. Please make sure that the array is in the right format.\n" +
				"Like: new String[] { \"year\", \"years\", \"month\", \"months\", \"day\", \"days\", \"hour\", \"hours\", \"minute\", \"minutes\", \"second\", \"seconds\" }");
		unitNames = timeUnitNames.clone();
	}

	@Override
	public String format(TimeSpan span)
	{
		int[] timeSpan = span.getArrayNoCopy();
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < TimeSpan.TOTAL_DAYS; i++)
		{
			if(timeSpan[i] > 0 || (i == TimeSpan.SECOND && stringBuilder.length() == 0))
			{
				if(stringBuilder.length() > 0) stringBuilder.append(' ');
				stringBuilder.append(timeSpan[i]);
				stringBuilder.append(' ');
				stringBuilder.append((timeSpan[i] == 1) ? unitNames[i*2] : unitNames[i*2+1]);
			}
		}
		return stringBuilder.toString();
	}
}