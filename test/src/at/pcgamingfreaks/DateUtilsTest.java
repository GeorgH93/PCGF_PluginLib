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

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DateUtilsTest
{
	private static final long TIME1 = 1501948524000L, TIME2 = 1470412524000L, TIME3 = TIME2 + 1000L;

	@BeforeClass
	public static void prepareTestData()
	{
		new DateUtils();
	}

	@Test
	public void testTimeSpanLong()
	{
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, DateUtils.timeSpan(TIME1, TIME2));
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, DateUtils.timeSpan(TIME1, TIME3));
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, DateUtils.timeSpan(TIME2, TIME1));
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, DateUtils.timeSpan(TIME3, TIME1));
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, DateUtils.timeSpan(System.currentTimeMillis() - 1000L));
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, DateUtils.timeSpan(System.currentTimeMillis() - 1000L, true));
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, DateUtils.timeSpan(TIME1, TIME2, true));
		assertArrayEquals(new int[] { 0, 11, 29, 23, 59, 59, 364 }, DateUtils.timeSpan(TIME1, TIME3, true));
	}

	@Test
	public void testTimeSpanDate()
	{
		Date date1 = new Date(TIME1), date2 = new Date(TIME2), date3 = new Date(TIME3);
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, DateUtils.timeSpan(date1, date2));
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, DateUtils.timeSpan(date1, date3));
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, DateUtils.timeSpan(date2, date1));
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, DateUtils.timeSpan(date3, date1));
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0}, DateUtils.timeSpan(new Date(System.currentTimeMillis() - 1000L)));
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, DateUtils.timeSpan(new Date(System.currentTimeMillis() - 1000L), true));
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, DateUtils.timeSpan(date1, date2, true));
		assertArrayEquals(new int[] { 0, 11, 29, 23, 59, 59, 364 }, DateUtils.timeSpan(date1, date3, true));
	}

	@Test
	public void testTimeSpanCalendar()
	{
		Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), calNow = Calendar.getInstance();
		cal1.setTimeInMillis(TIME1);
		cal2.setTimeInMillis(TIME2);
		cal3.setTimeInMillis(TIME3);
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, DateUtils.timeSpan(cal1, cal2));
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364}, DateUtils.timeSpan(cal1, cal3));
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, DateUtils.timeSpan(cal2, cal1));
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, DateUtils.timeSpan(cal3, cal1));
		calNow.setTimeInMillis(System.currentTimeMillis() - 1000L);
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, DateUtils.timeSpan(calNow));
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, DateUtils.timeSpan(calNow, true));
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, DateUtils.timeSpan(cal1, cal2, true));
		assertArrayEquals(new int[] { 0, 11, 29, 23, 59, 59, 364}, DateUtils.timeSpan(cal1, cal3, true));
		cal1.set(Calendar.YEAR, 2017);
		cal1.set(Calendar.MONTH, Calendar.JANUARY);
		cal1.set(Calendar.DAY_OF_MONTH, 31);
		cal2.set(Calendar.YEAR, 2017);
		cal2.set(Calendar.MONTH, Calendar.MARCH);
		cal2.set(Calendar.DAY_OF_MONTH, 1);
		assertArrayEquals(new int[] { 0, 1, 1, 0, 0, 0, 29 }, DateUtils.timeSpan(cal1, cal2));
		assertArrayEquals(new int[] { 0, 0, 29, 0, 0, 0, 29 }, DateUtils.timeSpan(cal1, cal2, true));
	}

	@Test
	public void testTimeSpanToString()
	{
		assertEquals("1 year", DateUtils.timeSpanToString(new int[] { 1, 0, 0, 0, 0, 0, 365 }));
		assertEquals("11 months 30 days 23 hours 59 minutes 59 seconds", DateUtils.timeSpanToString(new int[] { 0, 11, 30, 23, 59, 59, 364 }));
		assertEquals("0 seconds", DateUtils.timeSpanToString(new int[] { 0, 0, 0, 0, 0, 0, 0 }));
	}
}