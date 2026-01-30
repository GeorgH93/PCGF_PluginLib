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

package at.pcgamingfreaks;

import org.junit.Test;

import static org.junit.Assert.*;

public class VersionTest
{
	static final String VERSION_1 = "1", VERSION_1_0 = "1.0", VERSION_V1_0 = "v1.0", VERSION_V2_0 = "v2.0", VERSION_2_0_SNAPSHOT = "2.0-SNAPSHOT";
	static final String VERSION_1_2_SNAPSHOT = "1.2-SNAPSHOT", VERSION_1_2 = "1.2", VERSION_1_2_BETA = "1.2-Beta", VERSION_1_2_BETA2 = "1.2-BETA2";
	static final String VERSION_1_2_SNAPSHOT_BUILD_5 = "1.2-SNAPSHOT-Build5", VERSION_1_2_SNAPSHOT_BUILD_8 = "1.2-SNAPSHOT-Build=8";
	static final String VERSION_1_2_SNAPSHOT_TIME_201703081212 = "1.2-SNAPSHOT-T201703081212", VERSION_1_2_SNAPSHOT_TIME_201603081212 = "1.2-SNAPSHOT-Timestamp=201603081212";
	static final Version version_1 = new Version(VERSION_1), version_1_0 = new Version(VERSION_1_0), version_v1_0 = new Version(VERSION_V1_0), version_1_2_it = new Version(VERSION_1_2, true);
	static final Version version_1_2_snapshot = new Version(VERSION_1_2_SNAPSHOT), version_1_2 = new Version(VERSION_1_2), version_1_2_snapshot_it = new Version(VERSION_1_2_SNAPSHOT, true);
	static final Version version_1_2_beta = new Version(VERSION_1_2_BETA), version_1_2_beta2 = new Version(VERSION_1_2_BETA2), version_v2_0 = new Version(VERSION_V2_0);
	static final Version version_2_0_snapshot = new Version(VERSION_2_0_SNAPSHOT);
	static final Version version_1_2_snapshot_b_5 = new Version(VERSION_1_2_SNAPSHOT_BUILD_5), version_1_2_snapshot_b_8 = new Version(VERSION_1_2_SNAPSHOT_BUILD_8);
	static final Version version_1_2_snapshot_t_201703081212 = new Version(VERSION_1_2_SNAPSHOT_TIME_201703081212), version_1_2_snapshot_t_201603081212 = new Version(VERSION_1_2_SNAPSHOT_TIME_201603081212);

	@Test
	public void testIsValidVersionString()
	{
		assertTrue("The given string should be a valid version string", Version.isValidVersionString(VERSION_1));
		assertTrue("The given string should be a valid version string", Version.isValidVersionString(VERSION_1_0));
		assertTrue("The given string should be a valid version string", Version.isValidVersionString(VERSION_V1_0));
		assertTrue("The given string should be a valid version string", Version.isValidVersionString(VERSION_1_2));
		assertTrue("The given string should be a valid version string", Version.isValidVersionString(VERSION_1_2_SNAPSHOT));
		assertTrue("The given string should be a valid version string", Version.isValidVersionString(VERSION_1_2_BETA));
		assertTrue("The given string should be a valid version string", Version.isValidVersionString(VERSION_1_2_BETA2));
		assertTrue("The given string should be a valid version string", Version.isValidVersionString(VERSION_V2_0));
		assertTrue("The given string should be a valid version string", Version.isValidVersionString(VERSION_2_0_SNAPSHOT));
		assertTrue("The given string should be a valid version string", Version.isValidVersionString("123.456.789-ABC-def"));
		assertFalse("The given string should not be a valid version string", Version.isValidVersionString("aRandomString"));
		assertFalse("The given string should not be a valid version string", Version.isValidVersionString("k1.0"));
		assertFalse("The given string should not be a valid version string", Version.isValidVersionString("k1.0-test"));
		assertFalse("The given string should not be a valid version string", Version.isValidVersionString("v1.0--alpha"));
	}

	@Test(expected = Version.InvalidVersionStringException.class)
	public void testEmptyVersion()
	{
		new Version("");
	}

	@Test(expected = Version.InvalidVersionStringException.class)
	public void testInvalidVersion()
	{
		new Version("invalid-version");
	}

	@Test
	public void testSpecialVersion()
	{
		assertEquals("The version should match", "1.8.0", new Version("1.8.0").toString());
		assertEquals("The version should match", "1.8-alpha-snapshot", new Version("1.8-alpha-snapshot").toString());
	}

	@Test
	public void testToString()
	{
		assertEquals("The version strings should match", VERSION_1, version_1.toString());
		assertEquals("The version strings should match", VERSION_1_0, version_1_0.toString());
		assertEquals("The version strings should match", VERSION_1_0, version_v1_0.toString());
		assertEquals("The version strings should match", VERSION_1_2, version_1_2.toString());
		assertEquals("The version strings should match", VERSION_1_2, version_1_2_it.toString());
		assertEquals("The version strings should match", VERSION_1_2_SNAPSHOT, version_1_2_snapshot.toString());
		assertEquals("The version strings should match", VERSION_1_2_SNAPSHOT, version_1_2_snapshot_it.toString());
		assertEquals("The version strings should match", VERSION_1_2_BETA, version_1_2_beta.toString());
		assertEquals("The version strings should match", VERSION_1_2_BETA2, version_1_2_beta2.toString());
		assertEquals("The version strings should match", "2.0", version_v2_0.toString());
		assertEquals("The version strings should match", VERSION_2_0_SNAPSHOT, version_2_0_snapshot.toString());
		assertNotEquals("The version strings should not match", VERSION_V1_0, version_v1_0.toString());
		assertNotEquals("The version strings should not match", VERSION_1_0, version_1.toString());
		assertNotEquals("The version strings should not match", VERSION_1_0, version_1_2.toString());
		assertNotEquals("The version strings should not match", VERSION_V2_0, version_v2_0.toString());
	}

	@Test
	public void testEquals()
	{
		assertEquals("The version should be the same", version_1, version_1_0);
		assertEquals("The version should be the same", version_1, version_v1_0);
		assertEquals("The version should be the same", version_1_0, version_1);
		assertEquals("The version should be the same", version_1_0, version_v1_0);
		assertEquals("The version should be the same", version_v1_0, version_1);
		assertEquals("The version should be the same", version_v1_0, version_1_0);
		assertEquals("The version should be the same", version_1_2, version_1_2_it);
		assertEquals("The version should be the same", version_1_2, version_1_2_snapshot_it);
		assertEquals("The version should be the same", version_1_2_it, version_1_2);
		assertEquals("The version should be the same", version_1_2_it, version_1_2_snapshot_it);
		assertEquals("The version should be the same", version_1_2_snapshot_it, version_1_2);
		assertEquals("The version should be the same", version_1_2_snapshot_it, version_1_2_it);
		assertNotEquals("The version should not be the same", version_1, version_1_2);
		assertNotEquals("The version should not be the same", version_1, version_1_2_it);
		assertNotEquals("The version should not be the same", version_1, version_1_2_snapshot);
		assertNotEquals("The version should not be the same", version_1, version_1_2_snapshot_it);
		assertNotEquals("The version should not be the same", version_1, version_1_2_beta);
		assertNotEquals("The version should not be the same", version_1, version_1_2_beta2);
		assertNotEquals("The version should not be the same", version_1, version_v2_0);
		assertNotEquals("The version should not be the same", version_1_0, version_1_2);
		assertNotEquals("The version should not be the same", version_1_0, version_1_2_it);
		assertNotEquals("The version should not be the same", version_1_0, version_1_2_snapshot);
		assertNotEquals("The version should not be the same", version_1_0, version_1_2_snapshot_it);
		assertNotEquals("The version should not be the same", version_1_0, version_1_2_beta);
		assertNotEquals("The version should not be the same", version_1_0, version_1_2_beta2);
		assertNotEquals("The version should not be the same", version_1_0, version_v2_0);
		assertNotEquals("The version should not be the same", version_v1_0, version_1_2);
		assertNotEquals("The version should not be the same", version_v1_0, version_1_2_it);
		assertNotEquals("The version should not be the same", version_v1_0, version_1_2_snapshot);
		assertNotEquals("The version should not be the same", version_v1_0, version_1_2_snapshot_it);
		assertNotEquals("The version should not be the same", version_v1_0, version_1_2_beta);
		assertNotEquals("The version should not be the same", version_v1_0, version_1_2_beta2);
		assertNotEquals("The version should not be the same", version_v1_0, version_v2_0);
		assertNotEquals("The version should not be the same", version_1_2, version_1);
		assertNotEquals("The version should not be the same", version_1_2, version_1_0);
		assertNotEquals("The version should not be the same", version_1_2, version_v1_0);
		assertNotEquals("The version should not be the same", version_1_2, version_1_2_snapshot);
		assertNotEquals("The version should not be the same", version_1_2, version_1_2_beta);
		assertNotEquals("The version should not be the same", version_1_2, version_1_2_beta2);
		assertNotEquals("The version should not be the same", version_1_2, version_v2_0);
		assertNotEquals("The version should not be the same", version_1_2_it, version_1);
		assertNotEquals("The version should not be the same", version_1_2_it, version_1_0);
		assertNotEquals("The version should not be the same", version_1_2_it, version_v1_0);
		assertNotEquals("The version should not be the same", version_1_2_it, version_1_2_snapshot);
		assertNotEquals("The version should not be the same", version_1_2_it, version_1_2_beta);
		assertNotEquals("The version should not be the same", version_1_2_it, version_1_2_beta2);
		assertNotEquals("The version should not be the same", version_1_2_it, version_v2_0);
		assertNotEquals("The version should not be the same", version_1_2_snapshot, version_1);
		assertNotEquals("The version should not be the same", version_1_2_snapshot, version_1_0);
		assertNotEquals("The version should not be the same", version_1_2_snapshot, version_v1_0);
		assertNotEquals("The version should not be the same", version_1_2_snapshot, version_1_2);
		assertNotEquals("The version should not be the same", version_1_2_snapshot, version_1_2_it);
		assertNotEquals("The version should not be the same", version_1_2_snapshot, version_1_2_snapshot_it);
		assertNotEquals("The version should not be the same", version_1_2_snapshot, version_1_2_beta);
		assertNotEquals("The version should not be the same", version_1_2_snapshot, version_1_2_beta2);
		assertNotEquals("The version should not be the same", version_1_2_snapshot, version_v2_0);
		assertNotEquals("The version should not be the same", version_1_2_snapshot_it, version_1);
		assertNotEquals("The version should not be the same", version_1_2_snapshot_it, version_1_0);
		assertNotEquals("The version should not be the same", version_1_2_snapshot_it, version_v1_0);
		assertNotEquals("The version should not be the same", version_1_2_snapshot_it, version_1_2_snapshot);
		assertNotEquals("The version should not be the same", version_1_2_snapshot_it, version_1_2_beta);
		assertNotEquals("The version should not be the same", version_1_2_snapshot_it, version_1_2_beta2);
		assertNotEquals("The version should not be the same", version_1_2_snapshot_it, version_v2_0);
		assertNotEquals("The version should not be the same", version_1_2_beta, version_v2_0);
		assertNotEquals("The version should not be the same", version_1_2_beta2, version_v2_0);
		assertNotEquals("The version should not be the same", version_1_2_beta2, version_2_0_snapshot);
		assertNotEquals("The version should not be the same", version_v2_0, version_2_0_snapshot);
		//noinspection ObjectEqualsNull
		assertNotEquals("The version should not be the same", null, version_v2_0);
		assertEquals("The versions should match", version_1, new Version(VERSION_1));
		assertEquals("The versions should match", version_1_0, new Version(VERSION_1_0));
		assertEquals("The versions should match", version_1, new Version(VERSION_1_0));
		assertEquals("The versions should match", version_v1_0, new Version(VERSION_V1_0));
		assertEquals("The versions should match", version_1_2, new Version(VERSION_1_2));
		assertEquals("The versions should match", version_1_2_snapshot, new Version(VERSION_1_2_SNAPSHOT));
		assertEquals("The versions should match", version_1_2_beta, new Version(VERSION_1_2_BETA));
		assertEquals("The versions should match", version_1_2_beta2, new Version(VERSION_1_2_BETA2));
		assertEquals("The versions should match", version_v2_0, new Version(VERSION_V2_0));
		assertEquals("The versions should match", version_1_2_it, new Version(VERSION_1_2));
		assertNotEquals("The versions should not match", version_1_2_snapshot_it, new Version(VERSION_1_2_SNAPSHOT));
		assertEquals("The versions should match", version_1, new Version(VERSION_1, true));
		assertEquals("The versions should match", version_1_0, new Version(VERSION_1_0, true));
		assertEquals("The versions should match", version_v1_0, new Version(VERSION_V1_0, true));
		assertEquals("The versions should match", version_1_2, new Version(VERSION_1_2, true));
		assertNotEquals("The versions should not match", version_1_2_snapshot, new Version(VERSION_1_2_SNAPSHOT, true));
		assertEquals("The versions should not match", version_1_2_it, new Version(VERSION_1_2, true));
		assertEquals("The versions should not match", version_1_2_snapshot_it, new Version(VERSION_1_2_SNAPSHOT, true));
	}

	@Test
	public void testHashCode()
	{
		assertEquals("The hashcode should match", new Version(VERSION_1).hashCode(), version_1.hashCode());
		assertEquals("The hashcode should match", version_1.hashCode(), version_1_0.hashCode());
		assertEquals("The hashcode should match", version_1.hashCode(), version_v1_0.hashCode());
		assertNotEquals("The hashcode should not match", version_1.hashCode(), version_1_2.hashCode());
		assertNotEquals("The hashcode should not match", version_1.hashCode(), version_1_2_it.hashCode());
		assertNotEquals("The hashcode should not match", version_1.hashCode(), version_1_2_snapshot.hashCode());
		assertNotEquals("The hashcode should not match", version_1.hashCode(), version_1_2_snapshot_it.hashCode());
		assertNotEquals("The hashcode should not match", version_1.hashCode(), version_v2_0.hashCode());
		assertEquals("The hashcode should match", new Version(VERSION_1_0).hashCode(), version_1_0.hashCode());
		assertEquals("The hashcode should match", version_1_0.hashCode(), version_v1_0.hashCode());
		assertEquals("The hashcode should match", version_1_0.hashCode(), version_1.hashCode());
		assertNotEquals("The hashcode should not match", version_1_0.hashCode(), version_1_2.hashCode());
		assertNotEquals("The hashcode should not match", version_1_0.hashCode(), version_1_2_it.hashCode());
		assertNotEquals("The hashcode should not match", version_1_0.hashCode(), version_1_2_snapshot.hashCode());
		assertNotEquals("The hashcode should not match", version_1_0.hashCode(), version_1_2_snapshot_it.hashCode());
		assertNotEquals("The hashcode should not match", version_1_0.hashCode(), version_v2_0.hashCode());
	}

	@Test
	public void testUnimportantVersionParts()
	{
		assertEquals(new Version("1.0.0.3"), new Version("1.0.0.3.0"));
		assertEquals(new Version("1.0.0.3"), new Version("1.0.0.3.0.0"));
		assertEquals(new Version("1.0.0.3"), new Version("1.0.0.3.0.0.0"));
		assertEquals(new Version("1.0.0.3"), new Version("1.0.0.3.0.00.0"));
		assertEquals(new Version("1.0.0.3"), new Version("1.00.0.3"));
	}

	@Test
	public void testPreRelease()
	{
		Version version = new Version("1.11.2-snapshot");
		assertTrue("The version should be a pre release version", version.isPreRelease());
	}

	@Test
	public void testGetBuildParameterWithError()
	{
		String versionString = "1.2-b1" + Long.MAX_VALUE;
		Version version = new Version(versionString);
		assertEquals("The version string should match", versionString, version.toString());
	}

	@Test
	public void testIntVersions()
	{
		Version intVersion = new Version(1);
		Version stringVersion = new Version("1");
		assertEquals(intVersion, stringVersion);
		intVersion = new Version(1,2);
		stringVersion = new Version("1.2");
		assertEquals(intVersion, stringVersion);
		intVersion = new Version(1,2,3);
		stringVersion = new Version("1.2.3");
		assertEquals(intVersion, stringVersion);
	}

	@Test
	public void testIncomparableVersionPairs()
	{
		assertFalse("The version should not be newer", version_1_2_snapshot_b_5.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_1_2_snapshot.newerThan(version_1_2_snapshot_b_5));
		assertFalse("The version should not be newer", version_1_2_snapshot_t_201703081212.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_1_2_snapshot.newerThan(version_1_2_snapshot_t_201703081212));
	}
}
