/*
 *   Copyright (C) 2016 GeorgH93
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

import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlwaysUpdateProviderTest
{
	private static final String URL = "http://test.com/download/test.jar";
	private final AlwaysUpdateProvider provider = new AlwaysUpdateProvider(URL);

	@Test
	public void testQuery() throws Exception
	{
		assertEquals("The result should match", UpdateResult.SUCCESS, provider.query(null));
	}

	@Test
	public void testGetLatestVersionAsString() throws Exception
	{
		assertEquals("The version strings should match", Integer.MAX_VALUE + "." + Integer.MAX_VALUE, provider.getLatestVersionAsString());
	}

	@Test
	public void testGetLatestVersion() throws Exception
	{
		assertTrue("The version should match", new Version(Integer.MAX_VALUE + "." + Integer.MAX_VALUE).equals(provider.getLatestVersion()));
	}

	@Test
	public void testGetLatestFileURL() throws Exception
	{
		assertEquals("The URL should match", URL, provider.getLatestFileURL().toString());
	}

	@Test
	public void testGetLatestVersionFileName() throws Exception
	{
		assertEquals("The file name should match", "file.jar", provider.getLatestVersionFileName());
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

	@Test
	public void testProvideDownloadURL() throws Exception
	{
		assertTrue("The download URL should be provided", provider.provideDownloadURL());
	}

	@Test
	public void testProvideMinecraftVersion() throws Exception
	{
		assertFalse("The Minecraft version should not be provided", provider.provideMinecraftVersion());
	}

	@Test
	public void testProvideChangelog() throws Exception
	{
		assertFalse("The changelog should not be provided", provider.provideChangelog());
	}

	@Test
	public void testProvideMD5Checksum() throws Exception
	{
		assertFalse("The MD5 checksum should not be provided", provider.provideMD5Checksum());
	}

	@Test
	public void testProvideReleaseType() throws Exception
	{
		assertTrue("The release type should not be provided", provider.provideReleaseType());
	}

	@Test
	public void testProvideUpdateHistory() throws Exception
	{
		assertFalse("The update history should not be provided", provider.provideUpdateHistory());
	}

	@Test
	public void testProvideDependencies() throws Exception
	{
		assertFalse("The dependencies should not be provided", provider.provideDependencies());
	}

	@Test
	public void testWithInvalidURL() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		assertNull("An invalid URL should throw an exception and therefore not lead to a download URL object", new AlwaysUpdateProvider("x://invalid-path").getLatestFileURL());
	}
}