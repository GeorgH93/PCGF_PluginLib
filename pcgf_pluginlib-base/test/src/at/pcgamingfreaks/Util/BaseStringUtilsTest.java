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

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class BaseStringUtilsTest
{
	private static final String longText = "This is a long text, without any meaningful content.", shortText = "Just a text.", exactText = "This text should not change.";
	private static final int maxLength = exactText.length();
	private static final String longTextFinal = longText.substring(0, maxLength), shortTextFinal = shortText, exactTextFinal = exactText;
	private static final String t1 = "Test String 1", t2 = "test 2", t3 = "The tree is old.";
	private static final String[] testArray = new String[] { t1, t2, t3 };
	private static final String TEST_STRING = "This is a test string!";

	@BeforeClass
	public static void prepareTestData()
	{
		new BaseStringUtils();
	}

	@Test
	public void testLimitLength()
	{
		assertEquals("The limited string should be correct", longTextFinal, BaseStringUtils.limitLength(longText, maxLength));
		assertEquals("The limited string should be correct", shortTextFinal, BaseStringUtils.limitLength(shortText, maxLength));
		assertEquals("The limited string should be correct", exactTextFinal, BaseStringUtils.limitLength(exactText, maxLength));
		assertEquals("A limit of 0 characters should lead to an empty string", "", BaseStringUtils.limitLength(longText, 0));
		assertEquals("An empty string should lead to an empty output string", "", BaseStringUtils.limitLength("", 10));
		assertEquals("This is a test", BaseStringUtils.limitLength(TEST_STRING, 14));
		assertEquals("", BaseStringUtils.limitLength("", 100));
		assertEquals("", BaseStringUtils.limitLength(TEST_STRING, 0));
		assertEquals(TEST_STRING, BaseStringUtils.limitLength(TEST_STRING, TEST_STRING.length()));
	}

	@Test
	public void testArrayContains()
	{
		String[] array = new String[] { "Junk", "tree", "Hello" };
		assertTrue("The array should contain the string", BaseStringUtils.arrayContains(array, "tree"));
		assertTrue("The array should contain the string", BaseStringUtils.arrayContains(array, "Hello"));
		assertTrue("The array should contain the string", BaseStringUtils.arrayContains(array, "Junk"));
		assertFalse("The array should not contain the string", BaseStringUtils.arrayContains(array, "junk"));
		assertFalse("The array should not contain the string", BaseStringUtils.arrayContains(array, "Tree"));
		assertFalse("The array should not contain the string", BaseStringUtils.arrayContains(array, "hello"));
		assertFalse("The array should not contain the string", BaseStringUtils.arrayContains(array, "just a string"));
		assertFalse("The array should not contain the string", BaseStringUtils.arrayContains(array, ""));
		assertFalse("The array should not contain the string", BaseStringUtils.arrayContains(array, null));
	}

	@Test
	public void testArrayContainsIgnoreCase()
	{
		String[] array = new String[] { "Junk", "tree", "Hello" };
		assertTrue("The array should contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, "tree"));
		assertTrue("The array should contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, "Tree"));
		assertTrue("The array should contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, "Junk"));
		assertTrue("The array should contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, "juNk"));
		assertTrue("The array should contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, "junk"));
		assertTrue("The array should contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, "Hello"));
		assertTrue("The array should contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, "hello"));
		assertFalse("The array should not contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, "just a string"));
		assertFalse("The array should not contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, ""));
		assertFalse("The array should not contain the string", BaseStringUtils.arrayContainsIgnoreCase(array, null));
	}

	@Test
	public void testArrayContainsAny()
	{
		String[] array = new String[] { "Junk", "tree", "Hello" };
		assertTrue("The array should contain one of the strings", BaseStringUtils.arrayContainsAny(array, "junk", "tree"));
		assertFalse("The array should not contain one of the strings", BaseStringUtils.arrayContainsAny(array, "junk", "Tree"));
		assertFalse("The array should not contain one of the strings", BaseStringUtils.arrayContainsAny(array));
		assertFalse("The array should not contain one of the strings", BaseStringUtils.arrayContainsAny(array, (String[]) null));
	}

	@Test
	public void testArrayContainsAnyIgnoreCase()
	{
		String[] array = new String[] { "Junk", "tree", "Hello" };
		assertTrue("The array should contain one of the strings", BaseStringUtils.arrayContainsAnyIgnoreCase(array, "junk", "tree"));
		assertTrue("The array should not contain one of the strings", BaseStringUtils.arrayContainsAnyIgnoreCase(array, "junk", "Tree"));
		assertTrue("The array should not contain one of the strings", BaseStringUtils.arrayContainsAnyIgnoreCase(array, "just a string", "hello"));
		assertFalse("The array should not contain one of the strings", BaseStringUtils.arrayContainsAnyIgnoreCase(array, "just a string", "an other string"));
		assertFalse("The array should not contain one of the strings", BaseStringUtils.arrayContainsAnyIgnoreCase(array));
		assertFalse("The array should not contain one of the strings", BaseStringUtils.arrayContainsAnyIgnoreCase(array, (String[]) null));
	}

	@Test
	public void testEscapeJsonString()
	{
		assertEquals("The string should be escaped correctly", "\\\\Hello \\\"World!\\\"\\\\", BaseStringUtils.escapeJsonString("\\Hello \"World!\"\\"));
	}

	@Test
	public void testStringContainsIgnoreCase()
	{
		String testString = "This is a test string";
		assertTrue("The searched element should be contained", BaseStringUtils.containsIgnoreCase(testString, "This"));
		assertTrue("The searched element should be contained", BaseStringUtils.containsIgnoreCase(testString, "this"));
		assertTrue("The searched element should be contained", BaseStringUtils.containsIgnoreCase(testString, "String"));
		assertTrue("The searched element should be contained", BaseStringUtils.containsIgnoreCase(testString, "string"));
		assertFalse("The searched element should not be contained", BaseStringUtils.containsIgnoreCase(testString, "tree"));
		assertFalse("The searched element should not be contained", BaseStringUtils.containsIgnoreCase(testString, "Tree"));
	}

	@Test
	public void testGetAllContaining()
	{
		List<String> result = BaseStringUtils.getAllContaining(testArray, "String");
		assertEquals("The lists element count should math", 1, result.size());
		assertTrue("The list should contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = BaseStringUtils.getAllContaining(testArray, "string");
		assertEquals("The lists element count should math", 0, result.size());
		assertFalse("The list should not contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = BaseStringUtils.getAllContaining(testArray, "test");
		assertEquals("The lists element count should math", 1, result.size());
		assertFalse("The list should not contain the element", result.contains(t1));
		assertTrue("The list should contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
	}

	@Test
	public void testGetAllContainingIgnoreCase()
	{
		List<String> result = BaseStringUtils.getAllContainingIgnoreCase(testArray, "String");
		assertEquals("The lists element count should math", 1, result.size());
		assertTrue("The list should contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = BaseStringUtils.getAllContainingIgnoreCase(testArray, "string");
		assertEquals("The lists element count should math", 1, result.size());
		assertTrue("The list should contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = BaseStringUtils.getAllContainingIgnoreCase(testArray, "test");
		assertEquals("The lists element count should math", 2, result.size());
		assertTrue("The list should contain the element", result.contains(t1));
		assertTrue("The list should contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
		result = BaseStringUtils.getAllContainingIgnoreCase(testArray, "Train");
		assertEquals("The lists element count should math", 0, result.size());
		assertFalse("The list should not contain the element", result.contains(t1));
		assertFalse("The list should not contain the element", result.contains(t2));
		assertFalse("The list should not contain the element", result.contains(t3));
	}

	@Test
	public void testContainsIgnoreCase()
	{
		String testText = "some text to check";
		assertTrue(BaseStringUtils.containsIgnoreCase(testText, "an", "text"));
		assertFalse(BaseStringUtils.containsIgnoreCase(testText, "an", "tree"));
	}

	@Test
	public void testParsePageNumber()
	{
		assertEquals(0, BaseStringUtils.parsePageNumber("0"));
		assertEquals(0, BaseStringUtils.parsePageNumber("1--"));
		assertEquals(0, BaseStringUtils.parsePageNumber("1-1"));
		assertEquals(0, BaseStringUtils.parsePageNumber("2-2"));
		assertEquals(0, BaseStringUtils.parsePageNumber("1"));
		assertEquals(0, BaseStringUtils.parsePageNumber("0++"));
		assertEquals(0, BaseStringUtils.parsePageNumber("0+1"));
		assertEquals(0, BaseStringUtils.parsePageNumber("3-2"));
		assertEquals(0, BaseStringUtils.parsePageNumber("2--"));
		assertEquals(1, BaseStringUtils.parsePageNumber("2"));
		assertEquals(1, BaseStringUtils.parsePageNumber("1++"));
		assertEquals(1, BaseStringUtils.parsePageNumber("0+2"));
		assertEquals(1, BaseStringUtils.parsePageNumber("4-2"));
		assertEquals(1, BaseStringUtils.parsePageNumber("3--"));
	}

	@Test
	public void testFormatByteCountHumanReadable()
	{
		assertEquals("1 byte", BaseStringUtils.formatByteCountHumanReadable(1));
		assertEquals("10 bytes", BaseStringUtils.formatByteCountHumanReadable(10));
		assertEquals("1.00 kiB", BaseStringUtils.formatByteCountHumanReadable(1024));
		assertEquals("1.00 MiB", BaseStringUtils.formatByteCountHumanReadable(1024*1024));
		assertEquals("1.00 GiB", BaseStringUtils.formatByteCountHumanReadable(1024*1024*1024));
		assertEquals("2.00 GiB", BaseStringUtils.formatByteCountHumanReadable(Integer.MAX_VALUE));
		assertEquals("1.00 TiB", BaseStringUtils.formatByteCountHumanReadable(1024L*1024*1024*1024));
		assertEquals("1.00 PiB", BaseStringUtils.formatByteCountHumanReadable(1024L*1024*1024*1024*1024));
		assertEquals("1.00 EiB", BaseStringUtils.formatByteCountHumanReadable(1024L*1024*1024*1024*1024*1024));
		assertEquals("100.0 MiB", BaseStringUtils.formatByteCountHumanReadable(1024*1024*100));
		assertEquals("8.00 EiB", BaseStringUtils.formatByteCountHumanReadable(Long.MAX_VALUE));
		assertEquals("8,00 EiB", BaseStringUtils.formatByteCountHumanReadable(Locale.GERMAN, Long.MAX_VALUE));
	}

	@Test
	public void testContainsAny()
	{
		assertTrue(BaseStringUtils.containsAny(TEST_STRING, "test", "house"));
		assertTrue(BaseStringUtils.containsAny(TEST_STRING, "house", "test"));
		assertFalse(BaseStringUtils.containsAny(TEST_STRING, "house", "TEST"));
	}

	@Test
	public void testArrayToString()
	{
		assertEquals("test 1", BaseStringUtils.arrayToString(new Object[] { "test", 1 }));
		assertEquals("", BaseStringUtils.arrayToString(new Object[0]));
	}

	@Test
	public void testIndexOf()
	{
		String[] testData = new String[] { "test1", "Test2", "test3 test3"};
		assertEquals(-1, BaseStringUtils.indexOf(testData, "test99"));
		assertEquals(0, BaseStringUtils.indexOf(testData, "test1"));
		assertEquals(-1, BaseStringUtils.indexOf(testData, "test2"));
		assertEquals(1, BaseStringUtils.indexOf(testData, "Test2"));
		assertEquals(-1, BaseStringUtils.indexOf(testData, "test3"));
		assertEquals(2, BaseStringUtils.indexOf(testData, "test3 test3"));
	}

	@Test
	public void testIndexOfIgnoreCase()
	{
		String[] testData = new String[] { "test1", "Test2", "test3 test3"};
		assertEquals(-1, BaseStringUtils.indexOfIgnoreCase(testData, "test99"));
		assertEquals(0, BaseStringUtils.indexOfIgnoreCase(testData, "test1"));
		assertEquals(1, BaseStringUtils.indexOfIgnoreCase(testData, "test2"));
		assertEquals(1, BaseStringUtils.indexOfIgnoreCase(testData, "Test2"));
		assertEquals(-1, BaseStringUtils.indexOfIgnoreCase(testData, "test3"));
		assertEquals(2, BaseStringUtils.indexOfIgnoreCase(testData, "test3 test3"));
	}

	@Test
	public void testIndexOfAllMatching()
	{
		String[] testData = new String[] { "test1", "Test2", "test3 test3"};
		assertEquals(new Integer[]{0}, BaseStringUtils.indexOfAllMatching(testData, "test1").toArray());
		assertEquals(new Integer[]{0}, BaseStringUtils.indexOfAllMatching(testData, "test1?").toArray());
		assertEquals(new Integer[]{0, 1}, BaseStringUtils.indexOfAllMatching(testData, "[tT]est[\\d]").toArray());
	}
}