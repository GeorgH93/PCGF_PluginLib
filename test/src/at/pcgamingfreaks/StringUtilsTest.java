/*
 *   Copyright (C) 2016-2018 GeorgH93
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

import java.util.List;

import static org.junit.Assert.*;

public class StringUtilsTest
{
	private static final String longText = "This is a long text, without any meaningful content.", shortText = "Just a text.", exactText = "This text should not change.";
	private static final int maxLength = exactText.length();
	private static final String longTextFinal = longText.substring(0, maxLength - 1), shortTextFinal = shortText, exactTextFinal = exactText;
	private static final String t1 = "Test String 1", t2 = "test 2", t3 = "The tree is old.";
	private static final String[] testArray = new String[] { t1, t2, t3 };
	private static final String ENABLED_MESSAGE = ConsoleColor.GREEN + " TestPlugin v1.2 has been enabled! " + ConsoleColor.YELLOW + " :) " + ConsoleColor.RESET;
	private static final String DISABLED_MESSAGE = ConsoleColor.RED + " TestPlugin v1.2 has been disabled. " + ConsoleColor.YELLOW + " :( " + ConsoleColor.RESET;

	@BeforeClass
	public static void prepareTestData()
	{
		new StringUtils();
	}

	@Test
	public void testLimitStringLength()
	{
		assertEquals("The limited string should be correct", longTextFinal, StringUtils.limitLength(longText, maxLength));
		assertEquals("The limited string should be correct", shortTextFinal, StringUtils.limitLength(shortText, maxLength));
		assertEquals("The limited string should be correct", exactTextFinal, StringUtils.limitLength(exactText, maxLength));
		try
		{
			StringUtils.limitLength(longText, -1);
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("A negative limit length should throw an error", e.getMessage(), "The max length must not be negative!");
		}
		try
		{
			//noinspection ConstantConditions
			StringUtils.limitLength(null, 10);
		}
		catch(IllegalArgumentException | NullPointerException e)
		{
			//noinspection SpellCheckingInspection
			assertTrue("A null string should throw an error", e.getMessage().equals("Argument for @NotNull parameter 'text' of at/pcgamingfreaks/Utils.limitLength must not be null") || e.getMessage().equals("The text must not be null."));
		}
		assertEquals("A limit of 0 characters should lead to an empty string", "", StringUtils.limitLength(longText, 0));
		assertEquals("An empty string should lead to an empty output string", "", StringUtils.limitLength("", 10));
	}

	@Test
	public void testStringArrayContains()
	{
		String[] array = new String[] { "Junk", "tree", "Hello" };
		assertTrue("The array should contain the string", StringUtils.arrayContains(array, "tree"));
		assertTrue("The array should contain the string", StringUtils.arrayContains(array, "Hello"));
		assertTrue("The array should contain the string", StringUtils.arrayContains(array, "Junk"));
		assertFalse("The array should not contain the string", StringUtils.arrayContains(array, "junk"));
		assertFalse("The array should not contain the string", StringUtils.arrayContains(array, "Tree"));
		assertFalse("The array should not contain the string", StringUtils.arrayContains(array, "hello"));
		assertFalse("The array should not contain the string", StringUtils.arrayContains(array, "just a string"));
		assertFalse("The array should not contain the string", StringUtils.arrayContains(array, ""));
		assertFalse("The array should not contain the string", StringUtils.arrayContains(array, null));
	}

	@Test
	public void testStringArrayContainsIgnoreCase()
	{
		String[] array = new String[] { "Junk", "tree", "Hello" };
		assertTrue("The array should contain the string", StringUtils.arrayContainsIgnoreCase(array, "tree"));
		assertTrue("The array should contain the string", StringUtils.arrayContainsIgnoreCase(array, "Tree"));
		assertTrue("The array should contain the string", StringUtils.arrayContainsIgnoreCase(array, "Junk"));
		assertTrue("The array should contain the string", StringUtils.arrayContainsIgnoreCase(array, "juNk"));
		assertTrue("The array should contain the string", StringUtils.arrayContainsIgnoreCase(array, "junk"));
		assertTrue("The array should contain the string", StringUtils.arrayContainsIgnoreCase(array, "Hello"));
		assertTrue("The array should contain the string", StringUtils.arrayContainsIgnoreCase(array, "hello"));
		assertFalse("The array should not contain the string", StringUtils.arrayContainsIgnoreCase(array, "just a string"));
		assertFalse("The array should not contain the string", StringUtils.arrayContainsIgnoreCase(array, ""));
		assertFalse("The array should not contain the string", StringUtils.arrayContainsIgnoreCase(array, null));
	}

	@Test
	public void testStringArrayContainsAny()
	{
		String[] array = new String[] { "Junk", "tree", "Hello" };
		assertTrue("The array should contain one of the strings", StringUtils.arrayContainsAny(array, "junk", "tree"));
		assertFalse("The array should not contain one of the strings", StringUtils.arrayContainsAny(array, "junk", "Tree"));
		assertFalse("The array should not contain one of the strings", StringUtils.arrayContainsAny(array));
		assertFalse("The array should not contain one of the strings", StringUtils.arrayContainsAny(array, (String[]) null));
	}

	@Test
	public void testStringArrayContainsAnyIgnoreCase()
	{
		String[] array = new String[] { "Junk", "tree", "Hello" };
		assertTrue("The array should contain one of the strings", StringUtils.arrayContainsAnyIgnoreCase(array, "junk", "tree"));
		assertTrue("The array should not contain one of the strings", StringUtils.arrayContainsAnyIgnoreCase(array, "junk", "Tree"));
		assertTrue("The array should not contain one of the strings", StringUtils.arrayContainsAnyIgnoreCase(array, "just a string", "hello"));
		assertFalse("The array should not contain one of the strings", StringUtils.arrayContainsAnyIgnoreCase(array, "just a string", "an other string"));
		assertFalse("The array should not contain one of the strings", StringUtils.arrayContainsAnyIgnoreCase(array));
		assertFalse("The array should not contain one of the strings", StringUtils.arrayContainsAnyIgnoreCase(array, (String[]) null));
	}

	@Test
	public void testEscapeJsonString()
	{
		assertEquals("The string should be escaped correctly", "\\\\Hello \\\"World!\\\"\\\\", StringUtils.escapeJsonString("\\Hello \"World!\"\\"));
	}

	@Test
	public void testStringContainsIgnoreCase()
	{
		String testString = "This is a test string";
		assertTrue("The searched element should be contained", StringUtils.containsIgnoreCase(testString, "This"));
		assertTrue("The searched element should be contained", StringUtils.containsIgnoreCase(testString, "this"));
		assertTrue("The searched element should be contained", StringUtils.containsIgnoreCase(testString, "String"));
		assertTrue("The searched element should be contained", StringUtils.containsIgnoreCase(testString, "string"));
		assertFalse("The searched element should not be contained", StringUtils.containsIgnoreCase(testString, "tree"));
		assertFalse("The searched element should not be contained", StringUtils.containsIgnoreCase(testString, "Tree"));
	}

	@Test
	public void testGetAllContainingStrings()
	{
		List<String> result = StringUtils.getAllContaining(testArray, "String");
		assertEquals("The lists element count should math", 1, result.size());
		assertTrue("The list should contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = StringUtils.getAllContaining(testArray, "string");
		assertEquals("The lists element count should math", 0, result.size());
		assertFalse("The list should not contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = StringUtils.getAllContaining(testArray, "test");
		assertEquals("The lists element count should math", 1, result.size());
		assertFalse("The list should not contain the element", result.contains(t1));
		assertTrue("The list should contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
	}

	@Test
	public void testGetAllContainingStringsIgnoreCase()
	{
		List<String> result = StringUtils.getAllContainingIgnoreCase(testArray, "String");
		assertEquals("The lists element count should math", 1, result.size());
		assertTrue("The list should contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = StringUtils.getAllContainingIgnoreCase(testArray, "string");
		assertEquals("The lists element count should math", 1, result.size());
		assertTrue("The list should contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = StringUtils.getAllContainingIgnoreCase(testArray, "test");
		assertEquals("The lists element count should math", 2, result.size());
		assertTrue("The list should contain the element", result.contains(t1));
		assertTrue("The list should contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = StringUtils.getAllContainingIgnoreCase(testArray, "Train");
		assertEquals("The lists element count should math", 0, result.size());
		assertFalse("The list should not contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
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

	@Test
	public void testContainsIgnoreCase()
	{
		String testText = "some text to check";
		assertTrue(StringUtils.containsIgnoreCase(testText, "an", "text"));
		assertFalse(StringUtils.containsIgnoreCase(testText, "an", "tree"));
	}
}