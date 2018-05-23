/*
 *   Copyright (C) 2016, 2017 GeorgH93
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

import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.Updater.UpdateProviders.BukkitUpdateProvider;
import at.pcgamingfreaks.Updater.UpdateProviders.NotSuccessfullyQueriedException;
import at.pcgamingfreaks.Updater.UpdateProviders.RequestTypeNotAvailableException;
import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;
import at.pcgamingfreaks.Utils;
import at.pcgamingfreaks.Version;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ByteStreams.class, FileOutputStream.class, Updater.class, Utils.class })
public class UpdaterTest
{
	private static final Logger LOGGER = Logger.getLogger(UpdaterTest.class.getName());
	private static final File PLUGINS_FOLDER = new File("plugins"), TARGET_FILE = new File(PLUGINS_FOLDER, "updates" + File.separator + "MM.jar");

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

	private void setFile(URL source, File target) throws IOException
	{
		if(source != null)
		{
			if(target.exists())
			{
				//noinspection StatementWithEmptyBody
				while(!target.delete())
				{
				}
			}
			//noinspection deprecation
			Files.copy(new File(URLDecoder.decode(source.getPath())), target);
		}
	}

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		//noinspection SpellCheckingInspection
		bukkitProvider = new BukkitUpdateProvider(74734, LOGGER); // 74734 is the Bukkit id of Marriage Master
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/updates").mkdirs();
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/Updater").mkdirs();
		TestUtils.initReflection();
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Test
	public void testBukkitUpdateProviderProperties()
	{
		assertFalse(bukkitProvider.providesChangelog());
		assertFalse(bukkitProvider.providesDependencies());
		assertTrue(bukkitProvider.providesDownloadURL());
		assertTrue(bukkitProvider.providesMinecraftVersion());
		assertTrue(bukkitProvider.providesUpdateHistory());
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Test
	public void testBukkitUpdateProvider()
	{
		assertEquals("The result of the query should be success.", UpdateResult.SUCCESS, bukkitProvider.query());
	}

	@Test
	public void testUpdateCheck() throws NoSuchFieldException, IllegalAccessException
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
		UpdateProvider mockedUpdateProvider = mock(UpdateProvider.class);
		doReturn(UpdateResult.DISABLED).when(mockedUpdateProvider).query();
		Field updateProvider = TestUtils.setAccessible(Updater.class, updater, "updateProvider", mockedUpdateProvider);
		updater.checkForUpdate(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result2)
			{
				assertEquals(UpdateResult.DISABLED, result2);
			}
		});
		TestUtils.setUnaccessible(updateProvider, updater, true);
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
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/updates").delete();
		updater.update(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result2)
			{
				assertEquals("The update result should be correct", UpdateResult.SUCCESS, result2);
				assertTrue("The target file should exist", TARGET_FILE.exists());
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
				assertEquals("The update result should be correct", UpdateResult.NO_UPDATE, result2);
				assertFalse("The target file should not exist", TARGET_FILE.exists());
			}
		});
	}

	@Test
	public void testUpdate() throws NoSuchFieldException, IllegalAccessException, RequestTypeNotAvailableException, NotSuccessfullyQueriedException, InterruptedException, MalformedURLException
	{
		int shouldHaveUpdateResponses = 0;
		final int[] updateResponses = { 0 };
		final Updater.UpdaterResponse updaterResponse = new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result)
			{
				updateResponses[0]++;
			}
		};
		final Updater updater = spy(getUpdater("1.0"));
		UpdateProvider mockedUpdateProvider = mock(UpdateProvider.class);
		doReturn(UpdateResult.DISABLED).when(mockedUpdateProvider).query();
		Field updateProvider = TestUtils.setAccessible(Updater.class, updater, "updateProvider", mockedUpdateProvider);
		updater.update(updaterResponse);
		assertEquals("There should be an update response", ++shouldHaveUpdateResponses, updateResponses[0]);
		final Field result = TestUtils.setAccessible(Updater.class, updater, "result", UpdateResult.NO_UPDATE);
		doReturn(UpdateResult.SUCCESS).when(mockedUpdateProvider).query();
		doReturn(false).when(mockedUpdateProvider).providesDownloadURL();
		doReturn(null).when(updater).getRemoteVersion();
		doReturn(true).when(updater).versionCheck((Version) anyObject());
		updater.update(updaterResponse);
		assertEquals("There should be an update response", ++shouldHaveUpdateResponses, updateResponses[0]);
		doReturn("test.zip").when(mockedUpdateProvider).getLatestFileName();
		updater.update(updaterResponse);
		assertEquals("There should be an update response", ++shouldHaveUpdateResponses, updateResponses[0]);
		doReturn(true).when(mockedUpdateProvider).providesDownloadURL();
		doReturn(null).when(mockedUpdateProvider).getLatestFileURL();
		updater.update(updaterResponse);
		assertEquals("There should be an update response", ++shouldHaveUpdateResponses, updateResponses[0]);
		doThrow(new NotSuccessfullyQueriedException()).when(mockedUpdateProvider).getLatestFileURL();
		updater.update();
		assertEquals("There should not be an update response", shouldHaveUpdateResponses, updateResponses[0]);
		doReturn(new URL("http://www.test.xyz")).when(mockedUpdateProvider).getLatestFileURL();
		doReturn(true).when(mockedUpdateProvider).providesDependencies();
		UpdateProvider.UpdateFile updateFile = new UpdateProvider.UpdateFile(new URL("http://www.test.download.link"), "DepFile", new Version("1.0"), "", "", "", "");
		doReturn(new UpdateProvider.UpdateFile[] { updateFile }).when(mockedUpdateProvider).getLatestDependencies();
		Field downloadDependencies = TestUtils.setAccessible(Updater.class, updater, "downloadDependencies", true);
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				result.set(updater, UpdateResult.SUCCESS);
				return null;
			}
		}).when(updater).download(any(URL.class), anyString());
		updater.update(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result)
			{
				updateResponses[0]++;
				assertEquals("The update result should be correct", UpdateResult.SUCCESS, result);
			}
		});
		shouldHaveUpdateResponses++;
		doReturn("").when(mockedUpdateProvider).getLatestFileName();
		doAnswer(new Answer()
		{
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				result.set(updater, UpdateResult.SUCCESS_DEPENDENCY_DOWNLOAD_FAILED);
				return null;
			}
		}).when(updater).download(updateFile.getDownloadURL(), updateFile.getFileName());
		updater.update(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result)
			{
				updateResponses[0]++;
				assertEquals("The update result should be correct", UpdateResult.SUCCESS_DEPENDENCY_DOWNLOAD_FAILED, result);
			}
		});
		shouldHaveUpdateResponses++;
		doReturn(null).when(mockedUpdateProvider).getLatestDependencies();
		updater.update(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result)
			{
				updateResponses[0]++;
				assertEquals("The update result should be correct", UpdateResult.SUCCESS, result);
			}
		});
		shouldHaveUpdateResponses++;
		doReturn(false).when(mockedUpdateProvider).providesDependencies();
		updater.update(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result)
			{
				updateResponses[0]++;
				assertEquals("The update result should be correct", UpdateResult.SUCCESS, result);
			}
		});
		shouldHaveUpdateResponses++;
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				result.set(updater, UpdateResult.FAIL_DOWNLOAD);
				return null;
			}
		}).when(updater).download(any(URL.class), anyString());
		updater.update(new Updater.UpdaterResponse()
		{
			@Override
			public void onDone(UpdateResult result)
			{
				updateResponses[0]++;
				assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result);
			}
		});
		updater.update();
		TestUtils.setUnaccessible(result, updater, true);
		TestUtils.setUnaccessible(downloadDependencies, updater, true);
		TestUtils.setUnaccessible(updateProvider, updater, true);
		Thread.sleep(100);
		assertEquals("The onDone method should be called as often as given", ++shouldHaveUpdateResponses, updateResponses[0]);
	}

	@Test
	public void testUnzip() throws Exception
	{
		File file = new File("ZIP-Archive.zip");
		Updater updater = getUpdater("1.0");
		updater.unzip(new File("Not-Found.zip"));
		URL zipArchive = Updater.class.getResource("/ZIP-Archive.zip");
		setFile(zipArchive, file);
		//noinspection ResultOfMethodCallIgnored
		updater.unzip(file);
		File jarFile = new File("plugins/updates/Test-JAR.jar");
		assertTrue("The jar file should be unzipped", jarFile.exists());
		assertFalse("The txt file shouldn't be unzipped", new File("plugins/updates/Test-TXT.txt").exists());
		assertFalse("The given jar file should not be found as plugin", updater.isPluginFile("NotFound.jar"));
		File pluginFile = new File("plugins/Test-JAR.jar");
		Files.copy(jarFile, pluginFile);
		//noinspection ResultOfMethodCallIgnored
		jarFile.delete();
		assertTrue("The given jar file should be found as plugin", updater.isPluginFile("Test-JAR.jar"));
		//noinspection ResultOfMethodCallIgnored
		pluginFile.delete();
		final boolean[] hasMore = { false };
		ZipFile mockedZipFile = mock(ZipFile.class);
		ZipEntry mockedZipEntry = mock(ZipEntry.class);
		doReturn("Test-JAR.jar").when(mockedZipEntry).getName();
		final Enumeration mockedEnumeration = mock(Enumeration.class);
		doAnswer(new Answer()
		{
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock)
			{
				hasMore[0] = !hasMore[0];
				return hasMore[0];
			}
		}).when(mockedEnumeration).hasMoreElements();
		doReturn(mockedZipEntry).when(mockedEnumeration).nextElement();
		doAnswer(new Answer()
		{
			@Override
			public Enumeration answer(InvocationOnMock invocationOnMock)
			{
				hasMore[0] = false;
				return mockedEnumeration;
			}
		}).when(mockedZipFile).entries();
		whenNew(ZipFile.class).withAnyArguments().thenReturn(mockedZipFile);
		mockStatic(ByteStreams.class);
		PowerMockito.doAnswer(new Answer<Long>()
		{
			@Override
			public Long answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return (long) ((InputStream) invocationOnMock.getArguments()[0]).available();
			}
		}).when(ByteStreams.class, "copy", any(BufferedInputStream.class), any(BufferedOutputStream.class));
		updater.unzip(file);
		BufferedOutputStream mockedOutputStream = mock(BufferedOutputStream.class);
		doThrow(new IOException()).when(mockedOutputStream).flush();
		whenNew(BufferedOutputStream.class).withAnyArguments().thenReturn(mockedOutputStream);
		updater.unzip(file);
		PowerMockito.doThrow(new IOException()).when(ByteStreams.class, "copy", any(BufferedInputStream.class), any(BufferedOutputStream.class));
		updater.unzip(file);
		whenNew(FileOutputStream.class).withParameterTypes(File.class).withArguments(any(File.class)).thenThrow(new FileNotFoundException());
		updater.unzip(file);
		MockRepository.remove(FileOutputStream.class);
		whenNew(FileOutputStream.class).withParameterTypes(File.class).withArguments(any(File.class)).thenThrow(new SecurityException());
		Exception exception = null;
		try
		{
			updater.unzip(file);
		}
		catch(Exception e)
		{
			exception = e;
		}
		assertNotNull("An exception should be thrown", exception);
		assertEquals("The exception should be the correct one", SecurityException.class, exception.getClass());
		MockRepository.remove(FileOutputStream.class);
		whenNew(BufferedOutputStream.class).withAnyArguments().thenThrow(new IllegalArgumentException());
		exception = null;
		try
		{
			updater.unzip(file);
		}
		catch(Exception e)
		{
			exception = e;
		}
		assertNotNull("An exception should be thrown", exception);
		assertEquals("The exception should be the correct one", IllegalArgumentException.class, exception.getClass());
		doThrow(new IllegalStateException()).when(mockedZipFile).getInputStream(any(ZipEntry.class));
		exception = null;
		try
		{
			updater.unzip(file);
		}
		catch(Exception e)
		{
			exception = e;
		}
		assertNotNull("An exception should be thrown", exception);
		assertEquals("The exception should be the correct one", IllegalStateException.class, exception.getClass());
		doThrow(new ZipException()).when(mockedZipFile).getInputStream(any(ZipEntry.class));
		updater.unzip(file);
		doThrow(new IOException()).when(mockedZipFile).close();
		updater.unzip(file);
		whenNew(ZipFile.class).withAnyArguments().thenReturn(null);
		exception = null;
		try
		{
			updater.unzip(file);
		}
		catch(Exception e)
		{
			exception = e;
		}
		assertNotNull("An exception should be thrown", exception);
		assertEquals("The exception should be the correct one", NullPointerException.class, exception.getClass());
		//noinspection ResultOfMethodCallIgnored
		file.delete();
		//noinspection ResultOfMethodCallIgnored
		File testJAR = new File("plugins/updates/Test-JAR.jar");
		//noinspection ResultOfMethodCallIgnored
		testJAR.delete();
	}

	@Test
	public void testWithGravityUpdaterConfig() throws IOException, NoSuchFieldException, IllegalAccessException
	{
		URL config = Updater.class.getResource("/gravityUpdaterConfig.yml");
		if(config != null)
		{
			//noinspection deprecation
			Files.copy(new File(URLDecoder.decode(config.getPath())), new File("plugins/Updater/config.yml"));
		}
		Field result = Updater.class.getDeclaredField("result");
		result.setAccessible(true);
		Updater updater = getUpdater("1.0");
		assertEquals("The update function should be disabled", UpdateResult.DISABLED, result.get(updater));
		updater.checkForUpdate(null);
		updater.update();
		config = Updater.class.getResource("/gravityUpdaterConfig2.yml");
		if(config != null)
		{
			//noinspection deprecation
			Files.copy(new File(URLDecoder.decode(config.getPath())), new File("plugins/Updater/config.yml"));
		}
		updater = getUpdater("1.0");
		assertNotEquals("The update function should be enabled", UpdateResult.DISABLED, result.get(updater));
		result.setAccessible(false);
		//noinspection ResultOfMethodCallIgnored
		new File("plugins/updater/config.yml").delete();
	}

	@Test
	public void testGetVersion() throws NoSuchFieldException, IllegalAccessException, NotSuccessfullyQueriedException
	{
		Updater updater = getUpdater("1.0");
		UpdateProvider mockedUpdateProvider = mock(UpdateProvider.class);
		doThrow(new NotSuccessfullyQueriedException()).when(mockedUpdateProvider).getLatestVersion();
		Field updateProvider = TestUtils.setAccessible(Updater.class, updater, "updateProvider", mockedUpdateProvider);
		assertNull("The version result should be null", updater.getRemoteVersion());
		TestUtils.setUnaccessible(updateProvider, updater, true);
	}

	@Test
	public void testVersionCheck()
	{
		assertFalse("The version check should return false", getUpdater("1.0").versionCheck(null));
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test
	public void testDownload() throws Exception
	{
		Updater updater = getUpdater("1.0");
		Method download = Updater.class.getDeclaredMethod("download", URL.class, String.class, int.class);
		Field result = TestUtils.setAccessible(Updater.class, updater, "result", UpdateResult.NO_UPDATE);
		URL mockedURL = PowerMockito.mock(URL.class);
		HttpURLConnection mockedConnection = mock(HttpURLConnection.class);
		doReturn(HttpURLConnection.HTTP_MOVED_PERM).when(mockedConnection).getResponseCode();
		PowerMockito.doReturn(mockedConnection).when(mockedURL).openConnection();
		InputStream mockedInputStream = mock(InputStream.class);
		//noinspection ResultOfMethodCallIgnored
		PowerMockito.doReturn(3).doReturn(0).when(mockedInputStream).available();
		//noinspection ResultOfMethodCallIgnored
		PowerMockito.doReturn(3).doReturn(0).when(mockedInputStream).read(any(byte[].class), anyInt(), anyInt());
		PowerMockito.doReturn(mockedInputStream).when(mockedURL).openStream();
		download.invoke(updater, mockedURL, "Test-JAR.jar", 5);
		assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
		result.set(updater, UpdateResult.NO_UPDATE);
		PowerMockito.doReturn(3L).when(mockedConnection).getContentLengthLong();
		download.invoke(updater, mockedURL, "Test-JAR.jar", 5);
		assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
		doReturn(HttpURLConnection.HTTP_OK).when(mockedConnection).getResponseCode();
		Field announceDownload = TestUtils.setAccessible(Updater.class, updater, "announceDownloadProgress", false);
		UpdateProvider mockedUpdateProvider = mock(UpdateProvider.class);
		doReturn(ChecksumType.NONE).when(mockedUpdateProvider).providesChecksum();
		Field updateProvider = TestUtils.setAccessible(Updater.class, updater, "updateProvider", mockedUpdateProvider);
		result.set(updater, UpdateResult.NO_UPDATE);
		download.invoke(updater, mockedURL, "Test-Download.zip", 0);
		assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
		File mockedUpdateFolder = spy(new File("plugins/updater"));
		//noinspection ResultOfMethodCallIgnored
		doReturn(false).when(mockedUpdateFolder).exists();
		Field updateFolder = TestUtils.setAccessible(Updater.class, updater, "updateFolder", mockedUpdateFolder);
		result.set(updater, UpdateResult.SUCCESS);
		download.invoke(updater, mockedURL, "Test-JAR.jar", 0);
		assertEquals("The update result should be correct", UpdateResult.SUCCESS, result.get(updater));
		mockStatic(Utils.class);
		PowerMockito.doReturn("abc").when(Utils.class, "byteArrayToHex", (Object) any(byte[].class));
		doReturn(ChecksumType.MD5).when(mockedUpdateProvider).providesChecksum();
		result.set(updater, UpdateResult.NO_UPDATE);
		download.invoke(updater, mockedURL, "Test-Download.zip", 0);
		assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
		File mockedFile = spy(new File(System.getProperty("user.dir"), "Test-ZIP.zip"));
		doReturn(false).when(mockedFile).delete();
		whenNew(File.class).withAnyArguments().thenReturn(mockedFile);
		download.invoke(updater, mockedURL, "Test-ZIP.zip", 0);
		assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
		PowerMockito.doReturn(mockedInputStream).when(mockedConnection).getInputStream();
		doReturn("123").when(mockedUpdateProvider).getLatestChecksum();
		result.set(updater, UpdateResult.NO_UPDATE);
		download.invoke(updater, mockedURL, "Test-Download.zip", 0);
		assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
		doThrow(new RequestTypeNotAvailableException("")).when(mockedUpdateProvider).getLatestChecksum();
		result.set(updater, UpdateResult.NO_UPDATE);
		download.invoke(updater, mockedURL, "Test-Download.zip", 0);
		assertEquals("The update result should be correct", UpdateResult.NO_UPDATE, result.get(updater));
		doThrow(new NotSuccessfullyQueriedException()).when(mockedUpdateProvider).getLatestChecksum();
		result.set(updater, UpdateResult.NO_UPDATE);
		download.invoke(updater, mockedURL, "Test-Download.zip", 0);
		assertEquals("The update result should be correct", UpdateResult.FAIL_NO_VERSION_FOUND, result.get(updater));
		TestUtils.setUnaccessible(updateFolder, updater, true);
		TestUtils.setUnaccessible(announceDownload, updater, true);
		TestUtils.setUnaccessible(updateProvider, updater, true);
		TestUtils.setUnaccessible(result, updater, true);
	}

	@AfterClass
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void cleanupTestData()
	{
		new File("plugins/updates/Test-Download.zip").delete();
		new File("plugins/updates/Test-JAR.jar").delete();
		new File("plugins/Updater/Test-JAR.jar").delete();
		new File("plugins/updater").delete();
		new File("plugins/updates").delete();
		new File("plugins").delete();
	}
}