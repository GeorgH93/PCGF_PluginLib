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

import com.google.common.io.Files;

import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class UpdaterTest
{
	private final static Logger LOGGER = Logger.getLogger(UpdaterTest.class.getName());
	private final static File PLUGINS_FOLDER = new File("plugins"), TARGET_FILE = new File(PLUGINS_FOLDER, "updates" + File.separator + "MM.jar");

	@SuppressWarnings("SpellCheckingInspection")
	private static UpdateProvider bukkitProvider;

	private Updater getUpdater(String version)
	{
		return new Updater(PLUGINS_FOLDER, true, false, LOGGER, bukkitProvider, version, TARGET_FILE.getName())
		{
			@Override
			protected void runSync(Runnable runnable)
			{
				runnable.run();
			}

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
			public void waitForAsyncOperation() {}
		};
	}

	@BeforeClass
	public static void prepareTestData()
	{
		//noinspection SpellCheckingInspection
		bukkitProvider = new BukkitUpdateProvider(74734); // 74734 is the bukkit id of marriage masters
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/updates").mkdirs();
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/Updater").mkdirs();
	}

	@SuppressWarnings("SpellCheckingInspection")
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

	@SuppressWarnings("SpellCheckingInspection")
	@Test
	public void testBukkitUpdateProvider()
	{
		assertEquals("The result of the query should be success.", UpdateResult.SUCCESS, bukkitProvider.query(LOGGER));
	}

	@Test
	public void testUpdateCheck()
	{
		Updater updater = getUpdater("1.0");
		updater.checkForUpdate(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result2)
			{
				assertEquals(UpdateResult.UPDATE_AVAILABLE, result2);
			}
		});
	}

	@Test
	public void testUpdateCheckNoUpdateAvailable()
	{
		Updater updater = getUpdater("99.0");
		updater.checkForUpdate(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result2)
			{
				assertEquals(UpdateResult.NO_UPDATE, result2);
			}
		});
	}

	@Test
	public void testUpdateDownload()
	{
		Updater updater = getUpdater("1.0");
		updater.update(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result2)
			{
				assertEquals(UpdateResult.SUCCESS, result2);
				assertTrue(TARGET_FILE.exists());
				//noinspection ResultOfMethodCallIgnored
				TARGET_FILE.delete();
			}
		});
	}

	@Test
	public void testUpdateDownloadNoUpdateAvailable()
	{
		Updater updater = getUpdater("99.0");
		updater.update(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result2)
			{
				assertEquals(UpdateResult.NO_UPDATE, result2);
				assertFalse(TARGET_FILE.exists());
			}
		});
	}

	@Test
	public void testUnzip() throws IOException
	{
		Updater updater = getUpdater("1.0");
		updater.unzip(new File("Not-Found.zip"));
		URL zipArchive = Updater.class.getResource("/ZIP-Archive.zip");
		if (zipArchive != null)
		{
			//noinspection deprecation
			Files.copy(new File(URLDecoder.decode(zipArchive.getPath())), new File("ZIP-Archive.zip"));
		}
		//noinspection ResultOfMethodCallIgnored
		updater.unzip(new File("ZIP-Archive.zip"));
		File jarFile = new File("plugins/updates/Test-JAR.jar");
		assertTrue("The jar file should exist", jarFile.exists());
		assertFalse("The txt file shouldn't exist", new File("plugins/updates/Test-TXT.txt").exists());
		assertFalse("The given jar file should not be found as plugin", updater.isPluginFile("NotFound.jar"));
		File pluginFile = new File("plugins/Test-JAR.jar");
		Files.copy(jarFile, pluginFile);
		//noinspection ResultOfMethodCallIgnored
		jarFile.delete();
		assertTrue("The given jar file should be found as plugin", updater.isPluginFile("Test-JAR.jar"));
		//noinspection ResultOfMethodCallIgnored
		pluginFile.delete();
	}

	@Test
	public void testWithGravityUpdaterConfig() throws IOException, NoSuchFieldException, IllegalAccessException
	{
		URL config = Updater.class.getResource("/gravityUpdaterConfig.yml");
		if (config != null)
		{
			//noinspection deprecation
			Files.copy(new File(URLDecoder.decode(config.getPath())), new File("plugins/Updater/config.yml"));
		}
		Updater updater = getUpdater("1.0");
		Field result = Updater.class.getDeclaredField("result");
		result.setAccessible(true);
		assertEquals("The update function should be disabled", UpdateResult.DISABLED, result.get(updater));
		result.setAccessible(false);
		updater.checkForUpdate(null);
		updater.update();
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/updater/config.yml").delete();
	}

	@Test
	public void testPrepareVersion()
	{
		Updater updater = getUpdater("1.0");
		String[] versionResult = updater.prepareVersion("1.3-alpha");
		assertEquals("The version result length should match", 3, versionResult.length);
		assertEquals("The version result should match", "1", versionResult[0]);
		assertEquals("The version result should match", "2", versionResult[1]);
		assertEquals("The version result should match", String.valueOf(Integer.MAX_VALUE - 10000), versionResult[2]);
		versionResult = updater.prepareVersion("3-beta");
		assertEquals("The version result length should match", 2, versionResult.length);
		assertEquals("The version result should match", "2", versionResult[0]);
		assertEquals("The version result should match", String.valueOf(Integer.MAX_VALUE - 1000), versionResult[1]);
	}

	@Test
	public void testShouldUpdate()
	{
		Updater updater = getUpdater("1.0");
		assertTrue("The plugin should be updated", updater.shouldUpdate("1.0.5"));
		assertFalse("The plugin should not be updated", updater.shouldUpdate("1.0"));
		assertTrue("The plugin should be updated when the newer one could not be determined", updater.shouldUpdate("1.a"));
	}

	@Test
	public void testVersionCheck()
	{
		assertFalse("The version check should return false", getUpdater("1.0").versionCheck(null));
	}

	@AfterClass
	public static void cleanupTestData()
	{
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/updater").delete();
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/updates").delete();
		//noinspection ResultOfMethodCallIgnored
		new File("plugins").delete();
	}
}