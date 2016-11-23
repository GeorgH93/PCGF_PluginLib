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

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NMSReflection;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.protocol.packet.Chat;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

public class TestObjects
{
	private static JavaPlugin mockedJavaPlugin;
	@SuppressWarnings("SpellCheckingInspection")
	private static org.bukkit.plugin.Plugin mockedBukkitPlugin;
	private static Plugin mockedPlugin;
	private static ProxiedPlayer mockedPlayer;

	private static List<ProxiedPlayer> players;

	public static void initMockedJavaPlugin() throws Exception
	{
		BukkitScheduler mockedScheduler = mock(BukkitScheduler.class);
		when(mockedScheduler.runTask(any(org.bukkit.plugin.Plugin.class), any(Runnable.class))).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				((Runnable) invocationOnMock.getArguments()[1]).run();
				return null;
			}
		});
		Server mockedServer = mock(Server.class);
		when(mockedServer.getScheduler()).thenReturn(mockedScheduler);
		mockedJavaPlugin = PowerMockito.mock(JavaPlugin.class);
		when(mockedJavaPlugin.getLogger()).thenReturn(Logger.getLogger("TestLogger"));
		when(mockedJavaPlugin.getDataFolder()).thenReturn(new File(""));
		when(mockedJavaPlugin.getServer()).thenReturn(mockedServer);
	}

	@SuppressWarnings("SpellCheckingInspection")
	public static void initMockedBukkitPlugin()
	{
		PluginDescriptionFile mockedPluginDescription = PowerMockito.mock(PluginDescriptionFile.class);
		PowerMockito.when(mockedPluginDescription.getFullName()).thenReturn("TestPlugin");
		mockedBukkitPlugin = mock(org.bukkit.plugin.Plugin.class);
		when(mockedBukkitPlugin.getLogger()).thenReturn(Logger.getLogger("BukkitTestLogger"));
		when(mockedBukkitPlugin.getDescription()).thenReturn(mockedPluginDescription);
	}

	public static void initMockedPlugin()
	{
		File mockedFile = mock(File.class);
		when(mockedFile.getParentFile()).thenReturn(new File(""));
		when(mockedFile.getName()).thenReturn("FileName");
		TaskScheduler mockedTaskScheduler = mock(TaskScheduler.class);
		when(mockedTaskScheduler.runAsync(any(Plugin.class), any(Runnable.class))).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				((Runnable) invocationOnMock.getArguments()[1]).run();
				return null;
			}
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

	@SuppressWarnings("SpellCheckingInspection")
	public static void initNMSReflection() throws NoSuchFieldException, IllegalAccessException
	{
		setBukkitVersion("1_8_R1");
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		Field nmsClassPath = NMSReflection.class.getDeclaredField("NMS_CLASS_PATH");
		nmsClassPath.setAccessible(true);
		modifiers.set(nmsClassPath, nmsClassPath.getModifiers() & ~Modifier.FINAL);
		nmsClassPath.set(null, "at.pcgamingfreaks.TestClasses.NMS.");
		modifiers.set(nmsClassPath, nmsClassPath.getModifiers() | Modifier.FINAL);
		nmsClassPath.setAccessible(false);
		Field obcClassPath = NMSReflection.class.getDeclaredField("OBC_CLASS_PATH");
		obcClassPath.setAccessible(true);
		modifiers.set(obcClassPath, obcClassPath.getModifiers() & ~Modifier.FINAL);
		obcClassPath.set(null, "at.pcgamingfreaks.TestClasses.OBC.");
		modifiers.set(obcClassPath, obcClassPath.getModifiers() | Modifier.FINAL);
		obcClassPath.setAccessible(false);
		modifiers.setAccessible(false);
	}

	@SuppressWarnings("SpellCheckingInspection")
	public static void setBukkitVersion(String version) throws NoSuchFieldException, IllegalAccessException
	{
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		Field bukkitVersion = NMSReflection.class.getDeclaredField("BUKKIT_VERSION");
		bukkitVersion.setAccessible(true);
		modifiers.set(bukkitVersion, bukkitVersion.getModifiers() & ~Modifier.FINAL);
		bukkitVersion.set(null, version);
		modifiers.set(bukkitVersion, bukkitVersion.getModifiers() | Modifier.FINAL);
		bukkitVersion.setAccessible(false);
		Field currentVersion = MCVersion.class.getDeclaredField("CURRENT_VERSION");
		currentVersion.setAccessible(true);
		modifiers.set(currentVersion, currentVersion.getModifiers() & ~Modifier.FINAL);
		currentVersion.set(null, MCVersion.getFromServerVersion(version));
		modifiers.set(currentVersion, currentVersion.getModifiers() | Modifier.FINAL);
		currentVersion.setAccessible(false);
		modifiers.setAccessible(false);
	}

	public static void initPlayers()
	{
		players = new ArrayList<>();
		players.add(mockedPlayer);
		players.add(mockedPlayer);
		players.add(mockedPlayer);
	}

	@SuppressWarnings("SpellCheckingInspection")
	public static void initBukkitOnlinePlayers() throws Exception
	{
		List<Player> bukkitPlayers = new ArrayList<>();
		bukkitPlayers.add(new TestBukkitPlayer());
		bukkitPlayers.add(new TestBukkitPlayer());
		mockStatic(Bukkit.class);
		doReturn(bukkitPlayers).when(Bukkit.class, "getOnlinePlayers");
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

	public static JavaPlugin getJavaPlugin() { return mockedJavaPlugin; }

	@SuppressWarnings("SpellCheckingInspection")
	public static org.bukkit.plugin.Plugin getBukkitPlugin() { return mockedBukkitPlugin; }

	public static Plugin getPlugin()
	{
		return mockedPlugin;
	}

	public static ProxiedPlayer getPlayer() { return mockedPlayer; }

	public static List<ProxiedPlayer> getPlayers() { return players; }
}