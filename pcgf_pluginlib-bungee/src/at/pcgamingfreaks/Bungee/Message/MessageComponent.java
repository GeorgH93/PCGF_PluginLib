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

package at.pcgamingfreaks.Bungee.Message;

import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Message.MessageFormat;

import net.md_5.bungee.api.ChatColor;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Message component class.
 *
 * @deprecated Use {@link at.pcgamingfreaks.Message.MessageComponent} instead.
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.0.37")
public final class MessageComponent extends at.pcgamingfreaks.Message.MessageComponent
{
	//region Constructors
	/**
	 * Creates a new empty MessageComponent instance.
	 */
	public MessageComponent() {}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text   The text for the {@link MessageComponent}.
	 * @param formats The style for the {@link MessageComponent}.
	 */
	public MessageComponent(String text, final MessageFormat... formats)
	{
		super(text, formats);
	}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text    The text for the {@link MessageComponent}.
	 * @param color   The color for the {@link MessageComponent}.
	 * @param formats The style for the {@link MessageComponent}.
	 */
	public MessageComponent(final @NotNull String text, final @Nullable MessageColor color, final MessageFormat... formats)
	{
		super(text, color, formats);
	}
	//endregion

	//region Message modifier (getter/setter)
	/**
	 * Gets the color of the component.
	 *
	 * @return The color of the component as a {@link ChatColor}, null if no color is defined.
	 *
	 * @deprecated Use {@link at.pcgamingfreaks.Message.MessageComponent#getColor()} instead.
	 */
	@Deprecated
	public ChatColor getChatColor()
	{
		return ChatColor.valueOf(getColorString().toUpperCase(Locale.ROOT));
	}
	//endregion
}