/*
 * Copyright (C) 2016 MarkusWME
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
import at.pcgamingfreaks.yaml.YAMLInvalidContentException;
import at.pcgamingfreaks.yaml.YAMLKeyNotFoundException;
import at.pcgamingfreaks.yaml.YAMLNotInitializedException;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ByteStreams.class, LanguageUpdateMethod.class, YAML.class })
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
	public void testBasicConfigurationFile() throws IOException, YAMLInvalidContentException, YAMLKeyNotFoundException, NoSuchFieldException, IllegalAccessException
	{
		setConfigFile();
		Configuration configuration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 5);
		assertNotNull("The configuration file must not be null", configuration);
		Field configField = TestUtils.setAccessible(Configuration.class, configuration, "config", null);
		assertFalse("The configuration file should not be loaded", configuration.isLoaded());
		TestUtils.setUnaccessible(configField, configuration, false);
		assertTrue("The configuration file should be loaded", configuration.isLoaded());
		YAML config = configuration.getConfig();
		assertEquals("The test string value should be found in the test config file", "Another Test", config.getString("TestConfig.Value2"));
		assertEquals("The test int value should be found in the test config file", 5, config.getInt("Version"));
		assertEquals("The test boolean value should be found in the test config file", false, config.getBoolean("TestBoolean"));
		assertEquals("The test double value should be found in the test config file", 2.6, config.getDouble("TestDouble"), 0.1);
		assertEquals("The returned string value should match", config.getString("TestConfig.Value2"), configuration.getString("TestConfig.Value2"));
		assertEquals("The returned integer value should match", config.getInt("Version"), configuration.getInt("Version"));
		assertEquals("The returned boolean value should match", config.getBoolean("TestBoolean"), configuration.getBool("TestBoolean"));
		assertEquals("The returned double value should match", config.getDouble("TestDouble"), configuration.getDouble("TestDouble"), 0.1);
		assertEquals("The returned string value should match", "Not there", configuration.getString("Nothing", "Not there"));
		assertEquals("The returned integer value should match", -1, configuration.getInt("Nothing", -1));
		assertEquals("The returned boolean value should match", true, configuration.getBool("Nothing", true));
		assertEquals("The returned double value should match", 1.0, configuration.getDouble("Nothing", 1.0), 0.1);
		config.set("TestConfig.Value3", "String");
		config.set("VersionX", 3);
		config.set("TestBoolean2", true);
		config.set("TestDouble2", 3.1415);
		configuration.set("TestConfig.Value2", config.getString("TestConfig.Value3"));
		configuration.set("Version", config.getInt("VersionX"));
		configuration.set("TestBoolean", config.getBoolean("TestBoolean2"));
		configuration.set("TestDouble", config.getDouble("TestDouble2"));
		assertEquals("The returned string value should match after setting the new value", config.getString("TestConfig.Value3"), configuration.getString("TestConfig.Value2"));
		assertEquals("The returned integer value should match after setting the new value", config.getInt("VersionX"), configuration.getInt("Version"));
		assertEquals("The returned boolean value should match after setting the new value", config.getBoolean("TestBoolean2"), configuration.getBool("TestBoolean"));
		assertEquals("The returned double value should match after setting the new value", config.getDouble("TestDouble2"), configuration.getDouble("TestDouble"), 0.1);
		configuration.reload();
		YAML copiedConfig = new YAML(new File(System.getProperty("user.dir"), "config.yml"));
		assertEquals("The returned string value should match after reload", copiedConfig.getString("TestConfig.Value2"), configuration.getString("TestConfig.Value2"));
		assertEquals("The returned integer value should match after reload", copiedConfig.getInt("Version"), configuration.getInt("Version"));
		assertEquals("The returned boolean value should match after reload", copiedConfig.getBoolean("TestBoolean"), configuration.getBool("TestBoolean"));
		assertEquals("The returned double value should match after reload", copiedConfig.getDouble("TestDouble"), configuration.getDouble("TestDouble"), 0.1);
		assertEquals("The language element should be found in the configuration", config.getString("Language"), configuration.getLanguage());
		assertEquals("The language update method should be found in the configuration", LanguageUpdateMethod.valueOf(config.getString("LanguageUpdateMode").toUpperCase()), configuration.getLanguageUpdateMode());
	}

	@Test
	public void testExtendedConfiguration() throws YAMLKeyNotFoundException
	{
		Configuration oldConfiguration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1, "config.yml");
		Configuration upgradeThresholdConfig = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1, 1);
		assertEquals("The configuration with the current version as upgrade threshold should return the same configuration", oldConfiguration.getConfig().getString("Version"), upgradeThresholdConfig.getConfig().getString("Version"));
	}

	@Test
	public void testLoadConfig() throws Exception
	{
		Configuration invalidConfiguration = spy(new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1, "NotFound.yml"));
		when(invalidConfiguration.newConfigCreated()).thenReturn(true);
		Field configBaseDir = Configuration.class.getDeclaredField("configBaseDir");
		Field configFile = Configuration.class.getDeclaredField("configFile");
		configBaseDir.setAccessible(true);
		configFile.setAccessible(true);
		File currentConfigBaseDir = (File) configBaseDir.get(invalidConfiguration);
		File currentConfigFile = (File) configFile.get(invalidConfiguration);
		File mockedFile = spy(currentConfigFile);
		when(mockedFile.exists()).thenReturn(false);
		when(mockedFile.mkdir()).thenReturn(false);
		configBaseDir.set(invalidConfiguration, mockedFile);
		configFile.set(invalidConfiguration, mockedFile);
		FileOutputStream mockedFileOutputStream = mock(FileOutputStream.class);
		whenNew(FileOutputStream.class).withAnyArguments().thenReturn(mockedFileOutputStream);
		PowerMockito.doAnswer(new Answer<Void>()
		{
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		}).when(mockedFileOutputStream).flush();
		mockStatic(ByteStreams.class);
		when(ByteStreams.copy(any(FileInputStream.class), any(FileOutputStream.class))).thenReturn((long) 0);
		invalidConfiguration.reload();
		when(mockedFile.mkdir()).thenReturn(true);
		whenNew(YAML.class).withAnyArguments().thenReturn(null);
		invalidConfiguration.reload();
		configBaseDir.set(invalidConfiguration, currentConfigBaseDir);
		configFile.set(invalidConfiguration, currentConfigFile);
		configBaseDir.setAccessible(false);
		configFile.setAccessible(false);
		assertEquals("The version should not be found", -1, invalidConfiguration.getVersion());
		mockStatic(LanguageUpdateMethod.class);
		given(LanguageUpdateMethod.valueOf(anyString())).willThrow(new IllegalArgumentException());
		YAML mockedYAML = mock(YAML.class);
		when(mockedYAML.getString(anyString(), anyString())).thenReturn("SOMETHING");
		Field configYAML = TestUtils.setAccessible(Configuration.class, invalidConfiguration, "config", mockedYAML);
		assertEquals("The language update mode should not be found", LanguageUpdateMethod.UPGRADE, invalidConfiguration.getLanguageUpdateMode());
		TestUtils.setUnaccessible(configYAML, invalidConfiguration, false);
	}

	@Test
	public void testSaveConfig() throws FileNotFoundException, YAMLKeyNotFoundException
	{
		Configuration configuration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1);
		configuration.set("NewlySavedValue", true);
		configuration.saveConfig();
		configuration.reload();
		assertEquals("The saved value should be in the reloaded config", true, configuration.getBool("NewlySavedValue"));
	}

	@Test(expected = YAMLKeyNotFoundException.class)
	public void testSaveConfigWithError() throws Exception
	{
		YAML mockedYAML = mock(YAML.class);
		doThrow(new YAMLNotInitializedException()).when(mockedYAML).save((File) anyObject());
		Configuration configuration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1);
		configuration.set("NotSavedValue", false);
		Field configurationYAML = Configuration.class.getDeclaredField("config");
		configurationYAML.setAccessible(true);
		YAML currentConfiguration = (YAML) configurationYAML.get(configuration);
		configurationYAML.set(configuration, mockedYAML);
		configuration.saveConfig();
		configurationYAML.set(configuration, currentConfiguration);
		configurationYAML.setAccessible(false);
		configuration.reload();
		configuration.getBool("NotSavedValue");
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
	public void testUpdateConfig() throws Exception
	{
		int currentVersion = 5;
		File userDir = new File(System.getProperty("user.dir"));
		Configuration upgradeTest = new Configuration(mockedLogger, userDir, 3, 2);
		File mockedFile = spy(new File(userDir + "\\config.yml.old_v1"));
		when(mockedFile.exists()).thenReturn(true);
		when(mockedFile.delete()).thenReturn(false);
		whenNew(File.class).withArguments(userDir + "\\config.yml.old_v1").thenReturn(mockedFile);
		upgradeTest.reload();
		Configuration updateTest = spy(new Configuration(mockedLogger, userDir, currentVersion++));
		Method updateConfig = Configuration.class.getDeclaredMethod("updateConfig");
		updateConfig.setAccessible(true);
		Field expectedVersion = TestUtils.setAccessible(Configuration.class, updateTest, "expectedVersion", ++currentVersion);
		assertTrue("The configuration should have been updated", (Boolean) updateConfig.invoke(updateTest));
		expectedVersion.set(updateTest, ++currentVersion);
		Field upgradeThreshold = TestUtils.setAccessible(Configuration.class, updateTest, "upgradeThreshold", currentVersion);
		doReturn(true).when(updateTest).newConfigCreated();
		assertTrue("The configuration should have been updated", (Boolean) updateConfig.invoke(updateTest));
		expectedVersion.set(updateTest, ++currentVersion);
		assertTrue("The configuration should have been updated", (Boolean) updateConfig.invoke(updateTest));
		expectedVersion.set(updateTest, ++currentVersion);
		upgradeThreshold.set(updateTest, 1);
		doThrow(new FileNotFoundException()).when(updateTest).saveConfig();
		assertFalse("The configuration should not have been updated", (Boolean) updateConfig.invoke(updateTest));
		TestUtils.setUnaccessible(upgradeThreshold, updateTest, true);
		TestUtils.setUnaccessible(expectedVersion, updateTest, true);
		updateConfig.setAccessible(false);
	}

	@Test
	@PrepareForTest({ Configuration.class, File.class })
	public void testUpgrade() throws Exception
	{
		int version = 3;
		File userDir = new File(System.getProperty("user.dir"));
		Configuration configuration = spy(new Configuration(mockedLogger, userDir, 1));
		doReturn(true).when(configuration).newConfigCreated();
		configuration.set("Version", version);
		configuration.set("TestBoolean", true);
		configuration.set("NewValue", "NotFound");
		assertEquals("The TestBoolean value should be true", true, configuration.getBool("TestBoolean"));
		Configuration oldConfiguration = new Configuration(mockedLogger, userDir, 1);
		configuration.doUpgrade(oldConfiguration);
		assertEquals("The version of the upgraded configuration should match", version, configuration.getVersion());
		assertEquals("The boolean value should be taken from the old configuration", false, configuration.getBool("TestBoolean"));
		Method upgradeConfig = Configuration.class.getDeclaredMethod("upgradeConfig");
		upgradeConfig.setAccessible(true);
		File mockedFile = mock(File.class);
		//noinspection ResultOfMethodCallIgnored
		doReturn(true).when(mockedFile).exists();
		//noinspection ResultOfMethodCallIgnored
		doReturn(false).when(mockedFile).delete();
		Field configFile = TestUtils.setAccessible(Configuration.class, configuration, "configFile", mockedFile);
		//noinspection ResultOfMethodCallIgnored
		doReturn(false).when(mockedFile).renameTo(mockedFile);
		whenNew(File.class).withAnyArguments().thenReturn(mockedFile);
		upgradeConfig.invoke(configuration);
		assertEquals("The version of the config file should match because it has not been upgraded", version, configuration.getVersion());
		//noinspection ResultOfMethodCallIgnored
		doThrow(new NullPointerException()).when(mockedFile).renameTo(mockedFile);
		upgradeConfig.invoke(configuration);
		assertEquals("The version of the config file should be set to -1 if an error occurs", -1, configuration.getVersion());
		//noinspection ResultOfMethodCallIgnored
		doReturn(true).when(mockedFile).delete();
		//noinspection ResultOfMethodCallIgnored
		doReturn(true).when(mockedFile).renameTo(mockedFile);
		doReturn(false).when(configuration).isLoaded();
		upgradeConfig.invoke(configuration);
		assertEquals("The version of the config file should match because it has not been upgraded", -1, configuration.getVersion());
		upgradeConfig.setAccessible(false);
		TestUtils.setUnaccessible(configFile, configuration, true);
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
		doThrow(new FileNotFoundException()).when(configuration).saveConfig();
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