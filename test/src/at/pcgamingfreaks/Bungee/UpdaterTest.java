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
import at.pcgamingfreaks.Updater.UpdateProviders.BukkitUpdateProvider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class UpdaterTest
{
	private static String status;

	private Runnable syncRunnable = new Runnable() {
		@Override
		public void run()
		{
			status = "SYNC";
		}
	};

	private Runnable asyncRunnable = new Runnable() {
		@Override
		public void run()
		{
			status = "ASYNC";
		}
	};

	@Before
	public void prepareTestObjects()
	{
		TestObjects.initMockedPlugin();
	}

	@Test
	public void testUpdater()
	{
		Updater updater = new Updater(TestObjects.getPlugin(), false, new BukkitUpdateProvider(2, TestObjects.getPlugin().getLogger()));
		assertEquals("The author should match", "", updater.getAuthor());
		when(updater.getAuthor()).thenReturn("MarkusWME");
		assertEquals("The author should match", "MarkusWME", updater.getAuthor());
		updater.waitForAsyncOperation();
		updater.runSync(syncRunnable);
		assertEquals("The sync runnable should be executed", "SYNC", status);
		updater.runAsync(asyncRunnable);
		assertEquals("The async runnable should be executed", "ASYNC", status);
	}
}