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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks;

import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YAMLKeyNotFoundException;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ByteStreams.class, YamlFileUpdateMethod.class, YAML.class })
public class ConfigurationTest
{
	private static Logger mockedLogger;

	@SuppressWarnings("SpellCheckingInspection")
	private static int loggedInfos = 0;

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		setConfigFile();
		TestUtils.initReflection();
	}

	@Before
	public void prepareTestClass()
	{
		mockedLogger = mock(Logger.class);
		doAnswer(new Answer<Void>()
		{
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				loggedInfos++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
	}

	private static void setConfigFile()
	{
		File targetFile = new File("config.yml");
		if(targetFile.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			targetFile.delete();
		}
		URL resource = UUIDConverterTest.class.getResource("/config.yml");
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
	public void testExtendedConfiguration() throws YAMLKeyNotFoundException
	{
		Configuration oldConfiguration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1, "config.yml");
		Configuration upgradeThresholdConfig = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1, 1);
		assertEquals("The configuration with the current version as upgrade threshold should return the same configuration", oldConfiguration.getConfig().getString("Version"), upgradeThresholdConfig.getConfig().getString("Version"));
	}

	@Test
	public void testSaveConfig() throws FileNotFoundException, YAMLKeyNotFoundException
	{
		Configuration configuration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1);
		configuration.set("NewlySavedValue", true);
		configuration.save();
		configuration.reload();
		assertEquals("The saved value should be in the reloaded config", true, configuration.getBool("NewlySavedValue"));
	}

	@Test
	public void testUpdate()
	{
		Configuration configuration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1);
		int currentLoggedInfo = loggedInfos;
		configuration.doUpdate();
		assertEquals("The log count should be one more than before because the update function has been called", currentLoggedInfo + 1, loggedInfos);
		assertFalse("No new configuration has been created, therefore false should be returned", configuration.newConfigCreated());
	}

	@Test
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@PrepareForTest({ ByteStreams.class, Configuration.class })
	public void testSaveError() throws Exception
	{
		final int[] loggerObject = { 0 };
		mockStatic(ByteStreams.class);
		when(ByteStreams.copy(any(FileInputStream.class), any(FileOutputStream.class))).thenReturn((long) 0);
		Logger mockedLogger = mock(Logger.class);
		YAML mockedYAML = mock(YAML.class);
		whenNew(YAML.class).withArguments(File.class).thenReturn(mockedYAML);
		Configuration configuration = spy(new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1, 10, "NOT_HERE.cfg"));
		new File("NOT_HERE.cfg").delete();
		doReturn(true).when(configuration).newConfigCreated();
		doThrow(new FileNotFoundException()).when(configuration).save();
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				loggerObject[0]++;
				return null;
			}
		}).when(mockedLogger).warning(anyString());
		configuration.reload();
		assertEquals("The error should be logged", 1, loggerObject[0]);
	}

	@AfterClass
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void cleanupTestData()
	{
		new File("config.yml").delete();
		new File("NotFound.yml").delete();
		new File("NOT_HERE.cfg").delete();
		new File("config.yml.old_v1").delete();
		new File("config.yml.old_v3").delete();
		new File("config.yml.old_v5").delete();
		new File("config.yml.old_v7").delete();
	}
}