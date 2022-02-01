/*
 *   Copyright (C) 2021 GeorgH93
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

import at.pcgamingfreaks.TestClasses.TestMessage;
import at.pcgamingfreaks.TestClasses.TestMessageBuilder;

import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageHoverEventTest
{
	private static JsonObject testObject;
	private static TestMessage testMessage;
	private static MessageComponent[] testComponentArray;
	private static List<? extends MessageComponent> testComponents;

	@BeforeClass
	public static void prepareTestData()
	{
		testObject = new JsonObject();
		testObject.addProperty("test", "test");
		TestMessageBuilder messageBuilder = new TestMessageBuilder(new MessageComponent("hi")).append(" tester");
		testComponentArray = messageBuilder.getJsonMessage();
		testComponents = messageBuilder.getJsonMessageAsList();
		testMessage = new TestMessage(testComponents);
	}

	@Test
	public void testConstructors()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "test1");
		assertEquals("test1", hoverEvent.getValue());
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
		hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, testObject);
		assertEquals(testObject, hoverEvent.getValue());
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
		hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, testMessage);
		Assert.assertArrayEquals(testComponentArray, (Object[]) hoverEvent.getValue());
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
		hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, testComponentArray);
		Assert.assertArrayEquals((Object[]) hoverEvent.getValue(), testComponentArray);
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
		hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, testComponents);
		assertEquals(testComponents, hoverEvent.getValue());
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
	}

	@Test
	public void testGetAction()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "123");
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
	}

	@Test
	public void testSetAction()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "123");
		hoverEvent.setAction(MessageHoverEvent.HoverEventAction.SHOW_ITEM);
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_ITEM, hoverEvent.getAction());
		hoverEvent.setAction(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT);
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, hoverEvent.getAction());
		hoverEvent.setAction(MessageHoverEvent.HoverEventAction.SHOW_ENTITY);
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_ENTITY, hoverEvent.getAction());
		hoverEvent.setAction(MessageHoverEvent.HoverEventAction.valueOf("SHOW_ITEM"));
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_ITEM, hoverEvent.getAction());
	}

	@Test
	public void testGetValue()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "123");
		assertEquals("123", hoverEvent.getValue());
	}

	@Test
	public void testSetValue()
	{
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "test1");
		hoverEvent.setValue("test2");
		assertEquals("test2", hoverEvent.getValue());
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
		hoverEvent.setValue(testObject);
		assertEquals(testObject, hoverEvent.getValue());
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
		hoverEvent.setValue(testMessage);
		Assert.assertArrayEquals((Object[]) hoverEvent.getValue(), testComponentArray);
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
		hoverEvent.setValue(testComponentArray);
		Assert.assertArrayEquals((Object[]) hoverEvent.getValue(), testComponentArray);
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
		hoverEvent.setValue(testComponents);
		assertEquals(testComponents, hoverEvent.getValue());
		assertEquals(MessageHoverEvent.HoverEventAction.SHOW_TEXT, hoverEvent.getAction());
	}
}