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

public class UpdateResultTest
{
	@Test
	public void testUpdateResult()
	{
		assertEquals("The update result enum value should match", UpdateResult.SUCCESS, UpdateResult.valueOf("SUCCESS"));
		assertEquals("The update result enum value should match", UpdateResult.NO_UPDATE, UpdateResult.valueOf("NO_UPDATE"));
		assertEquals("The update result enum value should match", UpdateResult.DISABLED, UpdateResult.valueOf("DISABLED"));
		assertEquals("The update result enum value should match", UpdateResult.FAIL_DOWNLOAD, UpdateResult.valueOf("FAIL_DOWNLOAD"));
		assertEquals("The update result enum value should match", UpdateResult.FAIL_SERVER_OFFLINE, UpdateResult.valueOf("FAIL_SERVER_OFFLINE"));
		assertEquals("The update result enum value should match", UpdateResult.FAIL_NO_VERSION_FOUND, UpdateResult.valueOf("FAIL_NO_VERSION_FOUND"));
		assertEquals("The update result enum value should match", UpdateResult.FAIL_FILE_NOT_FOUND, UpdateResult.valueOf("FAIL_FILE_NOT_FOUND"));
		assertEquals("The update result enum value should match", UpdateResult.FAIL_API_KEY, UpdateResult.valueOf("FAIL_API_KEY"));
		assertEquals("The update result enum value should match", UpdateResult.UPDATE_AVAILABLE, UpdateResult.valueOf("UPDATE_AVAILABLE"));
		assertEquals("The update result enum value should match", UpdateResult.SUCCESS_DEPENDENCY_DOWNLOAD_FAILED, UpdateResult.valueOf("SUCCESS_DEPENDENCY_DOWNLOAD_FAILED"));
	}
}