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

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

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
	public void testLimitLength()
	{
		assertEquals("The limited string should be correct", longTextFinal, StringUtils.limitLength(longText, maxLength));
		assertEquals("The limited string should be correct", shortTextFinal, StringUtils.limitLength(shortText, maxLength));
		assertEquals("The limited string should be correct", exactTextFinal, StringUtils.limitLength(exactText, maxLength));
		assertEquals("A limit of 0 characters should lead to an empty string", "", StringUtils.limitLength(longText, 0));
		assertEquals("An empty string should lead to an empty output string", "", StringUtils.limitLength("", 10));
		assertEquals("This is a test", StringUtils.limitLength(TEST_STRING, 14));
		assertEquals("", StringUtils.limitLength("", 100));
		assertEquals("", StringUtils.limitLength(TEST_STRING, 0));
		assertEquals(TEST_STRING, StringUtils.limitLength(TEST_STRING, TEST_STRING.length()));
	}

	@Test
	public void testArrayContains()
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
	public void testArrayContainsIgnoreCase()
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
	public void testArrayContainsAny()
	{
		String[] array = new String[] { "Junk", "tree", "Hello" };
		assertTrue("The array should contain one of the strings", StringUtils.arrayContainsAny(array, "junk", "tree"));
		assertFalse("The array should not contain one of the strings", StringUtils.arrayContainsAny(array, "junk", "Tree"));
		assertFalse("The array should not contain one of the strings", StringUtils.arrayContainsAny(array));
		assertFalse("The array should not contain one of the strings", StringUtils.arrayContainsAny(array, (String[]) null));
	}

	@Test
	public void testArrayContainsAnyIgnoreCase()
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
	public void testGetAllContaining()
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
	public void testGetAllContainingIgnoreCase()
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

	@Test
	public void testParsePageNumber()
	{
		assertEquals(0, StringUtils.parsePageNumber("0"));
		assertEquals(0, StringUtils.parsePageNumber("1--"));
		assertEquals(0, StringUtils.parsePageNumber("1-1"));
		assertEquals(0, StringUtils.parsePageNumber("2-2"));
		assertEquals(0, StringUtils.parsePageNumber("1"));
		assertEquals(0, StringUtils.parsePageNumber("0++"));
		assertEquals(0, StringUtils.parsePageNumber("0+1"));
		assertEquals(0, StringUtils.parsePageNumber("3-2"));
		assertEquals(0, StringUtils.parsePageNumber("2--"));
		assertEquals(1, StringUtils.parsePageNumber("2"));
		assertEquals(1, StringUtils.parsePageNumber("1++"));
		assertEquals(1, StringUtils.parsePageNumber("0+2"));
		assertEquals(1, StringUtils.parsePageNumber("4-2"));
		assertEquals(1, StringUtils.parsePageNumber("3--"));
	}

	@Test
	public void testFormatByteCountHumanReadable()
	{
		assertEquals("1 byte", StringUtils.formatByteCountHumanReadable(1));
		assertEquals("10 bytes", StringUtils.formatByteCountHumanReadable(10));
		assertEquals("1.00 kiB", StringUtils.formatByteCountHumanReadable(1024));
		assertEquals("1.00 MiB", StringUtils.formatByteCountHumanReadable(1024*1024));
		assertEquals("1.00 GiB", StringUtils.formatByteCountHumanReadable(1024*1024*1024));
		assertEquals("2.00 GiB", StringUtils.formatByteCountHumanReadable(Integer.MAX_VALUE));
		assertEquals("1.00 TiB", StringUtils.formatByteCountHumanReadable(1024L*1024*1024*1024));
		assertEquals("1.00 PiB", StringUtils.formatByteCountHumanReadable(1024L*1024*1024*1024*1024));
		assertEquals("1.00 EiB", StringUtils.formatByteCountHumanReadable(1024L*1024*1024*1024*1024*1024));
		assertEquals("100.0 MiB", StringUtils.formatByteCountHumanReadable(1024*1024*100));
		assertEquals("8.00 EiB", StringUtils.formatByteCountHumanReadable(Long.MAX_VALUE));
		assertEquals("8,00 EiB", StringUtils.formatByteCountHumanReadable(Locale.GERMAN, Long.MAX_VALUE));
	}

	@Test
	public void testContainsAny()
	{
		assertTrue(StringUtils.containsAny(TEST_STRING, "test", "house"));
		assertTrue(StringUtils.containsAny(TEST_STRING, "house", "test"));
		assertFalse(StringUtils.containsAny(TEST_STRING, "house", "TEST"));
	}

	@Test
	public void testArrayToString()
	{
		assertEquals("test 1", StringUtils.arrayToString(new Object[] { "test", 1 }));
		assertEquals("", StringUtils.arrayToString(new Object[0]));
	}

	@Test
	public void testIndexOf()
	{
		String[] testData = new String[] { "test1", "Test2", "test3 test3"};
		assertEquals(-1, StringUtils.indexOf(testData, "test99"));
		assertEquals(0, StringUtils.indexOf(testData, "test1"));
		assertEquals(-1, StringUtils.indexOf(testData, "test2"));
		assertEquals(1, StringUtils.indexOf(testData, "Test2"));
		assertEquals(-1, StringUtils.indexOf(testData, "test3"));
		assertEquals(2, StringUtils.indexOf(testData, "test3 test3"));
	}

	@Test
	public void testIndexOfIgnoreCase()
	{
		String[] testData = new String[] { "test1", "Test2", "test3 test3"};
		assertEquals(-1, StringUtils.indexOfIgnoreCase(testData, "test99"));
		assertEquals(0, StringUtils.indexOfIgnoreCase(testData, "test1"));
		assertEquals(1, StringUtils.indexOfIgnoreCase(testData, "test2"));
		assertEquals(1, StringUtils.indexOfIgnoreCase(testData, "Test2"));
		assertEquals(-1, StringUtils.indexOfIgnoreCase(testData, "test3"));
		assertEquals(2, StringUtils.indexOfIgnoreCase(testData, "test3 test3"));
	}

	@Test
	public void testIndexOfAllMatching()
	{
		String[] testData = new String[] { "test1", "Test2", "test3 test3"};
		assertEquals(new Integer[]{0}, StringUtils.indexOfAllMatching(testData, "test1").toArray());
		assertEquals(new Integer[]{0}, StringUtils.indexOfAllMatching(testData, "test1?").toArray());
		assertEquals(new Integer[]{0, 1}, StringUtils.indexOfAllMatching(testData, "[tT]est[\\d]").toArray());
	}
}