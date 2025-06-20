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

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TimeSpanTest
{
	private static final long TIME1 = 1501948524000L, TIME2 = 1470412524000L, TIME3 = TIME2 + 1000L, TIME4 = 1580508509763L;

	@Test
	public void testTimeSpanLong()
	{
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, new TimeSpan(TIME1, TIME2).getArray());
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, new TimeSpan(TIME1, TIME3).getArray());
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, new TimeSpan(TIME2, TIME1).getArray());
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, new TimeSpan(TIME3, TIME1).getArray());
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, new TimeSpan(System.currentTimeMillis() - 1000L).getArray());
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, new TimeSpan(System.currentTimeMillis() - 1000L, true).getArray());
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, new TimeSpan(TIME1, TIME2, true).getArray());
		assertArrayEquals(new int[] { 0, 11, 29, 23, 59, 59, 364 }, new TimeSpan(TIME1, TIME3, true).getArray());
	}

	@Test
	public void testTimeSpanDate()
	{
		Date date1 = new Date(TIME1), date2 = new Date(TIME2), date3 = new Date(TIME3);
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, new TimeSpan(date1, date2).getArray());
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, new TimeSpan(date1, date3).getArray());
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, new TimeSpan(date2, date1).getArray());
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, new TimeSpan(date3, date1).getArray());
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, new TimeSpan(new Date(System.currentTimeMillis() - 1000L)).getArray());
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, new TimeSpan(new Date(System.currentTimeMillis() - 1000L), true).getArray());
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, new TimeSpan(date1, date2, true).getArray());
		assertArrayEquals(new int[] { 0, 11, 29, 23, 59, 59, 364 }, new TimeSpan(date1, date3, true).getArray());
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, new TimeSpan(new Date(TIME4), new Date(TIME4 - 1000L)).getArray());
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, new TimeSpan(new Date(TIME4), new Date(TIME4 - 1000L), true).getArray());
	}

	@Test
	public void testTimeSpanCalendar()
	{
		Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), calNow = Calendar.getInstance();
		cal1.setTimeInMillis(TIME1);
		cal2.setTimeInMillis(TIME2);
		cal3.setTimeInMillis(TIME3);
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, new TimeSpan(cal1, cal2).getArray());
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, new TimeSpan(cal1, cal3).getArray());
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, new TimeSpan(cal2, cal1).getArray());
		assertArrayEquals(new int[] { 0, 11, 30, 23, 59, 59, 364 }, new TimeSpan(cal3, cal1).getArray());
		calNow.setTimeInMillis(System.currentTimeMillis() - 1000L);
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, new TimeSpan(calNow).getArray());
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 1, 0 }, new TimeSpan(calNow, true).getArray());
		assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 365 }, new TimeSpan(cal1, cal2, true).getArray());
		assertArrayEquals(new int[] { 0, 11, 29, 23, 59, 59, 364 }, new TimeSpan(cal1, cal3, true).getArray());
		cal1.set(Calendar.YEAR, 2017);
		cal1.set(Calendar.MONTH, Calendar.JANUARY);
		cal1.set(Calendar.DAY_OF_MONTH, 31);
		cal2.set(Calendar.YEAR, 2017);
		cal2.set(Calendar.MONTH, Calendar.MARCH);
		cal2.set(Calendar.DAY_OF_MONTH, 1);
		assertArrayEquals(new int[] { 0, 1, 1, 0, 0, 0, 29 }, new TimeSpan(cal1, cal2).getArray());
		assertArrayEquals(new int[] { 0, 0, 29, 0, 0, 0, 29 }, new TimeSpan(cal1, cal2, true).getArray());
	}

	@Test
	public void testTimeSpanToString()
	{
		assertEquals("1 year", new TimeSpan(TIME1, TIME2, true).toString());
		assertEquals("11 months 30 days 23 hours 59 minutes 59 seconds", new TimeSpan(TIME3, TIME1).toString());
		assertEquals("0 seconds", new TimeSpan(0, 0, true).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTimeSpanToStringWithError()
	{
		new TimeSpan(0, 0, true).toString(new String[] {});
	}

	@Test
	public void testGetters()
	{
		TimeSpan ts = new TimeSpan(TIME3, TIME1);
		assertEquals(0, ts.getYears());
		assertEquals(11, ts.getMonths());
		assertEquals(30, ts.getDays());
		assertEquals(23, ts.getHours());
		assertEquals(59, ts.getMinutes());
		assertEquals(59, ts.getSeconds());
		assertEquals(364, ts.getTotalDays());
	}

	@Test
	public void testHashCodeAndEquals()
	{
		TimeSpan ts1 = new TimeSpan(TIME3, TIME1), ts2 = new TimeSpan(TIME3, TIME1);
		assertEquals(ts1.hashCode(), ts2.hashCode());
		assertEquals(ts1, ts2);
	}
}