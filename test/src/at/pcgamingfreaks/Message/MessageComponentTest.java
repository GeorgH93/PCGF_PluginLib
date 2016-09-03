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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class MessageComponentTest
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

		public TestMessageComponent(String text, MessageColor... styles)
		{
			super(text, styles);
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

	@Test
	public void testGetClassicMessage()
	{
		List<MessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new TestMessageComponent("test message "));
		messageComponents.add(new TestMessageComponent("with extras"));
		TestMessageComponent messageComponent = new TestMessageComponent("This is a ");
		messageComponent.setExtras(messageComponents);
		//noinspection SpellCheckingInspection
		assertEquals("The text should match", "This is a test message §rwith extras§r§r", messageComponent.getClassicMessage());
		//noinspection SpellCheckingInspection
		assertEquals("The text of the static function should match", "test message §rwith extras§r", MessageComponent.getClassicMessage(messageComponents));
		assertEquals("The extras should be found", messageComponents, messageComponent.getExtras());
	}

	@Test
	public void testFormats()
	{
		TestMessageComponent messageComponent = new TestMessageComponent("This is a text");
		messageComponent.bold().italic().obfuscated().strikethrough().underlined().color(MessageColor.BLACK).insert("Insertion");
		assertTrue("The message should be bold", messageComponent.isBold());
		assertEquals("The message should be black", MessageColor.BLACK, messageComponent.getColor());
		assertEquals("The color of the message should be black", "black", messageComponent.getColorString());
		assertTrue("The message should be italic", messageComponent.isItalic());
		assertTrue("The message should be obfuscated", messageComponent.isObfuscated());
		assertTrue("The message should be strikethrough", messageComponent.isStrikethrough());
		assertTrue("The message should be underlined", messageComponent.isUnderlined());
		assertEquals("The insertion should be correct", "Insertion", messageComponent.getInsertion());
		messageComponent.setObfuscated(false);
		messageComponent.setStrikethrough(false);
		assertFalse("The message should not be obfuscated", messageComponent.isObfuscated());
		assertFalse("The message should not be strikethrough", messageComponent.isStrikethrough());
		messageComponent.color("Aqua");
		assertEquals("The message color should be aqua", MessageColor.AQUA, messageComponent.getColor());
	}

	@Test
	public void testEvents()
	{
		TestMessageComponent messageComponent = new TestMessageComponent();
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, "achievement");
		messageComponent.setClickEvent(null).setHoverEvent(hoverEvent);
		assertNull("The message should be null", messageComponent.getText());
		assertNull("The click event should be null", messageComponent.getClickEvent());
		assertEquals("The hover event should be returned", hoverEvent, messageComponent.getHoverEvent());
		messageComponent.setHoverEvent(null);
		assertEquals("The JSON of the message should be empty", "{}", messageComponent.toString());
		messageComponent.file("C:\\Test\\Path");
		assertEquals("The click action should be as given", MessageClickEvent.ClickEventAction.OPEN_FILE, messageComponent.getClickEvent().getAction());
		assertEquals("The file path should match", "C:\\Test\\Path", messageComponent.getClickEvent().getValue());
		messageComponent.link("http:\\\\test.page.org");
		assertEquals("The click action should be as given", MessageClickEvent.ClickEventAction.OPEN_URL, messageComponent.getClickEvent().getAction());
		assertEquals("The link url should match", "http:\\\\test.page.org", messageComponent.getClickEvent().getValue());
		messageComponent.suggest("cmd run");
		assertEquals("The click action should be as given", MessageClickEvent.ClickEventAction.SUGGEST_COMMAND, messageComponent.getClickEvent().getAction());
		assertEquals("The given command should be suggested", "cmd run", messageComponent.getClickEvent().getValue());
		messageComponent.command("cmd run");
		assertEquals("The click action should be as given", MessageClickEvent.ClickEventAction.RUN_COMMAND, messageComponent.getClickEvent().getAction());
		assertEquals("The given command should be found", "cmd run", messageComponent.getClickEvent().getValue());
		messageComponent.onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "Text 1");
		assertEquals("The hover message text should match", "Text 1", messageComponent.getHoverEvent().getValue());
		List<MessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(messageComponent);
		messageComponent.onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, messageComponents);
		assertEquals("The hover message should be the current message object", messageComponents, messageComponent.getHoverEvent().getValue());
		messageComponent.achievementTooltip("Achievement");
		assertEquals("The achievement value should match", "achievement.Achievement", messageComponent.getHoverEvent().getValue());
		messageComponent.statisticTooltip("Statistic");
		assertEquals("The statistic value should match", "stat.Statistic", messageComponent.getHoverEvent().getValue());
		messageComponent.itemTooltip("{}");
		assertEquals("The hover action should match", MessageHoverEvent.HoverEventAction.SHOW_ITEM, messageComponent.getHoverEvent().getAction());
		messageComponent.tooltip("First line", "Second one");
		assertEquals("The hover message text should match", "First line\nSecond one", messageComponent.getHoverEvent().getValue());
		messageComponent.formattedTooltip(new TestMessageComponent("Test text"));
		assertEquals("The hover message text should match the formatted tooltip text", "{\"text\":\"Test text\"}", messageComponent.getHoverEvent().getValue());
		messageComponents.clear();
		messageComponent.formattedTooltip((Message[]) new TestMessage[0]);
		assertNull("The hover event should be null", messageComponent.getHoverEvent());
		messageComponents.add(new TestMessageComponent("Another text"));
		messageComponent.formattedTooltip(new TestMessage(messageComponents), new TestMessage(messageComponents));
		assertEquals("The hover message text should match the formatted tooltip text", "[{\"text\":\"Another text\"}, {\"text\":\"\\n\"}, {\"text\":\"Another text\"}]", messageComponent.getHoverEvent().getValue().toString());
	}

	@Test
	public void testExtraAndFormat()
	{
		TestMessageComponent messageComponent = new TestMessageComponent();
		messageComponent.text("Text");
		assertEquals("The text of the message component should be correct", "Text", messageComponent.getText());
		messageComponent.extra(new TestMessageComponent("another extra"));
		//noinspection SpellCheckingInspection
		assertEquals("The text should match", "Textanother extra§r§r", messageComponent.getClassicMessage());
		messageComponent.format(MessageColor.UNDERLINE, MessageColor.ITALIC, MessageColor.BOLD, MessageColor.STRIKETHROUGH);
		assertTrue("The message should be underlined", messageComponent.isUnderlined());
		assertFalse("The magic flag should not be set", messageComponent.isObfuscated());
		messageComponent.style(MessageColor.MAGIC, MessageColor.GREEN);
		assertTrue("The magic flag should now be set", messageComponent.isObfuscated());
		assertEquals("The color of the message should be green", "green", messageComponent.getColorString());
	}

	@Test
	public void testDeserialize()
	{
		Gson gson = new Gson();
		JsonElement json = gson.fromJson("{\"text\":\"JSON text\"}", JsonElement.class);
		TestMessageComponent messageComponent = new TestMessageComponent();
		MessageComponent component = messageComponent.deserialize(json, null, null);
		assertEquals("The text should match", "JSON text", component.getText());
	}

	@Test
	public void testDeserializeWithError() throws NoSuchMethodException
	{
		Gson gson = new Gson();
		JsonElement json = gson.fromJson("{\"text\":\"Another JSON text\"}", JsonElement.class);
		TestMessageComponent messageComponent = new TestMessageComponent();
		MessageComponent.messageComponentConstructor = null;
		assertNull(messageComponent.deserialize(json, null, null));
		MessageComponent.messageComponentConstructor = TestMessageComponent.class.getConstructor();
	}

	@Test
	public void testFromJsonWorker() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		TestMessageComponent messageComponent = new TestMessageComponent();
		Method fromJsonWorker = MessageComponent.class.getDeclaredMethod("fromJsonWorker", String.class);
		fromJsonWorker.setAccessible(true);
		//noinspection unchecked
		TestMessage message = new TestMessage((Collection<? extends MessageComponent>) fromJsonWorker.invoke(messageComponent, "[[\"St\", \"ring \"], {\"text\":\"from JSON worker\"}]"));
		fromJsonWorker.setAccessible(false);
		//noinspection SpellCheckingInspection
		assertEquals("The message text should match", "St§rring §rfrom JSON worker§r", message.getClassicMessage());
	}

	@Test
	public void testFromJsonArrayWorkerWithError() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		TestMessageComponent messageComponent = new TestMessageComponent();
		Method fromJsonWorker = MessageComponent.class.getDeclaredMethod("fromJsonWorker", String.class);
		fromJsonWorker.setAccessible(true);
		MessageComponent.messageComponentConstructor = null;
		//noinspection unchecked
		assertTrue("There should not be returned any message components", ((List<TestMessageComponent>) fromJsonWorker.invoke(messageComponent, "[\"String\"]")).size() == 0);
		MessageComponent.messageComponentConstructor = TestMessageComponent.class.getConstructor();
		fromJsonWorker.setAccessible(false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetColor()
	{
		new TestMessageComponent("Message").setColor(MessageColor.STRIKETHROUGH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetFormats()
	{
		new TestMessageComponent().setFormats(MessageColor.BLUE);
	}
}