/*
 * Copyright (C) 2016, 2018 MarkusWME
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks;

import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ByteStreams.class, YamlFileManager.class, YamlFileUpdateMethod.class, YAML.class })
public class ConfigurationTest
{
	private static Logger mockedLogger;
	private static File tmpDir;

	private static int loggedInfos = 0;

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		tmpDir = Files.createTempDir();
		tmpDir.deleteOnExit();
		setConfigFile();
		TestUtils.initReflection();
	}

	@Before
	public void prepareTestClass()
	{
		mockedLogger = mock(Logger.class);
		doAnswer((Answer<Void>) invocationOnMock -> {
			loggedInfos++;
			return null;
		}).when(mockedLogger).info(anyString());
	}

	private static void setConfigFile()
	{
		File targetFile = new File(tmpDir, "config.yml");
		if(targetFile.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			targetFile.delete();
		}
		URL resource = ConfigurationTest.class.getResource("/config.yml");
		if(resource != null)
		{
			try
			{
				//noinspection deprecation
				Files.copy(new File(URLDecoder.decode(resource.getFile())), targetFile);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testExtendedConfiguration() throws YamlKeyNotFoundException
	{
		Configuration oldConfiguration = new Configuration(this, mockedLogger, tmpDir, new Version(1), "config.yml");
		Configuration upgradeThresholdConfig = new Configuration(this, mockedLogger, tmpDir, new Version(1), new Version(1));
		assertEquals("The configuration with the current version as upgrade threshold should return the same configuration", oldConfiguration.getConfig().getString("Version"), upgradeThresholdConfig.getConfig().getString("Version"));
	}

	@Test
	public void testSaveConfig() throws FileNotFoundException, YamlKeyNotFoundException
	{
		Configuration configuration = new Configuration(this, mockedLogger, tmpDir, new Version(1));
		configuration.set("NewlySavedValue", true);
		configuration.save();
		configuration.reload();
		assertTrue("The saved value should be in the reloaded config", configuration.getBool("NewlySavedValue"));
	}

	@Test
	public void testUpdate()
	{
		Configuration configuration = new Configuration(this, mockedLogger, tmpDir, new Version(1));
		int currentLoggedInfo = loggedInfos;
		configuration.doUpdate();
		assertEquals("The log count should be one more than before because the update function has been called", currentLoggedInfo + 1, loggedInfos);
		assertFalse("No new configuration has been created, therefore false should be returned", configuration.newConfigCreated());
	}

	@Test
	public void testGetter() throws IllegalAccessException, NoSuchFieldException, YamlKeyNotFoundException
	{
		Configuration configuration = new Configuration(this, mockedLogger, tmpDir, new Version(1));
		YAML mockedYAML = mock(YAML.class);
		Field yamlField = TestUtils.setAccessible(YamlFileManager.class, configuration, "yaml", mockedYAML);
		doReturn(123).when(mockedYAML).getInt(anyString());
		assertEquals("The int should match", 123, configuration.getInt("Test"));
		doReturn(234).when(mockedYAML).getInt(anyString(), anyInt());
		assertEquals("The int should match", 234, configuration.getInt("Test", 123));
		doReturn(123.123).when(mockedYAML).getDouble(anyString());
		assertEquals("The double should match", 123.123, configuration.getDouble("Test"), 0.1);
		doReturn(234.234).when(mockedYAML).getDouble(anyString(), anyDouble());
		assertEquals("The double should match", 234.234, configuration.getDouble("Test", 123.123), 0.1);
		doReturn("Hallo Welt!").when(mockedYAML).getString(anyString());
		assertEquals("The string should match", "Hallo Welt!", configuration.getString("Test"));
		doReturn("Hello World!").when(mockedYAML).getString(anyString(), anyString());
		assertEquals("The string should match", "Hello World!", configuration.getString("Test", "Hallo Welt!"));
		doReturn(false).when(mockedYAML).getBoolean(anyString());
		assertFalse("The boolean should match", configuration.getBool("Test"));
		doReturn(true).when(mockedYAML).getBoolean(anyString(), anyBoolean());
		assertTrue("The boolean should match", configuration.getBool("Test", false));
		TestUtils.setUnaccessible(yamlField, configuration, false);
	}

	@Test
	public void testSetter() throws NoSuchFieldException, IllegalAccessException
	{
		Configuration configuration = new Configuration(this, mockedLogger, tmpDir, new Version(1));
		YAML mockedYAML = mock(YAML.class);
		final int[] count = { 0 };
		doAnswer(invocationOnMock -> {
			count[0]++;
			return null;
		}).when(mockedYAML).set(anyString(), any());
		Field yamlField = TestUtils.setAccessible(YamlFileManager.class, configuration, "yaml", mockedYAML);
		configuration.set("Test", "Test");
		configuration.set("Test", 1);
		configuration.set("Test", 1.1);
		configuration.set("Test", false);
		assertEquals("The setter should have been called 4 times", 4, count[0]);
		TestUtils.setUnaccessible(yamlField, configuration, false);
	}
}