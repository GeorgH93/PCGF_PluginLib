/*
 *   Copyright (C) 2017 GeorgH93
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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Date;

public class DateUtils
{
	public static final byte YEAR = 0, MONTH = 1, DAY = 2, HOUR = 3, MINUTE = 4, SECOND = 5, TOTAL_DAYS = 6;
	private static final int[] types = new int[] { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH };
	private static String[] timeUnitNames = new String[] { "year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds" };

	//region TimeSpan methods
	public static int[] timeSpan(long time)
	{
		return timeSpan(time, false);
	}

	public static int[] timeSpan(long time, boolean fastMode)
	{
		return timeSpan(time, System.currentTimeMillis(), fastMode);
	}

	public static int[] timeSpan(long timeFrom, long timeTo)
	{
		return timeSpan(timeFrom, timeTo, false);
	}

	public static int[] timeSpan(long timeFrom, long timeTo, boolean fastMode)
	{
		return timeSpan(timeFrom, null, timeTo, null, fastMode);
	}

	public static int[] timeSpan(Date time)
	{
		return timeSpan(time, false);
	}

	public static int[] timeSpan(Date time, boolean fastMode)
	{
		return timeSpan(time, new Date(), fastMode);
	}

	public static int[] timeSpan(Date timeFrom, Date timeTo)
	{
		return timeSpan(timeFrom, timeTo, false);
	}

	public static int[] timeSpan(Date timeFrom, Date timeTo, boolean fastMode)
	{
		return timeSpan(timeFrom.getTime(), null, timeTo.getTime(), null, fastMode);
	}

	public static int[] timeSpan(Calendar time)
	{
		return timeSpan(time, false);
	}

	public static int[] timeSpan(Calendar time, boolean fastMode)
	{
		return timeSpan(time, Calendar.getInstance(), fastMode);
	}

	public static int[] timeSpan(Calendar timeFrom, Calendar timeTo)
	{
		return timeSpan(timeFrom, timeTo, false);
	}

	public static int[] timeSpan(Calendar timeFrom, Calendar timeTo, boolean fastMode)
	{
		return timeSpan(timeFrom.getTimeInMillis(), (Calendar) timeFrom.clone(), timeTo.getTimeInMillis(), timeTo, fastMode);
	}

	//region calculation methods
	@SuppressWarnings("MagicConstant")
	private static int[] timeSpan(long from, @Nullable Calendar fromDate, long to, @Nullable Calendar toDate, boolean fastMode)
	{
		long totalDiffSeconds = ((to > from) ? (to - from) : (from - to)) / 1000L;
		int[] diff = new int[] { -1, -1, -1, 0, 0, 0, (int) ((totalDiffSeconds) / 86400L) };
		if(fastMode)
		{
			diff[YEAR] = diff[TOTAL_DAYS] / 365;
			diff[DAY] = diff[TOTAL_DAYS] - diff[YEAR] * 365;
			diff[MONTH] = (int)(diff[DAY] / (365/12.0));
			diff[DAY] -= diff[MONTH] * (365/12.0);
		}
		else
		{
			if(fromDate == null)
			{
				fromDate = Calendar.getInstance();
				fromDate.setTimeInMillis(from);
			}
			if(toDate == null)
			{
				toDate = Calendar.getInstance();
				toDate.setTimeInMillis(to);
			}

			boolean future = toDate.after(fromDate);
			for(int i = 0, dayOfMonth = fromDate.get(Calendar.DAY_OF_MONTH); i < types.length; i++)
			{
				while((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate)))
				{
					fromDate.add(types[i], future ? 1 : -1);
					if(types[i] != Calendar.DAY_OF_MONTH && fromDate.get(Calendar.DAY_OF_MONTH) < dayOfMonth && fromDate.get(Calendar.DAY_OF_MONTH) < fromDate.getActualMaximum(Calendar.DAY_OF_MONTH))
					{
						fromDate.set(Calendar.DAY_OF_MONTH, Math.min(fromDate.getActualMaximum(Calendar.DAY_OF_MONTH), dayOfMonth));
					}
					diff[i]++;
				}
				fromDate.add(types[i], future ? -1 : 1);
			}
		}

		int secondsOnDay = (int) (totalDiffSeconds % 86400);
		diff[HOUR] = secondsOnDay / 3600;
		diff[MINUTE] = (secondsOnDay / 60) % 60;
		diff[SECOND] = secondsOnDay % 60;

		return diff;
	}
	//endregion
	//endregion

	//region TimeSpanToString methods
	public static String timeSpanToString(@NotNull int[] timeSpan)
	{
		return timeSpanToString(timeSpan, timeUnitNames);
	}

	public static String timeSpanToString(@NotNull int[] timeSpan, @NotNull String[] unitNames)
	{
		Validate.notNull(timeSpan);
		Validate.notNull(unitNames);
		Validate.isTrue(timeSpan.length > 5);
		Validate.isTrue(unitNames.length == 12);

		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < TOTAL_DAYS; i++)
		{
			if(timeSpan[i] > 0 || (i == SECOND && stringBuilder.length() == 0))
			{
				if(stringBuilder.length() > 0) stringBuilder.append(' ');
				stringBuilder.append(timeSpan[i]);
				stringBuilder.append(' ');
				stringBuilder.append((timeSpan[i] == 1) ? unitNames[i*2] : unitNames[i*2+1]);
			}
		}
		return stringBuilder.toString();
	}
	//endregion
}