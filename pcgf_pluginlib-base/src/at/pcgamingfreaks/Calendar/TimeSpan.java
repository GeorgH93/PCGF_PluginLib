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

	private static final BasicTimeSpanFormat DEFAULT_TIME_SPAN_FORMAT = new BasicTimeSpanFormat();
	private static final int[] CAL_TYPES = new int[] { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH };
	private int[] timeSpan = new int[] { 0, 0, 0, 0, 0, 0, 0};

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
	public TimeSpan(@NotNull Date time)
	{
		this(time, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param time The date that should be compared with now.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(@NotNull Date time, boolean fastMode)
	{
		this(time, new Date(), fastMode);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The date that should be the first point in the calculation.
	 * @param timeTo The date that should be the second point in the calculation.
	 */
	public TimeSpan(@NotNull Date timeFrom, @NotNull Date timeTo)
	{
		this(timeFrom, timeTo, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The date that should be the first point in the calculation.
	 * @param timeTo The date that should be the second point in the calculation.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(@NotNull Date timeFrom, @NotNull Date timeTo, boolean fastMode)
	{
		this(timeFrom.getTime(), null, timeTo.getTime(), null, fastMode);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param time The date that should be compared with now.
	 */
	public TimeSpan(@NotNull Calendar time)
	{
		this(time, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param time The date that should be compared with now.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(@NotNull Calendar time, boolean fastMode)
	{
		this(time, Calendar.getInstance(), fastMode);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The date that should be the first point in the calculation.
	 * @param timeTo The date that should be the second point in the calculation.
	 */
	public TimeSpan(@NotNull Calendar timeFrom, @NotNull Calendar timeTo)
	{
		this(timeFrom, timeTo, false);
	}

	/**
	 * Initiate a new TimeSpan object.
	 * @param timeFrom The date that should be the first point in the calculation.
	 * @param timeTo The date that should be the second point in the calculation.
	 * @param fastMode If the fast mode is enabled the calculation will be done with less precision, but therefor much faster.
	 */
	public TimeSpan(@NotNull Calendar timeFrom, @NotNull Calendar timeTo, boolean fastMode)
	{
		this(timeFrom.getTimeInMillis(), timeFrom, timeTo.getTimeInMillis(), timeTo, fastMode);
	}

	//region calculation
	private TimeSpan(long from, @Nullable Calendar fromDate, long to, @Nullable Calendar toDate, boolean fastMode)
	{
		long totalDiffSeconds = totalDiffSeconds(from, to);
		if(fastMode)
		{
			calcFast(totalDiffSeconds);
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
			if(toDate.before(fromDate))
			{
				Calendar tmpDate = fromDate;
				fromDate = toDate;
				toDate = tmpDate;
			}
			calcCalendar(totalDiffSeconds, fromDate, toDate);
		}
	}

	private long totalDiffSeconds(long from, long to)
	{
		return ((to > from) ? (to - from) : (from - to)) / 1000L;
	}

	private void calcFast(long totalDiffSeconds)
	{
		timeSpan[TOTAL_DAYS] = (int) (totalDiffSeconds / 86400L);
		timeSpanDaysFast();
		timeSpanDayComponents(totalDiffSeconds);
	}

	private void calcCalendar(long totalDiffSeconds, @NotNull Calendar fromDate, @NotNull Calendar toDate)
	{
		timeSpan[TOTAL_DAYS] = (int) (totalDiffSeconds / 86400L);
		timeSpanDaysCalendar(fromDate, toDate);
		timeSpanDayComponents(totalDiffSeconds);
	}

	private void timeSpanDayComponents(long totalDiffSeconds)
	{
		int secondsOnDay = (int) (totalDiffSeconds % 86400);
		timeSpan[HOUR] = secondsOnDay / 3600;
		secondsOnDay -= timeSpan[HOUR] * 3600;
		timeSpan[MINUTE] = (secondsOnDay / 60);
		timeSpan[SECOND] = secondsOnDay % 60;
	}

	private void timeSpanDaysFast()
	{
		timeSpan[YEAR] = timeSpan[TOTAL_DAYS] / 365;
		timeSpan[DAY] = timeSpan[TOTAL_DAYS] - timeSpan[YEAR] * 365;
		timeSpan[MONTH] = (int)(timeSpan[DAY] / (365/12.0));
		timeSpan[DAY] -= timeSpan[MONTH] * (365/12.0);
	}

	@SuppressWarnings("MagicConstant")
	private void timeSpanDaysCalendar(@NotNull Calendar fromDate, @NotNull Calendar toDate)
	{
		timeSpan[YEAR] = timeSpan[MONTH] = timeSpan[DAY] = -1;
		Calendar from = (Calendar) fromDate.clone(), backup = (Calendar) from.clone();
		for(int i = 0, dayOfMonth = from.get(Calendar.DAY_OF_MONTH); i < CAL_TYPES.length; i++)
		{
			while(!from.after(toDate))
			{
				backup = (Calendar) from.clone();
				from.add(CAL_TYPES[i], 1);
				if(CAL_TYPES[i] != Calendar.DAY_OF_MONTH && from.get(Calendar.DAY_OF_MONTH) < dayOfMonth && from.get(Calendar.DAY_OF_MONTH) < from.getActualMaximum(Calendar.DAY_OF_MONTH))
				{
					from.set(Calendar.DAY_OF_MONTH, Math.min(from.getActualMaximum(Calendar.DAY_OF_MONTH), dayOfMonth));
				}
				timeSpan[i]++;
			}
			from = backup;
		}
	}
	//endregion

	private TimeSpan() {}
	//endregion

	//region static from functions
	/**
	 * Creates a {@link TimeSpan} object from an amount of milliseconds.
	 * The TimeSpan is created using the fast method.
	 *
	 * @param ms The amount of milliseconds that should be converted into a {@link TimeSpan}.
	 * @return The {@link TimeSpan} created from the given amount of milliseconds.
	 */
	public static @NotNull TimeSpan fromMilliseconds(long ms)
	{
		return fromSeconds(ms / 1000L);
	}

	/**
	 * Creates a {@link TimeSpan} object from an amount of seconds.
	 * The TimeSpan is created using the fast method.
	 *
	 * @param seconds The amount of seconds that should be converted into a {@link TimeSpan}.
	 * @return The {@link TimeSpan} created from the given amount of seconds.
	 */
	public static @NotNull TimeSpan fromSeconds(long seconds)
	{
		TimeSpan timeSpan = new TimeSpan();
		timeSpan.calcFast(seconds);
		return timeSpan;
	}
	//endregion

	//region TimeSpanToString methods
	@Override
	public String toString()
	{
		return toString(DEFAULT_TIME_SPAN_FORMAT);
	}

	/**
	 * @param unitNames Names for the time unites to be used. Array must contain 12 elements.
	 *                  new String[] { "year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds" }
	 * @return The TimeSpan converted to a string
	 */
	@Deprecated
	public String toString(@NotNull String[] unitNames)
	{
		return toString(new BasicTimeSpanFormat(unitNames));
	}

	public String toString(TimeSpanFormat timeSpanFormat)
	{
		return timeSpanFormat.format(this);
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

	/**
	 * Gets a copy of the internally used data array.
	 *
	 * @return The internal time span array.
	 */
	public @NotNull int[] getArray()
	{
		return timeSpan.clone();
	}

	/**
	 * Returns the internally used data array. Modifying the array will change the values of the TimeSpan.
	 *
	 * @return The internal time span array.
	 */
	public @NotNull int[] getArrayNoCopy() { return timeSpan; }
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