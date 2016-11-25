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
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BukkitUpdateProvider.class, Gson.class, ReleaseType.class, URL.class })
@SuppressWarnings("SpellCheckingInspection")
public class BukkitUpdateProviderTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException
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
		Class versionClass = BukkitUpdateProvider.class.getDeclaredClasses()[0];
		Constructor versionConstructor = versionClass.getDeclaredConstructors()[0];
		versionConstructor.setAccessible(true);
		BukkitUpdateProvider bukkitUpdateProvider = new BukkitUpdateProvider(-3);
		Field url = TestUtils.setAccessible(BukkitUpdateProvider.class, bukkitUpdateProvider, "url", null);
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query(mockedLogger));
		assertEquals("The logger should be used as often as given", currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		bukkitUpdateProvider = new BukkitUpdateProvider(74734);
		URL mockedURL = PowerMockito.mock(URL.class);
		PowerMockito.doThrow(new IOException("")).when(mockedURL).openConnection();
		url.set(bukkitUpdateProvider, mockedURL);
		bukkitUpdateProvider.query(mockedLogger);
		currentSevere += 3;
		assertEquals("The logger should be used as often as given", currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		TestUtils.setUnaccessible(url, bukkitUpdateProvider, false);
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query(mockedLogger));
		assertEquals("The logger should be used as often as given", ++currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		Gson mockedGson = PowerMockito.mock(Gson.class);
		PowerMockito.doReturn(Array.newInstance(versionClass, 0)).when(mockedGson).fromJson(any(Reader.class), any(Class.class));
		whenNew(Gson.class).withAnyArguments().thenReturn(mockedGson);
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query(mockedLogger));
		assertEquals("The logger should be used as often as given", ++currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		PowerMockito.doReturn(null).when(mockedGson).fromJson(any(Reader.class), any(Class.class));
		assertEquals("The query should fail", UpdateResult.FAIL_FILE_NOT_FOUND, bukkitUpdateProvider.query(mockedLogger));
		assertEquals("The logger should be used as often as given", ++currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		bukkitUpdateProvider = new BukkitUpdateProvider(74734, "Nothing");
		assertEquals("The API key isn't correct, therefore it should fail", UpdateResult.FAIL_API_KEY, bukkitUpdateProvider.query(mockedLogger));
		currentSevere += 3;
		assertEquals("The logger should be used as often as given", currentWarning, loggerCalls[0]);
		assertEquals("The logger should be used as often as given", currentSevere, loggerCalls[1]);
		versionConstructor.newInstance();
		versionConstructor.setAccessible(false);
	}

	@Test
	public void testGetLatestVersionAsString() throws NoSuchFieldException, IllegalAccessException, NotSuccessfullyQueriedException
	{
		BukkitUpdateProvider bukkitUpdateProvider = spy(getProvider());
		doReturn(null).when(bukkitUpdateProvider).getLatestName();
		assertNull("The latest version should be null", bukkitUpdateProvider.getLatestVersionAsString());
	}

	@Test
	public void testGetLatestVersion() throws NoSuchFieldException, IllegalAccessException, NotSuccessfullyQueriedException
	{
		BukkitUpdateProvider bukkitUpdateProvider = spy(getProvider());
		doReturn("").when(bukkitUpdateProvider).getLatestName();
		assertNull("The latest version should be null", bukkitUpdateProvider.getLatestVersion());
	}

	@Test
	public void testGetLatestFileUrl() throws Exception
	{
		BukkitUpdateProvider bukkitUpdateProvider = getLoggedProvider();
		whenNew(URL.class).withParameterTypes(String.class).withArguments(anyString()).thenThrow(new MalformedURLException());
		assertNull("The latest file url should be null", bukkitUpdateProvider.getLatestFileURL());
	}

	@Test
	public void testGetLatestMinecraftVersion() throws NotSuccessfullyQueriedException
	{
		assertNotNull("The latest Minecraft version should not be null", getLoggedProvider().getLatestMinecraftVersion());
	}

	@Test
	public void testGetLatestReleaseType() throws Exception
	{
		BukkitUpdateProvider bukkitUpdateProvider = getLoggedProvider();
		bukkitUpdateProvider.getLatestReleaseType();
		mockStatic(ReleaseType.class);
		PowerMockito.doThrow(new IllegalArgumentException()).when(ReleaseType.class, "valueOf", anyString());
		assertEquals("The release type should be unknown", ReleaseType.UNKNOWN, bukkitUpdateProvider.getLatestReleaseType());
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestVersionFileName() throws NoSuchFieldException, IllegalAccessException, NotSuccessfullyQueriedException
	{
		getProvider().getLatestVersionFileName();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestName() throws NoSuchFieldException, IllegalAccessException, NotSuccessfullyQueriedException
	{
		getProvider().getLatestName();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestChecksum() throws NoSuchFieldException, IllegalAccessException, NotSuccessfullyQueriedException
	{
		getProvider().getLatestChecksum();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestChangelog() throws NoSuchFieldException, IllegalAccessException, RequestTypeNotAvailableException
	{
		getProvider().getLatestChangelog();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestDependencies() throws NoSuchFieldException, IllegalAccessException, RequestTypeNotAvailableException
	{
		getProvider().getLatestDependencies();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestFileURLWithError() throws NoSuchFieldException, IllegalAccessException, NotSuccessfullyQueriedException
	{
		getProvider().getLatestFileURL();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestMinecraftVersionWithError() throws NoSuchFieldException, IllegalAccessException, NotSuccessfullyQueriedException
	{
		getProvider().getLatestMinecraftVersion();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestReleaseTypeWithError() throws NoSuchFieldException, IllegalAccessException, NotSuccessfullyQueriedException
	{
		getProvider().getLatestReleaseType();
	}

	private BukkitUpdateProvider getProvider() throws NoSuchFieldException, IllegalAccessException
	{
		BukkitUpdateProvider bukkitUpdateProvider = new BukkitUpdateProvider(74734);
		TestUtils.setAccessible(BukkitUpdateProvider.class, bukkitUpdateProvider, "devBukkitVersions", null);
		return bukkitUpdateProvider;
	}

	private BukkitUpdateProvider getLoggedProvider()
	{
		Logger mockedLogger = mock(Logger.class);
		BukkitUpdateProvider bukkitUpdateProvider = new BukkitUpdateProvider(74734);
		bukkitUpdateProvider.query(mockedLogger);
		return bukkitUpdateProvider;
	}
}