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

import org.jetbrains.annotations.NotNull;

/**
 * This class allows to change the color of console outputs.
 * It's recommended that you reset the color at the end of your output, to make sure the next message has the default color again.
 */
public enum ConsoleColor
{
	/**
	 * Resets all the formats of the console.
	 */
	RESET  (0),
	/**
	 * Changes the font of the console to bold.
	 */
	BOLD   (1),
	/**
	 * Changes the font color of the console to black.
	 */
	BLACK  (30),
	/**
	 * Changes the font color of the console to red.
	 */
	RED    (31),
	/**
	 * Changes the font color of the console to green.
	 */
	GREEN  (32),
	/**
	 * Changes the font color of the console to yellow.
	 */
	YELLOW (33),
	/**
	 * Changes the font color of the console to blue.
	 */
	BLUE   (34),
	/**
	 * Changes the font color of the console to purple.
	 */
	PURPLE (35),
	/**
	 * Changes the font color of the console to cyan.
	 */
	CYAN   (36),
	/**
	 * Changes the font color of the console to white.
	 */
	WHITE  (37),
	/**
	 * Resets the font color of the console.
	 */
	DEFAULT(39),
	/**
	 * Changes the background color of the console to black.
	 */
	BACKGROUND_BLACK  (40),
	/**
	 * Changes the background color of the console to red.
	 */
	BACKGROUND_RED    (41),
	/**
	 * Changes the background color of the console to green.
	 */
	BACKGROUND_GREEN  (42),
	/**
	 * Changes the background color of the console to yellow.
	 */
	BACKGROUND_YELLOW (43),
	/**
	 * Changes the background color of the console to blue.
	 */
	BACKGROUND_BLUE   (44),
	/**
	 * Changes the background color of the console to purple.
	 */
	BACKGROUND_PURPLE (45),
	/**
	 * Changes the background color of the console to cyan.
	 */
	BACKGROUND_CYAN   (46),
	/**
	 * Changes the background color of the console to white.
	 */
	BACKGROUND_WHITE  (47),
	/**
	 * Resets the background color of the console.
	 */
	BACKGROUND_DEFAULT(49);
	
	private final String code;
	public static final char ESC_SEQUENCE = '\u001B';

	ConsoleColor(int code)
	{
		this.code = String.valueOf(ESC_SEQUENCE) + '[' + code + 'm';
	}

	@Override
	public @NotNull String toString()
	{
		return this.code;
	}
}