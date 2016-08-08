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

package at.pcgamingfreaks.Bukkit.Message;

import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class MessageBuilder extends at.pcgamingfreaks.Message.MessageBuilder<MessageBuilder>
{
	private List<MessageComponent> messageList = new LinkedList<>();
	private MessageComponent current;
	private final static MessageComponent NEW_LINE_HELPER = new MessageComponent("\n");

	@Override
	protected at.pcgamingfreaks.Message.MessageComponent getCurrentComponent()
	{
		return current;
	}

	//region Constructors
	/**
	 * Creates a new MessageBuilder with an empty {@link MessageComponent}.
	 */
	public MessageBuilder()
	{
		this(new MessageComponent());
	}

	/**
	 * Creates a new MessageBuilder with a given {@link MessageComponent}.
	 *
	 * @param initComponent The {@link MessageComponent} that should be on the first position of the message.
	 */
	public MessageBuilder(MessageComponent initComponent)
	{
		current = initComponent;
		messageList.add(current);
	}

	/**
	 * Creates a new MessageBuilder with a given {@link Collection} of {@link MessageComponent}'s.
	 *
	 * @param initCollection The {@link Collection} of {@link MessageComponent} that should be used to initiate the MessageBuilder.
	 */
	public MessageBuilder(Collection<MessageComponent> initCollection)
	{
		messageList = new LinkedList<>(initCollection);
		current = messageList.get(messageList.size() - 1);
	}

	/**
	 * Creates a new MessageBuilder from a given text and format information.
	 *
	 * @param text   The text that should be used to initialize the first {@link MessageComponent} of the message.
	 * @param styles The style the should be used to initialize the first {@link MessageComponent} of the message.
	 */
	public MessageBuilder(String text, ChatColor... styles)
	{
		this(new MessageComponent(text, styles));
	}
	//endregion

	/**
	 * Creates a MessageBuilder from a JSON string.
	 *
	 * @param json The JSON that should be used to create the MessageBuilder object.
	 * @return The MessageBuilder object containing the {@link MessageComponent}'s from the given JSON.
	 */
	public static MessageBuilder fromJson(String json)
	{
		List<MessageComponent> components = null;
		try
		{
			components = MessageComponent.fromJson(json);
		}
		catch(Exception ignored) {}
		if(components == null)
		{
			components = new LinkedList<>();
			components.add(new MessageComponent(json));
		}
		return new MessageBuilder(components);
	}

	//region append functions
	/**
	 * Adds a new {@link MessageComponent} to the builder.
	 *
	 * @return The message builder instance (for chaining).
	 */
	public MessageBuilder append()
	{
		return append(new MessageComponent());
	}

	/**
	 * Adds a new {@link MessageComponent} to the builder, generated from a text and optional style data.
	 *
	 * @param text   The text that should be used to generate the new {@link MessageComponent} that will be added to the builder.
	 * @param styles The style information for the new {@link MessageComponent} that will be added to the builder.
	 * @return The message builder instance (for chaining).
	 */
	public MessageBuilder append(String text, ChatColor... styles)
	{
		return append(new MessageComponent(text, styles));
	}

	/**
	 * Adds a new {@link MessageComponent} to the builder, deserialized from a JSON string.
	 *
	 * @param json The JSON string that should be deserialized in oder to add it to the builder.
	 * @return The message builder instance (for chaining).
	 */
	public MessageBuilder appendJson(String json)
	{
		List<MessageComponent> components = null;
		try
		{
			components = MessageComponent.fromJson(json);
		}
		catch(Exception ignored) {}
		return (components == null) ? append(json) : append(components);
	}

	/**
	 * Adds an array of {@link MessageComponent}'s to the builder.
	 *
	 * @param components The array of {@link MessageComponent}'s that should be added to the builder.
	 * @return The message builder instance (for chaining).
	 */
	public MessageBuilder append(MessageComponent... components)
	{
		if(components != null && components.length > 0)
		{
			current = components[components.length - 1];
			Collections.addAll(messageList, components);
		}
		return this;
	}

	/**
	 * Adds a collection of {@link MessageComponent}'s to the builder.
	 *
	 * @param components The collection of {@link MessageComponent}'s that should be added to the builder.
	 * @return The message builder instance (for chaining).
	 */
	public MessageBuilder append(Collection<MessageComponent> components)
	{
		messageList.addAll(components);
		current = messageList.get(messageList.size() - 1);
		return this;
	}

	public MessageBuilder appendNewLine()
	{
		return append(NEW_LINE_HELPER);
	}
	//endregion

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
	public Iterator<MessageComponent> iterator()
	{
		return messageList.iterator();
	}

	//region Getter for the final message
	/**
	 * Gets the {@link Message} object of the message build with the builder.
	 *
	 * @return The {@link Message} object.
	 */
	public Message getMessage()
	{
		return new Message(getJsonMessageAsList());
	}

	/**
	 * Gets the build message as a list of {@link MessageComponent}'s.
	 *
	 * @return The list of {@link MessageComponent}'s.
	 */
	public List<MessageComponent> getJsonMessageAsList()
	{
		return new ArrayList<>(messageList);
	}

	/**
	 * Gets the build message as an array of {@link MessageComponent}'s.
	 *
	 * @return The array of {@link MessageComponent}'s.
	 */
	public MessageComponent[] getJsonMessage()
	{
		return messageList.toArray(new MessageComponent[messageList.size()]);
	}

	/**
	 * Gets the message in the classic minecraft chat format used in minecraft version prior than 1.7.
	 *
	 * @return The classic message string of the build message.
	 */
	public String getClassicMessage()
	{
		return MessageComponent.getClassicMessage(messageList);
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

	//region Modifier for the current component
	/**
	 * Sets the color of the current component.
	 *
	 * @param color The new color of the current component.
	 * @return The message builder instance (for chaining).
	 * @exception IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).
	 */
	public MessageBuilder color(ChatColor color) throws IllegalArgumentException
	{
		((MessageComponent) getCurrentComponent()).setColor(color);
		return this;
	}

	/**
	 * Sets the format of the current component.
	 *
	 * @param formats The array of formats to apply to the current component.
	 * @return The message builder instance (for chaining).
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public MessageBuilder format(ChatColor... formats) throws IllegalArgumentException
	{
		((MessageComponent) getCurrentComponent()).setFormats(formats);
		return this;
	}

	/**
	 * Sets the style of the current component.
	 *
	 * @param styles The array of styles to apply to the current component.
	 * @return The message builder instance (for chaining).
	 */
	public MessageBuilder style(ChatColor... styles)
	{
		((MessageComponent) getCurrentComponent()).setStyles(styles);
		return this;
	}

	/**
	 * Set the behavior of the current component to display information about an achievement when the client hovers over the text.
	 *
	 * @param achievement The achievement to display.
	 * @return The message builder instance (for chaining).
	 */
	public MessageBuilder achievementTooltip(Achievement achievement)
	{
		((MessageComponent) getCurrentComponent()).achievementTooltip(achievement);
		return this;
	}

	/**
	 * Set the behavior of the current component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param statistic The statistic to display.
	 * @return The message builder instance (for chaining).
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied.
	 */
	public MessageBuilder statisticTooltip(Statistic statistic) throws IllegalArgumentException
	{
		((MessageComponent) getCurrentComponent()).statisticTooltip(statistic);
		return this;
	}

	/**
	 * Set the behavior of the current component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param statistic The statistic to display.
	 * @param material The material parameter to the statistic.
	 * @return The message builder instance (for chaining).
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
	 */
	public MessageBuilder statisticTooltip(Statistic statistic, Material material) throws IllegalArgumentException
	{
		((MessageComponent) getCurrentComponent()).statisticTooltip(statistic, material);
		return this;
	}

	/**
	 * Set the behavior of the current component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param statistic The statistic to display.
	 * @param entity The entity type parameter to the statistic.
	 * @return The message builder instance (for chaining).
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
	 */
	public MessageBuilder statisticTooltip(Statistic statistic, EntityType entity) throws IllegalArgumentException
	{
		((MessageComponent) getCurrentComponent()).statisticTooltip(statistic, entity);
		return this;
	}

	/**
	 * Set the behavior of the current component to display information about an item when the client hovers over the text.
	 *
	 * @param itemStack The stack for which to display information.
	 * @return The message builder instance (for chaining).
	 */
	public MessageBuilder itemTooltip(ItemStack itemStack)
	{
		((MessageComponent) getCurrentComponent()).itemTooltip(itemStack);
		return this;
	}
	//endregion
}