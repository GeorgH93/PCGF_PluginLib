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

package at.pcgamingfreaks.Calendar;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents to difference between two points in times.
 */
public class TimeSpan
{
	public static final byte YEAR = 0, MONTH = 1, DAY = 2, HOUR = 3, MINUTE = 4, SECOND = 5, TOTAL_DAYS = 6;
	private static final int[] CalTypes = new int[] { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH };
	private static String[] timeUnitNames = new String[] { "year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds" };
	private int[] timeSpan;

	//region TimeSpan constructors

	/**
	 * Initiate a new TimeSpan object.
	 * @param time The unix time that should be compared with now.
	 */
	public TimeSpan(long time)
	{
		this(time, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param time The unix time that should be compared with now.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(long time, boolean fastMode)
	{
		this(time, System.currentTimeMillis(), fastMode);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The unix time that should be the first point in the calculation.
	 * @param timeTo The unix time that should be the second point in the calculation.
	 */
	public TimeSpan(long timeFrom, long timeTo)
	{
		this(timeFrom, timeTo, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The unix time that should be the first point in the calculation.
	 * @param timeTo The unix time that should be the second point in the calculation.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(long timeFrom, long timeTo, boolean fastMode)
	{
		this(timeFrom, null, timeTo, null, fastMode);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param time The date that should be compared with now.
	 */
	public TimeSpan(Date time)
	{
		this(time, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param time The date that should be compared with now.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(Date time, boolean fastMode)
	{
		this(time, new Date(), fastMode);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The date that should be the first point in the calculation.
	 * @param timeTo The date that should be the second point in the calculation.
	 */
	public TimeSpan(Date timeFrom, Date timeTo)
	{
		this(timeFrom, timeTo, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The date that should be the first point in the calculation.
	 * @param timeTo The date that should be the second point in the calculation.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(Date timeFrom, Date timeTo, boolean fastMode)
	{
		this(timeFrom.getTime(), null, timeTo.getTime(), null, fastMode);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param time The date that should be compared with now.
	 */
	public TimeSpan(Calendar time)
	{
		this(time, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param time The date that should be compared with now.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(Calendar time, boolean fastMode)
	{
		this(time, Calendar.getInstance(), fastMode);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The date that should be the first point in the calculation.
	 * @param timeTo The date that should be the second point in the calculation.
	 */
	public TimeSpan(Calendar timeFrom, Calendar timeTo)
	{
		this(timeFrom, timeTo, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The date that should be the first point in the calculation.
	 * @param timeTo The date that should be the second point in the calculation.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(Calendar timeFrom, Calendar timeTo, boolean fastMode)
	{
		this(timeFrom.getTimeInMillis(), (Calendar) timeFrom.clone(), timeTo.getTimeInMillis(), timeTo, fastMode);
	}

	//region calculation
	@SuppressWarnings("MagicConstant")
	private TimeSpan(long from, @Nullable Calendar fromDate, long to, @Nullable Calendar toDate, boolean fastMode)
	{
		long totalDiffSeconds = ((to > from) ? (to - from) : (from - to)) / 1000L;
		timeSpan = new int[] { -1, -1, -1, 0, 0, 0, (int) ((totalDiffSeconds) / 86400L) };
		if(fastMode)
		{
			timeSpan[YEAR] = timeSpan[TOTAL_DAYS] / 365;
			timeSpan[DAY] = timeSpan[TOTAL_DAYS] - timeSpan[YEAR] * 365;
			timeSpan[MONTH] = (int)(timeSpan[DAY] / (365/12.0));
			timeSpan[DAY] -= timeSpan[MONTH] * (365/12.0);
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
			for(int i = 0, dayOfMonth = fromDate.get(Calendar.DAY_OF_MONTH); i < CalTypes.length; i++)
			{
				while((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate)))
				{
					fromDate.add(CalTypes[i], future ? 1 : -1);
					if(CalTypes[i] != Calendar.DAY_OF_MONTH && fromDate.get(Calendar.DAY_OF_MONTH) < dayOfMonth && fromDate.get(Calendar.DAY_OF_MONTH) < fromDate.getActualMaximum(Calendar.DAY_OF_MONTH))
					{
						fromDate.set(Calendar.DAY_OF_MONTH, Math.min(fromDate.getActualMaximum(Calendar.DAY_OF_MONTH), dayOfMonth));
					}
					timeSpan[i]++;
				}
				fromDate.add(CalTypes[i], future ? -1 : 1);
			}
		}

		int secondsOnDay = (int) (totalDiffSeconds % 86400);
		timeSpan[HOUR] = secondsOnDay / 3600;
		timeSpan[MINUTE] = (secondsOnDay / 60) % 60;
		timeSpan[SECOND] = secondsOnDay % 60;
	}
	//endregion
	//endregion

	//region TimeSpanToString methods
	@Override
	public String toString()
	{
		return toString(timeUnitNames);
	}

	/**
	 * @param unitNames Names for the time unites to be used. Array must contain 12 elements.
	 *                  new String[] { "year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds" }
	 * @return The TimeSpan converted to a string
	 */
	public String toString(@NotNull String[] unitNames)
	{
		Validate.notNull(unitNames);
		Validate.isTrue(unitNames.length == 12, "Not enough unit names given.");

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

	//region Getters
	public int getYears()
	{
		return timeSpan[YEAR];
	}

	public int getMonths()
	{
		return timeSpan[MONTH];
	}

	public int getDays()
	{
		return timeSpan[DAY];
	}

	public int getTotalDays()
	{
		return timeSpan[TOTAL_DAYS];
	}

	public int getHours()
	{
		return timeSpan[HOUR];
	}

	public int getMinutes()
	{
		return timeSpan[MINUTE];
	}

	public int getSeconds()
	{
		return timeSpan[SECOND];
	}

	public int[] getArray()
	{
		return timeSpan.clone();
	}
	//endregion

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof TimeSpan && Arrays.equals(((TimeSpan) obj).timeSpan, this.timeSpan);
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(this.timeSpan);
	}
}