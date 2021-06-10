/*
 *   Copyright (C) 2020 GeorgH93
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

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * The hover event is used for the JSON messages when the part of the message using it is hovered by the player with the cursor.
 */
public class MessageHoverEvent
{
	/**
	 * The action of the hover event.
	 */
	@Getter @Setter	private @NotNull HoverEventAction action;

	/**
	 * The value of the hover event.
	 */
	@SuppressWarnings("NotNullFieldNotInitialized")
	@Getter private @NotNull Object value;

	/**
	 * Creates a new hover event for a JSON message component.
	 *
	 * @param action The action that should be executed when the user hovers over the text with his cursor.
	 * @param value  The value used for the action of the event.
	 */
	public MessageHoverEvent(final @NotNull HoverEventAction action, final @NotNull String value)
	{
		setAction(action);
		setValue(value);
	}

	/**
	 * Creates a new hover event for a JSON message component.
	 *
	 * @param action The action that should be executed when the user hovers over the text with his cursor.
	 * @param value  The value used for the action of the event.
	 */
	public MessageHoverEvent(final @NotNull HoverEventAction action, final @NotNull JsonObject value)
	{
		setAction(action);
		setValue(value);
	}

	/**
	 * Creates a new hover event for a JSON message component.
	 *
	 * @param action The action that should be executed when the user hovers over the text with his cursor.
	 * @param value  The value used for the action of the event.
	 */
	public MessageHoverEvent(final @NotNull HoverEventAction action, final @NotNull Message value)
	{
		setAction(action);
		setValue(value);
	}

	/**
	 * Creates a new hover event for a JSON message component.
	 *
	 * @param action The action that should be executed when the user hovers over the text with his cursor.
	 * @param value  The value used for the action of the event.
	 */
	public MessageHoverEvent(final @NotNull HoverEventAction action, final @NotNull Collection<? extends MessageComponent> value)
	{
		setAction(action);
		setValue(value);
	}

	/**
	 * Creates a new hover event for a JSON message component.
	 *
	 * @param action The action that should be executed when the user hovers over the text with his cursor.
	 * @param value  The value used for the action of the event.
	 */
	public MessageHoverEvent(final @NotNull HoverEventAction action, final @NotNull MessageComponent[] value)
	{
		setAction(action);
		setValue(value);
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(final @NotNull String value)
	{
		this.value = value;
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(final @NotNull JsonObject value)
	{
		this.value = value;
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(final @NotNull Collection<? extends MessageComponent> value)
	{
		this.value = value;
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(final @NotNull MessageComponent[] value)
	{
		this.value = value;
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(final @NotNull Message<?,?,?,?> value)
	{
		setValue(value.getMessageComponents());
	}

	/**
	 * Enum with all possible actions for a hover event.
	 */
	public enum HoverEventAction
	{
		/**
		 * Shows a text.
		 */
		@SerializedName("show_text") SHOW_TEXT,
		/**
		 * Shows an item.
		 */
		@SerializedName("show_item") SHOW_ITEM,
		/**
		 * Shows an achievement.
		 */
		@SerializedName("show_achievement") SHOW_ACHIEVEMENT,
		/**
		 * Shows an entity.
		 */
		@SerializedName("show_entity") SHOW_ENTITY
	}
}