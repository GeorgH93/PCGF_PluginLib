/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.TestClasses.NMS.PlayerConnection;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UtilsTest
{
	private static boolean skipTests = !TestUtils.canMockJdkClasses();
	private static TestBukkitServer server = skipTests ? null : new TestBukkitServer();
	private static final World WORLD_1 = skipTests ? null : Mockito.mock(World.class);
	private static final World WORLD_2 = skipTests ? null : Mockito.mock(World.class);
	private static final Player PLAYER1 = skipTests ? null : Mockito.mock(Player.class);
	private static final Player PLAYER2 = skipTests ? null : Mockito.mock(Player.class);
	private static final Player PLAYER3 = skipTests ? null : Mockito.mock(Player.class);
	private static final Player PLAYER4 = skipTests ? null : Mockito.mock(Player.class);

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		if (skipTests) return;
		server.allowPluginManager = true;
		Bukkit.setServer(server);
		TestObjects.initNMSReflection();
		TestUtils.initReflection();
		doReturn("Lobby").when(WORLD_1).getName();
		doReturn("Survival").when(WORLD_2).getName();
		doReturn(false).when(PLAYER1).hasPermission("bypass.rangelimit");
		doReturn(false).when(PLAYER2).hasPermission("bypass.rangelimit");
		doReturn(false).when(PLAYER3).hasPermission("bypass.rangelimit");
		doReturn(true).when(PLAYER4).hasPermission("bypass.rangelimit");
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
	public void testWarnIfPerWorldPluginInstalled()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		Logger mockedLogger = spy(server.getLogger());
		Utils.warnIfPerWorldPluginsIsInstalled(mockedLogger);
		verify(mockedLogger, times(0)).warning(anyString());
		server.perWorldPlugins = true;
		Utils.warnIfPerWorldPluginsIsInstalled(mockedLogger, 0);
		verify(mockedLogger, times(8)).warning(anyString());
	}

	@Test
	public void testGetDistance()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		Player mockedPlayer1 = mock(Player.class);
		Player mockedPlayer2 = mock(Player.class);
		World mockedWorld1 = mock(World.class);
		doReturn("World1").when(mockedWorld1).getName();
		World mockedWorld2 = mock(World.class);
		doReturn("World2").when(mockedWorld2).getName();
		Location mockedLocation = mock(Location.class);
		assertEquals("The distance should be 0 if the players are the same", 0.0, Utils.getDistance(mockedPlayer1, mockedPlayer1), 0.1);
		doReturn(mockedWorld1).when(mockedPlayer1).getWorld();
		doReturn(mockedWorld2).when(mockedPlayer2).getWorld();
		assertEquals("The distance should be positive infinity if the players are in different worlds", Double.POSITIVE_INFINITY, Utils.getDistance(mockedPlayer1, mockedPlayer2), 0.1);
		doReturn(mockedWorld1).when(mockedPlayer2).getWorld();
		doReturn(mockedLocation).when(mockedPlayer1).getLocation();
		doReturn(mockedLocation).when(mockedPlayer2).getLocation();
		doReturn(5.0).when(mockedLocation).distance(mockedLocation);
		assertEquals("The distance should be correct if the players are in the same world", 5.0, Utils.getDistance(mockedPlayer1, mockedPlayer2), 0.1);
	}

	@Test
	public void testGetDistanceSquared()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		Player mockedPlayer1 = mock(Player.class);
		Player mockedPlayer2 = mock(Player.class);
		World mockedWorld1 = mock(World.class);
		doReturn("World1").when(mockedWorld1).getName();
		World mockedWorld2 = mock(World.class);
		doReturn("World2").when(mockedWorld2).getName();
		Location mockedLocation = mock(Location.class);
		assertEquals("The distance should be 0 if the players are the same", 0.0, Utils.getDistanceSquared(mockedPlayer1, mockedPlayer1), 0.1);
		doReturn(mockedWorld1).when(mockedPlayer1).getWorld();
		doReturn(mockedWorld2).when(mockedPlayer2).getWorld();
		assertEquals("The distance should be positive infinity if the players are in different worlds", Double.POSITIVE_INFINITY, Utils.getDistanceSquared(mockedPlayer1, mockedPlayer2), 0.1);
		doReturn(mockedWorld1).when(mockedPlayer2).getWorld();
		doReturn(mockedLocation).when(mockedPlayer1).getLocation();
		doReturn(mockedLocation).when(mockedPlayer2).getLocation();
		doReturn(25.0).when(mockedLocation).distanceSquared(mockedLocation);
		assertEquals("The distance should be correct if the players are in the same world", 25.0, Utils.getDistanceSquared(mockedPlayer1, mockedPlayer2), 0.1);
	}

	@Test
	public void testInRange()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
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
		assertTrue(Utils.inRange(PLAYER4, PLAYER3, 0, "bypass.rangelimit"));
	}

	@Test
	public void testInRangeSquared()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		assertTrue(Utils.inRangeSquared(PLAYER1, PLAYER2, -1.0));
		assertTrue(Utils.inRangeSquared(PLAYER1, PLAYER4, -1.0));
		assertTrue(Utils.inRangeSquared(PLAYER1, PLAYER2, 0));
		assertTrue(Utils.inRangeSquared(PLAYER1, PLAYER2, 300*300));
		assertFalse(Utils.inRangeSquared(PLAYER1, PLAYER4, 1.0));
		assertFalse(Utils.inRangeSquared(PLAYER1, PLAYER3, 1.0));
		assertFalse(Utils.inRangeSquared(PLAYER1, PLAYER2, 1.0));
		assertFalse(Utils.inRangeSquared(PLAYER1, PLAYER3, 0));
		assertTrue(Utils.inRangeSquared(PLAYER1, PLAYER2, -1.0, "bypass.rangelimit"));
		assertTrue(Utils.inRangeSquared(PLAYER1, PLAYER4, -1.0, "bypass.rangelimit"));
		assertTrue(Utils.inRangeSquared(PLAYER1, PLAYER4, 1.0, "bypass.rangelimit"));
		assertTrue(Utils.inRangeSquared(PLAYER1, PLAYER2, 0, "bypass.rangelimit"));
		assertTrue(Utils.inRangeSquared(PLAYER1, PLAYER2, 300*300, "bypass.rangelimit"));
		assertFalse(Utils.inRangeSquared(PLAYER1, PLAYER3, 1.0, "bypass.rangelimit"));
		assertFalse(Utils.inRangeSquared(PLAYER1, PLAYER2, 1.0, "bypass.rangelimit"));
		assertFalse(Utils.inRangeSquared(PLAYER1, PLAYER3, 0, "bypass.rangelimit"));
		assertTrue(Utils.inRangeSquared(PLAYER4, PLAYER3, 0, "bypass.rangelimit"));
	}
}