/*
 *   Copyright (C) 2016, 2018 GeorgH93
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

import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class AlwaysUpdateProviderTest
{
	private static final String URL = "http://test.com/download/test.jar";
	private final AlwaysUpdateProvider provider = new AlwaysUpdateProvider(URL);

	@Test
	public void testQuery()
	{
		assertEquals("The result should match", UpdateResult.SUCCESS, provider.query());
	}

	@Test
	public void testGetLatestVersion() throws Exception
	{
		assertEquals("The version should match", new Version(Integer.MAX_VALUE + "." + Integer.MAX_VALUE), provider.getLatestVersion());
	}

	@Test
	public void testGetLatestFileURL() throws Exception
	{
		assertEquals("The URL should match", URL, provider.getLatestFileURL().toString());
	}

	@Test
	public void testGetLatestVersionFileName() throws Exception
	{
		assertEquals("The file name should match", "file.jar", provider.getLatestFileName());
	}

	@Test
	public void testGetLatestName() throws Exception
	{
		try
		{
			provider.getLatestName();
		}
		catch(RequestTypeNotAvailableException e)
		{
			assertEquals("This provider does not provide the name of the latest version.", e.getMessage());
		}
	}

	@Test
	public void testGetLatestMinecraftVersion() throws Exception
	{
		try
		{
			provider.getLatestMinecraftVersion();
		}
		catch(RequestTypeNotAvailableException e)
		{
			assertEquals("This provider does not provide the minecraft version of the latest version.", e.getMessage());
		}
	}

	@Test
	public void testGetLatestReleaseType() throws Exception
	{
		assertEquals("The release type should match", ReleaseType.RELEASE, provider.getLatestReleaseType());
		assertEquals("The release type should match", ReleaseType.BETA, new AlwaysUpdateProvider(URL, "file.jar", ReleaseType.BETA).getLatestReleaseType());
	}

	@Test
	public void testGetLatestChecksum() throws Exception
	{
		try
		{
			provider.getLatestChecksum();
		}
		catch(RequestTypeNotAvailableException e)
		{
			assertEquals("This provider does not provide the checksum for the latest file.", e.getMessage());
		}
	}

	@Test
	public void testGetLatestChangelog() throws Exception
	{
		try
		{
			provider.getLatestChangelog();
		}
		catch(RequestTypeNotAvailableException e)
		{
			assertEquals("This provider does not provide the changelog for the latest file.", e.getMessage());
		}
	}

	@Test
	public void testGetLatestDependencies() throws Exception
	{
		try
		{
			provider.getLatestDependencies();
		}
		catch(RequestTypeNotAvailableException e)
		{
			assertEquals("This provider does not provide dependencies for the latest file.", e.getMessage());
		}
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetUpdateHistory() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		provider.getUpdateHistory();
	}

	@Test
	public void testProvidesDownloadURL()
	{
		assertTrue("The download URL should be provided", provider.providesDownloadURL());
	}

	@Test
	public void testProvidesMinecraftVersion()
	{
		assertFalse("The Minecraft version should not be provided", provider.providesMinecraftVersion());
	}

	@Test
	public void testProvidesChangelog()
	{
		assertFalse("The changelog should not be provided", provider.providesChangelog());
	}

	@Test
	public void testProvidesChecksum()
	{
		assertEquals("The provider should not provide a checksum", ChecksumType.NONE, provider.providesChecksum());
	}

	@Test
	public void testProvidesUpdateHistory()
	{
		assertFalse("The update history should not be provided", provider.providesUpdateHistory());
	}

	@Test
	public void testProvidesDependencies()
	{
		assertFalse("The dependencies should not be provided", provider.providesDependencies());
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testMalformedURL() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		AlwaysUpdateProvider updateProvider = new AlwaysUpdateProvider("malformed.url");
		updateProvider.getLatestFileURL();
	}

	@Test
	public void testAlwaysUpdateProvider() throws MalformedURLException, NotSuccessfullyQueriedException
	{
		AlwaysUpdateProvider updateProvider = new AlwaysUpdateProvider(new URL("http://this.is.a.url/"));
		assertEquals("The release type should match", ReleaseType.RELEASE, updateProvider.getLatestReleaseType());
	}
}