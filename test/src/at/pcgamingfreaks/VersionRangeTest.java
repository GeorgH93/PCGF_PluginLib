/*
 *   Copyright (C) 2018 GeorgH93
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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VersionRangeTest
{
	private static final String MIN_VERSION = "1.2", MAX_VERSION = "2.0", TEST_VERSION_LOW = "1.1", TEST_VERSION_HIGH = "2.1", TEST_VERSION_WITHIN = "1.5";
	private VersionRange testRange, testRangeNoLower, testRangeNoUpper, testRangeNoLowerAndUpper;

	@Before
	public void setUp()
	{
		testRange = new VersionRange(MIN_VERSION, MAX_VERSION);
		testRangeNoLower = new VersionRange(null, MAX_VERSION);
		testRangeNoUpper = new VersionRange(MIN_VERSION, null);
		testRangeNoLowerAndUpper = new VersionRange((String) null, null);
	}

	@Test
	public void testInRange()
	{
		assertTrue(testRange.inRange(TEST_VERSION_WITHIN));
		assertFalse(testRange.inRange(TEST_VERSION_LOW));
		assertFalse(testRange.inRange(TEST_VERSION_HIGH));
		assertTrue(testRange.inRange(MIN_VERSION));
		assertTrue(testRange.inRange(MAX_VERSION));
		assertTrue(testRangeNoLower.inRange(TEST_VERSION_WITHIN));
		assertTrue(testRangeNoLower.inRange(TEST_VERSION_LOW));
		assertFalse(testRangeNoLower.inRange(TEST_VERSION_HIGH));
		assertTrue(testRangeNoLower.inRange(MIN_VERSION));
		assertTrue(testRangeNoLower.inRange(MAX_VERSION));
		assertTrue(testRangeNoUpper.inRange(TEST_VERSION_WITHIN));
		assertFalse(testRangeNoUpper.inRange(TEST_VERSION_LOW));
		assertTrue(testRangeNoUpper.inRange(TEST_VERSION_HIGH));
		assertTrue(testRangeNoUpper.inRange(MIN_VERSION));
		assertTrue(testRangeNoUpper.inRange(MAX_VERSION));
		assertTrue(testRangeNoLowerAndUpper.inRange(TEST_VERSION_WITHIN));
		assertTrue(testRangeNoLowerAndUpper.inRange(TEST_VERSION_LOW));
		assertTrue(testRangeNoLowerAndUpper.inRange(TEST_VERSION_HIGH));
		assertTrue(testRangeNoLowerAndUpper.inRange(MIN_VERSION));
		assertTrue(testRangeNoLowerAndUpper.inRange(MAX_VERSION));
	}

	@Test
	public void testInRangeExclusive()
	{
		assertTrue(testRange.inRangeExclusive(TEST_VERSION_WITHIN));
		assertFalse(testRange.inRangeExclusive(TEST_VERSION_LOW));
		assertFalse(testRange.inRangeExclusive(TEST_VERSION_HIGH));
		assertFalse(testRange.inRangeExclusive(MIN_VERSION));
		assertFalse(testRange.inRangeExclusive(MAX_VERSION));
		assertTrue(testRangeNoLower.inRangeExclusive(TEST_VERSION_WITHIN));
		assertTrue(testRangeNoLower.inRangeExclusive(TEST_VERSION_LOW));
		assertFalse(testRangeNoLower.inRangeExclusive(TEST_VERSION_HIGH));
		assertTrue(testRangeNoLower.inRangeExclusive(MIN_VERSION));
		assertFalse(testRangeNoLower.inRangeExclusive(MAX_VERSION));
		assertTrue(testRangeNoUpper.inRangeExclusive(TEST_VERSION_WITHIN));
		assertFalse(testRangeNoUpper.inRangeExclusive(TEST_VERSION_LOW));
		assertTrue(testRangeNoUpper.inRangeExclusive(TEST_VERSION_HIGH));
		assertFalse(testRangeNoUpper.inRangeExclusive(MIN_VERSION));
		assertTrue(testRangeNoUpper.inRangeExclusive(MAX_VERSION));
		assertTrue(testRangeNoLowerAndUpper.inRangeExclusive(TEST_VERSION_WITHIN));
		assertTrue(testRangeNoLowerAndUpper.inRangeExclusive(TEST_VERSION_LOW));
		assertTrue(testRangeNoLowerAndUpper.inRangeExclusive(TEST_VERSION_HIGH));
		assertTrue(testRangeNoLowerAndUpper.inRangeExclusive(MIN_VERSION));
		assertTrue(testRangeNoLowerAndUpper.inRangeExclusive(MAX_VERSION));
	}
}