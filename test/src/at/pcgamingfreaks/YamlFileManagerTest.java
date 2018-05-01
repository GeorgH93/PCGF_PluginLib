/*
 *   Copyright (C) 2018 GeorgH93, MarkusWME
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

package at.pcgamingfreaks;

import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YAMLInvalidContentException;
import at.pcgamingfreaks.yaml.YAMLNotInitializedException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(YamlFileManager.class)
public class YamlFileManagerTest
{
	@Test
	public void testConstructor()
	{
		YamlFileManager testFileManager = new YamlFileManager(null, null, 20, 15, "", null, "", new YAML());
		assertNotNull("The YamlFileManager should not be null", testFileManager);
		assertNotNull("The YAML object should not be null", testFileManager.getYaml());
	}

	@Test
	public void testIsLoaded()
	{
		YamlFileManager testFileManager = new YamlFileManager(null, null, 20, 15, "", null, "", new YAML());
		assertTrue("The file should be loaded", testFileManager.isLoaded());
		testFileManager = new YamlFileManager(null, null, 20, 15, "", null, "", null);
		assertFalse("The file should not be loaded", testFileManager.isLoaded());
	}

	@Test
	public void testDoUpgrade() throws YAMLInvalidContentException
	{
		final int[] infoCount = { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				infoCount[0]++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
		YamlFileManager testFileManager = new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", new YAML("Version: 7\nTest: 2\nHallo: Welt"));
		YamlFileManager oldFileManager = new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", new YAML("Version: 2\nTest: 5"));
		testFileManager.doUpgrade(oldFileManager);
		assertEquals("An info should be written to the console", 1, infoCount[0]);
	}

	@Test
	public void testDoUpdate()
	{
		final int[] infoCount = { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				infoCount[0]++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
		new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", null).doUpdate();
		assertEquals("One info message should be written to the console", 1, infoCount[0]);
	}

	@Test
	public void testNewConfigCreated()
	{
		assertFalse("No new config should have been created", new YamlFileManager(null, null, 20, 15, "", null, "", null).newConfigCreated());
	}

	@Test
	public void testSave() throws FileNotFoundException, YAMLNotInitializedException
	{
		final int[] warnCount = { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				warnCount[0]++;
				return null;
			}
		}).when(mockedLogger).warning(anyString());
		YAML mockedYAML = mock(YAML.class);
		doNothing().when(mockedYAML).save(any(File.class));
		YamlFileManager testFileManager = new YamlFileManager(mockedLogger, null, 20, 15, "", "Test.file", "", mockedYAML);
		testFileManager.save();
		assertEquals("No info message should be written to the console", 0, warnCount[0]);
		doThrow(new YAMLNotInitializedException()).when(mockedYAML).save(any(File.class));
		testFileManager.save();
		assertEquals("One info message should be written to the console", 1, warnCount[0]);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test
	public void testLoad() throws Exception
	{
		File mockedFile = mock(File.class);
		doReturn(true).when(mockedFile).exists();
		doReturn(12L).when(mockedFile).length();
		final int[] warnCount = { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				warnCount[0]++;
				return null;
			}
		}).when(mockedLogger).warning(anyString());
		YamlFileManager testFileManager = spy(new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", null));
		doNothing().when(testFileManager).validate();
		doNothing().when(testFileManager).save();
		TestUtils.initReflection();
		Field yamlFileField = TestUtils.setAccessible(YamlFileManager.class, testFileManager, "yamlFile", mockedFile);
		YAML mockedYAML = mock(YAML.class);
		whenNew(YAML.class).withAnyArguments().thenReturn(mockedYAML);
		testFileManager.load();
		assertEquals("No warning should be shown", 0, warnCount[0]);
		Field extractedField = TestUtils.setAccessible(YamlFileManager.class, testFileManager, "extracted", true);
		testFileManager.load();
		assertEquals("No warning should be shown", 0, warnCount[0]);
		doReturn(true).when(testFileManager).newConfigCreated();
		testFileManager.load();
		assertEquals("No warning should be shown", 0, warnCount[0]);
		TestUtils.setUnaccessible(extractedField, testFileManager, false);
		TestUtils.setUnaccessible(yamlFileField, testFileManager, false);
	}

	@Test
	public void testValidate() throws NoSuchFieldException, IllegalAccessException
	{
		final int[] count = { 0, 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				count[0]++;
				return null;
			}
		}).when(mockedLogger).warning(anyString());
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				count[1]++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
		YamlFileManager testFileManager = spy(new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", null));
		doReturn(5).when(testFileManager).getVersion();
		doNothing().when(testFileManager).extractFile();
		doNothing().when(testFileManager).load();
		doNothing().when(testFileManager).upgrade();
		doNothing().when(testFileManager).update();
		testFileManager.validate();
		assertEquals("There should be no warning in the log", 0, count[0]);
		TestUtils.initReflection();
		Field extractedField = TestUtils.setAccessible(YamlFileManager.class, testFileManager, "extracted", true);
		testFileManager.validate();
		assertEquals("There should be one warning in the log", 1, count[0]);
		doReturn(500).when(testFileManager).getVersion();
		testFileManager.validate();
		assertEquals("There should be one info in the log", 1, count[1]);
		Field updateModeField = TestUtils.setAccessible(YamlFileManager.class, testFileManager, "updateMode", YamlFileUpdateMethod.UPGRADE);
		testFileManager.validate();
		doReturn(1).when(testFileManager).getVersion();
		testFileManager.validate();
		assertEquals("There should be new info in the log", 2, count[1]);
		updateModeField.set(testFileManager, YamlFileUpdateMethod.OVERWRITE);
		testFileManager.validate();
		assertEquals("There should be no new info in the log in overwrite mode when the file has been extracted", 2, count[1]);
		extractedField.set(testFileManager, false);
		testFileManager.validate();
		assertEquals("There should be new info in the log in overwrite mode", 3, count[1]);
		TestUtils.setUnaccessible(extractedField, testFileManager, false);
		TestUtils.setUnaccessible(updateModeField, testFileManager, false);
	}

	@Test
	public void testUpdate() throws FileNotFoundException, NoSuchFieldException, IllegalAccessException
	{
		final int[] count = { 0, 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				count[0]++;
				return null;
			}
		}).when(mockedLogger).warning(anyString());
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				count[1]++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
		YamlFileManager testFileManager = spy(new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", null));
		doNothing().when(testFileManager).doUpdate();
		doNothing().when(testFileManager).save();
		doReturn(-1).when(testFileManager).getVersion();
		YAML mockedYAML = mock(YAML.class);
		doNothing().when(mockedYAML).set(anyString(), anyString());
		testFileManager.update();
		assertEquals("One warning should be written out", 1, count[0]);
		assertEquals("Info should be written out on update", 1, count[1]);
		TestUtils.initReflection();
		Field yamlField = TestUtils.setAccessible(YamlFileManager.class, testFileManager, "yaml", mockedYAML);
		testFileManager.update();
		assertEquals("No warning should be written out", 1, count[0]);
		assertEquals("Info should be written out on update", 3, count[1]);
		TestUtils.setUnaccessible(yamlField, testFileManager, false);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Test
	public void testUpgrade() throws Exception
	{
		int warnings = 0;
		int infos = 0;
		final int[] count = { 0, 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				count[0]++;
				return null;
			}
		}).when(mockedLogger).warning(anyString());
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				count[1]++;
				return null;
			}
		}).when(mockedLogger).info(anyString());
		YamlFileManager testFileManager = spy(new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", null));
		doNothing().when(testFileManager).load();
		doNothing().when(testFileManager).save();
		doReturn(-1).when(testFileManager).getVersion();
		testFileManager.upgrade();
		assertEquals("A warning should be written out", ++warnings, count[0]);
		assertEquals("Info should be written out on no upgrade", ++infos, count[1]);
		File mockedFile = mock(File.class);
		doReturn(true).when(mockedFile).exists();
		whenNew(File.class).withAnyArguments().thenReturn(mockedFile);
		testFileManager.upgrade();
		warnings += 2;
		assertEquals("Two warnings should be written out", warnings, count[0]);
		assertEquals("Info should be written out on no upgrade", ++infos, count[1]);
		TestUtils.initReflection();
		Field yamlFileField = TestUtils.setAccessible(YamlFileManager.class, testFileManager, "yamlFile", mockedFile);
		doReturn(true).when(mockedFile).delete();
		testFileManager.upgrade();
		assertEquals("A warning should be written out", ++warnings, count[0]);
		assertEquals("An info should be written out on no upgrade", ++infos, count[1]);
		doReturn(false).when(mockedFile).renameTo(any(File.class));
		testFileManager.upgrade();
		assertEquals("A warning should be written out", ++warnings, count[0]);
		assertEquals("An info should be written out on no upgrade", ++infos, count[1]);
		doReturn(true).when(mockedFile).renameTo(any(File.class));
		YAML mockedYAML = mock(YAML.class);
		doNothing().when(mockedYAML).set(anyString(), anyString());
		Field yamlField = TestUtils.setAccessible(YamlFileManager.class, testFileManager, "yaml", mockedYAML);
		testFileManager.upgrade();
		infos += 3;
		assertEquals("No warning should be written out", warnings, count[0]);
		assertEquals("Much info should be written out on no upgrade", infos, count[1]);
		doReturn(true).when(testFileManager).isLoaded();
		testFileManager.upgrade();
		infos += 3;
		assertEquals("No warning should be written out", warnings, count[0]);
		assertEquals("Much info should be written out on no upgrade", infos, count[1]);
		doReturn(false).when(testFileManager).isLoaded();
		testFileManager.upgrade();
		infos += 2;
		assertEquals("No warning should be written out", warnings, count[0]);
		assertEquals("Much info should be written out on no upgrade", infos, count[1]);
		TestUtils.setUnaccessible(yamlFileField, testFileManager, false);
		TestUtils.setUnaccessible(yamlField, testFileManager, false);
	}
}