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

package at.pcgamingfreaks.TestClasses;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.protocol.packet.Chat;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

public class TestObjects
{
	private static Plugin mockedPlugin;
	private static ProxiedPlayer mockedPlayer;
	private static List<ProxiedPlayer> players;

	public static void initMockedPlugin()
	{
		File mockedFile = mock(File.class);
		when(mockedFile.getParentFile()).thenReturn(new File(""));
		when(mockedFile.getName()).thenReturn("FileName");
		TaskScheduler mockedTaskScheduler = mock(TaskScheduler.class);
		when(mockedTaskScheduler.runAsync(any(Plugin.class), any(Runnable.class))).thenAnswer(invocationOnMock -> {
			((Runnable) invocationOnMock.getArguments()[1]).run();
			return null;
		});
		ProxyServer mockedProxyServer = mock(ProxyServer.class);
		when(mockedProxyServer.getPluginsFolder()).thenReturn(new File(""));
		when(mockedProxyServer.getScheduler()).thenReturn(mockedTaskScheduler);
		PluginDescription mockedPluginDescription = mock(PluginDescription.class);
		when(mockedPluginDescription.getName()).thenReturn("TestPlugin");
		when(mockedPluginDescription.getVersion()).thenReturn("1.0");
		when(mockedPluginDescription.getFile()).thenReturn(mockedFile);
		mockedPlugin = mock(Plugin.class);
		when(mockedPlugin.getProxy()).thenReturn(mockedProxyServer);
		when(mockedPlugin.getDescription()).thenReturn(mockedPluginDescription);
		when(mockedPlugin.getLogger()).thenReturn(Logger.getLogger("TestLogger"));
	}

	public static void initMockedPlayer()
	{
		Connection.Unsafe mockedUnsafe = mock(Connection.Unsafe.class);
		doNothing().when(mockedUnsafe).sendPacket(any(Chat.class));
		mockedPlayer = mock(ProxiedPlayer.class);
		when(mockedPlayer.unsafe()).thenReturn(mockedUnsafe);
	}

	public static void initPlayers()
	{
		players = new ArrayList<>();
		players.add(mockedPlayer);
		players.add(mockedPlayer);
		players.add(mockedPlayer);
	}

	public static void initProxyServer() throws NoSuchFieldException, IllegalAccessException
	{
		initPlayers();
		ProxyServer mockedProxyServer = mock(ProxyServer.class);
		when(mockedProxyServer.getPlayers()).thenReturn(getPlayers());
		Field instance = ProxyServer.class.getDeclaredField("instance");
		instance.setAccessible(true);
		instance.set(mockedProxyServer, mockedProxyServer);
		instance.setAccessible(false);
	}

	public static Plugin getPlugin()
	{
		return mockedPlugin;
	}

	public static ProxiedPlayer getPlayer() { return mockedPlayer; }

	public static List<ProxiedPlayer> getPlayers() { return players; }
}