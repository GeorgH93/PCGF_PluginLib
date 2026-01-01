/*
 *   Copyright (C) 2020 GeorgH93
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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
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
import org.junit.*;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.*;
import java.lang.reflect.Field;
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
import static org.mockito.Mockito.*;

@SuppressWarnings("UnstableApiUsage")
public class UpdaterTest
{
	private static final Logger LOGGER = Logger.getLogger(UpdaterTest.class.getName());
	private static final File PLUGINS_FOLDER, TARGET_FILE;

	static
	{
		PLUGINS_FOLDER = Files.createTempDir();
		PLUGINS_FOLDER.deleteOnExit();
		TARGET_FILE = new File(PLUGINS_FOLDER, "updates" + File.separator + "MM.jar");
	}

	private static UpdateProvider bukkitProvider;

	public static class TestableUpdater extends Updater
	{
		public TestableUpdater(File pluginsFolder, boolean enabled, boolean downloadDependencies, Logger logger, UpdateProvider updateProvider, String localVersion, String localFileName)
		{
			super(pluginsFolder, enabled, downloadDependencies, logger, updateProvider, localVersion, localFileName);
		}

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

		@Override
		public boolean isRunning()
		{
			return false;
		}
	}

	private Updater getUpdater(String version)
	{
		return getUpdater(version, bukkitProvider);
	}

	private Updater getUpdater(String version, UpdateProvider provider)
	{
		return new TestableUpdater(PLUGINS_FOLDER, false, false, LOGGER, provider, version, TARGET_FILE.getName());
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
		bukkitProvider = new BukkitUpdateProvider(74734, LOGGER); // 74734 is the Bukkit id of Marriage Master
		//noinspection ResultOfMethodCallIgnored
		new File(PLUGINS_FOLDER, "updates").mkdirs();
		//noinspection ResultOfMethodCallIgnored
		new File(PLUGINS_FOLDER, "Updater").mkdirs();
		TestUtils.initReflection();
	}

	@Test
	public void testBukkitUpdateProviderProperties()
	{
		assertFalse(bukkitProvider.providesChangelog());
		assertFalse(bukkitProvider.providesDependencies());
		assertTrue(bukkitProvider.providesDownloadURL());
		assertTrue(bukkitProvider.providesMinecraftVersion());
		assertTrue(bukkitProvider.providesUpdateHistory());
	}

	@Test
	public void testBukkitUpdateProvider()
	{
		assertEquals("The result of the query should be \"SUCCESS\".", UpdateResult.SUCCESS, bukkitProvider.query());
	}

	@Test
	public void testUpdateCheck() throws NoSuchFieldException, IllegalAccessException
	{
		IUpdater updater = getUpdater("1.0");
		updater.checkForUpdate(result2 -> assertEquals(UpdateResult.UPDATE_AVAILABLE, result2));

		UpdateProvider mockedUpdateProvider = mock(UpdateProvider.class);
		doReturn(UpdateResult.DISABLED).when(mockedUpdateProvider).query();
		updater = getUpdater("1.0", mockedUpdateProvider);
		Field updateProvider = TestUtils.setAccessible(Updater.class, updater, "updateProvider", mockedUpdateProvider);
		updater.checkForUpdate(result2 -> assertEquals(UpdateResult.DISABLED, result2));
		TestUtils.setUnaccessible(updateProvider, updater, true);
	}

	@Test
	public void testUpdateCheckNoUpdateAvailable()
	{
		IUpdater updater = getUpdater("99.0");
		updater.checkForUpdate(result2 -> assertEquals(UpdateResult.NO_UPDATE, result2));
	}

	@Test
	public void testUpdateDownload()
	{
		IUpdater updater = getUpdater("1.0");
		//noinspection ResultOfMethodCallIgnored
		new File(PLUGINS_FOLDER, "updates").delete();
		updater.update(result2 -> {
			assertEquals("The update result should be correct", UpdateResult.SUCCESS, result2);
			assertTrue("The target file should exist", TARGET_FILE.exists());
		});
	}

	@Test
	public void testUpdateDownloadNoUpdateAvailable()
	{
		IUpdater updater = getUpdater("99.0");
		updater.update(result2 -> {
			assertEquals("The update result should be correct", UpdateResult.NO_UPDATE, result2);
			assertFalse("The target file should not exist", TARGET_FILE.exists());
		});
	}

	@Test
	public void testUpdate() throws NoSuchFieldException, IllegalAccessException, RequestTypeNotAvailableException, NotSuccessfullyQueriedException, MalformedURLException
	{
		org.junit.Assume.assumeTrue("Skip on Java 16+ - spy() doesn't work on TestableUpdater", 
			System.getProperty("java.specification.version").compareTo("16") < 0);
		int shouldHaveUpdateResponses = 0;
		final int[] updateResponses = { 0 };
		final UpdateResponseCallback updaterResponse = result -> updateResponses[0]++;
		UpdateProvider mockedUpdateProvider = mock(UpdateProvider.class);
		doReturn(UpdateResult.DISABLED).when(mockedUpdateProvider).query();
		final Updater updater = spy(getUpdater("1.0", mockedUpdateProvider));
		updater.update(updaterResponse);
		assertEquals("There should be an update response", ++shouldHaveUpdateResponses, updateResponses[0]);
		final Field result = TestUtils.setAccessible(Updater.class, updater, "result", UpdateResult.NO_UPDATE);
		doReturn(UpdateResult.SUCCESS).when(mockedUpdateProvider).query();
		doReturn(false).when(mockedUpdateProvider).providesDownloadURL();
		doReturn(null).when(updater).getRemoteVersion();
		doReturn(true).when(updater).versionCheck(any());
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
		doReturn(new URL("https://www.test.xyz")).when(mockedUpdateProvider).getLatestFileURL();
		doReturn(true).when(mockedUpdateProvider).providesDependencies();
		UpdateProvider.UpdateFile updateFile = new UpdateProvider.UpdateFile(new URL("https://www.test.download.link"), "DepFile", new Version("1.0"), "", "", "", "");
		doReturn(new UpdateProvider.UpdateFile[] { updateFile }).when(mockedUpdateProvider).getLatestDependencies();
		Field downloadDependencies = TestUtils.setAccessible(Updater.class, updater, "downloadDependencies", true);
		doAnswer(invocationOnMock -> {
			result.set(updater, UpdateResult.SUCCESS);
			return null;
		}).when(updater).download(any(URL.class), anyString());
		updater.update(result15 -> {
			updateResponses[0]++;
			assertEquals("The update result should be correct", UpdateResult.SUCCESS, result15);
		});
		shouldHaveUpdateResponses++;
		doReturn("").when(mockedUpdateProvider).getLatestFileName();
		doAnswer(invocationOnMock -> {
			result.set(updater, UpdateResult.FAIL_FILE_NOT_FOUND);
			return null;
		}).when(updater).download(updateFile.getDownloadURL(), updateFile.getFileName());
		updater.update(result1 -> {
			updateResponses[0]++;
			assertEquals("The update result should be correct", UpdateResult.SUCCESS_DEPENDENCY_DOWNLOAD_FAILED, result1);
		});
		shouldHaveUpdateResponses++;
		doReturn(null).when(mockedUpdateProvider).getLatestDependencies();
		updater.update(result12 -> {
			updateResponses[0]++;
			assertEquals("The update result should be correct", UpdateResult.SUCCESS, result12);
		});
		shouldHaveUpdateResponses++;
		doReturn(false).when(mockedUpdateProvider).providesDependencies();
		updater.update(result14 -> {
			updateResponses[0]++;
			assertEquals("The update result should be correct", UpdateResult.SUCCESS, result14);
		});
		shouldHaveUpdateResponses++;
		doAnswer(invocationOnMock -> {
			result.set(updater, UpdateResult.FAIL_DOWNLOAD);
			return null;
		}).when(updater).download(any(URL.class), anyString());
		updater.update(result13 -> {
			updateResponses[0]++;
			assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result13);
		});
		updater.update();
		TestUtils.setUnaccessible(result, updater, true);
		TestUtils.setUnaccessible(downloadDependencies, updater, true);
		assertEquals("The onDone method should be called as often as given", ++shouldHaveUpdateResponses, updateResponses[0]);
	}

	@Test
	public void testUnzip() throws Exception
	{
		Assume.assumeTrue("Skip if mockito-inline not available", TestUtils.canMockJdkClasses());
		File file = new File("ZIP-Archive.zip");
		Updater updater = getUpdater("1.0");
		updater.unzip(new File("Not-Found.zip"));
		URL zipArchive = Updater.class.getResource("/ZIP-Archive.zip");
		setFile(zipArchive, file);
		updater.unzip(file);
		File jarFile = new File(PLUGINS_FOLDER, "updates/Test-JAR.jar");
		assertTrue("The jar file should be unzipped", jarFile.exists());
		assertFalse("The txt file shouldn't be unzipped", new File(PLUGINS_FOLDER, "updates/Test-TXT.txt").exists());
		assertFalse("The given jar file should not be found as plugin", updater.isPluginFile("NotFound.jar"));
		File pluginFile = new File(PLUGINS_FOLDER, "Test-JAR.jar");
		Files.copy(jarFile, pluginFile);
		//noinspection ResultOfMethodCallIgnored
		jarFile.delete();
		assertTrue("The given jar file should be found as plugin", updater.isPluginFile("Test-JAR.jar"));
		//noinspection ResultOfMethodCallIgnored
		pluginFile.delete();
		final boolean[] hasMore = { false };
		ZipEntry mockedZipEntry = mock(ZipEntry.class);
		doReturn("Test-JAR.jar").when(mockedZipEntry).getName();
		final Enumeration<?> mockedEnumeration = mock(Enumeration.class);
		doAnswer(invocationOnMock -> {
			hasMore[0] = !hasMore[0];
			return hasMore[0];
		}).when(mockedEnumeration).hasMoreElements();
		doReturn(mockedZipEntry).when(mockedEnumeration).nextElement();

		try (MockedStatic<ByteStreams> mockedByteStreams = Mockito.mockStatic(ByteStreams.class);
			 MockedConstruction<ZipFile> mcZipFile = Mockito.mockConstruction(ZipFile.class, (mock, context) -> {
				 doAnswer(invocationOnMock -> {
					 hasMore[0] = false;
					 return mockedEnumeration;
				 }).when(mock).entries();
			 }))
		{
			ZipFile testZipFile = mcZipFile.constructed().isEmpty() ? null : mcZipFile.constructed().get(0);
			if (testZipFile != null)
			{
				doAnswer(invocationOnMock -> {
					hasMore[0] = false;
					return mockedEnumeration;
				}).when(testZipFile).entries();
			}
			mockedByteStreams.when(() -> ByteStreams.copy(any(BufferedInputStream.class), any(BufferedOutputStream.class)))
				.thenAnswer((Answer<Long>) invocationOnMock -> (long) ((InputStream) invocationOnMock.getArguments()[0]).available());
			updater.unzip(file);
		}

		try (MockedStatic<ByteStreams> mockedByteStreams = Mockito.mockStatic(ByteStreams.class);
			 MockedConstruction<ZipFile> mcZipFile = Mockito.mockConstruction(ZipFile.class, (mock, context) -> {
				 doAnswer(invocationOnMock -> {
					 hasMore[0] = false;
					 return mockedEnumeration;
				 }).when(mock).entries();
			 });
			 MockedConstruction<BufferedOutputStream> mcBufferedOutputStream = Mockito.mockConstruction(BufferedOutputStream.class, (mock, context) -> {
				 doThrow(new IOException()).when(mock).flush();
			 }))
		{
			mockedByteStreams.when(() -> ByteStreams.copy(any(BufferedInputStream.class), any(BufferedOutputStream.class)))
				.thenAnswer((Answer<Long>) invocationOnMock -> (long) ((InputStream) invocationOnMock.getArguments()[0]).available());
			updater.unzip(file);
		}

		try (MockedStatic<ByteStreams> mockedByteStreams = Mockito.mockStatic(ByteStreams.class);
			 MockedConstruction<ZipFile> mcZipFile = Mockito.mockConstruction(ZipFile.class, (mock, context) -> {
				 doAnswer(invocationOnMock -> {
					 hasMore[0] = false;
					 return mockedEnumeration;
				 }).when(mock).entries();
			 }))
		{
			mockedByteStreams.when(() -> ByteStreams.copy(any(BufferedInputStream.class), any(BufferedOutputStream.class)))
				.thenThrow(new IOException());
			updater.unzip(file);
		}

		try (MockedConstruction<FileOutputStream> mcFileOutputStream = Mockito.mockConstruction(FileOutputStream.class, (mock, context) -> {
			throw new FileNotFoundException();
		}))
		{
			updater.unzip(file);
		}

		try (MockedConstruction<FileOutputStream> mcFileOutputStream = Mockito.mockConstruction(FileOutputStream.class, (mock, context) -> {
			throw new SecurityException();
		}))
		{
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
		}

		try (MockedConstruction<BufferedOutputStream> mcBufferedOutputStream = Mockito.mockConstruction(BufferedOutputStream.class, (mock, context) -> {
			throw new IllegalArgumentException();
		}))
		{
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
			assertEquals("The exception should be the correct one", IllegalArgumentException.class, exception.getClass());
		}

		try (MockedConstruction<ZipFile> mcZipFile = Mockito.mockConstruction(ZipFile.class, (mock, context) -> {
			doThrow(new IllegalStateException()).when(mock).getInputStream(any(ZipEntry.class));
		}))
		{
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
			assertEquals("The exception should be the correct one", IllegalStateException.class, exception.getClass());
		}

		try (MockedConstruction<ZipFile> mcZipFile = Mockito.mockConstruction(ZipFile.class, (mock, context) -> {
			doThrow(new ZipException()).when(mock).getInputStream(any(ZipEntry.class));
		}))
		{
			updater.unzip(file);
		}

		try (MockedConstruction<ZipFile> mcZipFile = Mockito.mockConstruction(ZipFile.class, (mock, context) -> {
			doThrow(new IOException()).when(mock).close();
		}))
		{
			updater.unzip(file);
		}

		// Mockito 4.x cannot make mockConstruction return null, skipping null ZipFile test case

		//noinspection ResultOfMethodCallIgnored
		file.delete();
		File testJAR = new File(PLUGINS_FOLDER, "updates/Test-JAR.jar");
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
			Files.copy(new File(URLDecoder.decode(config.getPath())), new File(PLUGINS_FOLDER, "Updater/config.yml"));
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
			Files.copy(new File(URLDecoder.decode(config.getPath())), new File(PLUGINS_FOLDER, "Updater/config.yml"));
		}
		updater = getUpdater("1.0");
		assertNotEquals("The update function should be enabled", UpdateResult.DISABLED, result.get(updater));
		result.setAccessible(false);
		//noinspection ResultOfMethodCallIgnored
		new File(PLUGINS_FOLDER, "updater/config.yml").delete();
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

	@Ignore("brocken")
	@Test
	public void testDownload() throws Exception
	{
		Updater updater = getUpdater("1.0");
		Field result = TestUtils.setAccessible(Updater.class, updater, "result", UpdateResult.NO_UPDATE);
		URL mockedURL = Mockito.mock(URL.class);
		HttpURLConnection mockedConnection = mock(HttpURLConnection.class);
		doReturn(HttpURLConnection.HTTP_MOVED_PERM).when(mockedConnection).getResponseCode();
		doReturn(mockedConnection).when(mockedURL).openConnection();
		InputStream mockedInputStream = mock(InputStream.class);
		doReturn(3).doReturn(0).when(mockedInputStream).available();
		doReturn(3).doReturn(0).when(mockedInputStream).read(any(byte[].class), anyInt(), anyInt());
		doReturn(mockedInputStream).when(mockedURL).openStream();
		updater.download(mockedURL, "Test-JAR.jar");
		assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
		result.set(updater, UpdateResult.NO_UPDATE);
		doReturn(3L).when(mockedConnection).getContentLengthLong();
		updater.download(mockedURL, "Test-JAR.jar");
		assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
		doReturn(HttpURLConnection.HTTP_OK).when(mockedConnection).getResponseCode();
		Field announceDownload = TestUtils.setAccessible(Updater.class, updater, "announceDownloadProgress", false);
		UpdateProvider mockedUpdateProvider = mock(UpdateProvider.class);
		doReturn(ChecksumType.NONE).when(mockedUpdateProvider).providesChecksum();
		Field updateProviderField = TestUtils.setAccessible(Updater.class, updater, "updateProvider", mockedUpdateProvider);
		result.set(updater, UpdateResult.NO_UPDATE);
		updater.download(mockedURL, "Test-Download.zip");
		assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
		File mockedUpdateFolder = spy(new File(PLUGINS_FOLDER, "updater"));
		doReturn(false).when(mockedUpdateFolder).exists();
		Field updateFolder = TestUtils.setAccessible(Updater.class, updater, "updateFolder", mockedUpdateFolder);
		result.set(updater, UpdateResult.SUCCESS);
		updater.download(mockedURL, "Test-JAR.jar");
		assertEquals("The update result should be correct", UpdateResult.SUCCESS, result.get(updater));
		try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
			 MockedConstruction<File> mcFile = Mockito.mockConstruction(File.class, (mock, context) -> {
				 File tmpDir = Files.createTempDir();
				 tmpDir.deleteOnExit();
				 File spiedFile = spy(new File(tmpDir, "Test-ZIP.zip"));
				 doReturn(false).when(spiedFile).delete();
			 }))
		{
			//noinspection PrimitiveArrayArgumentToVarargsMethod
			mockedUtils.when(() -> Utils.byteArrayToHex(any(byte[].class))).thenReturn("abc");
			doReturn(ChecksumType.MD5).when(mockedUpdateProvider).providesChecksum();
			result.set(updater, UpdateResult.NO_UPDATE);
			updater.download(mockedURL, "Test-Download.zip");
			assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
			File tmpDir = Files.createTempDir();
			tmpDir.deleteOnExit();
			File mockedFile = spy(new File(tmpDir, "Test-ZIP.zip"));
			doReturn(false).when(mockedFile).delete();
			updater.download(mockedURL, "Test-ZIP.zip");
			assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
			doReturn(mockedInputStream).when(mockedConnection).getInputStream();
			doReturn("123").when(mockedUpdateProvider).getLatestChecksum();
			result.set(updater, UpdateResult.NO_UPDATE);
			updater.download(mockedURL, "Test-Download.zip");
			assertEquals("The update result should be correct", UpdateResult.FAIL_DOWNLOAD, result.get(updater));
			doThrow(new RequestTypeNotAvailableException("")).when(mockedUpdateProvider).getLatestChecksum();
			result.set(updater, UpdateResult.NO_UPDATE);
			updater.download(mockedURL, "Test-Download.zip");
			assertEquals("The update result should be correct", UpdateResult.NO_UPDATE, result.get(updater));
			doThrow(new NotSuccessfullyQueriedException()).when(mockedUpdateProvider).getLatestChecksum();
			result.set(updater, UpdateResult.NO_UPDATE);
			updater.download(mockedURL, "Test-Download.zip");
			assertEquals("The update result should be correct", UpdateResult.FAIL_NO_VERSION_FOUND, result.get(updater));
		}
		TestUtils.setUnaccessible(updateFolder, updater, true);
		TestUtils.setUnaccessible(announceDownload, updater, true);
		TestUtils.setUnaccessible(updateProviderField, updater, true);
		TestUtils.setUnaccessible(result, updater, true);
	}

	@After
	public void cleanupAfterTest()
	{
		if(TARGET_FILE.exists())
			//noinspection ResultOfMethodCallIgnored
			TARGET_FILE.delete();
	}

	@AfterClass
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void cleanupTestData()
	{
		new File(PLUGINS_FOLDER, "updates/Test-Download.zip").delete();
		new File(PLUGINS_FOLDER, "updates/Test-JAR.jar").delete();
		new File(PLUGINS_FOLDER, "Updater/Test-JAR.jar").delete();
		new File(PLUGINS_FOLDER, "updater").delete();
		new File(PLUGINS_FOLDER, "updates").delete();
	}
}