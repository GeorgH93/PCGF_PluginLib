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
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.logging.Logger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

public class TestObjects
{
	private static Plugin mockedPlugin;

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
		when(mockedPluginDescription.getName()).thenReturn("");
		when(mockedPluginDescription.getVersion()).thenReturn("TestPlugin 1.0");
		when(mockedPluginDescription.getFile()).thenReturn(mockedFile);
		mockedPlugin = mock(Plugin.class);
		when(mockedPlugin.getProxy()).thenReturn(mockedProxyServer);
		when(mockedPlugin.getDescription()).thenReturn(mockedPluginDescription);
		when(mockedPlugin.getLogger()).thenReturn(Logger.getLogger("TestLogger"));
	}

	public static Plugin getPlugin()
	{
		return mockedPlugin;
	}
}