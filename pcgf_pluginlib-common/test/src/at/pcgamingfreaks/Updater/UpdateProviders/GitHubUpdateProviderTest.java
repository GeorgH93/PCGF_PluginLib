/*
 * Copyright (C) 2018 MarkusWME
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

import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.UpdateResult;

import org.jetbrains.annotations.NotNull;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;

public class GitHubUpdateProviderTest
{ //TODO the connection to github should be mocked away to improve the reliability of the test
	private final Logger logger = Logger.getLogger("GitHubUpdateProviderTest");

	@Test
	public void testWithoutMD5()
	{
		GitHubUpdateProvider updateProvider = new GitHubUpdateProvider("MarkusWME", "Minecraft-Fly-Mod", "Minecraft-Fly-Mod", ".*\\.jar", null, logger);
		assertNotNull("The GitHubUpdateProvider should not be null", updateProvider);
		@NotNull UpdateResult result = updateProvider.query();
		if(result == UpdateResult.FAIL_API_KEY) return; // Workaround for Travis-CI
		assertEquals("", UpdateResult.FAIL_FILE_NOT_FOUND, result);
		assertEquals("No checksum should be provided", ChecksumType.NONE, updateProvider.providesChecksum());
	}

	@Test
	public void testQuery()
	{
		GitHubUpdateProvider updateProvider = new GitHubUpdateProvider("MarkusWME", "Minecraft-Fly-Mod", logger);
		@NotNull UpdateResult result = updateProvider.query();
		if(result == UpdateResult.FAIL_API_KEY) return; // Workaround for Travis-CI
		assertEquals("The update result should match", UpdateResult.FAIL_FILE_NOT_FOUND, result);
		updateProvider = new GitHubUpdateProvider("MarkusWME", "Minecraft-Fly-Mod", "Minecraft-Fly-Mod", ".*\\.litemod", ".*\\.litemod", logger);
		assertEquals("The update result should match", UpdateResult.SUCCESS, updateProvider.query());
	}

	@Test
	public void testQueryWithInvalidRepository() throws Exception
	{
		GitHubUpdateProvider updateProvider = new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", logger);
		assertEquals("No checksum should be provided", ChecksumType.MD5, updateProvider.providesChecksum());
		@NotNull UpdateResult result = updateProvider.query();
		if(result == UpdateResult.FAIL_API_KEY) return; // Workaround for Travis-CI
		assertEquals("The update result should match", UpdateResult.FAIL_SERVER_OFFLINE, result);
		Assume.assumeTrue("Skip if mockito-inline not available", TestUtils.canMockJdkClasses());
		try (MockedConstruction<URL> mcURL = Mockito.mockConstruction(URL.class, (mock, context) -> {
			throw new MalformedURLException();
		}))
		{
			updateProvider = new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", logger);
			assertEquals("The update result should match", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
		}
	}

	@Test
	public void testUpdateProviderProperties() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		GitHubUpdateProvider updateProvider = new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", logger);
		assertEquals("The latest changelog should match", "", updateProvider.getLatestChangelog());
		assertFalse("The Minecraft version should not be provided", updateProvider.providesMinecraftVersion());
		assertFalse("The changelog should currently not be provided", updateProvider.providesChangelog());
		assertFalse("The update history should not be available", updateProvider.providesUpdateHistory());
		assertFalse("No dependencies should be provided", updateProvider.providesDependencies());
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestMinecraftVersion() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", logger).getLatestMinecraftVersion();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestDependencies() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", logger).getLatestDependencies();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetUpdateHistory() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", logger).getUpdateHistory();
	}

	@Test(expected = RequestTypeNotAvailableException.class)
	public void testGetLatestChecksum() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		new GitHubUpdateProvider("MarkusWME", "Minecraft-Fly-Mod", "Minecraft-Fly-Mod", ".*\\.jar", null, logger).getLatestChecksum();
	}

	@Test(expected = NotSuccessfullyQueriedException.class)
	public void testGetLatestChecksumWithMD5() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", logger).getLatestChecksum();
	}
}