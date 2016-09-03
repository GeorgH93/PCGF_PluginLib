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

import com.google.gson.*;

import net.md_5.bungee.api.ChatColor;

import java.util.HashSet;
import java.util.List;

public final class MessageComponent extends at.pcgamingfreaks.Message.MessageComponent<MessageComponent> implements JsonDeserializer<MessageComponent>
{
	private transient final static at.pcgamingfreaks.Message.MessageComponent NEW_LINE_HELPER = new MessageComponent("\n");
	private transient final static HashSet<ChatColor> COLOR_LIST = new HashSet<>();
	private transient final static MessageComponent MESSAGE_COMPONENT_INSTANCE = new MessageComponent();

	static
	{
		COLOR_LIST.add(ChatColor.BLACK);
		COLOR_LIST.add(ChatColor.DARK_BLUE);
		COLOR_LIST.add(ChatColor.DARK_GREEN);
		COLOR_LIST.add(ChatColor.DARK_AQUA);
		COLOR_LIST.add(ChatColor.DARK_RED);
		COLOR_LIST.add(ChatColor.DARK_PURPLE);
		COLOR_LIST.add(ChatColor.GOLD);
		COLOR_LIST.add(ChatColor.GRAY);
		COLOR_LIST.add(ChatColor.DARK_GRAY);
		COLOR_LIST.add(ChatColor.BLUE);
		COLOR_LIST.add(ChatColor.GREEN);
		COLOR_LIST.add(ChatColor.AQUA);
		COLOR_LIST.add(ChatColor.RED);
		COLOR_LIST.add(ChatColor.LIGHT_PURPLE);
		COLOR_LIST.add(ChatColor.YELLOW);
		COLOR_LIST.add(ChatColor.WHITE);

		GSON = new GsonBuilder().registerTypeAdapter(MessageComponent.class, MESSAGE_COMPONENT_INSTANCE).create();
		try
		{
			messageComponentClass = MessageComponent.class;
			messageComponentConstructor = MessageComponent.class.getConstructor();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
	 * @param text   The text for the MessageComponent.
	 * @param styles The style for the MessageComponent.
	 */
	public MessageComponent(String text, ChatColor... styles)
	{
		super(text);
		setStyles(styles);
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

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).
	 */
	public MessageComponent setColor(ChatColor color) throws IllegalArgumentException
	{
		setColor(color.name().toUpperCase());
		return this;
	}

	/**
	 * Sets the formats of the component.
	 *
	 * @param formats The array of formats to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	@SuppressWarnings("Duplicates")
	public MessageComponent setFormats(ChatColor... formats) throws IllegalArgumentException
	{
		if(formats != null)
		{
			for(ChatColor style : formats)
			{
				//noinspection SuspiciousMethodCalls
				if(COLOR_LIST.contains(formats))
				{
					throw new IllegalArgumentException(style.name() + " is not a formatter");
				}
			}
			for(ChatColor format : formats)
			{
				switch(format)
				{
					case ITALIC: setItalic(); break;
					case BOLD: setBold(); break;
					case UNDERLINE: setUnderlined(); break;
					case STRIKETHROUGH: setStrikethrough(); break;
					case MAGIC: setObfuscated(); break;
				}
			}
		}
		return this;
	}

	/**
	 * Sets the style of the component.
	 *
	 * @param styles The array of styles to apply to the component.
	 * @return This message component instance.
	 */
	public MessageComponent setStyles(ChatColor... styles)
	{
		if(styles != null)
		{
			for(ChatColor style : styles)
			{
				if(COLOR_LIST.contains(style))
				{
					setColor(style);
				}
				else
				{
					setFormats(style);
				}
			}
		}
		return this;
	}
	//endregion

	//region Short message modifier (setter)
	/**
	 * Sets the format of the component.
	 *
	 * @param formats The array of format to apply to the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
	 */
	public MessageComponent format(ChatColor... formats) throws IllegalArgumentException
	{
		return setFormats(formats);
	}

	/**
	 * Sets the style of the component.
	 *
	 * @param styles The array of styles to apply to the component.
	 * @return This message component instance.
	 */
	public MessageComponent style(ChatColor... styles)
	{
		return setStyles(styles);
	}
	//endregion
}