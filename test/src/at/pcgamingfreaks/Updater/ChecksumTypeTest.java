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

package at.pcgamingfreaks.Updater;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChecksumTypeTest
{
	@Test
	public void testIsSupported() throws Exception
	{
		assertTrue("The hashing algorithm should be supported", ChecksumType.MD5.isSupported());
	}

	@Test
	public void testGetAlgorithm() throws Exception
	{
		assertEquals("The hashing algorithm should be MD5", "MD5", ChecksumType.MD5.getAlgorithm());
	}

	@Test
	public void testGetInstance() throws Exception
	{
		assertNotNull("The instance should not be null", ChecksumType.MD5.getInstance());
	}

	@Test
	public void testGetInstanceOrNull() throws Exception
	{
		assertNull("The algorithm should not be supported", ChecksumType.NONE.getInstanceOrNull());
	}
}