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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Version;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class BaseOnlineProviderTest
{
	@Test
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testGetLatestReleaseType() throws NotSuccessfullyQueriedException
	{
		BaseOnlineProvider provider = mock(JenkinsUpdateProvider.class);
		Version mockedVersion = mock(Version.class);
		doReturn(true).when(mockedVersion).isPreRelease();
		doReturn("Minecraft-1.11.2-invalid").when(mockedVersion).toString();
		doReturn(mockedVersion).when(provider).getLatestVersion();
		assertEquals("The release type should be unknown", ReleaseType.UNKNOWN, provider.getLatestReleaseType());
		doReturn("Minecraft-1.11.2-snapshot").when(mockedVersion).toString();
		assertEquals("The release type should be snapshot", ReleaseType.SNAPSHOT, provider.getLatestReleaseType());
		doReturn("Minecraft-1.11.2-rc").when(mockedVersion).toString();
		assertEquals("The release type should be rc", ReleaseType.RC, provider.getLatestReleaseType());
		doReturn("Minecraft-1.11.2-beta").when(mockedVersion).toString();
		assertEquals("The release type should be beta", ReleaseType.BETA, provider.getLatestReleaseType());
		doReturn("Minecraft-1.11.2-alpha").when(mockedVersion).toString();
		assertEquals("The release type should be alpha", ReleaseType.ALPHA, provider.getLatestReleaseType());
		doReturn(false).when(mockedVersion).isPreRelease();
		assertEquals("The release type should be release", ReleaseType.RELEASE, provider.getLatestReleaseType());
	}
}