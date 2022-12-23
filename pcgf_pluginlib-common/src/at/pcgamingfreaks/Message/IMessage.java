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

package at.pcgamingfreaks.Message;

import at.pcgamingfreaks.Message.Placeholder.Processors.IPlaceholderProcessor;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IMessage<PLAYER, COMMAND_SENDER>
{
	//region Send methods
	/**
	 * Sends the message to a target.
	 *
	 * @param target The target that should receive the message.
	 * @param args   An optional array of arguments.
	 *                  If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                  This can be used to add variable data into the message.
	 */
	void send(@NotNull COMMAND_SENDER target, @Nullable Object... args);

	/**
	 * Sends the message to a {@link Collection} of targets.
	 *
	 * @param targets The targets that should receive the message.
	 * @param args    An optional array of arguments.
	 *                   If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                   This can be used to add variable data into the message.
	 */
	void send(@NotNull Collection<? extends PLAYER> targets, @Nullable Object... args);

	/**
	 * Sends the message to all online players on the server, as well as the console.
	 *
	 * @param args    An optional array of arguments.
	 *                   If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                   This can be used to add variable data into the message.
	 */
	void broadcast(@Nullable Object... args);
	//endregion
}