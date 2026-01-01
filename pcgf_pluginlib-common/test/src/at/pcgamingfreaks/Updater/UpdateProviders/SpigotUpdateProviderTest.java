/*
 *   Copyright (C) 2022 GeorgH93
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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.TestClasses.LogCapture;
import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedConstruction;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SpigotUpdateProviderTest
{
	private static final int PLUGIN_ID_EXT = 19286, PLUGIN_ID_HOSTED = 15584;
	private static final Logger LOGGER = Logger.getLogger(SpigotUpdateProviderTest.class.getName());
	
	private LogCapture logCapture;
	
	@Before
	public void setUp()
	{
		logCapture = new LogCapture();
		LogCapture.createTestLogger(SpigotUpdateProviderTest.class.getName(), logCapture);
	}
	
	@After
	public void tearDown()
	{
		LOGGER.removeHandler(logCapture);
	}

	private SpigotUpdateProvider getProvider()
	{
		return new SpigotUpdateProvider(PLUGIN_ID_EXT, LOGGER);
	}

	@Test
	public void testQueryExt() throws Exception
	{
		SpigotUpdateProvider sup = new SpigotUpdateProvider(PLUGIN_ID_EXT, LOGGER);
		assertEquals(UpdateResult.SUCCESS, sup.query());
		//assertFalse(sup.providesDownloadURL()); // Temporary disabled because the current minepacks version is uploaded to spigotmc.org
		assertEquals(PLUGIN_ID_EXT + ".jar", sup.getLatestFileName());
		assertTrue(sup.getLatestName().contains("Minepacks"));
	}

	@Test
	public void testQueryHosted() throws Exception
	{
		SpigotUpdateProvider sup = new SpigotUpdateProvider(PLUGIN_ID_HOSTED, LOGGER);
		assertEquals(UpdateResult.SUCCESS, sup.query());
		assertTrue(sup.providesDownloadURL());
		assertEquals("https://api.spiget.org/v2/resources/" + PLUGIN_ID_HOSTED + "/download", sup.getLatestFileURL().getProtocol() + "://" + sup.getLatestFileURL().getHost() + sup.getLatestFileURL().getPath());
		assertEquals(PLUGIN_ID_HOSTED + ".jar", sup.getLatestFileName());
		assertEquals("MobMerge", sup.getLatestName());
	}

	@Test
	public void testQueryFail() throws Exception
	{
		Assume.assumeTrue("Skip if mockito-inline not available", TestUtils.canMockJdkClasses());
		
		SpigotUpdateProvider updateProvider = getProvider();
		URL mockUrlForConnection = mock(URL.class);
		final HttpURLConnection mockedHttpURLConnection = spy(new HttpURLConnection(mockUrlForConnection)
		{
			@Override
			public void connect() { }

			@Override
			public void disconnect() { }

			@Override
			public boolean usingProxy() { return false; }
		});
		try (MockedConstruction<URL> mcURL = mockConstruction(URL.class, (mock, context) -> {
			doReturn(mockedHttpURLConnection).when(mock).openConnection();
		}))
		{
			doReturn(HttpURLConnection.HTTP_MOVED_TEMP).when(mockedHttpURLConnection).getResponseCode();
			assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
			doReturn(HttpURLConnection.HTTP_SEE_OTHER).when(mockedHttpURLConnection).getResponseCode();
			assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
		}
		try (MockedConstruction<URL> mcURL = mockConstruction(URL.class, (mock, context) -> {
			doThrow(new IOException()).when(mock).openConnection();
		}))
		{
			assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
		}
	}

	@Test
	public void testLastResult() throws NoSuchFieldException, IllegalAccessException, RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		TestUtils.initReflection();
		String minecraftVersion = "Minecraft-1.12.2";
		Version version = new Version("v1.12.2");
		SpigotUpdateProvider updateProvider = getProvider();
		// Use real instance - Lombok @Data makes inner class static
		UpdateProvider.UpdateFile updateFile = new UpdateProvider.UpdateFile(null, null, version, null, null, null, minecraftVersion);
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