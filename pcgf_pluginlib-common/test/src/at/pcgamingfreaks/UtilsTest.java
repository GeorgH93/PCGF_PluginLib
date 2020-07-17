/*
 *   Copyright (C) 2020 GeorgH93
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
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
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
	public void testArrayContains()
	{
		Integer[] data = new Integer[] { 1, 3, 8 };
		assertTrue(Utils.arrayContains(data, 3));
		assertFalse(Utils.arrayContains(data, 2));
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test
	public void testExtract() throws Exception
	{
		final int[] logCount = new int[] { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer<Void>()
		{
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				logCount[0]++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
		File mockedFile = mock(File.class);
		doReturn(true).when(mockedFile).exists();
		doReturn(false).when(mockedFile).delete();
		doReturn(false).when(mockedFile).createNewFile();
		File parentFile = mock(File.class);
		doReturn(parentFile).when(mockedFile).getParentFile();
		doReturn(false).when(parentFile).exists();
		doReturn(false).when(parentFile).mkdirs();
		FileOutputStream mockedFileStream = mock(FileOutputStream.class);
		whenNew(FileOutputStream.class).withAnyArguments().thenReturn(mockedFileStream);
		Utils.extractFile(UtilsTest.class, mockedLogger, "", mockedFile);
		assertEquals("There should be all info messages in the log", 4, logCount[0]);
	}

	@Test
	public void testTryParse()
	{
		assertEquals(1, Utils.tryParse("1", 2));
		assertEquals(2, Utils.tryParse("1a", 2));
		assertEquals((byte) 1, Utils.tryParse("1", (byte) 2));
		assertEquals((byte) 2, Utils.tryParse("1a", (byte) 2));
		assertEquals((short) 1, Utils.tryParse("1", (short) 2));
		assertEquals((short) 2, Utils.tryParse("1a", (short) 2));
		assertEquals(1, Utils.tryParse("1", (long) 2));
		assertEquals(2, Utils.tryParse("1a", (long) 2));
		assertEquals(1.0f, Utils.tryParse("1.0", 2.0f), 0);
		assertEquals(2.0f, Utils.tryParse("1.0a", 2.0f), 0);
		assertEquals(1.0, Utils.tryParse("1.0", 2.0), 0);
		assertEquals(2.0, Utils.tryParse("1.0a", 2.0), 0);
	}
}