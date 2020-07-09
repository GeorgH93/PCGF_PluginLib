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

import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.command.*;
import org.bukkit.command.defaults.ReloadCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("SpellCheckingInspection")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginDescriptionFile.class })
public class RegisterablePluginCommandTest
{
	@Before
	public void prepareTestObjects()
	{
		TestObjects.initMockedBukkitPlugin();
	}

	@Test
	public void testRegisterablePluginCommand() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		Plugin plugin = TestObjects.getBukkitPlugin();
		RegisterablePluginCommand pluginCommand = new RegisterablePluginCommand(plugin, "TestPlugin", "TestPlugin", "Test", "Plugin");
		assertNotNull("The registerable plugin command should not be null", pluginCommand);
		assertEquals("The plugin should be set correctly", plugin, pluginCommand.getPlugin());
		assertEquals("The plugin command string should match", "at.pcgamingfreaks.Bukkit.RegisterablePluginCommand(TestPlugin, TestPlugin)", pluginCommand.toString());
		pluginCommand.registerCommand();
		pluginCommand.unregisterCommand();
		TestBukkitServer server = new TestBukkitServer();
		TestBukkitServer.commandMap = new SimpleCommandMap(server);
		when(plugin.getServer()).thenReturn(server);
		pluginCommand.registerCommand();
		CommandSender mockedCommandSender = mock(CommandSender.class);
		doNothing().when(mockedCommandSender).sendMessage(anyString());
		String[] args = new String[] { "arg1", "arg2", "arg3" };
		assertFalse("The command should not be executed when the plugin is disabled", pluginCommand.execute(mockedCommandSender, "TestPlugin", args));
		doReturn(true).when(plugin).isEnabled();
		doThrow(new RuntimeException()).when(plugin).onCommand(any(CommandSender.class), any(Command.class), anyString(), any(String[].class));
		assertFalse("The command should not be executed when an error occurs", pluginCommand.execute(mockedCommandSender, "TestPlugin", args));
		Field usageMessage = Command.class.getDeclaredField("usageMessage");
		usageMessage.setAccessible(true);
		usageMessage.set(pluginCommand, "");
		usageMessage.setAccessible(false);
		assertFalse("The command should not be executed when an error occurs", pluginCommand.execute(mockedCommandSender, "TestPlugin", args));
		doReturn(true).when(plugin).onCommand(any(CommandSender.class), any(Command.class), anyString(), any(String[].class));
		assertTrue("The command should be executed", pluginCommand.execute(mockedCommandSender, "TestPlugin", args));
		CommandExecutor mockedCommandExecutor = mock(CommandExecutor.class);
		pluginCommand.setExecutor(mockedCommandExecutor);
		assertEquals("The command executor should be set", mockedCommandExecutor, pluginCommand.getExecutor());
		pluginCommand.setExecutor(null);
		assertEquals("The command executor should be set to the plugin", plugin, pluginCommand.getExecutor());
		assertNull("The tab completer should be set to null", pluginCommand.getTabCompleter());
		assertEquals("The tab completion list should be empty", 0, pluginCommand.tabComplete(mockedCommandSender, "Test", args).size());
		TabCompleter mockedTabCompleter = mock(TabCompleter.class);
		pluginCommand.setTabCompleter(mockedTabCompleter);
		assertEquals("The tab completer should now be set", mockedTabCompleter, pluginCommand.getTabCompleter());
		doThrow(new RuntimeException()).when(mockedTabCompleter).onTabComplete(any(CommandSender.class), any(Command.class), anyString(), any(String[].class));
		boolean exceptionThrown = false;
		try
		{
			pluginCommand.tabComplete(mockedCommandSender, "Test", args);
		}
		catch(Exception ignored)
		{
			exceptionThrown = true;
		}
		assertTrue("An exception should be thrown with an invalid tab completion", exceptionThrown);
		doReturn(null).when(mockedTabCompleter).onTabComplete(any(CommandSender.class), any(Command.class), anyString(), any(String[].class));
		assertEquals("The tab completion list should be empty", 0, pluginCommand.tabComplete(mockedCommandSender, "Test", args).size());
		doReturn(server).when(mockedCommandSender).getServer();
		doReturn(null).when(plugin).onTabComplete(any(CommandSender.class), any(Command.class), anyString(), any(String[].class));
		assertEquals("The tab completion list should be empty", 0, pluginCommand.tabComplete(mockedCommandSender, "Test", args).size());
		pluginCommand.setExecutor(mockedCommandExecutor);
		assertEquals("The tab completion list should be empty", 0, pluginCommand.tabComplete(mockedCommandSender, "Test", args).size());
		pluginCommand.setExecutor(null);
		List<String> tabCompletionReturn = new ArrayList<>();
		tabCompletionReturn.add("TestTabComplete");
		doReturn(tabCompletionReturn).when(mockedTabCompleter).onTabComplete(any(CommandSender.class), any(Command.class), anyString(), any(String[].class));
		List<String> tabCompletions = pluginCommand.tabComplete(mockedCommandSender, "Test", args);
		assertEquals("The tab completion list should be empty", tabCompletionReturn.size(), tabCompletions.size());
		assertEquals("The tab completion should match", tabCompletionReturn.get(0), tabCompletions.get(0));
		server.allowPluginManager = true;
		Method setCommandMap = server.getPluginManager().getClass().getDeclaredMethod("setCommandMap", CommandMap.class);
		setCommandMap.setAccessible(true);
		TestBukkitServer.commandMap.register("TestPlugin", new ReloadCommand("TestAlias"));
		setCommandMap.invoke(server.getPluginManager(), TestBukkitServer.commandMap);
		setCommandMap.setAccessible(false);
		Field aliases = Command.class.getDeclaredField("aliases");
		aliases.setAccessible(true);
		//noinspection unchecked
		List<String> aliasList = (List<String>) aliases.get(pluginCommand);
		aliasList.add("testalias");
		aliasList.add("TestPlugin_abcde");
		aliasList.add("fghij");
		aliases.set(pluginCommand, aliasList);
		Field activeAliases = Command.class.getDeclaredField("activeAliases");
		activeAliases.setAccessible(true);
		activeAliases.set(pluginCommand, aliasList);
		pluginCommand.unregisterCommand();
		aliases.setAccessible(false);
		activeAliases.setAccessible(false);
		assertEquals("The plugin commands should be equal", new RegisterablePluginCommand(plugin, "TestPlugin", null).toString(), new RegisterablePluginCommand(plugin, "TestPlugin", null, null).toString());
	}
}