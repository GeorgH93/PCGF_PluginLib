/*
 *   Copyright (C) 2018 GeorgH93
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

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import com.google.common.base.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BaseOnlineProvider.class, SpigotUpdateProvider.class, URL.class })
public class SpigotUpdateProviderTest
{
	private static final int PLUGIN_ID_EXT = 19286, PLUGIN_ID_HOSTED = 15584;

	private SpigotUpdateProvider getProvider()
	{
		//noinspection ConstantConditions
		return new SpigotUpdateProvider(PLUGIN_ID_EXT, null);
	}

	@Test
	public void testQueryExt() throws Exception
	{
		final int[] loggerCalls = new int[] { 0, 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				loggerCalls[0]++;
				return null;
			}
		}).when(mockedLogger).warning(anyString());
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				loggerCalls[1]++;
				return null;
			}
		}).when(mockedLogger).severe(anyString());
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				loggerCalls[1]++;
				return null;
			}
		}).when(mockedLogger).log(any(Level.class), String.valueOf(any(Supplier.class)), any(Throwable.class));
		SpigotUpdateProvider sup = new SpigotUpdateProvider(PLUGIN_ID_EXT, mockedLogger);
		assertEquals(UpdateResult.SUCCESS, sup.query());
		assertFalse(sup.providesDownloadURL());
		assertEquals(PLUGIN_ID_EXT + ".jar", sup.getLatestFileName());
		assertTrue(sup.getLatestName().contains("Minepacks"));
	}

	@Test
	public void testQueryHosted() throws Exception
	{
		Logger mockedLogger = mock(Logger.class);
		SpigotUpdateProvider sup = new SpigotUpdateProvider(PLUGIN_ID_HOSTED, mockedLogger);
		assertEquals(UpdateResult.SUCCESS, sup.query());
		assertTrue(sup.providesDownloadURL());
		assertEquals("http://api.spiget.org/v2/resources/" + PLUGIN_ID_HOSTED + "/download", sup.getLatestFileURL().getProtocol() + "://" + sup.getLatestFileURL().getHost() + sup.getLatestFileURL().getPath());
		assertEquals(PLUGIN_ID_HOSTED + ".jar", sup.getLatestFileName());
		assertEquals("MobMerge", sup.getLatestName());
	}

	@Test
	public void testQueryFail() throws Exception
	{
		SpigotUpdateProvider updateProvider = spy(getProvider());
		URL mockedURL = PowerMockito.mock(URL.class);
		final HttpURLConnection mockedHttpURLConnection = spy(new HttpURLConnection(mockedURL)
		{
			@Override
			public void connect() throws IOException { }

			@Override
			public void disconnect() { }

			@Override
			public boolean usingProxy() { return false; }
		});
		PowerMockito.doReturn(mockedHttpURLConnection).when(mockedURL).openConnection();
		doReturn(HttpURLConnection.HTTP_MOVED_TEMP).when(mockedHttpURLConnection).getResponseCode();
		whenNew(URL.class).withAnyArguments().thenReturn(mockedURL);
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
		doReturn(HttpURLConnection.HTTP_SEE_OTHER).when(mockedHttpURLConnection).getResponseCode();
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
		PowerMockito.doThrow(new IOException()).when(mockedURL).openConnection();
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
		final int[] connectionCount = { 0 };
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				if (connectionCount[0]++ == 0)
				{
					return mockedHttpURLConnection;
				}
				return null;
			}
		}).when(updateProvider).connect(any(URL.class));
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test
	public void testLastResult() throws NoSuchFieldException, IllegalAccessException, RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		TestUtils.initReflection();
		String minecraftVersion = "Minecraft-1.12.2";
		Version version = new Version("v1.12.2");
		SpigotUpdateProvider updateProvider = getProvider();
		UpdateProvider.UpdateFile updateFile = mock(UpdateProvider.UpdateFile.class);
		doReturn(version).when(updateFile).getVersion();
		doReturn(minecraftVersion).when(updateFile).getGameVersion();
		Field lastResultField = TestUtils.setAccessible(SpigotUpdateProvider.class, updateProvider, "lastResult", updateFile);
		assertEquals("The version should match", version, updateProvider.getLatestVersion());
		assertEquals("The Minecraft version should match", minecraftVersion, updateProvider.getLatestMinecraftVersion());
		TestUtils.setUnaccessible(lastResultField, updateProvider, false);
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestFileName() throws Exception
	{
		getProvider().getLatestFileName();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestName() throws Exception
	{
		getProvider().getLatestName();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestVersion() throws Exception
	{
		getProvider().getLatestVersion();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestFileURL() throws Exception
	{
		getProvider().getLatestFileURL();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestMinecraftVersion() throws Exception
	{
		getProvider().getLatestMinecraftVersion();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestChecksum() throws Exception
	{
		getProvider().getLatestChecksum();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestChangelog() throws Exception
	{
		getProvider().getLatestChangelog();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestDependencies() throws Exception
	{
		getProvider().getLatestDependencies();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetUpdateHistory() throws Exception
	{
		getProvider().getUpdateHistory();
	}

	//region test provider properties
	@Test
	public void testProvidesDownloadURL() throws Exception
	{
		SpigotUpdateProvider provider = getProvider();
		assertFalse(provider.providesDownloadURL());
		Objects.requireNonNull(Reflection.getField(SpigotUpdateProvider.class, "downloadable")).set(provider, true);
		assertTrue(provider.providesDownloadURL());
	}

	@Test
	public void testProvidesMinecraftVersion()
	{
		assertTrue(getProvider().providesMinecraftVersion());
	}

	@Test
	public void testProvidesChangelog()
	{
		assertFalse(getProvider().providesChangelog());
	}

	@Test
	public void testProvidesChecksum()
	{
		assertEquals(ChecksumType.NONE, getProvider().providesChecksum());
	}

	@Test
	public void testProvidesUpdateHistory()
	{
		assertFalse(getProvider().providesUpdateHistory());
	}

	@Test
	public void testProvidesDependencies()
	{
		assertFalse(getProvider().providesDependencies());
	}
	//endregion
}