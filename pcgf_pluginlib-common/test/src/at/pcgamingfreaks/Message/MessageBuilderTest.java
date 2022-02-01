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

package at.pcgamingfreaks.Message;

import at.pcgamingfreaks.TestClasses.TestMessage;
import at.pcgamingfreaks.TestClasses.TestMessageBuilder;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MessageBuilderTest
{
	private static MessageComponent initComponent;

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		initComponent = new MessageComponent("Init");
		TestUtils.initReflection();
	}

	@Test
	public void testMessageBuilder()
	{
		TestMessageBuilder messageBuilder = new TestMessageBuilder(new MessageComponent());
		messageBuilder.text("MessageBuilder text");
		messageBuilder.color("green");
		messageBuilder.bold().italic().underlined().obfuscated().strikethrough();
		messageBuilder.onClick(MessageClickEvent.ClickEventAction.RUN_COMMAND, "cmd run");
		assertEquals("The message text should match", "MessageBuilder text", messageBuilder.getCurrentComponent().getText());
		messageBuilder.file("C:\\Test\\Path");
		messageBuilder.link("https:\\test.url.org");
		assertEquals("The url should match", "https:\\test.url.org", messageBuilder.getCurrentComponent().getClickEvent().getValue());
		messageBuilder.suggest("cmd run");
		messageBuilder.command("cmd run");
		messageBuilder.insert("Inserted String");
		assertEquals("The insertion should match", "Inserted String", messageBuilder.getCurrentComponent().getInsertion());
		messageBuilder.onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "Hover text");
		List<MessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new MessageComponent("Another hover text"));
		messageBuilder.onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, messageComponents);
		assertEquals("The hover text should match", messageComponents, messageBuilder.getCurrentComponent().getHoverEvent().getValue());
		messageBuilder.achievementTooltip("Achievement");
		messageBuilder.statisticTooltip("Statistic");
		messageBuilder.itemTooltip("[{\"text\":\"Item tooltip\"}]");
		assertEquals("The hover action should match", MessageHoverEvent.HoverEventAction.SHOW_ITEM, messageBuilder.getCurrentComponent().getHoverEvent().getAction());
		messageBuilder.tooltip("Tooltip text");
		messageBuilder.formattedTooltip(messageComponents.get(0));
		messageBuilder.formattedTooltip(new TestMessage(messageComponents));
		assertEquals("The tooltip should match", messageComponents, messageBuilder.getCurrentComponent().getHoverEvent().getValue());
		messageBuilder.extra(messageComponents.get(0));
		assertEquals("The message text should match", "§l§o§k§n§m§aMessageBuilder textAnother hover text§r§r", messageBuilder.getCurrentComponent().getClassicMessage());
	}

	@Test
	public void testMessageBuilderSpecialCases()
	{
		TestMessageBuilder messageBuilder = new TestMessageBuilder(new MessageComponent("MessageComponent"));
		assertEquals("The message string should match", "MessageComponent§r", messageBuilder.getClassicMessage());
		messageBuilder.appendNewLine();
		assertEquals("The message string should match", "MessageComponent\n§r§r", messageBuilder.getClassicMessage());
		messageBuilder.append(new MessageComponent("New component"));
		assertEquals("The message component count should match", 2, messageBuilder.size());
	}

	@Test
	public void testGetMessage() throws NoSuchFieldException, IllegalAccessException
	{
		TestMessageBuilder messageBuilder = new TestMessageBuilder(initComponent);
		messageBuilder.color(MessageColor.BLUE).color(MessageColor.DARK_RED).format(MessageFormat.BOLD).format(MessageFormat.UNDERLINE).format(MessageFormat.MAGIC).format(MessageFormat.MAGIC);
		assertEquals("The message should match", "§l§k§n§4Init§r", messageBuilder.getMessage().getClassicMessage());
		Field constructor = TestUtils.setAccessible(MessageBuilder.class, null, "MESSAGE_CONSTRUCTOR", null);
		assertNull("The creation of a new message should fail", messageBuilder.getMessage());
		TestUtils.setUnaccessible(constructor, null, false);
	}
}