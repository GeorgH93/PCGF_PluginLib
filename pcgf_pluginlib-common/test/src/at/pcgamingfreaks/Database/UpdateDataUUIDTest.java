/*
 *   Copyright (C) 2026 GeorgH93
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

package at.pcgamingfreaks.Database;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for {@link UpdateDataUUID}, focusing on the UUID formatting logic
 * used while fixing UUIDs stored in the wrong format.
 */
public class UpdateDataUUIDTest
{
	private static final String UUID_WITH_SEPARATORS    = "069a79f4-4c9a-4efc-a9b1-6ab5a2f1b1a9";
	private static final String UUID_WITHOUT_SEPARATORS = "069a79f44c9a4efca9b16ab5a2f1b1a9";

	@Test
	public void testFormatUuidWithSeparatorsKeepsSeparatedUuid()
	{
		UpdateDataUUID updateData = new UpdateDataUUID("TestUser", UUID_WITH_SEPARATORS, 1);
		assertEquals(UUID_WITH_SEPARATORS, updateData.formatUuid(true));
	}

	@Test
	public void testFormatUuidWithSeparatorsAddsSeparatorsToCompactUuid()
	{
		UpdateDataUUID updateData = new UpdateDataUUID("TestUser", UUID_WITHOUT_SEPARATORS, 2);
		assertEquals(UUID_WITH_SEPARATORS, updateData.formatUuid(true));
	}

	@Test
	public void testFormatUuidWithoutSeparatorsRemovesSeparators()
	{
		UpdateDataUUID updateData = new UpdateDataUUID("TestUser", UUID_WITH_SEPARATORS, 3);
		assertEquals(UUID_WITHOUT_SEPARATORS, updateData.formatUuid(false));
	}

	@Test
	public void testFormatUuidWithoutSeparatorsKeepsCompactUuid()
	{
		UpdateDataUUID updateData = new UpdateDataUUID("TestUser", UUID_WITHOUT_SEPARATORS, 4);
		assertEquals(UUID_WITHOUT_SEPARATORS, updateData.formatUuid(false));
	}

	@Test
	public void testFormatUuidRoundTripPreservesValue()
	{
		UpdateDataUUID updateData = new UpdateDataUUID("TestUser", UUID_WITH_SEPARATORS, 5);
		String compact = updateData.formatUuid(false);
		assertEquals(UUID_WITHOUT_SEPARATORS, compact);
		updateData.setUuid(compact);
		assertEquals(UUID_WITH_SEPARATORS, updateData.formatUuid(true));
	}

	@Test
	public void testFormatUuidLeavesNonUuidValuesUnchanged()
	{
		// The separator insertion regex only matches 8-4-4-4-12 word character groups,
		// values not matching that pattern are returned as they are.
		UpdateDataUUID updateData = new UpdateDataUUID("TestUser", "not-a-valid-uuid", 6);
		assertEquals("not-a-valid-uuid", updateData.formatUuid(true));
		updateData.setUuid("short");
		assertEquals("short", updateData.formatUuid(true));
		assertEquals("short", updateData.formatUuid(false));
	}

	@Test
	public void testGetterAndSetter()
	{
		UpdateDataUUID updateData = new UpdateDataUUID("TestUser", UUID_WITH_SEPARATORS, 42);
		assertEquals("TestUser", updateData.getName());
		assertEquals(42L, updateData.getId());
		assertEquals(UUID_WITH_SEPARATORS, updateData.getUuid());
		updateData.setUuid(UUID_WITHOUT_SEPARATORS);
		assertEquals(UUID_WITHOUT_SEPARATORS, updateData.getUuid());
		assertNotEquals(UUID_WITH_SEPARATORS, updateData.getUuid());
	}
}
