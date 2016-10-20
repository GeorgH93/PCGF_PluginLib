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

import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.yaml.YAML;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Language.class, YAML.class })
public class LanguageTest
{
	private static Logger mockedLogger;
	private static int loggedInfoCount;

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		loggedInfoCount = 0;
		mockedLogger = mock(Logger.class);
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				loggedInfoCount++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
		TestUtils.initReflection();
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
		whenNew(FileOutputStream.class).withArguments(mockedFile).thenThrow(new FileNotFoundException());
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

	@AfterClass
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void cleanupTestData()
	{
		new File("lang\\de.yml").delete();
		new File("lang\\en.yml").delete();
		new File("lang").delete();
	}
}