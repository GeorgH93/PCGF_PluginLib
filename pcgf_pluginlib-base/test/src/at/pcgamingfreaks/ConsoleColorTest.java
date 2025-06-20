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

package at.pcgamingfreaks;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConsoleColorTest
{
	@Test
	public void testValues()
	{
		final String start = ConsoleColor.ESC_SEQUENCE + "[", end = "m";
		assertEquals("The format string should match", start + 0 + end, ConsoleColor.RESET.toString());
		assertEquals("The format string should match", start + 1 + end, ConsoleColor.BOLD.toString());
		assertEquals("The format string should match", start + 30 + end, ConsoleColor.BLACK.toString());
		assertEquals("The format string should match", start + 31 + end, ConsoleColor.RED.toString());
		assertEquals("The format string should match", start + 32 + end, ConsoleColor.GREEN.toString());
		assertEquals("The format string should match", start + 33 + end, ConsoleColor.YELLOW.toString());
		assertEquals("The format string should match", start + 34 + end, ConsoleColor.BLUE.toString());
		assertEquals("The format string should match", start + 35 + end, ConsoleColor.PURPLE.toString());
		assertEquals("The format string should match", start + 36 + end, ConsoleColor.CYAN.toString());
		assertEquals("The format string should match", start + 37 + end, ConsoleColor.WHITE.toString());
		assertEquals("The format string should match", start + 39 + end, ConsoleColor.DEFAULT.toString());
		assertEquals("The format string should match", start + 40 + end, ConsoleColor.BACKGROUND_BLACK.toString());
		assertEquals("The format string should match", start + 41 + end, ConsoleColor.BACKGROUND_RED.toString());
		assertEquals("The format string should match", start + 42 + end, ConsoleColor.BACKGROUND_GREEN.toString());
		assertEquals("The format string should match", start + 43 + end, ConsoleColor.BACKGROUND_YELLOW.toString());
		assertEquals("The format string should match", start + 44 + end, ConsoleColor.BACKGROUND_BLUE.toString());
		assertEquals("The format string should match", start + 45 + end, ConsoleColor.BACKGROUND_PURPLE.toString());
		assertEquals("The format string should match", start + 46 + end, ConsoleColor.BACKGROUND_CYAN.toString());
		assertEquals("The format string should match", start + 47 + end, ConsoleColor.BACKGROUND_WHITE.toString());
		assertEquals("The format string should match", start + 49 + end, ConsoleColor.BACKGROUND_DEFAULT.toString());
		assertEquals("The format string should match", start + 36 + end, ConsoleColor.valueOf("CYAN").toString());
	}
}