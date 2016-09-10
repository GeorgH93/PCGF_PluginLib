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

import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Reflection;

import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class MessageBuilder extends at.pcgamingfreaks.Message.MessageBuilder<MessageBuilder, MessageComponent, Message, ChatColor>
{
	static
	{
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "NEW_LINE_HELPER", new MessageComponent("\n"));
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "EMPTY_COMPONENT_CONSTRUCTOR", Reflection.getConstructor(MessageComponent.class));
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "MESSAGE_CONSTRUCTOR", Reflection.getConstructor(Message.class, Collection.class));
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "INIT_COMPONENT_CONSTRUCTOR", Reflection.getConstructor(MessageComponent.class, String.class, MessageColor[].class));
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "COMPONENT_CLASS", MessageComponent.class);
	}

	//region Constructors
	/**
	 * Creates a new MessageBuilder with an empty {@link MessageComponent}.
	 */
	public MessageBuilder()
	{
		super(new MessageComponent());
	}

	/**
	 * Creates a new MessageBuilder with a given {@link MessageComponent}.
	 *
	 * @param initComponent The {@link MessageComponent} that should be on the first position of the message.
	 */
	public MessageBuilder(MessageComponent initComponent)
	{
		super(initComponent);
	}

	/**
	 * Creates a new MessageBuilder with a given {@link Collection} of {@link MessageComponent}'s.
	 *
	 * @param initCollection The {@link Collection} of {@link MessageComponent} that should be used to initiate the MessageBuilder.
	 */
	public MessageBuilder(Collection<MessageComponent> initCollection)
	{
		super(initCollection);
	}

	/**
	 * Creates a new MessageBuilder from a given text and format information.
	 *
	 * @param text   The text that should be used to initialize the first {@link MessageComponent} of the message.
	 * @param styles The style the should be used to initialize the first {@link MessageComponent} of the message.
	 */
	public MessageBuilder(String text, MessageColor... styles)
	{
		this(new MessageComponent(text, styles));
	}

	/**
	 * Creates a new MessageBuilder from a given text and format information.
	 *
	 * @param text   The text that should be used to initialize the first {@link MessageComponent} of the message.
	 * @param styles The style the should be used to initialize the first {@link MessageComponent} of the message.
	 */
	public MessageBuilder(String text, ChatColor[] styles)
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
		MessageBuilder builder = new MessageBuilder((MessageComponent) null);
		builder.appendJson(json);
		return builder;
	}

	//region Append functions
	/**
	 * Adds a new {@link MessageComponent} to the builder, deserialized from a JSON string.
	 *
	 * @param json The JSON string that should be deserialized in oder to add it to the builder.
	 * @return The message builder instance (for chaining).
	 */
	@Override
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
	//endregion

	//region Modifier for the current component
	/**
	 * Set the behavior of the current component to display information about an achievement when the client hovers over the text.
	 *
	 * @param achievement The achievement to display.
	 * @return The message builder instance (for chaining).
	 */
	public MessageBuilder achievementTooltip(Achievement achievement)
	{
		getCurrentComponent().achievementTooltip(achievement);
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
		getCurrentComponent().statisticTooltip(statistic);
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
		getCurrentComponent().statisticTooltip(statistic, material);
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
		getCurrentComponent().statisticTooltip(statistic, entity);
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
		getCurrentComponent().itemTooltip(itemStack);
		return this;
	}
	//endregion
}