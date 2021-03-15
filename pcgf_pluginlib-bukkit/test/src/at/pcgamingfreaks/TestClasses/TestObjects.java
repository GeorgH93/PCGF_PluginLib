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
import at.pcgamingfreaks.Bukkit.OBCReflection;

import com.google.common.io.Files;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

public class TestObjects
{
	private static JavaPlugin mockedJavaPlugin;
	private static org.bukkit.plugin.Plugin mockedBukkitPlugin;

	public static void initMockedJavaPlugin()
	{
		BukkitScheduler mockedScheduler = mock(BukkitScheduler.class);
		when(mockedScheduler.runTask(any(org.bukkit.plugin.Plugin.class), any(Runnable.class))).thenAnswer(invocationOnMock -> {
			((Runnable) invocationOnMock.getArguments()[1]).run();
			return null;
		});
		Server mockedServer = mock(Server.class);
		when(mockedServer.getScheduler()).thenReturn(mockedScheduler);
		mockedJavaPlugin = PowerMockito.mock(JavaPlugin.class);
		when(mockedJavaPlugin.getLogger()).thenReturn(Logger.getLogger("TestLogger"));
		File pluginDir = Files.createTempDir();
		pluginDir.deleteOnExit();
		when(mockedJavaPlugin.getDataFolder()).thenReturn(pluginDir);
		when(mockedJavaPlugin.getServer()).thenReturn(mockedServer);
	}

	public static void initMockedBukkitPlugin()
	{
		PluginDescriptionFile mockedPluginDescription = PowerMockito.mock(PluginDescriptionFile.class);
		PowerMockito.when(mockedPluginDescription.getFullName()).thenReturn("TestPlugin");
		mockedBukkitPlugin = mock(org.bukkit.plugin.Plugin.class);
		when(mockedBukkitPlugin.getLogger()).thenReturn(Logger.getLogger("BukkitTestLogger"));
		when(mockedBukkitPlugin.getDescription()).thenReturn(mockedPluginDescription);
		when(mockedBukkitPlugin.getName()).thenReturn("TestPlugin");
	}

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
		Field obcClassPath = OBCReflection.class.getDeclaredField("OBC_CLASS_PATH");
		obcClassPath.setAccessible(true);
		modifiers.set(obcClassPath, obcClassPath.getModifiers() & ~Modifier.FINAL);
		obcClassPath.set(null, "at.pcgamingfreaks.TestClasses.OBC.");
		modifiers.set(obcClassPath, obcClassPath.getModifiers() | Modifier.FINAL);
		obcClassPath.setAccessible(false);
		modifiers.setAccessible(false);
	}

	public static void setBukkitVersion(String version) throws NoSuchFieldException, IllegalAccessException
	{
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		Field bukkitVersion = OBCReflection.class.getDeclaredField("BUKKIT_VERSION");
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

	public static void initBukkitOnlinePlayers() throws Exception
	{
		Server server = Bukkit.getServer();
		if(server instanceof TestBukkitServer)
		{
			((TestBukkitServer) server).getPlayers().clear();
			((TestBukkitServer) server).getPlayers().add(new TestBukkitPlayer());
			((TestBukkitServer) server).getPlayers().add(new TestBukkitPlayer());
		}
		else
		{
			List<Player> bukkitPlayers = new ArrayList<>();
			bukkitPlayers.add(new TestBukkitPlayer());
			bukkitPlayers.add(new TestBukkitPlayer());
			mockStatic(Bukkit.class);
			doReturn(bukkitPlayers).when(Bukkit.class, "getOnlinePlayers");
		}
	}

	public static JavaPlugin getJavaPlugin() { return mockedJavaPlugin; }

	public static org.bukkit.plugin.Plugin getBukkitPlugin() { return mockedBukkitPlugin; }
}