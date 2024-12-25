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

package at.pcgamingfreaks.Util;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Version;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest
{
	private static final String longText = "This is a long text, without any meaningful content.", shortText = "Just a text.", exactText = "This text should not change.";
	private static final int maxLength = exactText.length();
	private static final String longTextFinal = longText.substring(0, maxLength), shortTextFinal = shortText, exactTextFinal = exactText;
	private static final String t1 = "Test String 1", t2 = "test 2", t3 = "The tree is old.";
	private static final String[] testArray = new String[] { t1, t2, t3 };
	private static final String ENABLED_MESSAGE = ConsoleColor.GREEN + " TestPlugin v1.2 has been enabled! " + ConsoleColor.YELLOW + " :) " + ConsoleColor.RESET;
	private static final String DISABLED_MESSAGE = ConsoleColor.RED + " TestPlugin v1.2 has been disabled. " + ConsoleColor.YELLOW + " :( " + ConsoleColor.RESET;
	private static final String TEST_STRING = "This is a test string!";

	@BeforeClass
	public static void prepareTestData()
	{
		new StringUtils();
	}

	@Test
	public void testGetPluginEnabledMessage()
	{
		assertEquals("The messages should match", ENABLED_MESSAGE, StringUtils.getPluginEnabledMessage("TestPlugin v1.2"));
		assertEquals("The messages should match", ENABLED_MESSAGE, StringUtils.getPluginEnabledMessage("TestPlugin", new Version("v1.2")));
	}

	@Test
	public void testGetPluginDisabledMessage()
	{
		assertEquals("The messages should match", DISABLED_MESSAGE, StringUtils.getPluginDisabledMessage("TestPlugin v1.2"));
		assertEquals("The messages should match", DISABLED_MESSAGE, StringUtils.getPluginDisabledMessage("TestPlugin", new Version("v1.2")));
	}
}