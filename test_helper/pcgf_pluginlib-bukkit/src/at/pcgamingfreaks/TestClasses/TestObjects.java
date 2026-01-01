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

package at.pcgamingfreaks.TestClasses;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.OBCReflection;
import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.Version;

import com.google.common.io.Files;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestObjects
{
	static class MockIPlugin implements IPlugin {
		@Override
		public @NotNull Logger getLogger()
		{
			return mockedJavaPlugin.getLogger();
		}

		@Override
		public @NotNull File getDataFolder()
		{
			return mockedJavaPlugin.getDataFolder();
		}

		@Override
		public @NotNull Version getVersion()
		{
			return new Version(0);
		}

		@Override
		public @NotNull String getName()
		{
			return "";
		}
	}

	private static JavaPlugin mockedJavaPlugin;
	private static org.bukkit.plugin.Plugin mockedBukkitPlugin;

	private static final Unsafe UNSAFE;

	static
	{
		try
		{
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			UNSAFE = (Unsafe) f.get(null);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to get Unsafe instance", e);
		}
	}

	private static void setStaticField(Class<?> clazz, String fieldName, Object value) throws NoSuchFieldException
	{
		Field field = clazz.getDeclaredField(fieldName);
		Object base = UNSAFE.staticFieldBase(field);
		long offset = UNSAFE.staticFieldOffset(field);
		UNSAFE.putObject(base, offset, value);
	}

	public static void initMockedJavaPlugin()
	{
		BukkitScheduler mockedScheduler = mock(BukkitScheduler.class);
		when(mockedScheduler.runTask(any(org.bukkit.plugin.Plugin.class), any(Runnable.class))).thenAnswer(invocationOnMock -> {
			((Runnable) invocationOnMock.getArguments()[1]).run();
			return null;
		});
		Server mockedServer = mock(Server.class);
		when(mockedServer.getScheduler()).thenReturn(mockedScheduler);
		mockedJavaPlugin = mock(JavaPlugin.class);
		when(mockedJavaPlugin.getLogger()).thenReturn(Logger.getLogger("TestLogger"));
		File pluginDir = Files.createTempDir();
		pluginDir.deleteOnExit();
		when(mockedJavaPlugin.getDataFolder()).thenReturn(pluginDir);
		when(mockedJavaPlugin.getServer()).thenReturn(mockedServer);
	}

	public static void initMockedBukkitPlugin()
	{
		PluginDescriptionFile mockedPluginDescription = mock(PluginDescriptionFile.class);
		when(mockedPluginDescription.getFullName()).thenReturn("TestPlugin");
		mockedBukkitPlugin = mock(org.bukkit.plugin.Plugin.class);
		when(mockedBukkitPlugin.getLogger()).thenReturn(Logger.getLogger("BukkitTestLogger"));
		when(mockedBukkitPlugin.getDescription()).thenReturn(mockedPluginDescription);
		when(mockedBukkitPlugin.getName()).thenReturn("TestPlugin");
	}

	public static void initNMSReflection() throws NoSuchFieldException
	{
		setBukkitVersion("1_8_R1");
		setStaticField(NMSReflection.class, "NMS_CLASS_PATH", "at.pcgamingfreaks.TestClasses.NMS.");
		setStaticField(OBCReflection.class, "OBC_CLASS_PATH", "at.pcgamingfreaks.TestClasses.OBC.");
	}

	public static void setBukkitVersion(String version) throws NoSuchFieldException
	{
		setStaticField(OBCReflection.class, "BUKKIT_VERSION", version);
		setStaticField(MCVersion.class, "CURRENT_VERSION", MCVersion.getFromServerVersion(version));
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
			throw new UnsupportedOperationException("initBukkitOnlinePlayers requires TestBukkitServer. Set Bukkit.setServer(new TestBukkitServer()) first.");
		}
	}

	public static JavaPlugin getJavaPlugin() { return mockedJavaPlugin; }

	public static org.bukkit.plugin.Plugin getBukkitPlugin() { return mockedBukkitPlugin; }

	public static IPlugin getIPlugin() { return new MockIPlugin(); }
}