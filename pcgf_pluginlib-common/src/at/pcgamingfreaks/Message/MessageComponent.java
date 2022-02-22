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

import at.pcgamingfreaks.Util.PatternPreservingStringSplitter;

import com.google.gson.*;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({ "UnusedReturnValue" })
public class MessageComponent implements Serializable
{
	//region JSON Variables
	protected MessageClickEvent clickEvent = null;
	protected MessageHoverEvent hoverEvent = null;
	protected String text, insertion;
	protected MessageColor color;
	protected Boolean bold, italic, underlined, strikethrough, obfuscated;
	protected List<MessageComponent> extra = null;

	/**
	 * The font of the component. null = default font
	 */
	@Getter @Nullable protected String font = null;

	@SuppressWarnings("unused")
	protected Object selector, score, translate; // We don't use them now, maybe later
	@SuppressWarnings("unused")
	protected List<Object> with; // Only for translate
	//endregion

	/**
	 * Gets the JSON string of the component.
	 *
	 * @return The JSON string of the component.
	 */
	@Override
	public String toString()
	{
		return GSON.toJson(this);
	}

	//region Constructors
	/**
	 * Creates a new empty MessageComponent instance.
	 */
	public MessageComponent() {}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text    The text for the {@link MessageComponent}.
	 * @param formats The style for the {@link MessageComponent}.
	 */
	public MessageComponent(final String text, MessageFormat... formats)
	{
		setText(text);
		if(formats != null) setFormats(formats);
	}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text    The text for the {@link MessageComponent}.
	 * @param color   The color for the {@link MessageComponent}.
	 * @param formats The style for the {@link MessageComponent}.
	 */
	public MessageComponent(final String text, final @Nullable MessageColor color, MessageFormat... formats)
	{
		setText(text);
		if(color != null) setColor(color);
		if(formats != null) setFormats(formats);
	}
	//endregion

	//region converting JSON message into a classic mc chat message
	/**
	 * Converts the JSON message component into a classic chat message.
	 *
	 * @return The JSON message component in the classic chat format.
	 */
	public String getClassicMessage()
	{
		StringBuilder stringBuilder = new StringBuilder(getClassicFormats());
		if(text != null) stringBuilder.append(text);
		if (extra != null)
		{
			for (MessageComponent e : extra)
			{
				stringBuilder.append(e.getClassicMessage());
			}
		}
		stringBuilder.append(MessageColor.RESET);
		return stringBuilder.toString();
	}

	/**
	 * Converts a {@link Collection} of MessageComponent's into a classic chat message.
	 *
	 * @param messageList The message components that should be converted into a classic minecraft chat message.
	 * @return The JSON message component in the classic chat format.
	 */
	public static @NotNull String getClassicMessage(Collection<? extends MessageComponent> messageList)
	{
		if(messageList == null) return ""; // If we don't have a JSON we can't calculate the classic message from it, so we will use an empty message
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		String classFormat = "";
		for(MessageComponent messageComponent : messageList)
		{
			if(first) // The first json objects format data is used for every following component (as long as they don't set their own stuff)
			{
				classFormat = messageComponent.getClassicFormats(); // We have to store the classic format from the first element
				first = false;
			}
			else
			{
				builder.append(classFormat); // Now we append the classic format to our string, followed by the real component
			}
			builder.append(messageComponent.getClassicMessage());
		}
		return builder.toString();
	}

	/**
	 * Gets the formats of the component in the classic way (§[color/style-code]).
	 *
	 * @return The classic format string of the component.
	 */
	public String getClassicFormats()
	{
		StringBuilder stringBuilder = new StringBuilder();
		if (isBold()) stringBuilder.append(MessageFormat.BOLD);
		if (isItalic()) stringBuilder.append(MessageFormat.ITALIC);
		if (isObfuscated()) stringBuilder.append(MessageFormat.MAGIC);
		if (isUnderlined()) stringBuilder.append(MessageFormat.UNDERLINE);
		if (isStrikethrough()) stringBuilder.append(MessageFormat.STRIKETHROUGH);
		if (color != null) stringBuilder.append(getColor().toString().toLowerCase(Locale.ROOT));
		return stringBuilder.toString();
	}
	//endregion

	//region Message modifier (getter/setter)
	/**
	 * Gets the click event of the component.
	 *
	 * @return The click event of the component.
	 */
	public MessageClickEvent getClickEvent()
	{
		return clickEvent;
	}

	/**
	 * Sets the click event of the component.
	 * The click event gets used when the client clicks the component.
	 *
	 * @param clickEvent The click event for the component.
	 * @return This message component instance.
	 */
	public MessageComponent setClickEvent(MessageClickEvent clickEvent)
	{
		this.clickEvent = clickEvent;
		return this;
	}

	/**
	 * Gets the hover event of the component.
	 *
	 * @return The hover event of the component.
	 */
	public MessageHoverEvent getHoverEvent()
	{
		return hoverEvent;
	}

	/**
	 * Sets the hover event of the component.
	 * The hover event gets used when the client hovers over the component.
	 *
	 * @param hoverEvent The hover event for the component.
	 * @return This message component instance.
	 */
	public MessageComponent setHoverEvent(MessageHoverEvent hoverEvent)
	{
		this.hoverEvent = hoverEvent;
		return this;
	}

	/**
	 * Gets the text of the component.
	 *
	 * @return The text of the component.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Sets the text of the component.
	 *
	 * @param text The new text of the component.
	 * @return This message component instance.
	 */
	public MessageComponent setText(String text)
	{
		this.text = text;
		return this;
	}

	/**
	 * Gets the color of the component.
	 *
	 * @return The color of the component as a {@link MessageColor}, null if no color is defined.
	 */
	public MessageColor getColor()
	{
		return color;
	}

	/**
	 * Gets the color of the component.
	 *
	 * @return The color of the component as a {@link String}, null if no color is defined.
	 */
	public String getColorString()
	{
		return getColor().name().toLowerCase(Locale.ROOT);
	}

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 */
	public MessageComponent setColor(final @NotNull String color)
	{
		MessageColor c = MessageColor.getColor(color);
		if(c == null) throw new IllegalArgumentException(color + " not a valid color!");
		return setColor(c);
	}

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).
	 */
	public MessageComponent setColor(MessageColor color) throws IllegalArgumentException
	{
		this.color = color == MessageColor.RESET ? null : color;
		return this;
	}

	/**
	 * Gets the behavior of the component if it gets shift-clicked.
	 *
	 * @return null if nothing should happen, otherwise the text that will be added to the players chat bar.
	 */
	public String getInsertion()
	{
		return insertion;
	}

	/**
	 * Set the behavior of the component to instruct the client to append the chat input box content with the specified string when the component is shift-clicked.
	 * The client will not immediately send the command to the server to be executed unless the client player submits the command/chat message, usually with the enter key.
	 *
	 * @param insertionText The text to append to the chat bar of the client.
	 * @return This message component instance.
	 */
	public MessageComponent setInsertion(String insertionText)
	{
		insertion = (insertionText.length() > 100) ? insertionText.substring(0, 100) : insertionText;
		return this;
	}

	/**
	 * Checks if the component is bold.
	 *
	 * @return true if the component is bold, false if not.
	 */
	public boolean isBold()
	{
		return bold != null && bold;
	}

	/**
	 * Sets the component to be bold.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent setBold()
	{
		return setBold(true);
	}

	/**
	 * Sets the component to be bold.
	 *
	 * @param bold Defines if the component should or should not be bold.
	 * @return This message component instance.
	 */
	public MessageComponent setBold(boolean bold)
	{
		this.bold = bold;
		return this;
	}

	/**
	 * Checks if the component is italic.
	 *
	 * @return true if the component is italic, false if not.
	 */
	public boolean isItalic()
	{
		return italic != null && italic;
	}

	/**
	 * Sets the component to be italic.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent setItalic()
	{
		return setItalic(true);
	}

	/**
	 * Sets the component to be italic.
	 *
	 * @param italic Defines if the component should or should not be italic.
	 * @return This message component instance.
	 */
	public MessageComponent setItalic(boolean italic)
	{
		this.italic = italic;
		return this;
	}

	/**
	 * Checks if the component is underlined.
	 *
	 * @return true if the component is underlined, false if not.
	 */
	public boolean isUnderlined()
	{
		return underlined != null && underlined;
	}

	/**
	 * Sets the component to be underlined.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent setUnderlined()
	{
		return setUnderlined(true);
	}

	/**
	 * Sets the component to be underlined.
	 *
	 * @param underlined Defines if the component should or should not be underlined.
	 * @return This message component instance.
	 */
	public MessageComponent setUnderlined(boolean underlined)
	{
		this.underlined = underlined;
		return this;
	}

	/**
	 * Checks if the component is strikethrough.
	 *
	 * @return true if the component is strikethrough, false if not.
	 */
	public boolean isStrikethrough()
	{
		return strikethrough != null && strikethrough;
	}

	/**
	 * Sets the component to be strikethrough.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent setStrikethrough()
	{
		return setStrikethrough(true);
	}

	/**
	 * Sets the component to be strikethrough.
	 *
	 * @param strikethrough Defines if the component should or should not be strikethrough.
	 * @return This message component instance.
	 */
	public MessageComponent setStrikethrough(boolean strikethrough)
	{
		this.strikethrough = strikethrough;
		return this;
	}

	/**
	 * Checks if the component is obfuscated.
	 *
	 * @return true if the component is obfuscated, false if not.
	 */
	public boolean isObfuscated()
	{
		return obfuscated != null && obfuscated;
	}

	/**
	 * Sets the component to be obfuscated.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent setObfuscated()
	{
		return setObfuscated(true);
	}

	/**
	 * Sets the component to be obfuscated.
	 *
	 * @param obfuscated Defines if the component should or should not be obfuscated.
	 * @return This message component instance.
	 */
	public MessageComponent setObfuscated(boolean obfuscated)
	{
		this.obfuscated = obfuscated;
		return this;
	}

	/**
	 * Gets all the extras of the component.
	 *
	 * @return A list of MessageComponents used as extras for the component.
	 */
	public List<? extends MessageComponent> getExtras()
	{
		return extra;
	}

	/**
	 * Sets all the extras of the component.
	 *
	 * @param extras A list of MessageComponents used as extras for the component.
	 * @return This message component instance.
	 */
	public MessageComponent setExtras(List<MessageComponent> extras)
	{
		extra = extras;
		return this;
	}

	/**
	 * Adds an inherited string to the component.
	 *
	 * @param extras The extras to be added to the component.
	 * @return This message component instance.
	 */
	public MessageComponent addExtra(MessageComponent... extras)
	{
		if(extras != null)
		{
			for(MessageComponent extra : extras)
			{
				if (extra == null) continue;
				if(this.extra == null)
				{
					this.extra = new ArrayList<>();
				}
				this.extra.add(extra);
			}
		}
		return this;
	}

	/**
	 * Sets the formats of the component.
	 *
	 * @param formats The array of formats to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public MessageComponent setFormats(MessageFormat... formats)
	{
		if(formats != null)
		{
			for(MessageFormat format : formats)
			{
				switch(format)
				{
					case ITALIC: setItalic(); break;
					case BOLD: setBold(); break;
					case UNDERLINE: setUnderlined(); break;
					case STRIKETHROUGH: setStrikethrough(); break;
					case MAGIC: setObfuscated(); break;
					case RESET: italic = bold = underlined = strikethrough = obfuscated = false; break;
				}
			}
		}
		return this;
	}


	/**
	 * Sets the formats of the component.
	 *
	 * @param formats The collection of formats to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public MessageComponent setFormats(Collection<MessageFormat> formats)
	{
		formats.forEach(this::setFormats);
		return this;
	}

	/**
	 * Sets the font of the component.
	 *
	 * @param font The name of the font that should be used.
	 * @return This message component instance.
	 */
	public MessageComponent setFont(final @Nullable String font)
	{
		this.font = font;
		return this;
	}
	//endregion

	//region Short message modifier (setter)
	/**
	 * Sets the text of the component.
	 *
	 * @param text The new text of the component.
	 * @return This message component instance.
	 */
	public MessageComponent text(String text)
	{
		return setText(text);
	}

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 */
	public MessageComponent color(String color)
	{
		return setColor(color);
	}

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).
	 */
	public MessageComponent color(MessageColor color) throws IllegalArgumentException
	{
		return setColor(color);
	}

	/**
	 * Sets the format of the component.
	 *
	 * @param formats The array of format to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public MessageComponent format(MessageFormat... formats) throws IllegalArgumentException
	{
		return setFormats(formats);
	}

	/**
	 * Sets the format of the component.
	 *
	 * @param formats The array of format to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public MessageComponent format(Collection<MessageFormat> formats) throws IllegalArgumentException
	{
		return setFormats(formats);
	}

	/**
	 * Sets the font of the component.
	 *
	 * @param font The name of the font that should be used.
	 * @return This message component instance.
	 */
	public MessageComponent font(final @Nullable String font)
	{
		return setFont(font);
	}

	/**
	 * Sets the component to be bold.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent bold()
	{
		return setBold();
	}

	/**
	 * Sets the component to be italic.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent italic()
	{
		return setItalic();
	}

	/**
	 * Sets the component to be underlined.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent underlined()
	{
		return setUnderlined();
	}

	/**
	 * Sets the component to be obfuscated.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent obfuscated()
	{
		return setObfuscated();
	}

	/**
	 * Sets the component to be strikethrough.
	 *
	 * @return This message component instance.
	 */
	public MessageComponent strikethrough()
	{
		return setStrikethrough();
	}

	/**
	 * Set the behavior of the component when the client clicks on it.
	 *
	 * @param action the action the client should execute.
	 * @param value the value the client should use for the action.
	 * @return This message component instance.
	 */
	public MessageComponent onClick(MessageClickEvent.ClickEventAction action, String value)
	{
		return setClickEvent(new MessageClickEvent(action, value));
	}

	/**
	 * Set the behavior of the component to instruct the client to open a file on the client side filesystem when the component is clicked.
	 *
	 * @param path The path of the file on the clients' filesystem.
	 * @return This message component instance.
	 */
	public MessageComponent file(String path)
	{
		return onClick(MessageClickEvent.ClickEventAction.OPEN_FILE, path);
	}

	/**
	 * Set the behavior of the component to instruct the client to open a web-page in the clients web browser when the component is clicked.
	 *
	 * @param url The URL of the page to open when the link is clicked.
	 * @return This message component instance.
	 */
	public MessageComponent link(String url)
	{
		return onClick(MessageClickEvent.ClickEventAction.OPEN_URL, url);
	}

	/**
	 * Set the behavior of the component to instruct the client to replace the chat input box content with the specified string when the component is clicked.
	 * The client will not immediately send the command to the server to be executed unless the client player submits the command/chat message, usually with the enter key.
	 *
	 * @param command The text to display in the chat bar of the client.
	 * @return This message component instance.
	 */
	public MessageComponent suggest(String command)
	{
		return onClick(MessageClickEvent.ClickEventAction.SUGGEST_COMMAND, command);
	}

	/**
	 * Set the behavior of the component to instruct the client to send the specified string to the server as a chat message when the component is clicked.
	 * The client <b>will</b> immediately send the command to the server to be executed when the editing component is clicked.
	 *
	 * @param command The text to display in the chat bar of the client.
	 * @return This message component instance.
	 */
	public MessageComponent command(final String command)
	{
		return onClick(MessageClickEvent.ClickEventAction.RUN_COMMAND, command);
	}

	/**
	 * Set the behavior of the component to instruct the client to append the chat input box content with the specified string when the component is shift-clicked.
	 * The client will not immediately send the command to the server to be executed unless the client player submits the command/chat message, usually with the enter key.
	 *
	 * @param insert The text to append to the chat bar of the client.
	 * @return This message component instance.
	 */
	public MessageComponent insert(String insert)
	{
		return setInsertion(insert);
	}

	/**
	 * Set the behavior of the component when the client hovers with the mouse over it.
	 *
	 * @param action the action the client should execute.
	 * @param value the value the client should use for the action.
	 * @return This message component instance.
	 */
	public MessageComponent onHover(final @NotNull MessageHoverEvent.HoverEventAction action, final @Nullable String value)
	{
		if(value == null) return this;
		return setHoverEvent(new MessageHoverEvent(action, value));
	}

	/**
	 * Set the behavior of the component when the client hovers with the mouse over it.
	 *
	 * @param action the action the client should execute.
	 * @param value the value the client should use for the action.
	 * @return This message component instance.
	 */
	public MessageComponent onHover(MessageHoverEvent.HoverEventAction action, Collection<? extends MessageComponent> value)
	{
		return setHoverEvent(new MessageHoverEvent(action, value));
	}

	/**
	 * Set the behavior of the component to display information about an achievement when the client hovers over the text.
	 *
	 * @param name The name of the achievement to display, excluding the "achievement." prefix.
	 * @return This message component instance.
	 */
	public MessageComponent achievementTooltip(String name)
	{
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, "achievement." + name);
	}

	/**
	 * Set the behavior of the component to display information about a statistic when the client hovers over the text.
	 *
	 * @param name The name of the statistic to display, excluding the "stat." prefix.
	 * @return This message component instance.
	 */
	public MessageComponent statisticTooltip(String name)
	{
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, "stat." + name);
	}

	/**
	 * Set the behavior of the component to display information about an item when the client hovers over the text.
	 *
	 * @param itemJSON A string representing the JSON-serialized NBT data tag of an ItemStack.
	 * @return This message component instance.
	 */
	public MessageComponent itemTooltip(String itemJSON)
	{
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_ITEM, itemJSON);
	}

	/**
	 * Set the behavior of the component to display raw text when the client hovers over the text.
	 *
	 * @param lines The lines of text which will be displayed to the client upon hovering.
	 * @return This message component instance.
	 */
	public MessageComponent tooltip(String... lines)
	{
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < lines.length; i++)
		{
			if(i > 0)
			{
				builder.append('\n');
			}
			builder.append(lines[i]);
		}
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, builder.toString());
	}

	/**
	 * Set the behavior of the component to display formatted text when the client hovers over the text.
	 *
	 * @param text The formatted text which will be displayed to the client upon hovering.
	 * @return This message component instance.
	 */
	public MessageComponent formattedTooltip(MessageComponent... text) throws IllegalArgumentException
	{
		StringBuilder builder = new StringBuilder();
		for(MessageComponent t : text)
		{
			if(t.getClickEvent() != null) throw new IllegalArgumentException("The tooltip text cannot have click data.");
			else if(t.getHoverEvent() != null) throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
			builder.append(t.toString());
		}
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, builder.toString());
	}

	public MessageComponent getNewLineComponent()
	{
		return new MessageComponent("\n");
	}

	public static MessageComponent makeNewLineComponent()
	{
		return new MessageComponent("\n");
	}

	/**
	 * Set the behavior of the component to display the specified lines of formatted text when the client hovers over the text.
	 *
	 * @param lines The lines of formatted text which will be displayed to the client upon hovering.
	 * @return This message component instance.
	 */
	public MessageComponent formattedTooltip(Message... lines) throws IllegalArgumentException
	{
		if(lines.length < 1)
		{
			return setHoverEvent(null);
		}

		List<MessageComponent> components = new ArrayList<>();

		for(int i = 0; i < lines.length; i++)
		{
			for(MessageComponent component : lines[i].getMessageComponents())
			{
				if(component.getClickEvent() != null) throw new IllegalArgumentException("The tooltip text cannot have click data.");
				else if(component.getHoverEvent() != null) throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
				if(i > 0)
				{
					components.add(getNewLineComponent());
				}
				components.add(component);
			}
		}
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, components);
	}

	/**
	 * Adds an inherited string to the component.
	 *
	 * @param extras The extras to be added to the component.
	 * @return This message component instance.
	 */
	public MessageComponent extra(MessageComponent... extras)
	{
		return addExtra(extras);
	}
	//endregion

	public boolean isEmpty()
	{
		return  (text == null || text.isEmpty()) && (extra == null || extra.isEmpty()) && color == null && (font == null || font.isEmpty()) && (insertion == null || insertion.isEmpty()) &&
				!bold && !italic && !underlined && !strikethrough && !obfuscated &&
				(with == null || with.isEmpty()) && score == null && translate == null && selector == null;
	}

	public MessageComponent split(final @NotNull @Language("RegExp") String pattern)
	{
		return split(new PatternPreservingStringSplitter(pattern));
	}

	public MessageComponent split(final @NotNull PatternPreservingStringSplitter splitter)
	{
		if(extra != null)
		{
			for(MessageComponent component : extra)
			{
				component.split(splitter);
			}
		}
		List<String> components = (text != null && !text.isEmpty()) ? splitter.split(text) : new ArrayList<>(0);
		if(components.size() > 0)
		{
			if(splitter.getPlaceholderPattern().matcher(components.get(0)).matches())
			{
				text = "";
			}
			else
			{
				text = components.get(0);
				components.remove(0);
			}
			List<MessageComponent> newMessageComponents = new ArrayList<>();
			for(String component : components)
			{
				if(component.isEmpty()) continue;
				newMessageComponents.add(new MessageComponent(component));
			}
			if(extra != null)
			{
				extra.addAll(0, newMessageComponents);
			}
			else
			{
				extra = newMessageComponents;
			}
		}
		return this;
	}

	//region Deserializer and Deserializer Functions
	//region deserializer variables
	protected transient static final Gson GSON = new GsonBuilder().registerTypeAdapter(MessageColor.class, new MessageColor.MessageColorSerializer()).disableHtmlEscaping().create();
	protected transient static final JsonParser JSON_PARSER = new JsonParser();
	protected transient static final MessageComponent MESSAGE_COMPONENT_INSTANCE = new MessageComponent();
	//endregion

	public static String toJSON(MessageComponent... components)
	{
		String json = GSON.toJson(components);
		if(json.startsWith("[{},")) json = "[\"\"," + json.substring(4);
		return json;
	}

	/**
	 * Generates a MessageComponent list from a given JSON string.
	 *
	 * @param jsonString The JSON string representing the components.
	 * @return A list of MessageComponent objects. An empty list if there are no components in the given {@link JsonArray}.
	 */
	protected List<MessageComponent> fromJsonWorker(String jsonString)
	{
		return fromJsonArrayWorker(JSON_PARSER.parse(jsonString).getAsJsonArray());
	}

	/**
	 * Generates a MessageComponent list from a given {@link JsonArray} object.
	 *
	 * @param componentArray The {@link JsonArray} containing all the components, from the deserializer.
	 * @return A list of MessageComponent objects. An empty list if there are no components in the given {@link JsonArray}.
	 */
	protected List<MessageComponent> fromJsonArrayWorker(JsonArray componentArray)
	{
		List<MessageComponent> components = new ArrayList<>();
		for(JsonElement component : componentArray)
		{
			if(component instanceof JsonPrimitive)
			{
				try
				{
					MessageComponent messageComponent = new MessageComponent();
					messageComponent.setText(component.getAsString());
					components.add(messageComponent);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(component instanceof JsonObject)
			{
				components.add(GSON.fromJson(component, MessageComponent.class));
			}
			else if(component instanceof JsonArray)
			{
				components.addAll(fromJsonArrayWorker((JsonArray) component));
			}
		}
		return components;
	}

	/**
	 * Generates a MessageComponent list from a given JSON string.
	 *
	 * @param jsonString The JSON string representing the components.
	 * @return A list of MessageComponent objects. An empty list if there are no components in the given {@link JsonArray}.
	 */
	public static List<MessageComponent> fromJson(String jsonString)
	{
		return MESSAGE_COMPONENT_INSTANCE.fromJsonWorker(jsonString);
	}

	/**
	 * Generates a MessageComponent list from a given {@link JsonArray} object.
	 *
	 * @param componentArray The {@link JsonArray} containing all the components, from the deserializer.
	 * @return A list of MessageComponent objects. An empty list if there are no components in the given {@link JsonArray}.
	 */
	public static List<MessageComponent> fromJsonArray(JsonArray componentArray)
	{
		return MESSAGE_COMPONENT_INSTANCE.fromJsonArrayWorker(componentArray);
	}
	//endregion
}