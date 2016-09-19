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

package at.pcgamingfreaks.Bungee.Message;

import at.pcgamingfreaks.Message.MessageColor;

import net.md_5.bungee.api.ChatColor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageBuilderTest
{
	@Test
	public void testGenericClass()
	{
		at.pcgamingfreaks.Message.MessageBuilder messageBuilder = new MessageBuilder(new MessageComponent());
		messageBuilder.appendJson("");
		assertEquals("The test of the generic object should match", "§r§r", messageBuilder.getClassicMessage());
	}

	@Test
	public void testMessageBuilder()
	{
		List<MessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new MessageComponent("Test"));
		assertEquals("The message builder text should match", "Test§r", new MessageBuilder(messageComponents.get(0)).getClassicMessage());
		assertEquals("The message builder text should match", "Test§r", new MessageBuilder(messageComponents).getClassicMessage());
		assertEquals("The message builder text with message colors should match", "§9Text§r", new MessageBuilder("Text", MessageColor.BLUE).getClassicMessage());
		assertEquals("The message builder text with chat color styles should match", "§l§9Text§r", new MessageBuilder("Text", new ChatColor[] { ChatColor.BLUE, ChatColor.BOLD }).getClassicMessage());
	}

	@Test
	public void testMessageBuilderFromJson()
	{
		assertEquals("The message builder text from JSON should match", "test§r", MessageBuilder.fromJson("[{\"text\":\"test\"}]").getClassicMessage());
		assertEquals("The message builder text from JSON should match", "SimpleText§r", MessageBuilder.fromJson("SimpleText").getClassicMessage());
	}
}