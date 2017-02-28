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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UtilsTest
{
	private static final byte[] byteArray1 = new byte[] { 0x01, 0x02, 0x03 }, byteArray2 = new byte[] { (byte) 0xFD, (byte) 0xDA, 0x11 };
	@SuppressWarnings("SpellCheckingInspection")
	private static final String final1 = "010203", final2 = "fdda11";
	private static final World WORLD_1 = Mockito.mock(World.class), WORLD_2 = Mockito.mock(World.class);
	private static final Player PLAYER1 = Mockito.mock(Player.class), PLAYER2 = Mockito.mock(Player.class), PLAYER3 = Mockito.mock(Player.class), PLAYER4 = Mockito.mock(Player.class);

	@BeforeClass
	public static void prepareTestData()
	{
		new Utils();
		doReturn("Lobby").when(WORLD_1).getName();
		doReturn("Survival").when(WORLD_2).getName();
		doReturn(false).when(PLAYER1).hasPermission("bypass.rangelimit");
		doReturn(false).when(PLAYER2).hasPermission("bypass.rangelimit");
		doReturn(false).when(PLAYER3).hasPermission("bypass.rangelimit");
		doReturn(true) .when(PLAYER4).hasPermission("bypass.rangelimit");
		doReturn(WORLD_1).when(PLAYER1).getWorld();
		doReturn(WORLD_1).when(PLAYER2).getWorld();
		doReturn(WORLD_2).when(PLAYER3).getWorld();
		doReturn(WORLD_2).when(PLAYER4).getWorld();
		Location locationPlayer1 = new Location(WORLD_1, 100, 200, 300);
		Location locationPlayer2 = new Location(WORLD_1, 300, 200, 300);
		Location locationPlayer3 = new Location(WORLD_2, 300, 200, 300);
		doReturn(locationPlayer1).when(PLAYER1).getLocation();
		doReturn(locationPlayer2).when(PLAYER2).getLocation();
		doReturn(locationPlayer3).when(PLAYER3).getLocation();
		doReturn(locationPlayer3).when(PLAYER4).getLocation();
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

	@Test
	public void testGetDistance()
	{
		assertEquals(200, Utils.getDistance(PLAYER1, PLAYER2), 0.1);
		assertEquals(Double.POSITIVE_INFINITY, Utils.getDistance(PLAYER3, PLAYER2), 0);
		assertEquals(0, Utils.getDistance(PLAYER1, PLAYER1), 0);
	}

	@Test
	public void testInRange()
	{
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, -1.0));
		assertTrue(Utils.inRange(PLAYER1, PLAYER4, -1.0));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, 0));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, 300));
		assertFalse(Utils.inRange(PLAYER1, PLAYER4, 1.0));
		assertFalse(Utils.inRange(PLAYER1, PLAYER3, 1.0));
		assertFalse(Utils.inRange(PLAYER1, PLAYER2, 1.0));
		assertFalse(Utils.inRange(PLAYER1, PLAYER3, 0));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, -1.0, "bypass.rangelimit"));
		assertTrue(Utils.inRange(PLAYER1, PLAYER4, -1.0, "bypass.rangelimit"));
		assertTrue(Utils.inRange(PLAYER1, PLAYER4, 1.0, "bypass.rangelimit"));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, 0, "bypass.rangelimit"));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, 300, "bypass.rangelimit"));
		assertFalse(Utils.inRange(PLAYER1, PLAYER3, 1.0, "bypass.rangelimit"));
		assertFalse(Utils.inRange(PLAYER1, PLAYER2, 1.0, "bypass.rangelimit"));
		assertFalse(Utils.inRange(PLAYER1, PLAYER3, 0, "bypass.rangelimit"));
	}
}