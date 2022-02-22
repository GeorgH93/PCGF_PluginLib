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

package at.pcgamingfreaks.Bungee.Message;

import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Message.MessageComponent;
import at.pcgamingfreaks.Message.MessageFormat;
import at.pcgamingfreaks.Reflection;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MessageBuilder extends at.pcgamingfreaks.Message.MessageBuilder<MessageBuilder, Message>
{
	static
	{
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "MESSAGE_CONSTRUCTOR", Reflection.getConstructor(Message.class, Collection.class));
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
	public MessageBuilder(Collection<? extends MessageComponent> initCollection)
	{
		super(initCollection);
	}

	/**
	 * Creates a new MessageBuilder from a given text and format information.
	 *
	 * @param text    The text that should be used to initialize the first {@link MessageComponent} of the message.
	 */
	public MessageBuilder(String text)
	{
		this(new MessageComponent(text));
	}

	/**
	 * Creates a new MessageBuilder from a given text and format information.
	 *
	 * @param text    The text that should be used to initialize the first {@link MessageComponent} of the message.
	 * @param color   The color that should be used to initialize the first {@link MessageComponent} of the message.
	 * @param formats The style that should be used to initialize the first {@link MessageComponent} of the message.
	 */
	public MessageBuilder(String text, MessageColor color, MessageFormat... formats)
	{
		this(new MessageComponent(text, color, formats));
	}

	/**
	 * Creates a new MessageBuilder from a given text and format information.
	 *
	 * @param text   The text that should be used to initialize the first {@link MessageComponent} of the message.
	 * @param styles The style that should be used to initialize the first {@link MessageComponent} of the message.
	 * @deprecated   Use the constructor with MessageColor instead!
	 */
	@Deprecated
	public MessageBuilder(String text, ChatColor[] styles)
	{
		this(new MessageComponent(text));
		style(styles);
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
	 * Adds a new {@link MessageComponent} to the builder, generated from a text and optional style data.
	 *
	 * @param text   The text that should be used to generate the new {@link MessageComponent} that will be added to the builder.
	 * @param styles The style information for the new {@link MessageComponent} that will be added to the builder.
	 * @return The message builder instance (for chaining).
	 * @deprecated Use {@link at.pcgamingfreaks.Message.MessageBuilder#append(String, MessageColor, MessageFormat...)} instead.
	 */
	@Deprecated
	public MessageBuilder append(String text, ChatColor[] styles)
	{

		if(styles == null || styles.length == 0) return this;
		append(text);
		return style(styles);
	}
	//endregion

	//region Modifier for the current component
	/**
	 * Sets the color of the current component.
	 *
	 * @param color The new color of the current component.
	 * @return The message builder instance (for chaining).
	 * @deprecated Use {@link at.pcgamingfreaks.Message.MessageBuilder#color(MessageColor)} instead!
	 */
	@Deprecated
	public MessageBuilder color(ChatColor color)
	{
		if(color.ordinal() > 15 && color != ChatColor.RESET) throw new IllegalArgumentException(color.name() + " is not a color!");
		getCurrentComponent().setColor(MessageColor.valueOf(color.name()));
		return this;
	}

	/**
	 * Sets the format of the current component
	 *
	 * @param formats The array of formats to apply to the current component.
	 * @return The message builder instance (for chaining).
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 * @deprecated Use {@link at.pcgamingfreaks.Message.MessageBuilder#format(MessageFormat...)} instead!
	 */
	@Deprecated
	public MessageBuilder format(ChatColor... formats) throws IllegalArgumentException
	{
		List<MessageFormat> formatsList = new ArrayList<>(formats.length);
		for(ChatColor format : formats)
		{
			if(format.ordinal() < 16) throw new IllegalArgumentException(format.name() + " is not a format!");
			formatsList.add(MessageFormat.valueOf(format.name()));
		}
		getCurrentComponent().setFormats(formatsList);
		return this;
	}

	/**
	 * Sets the style of the current component.
	 *
	 * @param styles The array of styles to apply to the current component.
	 * @return The message builder instance (for chaining).
	 * @deprecated Use {@link at.pcgamingfreaks.Message.MessageBuilder#format(MessageFormat...)} or {@link at.pcgamingfreaks.Message.MessageBuilder#color(MessageColor)} instead!
	 */
	@Deprecated
	public MessageBuilder style(ChatColor... styles)
	{
		for(ChatColor style : styles)
		{
			if(style == ChatColor.RESET) { color(MessageColor.RESET); format(MessageFormat.RESET); }
			else if(style.ordinal() < 16) color(style);
			else format(style);
		}
		return this;
	}
	//endregion
}