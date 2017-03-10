/*
 *   Copyright (C) 2016, 2017 GeorgH93
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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Message;

import com.google.gson.*;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class MessageComponent<T extends MessageComponent, STYLES extends Enum> implements JsonDeserializer<T>
{
	//region JSON Variables
	protected MessageClickEvent clickEvent = null;
	protected MessageHoverEvent hoverEvent = null;
	protected String text, insertion;
	protected MessageColor color;
	protected Boolean bold, italic, underlined, strikethrough, obfuscated;
	protected List<T> extra = null;

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
	protected MessageComponent() {}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text   The text for the {@link MessageComponent}.
	 * @param styles The style for the {@link MessageComponent}.
	 */
	protected MessageComponent(String text, MessageColor... styles)
	{
		setText(text);
		if(styles != null) setStyles(styles);
	}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text   The text for the {@link MessageComponent}.
	 * @param styles The style for the {@link MessageComponent}.
	 */
	protected MessageComponent(String text, @Nullable STYLES[] styles)
	{
		setText(text);
		if(styles != null) setStyles(styles);
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
	public static String getClassicMessage(Collection<? extends MessageComponent> messageList)
	{
		if(messageList == null) return ""; // If we don't have a JSON we can't calculate the classic message from it so we will use an empty message
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
		if (isBold()) stringBuilder.append(MessageColor.BOLD);
		if (isItalic()) stringBuilder.append(MessageColor.ITALIC);
		if (isObfuscated()) stringBuilder.append(MessageColor.MAGIC);
		if (isUnderlined()) stringBuilder.append(MessageColor.UNDERLINE);
		if (isStrikethrough()) stringBuilder.append(MessageColor.STRIKETHROUGH);
		if (color != null) stringBuilder.append(getColor().toString().toLowerCase());
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
	public T setClickEvent(MessageClickEvent clickEvent)
	{
		this.clickEvent = clickEvent;
		return (T)this;
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
	public T setHoverEvent(MessageHoverEvent hoverEvent)
	{
		this.hoverEvent = hoverEvent;
		return (T)this;
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
	public T setText(String text)
	{
		this.text = text;
		return (T)this;
	}

	/**
	 * Gets the color of the component.
	 *
	 * @return The color of the component as a {@link String}, null if no color is defined.
	 */
	public MessageColor getColor()
	{
		return color;
	}

	/**
	 * Gets the color of the component.
	 *
	 * @return The color of the component as a {@link MessageColor}, null if no color is defined.
	 */
	public String getColorString()
	{
		return getColor().name().toLowerCase();
	}

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 */
	public T setColor(String color)
	{
		return setColor(MessageColor.valueOf(color.toUpperCase()));
	}

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).
	 */
	public T setColor(MessageColor color) throws IllegalArgumentException
	{
		if(!color.isColor())
		{
			throw new IllegalArgumentException(color.name() + " is not a color");
		}
		this.color = color;
		return (T)this;
	}

	/**
	 * Sets the color of the component.
	 * Use {@link MessageColor} for better performance!
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).
	 */
	public T setColor(STYLES color) throws IllegalArgumentException
	{
		return setColor(MessageColor.messageColorFromStyle(color));
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
	public T setInsertion(String insertionText)
	{
		insertion = (insertionText.length() > 100) ? insertionText.substring(0, 100) : insertionText;
		return (T)this;
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
	public T setBold()
	{
		return setBold(true);
	}

	/**
	 * Sets the component to be bold.
	 *
	 * @param bold Defines if the component should or should not be bold.
	 * @return This message component instance.
	 */
	public T setBold(boolean bold)
	{
		this.bold = bold;
		return (T)this;
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
	public T setItalic()
	{
		return setItalic(true);
	}

	/**
	 * Sets the component to be italic.
	 *
	 * @param italic Defines if the component should or should not be italic.
	 * @return This message component instance.
	 */
	public T setItalic(boolean italic)
	{
		this.italic = italic;
		return (T)this;
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
	public T setUnderlined()
	{
		return setUnderlined(true);
	}

	/**
	 * Sets the component to be underlined.
	 *
	 * @param underlined Defines if the component should or should not be underlined.
	 * @return This message component instance.
	 */
	public T setUnderlined(boolean underlined)
	{
		this.underlined = underlined;
		return (T)this;
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
	public T setStrikethrough()
	{
		return setStrikethrough(true);
	}

	/**
	 * Sets the component to be strikethrough.
	 *
	 * @param strikethrough Defines if the component should or should not be strikethrough.
	 * @return This message component instance.
	 */
	public T setStrikethrough(boolean strikethrough)
	{
		this.strikethrough = strikethrough;
		return (T)this;
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
	public T setObfuscated()
	{
		return setObfuscated(true);
	}

	/**
	 * Sets the component to be obfuscated.
	 *
	 * @param obfuscated Defines if the component should or should not be obfuscated.
	 * @return This message component instance.
	 */
	public T setObfuscated(boolean obfuscated)
	{
		this.obfuscated = obfuscated;
		return (T)this;
	}

	/**
	 * Gets all the extras of the component.
	 *
	 * @return A list of MessageComponents used as extras for the component.
	 */
	public List<T> getExtras()
	{
		return extra;
	}

	/**
	 * Sets all the extras of the component.
	 *
	 * @param extras A list of MessageComponents used as extras for the component.
	 * @return This message component instance.
	 */
	public T setExtras(List<T> extras)
	{
		extra = extras;
		return (T)this;
	}

	/**
	 * Adds an inherited string to the component.
	 *
	 * @param extras The extras to be added to the component.
	 * @return This message component instance.
	 */
	public T addExtra(T... extras)
	{
		if(extras != null)
		{
			for(T extra : extras)
			{
				if (extra == null) continue;
				if(this.extra == null)
				{
					this.extra = new LinkedList<>();
				}
				this.extra.add(extra);
			}
		}
		return (T)this;
	}

	/**
	 * Sets the formats of the component.
	 *
	 * @param formats The array of formats to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public T setFormats(MessageColor... formats) throws IllegalArgumentException
	{
		if(formats != null)
		{
			for(MessageColor format : formats)
			{
				switch(format)
				{
					case ITALIC: setItalic(); break;
					case BOLD: setBold(); break;
					case UNDERLINE: setUnderlined(); break;
					case STRIKETHROUGH: setStrikethrough(); break;
					case MAGIC: setObfuscated(); break;
					case RESET: break;
					default: throw new IllegalArgumentException(format.name() + " is not a formatter");
				}
			}
		}
		return (T)this;
	}

	/**
	 * Sets the formats of the component.
	 * Use {@link MessageColor} for better performance!
	 *
	 * @param formats The array of formats to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public T setFormats(@Nullable STYLES... formats) throws IllegalArgumentException
	{
		if(formats == null || formats.length == 0) return (T)this;
		return setFormats(MessageColor.messageColorArrayFromStylesArray((Enum[]) formats));
	}

	/**
	 * Sets the style of the component.
	 *
	 * @param styles The array of styles to apply to the component.
	 * @return This message component instance.
	 */
	public T setStyles(MessageColor... styles)
	{
		if(styles != null)
		{
			for(MessageColor style : styles)
			{
				if (style == null) continue;
				if(style.isFormat())
				{
					setFormats(style);
				}
				if(style.isColor())
				{
					setColor(style);
				}
			}
		}
		return (T)this;
	}

	/**
	 * Sets the style of the component.
	 * Use {@link MessageColor} for better performance!
	 *
	 * @param styles The array of styles to apply to the component.
	 * @return This message component instance.
	 */
	public T setStyles(@Nullable STYLES... styles)
	{
		if(styles == null || styles.length == 0) return (T)this;
		return setStyles(MessageColor.messageColorArrayFromStylesArray((Enum[]) styles));
	}
	//endregion

	//region Short message modifier (setter)
	/**
	 * Sets the text of the component.
	 *
	 * @param text The new text of the component.
	 * @return This message component instance.
	 */
	public T text(String text)
	{
		return setText(text);
	}

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 */
	public T color(String color)
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
	public T color(MessageColor color) throws IllegalArgumentException
	{
		return setColor(color);
	}

	/**
	 * Sets the color of the component.
	 * Use {@link MessageColor} for better performance!
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 */
	public T color(STYLES color) throws IllegalArgumentException
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
	public T format(MessageColor... formats) throws IllegalArgumentException
	{
		return setFormats(formats);
	}

	/**
	 * Sets the format of the component.
	 * Use {@link MessageColor} for better performance!
	 *
	 * @param formats The array of format to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public T format(STYLES... formats) throws IllegalArgumentException
	{
		return setFormats(formats);
	}

	/**
	 * Sets the style of the component.
	 * Use {@link MessageColor} for better performance!
	 *
	 * @param styles The array of styles to apply to the component.
	 * @return This message component instance.
	 */
	public T style(STYLES... styles)
	{
		return setStyles(styles);
	}

	/**
	 * Sets the style of the component.
	 *
	 * @param styles The array of styles to apply to the component.
	 * @return This message component instance.
	 */
	public T style(MessageColor... styles)
	{
		return setStyles(styles);
	}

	/**
	 * Sets the component to be bold.
	 *
	 * @return This message component instance.
	 */
	public T bold()
	{
		return setBold();
	}

	/**
	 * Sets the component to be italic.
	 *
	 * @return This message component instance.
	 */
	public T italic()
	{
		return setItalic();
	}

	/**
	 * Sets the component to be underlined.
	 *
	 * @return This message component instance.
	 */
	public T underlined()
	{
		return setUnderlined();
	}

	/**
	 * Sets the component to be obfuscated.
	 *
	 * @return This message component instance.
	 */
	public T obfuscated()
	{
		return setObfuscated();
	}

	/**
	 * Sets the component to be strikethrough.
	 *
	 * @return This message component instance.
	 */
	public T strikethrough()
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
	public T onClick(MessageClickEvent.ClickEventAction action, String value)
	{
		return setClickEvent(new MessageClickEvent(action, value));
	}

	/**
	 * Set the behavior of the component to instruct the client to open a file on the client side filesystem when the component is clicked.
	 *
	 * @param path The path of the file on the clients filesystem.
	 * @return This message component instance.
	 */
	public T file(String path)
	{
		return onClick(MessageClickEvent.ClickEventAction.OPEN_FILE, path);
	}

	/**
	 * Set the behavior of the component to instruct the client to open a web-page in the clients web browser when the component is clicked.
	 *
	 * @param url The URL of the page to open when the link is clicked.
	 * @return This message component instance.
	 */
	public T link(String url)
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
	public T suggest(String command)
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
	public T command(final String command)
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
	public T insert(String insert)
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
	public T onHover(MessageHoverEvent.HoverEventAction action, String value)
	{
		return setHoverEvent(new MessageHoverEvent(action, value));
	}

	/**
	 * Set the behavior of the component when the client hovers with the mouse over it.
	 *
	 * @param action the action the client should execute.
	 * @param value the value the client should use for the action.
	 * @return This message component instance.
	 */
	public T onHover(MessageHoverEvent.HoverEventAction action, Collection<? extends MessageComponent> value)
	{
		return setHoverEvent(new MessageHoverEvent(action, value));
	}

	/**
	 * Set the behavior of the component to display information about an achievement when the client hovers over the text.
	 *
	 * @param name The name of the achievement to display, excluding the "achievement." prefix.
	 * @return This message component instance.
	 */
	public T achievementTooltip(String name)
	{
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, "achievement." + name);
	}

	/**
	 * Set the behavior of the component to display information about a statistic when the client hovers over the text.
	 *
	 * @param name The name of the statistic to display, excluding the "stat." prefix.
	 * @return This message component instance.
	 */
	public T statisticTooltip(String name)
	{
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, "stat." + name);
	}

	/**
	 * Set the behavior of the component to display information about an item when the client hovers over the text.
	 *
	 * @param itemJSON A string representing the JSON-serialized NBT data tag of an ItemStack.
	 * @return This message component instance.
	 */
	public T itemTooltip(String itemJSON)
	{
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_ITEM, itemJSON);
	}

	/**
	 * Set the behavior of the component to display raw text when the client hovers over the text.
	 *
	 * @param lines The lines of text which will be displayed to the client upon hovering.
	 * @return This message component instance.
	 */
	public T tooltip(String... lines)
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
	public T formattedTooltip(MessageComponent... text) throws IllegalArgumentException
	{
		StringBuilder builder = new StringBuilder("");
		for(MessageComponent t : text)
		{
			if(t.getClickEvent() != null) throw new IllegalArgumentException("The tooltip text cannot have click data.");
			else if(t.getHoverEvent() != null) throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
			builder.append(t.toString());
		}
		return onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, builder.toString());
	}

	protected abstract MessageComponent getNewLineComponent();

	/**
	 * Set the behavior of the component to display the specified lines of formatted text when the client hovers over the text.
	 *
	 * @param lines The lines of formatted text which will be displayed to the client upon hovering.
	 * @return This message component instance.
	 */
	public T formattedTooltip(Message... lines) throws IllegalArgumentException
	{
		if(lines.length < 1)
		{
			return setHoverEvent(null);
		}

		List<MessageComponent> components = new LinkedList<>();

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
	public T extra(T... extras)
	{
		return addExtra(extras);
	}
	//endregion

	//region Deserializer and Deserializer Functions
	//region deserializer variables
	protected transient static Gson GSON;
	protected transient static Constructor messageComponentConstructor;
	protected transient static Class messageComponentClass;
	protected static final transient JsonParser JSON_PARSER = new JsonParser();
	//endregion

	@Override
	public T deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject componentAsJsonObject = json.getAsJsonObject();
		T component;
		try
		{
			component = (T) messageComponentConstructor.newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		//Strings
		if(componentAsJsonObject.get("text") != null) component.text = componentAsJsonObject.get("text").getAsString();
		if(componentAsJsonObject.get("color") != null)  component.color = MessageColor.valueOf(componentAsJsonObject.get("color").getAsString());
		if(componentAsJsonObject.get("insertion") != null) component.insertion = componentAsJsonObject.get("insertion").getAsString();
		//Booleans
		if(componentAsJsonObject.get("bold") != null) component.bold = componentAsJsonObject.get("bold").getAsBoolean();
		if(componentAsJsonObject.get("italic") != null) component.italic =  componentAsJsonObject.get("italic").getAsBoolean();
		if(componentAsJsonObject.get("underlined") != null) component.underlined = componentAsJsonObject.get("underlined").getAsBoolean();
		if(componentAsJsonObject.get("obfuscated") != null) component.obfuscated = componentAsJsonObject.get("obfuscated").getAsBoolean();
		if(componentAsJsonObject.get("strikethrough") != null) component.strikethrough = componentAsJsonObject.get("strikethrough").getAsBoolean();
		//Extra List
		if(componentAsJsonObject.get("extra") != null) component.extra = fromJsonArrayWorker(componentAsJsonObject.get("extra").getAsJsonArray());
		//Events
		if(componentAsJsonObject.get("clickEvent") != null) component.clickEvent = GSON.fromJson(componentAsJsonObject.get("clickEvent"), MessageClickEvent.class);
		if(componentAsJsonObject.get("hoverEvent") == null) component.hoverEvent = GSON.fromJson(componentAsJsonObject.get("hoverEvent"), MessageHoverEvent.class);
		//Other stuff
		component.selector = componentAsJsonObject.get("with");
		component.selector = componentAsJsonObject.get("score");
		component.selector = componentAsJsonObject.get("selector");
		component.selector = componentAsJsonObject.get("translate");
		return component;
	}

	/**
	 * Generates a MessageComponent list from a given JSON string.
	 *
	 * @param jsonString The JSON string representing the components.
	 * @return A list of MessageComponent objects. An empty list if there are no components in the given {@link JsonArray}.
	 */
	protected List<T> fromJsonWorker(String jsonString)
	{
		return fromJsonArrayWorker(JSON_PARSER.parse(jsonString).getAsJsonArray());
	}

	/**
	 * Generates a MessageComponent list from a given {@link JsonArray} object.
	 *
	 * @param componentArray The {@link JsonArray} containing all the components, from the deserializer.
	 * @return A list of MessageComponent objects. An empty list if there are no components in the given {@link JsonArray}.
	 */
	protected List<T> fromJsonArrayWorker(JsonArray componentArray)
	{
		List<T> components = new LinkedList<>();
		for(JsonElement component : componentArray)
		{
			if(component instanceof JsonPrimitive)
			{
				try
				{
					T messageComponent = (T) messageComponentConstructor.newInstance();
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
				components.add((T) GSON.fromJson(component, messageComponentClass));
			}
			else if(component instanceof JsonArray)
			{
				components.addAll(fromJsonArrayWorker((JsonArray) component));
			}
		}
		return components;
	}
	//endregion
}