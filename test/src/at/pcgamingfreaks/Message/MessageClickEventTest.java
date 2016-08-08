/*
 * Copyright (C) 2016 GeorgH93
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

package at.pcgamingfreaks.Message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageClickEventTest
{
	@Test
	public void testGetAction()
	{
		MessageClickEvent clickEvent = new MessageClickEvent(MessageClickEvent.ClickEventAction.CHANGE_PAGE, "123");
		assertEquals(clickEvent.getAction(), MessageClickEvent.ClickEventAction.CHANGE_PAGE);
	}

	@Test
	public void testSetAction()
	{
		MessageClickEvent clickEvent = new MessageClickEvent(MessageClickEvent.ClickEventAction.CHANGE_PAGE, "123");
		clickEvent.setAction(MessageClickEvent.ClickEventAction.OPEN_FILE);
		assertEquals(clickEvent.getAction(), MessageClickEvent.ClickEventAction.OPEN_FILE);
		clickEvent.setAction(MessageClickEvent.ClickEventAction.OPEN_URL);
		assertEquals(clickEvent.getAction(), MessageClickEvent.ClickEventAction.OPEN_URL);
		clickEvent.setAction(MessageClickEvent.ClickEventAction.RUN_COMMAND);
		assertEquals(clickEvent.getAction(), MessageClickEvent.ClickEventAction.RUN_COMMAND);
		clickEvent.setAction(MessageClickEvent.ClickEventAction.SUGGEST_COMMAND);
		assertEquals(clickEvent.getAction(), MessageClickEvent.ClickEventAction.SUGGEST_COMMAND);
		clickEvent.setAction(MessageClickEvent.ClickEventAction.valueOf("OPEN_URL"));
		assertEquals(clickEvent.getAction(), MessageClickEvent.ClickEventAction.OPEN_URL);
	}

	@Test
	public void testGetValue()
	{
		MessageClickEvent clickEvent = new MessageClickEvent(MessageClickEvent.ClickEventAction.SUGGEST_COMMAND, "123");
		assertEquals(clickEvent.getValue(), "123");
	}

	@Test
	public void testSetValue()
	{
		MessageClickEvent clickEvent = new MessageClickEvent(MessageClickEvent.ClickEventAction.SUGGEST_COMMAND, "123");
		clickEvent.setValue("testValue");
		assertEquals(clickEvent.getValue(), "testValue");
	}
}