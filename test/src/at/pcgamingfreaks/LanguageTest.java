/*
 * Copyright (C) 2016, 2018 MarkusWME
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

package at.pcgamingfreaks;

import at.pcgamingfreaks.Bukkit.Message.Sender.SendMethod;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestMessage;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.yaml.YAML;

import org.bukkit.Bukkit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Language.class, Utils.class, YAML.class })
public class LanguageTest
{
	private static Logger mockedLogger;
	public static int loggedInfoCount;

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		loggedInfoCount = 0;
		mockedLogger = mock(Logger.class);
		doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				loggedInfoCount++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
		TestUtils.initReflection();
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
	}

	@Test
	public void testLanguage()
	{
		File userDir = new File(System.getProperty("user.dir"));
		Language language = new Language(mockedLogger, userDir, 1);
		assertNotNull("The loaded language file should not be null", language);
		language = new Language(mockedLogger, userDir, 4, 3);
		assertNotNull("The loaded language file should not be null", language);
		assertFalse("Language data should not be loaded", language.isLoaded());
	}

	@Test
	public void testLoad() throws Exception
	{
		File userDir = new File(System.getProperty("user.dir"));
		Language language = new Language(mockedLogger, userDir, 1);
		language.load("de", "overwrite");
		assertTrue("Language data should be loaded", language.isLoaded());
		language.load("en", "update");
		assertTrue("Language data should be loaded", language.isLoaded());
		language.load("en", "update");
		assertTrue("Language data should be loaded", language.isLoaded());
		assertEquals("The version of the language file should match", 1, language.getVersion());
		assertEquals("The language string should match", "text", language.get("Lang1"));
		assertEquals("The language string should match", "t", language.getTranslated("Lang4"));
		assertEquals("The language string should match", "§cMessage not found!", language.get("Lang1000"));
		language.set("Language.Lang1000", "New language value");
		assertEquals("The language string should match", "New language value", language.get("Lang1000"));
		assertNotNull("Language data should not be null", language.getLang());
		whenNew(YAML.class).withArguments(anyString()).thenThrow(new IOException());
		assertFalse("Language data should not be loaded", language.load("en", "overwrite"));
	}

	@Test
	public void testGetMessage() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		File userDir = new File(System.getProperty("user.dir"));
		Language language = new Language(mockedLogger, userDir, 1);
		language.load("en", YamlFileUpdateMethod.UPDATE);
		Method getMessage = Language.class.getDeclaredMethod("getMessage", boolean.class, String.class);
		getMessage.setAccessible(true);
		Field messageClasses = TestUtils.setAccessible(Language.class, language, "messageClasses", null);
		assertNull("The returned value should be null when the message classes are not initialized", getMessage.invoke(language, true, "Lang1"));
		messageClasses.set(language, new Language.MessageClassesReflectionDataHolder(Reflection.getConstructor(TestMessage.class, String.class), Reflection.getMethod(TestMessage.class, "setSendMethod", SendMethod.class), Reflection.getMethod(SendMethod.class, "getMetadataFromJsonMethod"), SendMethod.class));
		assertNotNull("The returned value should not be null", getMessage.invoke(language, true, "Lang1"));
		language.set("Language.Lang1_SendMethod", "CHAT");
		assertNotNull("The returned value should not be null", getMessage.invoke(language, false, "Lang1"));
		language.set("Language.Lang1_SendMethod", "TITLE");
		language.set("Language.Lang1_Parameters", "{\"isSubtitle\": false}");
		assertNotNull("The returned value should not be null", getMessage.invoke(language, false, "Lang1"));
		language.set("Language.Lang1_SendMethod", "CHAT");
		assertNotNull("The returned value should not be null", getMessage.invoke(language, false, "Lang1"));
		TestUtils.setUnaccessible(messageClasses, language, false);
		getMessage.setAccessible(false);
	}

	@Test
	public void testReload()
	{
		File userDir = new File(System.getProperty("user.dir"));
		Language language = new Language(mockedLogger, userDir, 1);
		language.reload();
		assertNotNull("The language object should not be null", language);
	}

	@Test
	public void testLoadWithConfig()
	{
		File userDir = new File(System.getProperty("user.dir"));
		Language language = spy(new Language(mockedLogger, userDir, 1));
		doReturn(false).when(language).load(anyString(), any(YamlFileUpdateMethod.class));
		Configuration mockedConfig = mock(Configuration.class);
		doReturn("").when(mockedConfig).getLanguage();
		language.load(mockedConfig);
		assertNotNull("The language object should not be null", language);
	}

	@Test
	public void testExtract() throws NoSuchFieldException, IllegalAccessException
	{
		mockStatic(Utils.class);
		given(Utils.extractFile(Matchers.<Class<?>>any(), any(Logger.class), anyString(), any(File.class))).willReturn(false);
		File userDir = new File(System.getProperty("user.dir"));
		Language language = new Language(mockedLogger, userDir, 1);
		Field languageField = TestUtils.setAccessible(Language.class, language, "language", "de");
		language.extractFile();
		languageField.set(language, "en");
		language.extractFile();
		TestUtils.setUnaccessible(languageField, language, false);
		assertNotNull("The language object should not be null", language);
	}

	@Test
	public void testPropertyGetters()
	{
		File userDir = new File(System.getProperty("user.dir"));
		Language language = new Language(mockedLogger, userDir, 1);
		language.load("de", "overwrite");
		assertEquals("Unknown", language.getAuthor());
		assertEquals("de", language.getLanguage());
	}

	@AfterClass
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void cleanupTestData()
	{
		new File("lang\\de.yml").delete();
		new File("lang\\en.yml").delete();
		new File("lang").delete();
	}
}