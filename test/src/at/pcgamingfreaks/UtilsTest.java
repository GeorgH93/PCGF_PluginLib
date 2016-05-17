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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class UtilsTest
{
	@SuppressWarnings("unused")
	private Utils JUST_TO_SILENCE_COVERAGE_REPORT = new Utils();
	private final static byte[] byteArray1 = new byte[] { 0x01, 0x02, 0x03 }, byteArray2 = new byte[] { (byte) 0xfd, (byte) 0xda, 0x11 };
	@SuppressWarnings("SpellCheckingInspection")
	private final static String final1 = "010203", final2 = "fdda11";

	@Test
	public void testByteArrayToHex()
	{
		assertEquals(final1, Utils.byteArrayToHex(byteArray1));
		assertEquals(final2, Utils.byteArrayToHex(byteArray2));
		assertEquals("", Utils.byteArrayToHex(null));
		assertEquals("", Utils.byteArrayToHex(new byte[0]));
	}

	private final static String longText = "This is a long text, without any meaningful content.", shortText = "Just a text.", exactText = "This text should not change.";
	private final static int maxLength = exactText.length();
	private final static String longTextFinal = longText.substring(0, maxLength - 1), shortTextFinal = shortText, exactTextFinal = exactText;

	@Test
	public void testLimitStringLength()
	{
		assertEquals(longTextFinal, Utils.limitLength(longText, maxLength));
		assertEquals(shortTextFinal, Utils.limitLength(shortText, maxLength));
		assertEquals(exactTextFinal, Utils.limitLength(exactText, maxLength));
		try
		{
			Utils.limitLength(longText, -1);
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(), "The max length must not be negative!");
		}
		try
		{
			//noinspection ConstantConditions
			Utils.limitLength(null, 10);
		}
		catch(IllegalArgumentException | NullPointerException e)
		{
			assertTrue(e.getMessage().equals("Argument for @NotNull parameter 'text' of at/pcgamingfreaks/Utils.limitLength must not be null") // JUnit is throwing this text cause of the @NotNull Annotation
					           || e.getMessage().equals("The text must not be null.")); // The message we are expecting
		}
		assertEquals("", Utils.limitLength(longText, 0));
		assertEquals("", Utils.limitLength("", 10));
	}

	@Test
	public void testBlockThread()
	{
		//region no blocking
		long startTime = System.currentTimeMillis();
		Utils.blockThread(0);
		long stopTime = System.currentTimeMillis(), elapsedTime = stopTime - startTime;
		assertTrue(elapsedTime < 10);
		//endregion
		//region with blocking
		startTime = System.currentTimeMillis();
		Utils.blockThread(1);
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		assertTrue(elapsedTime > 900 && elapsedTime < 1100); // Give it some tolerance
		//endregion
	}

	@Test
	public void testWarnOnJava7()
	{
		final int[] logCount = new int[]{0,0};
		Logger mockLogger = mock(Logger.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				logCount[0]++;
				return null;
			}
		}).when(mockLogger).warning(ConsoleColor.RED + "You are still using Java 1.7. Java 1.7 ist EOL for over a year now! You should really update to Java 1.8!" + ConsoleColor.RESET);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				logCount[1]++;
				return null;
			}
		}).when(mockLogger).info(ConsoleColor.YELLOW + "For now this plugin will still work fine with Java 1.7 but no warranty that this won't change in the future." + ConsoleColor.RESET);
		final String javaVersion = System.getProperty("java.version"); // Backup the java version property so that it can be restored after the tests
		System.setProperty("java.version", "1.7");
		long startTime = System.currentTimeMillis();
		Utils.warnOnJava_1_7(mockLogger, 1);
		long stopTime = System.currentTimeMillis(), elapsedTime = stopTime - startTime;
		assertTrue(elapsedTime > 900 && elapsedTime < 1100); // Give it some tolerance
		System.setProperty("java.version", "1.8");
		Utils.warnOnJava_1_7(mockLogger);
		System.setProperty("java.version", javaVersion); // Restore the java version property
		assertEquals(1, logCount[0]);
		assertEquals(1, logCount[1]);
	}
}