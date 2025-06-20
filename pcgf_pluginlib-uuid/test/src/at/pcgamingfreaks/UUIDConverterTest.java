/*
 *   Copyright (C) 2023 GeorgH93
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

import at.pcgamingfreaks.UUID.MojangUuidResolver;
import at.pcgamingfreaks.UUID.UuidCache;
import at.pcgamingfreaks.UUID.UuidConverter;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UUIDConverterTest
{
	private static final String TEST_USER_NAME = "GeorgH93", TEST_USER_UUID = "6c99e2b55c9e4663b4db7ad3bc52d28d", TEST_USER_UUID_SEPARATORS = "6c99e2b5-5c9e-4663-b4db-7ad3bc52d28d";
	private static final String TEST_USER_OFFLINE_UUID = "05015780f9dc3a409e1dfd4c4f9e20f1", TEST_USER_OFFLINE_UUID_SEPARATORS = "05015780-f9dc-3a40-9e1d-fd4c4f9e20f1";
	private static final UUID TEST_USER_UUID_AS_UUID = UUID.fromString(TEST_USER_UUID_SEPARATORS), TEST_USER_OFFLINE_UUID_AS_UUID = UUID.fromString(TEST_USER_OFFLINE_UUID_SEPARATORS);

	private static final String TEST_USER2_NAME_NEW = "Vaunan", TEST_USER2_NAME_OG = "ReedtheRed";
	private static final String TEST_USER2_UUID = "8f54523078d5474bbbea693964467ef0", TEST_USER2_UUID_SEPARATORS = "8f545230-78d5-474b-bbea-693964467ef0";
	private static final UUID TEST_USER2_UUID_AS_UUID = UUID.fromString(TEST_USER2_UUID_SEPARATORS);
	private static final Date TEST_USER2_LAST_SEEN = new Date(1423214002000L), TODAY = new Date(1456071840000L);

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

	@Test
	public void testGetOnlineUUIDFromName() throws Exception
	{
		Field resolver = UUIDConverter.class.getDeclaredField("MOJANG_RESOLVER");
		resolver.setAccessible(true);
		Field modifiers = resolver.getClass().getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(resolver, resolver.getModifiers() & ~Modifier.FINAL);
		UuidCache mockedUUIDCache = mock(UuidCache.class);
		resolver.set(this, new MojangUuidResolver(mockedUUIDCache));
		assertEquals("Username with no time given and no cache should match the current username", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, null));
		assertEquals("Username at the current time with no cache should match the current username", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, TODAY));
		when(mockedUUIDCache.contains(TEST_USER_NAME)).thenReturn(true);
		when(mockedUUIDCache.getUuidFromName(TEST_USER_NAME)).thenReturn(UuidConverter.uuidFromString(TEST_USER_UUID));
		assertEquals("Username with no time given and available cache should match the current username", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, null));
		assertEquals("Username at the current time and available cache should match the current username", TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, TODAY));
		reset(mockedUUIDCache);
		resolver.set(this, new MojangUuidResolver(UuidCache.getSHARED_UUID_CACHE()));
		resolver.setAccessible(false);
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
		//assertEquals("UUID of the online player with the given last seen date should match", TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_NEW, true, TEST_USER2_LAST_SEEN));
		//assertEquals("UUID of the online player with the given last seen date should match the separated one", TEST_USER2_UUID_SEPARATORS, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_NEW, true, true, TEST_USER2_LAST_SEEN));
		assertNull("UUID of the non-existent username should be null", UUIDConverter.getUUIDFromName("UltraLongUserName", true, false, false));
		assertEquals("UUID of the non-existent username should be correct", "946a57d3b7a3325480a82b2c927133f8", UUIDConverter.getUUIDFromName("UltraLongUserName", true, false, true));
	}

	@Test
	public void testGetUUIDObjectFromName()
	{
		assertEquals("The UUID of the user should match the online UUID", TEST_USER_UUID_AS_UUID, UUIDConverter.getUUIDFromNameAsUUID(TEST_USER_NAME, true));
		assertEquals("The UUID of the user should match the offline UUID", TEST_USER_OFFLINE_UUID_AS_UUID, UUIDConverter.getUUIDFromNameAsUUID(TEST_USER_NAME, false));
		assertNull("The UUID object should be null when no UUID could be found online", UUIDConverter.getUUIDFromNameAsUUID("UltraLongUserName", true, false));
		assertEquals("The UUID object should match the offline UUID when no UUID could be found online", UUID.fromString("946a57d3-b7a3-3254-80a8-2b2c927133f8"), UUIDConverter.getUUIDFromNameAsUUID("UltraLongUserName", true, true));
		//assertEquals("The UUID of the user should match the online UUID when using the given date", TEST_USER2_UUID_AS_UUID, UUIDConverter.getUUIDFromNameAsUUID(TEST_USER2_NAME_NEW, true, TEST_USER2_LAST_SEEN));
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection")
	public void testGetUUIDsFromNamesWithoutCache()
	{
		final Map<String, String> testNames = new TreeMap<>();
		testNames.put(TEST_USER_NAME, TEST_USER_UUID);
		testNames.put("Phei", "8abb0b91429b41e49be8bd659923acd6");
		testNames.put("AFKMaster", "175c57e4cd4b4fb3bfea1c28d094f5dc");
		testNames.put("Juuletz", "fc4b363ba4474ab98778d0ee353151ee");
		testNames.put("NotAnt0_", "5d44a19304d94ebaaa3f630b8c95b48a");
		final Map<String, String> testNamesSeparators = new TreeMap<>();
		testNamesSeparators.put(TEST_USER_NAME, TEST_USER_UUID_SEPARATORS);
		testNamesSeparators.put("Phei", "8abb0b91-429b-41e4-9be8-bd659923acd6");
		testNamesSeparators.put("AFKMaster", "175c57e4-cd4b-4fb3-bfea-1c28d094f5dc");
		testNamesSeparators.put("Juuletz", "fc4b363b-a447-4ab9-8778-d0ee353151ee");
		testNamesSeparators.put("NotAnt0_", "5d44a193-04d9-4eba-aa3f-630b8c95b48a");
		Map<String, String> namesUUIDs = UUIDConverter.getUUIDsFromNames(testNamesSeparators.keySet(), true, true);
		assertEquals("All user UUIDs should match the given ones with separators", testNamesSeparators, namesUUIDs);
		namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		assertEquals("All user UUIDs should match the given ones with separators", testNames, namesUUIDs);
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection")
	public void testGetUUIDsWithCleanCache() throws IllegalAccessException, NoSuchFieldException
	{
		/*Field cacheField = Reflection.getField(UUIDConverter.class, "UUID_CACHE");
		UUIDCacheMap oldCache = new UUIDCacheMap();
		oldCache.putAll((UUIDCacheMap) cacheField.get(null));
		((UUIDCacheMap)cacheField.get(null)).clear();

		final Map<String, String> testNamesSeparators = new TreeMap<>();
		testNamesSeparators.put(TEST_USER_NAME, TEST_USER_UUID_SEPARATORS);
		testNamesSeparators.put("Phei", "8abb0b91-429b-41e4-9be8-bd659923acd6");
		testNamesSeparators.put("AFKMaster", "175c57e4-cd4b-4fb3-bfea-1c28d094f5dc");
		testNamesSeparators.put("Juuletz", "fc4b363b-a447-4ab9-8778-d0ee353151ee");
		testNamesSeparators.put("NotAnt0_", "5d44a193-04d9-4eba-aa3f-630b8c95b48a");
		testNamesSeparators.put("Watchdog", "4ec35d4f-609c-4245-8d53-f779c6160dd2");
		testNamesSeparators.put("Artifexus", "27205e77-f8b4-4b31-b9a0-5bd97efc0560");
		testNamesSeparators.put("RatzzFatzz", "7b5f80aa-093e-4057-aa28-3950f2f24d62");
		testNamesSeparators.put("ArchonusIx", "341d63ce-39ba-4664-a02c-ee86375cf316");
		testNamesSeparators.put("oOJoKeROo", "7a560f2e-374f-42a1-9d57-463b25db12a7");
		testNamesSeparators.put("xPaddy93x", "9e25654d-266f-406a-8afe-598024af976f");
		Map<String, String> namesUUIDs = UUIDConverter.getUUIDsFromNames(testNamesSeparators.keySet(), true, true);
		for(Map.Entry<String, String> entry : testNamesSeparators.entrySet())
		{
			assertTrue("Expected profile " + entry.getKey() + " to exist.", namesUUIDs.containsKey(entry.getKey()));
			assertEquals(entry.getValue(), namesUUIDs.get(entry.getKey()));
		}
		assertEquals("The user count of online mode users should match the given amount of users", testNamesSeparators.size(), namesUUIDs.size());

		((UUIDCacheMap)cacheField.get(null)).putAll(oldCache); //Restore old cache*/
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection")
	public void testGetUUIDsFromNames() throws Exception
	{
		final Map<String, String> testNames = new TreeMap<>();
		testNames.put(TEST_USER_NAME, TEST_USER_UUID);
		testNames.put("Phei", "8abb0b91429b41e49be8bd659923acd6");
		testNames.put("AFKMaster", "175c57e4cd4b4fb3bfea1c28d094f5dc");
		testNames.put("Juuletz", "fc4b363ba4474ab98778d0ee353151ee");
		testNames.put("NotAnt0_", "5d44a19304d94ebaaa3f630b8c95b48a");
		final Map<String, String> testNamesSeparators = new TreeMap<>();
		testNamesSeparators.put(TEST_USER_NAME, TEST_USER_UUID_SEPARATORS);
		testNamesSeparators.put("Phei", "8abb0b91-429b-41e4-9be8-bd659923acd6");
		testNamesSeparators.put("AFKMaster", "175c57e4-cd4b-4fb3-bfea-1c28d094f5dc");
		testNamesSeparators.put("Juuletz", "fc4b363b-a447-4ab9-8778-d0ee353151ee");
		testNamesSeparators.put("NotAnt0_", "5d44a193-04d9-4eba-aa3f-630b8c95b48a");
		/*Field uuidCache = UUIDConverter.class.getDeclaredField("UUID_CACHE");
		uuidCache.setAccessible(true);
		Field modifiers = uuidCache.getClass().getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(uuidCache, uuidCache.getModifiers() & ~Modifier.FINAL);
		UUIDCacheMap currentCacheMap = (UUIDCacheMap) uuidCache.get(this);
		UUIDCacheMap mockedUUIDCacheMap = mock(UUIDCacheMap.class);
		when(mockedUUIDCacheMap.containsKey(anyString())).thenAnswer((Answer<Boolean>) invocationOnMock -> {
			//noinspection SuspiciousMethodCalls
			return testNamesSeparators.containsKey(invocationOnMock.getArguments()[0]);
		});
		when(mockedUUIDCacheMap.get(anyString())).thenAnswer((Answer<String>) invocationOnMock -> {
			//noinspection SuspiciousMethodCalls
			return testNamesSeparators.get(invocationOnMock.getArguments()[0]);
		});
		uuidCache.set(this, mockedUUIDCacheMap);
		Map<String, String> namesUUIDs = UUIDConverter.getUUIDsFromNames(testNamesSeparators.keySet(), true, true);
		assertEquals("The user count of online mode users should match the given amount of users", testNamesSeparators.size(), namesUUIDs.size());
		assertEquals("All user UUIDs should match the given ones with separators", namesUUIDs, testNamesSeparators);
		when(mockedUUIDCacheMap.containsKey(anyString())).thenAnswer((Answer<Boolean>) invocationOnMock -> {
			//noinspection SuspiciousMethodCalls
			return testNames.containsKey(invocationOnMock.getArguments()[0]);
		});
		when(mockedUUIDCacheMap.get(anyString())).thenAnswer((Answer<String>) invocationOnMock -> {
			//noinspection SuspiciousMethodCalls
			return testNames.get(invocationOnMock.getArguments()[0]);
		});
		namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		assertEquals("All user UUIDs should match the given ones", namesUUIDs, testNames);
		assertEquals("The user count of online mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
		namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), false, false);
		assertTrue("All user names should exist in the map", namesUUIDs.keySet().containsAll(testNames.keySet()));
		assertEquals("The user count of offline mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
		uuidCache.set(this, currentCacheMap);*/
		Map<String, String> namesUUIDs = UUIDConverter.getUUIDsFromNames(testNames.keySet(), true, false);
		assertTrue("All usernames should exist in the map when using no cache", namesUUIDs.keySet().containsAll(testNames.keySet()));
		assertEquals("The user count of online mode users should match the given amount of users", testNames.size(), namesUUIDs.size());
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