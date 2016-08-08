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

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The hover event is used for the JSON messages when the part of the message using it is hovered by the player with the cursor.
 */
public class MessageHoverEvent
{
	private static final transient String VALUE_CAN_NOT_BE_NULL = "The value for the click event can't be null!",
										  VALUE_SHOULD_NOT_BE_EMPTY = "The value for the hover event should not be empty!",
										  ACTION_CAN_NOT_BE_NULL = "The action for the hover event can't be null!";

	private HoverEventAction action;
	private Object value;

	/**
	 * Creates a new hover event for a JSON message component.
	 *
	 * @param action The action that should be executed when the user hovers over the text with his cursor.
	 * @param value  The value used for the action of the event.
	 */
	public MessageHoverEvent(@NotNull HoverEventAction action, @NotNull String value)
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
	public MessageHoverEvent(@NotNull HoverEventAction action, @NotNull JsonObject value)
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
	public MessageHoverEvent(@NotNull HoverEventAction action, @NotNull Message value)
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
	public MessageHoverEvent(@NotNull HoverEventAction action, @NotNull Collection<? extends MessageComponent> value)
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
	public MessageHoverEvent(@NotNull HoverEventAction action, @NotNull MessageComponent[] value)
	{
		setAction(action);
		setValue(value);
	}

	/**
	 * Changes the action of the hover event.
	 *
	 * @param action The new action that should be executed when the user hovers over the text with his cursor.
	 */
	public void setAction(@NotNull HoverEventAction action)
	{
		Validate.notNull(action, ACTION_CAN_NOT_BE_NULL);
		this.action = action;
	}

	/**
	 * Gets the action of the hover event.
	 *
	 * @return The action that should be executed when the user hovers over the text with his cursor.
	 */
	public @NotNull HoverEventAction getAction()
	{
		return action;
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(@NotNull String value)
	{
		Validate.notNull(action, VALUE_CAN_NOT_BE_NULL);
		Validate.notEmpty(value, VALUE_SHOULD_NOT_BE_EMPTY);
		this.value = value;
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(@NotNull JsonObject value)
	{
		Validate.notNull(action, VALUE_CAN_NOT_BE_NULL);
		this.value = value;
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(@NotNull Collection<? extends MessageComponent> value)
	{
		Validate.notNull(action, VALUE_CAN_NOT_BE_NULL);
		this.value = value;
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(@NotNull MessageComponent[] value)
	{
		Validate.notNull(action, VALUE_CAN_NOT_BE_NULL);
		this.value = value;
	}

	/**
	 * Changes the value of the hover event.
	 *
	 * @param value The new value used for the action of the event.
	 */
	public void setValue(@NotNull Message value)
	{
		Validate.notNull(action, VALUE_CAN_NOT_BE_NULL);
		setValue(value.getMessageComponents());
	}

	/**
	 * Gets the value of the hover event.
	 *
	 * @return The value of the hover event.
	 */
	public @NotNull Object getValue()
	{
		return value;
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