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

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.TestClasses.LogCapture;
import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Updater.UpdateResult;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BukkitUpdateProviderTest
{
	private static final Logger LOGGER = Logger.getLogger(BukkitUpdateProviderTest.class.getName());
	private LogCapture logCapture;
	
	@Before
	public void setUp()
	{
		logCapture = new LogCapture();
		LogCapture.createTestLogger(BukkitUpdateProviderTest.class.getName(), logCapture);
	}
	
	@After
	public void tearDown()
	{
		LOGGER.removeHandler(logCapture);
	}
	
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		TestUtils.initReflection();
	}

	@Test
	public void testQuery() throws Exception
	{
		Assume.assumeTrue("Skip if mockito-inline not available", TestUtils.canMockJdkClasses());
		int currentWarning = 0;
		int currentSevere = 0;
		Class<?> versionClass = BukkitUpdateProvider.class.getDeclaredClasses()[0];
		Constructor<?> versionConstructor = versionClass.getDeclaredConstructors()[0];
		versionConstructor.setAccessible(true);
		BukkitUpdateProvider bukkitUpdateProvider = new BukkitUpdateProvider(-3, LOGGER);
		Field url = TestUtils.setAccessible(BukkitUpdateProvider.class, bukkitUpdateProvider, "url", null);
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
		assertEquals("The logger should be used as often as given", currentWarning, logCapture.getRecordCountByLevel(Level.WARNING));
		assertEquals("The logger should be used as often as given", currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
		bukkitUpdateProvider = new BukkitUpdateProvider(74734, LOGGER);
		URL mockedURL = Mockito.mock(URL.class);
		doThrow(new IOException("")).when(mockedURL).openConnection();
		url.set(bukkitUpdateProvider, mockedURL);
		bukkitUpdateProvider.query();
		currentSevere += 3;
		assertEquals("The logger should be used as often as given", currentWarning, logCapture.getRecordCountByLevel(Level.WARNING));
		assertEquals("The logger should be used as often as given", currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
		TestUtils.setUnaccessible(url, bukkitUpdateProvider, false);
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
		assertEquals("The logger should be used as often as given", ++currentWarning, logCapture.getRecordCountByLevel(Level.WARNING));
		assertEquals("The logger should be used as often as given", currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
		Gson mockedGson = Mockito.mock(Gson.class);
		doReturn(Array.newInstance(versionClass, 0)).when(mockedGson).fromJson(any(Reader.class), any(Class.class));
		Field gsonField = TestUtils.setAccessible(BaseOnlineProvider.class, null, "GSON", mockedGson);
		try (MockedConstruction<Gson> mcGson = Mockito.mockConstruction(Gson.class, (mock, context) -> {
			doReturn(Array.newInstance(versionClass, 0)).when(mock).fromJson(any(Reader.class), any(Class.class));
		}))
		{
			assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
			assertEquals("The logger should be used as often as given", ++currentWarning, logCapture.getRecordCountByLevel(Level.WARNING));
			assertEquals("The logger should be used as often as given", currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
			doReturn(null).when(mockedGson).fromJson(any(Reader.class), any(Class.class));
			assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
			assertEquals("The logger should be used as often as given", ++currentWarning, logCapture.getRecordCountByLevel(Level.WARNING));
			assertEquals("The logger should be used as often as given", currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
			Class<?> devBukkitVersionClass = BukkitUpdateProvider.class.getDeclaredClasses()[0];
			Object devBukkitVersions = Array.newInstance(devBukkitVersionClass, 3);
			Object devBukkitVersion = devBukkitVersionClass.newInstance();
			Field latestNameField = TestUtils.setAccessible(devBukkitVersionClass, devBukkitVersion, "name", "INVALID-VERSION-STRING");
			Arrays.fill((Object[]) devBukkitVersions, devBukkitVersion);
			doReturn(devBukkitVersions).when(mockedGson).fromJson(any(Reader.class), any(Class.class));
			assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
			assertEquals("The logger should be used as often as given", currentWarning, logCapture.getRecordCountByLevel(Level.WARNING));
			assertEquals("The logger should be used as often as given", ++currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
			Field downloadURLField = TestUtils.setAccessible(devBukkitVersionClass, devBukkitVersion, "downloadUrl", "https://dl.url.org/dl");
			assertEquals("No valid version should be found", UpdateResult.FAIL_NO_VERSION_FOUND, bukkitUpdateProvider.query());
			TestUtils.setUnaccessible(downloadURLField, devBukkitVersion, false);
			TestUtils.setUnaccessible(latestNameField, devBukkitVersion, false);
		}
		TestUtils.setUnaccessible(gsonField, null, true);
		versionConstructor.newInstance();
		versionConstructor.setAccessible(false);
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestVersion() throws NotSuccessfullyQueriedException
	{
		getProvider().getLatestVersion();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestFileUrl() throws Exception
	{
		getProvider().getLatestFileURL();
	}

	@Test
	public void testGetLatestMinecraftVersion() throws NotSuccessfullyQueriedException
	{
		assertNotNull("The latest Minecraft version should not be null", getLoggedProvider().getLatestMinecraftVersion());
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestVersionFileName() throws NotSuccessfullyQueriedException
	{
		getProvider().getLatestFileName();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestName() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		getProvider().getLatestName();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestChecksum() throws NotSuccessfullyQueriedException
	{
		getProvider().getLatestChecksum();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestChangelog() throws RequestTypeNotAvailableException
	{
		getProvider().getLatestChangelog();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestDependencies() throws RequestTypeNotAvailableException
	{
		getProvider().getLatestDependencies();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestFileURLWithError() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		getProvider().getLatestFileURL();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestMinecraftVersionWithError() throws NotSuccessfullyQueriedException
	{
		getProvider().getLatestMinecraftVersion();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestReleaseTypeWithError() throws NotSuccessfullyQueriedException
	{
		getProvider().getLatestReleaseType();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestNameException() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		getProvider().getLatestName();
	}

	@Test
	public void testGetLatestNameSuccess() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		getProvider().query();
		assertNotNull("The latest name should not be null", getLoggedProvider().getLatestName());
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetUpdateHistoryException() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		getProvider().getUpdateHistory();
	}

	@Test
	public void testGetUpdateHistorySuccess() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		assertNotNull("The latest name should not be null", getLoggedProvider().getUpdateHistory());
	}

	private BukkitUpdateProvider getProvider()
	{
		//noinspection ConstantConditions
		return new BukkitUpdateProvider(74734, null);
	}

	private BukkitUpdateProvider getLoggedProvider()
	{
		BukkitUpdateProvider bukkitUpdateProvider = new BukkitUpdateProvider(74734, LOGGER);
		bukkitUpdateProvider.query();
		return bukkitUpdateProvider;
	}
}