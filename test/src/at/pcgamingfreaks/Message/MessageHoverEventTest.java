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

import at.pcgamingfreaks.Bungee.Message.Message;
import at.pcgamingfreaks.Bungee.Message.MessageBuilder;
import at.pcgamingfreaks.Bungee.Message.MessageComponent;

import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageHoverEventTest
{
	JsonObject testObject;
	Message testMessage;
	MessageComponent[] testComponentArray;
	List<MessageComponent> testComponents;
	@Before
	public void testInit()
	{
		testObject = new JsonObject();
		testObject.addProperty("test", "test");
		MessageBuilder messageBuilder = new MessageBuilder("hi").append(" tester");
		testComponentArray = messageBuilder.getJsonMessage();
		testComponents = messageBuilder.getJsonMessageAsList();
		testMessage = messageBuilder.getMessage();
	}

	@Test
	public void testConstructors()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "test1");
		assertEquals(hoverEvent.getValue(), "test1");
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
		hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, testObject);
		assertEquals(hoverEvent.getValue(), testObject);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
		hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, testMessage);
		Assert.assertArrayEquals((Object[]) hoverEvent.getValue(), testComponentArray);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
		hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, testComponentArray);
		Assert.assertArrayEquals((Object[]) hoverEvent.getValue(), testComponentArray);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
		hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, testComponents);
		assertEquals(hoverEvent.getValue(), testComponents);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
	}

	@Test
	public void testGetAction()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "123");
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
	}

	@Test
	public void testSetAction()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "123");
		hoverEvent.setAction(MessageHoverEvent.HoverEventAction.SHOW_ITEM);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_ITEM);
		hoverEvent.setAction(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT);
		hoverEvent.setAction(MessageHoverEvent.HoverEventAction.SHOW_ENTITY);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_ENTITY);
		hoverEvent.setAction(MessageHoverEvent.HoverEventAction.valueOf("SHOW_ITEM"));
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_ITEM);
	}

	@Test
	public void testGetValue()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "123");
		assertEquals(hoverEvent.getValue(), "123");
	}

	@Test
	public void testSetValue()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "test1");
		hoverEvent.setValue("test2");
		assertEquals(hoverEvent.getValue(), "test2");
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
		hoverEvent.setValue(testObject);
		assertEquals(hoverEvent.getValue(), testObject);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
		hoverEvent.setValue(testMessage);
		Assert.assertArrayEquals((Object[]) hoverEvent.getValue(), testComponentArray);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
		hoverEvent.setValue(testComponentArray);
		Assert.assertArrayEquals((Object[]) hoverEvent.getValue(), testComponentArray);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
		hoverEvent.setValue(testComponents);
		assertEquals(hoverEvent.getValue(), testComponents);
		assertEquals(hoverEvent.getAction(), MessageHoverEvent.HoverEventAction.SHOW_TEXT);
	}
}