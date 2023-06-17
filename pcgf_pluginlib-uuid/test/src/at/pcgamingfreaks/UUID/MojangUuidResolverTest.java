/*
 *   Copyright (C) 2022 GeorgH93
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

package at.pcgamingfreaks.UUID;

import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MojangUuidResolverTest
{
	private static final String TEST_USER_NAME = "GeorgH93";
	private static final String TEST_USER_OFFLINE_UUID = "05015780f9dc3a409e1dfd4c4f9e20f1", TEST_USER_OFFLINE_UUID_SEPARATORS = "05015780-f9dc-3a40-9e1d-fd4c4f9e20f1";
	private static final UUID TEST_USER_UUID = UUID.fromString("6c99e2b5-5c9e-4663-b4db-7ad3bc52d28d"), TEST_USER_OFFLINE_UUID_AS_UUID = UUID.fromString(TEST_USER_OFFLINE_UUID_SEPARATORS);

	private static final String TEST_USER2_NAME_NEW = "Vaunan", TEST_USER2_NAME_OG = "ReedtheRed";
	private static final String TEST_USER2_UUID = "8f54523078d5474bbbea693964467ef0", TEST_USER2_UUID_SEPARATORS = "8f545230-78d5-474b-bbea-693964467ef0";
	private static final UUID TEST_USER2_UUID_AS_UUID = UUID.fromString(TEST_USER2_UUID_SEPARATORS);
	private static final Date TEST_USER2_LAST_SEEN = new Date(1423214002000L), TODAY = new Date(1456071840000L);

	@Test
	public void testGetNameFromUUID()
	{
		MojangUuidResolver resolver = new MojangUuidResolver(null, null);
		assertEquals("Username should be retrieved correctly from the uuid", TEST_USER_NAME, resolver.getName(TEST_USER_UUID));
		assertEquals("unknown", resolver.getName(UUID.fromString("00000000-0000-0000-0000-000000000000")));
	}
}