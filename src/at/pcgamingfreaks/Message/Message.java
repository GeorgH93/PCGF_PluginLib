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

import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.StringUtils;

import com.google.gson.Gson;

import org.apache.commons.lang3.Validate;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class Message<T extends Message, PLAYER, COMMAND_SENDER>
{
	//region Variables
	protected static final Gson GSON = new Gson();

	protected Object optionalParameters = null;
	protected String json, fallback;
	protected List<? extends MessageComponent> messageComponents = null;
	protected final int hashCode;
	//endregion

	//region Constructors
	protected Message(@NotNull String message, Class messageComponentClass)
	{
		Validate.notEmpty("The message should not be empty!");
		try
		{
			//noinspection unchecked
			messageComponents = (List<? extends MessageComponent>) Reflection.getMethod(messageComponentClass, "fromJson", String.class).invoke(null, message);
		}
		catch(Exception ignored) {} // If there was an exception it's very likely that the given message isn't a JSON, that's all we need to know.
		if(messageComponents != null) // The json was successfully deserialized
		{
			json = message; // The given message string was a valid JSON so we are free to send it to the clients
			fallback = getClassicMessage(); // We need a fallback for the console an everything else that isn't a player
		}
		else
		{
			List<MessageComponent> messageComponentsList = new ArrayList<>(1);
			try
			{
				//noinspection ConstantConditions
				MessageComponent mc = (MessageComponent) Reflection.getConstructor(messageComponentClass).newInstance();
				mc.setText(message);
				messageComponentsList.add(mc);
				messageComponents = messageComponentsList;
			}
			catch(InstantiationException | IllegalAccessException | InvocationTargetException | NullPointerException e)
			{
				e.printStackTrace();
			}
			json = GSON.toJson(messageComponents);
			fallback = message; // Our message is no JSON so we can send it to everyone
		}
		hashCode = json.hashCode(); // Store the hashCode
	}

	protected Message(@NotNull Collection<? extends MessageComponent> message)
	{
		Validate.notEmpty(message, "The message should not be empty!");
		messageComponents = new LinkedList<>(message); // Lets save our deserialized JSON into an array (maybe we will need it at a later point, you never know)
		fallback = getClassicMessage(); // We need a fallback for the console and everything else that isn't a player
		json = GSON.toJson(message); // We need a JSON string to send to the player, so lets generate one from the component list
		hashCode = json.hashCode();  // Store the hashCode
	}
	//endregion

	/**
	 * Gets the message in the classic minecraft chat format (used for minecraft version below 1.7).
	 *
	 * @return The message in the classic minecraft chat format.
	 */
	public @NotNull String getClassicMessage()
	{
		return (fallback == null) ? MessageComponent.getClassicMessage(messageComponents) : fallback;
	}

	/**
	 * Gets the message as an array of {@link MessageComponent}'s.
	 *
	 * @return The array of {@link MessageComponent}'s representing the message.
	 */
	public @NotNull MessageComponent[] getMessageComponents()
	{
		return messageComponents.toArray(new MessageComponent[messageComponents.size()]);
	}

	/**
	 * Gets the message as a JSON string.
	 *
	 * @return The JSON string representing the message.
	 */
	@Override
	public @NotNull String toString()
	{
		return json;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		//noinspection NonFinalFieldReferenceInEquals
		return otherObject instanceof Message && json.equals(((Message) otherObject).json);
	}

	@Override
	public int hashCode()
	{
		return hashCode;
	}

	public void setOptionalParameters(@NotNull Object optionalParameters)
	{
		this.optionalParameters = optionalParameters;
	}

	/**
	 * Replaces strings within the JSON and the classic message of this message.
	 * This can be used to replace placeholders with static texts or with whitespaces for string format.
	 * The function is used the same way like String.replaceAll.
	 *
	 * @param regex       The regular expression to which the strings are to be matched.
	 * @param replacement The string which would replace the found expression.
	 * @return            This message instance (for chaining).
	 */
	public @NotNull T replaceAll(@NotNull @Language("RegExp") String regex, @NotNull String replacement)
	{
		json = json.replaceAll(regex, replacement);
		fallback = fallback.replaceAll(regex, replacement);
		//noinspection unchecked
		return (T) this;
	}

	//region Send methods
	/**
	 * Sends the message to a target.
	 *
	 * @param target The target that should receive the message.
	 * @param args   An optional array of arguments.
	 *                  If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                  This can be used to add variable data into the message.
	 */
	public abstract void send(@NotNull COMMAND_SENDER target, @Nullable Object... args);

	/**
	 * Sends the message to a {@link Collection} of targets.
	 *
	 * @param targets The targets that should receive the message.
	 * @param args    An optional array of arguments.
	 *                   If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                   This can be used to add variable data into the message.
	 */
	public abstract void send(@NotNull Collection<? extends PLAYER> targets, @Nullable Object... args);

	/**
	 * Sends the message to all online players on the server, as well as the console.
	 *
	 * @param args    An optional array of arguments.
	 *                   If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                   This can be used to add variable data into the message.
	 */
	public abstract void broadcast(@Nullable Object... args);
	//endregion

	protected Object[] quoteArgs(Object[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			if(args[i] instanceof String)
			{
				args[i] = StringUtils.escapeJsonString((String) args[i]);
			}
		}
		return args;
	}
}