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

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class UtilsTest
{
	private static final byte[] byteArray1 = new byte[] { 0x01, 0x02, 0x03 }, byteArray2 = new byte[] { (byte) 0xFD, (byte) 0xDA, 0x11 };
	@SuppressWarnings("SpellCheckingInspection")
	private static final String final1 = "010203", final2 = "fdda11";

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
	public void testBlockThread() throws InterruptedException
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
		Thread.currentThread().interrupt();
		Utils.blockThread(10);
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