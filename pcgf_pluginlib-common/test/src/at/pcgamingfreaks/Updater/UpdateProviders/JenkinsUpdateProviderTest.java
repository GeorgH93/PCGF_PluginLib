/*
 * Copyright (C) 2017 MarkusWME
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
import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.UpdateResult;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JenkinsUpdateProviderTest
{
	private static final Logger LOGGER = Logger.getLogger(JenkinsUpdateProviderTest.class.getName());
	private LogCapture logCapture;
	
	@Before
	public void setUp()
	{
		logCapture = new LogCapture();
		LogCapture.createTestLogger(JenkinsUpdateProviderTest.class.getName(), logCapture);
	}
	
	@After
	public void tearDown()
	{
		LOGGER.removeHandler(logCapture);
	}
	@Test(expected = NullPointerException.class)
	public void testJenkinsUpdateProviderWithoutJob()
	{
		//noinspection ConstantConditions
		new JenkinsUpdateProvider("", null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testJenkinsUpdateProviderWithoutHost()
	{
		//noinspection ConstantConditions
		new JenkinsUpdateProvider(null, null, null);
	}

	@Test
	public void testQuery() throws Exception
	{
		int currentWarnings = 0;
		int currentSevere = 0;
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("abc://invalid/", "NOPE", LOGGER);
		assertEquals("The invalid query should return a failure", UpdateResult.FAIL_FILE_NOT_FOUND, updater.query());
		assertEquals("A warning should be shown", currentWarnings, logCapture.getRecordCountByLevel(Level.WARNING));
		assertEquals("An error should be shown", ++currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
		updater = new JenkinsUpdateProvider("ci.pcgamingfreaks.at", "PluginLib", "", LOGGER);
		assertEquals("The query should be successful", UpdateResult.SUCCESS, updater.query());
		assertEquals("No warning should be shown", currentWarnings, logCapture.getRecordCountByLevel(Level.WARNING));
		updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", "", LOGGER);
		assertEquals("The query should be successful", UpdateResult.SUCCESS, updater.query());
		assertEquals("No warning should be shown", currentWarnings, logCapture.getRecordCountByLevel(Level.WARNING));
		Assume.assumeTrue("Skip if mockito-inline not available", TestUtils.canMockJdkClasses());
		TestUtils.initReflection();
		java.net.URL mockedURL = mock(java.net.URL.class);
		doThrow(new java.io.IOException("HTTP response code: 403")).when(mockedURL).openConnection();
		Field urlField = TestUtils.setAccessible(JenkinsUpdateProvider.class, updater, "url", mockedURL);
		assertEquals("The query should return a failure", UpdateResult.FAIL_API_KEY, updater.query());
		currentSevere += 2;
		assertEquals("The logger should log the error", currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
		Field tokenField = TestUtils.setAccessible(JenkinsUpdateProvider.class, updater, "token", null);
		assertEquals("The query should return a failure", UpdateResult.FAIL_API_KEY, updater.query());
		currentSevere += 2;
		assertEquals("The logger should log the error", currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
		TestUtils.setUnaccessible(tokenField, updater, true);
		doThrow(new java.io.IOException("")).when(mockedURL).openConnection();
		assertEquals("The query should return a offline message", UpdateResult.FAIL_SERVER_OFFLINE, updater.query());
		currentSevere += 3;
		assertEquals("The logger should log the error", currentSevere, logCapture.getRecordCountByLevel(Level.SEVERE));
		TestUtils.setUnaccessible(urlField, updater, true);
		updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER, "PLib");
		updater.query();
		assertNotNull("The updater object should not be null", updater);
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestVersionFailure() throws NotSuccessfullyQueriedException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("abc://invalid/", "NOPE", LOGGER);
		updater.getLatestVersion();
	}

	@Test
	public void testGetLatestVersionSuccess() throws NotSuccessfullyQueriedException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER);
		updater.query();
		assertNotNull("The latest version should not be null", updater.getLatestVersion());
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestFileURLFailure() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("abc://invalid/", "NOPE", LOGGER);
		updater.getLatestFileURL();
	}

	@Test
	public void testGetLatestFileURLSuccess() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER);
		updater.query();
		assertNotNull("The latest file URL should not be null", updater.getLatestFileURL());
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestVersionFileNameFailure() throws NotSuccessfullyQueriedException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("abc://invalid/", "NOPE", LOGGER);
		updater.getLatestFileName();
	}

	@Test
	public void testGetLatestVersionFileNameSuccess() throws NotSuccessfullyQueriedException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER);
		updater.query();
		assertNotNull("The latest version file name should not be null", updater.getLatestFileName());
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestNameFailure() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("abc://invalid/", "NOPE", LOGGER);
		updater.getLatestName();
	}

	@Test
	public void testGetLatestNameSuccess() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER);
		updater.query();
		assertNotNull("The latest version should not be null", updater.getLatestName());
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestChecksumFailure() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("abc://invalid/", "NOPE", LOGGER);
		updater.getLatestChecksum();
	}

	@Test
	public void testGetLatestChecksumSuccess() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER);
		updater.query();
		assertNotNull("The latest checksum should not be null", updater.getLatestChecksum());
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestChangelogFailure() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("abc://invalid/", "NOPE", LOGGER);
		updater.getLatestChangelog();
	}

	@Test
	public void testGetLatestChangelogSuccess() throws NotSuccessfullyQueriedException, RequestTypeNotAvailableException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER);
		updater.query();
		assertNotNull("The latest changelog should not be null", updater.getLatestChangelog());
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestMinecraftVersion() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER);
		updater.getLatestMinecraftVersion();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestDependencies() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER);
		updater.getLatestDependencies();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetUpdateHistory() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", LOGGER);
		updater.getUpdateHistory();
	}

	@Test
	public void testSettings() throws IllegalAccessException, InvocationTargetException, InstantiationException
	{
		//noinspection ConstantConditions
		JenkinsUpdateProvider updater = new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", null);
		assertTrue("The JenkinsUpdateProvider should provide a download URL", updater.providesDownloadURL());
		assertTrue("The JenkinsUpdateProvider should provide a changelog", updater.providesChangelog());
		assertEquals("The JenkinsUpdateProvider should provide a MD5 checksum", ChecksumType.MD5, updater.providesChecksum());
		assertFalse("The JenkinsUpdateProvider should not provide a Minecraft version", updater.providesMinecraftVersion());
		assertFalse("The JenkinsUpdateProvider should not provide a update history", updater.providesUpdateHistory());
		assertFalse("The JenkinsUpdateProvider should not provide dependencies", updater.providesDependencies());
	}
}