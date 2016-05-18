/*
 *   Copyright (C) 2016 GeorgH93
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConsoleColorTest
{
	@Test
	public void testValues()
	{
		final String start = ConsoleColor.ESC_SEQUENCE + "[", end = "m";
		assertEquals(start + 0 + end, ConsoleColor.RESET.toString());
		assertEquals(start + 1 + end, ConsoleColor.BOLD.toString());
		assertEquals(start + 30 + end, ConsoleColor.BLACK.toString());
		assertEquals(start + 31 + end, ConsoleColor.RED.toString());
		assertEquals(start + 32 + end, ConsoleColor.GREEN.toString());
		assertEquals(start + 33 + end, ConsoleColor.YELLOW.toString());
		assertEquals(start + 34 + end, ConsoleColor.BLUE.toString());
		assertEquals(start + 35 + end, ConsoleColor.PURPLE.toString());
		assertEquals(start + 36 + end, ConsoleColor.CYAN.toString());
		assertEquals(start + 37 + end, ConsoleColor.WHITE.toString());
		assertEquals(start + 39 + end, ConsoleColor.DEFAULT.toString());
		assertEquals(start + 40 + end, ConsoleColor.BACKGROUND_BLACK.toString());
		assertEquals(start + 41 + end, ConsoleColor.BACKGROUND_RED.toString());
		assertEquals(start + 42 + end, ConsoleColor.BACKGROUND_GREEN.toString());
		assertEquals(start + 43 + end, ConsoleColor.BACKGROUND_YELLOW.toString());
		assertEquals(start + 44 + end, ConsoleColor.BACKGROUND_BLUE.toString());
		assertEquals(start + 45 + end, ConsoleColor.BACKGROUND_PURPLE.toString());
		assertEquals(start + 46 + end, ConsoleColor.BACKGROUND_CYAN.toString());
		assertEquals(start + 47 + end, ConsoleColor.BACKGROUND_WHITE.toString());
		assertEquals(start + 49 + end, ConsoleColor.BACKGROUND_DEFAULT.toString());
	}
}