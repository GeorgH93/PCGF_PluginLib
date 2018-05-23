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

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Updater.UpdateResult;

import com.google.common.base.Supplier;
import com.google.gson.Gson;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BukkitUpdateProvider.class, Gson.class, ReleaseType.class, URL.class })
@SuppressWarnings("SpellCheckingInspection")
public class BukkitUpdateProviderTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		TestUtils.initReflection();
	}

	@Test
	public void testQuery() throws Exception
	{
		int currentWarning = 0;
		int currentSevere = 0;
		final int[] loggerCalls = new int[] { 0, 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock)
			{
				loggerCalls[0]++;
				return null;
			}
		}).when(mockedLogger).warning(anyString());
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock)
			{
				loggerCalls[1]++;
				return null;
			}
		}).when(mockedLogger).severe(anyString());
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock)
			{
				loggerCalls[1]++;
				return null;
			}
		}).when(mockedLogger).log(any(Level.class), String.valueOf(any(Supplier.class)), any(Throwable.class));
		Class versionClass = BukkitUpdateProvider.class.getDeclaredClasses()[0];
		Constructor versionConstructor = versionClass.getDeclaredConstructors()[0];
		versionConstructor.setAccessible(true);
		BukkitUpdateProvider bukkitUpdateProvider = new BukkitUpdateProvider(-3, mockedLogger);
		Field url = TestUtils.setAccessible(BukkitUpdateProvider.class, bukkitUpdateProvider, "url", null);
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
		assertEquals("The logger should be used as often as given", currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		bukkitUpdateProvider = new BukkitUpdateProvider(74734, mockedLogger);
		URL mockedURL = PowerMockito.mock(URL.class);
		PowerMockito.doThrow(new IOException("")).when(mockedURL).openConnection();
		url.set(bukkitUpdateProvider, mockedURL);
		bukkitUpdateProvider.query();
		currentSevere += 3;
		assertEquals("The logger should be used as often as given", currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		TestUtils.setUnaccessible(url, bukkitUpdateProvider, false);
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
		assertEquals("The logger should be used as often as given", ++currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		Gson mockedGson = PowerMockito.mock(Gson.class);
		PowerMockito.doReturn(Array.newInstance(versionClass, 0)).when(mockedGson).fromJson(any(Reader.class), any(Class.class));
		whenNew(Gson.class).withAnyArguments().thenReturn(mockedGson);
		Field gsonField = TestUtils.setAccessible(BaseOnlineProvider.class, null, "GSON", mockedGson);
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
		assertEquals("The logger should be used as often as given", ++currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		PowerMockito.doReturn(null).when(mockedGson).fromJson(any(Reader.class), any(Class.class));
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
		assertEquals("The logger should be used as often as given", ++currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		Class devBukkitVersionClass = BukkitUpdateProvider.class.getDeclaredClasses()[0];
		Object devBukkitVersions = Array.newInstance(devBukkitVersionClass, 3);
		Object devBukkitVersion = devBukkitVersionClass.newInstance();
		Field latestNameField = TestUtils.setAccessible(devBukkitVersionClass, devBukkitVersion, "name", "INVALID-VERSION-STRING");
		for(int i = 0; i < ((Object[]) devBukkitVersions).length; i++)
		{
			((Object[]) devBukkitVersions)[i] = devBukkitVersion;
		}
		PowerMockito.doReturn(devBukkitVersions).when(mockedGson).fromJson(any(Reader.class), any(Class.class));
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query());
		assertEquals("The logger should be used as often as given", ++currentWarning, loggerCalls[0]);
		Field downloadURLField = TestUtils.setAccessible(devBukkitVersionClass, devBukkitVersion, "downloadUrl", "http://dl.url.org/dl");
		assertEquals("No valid version should be found", UpdateResult.FAIL_NO_VERSION_FOUND, bukkitUpdateProvider.query());
		TestUtils.setUnaccessible(downloadURLField, devBukkitVersion, false);
		TestUtils.setUnaccessible(latestNameField, devBukkitVersion, false);
		TestUtils.setUnaccessible(gsonField, null, true);
		bukkitUpdateProvider = new BukkitUpdateProvider(74734, "Nothing", mockedLogger);
		assertEquals("The API key isn't correct, therefore it should fail", UpdateResult.FAIL_API_KEY, bukkitUpdateProvider.query());
		currentSevere += 3;
		assertEquals("The logger should be used as often as given", currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
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
		Logger mockedLogger = mock(Logger.class);
		BukkitUpdateProvider bukkitUpdateProvider = new BukkitUpdateProvider(74734, mockedLogger);
		bukkitUpdateProvider.query();
		return bukkitUpdateProvider;
	}
}