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

package at.pcgamingfreaks.Bungee;

import at.pcgamingfreaks.TestClasses.TestObjects;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class ConfigurationTest
{
	@Before
	public void prepareTestObjects() throws Exception
	{
		TestObjects.initMockedPlugin();
	}

	@Test
	public void testConfiguration()
	{
		assertNotNull("The configuration should not be null", new Configuration(TestObjects.getPlugin(), 1));
		assertNotNull("The configuration should not be null", new Configuration(TestObjects.getPlugin(), 1, "config.yml"));
		assertNotNull("The configuration should not be null", new Configuration(TestObjects.getPlugin(), 1, 2));
	}

	@AfterClass
	public static void cleanupTestData()
	{
		//noinspection ResultOfMethodCallIgnored
		new File("\\config.yml").delete();
	}
}