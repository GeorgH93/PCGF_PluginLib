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

package at.pcgamingfreaks;

import at.pcgamingfreaks.Bukkit.Message.Sender.SendMethod;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestMessage;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YAMLNotInitializedException;

import org.bukkit.Bukkit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Language.class, YAML.class })
public class LanguageTest
{
	private static Logger mockedLogger;
	private static int loggedInfoCount;

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
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testExtractLangFile() throws Exception
	{
		File userDir = new File(System.getProperty("user.dir"));
		Language language = new Language(mockedLogger, userDir, 1);
		Method extractLangFile = Language.class.getDeclaredMethod("extractLangFile");
		extractLangFile.setAccessible(true);
		File mockedFile = mock(File.class);
		Field langFile = TestUtils.setAccessible(Language.class, language, "langFile", mockedFile);
		Field baseDir = TestUtils.setAccessible(Language.class, language, "baseDir", mockedFile);
		int logCount = loggedInfoCount;
		doReturn(true).when(mockedFile).exists();
		doReturn(false).when(mockedFile).delete();
		doReturn(false).when(mockedFile).createNewFile();
		FileOutputStream mockedOutputStream = spy(new FileOutputStream(new File(userDir, "en.yml")));
		doThrow(new IOException()).when(mockedOutputStream).flush();
		whenNew(FileOutputStream.class).withArguments(mockedFile).thenReturn(mockedOutputStream);
		extractLangFile.invoke(language);
		logCount += 2;
		assertEquals("The log count should be correct", logCount, loggedInfoCount);
		doReturn(true).when(mockedFile).delete();
		doReturn(false).when(mockedFile).mkdirs();
		extractLangFile.invoke(language);
		logCount += 1;
		assertEquals("The log count should be correct", logCount, loggedInfoCount);
		doReturn(false).when(mockedFile).exists();
		extractLangFile.invoke(language);
		logCount += 2;
		assertEquals("The log count should be correct", logCount, loggedInfoCount);
		TestUtils.setUnaccessible(baseDir, language, true);
		TestUtils.setUnaccessible(langFile, language, false);
		extractLangFile.setAccessible(false);
	}

	@Test
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testUpdateLang() throws Exception
	{
		int version = 5;
		File userDir = new File(System.getProperty("user.dir"));
		final Language language = PowerMockito.spy(new Language(mockedLogger, userDir, 1));
		language.load("en", "update");
		Method updateLang = Language.class.getDeclaredMethod("updateLang");
		updateLang.setAccessible(true);
		Field expectedVersion = TestUtils.setAccessible(Language.class, language, "expectedVersion", 0);
		assertFalse("The language file should be newer than expected", (Boolean) updateLang.invoke(language));
		expectedVersion.set(language, version);
		assertTrue("The language file should be updated", (Boolean) updateLang.invoke(language));
		doThrow(new FileNotFoundException()).when(language).save();
		expectedVersion.set(language, ++version);
		assertFalse("The language file should not be updated when an error occurs", (Boolean) updateLang.invoke(language));
		doCallRealMethod().when(language).save();
		Field updateMode = TestUtils.setAccessible(Language.class, language, "updateMode", YamlFileUpdateMethod.OVERWRITE);
		PowerMockito.doNothing().when(language, "extractLangFile");
		PowerMockito.doNothing().when(language, "loadLang");
		expectedVersion.set(language, ++version);
		assertTrue("The language file should be updated", (Boolean) updateLang.invoke(language));
		PowerMockito.doCallRealMethod().when(language, "loadLang");
		Field upgradeThreshold = TestUtils.setAccessible(Language.class, language, "upgradeThreshold", ++version);
		expectedVersion.set(language, version);
		assertTrue("The language file should be updated", (Boolean) updateLang.invoke(language));
		upgradeThreshold.set(language, 2);
		expectedVersion.set(language, ++version);
		assertTrue("The language file should be updated", (Boolean) updateLang.invoke(language));
		updateMode.set(language, YamlFileUpdateMethod.UPGRADE);
		expectedVersion.set(language, ++version);
		assertFalse("The language file should not be upgraded because the file could not be found", (Boolean) updateLang.invoke(language));
		File mockedFile = mock(File.class);
		whenNew(File.class).withAnyArguments().thenReturn(mockedFile);
		doReturn(true).when(mockedFile).exists();
		doReturn(false).when(mockedFile).delete();
		expectedVersion.set(language, ++version);
		assertFalse("The language file should not be upgraded because the file could not be deleted", (Boolean) updateLang.invoke(language));
		doReturn(true).when(mockedFile).delete();
		doReturn(true).when(mockedFile).renameTo(mockedFile);
		doReturn(false).when(language).isLoaded();
		Field langFile = TestUtils.setAccessible(Language.class, language, "langFile", mockedFile);
		expectedVersion.set(language, ++version);
		assertTrue("The language file should be upgraded", (Boolean) updateLang.invoke(language));
		whenNew(File.class).withArguments(contains(".old_v")).thenThrow(new IOException());
		expectedVersion.set(language, ++version);
		assertFalse("The language file should not be upgraded because the file object could not be created", (Boolean) updateLang.invoke(language));
		TestUtils.setUnaccessible(langFile, language, false);
		TestUtils.setUnaccessible(upgradeThreshold, language, true);
		TestUtils.setUnaccessible(updateMode, language, false);
		TestUtils.setUnaccessible(expectedVersion, language, true);
		updateLang.setAccessible(false);
	}

	@Test
	public void testUpgrade() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		File userDir = new File(System.getProperty("user.dir"));
		Language language = PowerMockito.spy(new Language(mockedLogger, userDir, 1));
		language.load("en", YamlFileUpdateMethod.UPDATE);
		language.set("Language.NewValue", "NEW");
		Language oldLanguage = PowerMockito.spy(new Language(mockedLogger, userDir, 1));
		oldLanguage.load("en", YamlFileUpdateMethod.UPDATE);
		oldLanguage.set("Language.Lang1", "TEST");
		Method doUpgrade = Language.class.getDeclaredMethod("doUpgrade", Language.class);
		doUpgrade.setAccessible(true);
		doUpgrade.invoke(language, oldLanguage);
		assertEquals("The upgrade should be successful", language.get("Lang1"), "TEST");
		doUpgrade.setAccessible(false);
	}

	@Test
	public void testSave() throws FileNotFoundException, NoSuchFieldException, IllegalAccessException, YAMLNotInitializedException
	{
		File userDir = new File(System.getProperty("user.dir"));
		Language language = PowerMockito.spy(new Language(mockedLogger, userDir, 1));
		YAML mockedYAML = mock(YAML.class);
		doThrow(new YAMLNotInitializedException()).when(mockedYAML).save(any(File.class));
		Field lang = TestUtils.setAccessible(Language.class, language, "lang", mockedYAML);
		language.save();
		TestUtils.setUnaccessible(lang, language, false);
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

	@AfterClass
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void cleanupTestData()
	{
		new File("lang\\de.yml").delete();
		new File("lang\\en.yml").delete();
		new File("lang").delete();
	}
}