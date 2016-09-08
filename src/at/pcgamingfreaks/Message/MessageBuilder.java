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

import java.util.*;

@SuppressWarnings("unchecked")
public abstract class MessageBuilder<T extends MessageBuilder, COMPONENT extends MessageComponent, STYLE extends Enum>
{
	protected List<COMPONENT> messageList = new LinkedList<>();
	protected COMPONENT current;
	private static MessageComponent NEW_LINE_HELPER = null;
	protected final static Gson GSON = new Gson();

	/**
	 * Creates a new MessageBuilder with an empty {@link MessageComponent}.
	 */
	public MessageBuilder() {}

	//region Append functions
	public T appendNewLine()
	{
		return append((COMPONENT) NEW_LINE_HELPER);
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
}