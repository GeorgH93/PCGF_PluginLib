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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class MCVersionTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
		TestObjects.setBukkitVersion("1_8_R1");
	}

	@Test
	public void testVersionComparison()
	{
		assertTrue("The version should match", MCVersion.isOlderOrEqualThan(MCVersion.MC_1_8));
		assertTrue("The version should match", MCVersion.isOlderOrEqualThan(MCVersion.MC_1_9));
		assertFalse("The version should not match", MCVersion.isOlderOrEqualThan(MCVersion.MC_1_7));
		assertTrue("The version should match", MCVersion.is(MCVersion.MC_1_8));
		assertFalse("The version should not match", MCVersion.is(MCVersion.MC_1_9));
		assertTrue("The version should match", MCVersion.MC_1_8.isSame(MCVersion.MC_1_8));
		assertFalse("The version should not match", MCVersion.MC_1_8.isSame(MCVersion.MC_1_9));
		assertTrue("The version should match", MCVersion.MC_1_8.newerOrEqualThan(MCVersion.MC_1_8));
		assertFalse("The version should not match", MCVersion.MC_1_8.newerOrEqualThan(MCVersion.MC_1_9));
		assertTrue("The version should match", MCVersion.MC_1_8.newerThan(MCVersion.MC_1_7));
		assertFalse("The version should not match", MCVersion.MC_1_8.newerThan(MCVersion.MC_1_9));
		assertTrue("The version should match", MCVersion.MC_1_8.olderOrEqualThan(MCVersion.MC_1_8));
		assertFalse("The version should not match", MCVersion.MC_1_8.olderOrEqualThan(MCVersion.MC_1_7));
		assertTrue("The version should match", MCVersion.MC_1_8.olderThan(MCVersion.MC_1_9));
		assertFalse("The version should not match", MCVersion.MC_1_8.olderThan(MCVersion.MC_1_8));
	}
}