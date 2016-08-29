/*
 *   Copyright (C) 2016 GeorgH93
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

import org.junit.*;
import org.junit.rules.Timeout;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UUIDConverterTest
{
	@Rule
	public PowerMockRule powerMock = new PowerMockRule();

	@Rule
	public Timeout globalTimeout = Timeout.seconds(10);

	private final static String TEST_USER_NAME = "GeorgH93", TEST_USER_UUID = "6c99e2b55c9e4663b4db7ad3bc52d28d", TEST_USER_UUID_SEPARATORS = "6c99e2b5-5c9e-4663-b4db-7ad3bc52d28d";
	private final static String TEST_USER_OFFLINE_UUID = "05015780f9dc3a409e1dfd4c4f9e20f1", TEST_USER_OFFLINE_UUID_SEPARATORS = "05015780-f9dc-3a40-9e1d-fd4c4f9e20f1";
	private final static UUID TEST_USER_UUID_AS_UUID = UUID.fromString(TEST_USER_UUID_SEPARATORS), TEST_USER_OFFLINE_UUID_AS_UUID = UUID.fromString(TEST_USER_OFFLINE_UUID_SEPARATORS);

	@SuppressWarnings("SpellCheckingInspection")
	private final static String TEST_USER2_NAME_NEW = "Watchdog", TEST_USER2_NAME_OG = "rzrct_";
	@SuppressWarnings("SpellCheckingInspection")
	private final static String TEST_USER2_UUID = "4ca6d49d8d80429fa7a4bcce9f9e4854", TEST_USER2_UUID_SEPARATORS = "4ca6d49d-8d80-429f-a7a4-bcce9f9e4854";
	private final static UUID TEST_USER2_UUID_AS_UUID = UUID.fromString(TEST_USER2_UUID_SEPARATORS);
	private final static Date TEST_USER2_LAST_SEEN = new Date(1423214002000L), TODAY = new Date(1456071840000L);

	private static ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
	private static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	@BeforeClass
	public static void prepareTestData()
	{
		//noinspection SpellCheckingInspection
		try(FileWriter fileWriter = new FileWriter("usercache.json"))
		{
			//noinspection SpellCheckingInspection
			fileWriter.write("[{\"name\":\"VoidcrafterHD\",\"uuid\":\"4ad6ef2a-7473-46bc-b15d-ec2ee61bc1b6\",\"expiresOn\":\"" + (new SimpleDateFormat("yyyy-MM-dd kk:mm:ss Z").format(new Date(new Date().getTime() + 10000000))) + "\"},{}]");
			fileWriter.flush();
			fileWriter.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.setOut(new PrintStream(outputStream));
		System.setErr(new PrintStream(errorStream));
		new UUIDConverter();
		assertEquals("UUIDConverter should initialize with 3 messages", 3, outputStream.toString().split("\n").length);
		assertTrue("UUIDConverter should throw an error", errorStream.size() > 0);
		//noinspection SpellCheckingInspection
		assertEquals("UUIDConverter should contain file data", UUIDConverter.getUUIDFromName("VoidCrafterHD", true, true), "4ad6ef2a-7473-46bc-b15d-ec2ee61bc1b6");
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

	/*@PrepareForTest({ HttpURLConnection.class, URL.class, UUIDCacheMap.class, UUIDConverter.class })
	@Test
	public void testGetOnlineUUID() throws Exception
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
		whenNew(URL.class).withParameterTypes(String.class).withArguments(anyString()).thenThrow(new MalformedURLException());
		UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, null);
		assertTrue("An error should be printed when a malformed URL occurs", errorStream.toString().contains("MalformedURLException"));
		URL mockedURL = mock(URL.class);
		whenNew(URL.class).withAnyArguments().thenReturn(mockedURL);
		when(mockedURL.openStream()).thenThrow(new IOException("HTTP response code: 429"));
		UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, null);
		assertTrue("An error should be printed when the URL can't open the stream", errorStream.toString().contains("IOException"));
		doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		}).when(mockedURL).openStream();
		UUIDConverter.getUUIDFromName(TEST_USER2_NAME_NEW, true, TEST_USER2_LAST_SEEN);
		assertTrue("A message should be printed when there doesn't exist a user at the given time", outputStream.size() > 0);
		uuidCache.set(this, currentCacheMap);
		uuidCache.setAccessible(false);
	}*/

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
		assertEquals("UUID of the non existent user name should be null", null, UUIDConverter.getUUIDFromName("UltraLongUserName", true, false, false));
		assertEquals("UUID of the non existent user name should be correct", "946a57d3b7a3325480a82b2c927133f8", UUIDConverter.getUUIDFromName("UltraLongUserName", true, false, true));
	}

	@Test
	public void testGetUUIDObjectFromName()
	{
		assertEquals("The UUID of the user should match the online UUID", TEST_USER_UUID_AS_UUID, UUIDConverter.getUUIDFromNameAsUUID(TEST_USER_NAME, true));
		assertEquals("The UUID of the user should match the offline UUID", TEST_USER_OFFLINE_UUID_AS_UUID, UUIDConverter.getUUIDFromNameAsUUID(TEST_USER_NAME, false));
		assertEquals("The UUID object should be null when no UUID could be found online", null, UUIDConverter.getUUIDFromNameAsUUID("UltraLongUserName", true, false));
		assertEquals("The UUID object should match the offline UUID when no UUID could be found online", UUID.fromString("946a57d3-b7a3-3254-80a8-2b2c927133f8"), UUIDConverter.getUUIDFromNameAsUUID("UltraLongUserName", true, true));
		assertEquals("The UUID of the user should match the online UUID when using the given date", TEST_USER2_UUID_AS_UUID, UUIDConverter.getUUIDFromNameAsUUID(TEST_USER2_NAME_NEW, true, TEST_USER2_LAST_SEEN));
	}

	/*@PrepareForTest({ URL.class, UUIDCacheMap.class, UUIDConverter.class })
	@SuppressWarnings("SpellCheckingInspection")
	@Test
	public void testGetUUIDsFromNames() throws Exception
	{
		final Map<String, String> testNames = new TreeMap<>();
		testNames.put(TEST_USER_NAME, TEST_USER_UUID);
		testNames.put("Phei", "8abb0b91429b41e49be8bd659923acd6");
		testNames.put("AFKMaster", "175c57e4cd4b4fb3bfea1c28d094f5dc");
		testNames.put("CleoMalika", "fc4b363ba4474ab98778d0ee353151ee");
		testNames.put("Ghetto1996", "5d44a19304d94ebaaa3f630b8c95b48a");
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
			public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				//noinspection SuspiciousMethodCalls
				return testNames.containsKey(invocationOnMock.getArguments()[0]);
			}
		});
		when(mockedUUIDCacheMap.get(anyString())).thenAnswer(new Answer<String>()
		{
			@Override
			public String answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				//noinspection SuspiciousMethodCalls
				return testNames.get(invocationOnMock.getArguments()[0]);
			}
		});
		uuidCache.set(this, mockedUUIDCacheMap);
		Map<String, String> namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		assertEquals("The user count of online mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
		assertTrue("All user UUIDs should match the given ones", namesUUIDs.equals(testNames));
		namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), false, false);
		assertEquals("The user count of offline mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
		assertTrue("All user names should exist in the map", namesUUIDs.keySet().containsAll(testNames.keySet()));
		uuidCache.set(this, currentCacheMap);
		namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		assertEquals("The user count of online mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
		assertTrue("All user names should exist in the map when using no cache", namesUUIDs.keySet().containsAll(testNames.keySet()));
		URL mockedURL = mock(URL.class);
		whenNew(URL.class).withAnyArguments().thenReturn(mockedURL);
		HttpURLConnection mockedHttpURLConnection = mock(HttpURLConnection.class);
		when(mockedURL.openConnection()).thenReturn(mockedHttpURLConnection);
		when(mockedHttpURLConnection.getResponseCode()).thenReturn(429);
		when(mockedHttpURLConnection.getOutputStream()).thenThrow(new IOException());
		spy(Thread.class);
		doThrow(new InterruptedException()).when(Thread.class);
		Thread.sleep(anyLong());
		UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		assertTrue("A message should be written to System.out when an error occurs", outputStream.size() > 0);
		assertTrue("An exception should be handled when it occurs", errorStream.size() > 0);
		uuidCache.setAccessible(false);
	}*/

	/*@Test
	public void testNameHistory() throws Exception
	{
		UUIDConverter.NameChange[] nameChanges = UUIDConverter.getNamesFromUUID(TEST_USER2_UUID);
		assertTrue("There should be at least 2 name changes (2 are already known)", nameChanges.length >= 2);
		assertEquals("The first name of the user should match", TEST_USER2_NAME_OG, nameChanges[0].name);
		assertEquals("The username after the first name change should match", TEST_USER2_NAME_NEW, nameChanges[1].name);
		nameChanges = UUIDConverter.getNamesFromUUID(TEST_USER2_UUID_AS_UUID);
		assertTrue("There should be at least 2 name changes (2 are already known)", nameChanges.length >= 2);
		assertEquals("The first name of the user should match", TEST_USER2_NAME_OG, nameChanges[0].name);
		assertEquals("The username after the first name change should match", TEST_USER2_NAME_NEW, nameChanges[1].name);
		assertNull("The invalid UUID should return null", UUIDConverter.getNamesFromUUID("123456"));
		URL mockedURL = mock(URL.class);
		whenNew(URL.class).withAnyArguments().thenReturn(mockedURL);
		when(mockedURL.openConnection()).thenThrow(new IOException("HTTP response code: 429"));
		UUIDConverter.getNamesFromUUID(TEST_USER2_UUID_AS_UUID);
		assertTrue("A message should be written to the console if an error occurs", outputStream.size() > 0);
		assertTrue("An exception should be thrown if an error occurs", errorStream.size() > 0);
	}*/

	@AfterClass
	public static void cleanupTestData()
	{
		System.setOut(System.out);
		System.setErr(System.err);
		//noinspection ResultOfMethodCallIgnored,SpellCheckingInspection
		new File("usercache.json").delete();
	}

	/*
	@Test
	public void testNameChangedNameToUUID()
	{
		assertEquals("UUID for " + TEST_USER2_NAME_OG + " on \"" + TEST_USER2_LAST_SEEN.toString() + "\" is expected to be " + TEST_USER2_UUID, TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_OG, true, TEST_USER2_LAST_SEEN)); // Test with the old name of the player and the date we have seen him the last time
		assertEquals("UUID for " + TEST_USER2_NAME_NEW + " on \"" + TODAY.toString() + "\" is expected to be " + TEST_USER2_UUID, TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_NEW, true, TODAY)); // Test with the new name of the player
		assertEquals(TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_OG, true, false, TEST_USER2_LAST_SEEN));
	}

	@Test
	public void testNameChangedNameToUUIDAsUUID()
	{
		assertEquals(UUID.fromString(TEST_USER2_UUID_SEPARATORS), UUIDConverter.getUUIDFromNameAsUUID(TEST_USER2_NAME_OG, true, TEST_USER2_LAST_SEEN)); // Test with the old name of the player and the date we have seen him the last time
		assertEquals(UUID.fromString(TEST_USER2_UUID_SEPARATORS), UUIDConverter.getUUIDFromNameAsUUID(TEST_USER2_NAME_NEW, true, TODAY)); // Test with the new name of the player
		assertEquals(UUID.fromString(TEST_USER2_UUID_SEPARATORS), UUIDConverter.getUUIDFromNameAsUUID(TEST_USER2_NAME_OG, true, false, TEST_USER2_LAST_SEEN));
	}*/
}