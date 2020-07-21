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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Message;

import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.StringUtils;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Message<MESSAGE extends Message<?,?,?,?>, PLAYER, COMMAND_SENDER, MESSAGE_COMPONENT extends MessageComponent<?>> implements IMessage<PLAYER, COMMAND_SENDER>
{
	private static Class<? extends MessageComponent<?>> MESSAGE_COMPONENT_CLASS;
	private static Method METHOD_MESSAGE_COMPONENT_FROM_JSON = null;

	protected static void setMessageComponentClass(Class<? extends MessageComponent<?>> messageComponentClass)
	{
		MESSAGE_COMPONENT_CLASS = messageComponentClass;
		METHOD_MESSAGE_COMPONENT_FROM_JSON = Reflection.getMethod(messageComponentClass, "fromJson", String.class);
	}

	//region Variables
	protected Object optionalParameters = null;
	protected String json, fallback;
	protected List<MESSAGE_COMPONENT> messageComponents = null;
	//endregion

	//region Constructors
	protected Message(final @NotNull String message)
	{
		try
		{
			//noinspection unchecked
			messageComponents = (List<MESSAGE_COMPONENT>) METHOD_MESSAGE_COMPONENT_FROM_JSON.invoke(null, message);
		}
		catch(Exception ignored) {} // If there was an exception it's very likely that the given message isn't a JSON, that's all we need to know.
		if(messageComponents != null) // The json was successfully deserialized
		{
			json = message; // The given message string was a valid JSON so we are free to send it to the clients
			fallback = getClassicMessage(); // We need a fallback for the console an everything else that isn't a player
		}
		else
		{
			List<MESSAGE_COMPONENT> messageComponentsList = new ArrayList<>(1);
			try
			{
				//noinspection unchecked
				MESSAGE_COMPONENT mc = (MESSAGE_COMPONENT) MESSAGE_COMPONENT_CLASS.newInstance();
				mc.setText(message);
				messageComponentsList.add(mc);
				messageComponents = messageComponentsList;
			}
			catch(InstantiationException | IllegalAccessException | NullPointerException e)
			{
				e.printStackTrace();
			}
			json = MessageComponent.GSON.toJson(messageComponents);
			fallback = message; // Our message is no JSON so we can send it to everyone
		}
	}

	protected Message(final @NotNull Collection<? extends MESSAGE_COMPONENT> message)
	{
		messageComponents = new ArrayList<>(message); // Lets save our deserialized JSON into an array (maybe we will need it at a later point, you never know)
		fallback = getClassicMessage(); // We need a fallback for the console and everything else that isn't a player
		json = MessageComponent.GSON.toJson(message); // We need a JSON string to send to the player, so lets generate one from the component list
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
	public @NotNull MESSAGE_COMPONENT[] getMessageComponents()
	{
		//noinspection unchecked
		return messageComponents.toArray((MESSAGE_COMPONENT[]) Array.newInstance(MESSAGE_COMPONENT_CLASS, 0));
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
		return this == otherObject || (otherObject instanceof Message<?,?,?,?> && json.equals(((Message<?,?,?,?>) otherObject).json));
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
	public @NotNull MESSAGE replaceAll(@NotNull @Language("RegExp") String regex, @NotNull String replacement)
	{
		json = json.replaceAll(regex, replacement);
		fallback = fallback.replaceAll(regex, replacement);
		//noinspection unchecked
		return (MESSAGE) this;
	}

	protected Object[] quoteArgs(final Object[] args)
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

	protected @NotNull String prepareMessage(final boolean useJson, final @Nullable Object... args)
	{
		final String msg = useJson ? json : fallback;
		if(args != null && args.length > 0)
		{
			if(useJson) quoteArgs(args);
			return String.format(msg, args);
		}
		return msg;
	}
}