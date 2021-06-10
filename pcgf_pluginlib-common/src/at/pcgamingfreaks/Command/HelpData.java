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

package at.pcgamingfreaks.Command;

import at.pcgamingfreaks.Message.MessageClickEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;

/**
 * Stores all the data that should be displayed when the help for a command is shown.
 */
public class HelpData
{
	/**
	 * The translated name of the sub-command.
	 */
	@Getter @Setter @NotNull private String translatedSubCommand;

	/**
	 * The parameters to be displayed. null or "" for no parameters.
	 */
	@SuppressWarnings("NotNullFieldNotInitialized")
	@Getter @NotNull private String parameter;

	/**
	 * The description of the sub-command.
	 */
	@Getter @Setter @NotNull private String description;

	/**
	 * The action that should be used when the sub-command gets clicked.
	 */
	@Getter @Setter @NotNull private MessageClickEvent.ClickEventAction clickAction;

	/**
	 * Creates a new instance of the {@link HelpData} object to store the data for the sub-command.
	 *
	 * @param translatedSubCommand The translated name of the sub-command.
	 * @param parameter The parameters to be displayed. null or "" for no parameters.
	 * @param description The description of the sub-command.
	 */
	public HelpData(final @NotNull String translatedSubCommand, final @Nullable String parameter, final @NotNull String description)
	{
		this(translatedSubCommand, parameter, description, MessageClickEvent.ClickEventAction.SUGGEST_COMMAND);
	}

	/**
	 * Creates a new instance of the {@link HelpData} object to store the data for the sub-command.
	 *
	 * @param translatedSubCommand The translated name of the sub-command.
	 * @param parameter The parameters to be displayed. null or "" for no parameters.
	 * @param description The description of the sub-command.
	 * @param clickAction The action that should be executed when the help line of the command is pressed.
	 */
	public HelpData(final @NotNull String translatedSubCommand, final @Nullable String parameter, final @NotNull String description, final @NotNull MessageClickEvent.ClickEventAction clickAction)
	{
		setTranslatedSubCommand(translatedSubCommand);
		setParameter(parameter);
		setDescription(description);
		setClickAction(clickAction);
	}

	/**
	 * Sets the parameter string of the help data.
	 *
	 * @param parameter The parameters to be displayed. null or "" for no parameters.
	 */
	public void setParameter(final @Nullable String parameter)
	{
		this.parameter = (parameter == null) ? "" : parameter;
	}
}