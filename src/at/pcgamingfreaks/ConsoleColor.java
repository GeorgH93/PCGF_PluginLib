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

/**
 * This class allows to change the color of console outputs.
 * It's recommended that you reset the color at the end of your output.
 */
@SuppressWarnings("unused")
public enum ConsoleColor
{
	/**
	 * Resets all the formats of the console.
	 */
	RESET  ("\u001B[0m"),
	BOLD   ("\u001B[1m"),
	/**
	 * Changes the font color of the console to black.
	 */
	BLACK  ("\u001B[30m"),
	/**
	 * Changes the font color of the console to red.
	 */
	RED    ("\u001B[31m"),
	/**
	 * Changes the font color of the console to green.
	 */
	GREEN  ("\u001B[32m"),
	/**
	 * Changes the font color of the console to yellow.
	 */
	YELLOW ("\u001B[33m"),
	/**
	 * Changes the font color of the console to blue.
	 */
	BLUE   ("\u001B[34m"),
	/**
	 * Changes the font color of the console to purple.
	 */
	PURPLE ("\u001B[35m"),
	/**
	 * Changes the font color of the console to cyan.
	 */
	CYAN   ("\u001B[36m"),
	/**
	 * Changes the font color of the console to white.
	 */
	WHITE  ("\u001B[37m"),
	/**
	 * Resets the font color of the console.
	 */
	DEFAULT("\u001B[39m"),
	/**
	 * Changes the background color of the console to black.
	 */
	BACKGROUND_BLACK  ("\u001B[40m"),
	/**
	 * Changes the background color of the console to red.
	 */
	BACKGROUND_RED    ("\u001B[41m"),
	/**
	 * Changes the background color of the console to green.
	 */
	BACKGROUND_GREEN  ("\u001B[42m"),
	/**
	 * Changes the background color of the console to yellow.
	 */
	BACKGROUND_YELLOW ("\u001B[43m"),
	/**
	 * Changes the background color of the console to blue.
	 */
	BACKGROUND_BLUE   ("\u001B[44m"),
	/**
	 * Changes the background color of the console to purple.
	 */
	BACKGROUND_PURPLE ("\u001B[45m"),
	/**
	 * Changes the background color of the console to cyan.
	 */
	BACKGROUND_CYAN   ("\u001B[46m"),
	/**
	 * Changes the background color of the console to white.
	 */
	BACKGROUND_WHITE  ("\u001B[47m"),
	/**
	 * Resets the background color of the console.
	 */
	BACKGROUND_DEFAULT("\u001B[49m");
	
	private final String code;

	ConsoleColor(String code)
	{
		this.code = code;
	}

	@Override
	public String toString()
	{
		return this.code;
	}
}