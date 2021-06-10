/*
 *   Copyright (C) 2021 GeorgH93
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

package at.pcgamingfreaks;

import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(YamlFileManager.class)
public class YamlFileManagerTest
{
	@Test
	public void testDoUpgrade() throws YamlInvalidContentException
	{
		Logger mockedLogger = mock(Logger.class);
		YamlFileManager testFileManager = new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", new YAML("Version: 7\nTest: 2\nHallo: Welt"));
		YamlFileManager oldFileManager = new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", new YAML("Version: 2\nTest: 5"));
		testFileManager.doUpgrade(oldFileManager);
	}

	@Test
	public void testDoUpdate()
	{
		final int[] infoCount = { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(invocationOnMock -> {
			infoCount[0]++;
			return null;
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
	public void testSave() throws FileNotFoundException
	{
		final int[] warnCount = { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(invocationOnMock -> {
			warnCount[0]++;
			return null;
		}).when(mockedLogger).warning(anyString());
		YAML mockedYAML = mock(YAML.class);
		doNothing().when(mockedYAML).save(any(File.class));
		YamlFileManager testFileManager = new YamlFileManager(mockedLogger, null, 20, 15, "", "Test.file", "", mockedYAML);
		testFileManager.save();
		assertEquals("No info message should be written to the console", 0, warnCount[0]);
	}

	@Test
	public void testLoad() throws Exception
	{
		File mockedFile = mock(File.class);
		doReturn(true).when(mockedFile).exists();
		doReturn(12L).when(mockedFile).length();
		final int[] warnCount = { 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(invocationOnMock -> {
			warnCount[0]++;
			return null;
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
		doAnswer(invocationOnMock -> {
			count[0]++;
			return null;
		}).when(mockedLogger).warning(anyString());
		doAnswer(invocationOnMock -> {
			count[1]++;
			return null;
		}).when(mockedLogger).info(anyString());
		YamlFileManager testFileManager = spy(new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", null));
		doReturn(new Version(5)).when(testFileManager).version();
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
		doReturn(new Version(500)).when(testFileManager).version();
		testFileManager.validate();
		assertEquals("There should be one info in the log", 2, count[1]);
		Field updateModeField = TestUtils.setAccessible(YamlFileManager.class, testFileManager, "updateMode", YamlFileUpdateMethod.UPGRADE);
		testFileManager.validate();
		doReturn(new Version(1)).when(testFileManager).version();
		testFileManager.validate();
		assertEquals("There should be new info in the log", 4, count[1]);
		updateModeField.set(testFileManager, YamlFileUpdateMethod.OVERWRITE);
		testFileManager.validate();
		assertEquals("There should be no new info in the log in overwrite mode when the file has been extracted", 4, count[1]);
		extractedField.set(testFileManager, false);
		testFileManager.validate();
		assertEquals("There should be new info in the log in overwrite mode", 5, count[1]);
		TestUtils.setUnaccessible(extractedField, testFileManager, false);
		TestUtils.setUnaccessible(updateModeField, testFileManager, false);
	}

	@Test
	public void testUpdate() throws FileNotFoundException, NoSuchFieldException, IllegalAccessException
	{
		final int[] count = { 0, 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(invocationOnMock -> {
			count[0]++;
			return null;
		}).when(mockedLogger).warning(anyString());
		doAnswer(invocationOnMock -> {
			count[1]++;
			return null;
		}).when(mockedLogger).info(anyString());
		YamlFileManager testFileManager = spy(new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", null));
		doNothing().when(testFileManager).doUpdate();
		doNothing().when(testFileManager).save();
		doReturn(new Version(0)).when(testFileManager).version();
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

	@Test
	public void testUpgrade() throws Exception
	{
		int warnings = 0;
		int infos = 0;
		final int[] count = { 0, 0 };
		Logger mockedLogger = mock(Logger.class);
		doAnswer(invocationOnMock -> {
			count[0]++;
			System.out.println("[Warning] " + invocationOnMock.getArgument(0));
			return null;
		}).when(mockedLogger).warning(anyString());
		doAnswer(invocationOnMock -> {
			count[1]++;
			System.out.println("[Info] " + invocationOnMock.getArgument(0));
			return null;
		}).when(mockedLogger).info(anyString());
		YamlFileManager testFileManager = spy(new YamlFileManager(mockedLogger, null, 20, 15, "", null, "", null));
		doNothing().when(testFileManager).load();
		doNothing().when(testFileManager).save();
		doReturn(new Version(0)).when(testFileManager).version();
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
		infos += 2;
		assertEquals("No warning should be written out", warnings, count[0]);
		assertEquals("Much info should be written out on no upgrade", infos, count[1]);
		doReturn(true).when(testFileManager).isLoaded();
		testFileManager.upgrade();
		infos += 2;
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