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

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

/**
 * The click event is used for the JSON messages when the part of the message using it is clicked.
 */
public class MessageClickEvent
{
	private ClickEventAction action;
	private String value;

	/**
	 * Creates a new click event for a JSON message component.
	 *
	 * @param action The action that should be executed on click.
	 * @param value  The value used for the action of the event.
	 */
	public MessageClickEvent(@NotNull ClickEventAction action, @NotNull String value)
	{
		Validate.notNull(action, "The action for the click event can't be null!");
		Validate.notEmpty(value, "The value for the click event should not be empty!");
		this.action = action;
		this.value = value;
	}

	/**
	 * Changes the action of the click event.
	 *
	 * @param action The new action that should be executed when the part of the message using this event is clicked.
	 */
	public void setAction(@NotNull ClickEventAction action)
	{
		Validate.notNull(action, "The action for the click event can't be null!");
		this.action = action;
	}

	/**
	 * Gets the action of the click event.
	 *
	 * @return The action that should be executed when the part of the message using this event is clicked.
	 */
	public @NotNull ClickEventAction getAction()
	{
		return action;
	}

	/**
	 * Changes the value of the click event.
	 *
	 * @param value The new value for the click event.
	 */
	public void setValue(@NotNull String value)
	{
		Validate.notEmpty(value, "The value for the click event should not be empty!");
		this.value = value;
	}

	/**
	 * Gets the value of the click event.
	 *
	 * @return The value of the click event.
	 */
	public @NotNull String getValue()
	{
		return value;
	}

	/**
	 * Enum with all possible actions for a click event.
	 */
	public enum ClickEventAction
	{
		/**
		 * Runs a command as the clicking player.
		 */
		@SerializedName("run_command") RUN_COMMAND,
		/**
		 * Suggests a command in the chat bar of the clicking player.
		 */
		@SerializedName("suggest_command") SUGGEST_COMMAND,
		/**
		 * Opens a url in the browser of the clicking player.
		 */
		@SerializedName("open_url") OPEN_URL,
		/**
		 * Changes the page of the book the clicking player is currently reading. <b>Only works in books!!!</b>
		 */
		@SerializedName("change_page") CHANGE_PAGE,
		/**
		 * Opens a file on the clicking players hard drive. Used from minecraft for the clickable screenshot link.
		 * The chance that we know the path to a file on the clients hard disk is pretty low, so the usage of this is pretty limited.
		 */
		@SerializedName("open_file") OPEN_FILE
	}
}