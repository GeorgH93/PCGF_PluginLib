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

package at.pcgamingfreaks.Bungee.Message.Sender;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SendMethodTest
{
	@Test
	public void testSendMethod() throws NoSuchMethodException
	{
		SendMethod sendMethod = SendMethod.CHAT;
		assertNull("The metadata class should be correct", sendMethod.getMetadataClass());
		assertNull("The from JSON method should be correct", sendMethod.getMetadataFromJsonMethod());
		sendMethod = SendMethod.TITLE;
		assertEquals("The metadata class should be correct", TitleMetadata.class, sendMethod.getMetadataClass());
		assertEquals("The from JSON method should be correct", TitleMetadata.class.getDeclaredMethod("fromJson", String.class), sendMethod.getMetadataFromJsonMethod());
	}
}