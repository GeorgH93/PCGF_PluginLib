/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Updater;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReleaseTypeTest
{
	@Test
	public void testReleaseType()
	{
		assertEquals("The release type enum value should match", ReleaseType.ALPHA, ReleaseType.valueOf("ALPHA"));
		assertEquals("The release type enum value should match", ReleaseType.BETA, ReleaseType.valueOf("BETA"));
		assertEquals("The release type enum value should match", ReleaseType.RELEASE, ReleaseType.valueOf("RELEASE"));
		assertEquals("The release type enum value should match", ReleaseType.SNAPSHOT, ReleaseType.valueOf("SNAPSHOT"));
		assertEquals("The release type enum value should match", ReleaseType.UNKNOWN, ReleaseType.valueOf("UNKNOWN"));
	}
}