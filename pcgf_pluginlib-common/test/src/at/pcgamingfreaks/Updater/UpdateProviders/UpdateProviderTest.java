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

import at.pcgamingfreaks.Version;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class UpdateProviderTest
{
	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateFileClass() throws IllegalAccessException, InvocationTargetException, InstantiationException, MalformedURLException, NoSuchMethodException
	{
		URL downloadURL = new URL("http://www.download.url/File.dl");
		String name = "Update File";
		Version version = new Version("1.3.6");
		String fileName = "UpdateFile-1.3.6.zip";
		UpdateProvider.UpdateFile updaterFile = new UpdateProvider.UpdateFile(downloadURL, name, version, fileName, "", "", "");
		assertEquals("The download url of the update file should match", downloadURL, updaterFile.getDownloadURL());
		assertEquals("The name of the update file should match", name, updaterFile.getName());
		assertEquals("The file name of the update file should match", fileName, updaterFile.getFileName());
		assertEquals("The version of the update file should match", version, updaterFile.getVersion());
		updaterFile.setGameVersion("Minecraft 1.11.2");
		assertEquals("The game version should match", "Minecraft 1.11.2", updaterFile.getGameVersion());
	}
}