/*
 * Copyright (C) 2014-2015 MarkusWME
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

import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YAMLInvalidContentException;
import at.pcgamingfreaks.yaml.YAMLKeyNotFoundException;
import at.pcgamingfreaks.yaml.YAMLNotInitializedException;

import com.google.common.io.Files;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;

public class ConfigurationTest
{
	private static Logger mockedLogger;

	private static int loggedInfos = 0;

	@BeforeClass
	public static void prepareTestData()
	{
		URL resource = UUIDConverterTest.class.getResource("/config.yml");
		if(resource != null)
		{
			try
			{
				//noinspection deprecation
				Files.copy(new File(URLDecoder.decode(resource.getFile())), new File("config.yml"));
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
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testBasicConfigurationFile() throws IOException, YAMLInvalidContentException, YAMLKeyNotFoundException
	{
		Configuration configuration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1);
		assertNotNull("The configuration file must not be null", configuration);
		assertTrue("The configuration file should be loaded", configuration.isLoaded());
		YAML config = configuration.getConfig();
		assertEquals("The test string value should be found in the test config file", "Another Test", config.getString("TestConfig.Value2"));
		assertEquals("The test int value should be found in the test config file", 1, config.getInt("Version"));
		assertEquals("The test boolean value should be found in the test config file", false, config.getBoolean("TestBoolean"));
		assertEquals("The test double value should be found in the test config file", 2.6, config.getDouble("TestDouble"), 0.1);
		assertEquals("The returned string value should match", config.getString("TestConfig.Value2"), configuration.getString("TestConfig.Value2"));
		assertEquals("The returned integer value should match", config.getInt("Version"), configuration.getInt("Version"));
		assertEquals("The returned boolean value should match", config.getBoolean("TestBoolean"), configuration.getBool("TestBoolean"));
		assertEquals("The returned double value should match", config.getDouble("TestDouble"), configuration.getDouble("TestDouble"), 0.1);
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
	public void testUpgrade() throws YAMLKeyNotFoundException
	{
		Configuration configuration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1);
		configuration.set("Version", 3);
		configuration.set("TestBoolean", true);
		assertEquals("The TestBoolean value should be true", true, configuration.getBool("TestBoolean"));
		Configuration oldConfiguration = new Configuration(mockedLogger, new File(System.getProperty("user.dir")), 1);
		configuration.doUpgrade(oldConfiguration);
		assertEquals("The version of the upgraded configuration should match", 3, configuration.getVersion());
		assertEquals("The boolean value should be taken from the old configuration", false, configuration.getBool("TestBoolean"));
	}

	@AfterClass
	public static void cleanupTestData()
	{
		//noinspection ResultOfMethodCallIgnored
		new File("config.yml").delete();
	}
}