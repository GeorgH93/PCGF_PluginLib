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

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class UtilsTest
{
	private final static byte[] byteArray1 = new byte[] { 0x01, 0x02, 0x03 }, byteArray2 = new byte[] { (byte) 0xFD, (byte) 0xDA, 0x11 };
	@SuppressWarnings("SpellCheckingInspection")
	private final static String final1 = "010203", final2 = "fdda11";
	private final static String longText = "This is a long text, without any meaningful content.", shortText = "Just a text.", exactText = "This text should not change.";
	private final static int maxLength = exactText.length();
	private final static String longTextFinal = longText.substring(0, maxLength - 1), shortTextFinal = shortText, exactTextFinal = exactText;

	@BeforeClass
	public static void prepareTestData()
	{
		new Utils();
	}

	@Test
	public void testByteArrayToHex()
	{
		assertEquals("The hex string should match the given byte array", final1, Utils.byteArrayToHex(byteArray1));
		assertEquals("The hex string should match the given byte array", final2, Utils.byteArrayToHex(byteArray2));
		assertEquals("The hex string should match the given byte array", "", Utils.byteArrayToHex(null));
		assertEquals("The hex string should match the given byte array", "", Utils.byteArrayToHex(new byte[0]));
	}

	@Test
	public void testBlockThread()
	{
		long startTime = System.currentTimeMillis();
		Utils.blockThread(0);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		assertTrue("The execution should need minimal time", elapsedTime < 10);
		startTime = System.currentTimeMillis();
		Utils.blockThread(1);
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		assertTrue("The execution should take the given amount of seconds", elapsedTime > 900 && elapsedTime < 1100);
	}

	@Test
	public void testLimitStringLength()
	{
		assertEquals("The limited string should be correct", longTextFinal, Utils.limitLength(longText, maxLength));
		assertEquals("The limited string should be correct", shortTextFinal, Utils.limitLength(shortText, maxLength));
		assertEquals("The limited string should be correct", exactTextFinal, Utils.limitLength(exactText, maxLength));
		try
		{
			Utils.limitLength(longText, -1);
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("A negative limit length should throw an error", e.getMessage(), "The max length must not be negative!");
		}
		try
		{
			//noinspection ConstantConditions
			Utils.limitLength(null, 10);
		}
		catch(IllegalArgumentException | NullPointerException e)
		{
			//noinspection SpellCheckingInspection
			assertTrue("A null string should throw an error", e.getMessage().equals("Argument for @NotNull parameter 'text' of at/pcgamingfreaks/Utils.limitLength must not be null") || e.getMessage().equals("The text must not be null."));
		}
		assertEquals("A limit of 0 characters should lead to an empty string", "", Utils.limitLength(longText, 0));
		assertEquals("An empty string should lead to an empty output string", "", Utils.limitLength("", 10));
	}

	@Test
	public void testWarnOnJava7()
	{
		final int[] logCount = new int[] { 0, 0 };
		Logger mockLogger = mock(Logger.class);
		doAnswer(new Answer<Void>()
		{
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				logCount[0]++;
				return null;
			}
		}).when(mockLogger).warning(anyString());
		doAnswer(new Answer<Void>()
		{
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				logCount[1]++;
				return null;
			}
		}).when(mockLogger).info(anyString());
		String javaVersion = System.getProperty("java.version");
		System.setProperty("java.version", "1.7");
		long startTime = System.currentTimeMillis();
		Utils.warnOnJava_1_7(mockLogger, 1);
		long stopTime = System.currentTimeMillis(), elapsedTime = stopTime - startTime;
		assertTrue("The elapsed time should match the given second", elapsedTime > 900 && elapsedTime < 1100);
		System.setProperty("java.version", "1.8");
		Utils.warnOnJava_1_7(mockLogger);
		System.setProperty("java.version", javaVersion);
		assertEquals("There should be one message in the warning log", 1, logCount[0]);
		assertEquals("There should be one message in the info log", 1, logCount[1]);
	}
}