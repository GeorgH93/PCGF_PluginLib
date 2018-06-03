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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.Updater.UpdateResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BaseOnlineProviderWithDownload.class, GitHubUpdateProvider.class, URL.class })
public class GitHubUpdateProviderTest
{
	@Test
	public void testQuery()
	{
		Logger mockedLogger = mock(Logger.class);
		GitHubUpdateProvider updateProvider = new GitHubUpdateProvider("MarkusWME", "Minecraft-Fly-Mod", mockedLogger);
		assertEquals("The update result should match", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
		updateProvider = new GitHubUpdateProvider("MarkusWME", "Minecraft-Fly-Mod", "Minecraft-Fly-Mod", ".*\\.litemod", ".*\\.litemod", mockedLogger);
		assertEquals("The update result should match", UpdateResult.SUCCESS, updateProvider.query());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testQueryWithInvalidRepository() throws Exception
	{
		Logger mockedLogger = mock(Logger.class);
		GitHubUpdateProvider updateProvider = new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", mockedLogger);
		assertEquals("The update result should match", UpdateResult.FAIL_SERVER_OFFLINE, updateProvider.query());
		whenNew(URL.class).withArguments(anyString()).thenThrow(new MalformedURLException());
		updateProvider = new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", mockedLogger);
		assertEquals("The update result should match", UpdateResult.FAIL_FILE_NOT_FOUND, updateProvider.query());
	}

	@Test
	public void testUpdateProviderProperties() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		Logger mockedLogger = mock(Logger.class);
		GitHubUpdateProvider updateProvider = new GitHubUpdateProvider("GeorgH93", "TelePlusPlus", mockedLogger);
		assertEquals("The latest changelog should match", "", updateProvider.getLatestChangelog());
		assertFalse("The Minecraft version should not be provided", updateProvider.providesMinecraftVersion());
		assertFalse("The changelog should currently not be provided", updateProvider.providesChangelog());
		assertFalse("The update history should not be available", updateProvider.providesUpdateHistory());
		assertFalse("No dependencies should be provided", updateProvider.providesDependencies());
	}
}