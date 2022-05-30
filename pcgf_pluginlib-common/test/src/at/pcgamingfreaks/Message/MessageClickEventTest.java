/*
 *   Copyright (C) 2022 GeorgH93
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

package at.pcgamingfreaks.Message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageClickEventTest
{
	@Test
	public void testGetAction()
	{
		MessageClickEvent clickEvent = new MessageClickEvent(MessageClickEvent.ClickEventAction.CHANGE_PAGE, "123");
		assertEquals(MessageClickEvent.ClickEventAction.CHANGE_PAGE, clickEvent.getAction());
	}

	@Test
	public void testSetAction()
	{
		MessageClickEvent clickEvent = new MessageClickEvent(MessageClickEvent.ClickEventAction.CHANGE_PAGE, "123");
		clickEvent.setAction(MessageClickEvent.ClickEventAction.OPEN_FILE);
		assertEquals(MessageClickEvent.ClickEventAction.OPEN_FILE, clickEvent.getAction());
		clickEvent.setAction(MessageClickEvent.ClickEventAction.OPEN_URL);
		assertEquals(MessageClickEvent.ClickEventAction.OPEN_URL, clickEvent.getAction());
		clickEvent.setAction(MessageClickEvent.ClickEventAction.RUN_COMMAND);
		assertEquals(MessageClickEvent.ClickEventAction.RUN_COMMAND, clickEvent.getAction());
		clickEvent.setAction(MessageClickEvent.ClickEventAction.SUGGEST_COMMAND);
		assertEquals(MessageClickEvent.ClickEventAction.SUGGEST_COMMAND, clickEvent.getAction());
		clickEvent.setAction(MessageClickEvent.ClickEventAction.valueOf("OPEN_URL"));
		assertEquals(MessageClickEvent.ClickEventAction.OPEN_URL, clickEvent.getAction());
	}

	@Test
	public void testGetValue()
	{
		MessageClickEvent clickEvent = new MessageClickEvent(MessageClickEvent.ClickEventAction.SUGGEST_COMMAND, "123");
		assertEquals("123", clickEvent.getValue());
	}

	@Test
	public void testSetValue()
	{
		MessageClickEvent clickEvent = new MessageClickEvent(MessageClickEvent.ClickEventAction.SUGGEST_COMMAND, "123");
		clickEvent.setValue("testValue");
		assertEquals("testValue", clickEvent.getValue());
	}
}