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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UUIDCacheMapTest
{
	private static final String TEST_USER_NAME = "GeorgH93", TEST_USER_UUID = "6c99e2b55c9e4663b4db7ad3bc52d28d", TEST_USER_UUID_SEPARATORS = "6c99e2b5-5c9e-4663-b4db-7ad3bc52d28d";
	@SuppressWarnings("SpellCheckingInspection")
	private static final String TEST_USER2_NAME = "Watchdog", TEST_USER2_UUID = "4ca6d49d8d80429fa7a4bcce9f9e4854", TEST_USER2_UUID_SEPARATORS = "4ca6d49d-8d80-429f-a7a4-bcce9f9e4854";
	@SuppressWarnings("SpellCheckingInspection")
	private static final String TEST_USER3_NAME = "CleoMalika", TEST_USER3_UUID = "fc4b363ba4474ab98778d0ee353151ee", TEST_USER3_UUID_SEPARATORS = "fc4b363b-a447-4ab9-8778-d0ee353151ee";
	@SuppressWarnings("SpellCheckingInspection")
	private static final String TEST_USER4_NAME = "AFKMaster", TEST_USER4_UUID = "175c57e4cd4b4fb3bfea1c28d094f5dc", TEST_USER4_UUID_SEPARATORS = "175c57e4-cd4b-4fb3-bfea-1c28d094f5dc";

	@Test
	public void testStandard()
	{
		UUIDCacheMap map = new UUIDCacheMap();
		map.put(TEST_USER_NAME, TEST_USER_UUID);
		map.put(TEST_USER2_NAME, TEST_USER2_UUID);
		map.put(TEST_USER3_NAME, TEST_USER3_UUID);
		assertEquals("The UUID of a not inserted name should be null", null, map.get(TEST_USER4_NAME));
		assertEquals("The UUID should match the correct one", TEST_USER_UUID, map.get(TEST_USER_NAME));
		assertEquals("The UUID should match the correct one", TEST_USER2_UUID, map.get(TEST_USER2_NAME));
		assertEquals("The UUID should match the correct one", TEST_USER3_UUID, map.get(TEST_USER3_NAME));
	}

	@Test
	public void testGetMixedCase()
	{
		UUIDCacheMap map = new UUIDCacheMap();
		map.put(TEST_USER_NAME, TEST_USER_UUID);
		map.put(TEST_USER2_NAME, TEST_USER2_UUID);
		map.put(TEST_USER3_NAME, TEST_USER3_UUID);
		assertEquals("The UUID of a not inserted name should be null", null, map.get(TEST_USER4_NAME.toUpperCase()));
		assertEquals("The UUID should match the correct one", TEST_USER_UUID, map.get(TEST_USER_NAME.toUpperCase()));
		assertEquals("The UUID should match the correct one", TEST_USER2_UUID, map.get(TEST_USER2_NAME.toUpperCase()));
		assertEquals("The UUID should match the correct one", TEST_USER3_UUID, map.get(TEST_USER3_NAME.toUpperCase()));
		//noinspection SuspiciousMethodCalls
		assertEquals("The UUID of an invalid key should return null", null, map.get(3));
	}

	@Test
	public void testRemove()
	{
		final UUIDCacheMap map = new UUIDCacheMap();
		map.put(TEST_USER_NAME, TEST_USER_UUID);
		map.put(TEST_USER2_NAME, TEST_USER2_UUID);
		map.put(TEST_USER3_NAME, TEST_USER3_UUID);
		assertEquals("The UUID of a not inserted name should be null", null, map.get(TEST_USER4_NAME));
		assertEquals("The UUID should match the correct one", TEST_USER_UUID, map.get(TEST_USER_NAME));
		assertEquals("The UUID should match the correct one", TEST_USER2_UUID, map.get(TEST_USER2_NAME));
		assertEquals("The UUID should match the correct one", TEST_USER3_UUID, map.get(TEST_USER3_NAME));
		map.remove(TEST_USER2_NAME);
		assertEquals("The UUID of a not inserted name should be null", null, map.get(TEST_USER4_NAME));
		assertEquals("The UUID should match the correct one", TEST_USER_UUID, map.get(TEST_USER_NAME));
		assertEquals("The UUID of the removed user should be null", null, map.get(TEST_USER2_NAME));
		assertEquals("The UUID should match the correct one", TEST_USER3_UUID, map.get(TEST_USER3_NAME));
		assertEquals("The removed UUID should be returned", TEST_USER_UUID, map.remove(TEST_USER_NAME));
		assertEquals("The UUID of the not inserted element that should be remove should be null", null, map.remove(TEST_USER_NAME));
		assertEquals("The UUID of a not inserted name should be null", null, map.get(TEST_USER4_NAME));
		assertEquals("The UUID of a not inserted name should be null", null, map.get(TEST_USER_NAME));
		assertEquals("The UUID of a not inserted name should be null", null, map.get(TEST_USER2_NAME));
		assertEquals("The UUID should match the correct one", TEST_USER3_UUID, map.get(TEST_USER3_NAME));
		//noinspection SuspiciousMethodCalls
		assertEquals("The removal of an integer element should return null", null, map.remove(5));
	}

	@Test
	public void testPutAll()
	{
		UUIDCacheMap map = new UUIDCacheMap();
		map.put(TEST_USER_NAME, TEST_USER_UUID);
		map.put(TEST_USER2_NAME, TEST_USER2_UUID);
		UUIDCacheMap map2 = new UUIDCacheMap();
		map2.put(TEST_USER2_NAME.toUpperCase(), TEST_USER2_UUID);
		map2.put(TEST_USER3_NAME, TEST_USER3_UUID);
		map.putAll(map2);
		assertEquals("A not inserted user should return null as UUID", null, map.get(TEST_USER4_NAME));
		assertEquals("The UUID of the inserted players should be returned correctly", TEST_USER_UUID, map.get(TEST_USER_NAME));
		assertEquals("The UUID of the inserted players should be returned correctly", TEST_USER2_UUID, map.get(TEST_USER2_NAME));
		assertEquals("The UUID of the inserted players should be returned correctly", TEST_USER3_UUID, map.get(TEST_USER3_NAME));
	}

	@Test
	public void testContains()
	{
		UUIDCacheMap map = new UUIDCacheMap();
		map.put(TEST_USER_NAME, TEST_USER_UUID);
		map.put(TEST_USER2_NAME, TEST_USER2_UUID);
		map.put(TEST_USER3_NAME, TEST_USER3_UUID);
		assertFalse("A not contained UUID should return false", map.contains(TEST_USER4_UUID));
		assertTrue("A contained UUID should return true", map.contains(TEST_USER_UUID));
		assertTrue("A contained UUID should return true", map.contains(TEST_USER2_UUID));
		assertTrue("A contained UUID should return true", map.contains(TEST_USER3_UUID));
		assertTrue("A contained UUID with separators should return true", map.contains(TEST_USER_UUID_SEPARATORS));
		assertTrue("A contained UUID with separators should return true", map.contains(TEST_USER2_UUID_SEPARATORS));
		assertTrue("A contained UUID with separators should return true", map.contains(TEST_USER3_UUID_SEPARATORS));
		//noinspection SuspiciousMethodCalls
		assertFalse(map.contains(12));
		//noinspection SuspiciousMethodCalls
		assertFalse(map.contains(35.1));
	}

	@Test
	public void testContainsKey()
	{
		UUIDCacheMap map = new UUIDCacheMap();
		map.put(TEST_USER_NAME, TEST_USER_UUID);
		map.put(TEST_USER2_NAME, TEST_USER2_UUID);
		map.put(TEST_USER3_NAME, TEST_USER3_UUID);
		assertFalse("A not contained name should return false", map.containsKey(TEST_USER4_NAME));
		assertTrue("A contained name should return true", map.containsKey(TEST_USER_NAME));
		assertTrue("A contained name should return true", map.containsKey(TEST_USER2_NAME));
		assertTrue("A contained name should return true", map.containsKey(TEST_USER3_NAME));
		assertTrue("A contained upper case name should return true", map.containsKey(TEST_USER_NAME.toUpperCase()));
		assertTrue("A contained upper case name should return true", map.containsKey(TEST_USER2_NAME.toUpperCase()));
		assertTrue("A contained upper case name should return true", map.containsKey(TEST_USER3_NAME.toUpperCase()));
		//noinspection SuspiciousMethodCalls
		assertFalse(map.containsKey(12));
		//noinspection SuspiciousMethodCalls
		assertFalse(map.containsKey(35.1));
	}

	@Test
	public void testUUIDsWithSeparators()
	{
		UUIDCacheMap map = new UUIDCacheMap();
		map.put(TEST_USER_NAME, TEST_USER_UUID_SEPARATORS);
		map.put(TEST_USER2_NAME, TEST_USER2_UUID_SEPARATORS);
		map.put(TEST_USER4_NAME, TEST_USER4_UUID_SEPARATORS);
		assertEquals("The UUID should be returned correctly", TEST_USER_UUID, map.get(TEST_USER_NAME));
		assertEquals("The UUID should be returned correctly", TEST_USER2_UUID, map.get(TEST_USER2_NAME));
		assertEquals("The UUID should be returned correctly", TEST_USER4_UUID, map.get(TEST_USER4_NAME));
	}
}