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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bukkit.class, NMSReflection.class, PluginDescriptionFile.class, Reflection.class })
public class UtilsTest
{
	@SuppressWarnings("SpellCheckingInspection")
	private static TestBukkitServer server = new TestBukkitServer();

	@BeforeClass
	public static void prepareTestData()
	{
		server.allowPluginManager = true;
		Bukkit.setServer(server);
	}

	@Before
	public void prepareTestObjects()
	{
		mockStatic(Reflection.class);
		given(Reflection.getMethod(any(Class.class), anyString(), any(Class.class))).willAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		});
		mockStatic(NMSReflection.class);
		given(NMSReflection.getOBCClass(anyString())).willAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		});
		given(NMSReflection.getNMSClass(anyString())).willAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		});
		given(NMSReflection.getMethod(any(Class.class), anyString(), any(Class.class))).willAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		});
		new Utils();
	}

	@Test
	public void testWarnIfPerWorldPluginInstalled()
	{
		Logger mockedLogger = spy(server.getLogger());
		Utils.warnIfPerWorldPluginsIsInstalled(mockedLogger);
		verify(mockedLogger, times(0)).warning(anyString());
		server.perWorldPlugins = true;
		Utils.warnIfPerWorldPluginsIsInstalled(mockedLogger, 0);
		verify(mockedLogger, times(9)).warning(anyString());
	}

	@Test
	public void testGetDistance()
	{
		Player mockedPlayer1 = mock(Player.class);
		Player mockedPlayer2 = mock(Player.class);
		World mockedWorld1 = mock(World.class);
		World mockedWorld2 = mock(World.class);
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
}