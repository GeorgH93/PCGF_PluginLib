/*
 *   Copyright (C) 2020 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class InventoryUtils_ReflectionTest
{
	private static boolean skipTests = !TestUtils.canMockJdkClasses();
	private static final TestBukkitServer server = skipTests ? null : new TestBukkitServer();

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		if (skipTests) return;
		server.allowPluginManager = true;
		Bukkit.setServer(server);
		TestObjects.initNMSReflection();
		TestUtils.initReflection();
	}

	@Test
	public void testConvertItemStackToJson()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		InventoryUtils_Reflection invUtils = new InventoryUtils_Reflection();
		Logger mockedLogger = spy(server.getLogger());
		ItemStack itemStack = new ItemStack(Material.STONE, 23);
		Assert.assertEquals("The converted ItemStack should match", "{\"id\":\"STONE\",\"Count\":\"23\"}", invUtils.convertItemStackToJson(itemStack, mockedLogger));
		verify(mockedLogger, times(0)).log(any(Level.class), anyString());
	}
}