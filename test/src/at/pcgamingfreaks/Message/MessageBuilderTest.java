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

import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageBuilderTest
{
	static class TestMessageComponent extends MessageComponent<MessageComponent>
	{
		static
		{
			GSON = new GsonBuilder().registerTypeAdapter(TestMessageComponent.class, new TestMessageComponent()).create();
			messageComponentClass = TestMessageComponent.class;
			try
			{
				messageComponentConstructor = TestMessageComponent.class.getConstructor();
			}
			catch(NoSuchMethodException e)
			{
				e.printStackTrace();
			}
		}

		public TestMessageComponent() {}

		public TestMessageComponent(String text)
		{
			super(text);
		}

		@Override
		protected MessageComponent getNewLineComponent()
		{
			return new TestMessageComponent("\n");
		}
	}

	private static class TestMessage extends Message<Message>
	{
		protected TestMessage(Collection<? extends MessageComponent> message)
		{
			super(message);
		}
	}

	private static class TestMessageBuilder extends MessageBuilder<MessageBuilder>
	{
		TestMessageComponent messageComponent = new TestMessageComponent();

		@Override
		protected MessageComponent getCurrentComponent()
		{
			return messageComponent;
		}
	}

	@Test
	public void testMessageBuilder()
	{
		TestMessageBuilder messageBuilder = new TestMessageBuilder();
		messageBuilder.text("MessageBuilder text");
		messageBuilder.color("green");
		messageBuilder.bold().italic().underlined().obfuscated().strikethrough();
		messageBuilder.onClick(MessageClickEvent.ClickEventAction.RUN_COMMAND, "cmd run");
		assertEquals("The message text should match", "MessageBuilder text", messageBuilder.getCurrentComponent().getText());
		messageBuilder.file("C:\\Test\\Path");
		messageBuilder.link("http:\\test.url.org");
		assertEquals("The url should match", "http:\\test.url.org", messageBuilder.getCurrentComponent().getClickEvent().getValue());
		messageBuilder.suggest("cmd run");
		messageBuilder.command("cmd run");
		messageBuilder.insert("Inserted String");
		assertEquals("The insertion should match", "Inserted String", messageBuilder.getCurrentComponent().getInsertion());
		messageBuilder.onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "Hover text");
		List<TestMessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new TestMessageComponent("Another hover text"));
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
}