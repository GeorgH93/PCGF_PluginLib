/*
 *   Copyright (C) 2016-2018 GeorgH93
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

import sun.reflect.ReflectionFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
public class UUIDConverterTest
{
	private static final String TEST_USER_NAME = "GeorgH93", TEST_USER_UUID = "6c99e2b55c9e4663b4db7ad3bc52d28d", TEST_USER_UUID_SEPARATORS = "6c99e2b5-5c9e-4663-b4db-7ad3bc52d28d";
	private static final String TEST_USER_OFFLINE_UUID = "05015780f9dc3a409e1dfd4c4f9e20f1", TEST_USER_OFFLINE_UUID_SEPARATORS = "05015780-f9dc-3a40-9e1d-fd4c4f9e20f1";
	private static final UUID TEST_USER_UUID_AS_UUID = UUID.fromString(TEST_USER_UUID_SEPARATORS), TEST_USER_OFFLINE_UUID_AS_UUID = UUID.fromString(TEST_USER_OFFLINE_UUID_SEPARATORS);

	@SuppressWarnings("SpellCheckingInspection")
	private static final String TEST_USER2_NAME_NEW = "Vaunan", TEST_USER2_NAME_OG = "ReedtheRed";
	@SuppressWarnings("SpellCheckingInspection")
	private static final String TEST_USER2_UUID = "8f54523078d5474bbbea693964467ef0", TEST_USER2_UUID_SEPARATORS = "8f545230-78d5-474b-bbea-693964467ef0";
	private static final UUID TEST_USER2_UUID_AS_UUID = UUID.fromString(TEST_USER2_UUID_SEPARATORS);
	private static final Date TEST_USER2_LAST_SEEN = new Date(1423214002000L), TODAY = new Date(1456071840000L);

	private static ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
	private static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		//noinspection SpellCheckingInspection
		try(FileWriter fileWriter = new FileWriter("usercache.json"))
		{
			//noinspection SpellCheckingInspection
			fileWriter.write("[{\"name\":\"VoidcrafterHD\",\"uuid\":\"4ad6ef2a-7473-46bc-b15d-ec2ee61bc1b6\",\"expiresOn\":\"" + (new SimpleDateFormat("yyyy-MM-dd kk:mm:ss Z").format(new Date(new Date().getTime() + 10000000))) + "\"},{}]");
			fileWriter.flush();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.setOut(new PrintStream(outputStream));
		System.setErr(new PrintStream(errorStream));
		new UUIDConverter();
		System.out.println(outputStream.toString());
		assertNotEquals("UUIDConverter should initialize with some messages", 0, outputStream.toString().split("\n").length);
		assertTrue("UUIDConverter should throw an error", errorStream.size() > 0);
		//noinspection SpellCheckingInspection
		assertEquals("UUIDConverter should contain file data", UUIDConverter.getUUIDFromName("VoidCrafterHD", true, true), "4ad6ef2a-7473-46bc-b15d-ec2ee61bc1b6");
		TestUtils.initReflection();
	}

	@Before
	public void resetConsoleData()
	{
		outputStream.reset();
		errorStream.reset();
	}

	@Test
	public void testGetNameFromUUID()
	{
		assertEquals("Username from UUID without separators should be converted correctly", TEST_USER_NAME, UUIDConverter.getNameFromUUID(TEST_USER_UUID));
		assertEquals("Username from UUID with separators should be converted correctly", TEST_USER_NAME, UUIDConverter.getNameFromUUID(TEST_USER_UUID_SEPARATORS));
		assertEquals("Username from UUID object should be converted correctly", TEST_USER_NAME, UUIDConverter.getNameFromUUID(UUID.fromString(TEST_USER_UUID_SEPARATORS)));
	}

	@Test
	@PrepareForTest({ URL.class, UUIDConverter.class })
	public void testGetOnlineUUIDFromName() throws Exception
	{
		Field uuidCache = UUIDConverter.class.getDeclaredField("UUID_CACHE");
		uuidCache.setAccessible(true);
		Field modifiers = uuidCache.getClass().getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(uuidCache, uuidCache.getModifiers() & ~Modifier.FINAL);
		UUIDCacheMap currentCacheMap = (UUIDCacheMap) uuidCache.get(this);
		UUIDCacheMap mockedUUIDCacheMap = mock(UUIDCacheMap.class);
		mockedUUIDCacheMap.clear();
		uuidCache.set(this, mockedUUIDCacheMap);
		assertEquals("Username with no time given and no cache should match the current username", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, null));
		assertEquals("Username at the current time with no cache should match the current username", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, TODAY));
		when(mockedUUIDCacheMap.containsKey(TEST_USER_NAME)).thenReturn(true);
		when(mockedUUIDCacheMap.get(TEST_USER_NAME)).thenReturn(TEST_USER_UUID);
		assertEquals("Username with no time given and available cache should match the current username", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, null));
		assertEquals("Username at the current time and available cache should match the current username", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, TODAY));
		reset(mockedUUIDCacheMap);
		URL mockedURL = PowerMockito.mock(URL.class);
		whenNew(URL.class).withArguments(anyString()).thenThrow(new MalformedURLException());
		UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, null);
		assertTrue("An error should be printed when a malformed URL occurs", errorStream.toString().contains("MalformedURLException"));
		PowerMockito.doThrow(new IOException("HTTP response code: 429")).when(mockedURL).openStream();
		whenNew(URL.class).withAnyArguments().thenReturn(mockedURL);
		UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, null);
		assertTrue("An error should be printed when the URL can't open the stream", errorStream.toString().contains("IOException"));
		PowerMockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock)
			{
				return null;
			}
		}).when(mockedURL).openStream();
		UUIDConverter.getUUIDFromName(TEST_USER2_NAME_NEW, true, TEST_USER2_LAST_SEEN);
		assertTrue("A message should be printed when there doesn't exist a user at the given time", outputStream.size() > 0);
		uuidCache.set(this, currentCacheMap);
		uuidCache.setAccessible(false);
	}

	@Test
	public void testGetUUIDFromName()
	{
		assertEquals("UUID of the online player should be returned without separators", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true));
		assertEquals("UUID of the offline player should be returned without separators", TEST_USER_OFFLINE_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, false));
		assertEquals("UUID of the online player should be returned without separators", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, false));
		assertEquals("UUID of the online player should be returned with separators", TEST_USER_UUID_SEPARATORS, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, true));
		assertEquals("UUID of the offline player should be returned without separators", TEST_USER_OFFLINE_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, false, false));
		assertEquals("UUID of the offline player should be returned with separators", TEST_USER_OFFLINE_UUID_SEPARATORS, UUIDConverter.getUUIDFromName(TEST_USER_NAME, false, true));
		assertEquals("UUID of the online player with the given last seen date should match", TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_NEW, true, TEST_USER2_LAST_SEEN));
		assertEquals("UUID of the online player with the given last seen date should match the separated one", TEST_USER2_UUID_SEPARATORS, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_NEW, true, true, TEST_USER2_LAST_SEEN));
		assertNull("UUID of the non existent user name should be null", UUIDConverter.getUUIDFromName("UltraLongUserName", true, false, false));
		assertEquals("UUID of the non existent user name should be correct", "946a57d3b7a3325480a82b2c927133f8", UUIDConverter.getUUIDFromName("UltraLongUserName", true, false, true));
	}

	@Test
	public void testGetUUIDObjectFromName()
	{
		assertEquals("The UUID of the user should match the online UUID", TEST_USER_UUID_AS_UUID, UUIDConverter.getUUIDFromNameAsUUID(TEST_USER_NAME, true));
		assertEquals("The UUID of the user should match the offline UUID", TEST_USER_OFFLINE_UUID_AS_UUID, UUIDConverter.getUUIDFromNameAsUUID(TEST_USER_NAME, false));
		assertNull("The UUID object should be null when no UUID could be found online", UUIDConverter.getUUIDFromNameAsUUID("UltraLongUserName", true, false));
		assertEquals("The UUID object should match the offline UUID when no UUID could be found online", UUID.fromString("946a57d3-b7a3-3254-80a8-2b2c927133f8"), UUIDConverter.getUUIDFromNameAsUUID("UltraLongUserName", true, true));
		assertEquals("The UUID of the user should match the online UUID when using the given date", TEST_USER2_UUID_AS_UUID, UUIDConverter.getUUIDFromNameAsUUID(TEST_USER2_NAME_NEW, true, TEST_USER2_LAST_SEEN));
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection")
	public void testGetUUIDsFromNamesWithoutCache()
	{
		final Map<String, String> testNames = new TreeMap<>();
		testNames.put(TEST_USER_NAME, TEST_USER_UUID);
		testNames.put("Phei", "8abb0b91429b41e49be8bd659923acd6");
		testNames.put("AFKMaster", "175c57e4cd4b4fb3bfea1c28d094f5dc");
		testNames.put("CleoMalika", "fc4b363ba4474ab98778d0ee353151ee");
		testNames.put("Ghetto1996", "5d44a19304d94ebaaa3f630b8c95b48a");
		final Map<String, String> testNamesSeparators = new TreeMap<>();
		testNamesSeparators.put(TEST_USER_NAME, TEST_USER_UUID_SEPARATORS);
		testNamesSeparators.put("Phei", "8abb0b91-429b-41e4-9be8-bd659923acd6");
		testNamesSeparators.put("AFKMaster", "175c57e4-cd4b-4fb3-bfea-1c28d094f5dc");
		testNamesSeparators.put("CleoMalika", "fc4b363b-a447-4ab9-8778-d0ee353151ee");
		testNamesSeparators.put("Ghetto1996", "5d44a193-04d9-4eba-aa3f-630b8c95b48a");
		Map<String, String> namesUUIDs = UUIDConverter.getUUIDsFromNames(testNamesSeparators.keySet(), true, true);
		assertEquals("The user count of online mode users should match the given amount of users", testNamesSeparators.size(), namesUUIDs.size());
		assertEquals("All user UUIDs should match the given ones with separators", namesUUIDs, testNamesSeparators);
		namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		assertEquals("The user count of online mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
		assertEquals("All user UUIDs should match the given ones with separators", namesUUIDs, testNames);
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection")
	@PrepareForTest({ URL.class, UUIDConverter.class })
	public void testGetUUIDsFromNames() throws Exception
	{
		final Map<String, String> testNames = new TreeMap<>();
		testNames.put(TEST_USER_NAME, TEST_USER_UUID);
		testNames.put("Phei", "8abb0b91429b41e49be8bd659923acd6");
		testNames.put("AFKMaster", "175c57e4cd4b4fb3bfea1c28d094f5dc");
		testNames.put("CleoMalika", "fc4b363ba4474ab98778d0ee353151ee");
		testNames.put("Ghetto1996", "5d44a19304d94ebaaa3f630b8c95b48a");
		final Map<String, String> testNamesSeparators = new TreeMap<>();
		testNamesSeparators.put(TEST_USER_NAME, TEST_USER_UUID_SEPARATORS);
		testNamesSeparators.put("Phei", "8abb0b91-429b-41e4-9be8-bd659923acd6");
		testNamesSeparators.put("AFKMaster", "175c57e4-cd4b-4fb3-bfea-1c28d094f5dc");
		testNamesSeparators.put("CleoMalika", "fc4b363b-a447-4ab9-8778-d0ee353151ee");
		testNamesSeparators.put("Ghetto1996", "5d44a193-04d9-4eba-aa3f-630b8c95b48a");
		Field uuidCache = UUIDConverter.class.getDeclaredField("UUID_CACHE");
		uuidCache.setAccessible(true);
		Field modifiers = uuidCache.getClass().getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(uuidCache, uuidCache.getModifiers() & ~Modifier.FINAL);
		UUIDCacheMap currentCacheMap = (UUIDCacheMap) uuidCache.get(this);
		UUIDCacheMap mockedUUIDCacheMap = mock(UUIDCacheMap.class);
		when(mockedUUIDCacheMap.containsKey(anyString())).thenAnswer(new Answer<Boolean>()
		{
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock)
			{
				//noinspection SuspiciousMethodCalls
				return testNamesSeparators.containsKey(invocationOnMock.getArguments()[0]);
			}
		});
		when(mockedUUIDCacheMap.get(anyString())).thenAnswer(new Answer<String>()
		{
			@Override
			public String answer(InvocationOnMock invocationOnMock)
			{
				//noinspection SuspiciousMethodCalls
				return testNamesSeparators.get(invocationOnMock.getArguments()[0]);
			}
		});
		uuidCache.set(this, mockedUUIDCacheMap);
		Map<String, String> namesUUIDs = UUIDConverter.getUUIDsFromNames(testNamesSeparators.keySet(), true, true);
		assertEquals("The user count of online mode users should match the given amount of users", testNamesSeparators.size(), namesUUIDs.size());
		assertEquals("All user UUIDs should match the given ones with separators", namesUUIDs, testNamesSeparators);
		when(mockedUUIDCacheMap.containsKey(anyString())).thenAnswer(new Answer<Boolean>()
		{
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock)
			{
				//noinspection SuspiciousMethodCalls
				return testNames.containsKey(invocationOnMock.getArguments()[0]);
			}
		});
		when(mockedUUIDCacheMap.get(anyString())).thenAnswer(new Answer<String>()
		{
			@Override
			public String answer(InvocationOnMock invocationOnMock)
			{
				//noinspection SuspiciousMethodCalls
				return testNames.get(invocationOnMock.getArguments()[0]);
			}
		});
		namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		assertEquals("The user count of online mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
		assertEquals("All user UUIDs should match the given ones", namesUUIDs, testNames);
		namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), false, false);
		assertEquals("The user count of offline mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
		assertTrue("All user names should exist in the map", namesUUIDs.keySet().containsAll(testNames.keySet()));
		uuidCache.set(this, currentCacheMap);
		namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		assertEquals("The user count of online mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
		assertTrue("All user names should exist in the map when using no cache", namesUUIDs.keySet().containsAll(testNames.keySet()));
		URL mockedURL = PowerMockito.mock(URL.class);
		whenNew(URL.class).withArguments(anyString()).thenReturn(mockedURL);
		HttpURLConnection mockedHttpURLConnection = mock(HttpURLConnection.class);
		PowerMockito.doReturn(429).doThrow(new IOException()).when(mockedHttpURLConnection).getResponseCode();
		PowerMockito.doThrow(new IOException()).when(mockedHttpURLConnection).getOutputStream();
		PowerMockito.doReturn(mockedHttpURLConnection).when(mockedURL).openConnection();
		Field queryRetryTime = TestUtils.setAccessible(UUIDConverter.class, null, "MOJANG_QUERY_RETRY_TIME", 10);
		UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		TestUtils.setUnaccessible(queryRetryTime, null, true);
		assertTrue("A message should be written to System.out when an error occurs", outputStream.size() > 0);
		uuidCache.setAccessible(false);
	}

	@Test
	@PrepareForTest({ URL.class, UUIDConverter.class })
	public void testNameHistory() throws Exception
	{
		UUIDConverter.NameChange[] nameChanges = UUIDConverter.getNamesFromUUID(TEST_USER2_UUID);
		assertTrue("There should be at least 2 name changes (2 are already known)", nameChanges.length >= 2);
		assertEquals("The first name of the user should match", TEST_USER2_NAME_OG, nameChanges[0].name);
		assertEquals("The username after the fifth name change should match", "Watchdog", nameChanges[5].name);
		nameChanges = UUIDConverter.getNamesFromUUID(TEST_USER2_UUID_AS_UUID);
		assertTrue("There should be at least 2 name changes (2 are already known)", nameChanges.length >= 2);
		assertEquals("The first name of the user should match", TEST_USER2_NAME_OG, nameChanges[0].name);
		assertEquals("The username after the fifth name change should match", "Watchdog", nameChanges[5].name);
		assertNull("The invalid UUID should return null", UUIDConverter.getNamesFromUUID("123456"));
		URL mockedURL = PowerMockito.mock(URL.class);
		whenNew(URL.class).withAnyArguments().thenReturn(mockedURL);
		PowerMockito.doThrow(new IOException("HTTP response code: 429")).when(mockedURL).openConnection();
		UUIDConverter.getNamesFromUUID(TEST_USER2_UUID_AS_UUID);
		int outputStreamSize = outputStream.size();
		int errorStreamSize = errorStream.size();
		assertTrue("A message should be written to the console if an error occurs", outputStreamSize > 0);
		assertTrue("An exception should be thrown if an error occurs", errorStreamSize > 0);
		PowerMockito.doThrow(new IOException("HTTP response code: 400")).when(mockedURL).openConnection();
		UUIDConverter.getNamesFromUUID(TEST_USER2_UUID_AS_UUID);
		assertTrue("A message should be written to the console if an error occurs", outputStream.size() > outputStreamSize);
		assertTrue("An exception should be thrown if an error occurs", errorStream.size() > errorStreamSize);
	}

	@Test
	public void testNameChangedNameToUUID()
	{
		assertEquals("UUID for " + TEST_USER2_NAME_OG + " on \"" + TEST_USER2_LAST_SEEN.toString() + "\" is expected to be " + TEST_USER2_UUID, TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_OG, true, TEST_USER2_LAST_SEEN));
		assertEquals("UUID for " + TEST_USER2_NAME_NEW + " on \"" + TODAY.toString() + "\" is expected to be " + TEST_USER2_UUID, TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_NEW, true, TODAY));
		assertEquals("The UUID should match", TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_OG, true, false, TEST_USER2_LAST_SEEN));
	}

	@Test
	public void testNameChangedNameToUUIDAsUUID()
	{
		assertEquals("The UUID should match", UUID.fromString(TEST_USER2_UUID_SEPARATORS), UUIDConverter.getUUIDFromNameAsUUID(TEST_USER2_NAME_OG, true, TEST_USER2_LAST_SEEN));
		assertEquals("The UUID should match", UUID.fromString(TEST_USER2_UUID_SEPARATORS), UUIDConverter.getUUIDFromNameAsUUID(TEST_USER2_NAME_NEW, true, TODAY));
		assertEquals("The UUID should match", UUID.fromString(TEST_USER2_UUID_SEPARATORS), UUIDConverter.getUUIDFromNameAsUUID(TEST_USER2_NAME_OG, true, false, TEST_USER2_LAST_SEEN));
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection,unchecked")
	public void testInternalClasses() throws Exception
	{
		Object object;
		ReflectionFactory reflection = ReflectionFactory.getReflectionFactory();
		Constructor<?> constructor;
		for(Class clazz : UUIDConverter.class.getDeclaredClasses())
		{
			constructor = reflection.newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor());
			object = constructor.newInstance();
			switch(clazz.getName())
			{
				case "at.pcgamingfreaks.UUIDConverter$NameChange":
					assertNotNull("The change date should not be null", clazz.getDeclaredMethod("getChangeDate").invoke(object));
					break;
				case "at.pcgamingfreaks.UUIDConverter$Profile":
					assertNotNull("The object should not be null", object);
					break;
				case "at.pcgamingfreaks.UUIDConverter$CacheData":
					SimpleDateFormat mockedDateFormat = mock(SimpleDateFormat.class);
					doThrow(new ParseException("", 0)).when(mockedDateFormat).parse(anyString());
					whenNew(SimpleDateFormat.class).withAnyArguments().thenReturn(mockedDateFormat);
					Field expiresOn = clazz.getDeclaredField("expiresOn");
					expiresOn.set(object, "2016-10-20 16:23:12 Z");
					assertNotNull("The change date should not be null", clazz.getDeclaredMethod("getExpiresDate").invoke(object));
					break;
			}
		}
	}

	@AfterClass
	public static void cleanupTestData()
	{
		System.setOut(System.out);
		System.setErr(System.err);
		//noinspection ResultOfMethodCallIgnored,SpellCheckingInspection
		new File("usercache.json").delete();
	}
}