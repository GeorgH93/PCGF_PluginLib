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

package at.pcgamingfreaks.Updater;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpdateResultTest
{
	@Test
	public void testUpdateResult()
	{
		assertEquals("The update result enum value should match", UpdateResult.valueOf("SUCCESS"), UpdateResult.SUCCESS);
		assertEquals("The update result enum value should match", UpdateResult.valueOf("NO_UPDATE"), UpdateResult.NO_UPDATE);
		assertEquals("The update result enum value should match", UpdateResult.valueOf("DISABLED"), UpdateResult.DISABLED);
		assertEquals("The update result enum value should match", UpdateResult.valueOf("FAIL_DOWNLOAD"), UpdateResult.FAIL_DOWNLOAD);
		assertEquals("The update result enum value should match", UpdateResult.valueOf("FAIL_SERVER_OFFLINE"), UpdateResult.FAIL_SERVER_OFFLINE);
		assertEquals("The update result enum value should match", UpdateResult.valueOf("FAIL_NO_VERSION_FOUND"), UpdateResult.FAIL_NO_VERSION_FOUND);
		assertEquals("The update result enum value should match", UpdateResult.valueOf("FAIL_FILE_NOT_FOUND"), UpdateResult.FAIL_FILE_NOT_FOUND);
		assertEquals("The update result enum value should match", UpdateResult.valueOf("FAIL_API_KEY"), UpdateResult.FAIL_API_KEY);
		assertEquals("The update result enum value should match", UpdateResult.valueOf("UPDATE_AVAILABLE"), UpdateResult.UPDATE_AVAILABLE);
		assertEquals("The update result enum value should match", UpdateResult.valueOf("SUCCESS_DEPENDENCY_DOWNLOAD_FAILED"), UpdateResult.SUCCESS_DEPENDENCY_DOWNLOAD_FAILED);
	}
}