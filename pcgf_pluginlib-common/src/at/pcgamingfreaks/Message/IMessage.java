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
	/**
	 * Replaces strings within this message.
	 * This can be used to replace placeholders with static texts or with whitespaces for string format.
	 * The function is used the same way as String.replaceAll.
	 *
	 * @param regex       The regular expression to which the strings are to be matched.
	 * @param replacement The string which would replace the found expression.
	 * @return            This message instance (for chaining).
	 *
	 * @deprecated Use placeholders instead. {@link IMessage#registerPlaceholder(String, IPlaceholderProcessor, int)}.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "1.0.40")
	@NotNull IMessage replaceAll(@NotNull @Language("RegExp") String regex, @NotNull String replacement);

	/**
	 * Registers a placeholder to be used with the message.
	 * A placeholder has to always match the <pre>{[a-zA-Z0-9_-]+}</pre> pattern.
	 * It will use the default .toString() as the placeholder processor.
	 *
	 * @param placeholder          The placeholder that should be registered. If the value is not surrounded by {}, they will be added automatically.
	 * @return                     This message instance (for chaining).
	 */
	@NotNull IMessage placeholder(@NotNull String placeholder);

	/**
	 * Registers a placeholder to be used with the message.
	 *
	 * @param placeholder          The placeholder that should be registered. If the value is not surrounded by {}, they will be added automatically.
	 * @param placeholderProcessor The placeholder processor that should be used for this placeholder.
	 * @return                     This message instance (for chaining).
	 */
	@NotNull IMessage placeholder(@NotNull String placeholder, @Nullable IPlaceholderProcessor placeholderProcessor);

	/**
	 * Registers a placeholder to be used with the message.
	 *
	 * @param placeholder          The placeholder that should be registered. If the value is not surrounded by {}, they will be added automatically.
	 * @param placeholderProcessor The placeholder processor that should be used for this placeholder.
	 * @return                     This message instance (for chaining).
	 */
	@NotNull IMessage registerPlaceholder(@NotNull String placeholder, @Nullable IPlaceholderProcessor placeholderProcessor);

	/**
	 * Registers a placeholder to be used with the message.
	 *
	 * @param placeholder          The placeholder that should be registered. If the value is not surrounded by {}, they will be added automatically.
	 * @param placeholderProcessor The placeholder processor that should be used for this placeholder.
	 * @param parameterIndex       The parameter index which will be used as a data source for this placeholder (this is the element index of the args parameter in the send/broadcast methods).
	 * @return                     This message instance (for chaining).
	 */
	@NotNull IMessage registerPlaceholder(@NotNull String placeholder, @Nullable IPlaceholderProcessor placeholderProcessor, int parameterIndex);

	//@NotNull IMessage registerPlaceholders(Pair<String, IPlaceholderProcessor>... placeholders);

	/**
	 * Registers a placeholder to be used with the message.
	 * It will use the default .toString() as the placeholder processor.
	 * A placeholder has to always match the <pre>{[a-zA-Z0-9_-]+}</pre> pattern.
	 *
	 * @param placeholder          A regex pattern for the placeholder to be registered. Please only enter the pattern for the part between the {}.
	 * @return                     This message instance (for chaining).
	 */
	@NotNull IMessage placeholderRegex(@NotNull @Language("RegExp") String placeholder);

	/**
	 * Registers a placeholder to be used with the message.
	 * A placeholder has to always match the <pre>{[a-zA-Z0-9_-]+}</pre> pattern.
	 *
	 * @param placeholder          A regex pattern for the placeholder to be registered. Please only enter the pattern for the part between the {}.
	 * @param placeholderProcessor The placeholder processor that should be used for this placeholder.
	 * @return                     This message instance (for chaining).
	 */
	@NotNull IMessage placeholderRegex(@NotNull @Language("RegExp") String placeholder, @Nullable IPlaceholderProcessor placeholderProcessor);

	/**
	 * Registers a placeholder to be used with the message.
	 * A placeholder has to always match the <pre>{[a-zA-Z0-9_-]+}</pre> pattern.
	 *
	 * @param placeholder          A regex pattern for the placeholder to be registered. Please only enter the pattern for the part between the {}.
	 * @param placeholderProcessor The placeholder processor that should be used for this placeholder.
	 * @return                     This message instance (for chaining).
	 */
	@NotNull IMessage registerPlaceholderRegex(@NotNull @Language("RegExp") String placeholder, @Nullable IPlaceholderProcessor placeholderProcessor);

	/**
	 * Registers a placeholder to be used with the message.
	 * A placeholder has to always match the <pre>{[a-zA-Z0-9_-]+}</pre> pattern.
	 *
	 * @param placeholder          A regex pattern for the placeholder to be registered. Please only enter the pattern for the part between the {}.
	 * @param placeholderProcessor The placeholder processor that should be used for this placeholder.
	 * @param parameterIndex       The parameter index which will be used as a data source for this placeholder (this is the element index of the args parameter in the send/broadcast methods).
	 * @return                     This message instance (for chaining).
	 */
	@NotNull IMessage registerPlaceholderRegex(@NotNull @Language("RegExp") String placeholder, @Nullable IPlaceholderProcessor placeholderProcessor, int parameterIndex);

	//@NotNull IMessage registerPlaceholdersRegex(Pair<String, IPlaceholderProcessor>... placeholders);

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