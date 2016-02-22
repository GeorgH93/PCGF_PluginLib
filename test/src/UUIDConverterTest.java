/*
 * Copyright (C) 2016 GeorgH93
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

import at.pcgamingfreaks.UUIDConverter;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UUIDConverterTest
{
	private final static String TEST_USER_NAME = "GeorgH93", TEST_USER_UUID = "6c99e2b55c9e4663b4db7ad3bc52d28d", TEST_USER_UUID_SEPARATORS = "6c99e2b5-5c9e-4663-b4db-7ad3bc52d28d";
	private final static String TEST_USER_OFFLINE_UUID = "05015780f9dc3a409e1dfd4c4f9e20f1";

	@Test
	public void testNameToUUID()
	{
		assertEquals(TEST_USER_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, false)); // Without separators
		assertEquals(TEST_USER_UUID_SEPARATORS, UUIDConverter.getUUIDFromName(TEST_USER_NAME, true, true));  // With separators
		assertEquals(TEST_USER_OFFLINE_UUID, UUIDConverter.getUUIDFromName(TEST_USER_NAME, false, false));  // Offline without separators
	}

	@Test
	public void testUUIDtoName() // We only have to test the offline mode
	{
		assertEquals(TEST_USER_NAME, UUIDConverter.getNameFromUUID(TEST_USER_UUID)); // Without separators
		assertEquals(TEST_USER_NAME, UUIDConverter.getNameFromUUID(TEST_USER_UUID_SEPARATORS));  // With separators
	}

	private final static String TEST_USER2_NAME_NEW = "Watchdog", TEST_USER2_NAME_OG = "rzrct_", TEST_USER2_UUID = "4ca6d49d8d80429fa7a4bcce9f9e4854";
	private final static Date TEST_USER2_LAST_SEEN = new Date(1423214002000L), TODAY = new Date(1456071840000L);

	@Test
	public void testNameChangedNameToUUID()
	{
		assertEquals("UUID for " + TEST_USER2_NAME_OG + " on \"" + TEST_USER2_LAST_SEEN.toString() + "\" is expected to be " + TEST_USER2_UUID,
				TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_OG, true, TEST_USER2_LAST_SEEN)); // Test with the old name of the player and the date we have seen him the last time
		assertEquals("UUID for " + TEST_USER2_NAME_NEW + " on \"" + TODAY.toString() + "\" is expected to be " + TEST_USER2_UUID,
				TEST_USER2_UUID, UUIDConverter.getUUIDFromName(TEST_USER2_NAME_NEW, true, TODAY)); // Test with the new name of the player
	}

	@Test
	public void testNameChangedUUIDtoName()
	{
		UUIDConverter.NameChange[] nameChanges = UUIDConverter.getNamesFromUUID(TEST_USER2_UUID);
		assertTrue(nameChanges.length >= 2); // Maybe he changes his name again
		assertEquals(TEST_USER2_NAME_OG, nameChanges[0].name); // His first name
		assertEquals(TEST_USER2_NAME_NEW, nameChanges[1].name); // His current name when writing this tests
	}
}