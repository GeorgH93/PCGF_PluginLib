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
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Matcher.class, Pattern.class })
public class VersionTest
{
	private static final String VERSION_1 = "1", VERSION_1_0 = "1.0", VERSION_V1_0 = "v1.0", VERSION_V2_0 = "v2.0", VERSION_2_0_SNAPSHOT = "2.0-SNAPSHOT";
	private static final String VERSION_1_2_SNAPSHOT = "1.2-SNAPSHOT", VERSION_1_2 = "1.2", VERSION_1_2_BETA = "1.2-Beta", VERSION_1_2_BETA2 = "1.2-BETA2";
	private static final String VERSION_1_2_SNAPSHOT_BUILD_5 = "1.2-SNAPSHOT-Build5", VERSION_1_2_SNAPSHOT_BUILD_8 = "1.2-SNAPSHOT-Build=8";
	private static final String VERSION_1_2_SNAPSHOT_TIME_201703081212 = "1.2-SNAPSHOT-T201703081212", VERSION_1_2_SNAPSHOT_TIME_201603081212 = "1.2-SNAPSHOT-Timestamp=201603081212";
	private static final Version version_1 = new Version(VERSION_1), version_1_0 = new Version(VERSION_1_0), version_v1_0 = new Version(VERSION_V1_0), version_1_2_it = new Version(VERSION_1_2, true);
	private static final Version version_1_2_snapshot = new Version(VERSION_1_2_SNAPSHOT), version_1_2 = new Version(VERSION_1_2), version_1_2_snapshot_it = new Version(VERSION_1_2_SNAPSHOT, true);
	private static final Version version_1_2_beta = new Version(VERSION_1_2_BETA), version_1_2_beta2 = new Version(VERSION_1_2_BETA2), version_v2_0 = new Version(VERSION_V2_0);
	private static final Version version_2_0_snapshot = new Version(VERSION_2_0_SNAPSHOT);
	private static final Version version_1_2_snapshot_b_5 = new Version(VERSION_1_2_SNAPSHOT_BUILD_5), version_1_2_snapshot_b_8 = new Version(VERSION_1_2_SNAPSHOT_BUILD_8);
	private static final Version version_1_2_snapshot_t_201703081212 = new Version(VERSION_1_2_SNAPSHOT_TIME_201703081212), version_1_2_snapshot_t_201603081212 = new Version(VERSION_1_2_SNAPSHOT_TIME_201603081212);

	@Test
	public void testIsValidVersionString() throws Exception
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
	public void testNullVersion()
	{
		//noinspection ConstantConditions
		new Version(null);
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
	public void testNewerThan() throws Exception
	{
		assertTrue("The version should be newer", version_1_2.newerThan(version_1));
		assertTrue("The version should be newer", version_1_2.newerThan(version_1_0));
		assertTrue("The version should be newer", version_1_2.newerThan(version_v1_0));
		assertTrue("The version should be newer", version_1_2.newerThan(version_1_2_snapshot));
		assertTrue("The version should be newer", version_1_2.newerThan(version_1_2_beta));
		assertTrue("The version should be newer", version_1_2.newerThan(version_1_2_beta2));
		assertTrue("The version should be newer", version_1_2_it.newerThan(version_1));
		assertTrue("The version should be newer", version_1_2_it.newerThan(version_1_0));
		assertTrue("The version should be newer", version_1_2_it.newerThan(version_v1_0));
		assertTrue("The version should be newer", version_1_2_it.newerThan(version_1_2_snapshot));
		assertTrue("The version should be newer", version_1_2_it.newerThan(version_1_2_beta));
		assertTrue("The version should be newer", version_1_2_it.newerThan(version_1_2_beta2));
		assertTrue("The version should be newer", version_1_2_snapshot.newerThan(version_1));
		assertTrue("The version should be newer", version_1_2_snapshot.newerThan(version_1_0));
		assertTrue("The version should be newer", version_1_2_snapshot.newerThan(version_v1_0));
		assertTrue("The version should be newer", version_1_2_snapshot.newerThan(version_1_2_beta));
		assertTrue("The version should be newer", version_1_2_snapshot.newerThan(version_1_2_beta2));
		assertTrue("The version should be newer", version_1_2_snapshot_it.newerThan(version_1));
		assertTrue("The version should be newer", version_1_2_snapshot_it.newerThan(version_1_0));
		assertTrue("The version should be newer", version_1_2_snapshot_it.newerThan(version_v1_0));
		assertTrue("The version should be newer", version_1_2_snapshot_it.newerThan(version_1_2_snapshot));
		assertTrue("The version should be newer", version_1_2_snapshot_it.newerThan(version_1_2_beta));
		assertTrue("The version should be newer", version_1_2_snapshot_it.newerThan(version_1_2_beta2));
		assertTrue("The version should be newer", version_1_2_beta.newerThan(version_1));
		assertTrue("The version should be newer", version_1_2_beta.newerThan(version_1_0));
		assertTrue("The version should be newer", version_1_2_beta.newerThan(version_v1_0));
		assertTrue("The version should be newer", version_1_2_beta2.newerThan(version_1));
		assertTrue("The version should be newer", version_1_2_beta2.newerThan(version_1_0));
		assertTrue("The version should be newer", version_1_2_beta2.newerThan(version_v1_0));
		assertTrue("The version should be newer", version_1_2_beta2.newerThan(version_1_2_beta));
		assertTrue("The version should be newer", version_2_0_snapshot.newerThan(version_1_2));
		assertTrue("The version should be newer", version_v2_0.newerThan(version_2_0_snapshot));
		assertTrue("The version should be newer", version_1_2_snapshot_b_8.newerThan(version_1_2_snapshot_b_5));
		assertTrue("The version should be newer", version_1_2_snapshot_t_201703081212.newerThan(version_1_2_snapshot_t_201603081212));
		assertFalse("The version should not be newer", version_1.newerThan(version_1));
		assertFalse("The version should not be newer", version_1.newerThan(version_1_0));
		assertFalse("The version should not be newer", version_1.newerThan(version_v1_0));
		assertFalse("The version should not be newer", version_1.newerThan(version_1_2));
		assertFalse("The version should not be newer", version_1.newerThan(version_1_2_it));
		assertFalse("The version should not be newer", version_1.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_1.newerThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer", version_1.newerThan(version_1_2_beta));
		assertFalse("The version should not be newer", version_1.newerThan(version_1_2_beta2));
		assertFalse("The version should not be newer", version_1.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_1));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_1_0));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_v1_0));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_1_2));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_1_2_it));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_1_2_beta));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_1_2_beta2));
		assertFalse("The version should not be newer", version_1_0.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_1_2));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_1_2_it));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_1));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_1_0));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_v1_0));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_1_2_beta));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_1_2_beta2));
		assertFalse("The version should not be newer", version_v1_0.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_1_2_snapshot.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_1_2_snapshot.newerThan(version_1_2));
		assertFalse("The version should not be newer", version_1_2_snapshot.newerThan(version_1_2_it));
		assertFalse("The version should not be newer", version_1_2_snapshot.newerThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer", version_1_2_snapshot.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_1_2.newerThan(version_1_2));
		assertFalse("The version should not be newer", version_1_2.newerThan(version_1_2_it));
		assertFalse("The version should not be newer", version_1_2.newerThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer", version_1_2.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_1_2_it.newerThan(version_1_2));
		assertFalse("The version should not be newer", version_1_2_it.newerThan(version_1_2_it));
		assertFalse("The version should not be newer", version_1_2_it.newerThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer", version_1_2_it.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_1_2_snapshot_it.newerThan(version_1_2));
		assertFalse("The version should not be newer", version_1_2_snapshot_it.newerThan(version_1_2_it));
		assertFalse("The version should not be newer", version_1_2_snapshot_it.newerThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer", version_1_2_snapshot_it.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_1_2_beta.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_1_2_beta.newerThan(version_1_2));
		assertFalse("The version should not be newer", version_1_2_beta.newerThan(version_1_2_it));
		assertFalse("The version should not be newer", version_1_2_beta.newerThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer", version_1_2_beta.newerThan(version_1_2_beta));
		assertFalse("The version should not be newer", version_1_2_beta.newerThan(version_1_2_beta2));
		assertFalse("The version should not be newer", version_1_2_beta.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_1_2_beta2.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_1_2_beta2.newerThan(version_1_2));
		assertFalse("The version should not be newer", version_1_2_beta2.newerThan(version_1_2_it));
		assertFalse("The version should not be newer", version_1_2_beta2.newerThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer", version_1_2_beta2.newerThan(version_1_2_beta2));
		assertFalse("The version should not be newer", version_1_2_beta2.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_1_2_snapshot_b_5.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_1_2_snapshot_t_201703081212.newerThan(version_1_2_snapshot));
		assertFalse("The version should not be newer", version_2_0_snapshot.newerThan(version_v2_0));
		assertFalse("The version should not be newer", version_1_2_snapshot.newerThan(version_1_2_snapshot_b_5));
		assertFalse("The version should not be newer", version_1_2_snapshot_b_5.newerThan(version_1_2_snapshot_b_8));
		assertFalse("The version should not be newer", version_1_2_snapshot.newerThan(version_1_2_snapshot_t_201703081212));
		assertFalse("The version should not be newer", version_1_2_snapshot_t_201603081212.newerThan(version_1_2_snapshot_t_201703081212));
	}

	@Test
	public void testNewerOrEqualThan() throws Exception
	{
		assertTrue("The version should be newer or equal", version_1.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_1.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_1.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_1_0.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_1_0.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_1_0.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_v1_0.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_v1_0.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_v1_0.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_1_2.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_1_2.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_1_2.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_1_2.newerOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be newer or equal", version_1_2.newerOrEqualThan(version_1_2));
		assertTrue("The version should be newer or equal", version_1_2.newerOrEqualThan(version_1_2_it));
		assertTrue("The version should be newer or equal", version_1_2.newerOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be newer or equal", version_1_2.newerOrEqualThan(version_1_2_beta));
		assertTrue("The version should be newer or equal", version_1_2.newerOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be newer or equal", version_1_2_it.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_1_2_it.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_1_2_it.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_1_2_it.newerOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be newer or equal", version_1_2_it.newerOrEqualThan(version_1_2));
		assertTrue("The version should be newer or equal", version_1_2_it.newerOrEqualThan(version_1_2_it));
		assertTrue("The version should be newer or equal", version_1_2_it.newerOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be newer or equal", version_1_2_it.newerOrEqualThan(version_1_2_beta));
		assertTrue("The version should be newer or equal", version_1_2_it.newerOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_1_2_beta));
		assertTrue("The version should be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_1_2));
		assertTrue("The version should be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_1_2_it));
		assertTrue("The version should be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_1_2_beta));
		assertTrue("The version should be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be newer or equal", version_1_2_beta.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_1_2_beta.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_1_2_beta.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_1_2_beta.newerOrEqualThan(version_1_2_beta));
		assertTrue("The version should be newer or equal", version_1_2_beta2.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_1_2_beta2.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_1_2_beta2.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_1_2_beta2.newerOrEqualThan(version_1_2_beta));
		assertTrue("The version should be newer or equal", version_1_2_beta2.newerOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be newer or equal", version_2_0_snapshot.newerOrEqualThan(version_1_2));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_1));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_1_0));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_v1_0));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_1_2));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_1_2_it));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_1_2_beta));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_2_0_snapshot));
		assertTrue("The version should be newer or equal", version_v2_0.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_1.newerOrEqualThan(version_1_2));
		assertFalse("The version should not be newer or equal", version_1.newerOrEqualThan(version_1_2_it));
		assertFalse("The version should not be newer or equal", version_1.newerOrEqualThan(version_1_2_snapshot));
		assertFalse("The version should not be newer or equal", version_1.newerOrEqualThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer or equal", version_1.newerOrEqualThan(version_1_2_beta));
		assertFalse("The version should not be newer or equal", version_1.newerOrEqualThan(version_1_2_beta2));
		assertFalse("The version should not be newer or equal", version_1.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_1_0.newerOrEqualThan(version_1_2));
		assertFalse("The version should not be newer or equal", version_1_0.newerOrEqualThan(version_1_2_it));
		assertFalse("The version should not be newer or equal", version_1_0.newerOrEqualThan(version_1_2_snapshot));
		assertFalse("The version should not be newer or equal", version_1_0.newerOrEqualThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer or equal", version_1_0.newerOrEqualThan(version_1_2_beta));
		assertFalse("The version should not be newer or equal", version_1_0.newerOrEqualThan(version_1_2_beta2));
		assertFalse("The version should not be newer or equal", version_1_0.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_v1_0.newerOrEqualThan(version_1_2));
		assertFalse("The version should not be newer or equal", version_v1_0.newerOrEqualThan(version_1_2_it));
		assertFalse("The version should not be newer or equal", version_v1_0.newerOrEqualThan(version_1_2_snapshot));
		assertFalse("The version should not be newer or equal", version_v1_0.newerOrEqualThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer or equal", version_v1_0.newerOrEqualThan(version_1_2_beta));
		assertFalse("The version should not be newer or equal", version_v1_0.newerOrEqualThan(version_1_2_beta2));
		assertFalse("The version should not be newer or equal", version_v1_0.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_1_2));
		assertFalse("The version should not be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_1_2_it));
		assertFalse("The version should not be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer or equal", version_1_2_snapshot.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_1_2.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_1_2_snapshot_it.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_1_2_it.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_1_2_beta.newerOrEqualThan(version_1_2));
		assertFalse("The version should not be newer or equal", version_1_2_beta.newerOrEqualThan(version_1_2_beta2));
		assertFalse("The version should not be newer or equal", version_1_2_beta.newerOrEqualThan(version_1_2_it));
		assertFalse("The version should not be newer or equal", version_1_2_beta.newerOrEqualThan(version_1_2_snapshot));
		assertFalse("The version should not be newer or equal", version_1_2_beta.newerOrEqualThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer or equal", version_1_2_beta.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_1_2_beta2.newerOrEqualThan(version_1_2));
		assertFalse("The version should not be newer or equal", version_1_2_beta2.newerOrEqualThan(version_1_2_it));
		assertFalse("The version should not be newer or equal", version_1_2_beta2.newerOrEqualThan(version_1_2_snapshot));
		assertFalse("The version should not be newer or equal", version_1_2_beta2.newerOrEqualThan(version_1_2_snapshot_it));
		assertFalse("The version should not be newer or equal", version_1_2_beta2.newerOrEqualThan(version_v2_0));
		assertFalse("The version should not be newer or equal", version_2_0_snapshot.newerOrEqualThan(version_v2_0));
	}

	@Test
	public void testOlderThan() throws Exception
	{
		assertTrue("The version should be older", version_1.olderThan(version_1_2));
		assertTrue("The version should be older", version_1.olderThan(version_1_2_it));
		assertTrue("The version should be older", version_1.olderThan(version_1_2_snapshot));
		assertTrue("The version should be older", version_1.olderThan(version_1_2_snapshot_it));
		assertTrue("The version should be older", version_1.olderThan(version_1_2_beta));
		assertTrue("The version should be older", version_1.olderThan(version_1_2_beta2));
		assertTrue("The version should be older", version_1.olderThan(version_v2_0));
		assertTrue("The version should be older", version_1_0.olderThan(version_1_2));
		assertTrue("The version should be older", version_1_0.olderThan(version_1_2_it));
		assertTrue("The version should be older", version_1_0.olderThan(version_1_2_snapshot));
		assertTrue("The version should be older", version_1_0.olderThan(version_1_2_snapshot_it));
		assertTrue("The version should be older", version_1_0.olderThan(version_1_2_beta));
		assertTrue("The version should be older", version_1_0.olderThan(version_1_2_beta2));
		assertTrue("The version should be older", version_1_0.olderThan(version_v2_0));
		assertTrue("The version should be older", version_v1_0.olderThan(version_1_2));
		assertTrue("The version should be older", version_v1_0.olderThan(version_1_2_it));
		assertTrue("The version should be older", version_v1_0.olderThan(version_1_2_snapshot));
		assertTrue("The version should be older", version_v1_0.olderThan(version_1_2_snapshot_it));
		assertTrue("The version should be older", version_v1_0.olderThan(version_1_2_beta));
		assertTrue("The version should be older", version_v1_0.olderThan(version_1_2_beta2));
		assertTrue("The version should be older", version_v1_0.olderThan(version_v2_0));
		assertTrue("The version should be older", version_1_2_snapshot.olderThan(version_1_2));
		assertTrue("The version should be older", version_1_2_snapshot.olderThan(version_1_2_it));
		assertTrue("The version should be older", version_1_2_snapshot.olderThan(version_1_2_snapshot_it));
		assertTrue("The version should be older", version_1_2_snapshot.olderThan(version_v2_0));
		assertTrue("The version should be older", version_1_2.olderThan(version_v2_0));
		assertTrue("The version should be older", version_1_2_snapshot_it.olderThan(version_v2_0));
		assertTrue("The version should be older", version_1_2_it.olderThan(version_v2_0));
		assertTrue("The version should be older", version_1_2_beta.olderThan(version_1_2));
		assertTrue("The version should be older", version_1_2_beta.olderThan(version_1_2_beta2));
		assertTrue("The version should be older", version_1_2_beta.olderThan(version_1_2_it));
		assertTrue("The version should be older", version_1_2_beta.olderThan(version_1_2_snapshot));
		assertTrue("The version should be older", version_1_2_beta.olderThan(version_1_2_snapshot_it));
		assertTrue("The version should be older", version_1_2_beta.olderThan(version_v2_0));
		assertTrue("The version should be older", version_1_2_beta2.olderThan(version_1_2));
		assertTrue("The version should be older", version_1_2_beta2.olderThan(version_1_2_it));
		assertTrue("The version should be older", version_1_2_beta2.olderThan(version_1_2_snapshot));
		assertTrue("The version should be older", version_1_2_beta2.olderThan(version_1_2_snapshot_it));
		assertTrue("The version should be older", version_1_2_beta2.olderThan(version_v2_0));
		assertTrue("The version should be older", version_2_0_snapshot.olderThan(version_v2_0));
		assertFalse("The version should not be older", version_1.olderThan(version_1));
		assertFalse("The version should not be older", version_1.olderThan(version_1_0));
		assertFalse("The version should not be older", version_1.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_1_0.olderThan(version_1));
		assertFalse("The version should not be older", version_1_0.olderThan(version_1_0));
		assertFalse("The version should not be older", version_1_0.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_v1_0.olderThan(version_1));
		assertFalse("The version should not be older", version_v1_0.olderThan(version_1_0));
		assertFalse("The version should not be older", version_v1_0.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_1_2.olderThan(version_1));
		assertFalse("The version should not be older", version_1_2.olderThan(version_1_0));
		assertFalse("The version should not be older", version_1_2.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_1_2.olderThan(version_1_2_snapshot));
		assertFalse("The version should not be older", version_1_2.olderThan(version_1_2));
		assertFalse("The version should not be older", version_1_2.olderThan(version_1_2_it));
		assertFalse("The version should not be older", version_1_2.olderThan(version_1_2_snapshot_it));
		assertFalse("The version should not be older", version_1_2.olderThan(version_1_2_beta));
		assertFalse("The version should not be older", version_1_2.olderThan(version_1_2_beta2));
		assertFalse("The version should not be older", version_1_2_it.olderThan(version_1));
		assertFalse("The version should not be older", version_1_2_it.olderThan(version_1_0));
		assertFalse("The version should not be older", version_1_2_it.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_1_2_it.olderThan(version_1_2_snapshot));
		assertFalse("The version should not be older", version_1_2_it.olderThan(version_1_2));
		assertFalse("The version should not be older", version_1_2_it.olderThan(version_1_2_it));
		assertFalse("The version should not be older", version_1_2_it.olderThan(version_1_2_snapshot_it));
		assertFalse("The version should not be older", version_1_2_it.olderThan(version_1_2_beta));
		assertFalse("The version should not be older", version_1_2_it.olderThan(version_1_2_beta2));
		assertFalse("The version should not be older", version_1_2_snapshot.olderThan(version_1_2_snapshot));
		assertFalse("The version should not be older", version_1_2_snapshot.olderThan(version_1));
		assertFalse("The version should not be older", version_1_2_snapshot.olderThan(version_1_0));
		assertFalse("The version should not be older", version_1_2_snapshot.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_1_2_snapshot.olderThan(version_1_2_beta));
		assertFalse("The version should not be older", version_1_2_snapshot.olderThan(version_1_2_beta2));
		assertFalse("The version should not be older", version_1_2_snapshot_it.olderThan(version_1_2));
		assertFalse("The version should not be older", version_1_2_snapshot_it.olderThan(version_1_2_it));
		assertFalse("The version should not be older", version_1_2_snapshot_it.olderThan(version_1_2_snapshot_it));
		assertFalse("The version should not be older", version_1_2_snapshot_it.olderThan(version_1));
		assertFalse("The version should not be older", version_1_2_snapshot_it.olderThan(version_1_0));
		assertFalse("The version should not be older", version_1_2_snapshot_it.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_1_2_snapshot_it.olderThan(version_1_2_snapshot));
		assertFalse("The version should not be older", version_1_2_snapshot_it.olderThan(version_1_2_beta));
		assertFalse("The version should not be older", version_1_2_snapshot_it.olderThan(version_1_2_beta2));
		assertFalse("The version should not be older", version_1_2_beta.olderThan(version_1));
		assertFalse("The version should not be older", version_1_2_beta.olderThan(version_1_0));
		assertFalse("The version should not be older", version_1_2_beta.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_1_2_beta.olderThan(version_1_2_beta));
		assertFalse("The version should not be older", version_1_2_beta2.olderThan(version_1));
		assertFalse("The version should not be older", version_1_2_beta2.olderThan(version_1_0));
		assertFalse("The version should not be older", version_1_2_beta2.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_1_2_beta2.olderThan(version_1_2_beta));
		assertFalse("The version should not be older", version_1_2_beta2.olderThan(version_1_2_beta2));
		assertFalse("The version should not be older", version_2_0_snapshot.olderThan(version_1_2));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_1));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_1_0));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_v1_0));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_1_2_snapshot));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_1_2));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_1_2_it));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_1_2_snapshot_it));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_1_2_beta));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_1_2_beta2));
		assertFalse("The version should not be older", version_v2_0.olderThan(version_v2_0));
	}

	@Test
	public void testOlderOrEqualThan() throws Exception
	{
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_1));
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_1_0));
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_v1_0));
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_1_2));
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_1_2_it));
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_1_2_beta));
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be older or equal", version_1.olderOrEqualThan(version_v2_0));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_1));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_1_0));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_v1_0));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_1_2));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_1_2_it));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_1_2_beta));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be older or equal", version_1_0.olderOrEqualThan(version_v2_0));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_1_2));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_1_2_it));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_1));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_1_0));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_v1_0));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_1_2_beta));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be older or equal", version_v1_0.olderOrEqualThan(version_v2_0));
		assertTrue("The version should be older or equal", version_1_2_snapshot.olderOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be older or equal", version_1_2_snapshot.olderOrEqualThan(version_1_2));
		assertTrue("The version should be older or equal", version_1_2_snapshot.olderOrEqualThan(version_1_2_it));
		assertTrue("The version should be older or equal", version_1_2_snapshot.olderOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be older or equal", version_1_2_snapshot.olderOrEqualThan(version_v2_0));
		assertTrue("The version should be older or equal", version_1_2.olderOrEqualThan(version_1_2));
		assertTrue("The version should be older or equal", version_1_2.olderOrEqualThan(version_1_2_it));
		assertTrue("The version should be older or equal", version_1_2.olderOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be older or equal", version_1_2.olderOrEqualThan(version_v2_0));
		assertTrue("The version should be older or equal", version_1_2_it.olderOrEqualThan(version_1_2));
		assertTrue("The version should be older or equal", version_1_2_it.olderOrEqualThan(version_1_2_it));
		assertTrue("The version should be older or equal", version_1_2_it.olderOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be older or equal", version_1_2_it.olderOrEqualThan(version_v2_0));
		assertTrue("The version should be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_1_2));
		assertTrue("The version should be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_1_2_it));
		assertTrue("The version should be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_v2_0));
		assertTrue("The version should be older or equal", version_1_2_beta.olderOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be older or equal", version_1_2_beta.olderOrEqualThan(version_1_2));
		assertTrue("The version should be older or equal", version_1_2_beta.olderOrEqualThan(version_1_2_it));
		assertTrue("The version should be older or equal", version_1_2_beta.olderOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be older or equal", version_1_2_beta.olderOrEqualThan(version_1_2_beta));
		assertTrue("The version should be older or equal", version_1_2_beta.olderOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be older or equal", version_1_2_beta.olderOrEqualThan(version_v2_0));
		assertTrue("The version should be older or equal", version_1_2_beta2.olderOrEqualThan(version_1_2_snapshot));
		assertTrue("The version should be older or equal", version_1_2_beta2.olderOrEqualThan(version_1_2));
		assertTrue("The version should be older or equal", version_1_2_beta2.olderOrEqualThan(version_1_2_it));
		assertTrue("The version should be older or equal", version_1_2_beta2.olderOrEqualThan(version_1_2_snapshot_it));
		assertTrue("The version should be older or equal", version_1_2_beta2.olderOrEqualThan(version_1_2_beta2));
		assertTrue("The version should be older or equal", version_1_2_beta2.olderOrEqualThan(version_v2_0));
		assertTrue("The version should be older or equal", version_2_0_snapshot.olderOrEqualThan(version_v2_0));
		assertFalse("The version should not be older or equal", version_1_2.olderOrEqualThan(version_1));
		assertFalse("The version should not be older or equal", version_1_2.olderOrEqualThan(version_1_0));
		assertFalse("The version should not be older or equal", version_1_2.olderOrEqualThan(version_v1_0));
		assertFalse("The version should not be older or equal", version_1_2.olderOrEqualThan(version_1_2_snapshot));
		assertFalse("The version should not be older or equal", version_1_2.olderOrEqualThan(version_1_2_beta));
		assertFalse("The version should not be older or equal", version_1_2.olderOrEqualThan(version_1_2_beta2));
		assertFalse("The version should not be older or equal", version_1_2_it.olderOrEqualThan(version_1));
		assertFalse("The version should not be older or equal", version_1_2_it.olderOrEqualThan(version_1_0));
		assertFalse("The version should not be older or equal", version_1_2_it.olderOrEqualThan(version_v1_0));
		assertFalse("The version should not be older or equal", version_1_2_it.olderOrEqualThan(version_1_2_snapshot));
		assertFalse("The version should not be older or equal", version_1_2_it.olderOrEqualThan(version_1_2_beta));
		assertFalse("The version should not be older or equal", version_1_2_it.olderOrEqualThan(version_1_2_beta2));
		assertFalse("The version should not be older or equal", version_1_2_snapshot.olderOrEqualThan(version_1));
		assertFalse("The version should not be older or equal", version_1_2_snapshot.olderOrEqualThan(version_1_0));
		assertFalse("The version should not be older or equal", version_1_2_snapshot.olderOrEqualThan(version_v1_0));
		assertFalse("The version should not be older or equal", version_1_2_snapshot.olderOrEqualThan(version_1_2_beta));
		assertFalse("The version should not be older or equal", version_1_2_snapshot.olderOrEqualThan(version_1_2_beta2));
		assertFalse("The version should not be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_1));
		assertFalse("The version should not be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_1_0));
		assertFalse("The version should not be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_v1_0));
		assertFalse("The version should not be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_1_2_snapshot));
		assertFalse("The version should not be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_1_2_beta));
		assertFalse("The version should not be older or equal", version_1_2_snapshot_it.olderOrEqualThan(version_1_2_beta2));
		assertFalse("The version should not be older or equal", version_1_2_beta.olderOrEqualThan(version_1));
		assertFalse("The version should not be older or equal", version_1_2_beta.olderOrEqualThan(version_1_0));
		assertFalse("The version should not be older or equal", version_1_2_beta.olderOrEqualThan(version_v1_0));
		assertFalse("The version should not be older or equal", version_1_2_beta2.olderOrEqualThan(version_1));
		assertFalse("The version should not be older or equal", version_1_2_beta2.olderOrEqualThan(version_1_0));
		assertFalse("The version should not be older or equal", version_1_2_beta2.olderOrEqualThan(version_v1_0));
		assertFalse("The version should not be older or equal", version_1_2_beta2.olderOrEqualThan(version_1_2_beta));
		assertFalse("The version should not be older or equal", version_2_0_snapshot.olderOrEqualThan(version_1_2));
	}

	@Test
	public void testToString() throws Exception
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
	public void testEquals() throws Exception
	{
		assertTrue("The version should be the same", version_1.equals(version_1_0));
		assertTrue("The version should be the same", version_1.equals(version_v1_0));
		assertTrue("The version should be the same", version_1_0.equals(version_1));
		assertTrue("The version should be the same", version_1_0.equals(version_v1_0));
		assertTrue("The version should be the same", version_v1_0.equals(version_1));
		assertTrue("The version should be the same", version_v1_0.equals(version_1_0));
		assertTrue("The version should be the same", version_1_2.equals(version_1_2_it));
		assertTrue("The version should be the same", version_1_2.equals(version_1_2_snapshot_it));
		assertTrue("The version should be the same", version_1_2_it.equals(version_1_2));
		assertTrue("The version should be the same", version_1_2_it.equals(version_1_2_snapshot_it));
		assertTrue("The version should be the same", version_1_2_snapshot_it.equals(version_1_2));
		assertTrue("The version should be the same", version_1_2_snapshot_it.equals(version_1_2_it));
		assertFalse("The version should not be the same", version_1.equals(version_1_2));
		assertFalse("The version should not be the same", version_1.equals(version_1_2_it));
		assertFalse("The version should not be the same", version_1.equals(version_1_2_snapshot));
		assertFalse("The version should not be the same", version_1.equals(version_1_2_snapshot_it));
		assertFalse("The version should not be the same", version_1.equals(version_1_2_beta));
		assertFalse("The version should not be the same", version_1.equals(version_1_2_beta2));
		assertFalse("The version should not be the same", version_1.equals(version_v2_0));
		assertFalse("The version should not be the same", version_1_0.equals(version_1_2));
		assertFalse("The version should not be the same", version_1_0.equals(version_1_2_it));
		assertFalse("The version should not be the same", version_1_0.equals(version_1_2_snapshot));
		assertFalse("The version should not be the same", version_1_0.equals(version_1_2_snapshot_it));
		assertFalse("The version should not be the same", version_1_0.equals(version_1_2_beta));
		assertFalse("The version should not be the same", version_1_0.equals(version_1_2_beta2));
		assertFalse("The version should not be the same", version_1_0.equals(version_v2_0));
		assertFalse("The version should not be the same", version_v1_0.equals(version_1_2));
		assertFalse("The version should not be the same", version_v1_0.equals(version_1_2_it));
		assertFalse("The version should not be the same", version_v1_0.equals(version_1_2_snapshot));
		assertFalse("The version should not be the same", version_v1_0.equals(version_1_2_snapshot_it));
		assertFalse("The version should not be the same", version_v1_0.equals(version_1_2_beta));
		assertFalse("The version should not be the same", version_v1_0.equals(version_1_2_beta2));
		assertFalse("The version should not be the same", version_v1_0.equals(version_v2_0));
		assertFalse("The version should not be the same", version_1_2.equals(version_1));
		assertFalse("The version should not be the same", version_1_2.equals(version_1_0));
		assertFalse("The version should not be the same", version_1_2.equals(version_v1_0));
		assertFalse("The version should not be the same", version_1_2.equals(version_1_2_snapshot));
		assertFalse("The version should not be the same", version_1_2.equals(version_1_2_beta));
		assertFalse("The version should not be the same", version_1_2.equals(version_1_2_beta2));
		assertFalse("The version should not be the same", version_1_2.equals(version_v2_0));
		assertFalse("The version should not be the same", version_1_2_it.equals(version_1));
		assertFalse("The version should not be the same", version_1_2_it.equals(version_1_0));
		assertFalse("The version should not be the same", version_1_2_it.equals(version_v1_0));
		assertFalse("The version should not be the same", version_1_2_it.equals(version_1_2_snapshot));
		assertFalse("The version should not be the same", version_1_2_it.equals(version_1_2_beta));
		assertFalse("The version should not be the same", version_1_2_it.equals(version_1_2_beta2));
		assertFalse("The version should not be the same", version_1_2_it.equals(version_v2_0));
		assertFalse("The version should not be the same", version_1_2_snapshot.equals(version_1));
		assertFalse("The version should not be the same", version_1_2_snapshot.equals(version_1_0));
		assertFalse("The version should not be the same", version_1_2_snapshot.equals(version_v1_0));
		assertFalse("The version should not be the same", version_1_2_snapshot.equals(version_1_2));
		assertFalse("The version should not be the same", version_1_2_snapshot.equals(version_1_2_it));
		assertFalse("The version should not be the same", version_1_2_snapshot.equals(version_1_2_snapshot_it));
		assertFalse("The version should not be the same", version_1_2_snapshot.equals(version_1_2_beta));
		assertFalse("The version should not be the same", version_1_2_snapshot.equals(version_1_2_beta2));
		assertFalse("The version should not be the same", version_1_2_snapshot.equals(version_v2_0));
		assertFalse("The version should not be the same", version_1_2_snapshot_it.equals(version_1));
		assertFalse("The version should not be the same", version_1_2_snapshot_it.equals(version_1_0));
		assertFalse("The version should not be the same", version_1_2_snapshot_it.equals(version_v1_0));
		assertFalse("The version should not be the same", version_1_2_snapshot_it.equals(version_1_2_snapshot));
		assertFalse("The version should not be the same", version_1_2_snapshot_it.equals(version_1_2_beta));
		assertFalse("The version should not be the same", version_1_2_snapshot_it.equals(version_1_2_beta2));
		assertFalse("The version should not be the same", version_1_2_snapshot_it.equals(version_v2_0));
		assertFalse("The version should not be the same", version_1_2_beta.equals(version_v2_0));
		assertFalse("The version should not be the same", version_1_2_beta2.equals(version_v2_0));
		assertFalse("The version should not be the same", version_1_2_beta2.equals(version_2_0_snapshot));
		assertFalse("The version should not be the same", version_v2_0.equals(version_2_0_snapshot));
		//noinspection ObjectEqualsNull
		assertFalse("The version should not be the same", version_v2_0.equals(null));
		assertTrue("The versions should match", new Version(VERSION_1).equals(version_1));
		assertTrue("The versions should match", new Version(VERSION_1_0).equals(version_1_0));
		assertTrue("The versions should match", new Version(VERSION_1_0).equals(version_1));
		assertTrue("The versions should match", new Version(VERSION_V1_0).equals(version_v1_0));
		assertTrue("The versions should match", new Version(VERSION_1_2).equals(version_1_2));
		assertTrue("The versions should match", new Version(VERSION_1_2_SNAPSHOT).equals(version_1_2_snapshot));
		assertTrue("The versions should match", new Version(VERSION_1_2_BETA).equals(version_1_2_beta));
		assertTrue("The versions should match", new Version(VERSION_1_2_BETA2).equals(version_1_2_beta2));
		assertTrue("The versions should match", new Version(VERSION_V2_0).equals(version_v2_0));
		assertTrue("The versions should match", new Version(VERSION_1_2).equals(version_1_2_it));
		assertFalse("The versions should not match", new Version(VERSION_1_2_SNAPSHOT).equals(version_1_2_snapshot_it));
		assertTrue("The versions should match", new Version(VERSION_1, true).equals(version_1));
		assertTrue("The versions should match", new Version(VERSION_1_0, true).equals(version_1_0));
		assertTrue("The versions should match", new Version(VERSION_V1_0, true).equals(version_v1_0));
		assertTrue("The versions should match", new Version(VERSION_1_2, true).equals(version_1_2));
		assertFalse("The versions should not match", new Version(VERSION_1_2_SNAPSHOT, true).equals(version_1_2_snapshot));
		assertTrue("The versions should not match", new Version(VERSION_1_2, true).equals(version_1_2_it));
		assertTrue("The versions should not match", new Version(VERSION_1_2_SNAPSHOT, true).equals(version_1_2_snapshot_it));
	}

	@Test
	public void testHashCode() throws Exception
	{
		assertTrue("The hashcode should match", new Version(VERSION_1).hashCode() == version_1.hashCode());
		assertTrue("The hashcode should match", version_1.hashCode() == version_1_0.hashCode());
		assertTrue("The hashcode should match", version_1.hashCode() == version_v1_0.hashCode());
		assertFalse("The hashcode should not match", version_1.hashCode() == version_1_2.hashCode());
		assertFalse("The hashcode should not match", version_1.hashCode() == version_1_2_it.hashCode());
		assertFalse("The hashcode should not match", version_1.hashCode() == version_1_2_snapshot.hashCode());
		assertFalse("The hashcode should not match", version_1.hashCode() == version_1_2_snapshot_it.hashCode());
		assertFalse("The hashcode should not match", version_1.hashCode() == version_v2_0.hashCode());
		assertTrue("The hashcode should match", new Version(VERSION_1_0).hashCode() == version_1_0.hashCode());
		assertTrue("The hashcode should match", version_1_0.hashCode() == version_v1_0.hashCode());
		assertTrue("The hashcode should match", version_1_0.hashCode() == version_1.hashCode());
		assertFalse("The hashcode should not match", version_1_0.hashCode() == version_1_2.hashCode());
		assertFalse("The hashcode should not match", version_1_0.hashCode() == version_1_2_it.hashCode());
		assertFalse("The hashcode should not match", version_1_0.hashCode() == version_1_2_snapshot.hashCode());
		assertFalse("The hashcode should not match", version_1_0.hashCode() == version_1_2_snapshot_it.hashCode());
		assertFalse("The hashcode should not match", version_1_0.hashCode() == version_v2_0.hashCode());
	}

	@Test
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testPreRelease() throws Exception
	{
		Matcher mockedMatcher = mock(Matcher.class);
		doReturn(true).when(mockedMatcher).matches();
		doReturn("Hallo Welt!").when(mockedMatcher).group(anyString());
		Pattern mockedPattern = mock(Pattern.class);
		doReturn(mockedMatcher).when(mockedPattern).matcher(anyString());
		PowerMockito.mockStatic(Pattern.class);
		//noinspection MagicConstant
		when(Pattern.compile(anyString(), anyInt())).thenReturn(mockedPattern);
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
}