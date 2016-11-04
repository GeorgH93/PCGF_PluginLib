/*
 * Copyright (C) 2016 GeorgH93
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Message;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

public class MessageColorTest
{
	@Test
	public void testIsColor()
	{
		assertTrue(MessageColor.BLACK.isColor());
		assertTrue(MessageColor.DARK_BLUE.isColor());
		assertTrue(MessageColor.DARK_GREEN.isColor());
		assertTrue(MessageColor.DARK_AQUA.isColor());
		assertTrue(MessageColor.DARK_RED.isColor());
		assertTrue(MessageColor.DARK_PURPLE.isColor());
		assertTrue(MessageColor.DARK_GRAY.isColor());
		assertTrue(MessageColor.GRAY.isColor());
		assertTrue(MessageColor.GOLD.isColor());
		assertTrue(MessageColor.BLUE.isColor());
		assertTrue(MessageColor.GREEN.isColor());
		assertTrue(MessageColor.AQUA.isColor());
		assertTrue(MessageColor.RED.isColor());
		assertTrue(MessageColor.LIGHT_PURPLE.isColor());
		assertTrue(MessageColor.YELLOW.isColor());
		assertTrue(MessageColor.WHITE.isColor());
		assertFalse(MessageColor.RESET.isColor());
		assertFalse(MessageColor.ITALIC.isColor());
		assertFalse(MessageColor.BOLD.isColor());
		assertFalse(MessageColor.MAGIC.isColor());
		assertFalse(MessageColor.UNDERLINE.isColor());
		assertFalse(MessageColor.STRIKETHROUGH.isColor());
	}

	@Test
	public void testIsFormat()
	{
		assertFalse(MessageColor.BLACK.isFormat());
		assertFalse(MessageColor.DARK_BLUE.isFormat());
		assertFalse(MessageColor.DARK_GREEN.isFormat());
		assertFalse(MessageColor.DARK_AQUA.isFormat());
		assertFalse(MessageColor.DARK_RED.isFormat());
		assertFalse(MessageColor.DARK_PURPLE.isFormat());
		assertFalse(MessageColor.DARK_GRAY.isFormat());
		assertFalse(MessageColor.GRAY.isFormat());
		assertFalse(MessageColor.GOLD.isFormat());
		assertFalse(MessageColor.BLUE.isFormat());
		assertFalse(MessageColor.GREEN.isFormat());
		assertFalse(MessageColor.AQUA.isFormat());
		assertFalse(MessageColor.RED.isFormat());
		assertFalse(MessageColor.LIGHT_PURPLE.isFormat());
		assertFalse(MessageColor.YELLOW.isFormat());
		assertFalse(MessageColor.WHITE.isFormat());
		assertFalse(MessageColor.RESET.isFormat());
		assertTrue(MessageColor.ITALIC.isFormat());
		assertTrue(MessageColor.BOLD.isFormat());
		assertTrue(MessageColor.MAGIC.isFormat());
		assertTrue(MessageColor.UNDERLINE.isFormat());
		assertTrue(MessageColor.STRIKETHROUGH.isFormat());
	}

	@Test
	public void testToString()
	{
		assertEquals(MessageColor.BLACK.toString(), "\u00A7" + 0);
		assertEquals(MessageColor.DARK_BLUE.toString(), "\u00A7" + 1);
		assertEquals(MessageColor.DARK_GREEN.toString(), "\u00A7" + 2);
		assertEquals(MessageColor.DARK_AQUA.toString(), "\u00A7" + 3);
		assertEquals(MessageColor.DARK_RED.toString(), "\u00A7" + 4);
		assertEquals(MessageColor.DARK_PURPLE.toString(), "\u00A7" + 5);
		assertEquals(MessageColor.DARK_GRAY.toString(), "\u00A7" + 8);
		assertEquals(MessageColor.GRAY.toString(), "\u00A7" + 7);
		assertEquals(MessageColor.GOLD.toString(), "\u00A7" + 6);
		assertEquals(MessageColor.BLUE.toString(), "\u00A7" + 9);
		assertEquals(MessageColor.GREEN.toString(), "\u00A7" + 'a');
		assertEquals(MessageColor.AQUA.toString(), "\u00A7" + 'b');
		assertEquals(MessageColor.RED.toString(), "\u00A7" + 'c');
		assertEquals(MessageColor.LIGHT_PURPLE.toString(), "\u00A7" + 'd');
		assertEquals(MessageColor.YELLOW.toString(), "\u00A7" + 'e');
		assertEquals(MessageColor.WHITE.toString(), "\u00A7" + 'f');
		assertEquals(MessageColor.RESET.toString(), "\u00A7" + 'r');
		assertEquals(MessageColor.ITALIC.toString(), "\u00A7" + 'o');
		assertEquals(MessageColor.BOLD.toString(), "\u00A7" + 'l');
		assertEquals(MessageColor.MAGIC.toString(), "\u00A7" + 'k');
		assertEquals(MessageColor.UNDERLINE.toString(), "\u00A7" + 'n');
		assertEquals(MessageColor.STRIKETHROUGH.toString(), "\u00A7" + 'm');
	}

	@Test
	public void testMessageColorArrayFromStylesArray()
	{
		assertNull("No styles should return null", MessageColor.messageColorArrayFromStylesArray((Enum[]) null));
		assertNull("No styles should return null", MessageColor.messageColorArrayFromStylesArray());
	}
}