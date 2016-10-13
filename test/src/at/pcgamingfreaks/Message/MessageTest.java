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

package at.pcgamingfreaks.Message;

import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.TestClasses.TestMessage;
import at.pcgamingfreaks.TestClasses.TestMessageComponent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Reflection.class })
public class MessageTest
{
	@Test
	public void testMessage()
	{
		List<TestMessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new TestMessageComponent("Message text"));
		TestMessage message1 = new TestMessage("A message text");
		TestMessage message2 = new TestMessage("Message text");
		TestMessage message3 = new TestMessage(messageComponents);
		message2.setOptionalParameters(5);
		assertEquals("The message texts should match", "Message text", message2.getClassicMessage());
		assertEquals("The other message texts should match", "Message text§r", message3.getClassicMessage());
		assertTrue("The messages should be equal", message2.equals(message3));
		assertEquals("The message hash code should match", message2.hashCode(), message3.hashCode());
		assertEquals("The message text should be correct", "[{\"text\":\"Message text\"}]", message3.toString());
		//noinspection deprecation
		assertEquals("The message components should be equal", messageComponents.toArray(), message3.getMessageComponents());
		assertEquals("The optional parameter of the message should match", 5, message2.optionalParameters);
		message1.replaceAll("A m", "M");
		message1.replaceAll("ext", "est");
		message2.replaceAll("ext", "est");
		assertEquals("The message texts should match", message1.getClassicMessage(), message2.getClassicMessage());
		//noinspection EqualsBetweenInconvertibleTypes
		assertFalse("A string should not equal a message object", message1.equals("False"));
		//noinspection EqualsBetweenInconvertibleTypes
		assertFalse("The messages should not be equal", message1.equals(message3));
	}

	@Test
	public void testMessageJSON()
	{
		TestMessage message = new TestMessage("{\"text\":\"Test\"}");
		assertEquals("The message text should match", "Test§r", message.getClassicMessage());
	}

	@Test
	public void testMessageWithError() throws Exception
	{
		assertEquals("The test message should be empty", "", new TestMessage("").getClassicMessage());
		mockStatic(Reflection.class);
		assertEquals("The test message should be empty", "", new TestMessage("").getClassicMessage());
	}
}