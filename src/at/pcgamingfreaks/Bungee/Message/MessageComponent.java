/*
 * Copyright (C) 2016 GeorgH93
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

package at.pcgamingfreaks.Bungee.Message;

import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Reflection;

import com.google.gson.*;

import net.md_5.bungee.api.ChatColor;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class MessageComponent extends at.pcgamingfreaks.Message.MessageComponent<MessageComponent, ChatColor> implements JsonDeserializer<MessageComponent>
{
	private static final transient at.pcgamingfreaks.Message.MessageComponent NEW_LINE_HELPER = new MessageComponent("\n");
	private static final transient MessageComponent MESSAGE_COMPONENT_INSTANCE = new MessageComponent();

	static
	{
		GSON = new GsonBuilder().registerTypeAdapter(MessageComponent.class, MESSAGE_COMPONENT_INSTANCE).create();
		messageComponentClass = MessageComponent.class;
		messageComponentConstructor = Reflection.getConstructor(MessageComponent.class);
	}

	@Override
	protected at.pcgamingfreaks.Message.MessageComponent getNewLineComponent()
	{
		return NEW_LINE_HELPER;
	}

	//region Constructors
	/**
	 * Creates a new empty MessageComponent instance.
	 */
	public MessageComponent() {}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text   The text for the {@link MessageComponent}.
	 * @param styles The style for the {@link MessageComponent}.
	 */
	public MessageComponent(String text, MessageColor... styles)
	{
		super(text, styles);
	}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text   The text for the {@link MessageComponent}.
	 * @param styles The style for the {@link MessageComponent}.
	 */
	public MessageComponent(String text, @Nullable ChatColor[] styles)
	{
		super(text, styles);
	}
	//endregion

	//region Deserializer and Deserializer Functions
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

	//region Message modifier (getter/setter)
	/**
	 * Gets the color of the component.
	 *
	 * @return The color of the component as a {@link ChatColor}, null if no color is defined.
	 */
	public ChatColor getChatColor()
	{
		return ChatColor.valueOf(getColorString().toUpperCase());
	}
	//endregion
}