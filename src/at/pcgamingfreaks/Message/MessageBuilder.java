/*
 *   Copyright (C) 2016 GeorgH93
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

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;

@SuppressWarnings("unchecked")
public abstract class MessageBuilder<T extends MessageBuilder, COMPONENT extends MessageComponent, MESSAGE extends Message, STYLE extends Enum>
{
	private List<COMPONENT> messageList = new LinkedList<>();
	private COMPONENT current;
	private static Constructor EMPTY_COMPONENT_CONSTRUCTOR = null, INIT_COMPONENT_CONSTRUCTOR = null, MESSAGE_CONSTRUCTOR = null;
	@SuppressWarnings("unused")
	private static Class<? extends MessageComponent> COMPONENT_CLASS;
	private static MessageComponent NEW_LINE_HELPER = null;
	protected static final Gson GSON = new Gson();

	/**
	 * Creates a new MessageBuilder with a given {@link MessageComponent}.
	 *
	 * @param initComponent The {@link MessageComponent} that should be on the first position of the message.
	 */
	public MessageBuilder(COMPONENT initComponent)
	{
		current = initComponent;
		if(initComponent != null) messageList.add(initComponent);
	}

	/**
	 * Creates a new MessageBuilder with a given {@link Collection} of {@link MessageComponent}'s.
	 *
	 * @param initCollection The {@link Collection} of {@link MessageComponent} that should be used to initiate the MessageBuilder.
	 */
	public MessageBuilder(Collection<COMPONENT> initCollection)
	{
		append(initCollection);
	}

	//region Append functions
	/**
	 * Adds a new line to the message.
	 *
	 * @return The message builder instance (for chaining)
	 */
	public T appendNewLine()
	{
		return append((COMPONENT) NEW_LINE_HELPER);
	}

	/**
	 * Adds a new {@link MessageComponent} to the builder.
	 *
	 * @return The message builder instance (for chaining).
	 */
	public T append()
	{
		try
		{
			//noinspection ConstantConditions
			return append((COMPONENT) EMPTY_COMPONENT_CONSTRUCTOR.newInstance());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return (T) this;
	}

	/**
	 * Adds a new {@link MessageComponent} to the builder, generated from a text and optional style data.
	 *
	 * @param text   The text that should be used to generate the new {@link MessageComponent} that will be added to the builder.
	 * @param styles The style information for the new {@link MessageComponent} that will be added to the builder.
	 * @return The message builder instance (for chaining).
	 */
	public T append(String text, STYLE[] styles)
	{

		if(styles == null || styles.length == 0) return (T)this;
		return append(text, MessageColor.messageColorArrayFromStylesArray((Enum[]) styles));
	}

	/**
	 * Adds a new {@link MessageComponent} to the builder, generated from a text and optional style data.
	 *
	 * @param text   The text that should be used to generate the new {@link MessageComponent} that will be added to the builder.
	 * @param styles The style information for the new {@link MessageComponent} that will be added to the builder.
	 * @return The message builder instance (for chaining).
	 */
	public T append(String text, MessageColor... styles)
	{
		try
		{
			//noinspection ConstantConditions
			return append((COMPONENT) INIT_COMPONENT_CONSTRUCTOR.newInstance(text, styles));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return (T) this;
	}

	/**
	 * Adds an array of {@link MessageComponent}'s to the builder.
	 *
	 * @param components The array of {@link MessageComponent}'s that should be added to the builder.
	 * @return The message builder instance (for chaining).
	 */
	public T append(COMPONENT... components)
	{
		if(components != null && components.length > 0)
		{
			current = components[components.length - 1];
			Collections.addAll(messageList, components);
		}
		return (T)this;
	}

	/**
	 * Adds a collection of {@link MessageComponent}'s to the builder.
	 *
	 * @param components The collection of {@link MessageComponent}'s that should be added to the builder.
	 * @return The message builder instance (for chaining).
	 */
	public T append(Collection<COMPONENT> components)
	{
		messageList.addAll(components);
		current = messageList.get(messageList.size() - 1);
		return (T)this;
	}

	/**
	 * Adds a new {@link MessageComponent} to the builder, deserialized from a JSON string.
	 *
	 * @param json The JSON string that should be deserialized in oder to add it to the builder.
	 * @return The message builder instance (for chaining).
	 */
	public abstract T appendJson(String json);
	//endregion

	protected COMPONENT getCurrentComponent()
	{
		return current;
	}

	/**
	 * Gets the size (amount of {@link MessageComponent}'s) of the builder.
	 *
	 * @return The size of the builder.
	 */
	public int size()
	{
		return messageList.size();
	}

	/**
	 * Gets an iterator of the builder. This makes it possible to change {@link MessageComponent}'s on every position within the message.
	 *
	 * @return The iterator of the builder.
	 */
	public Iterator<COMPONENT> iterator()
	{
		return messageList.iterator();
	}

	//region Modifier for the current component
	/**
	 * Sets the text of the current component.
	 *
	 * @param text The new text of the current component.
	 * @return The message builder instance (for chaining).
	 */
	public T text(String text)
	{
		getCurrentComponent().setText(text);
		return (T) this;
	}

	/**
	 * Sets the color of the current component.
	 *
	 * @param color The new color of the current component.
	 * @return The message builder instance (for chaining).
	 */
	public T color(String color)
	{
		getCurrentComponent().setColor(color);
		return (T) this;
	}

	/**
	 * Sets the color of the current component.
	 *
	 * @param color The new color of the current component.
	 * @return The message builder instance (for chaining).
	 */
	public T color(MessageColor color)
	{
		getCurrentComponent().setColor(color);
		return (T) this;
	}

	/**
	 * Sets the color of the current component.
	 *
	 * @param color The new color of the current component.
	 * @return The message builder instance (for chaining).
	 */
	public T color(STYLE color)
	{
		getCurrentComponent().setColor(color);
		return (T) this;
	}

	/**
	 * Sets the format of the current component
	 *
	 * @param formats The array of formats to apply to the current component.
	 * @return The message builder instance (for chaining).
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public T format(STYLE... formats) throws IllegalArgumentException
	{
		getCurrentComponent().setFormats(formats);
		return (T) this;
	}

	/**
	 * Sets the format of the current component
	 *
	 * @param formats The array of formats to apply to the current component.
	 * @return The message builder instance (for chaining).
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public T format(MessageColor... formats) throws IllegalArgumentException
	{
		getCurrentComponent().setFormats(formats);
		return (T) this;
	}

	/**
	 * Sets the style of the current component.
	 *
	 * @param styles The array of styles to apply to the current component.
	 * @return The message builder instance (for chaining).
	 */
	public T style(STYLE... styles)
	{
		getCurrentComponent().setStyles(styles);
		return (T) this;
	}

	/**
	 * Sets the style of the current component.
	 *
	 * @param styles The array of styles to apply to the current component.
	 * @return The message builder instance (for chaining).
	 */
	public T style(MessageColor... styles)
	{
		getCurrentComponent().setStyles(styles);
		return (T) this;
	}

	/**
	 * Sets the current component to be bold.
	 *
	 * @return The message builder instance (for chaining).
	 */
	public T bold()
	{
		getCurrentComponent().setBold();
		return (T) this;
	}

	/**
	 * Sets the current component to be italic.
	 *
	 * @return The message builder instance (for chaining).
	 */
	public T italic()
	{
		getCurrentComponent().setItalic();
		return (T) this;
	}

	/**
	 * Sets the current component to be underlined.
	 *
	 * @return The message builder instance (for chaining).
	 */
	public T underlined()
	{
		getCurrentComponent().setUnderlined();
		return (T) this;
	}

	/**
	 * Sets the current component to be obfuscated.
	 *
	 * @return The message builder instance (for chaining).
	 */
	public T obfuscated()
	{
		getCurrentComponent().setObfuscated();
		return (T) this;
	}

	/**
	 * Sets the current component to be strikethrough.
	 *
	 * @return The message builder instance (for chaining).
	 */
	public T strikethrough()
	{
		getCurrentComponent().setStrikethrough();
		return (T) this;
	}

	/**
	 * Set the behavior of the current component when the client clicks on it.
	 *
	 * @param action the action the client should execute.
	 * @param value the value the client should use for the action.
	 * @return The message builder instance (for chaining).
	 */
	public T onClick(MessageClickEvent.ClickEventAction action, String value)
	{
		getCurrentComponent().onClick(action, value);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to instruct the client to open a file on the client side filesystem when the current component is clicked.
	 *
	 * @param path The path of the file on the clients filesystem.
	 * @return The message builder instance (for chaining).
	 */
	public T file(String path)
	{
		getCurrentComponent().file(path);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to instruct the client to open a web-page in the clients web browser when the current component is clicked.
	 *
	 * @param url The URL of the page to open when the link is clicked.
	 * @return The message builder instance (for chaining).
	 */
	public T link(String url)
	{
		getCurrentComponent().link(url);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to instruct the client to replace the chat input box content with the specified string when the current component is clicked.
	 * The client will not immediately send the command to the server to be executed unless the client player submits the command/chat message, usually with the enter key.
	 *
	 * @param command The text to display in the chat bar of the client.
	 * @return The message builder instance (for chaining).
	 */
	public T suggest(String command)
	{
		getCurrentComponent().suggest(command);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to instruct the client to send the specified string to the server as a chat message when the current component is clicked.
	 * The client <b>will</b> immediately send the command to the server to be executed when the editing component is clicked.
	 *
	 * @param command The text to display in the chat bar of the client.
	 * @return The message builder instance (for chaining).
	 */
	public T command(final String command)
	{
		getCurrentComponent().command(command);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to instruct the client to append the chat input box content with the specified string when the current component is shift-clicked.
	 * The client will not immediately send the command to the server to be executed unless the client player submits the command/chat message, usually with the enter key.
	 *
	 * @param insert The text to append to the chat bar of the client.
	 * @return The message builder instance (for chaining).
	 */
	public T insert(String insert)
	{
		getCurrentComponent().setInsertion(insert);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component when the client hovers with the mouse over it.
	 *
	 * @param action The action the client should execute.
	 * @param value  The value the client should use for the action.
	 * @return The message builder instance (for chaining).
	 */
	public T onHover(MessageHoverEvent.HoverEventAction action, String value)
	{
		getCurrentComponent().onHover(action, value);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component when the client hovers with the mouse over it.
	 *
	 * @param action The action the client should execute.
	 * @param value  The value the client should use for the action.
	 * @return The message builder instance (for chaining).
	 */
	public T onHover(MessageHoverEvent.HoverEventAction action, Collection<? extends MessageComponent> value)
	{
		getCurrentComponent().onHover(action, value);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to display information about an achievement when the client hovers over the text.
	 *
	 * @param name The name of the achievement to display, excluding the "achievement." prefix.
	 * @return The message builder instance (for chaining).
	 */
	public T achievementTooltip(String name)
	{
		getCurrentComponent().achievementTooltip(name);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to display information about a statistic when the client hovers over the text.
	 *
	 * @param name The name of the statistic to displayed, excluding the "stat." prefix.
	 * @return The message builder instance (for chaining).
	 */
	public T statisticTooltip(String name)
	{
		getCurrentComponent().statisticTooltip(name);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to display information about an item when the client hovers over the text.
	 *
	 * @param itemJSON A string representing the JSON-serialized NBT data tag of an ItemStack.
	 * @return The message builder instance (for chaining).
	 */
	public T itemTooltip(String itemJSON)
	{
		getCurrentComponent().itemTooltip(itemJSON);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to display raw text when the client hovers over the text.
	 *
	 * @param lines The lines of text which will be displayed to the client upon hovering.
	 * @return The message builder instance (for chaining).
	 */
	public T tooltip(String... lines)
	{
		getCurrentComponent().tooltip(lines);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to display formatted text when the client hovers over the text.
	 *
	 * @param text The formatted text which will be displayed to the client upon hovering.
	 * @return The message builder instance (for chaining).
	 */
	public T formattedTooltip(MessageComponent... text) throws IllegalArgumentException
	{
		getCurrentComponent().formattedTooltip(text);
		return (T) this;
	}

	/**
	 * Set the behavior of the current component to display the specified lines of formatted text when the client hovers over the text.
	 *
	 * @param lines The lines of formatted text which will be displayed to the client upon hovering.
	 * @return The message builder instance (for chaining).
	 */
	public T formattedTooltip(Message... lines) throws IllegalArgumentException
	{
		getCurrentComponent().formattedTooltip(lines);
		return (T) this;
	}

	/**
	 * Adds an inherited MessageComponent to the current component.
	 *
	 * @param extras The extras to be added to the current component.
	 * @return The message builder instance (for chaining).
	 */
	public T extra(MessageComponent... extras)
	{
		getCurrentComponent().extra(extras);
		return (T) this;
	}
	//endregion

	//region Getter for the final message
	/**
	 * Gets the {@link Message} object of the message build with the builder.
	 *
	 * @return The {@link Message} object.
	 */
	public MESSAGE getMessage()
	{
		try
		{
			//noinspection ConstantConditions
			return (MESSAGE) MESSAGE_CONSTRUCTOR.newInstance(getJsonMessageAsList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets the build message as a list of {@link MessageComponent}'s.
	 *
	 * @return The list of {@link MessageComponent}'s.
	 */
	public List<COMPONENT> getJsonMessageAsList()
	{
		return new ArrayList<>(messageList);
	}

	/**
	 * Gets the build message as an array of {@link MessageComponent}'s.
	 *
	 * @return The array of {@link MessageComponent}'s.
	 */
	public COMPONENT[] getJsonMessage()
	{
		COMPONENT[] array = (COMPONENT[]) Array.newInstance(COMPONENT_CLASS, messageList.size());
		return messageList.toArray(array);
	}

	/**
	 * Gets the message in the classic minecraft chat format used in minecraft version prior than 1.7.
	 *
	 * @return The classic message string of the build message.
	 */
	public String getClassicMessage()
	{
		return COMPONENT.getClassicMessage(messageList);
	}

	/**
	 * Gets the build message as a JSON string.
	 *
	 * @return The JSON string of the build message.
	 */
	public String getJson()
	{
		return GSON.toJson(getJsonMessage());
	}
	//endregion
}