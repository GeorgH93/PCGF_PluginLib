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

package at.pcgamingfreaks.Updater;

import at.pcgamingfreaks.Updater.UpdateProviders.BukkitUpdateProvider;
import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class UpdaterTest
{
	private final static Logger LOGGER = Logger.getLogger(UpdaterTest.class.getName());
	private final static File PLUGINS_FOLDER = new File("plugins"), TARGET_FILE = new File(PLUGINS_FOLDER, "updates" + File.separator + "MM.jar");

	private UpdateProvider bukkitProvider;

	@Before
	public void setUp()
	{
		bukkitProvider = new BukkitUpdateProvider(74734); // 74734 is the bukkit id of marriage masters
	}

	//region test bukkit update provider
	@Test
	public void testBukkitUpdateProviderProperties()
	{
		assertFalse(bukkitProvider.provideDependencies());
		assertFalse(bukkitProvider.provideChangelog());
		assertTrue(bukkitProvider.provideDownloadURL());
		assertTrue(bukkitProvider.provideMD5Checksum());
		assertTrue(bukkitProvider.provideMinecraftVersion());
		assertTrue(bukkitProvider.provideUpdateHistory());
		assertTrue(bukkitProvider.provideReleaseType());
	}

	@Test
	public void testBukkitUpdateProvider()
	{
		assertEquals("The result of the query should be success.", UpdateResult.SUCCESS, bukkitProvider.query(LOGGER));
	}
	//endregion

	@Test
	public void testUpdateCheck()
	{
		Updater updater = new Updater(PLUGINS_FOLDER, true, false, LOGGER, bukkitProvider, "1.0", TARGET_FILE.getName())
		{
			// No async for testing purposes, so we don't need to sync it back
			@Override
			protected void runSync(Runnable runnable)
			{
				runnable.run();
			}

			// No async for testing purposes
			@Override
			protected void runAsync(Runnable runnable)
			{
				runnable.run();
			}

			@Override
			protected @NotNull String getAuthor()
			{
				return "GeorgH93";
			}

			@Override
			public void waitForAsyncOperation() {} // We are aren't running async
		};
		updater.checkForUpdate(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result2)
			{
				assertEquals(UpdateResult.UPDATE_AVAILABLE, result2);
			}
		});
		updater.update(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result2)
			{
				assertEquals(UpdateResult.SUCCESS, result2);
				assertTrue(TARGET_FILE.exists());
				//noinspection ResultOfMethodCallIgnored
				TARGET_FILE.delete(); // Cleanup, we don't need the file any longer
			}
		});
	}
}