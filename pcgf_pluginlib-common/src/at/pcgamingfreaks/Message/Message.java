/*
 *   Copyright (C) 2021 GeorgH93
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

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Message.Sender.IMetadata;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.StringUtils;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.UnsupportedOperationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Message<MESSAGE extends Message<?,?,?,?>, PLAYER, COMMAND_SENDER, MESSAGE_COMPONENT extends MessageComponent<?>> implements IMessage<PLAYER, COMMAND_SENDER>
{
	private static Class<? extends MessageComponent<?>> MESSAGE_COMPONENT_CLASS;
	private static Method METHOD_MESSAGE_COMPONENT_FROM_JSON = null;
	private static Constructor<? extends MessageBuilder> MESSAGE_BUILDER_CONSTRUCTOR;

	protected static void setMessageComponentClass(Class<? extends MessageComponent<?>> messageComponentClass, Constructor<? extends MessageBuilder> messageBuilderConstructor)
	{
		MESSAGE_COMPONENT_CLASS = messageComponentClass;
		METHOD_MESSAGE_COMPONENT_FROM_JSON = Reflection.getMethod(messageComponentClass, "fromJson", String.class);
		MESSAGE_BUILDER_CONSTRUCTOR = messageBuilderConstructor;
	}

	//region Variables
	@Setter @Getter protected IMetadata optionalParameters = null;
	@Getter protected String json, fallback;
	protected List<MESSAGE_COMPONENT> messageComponents = null;
	@Getter protected boolean placeholderApiEnabled = false;
	@Getter private boolean legacy = false;
	private boolean escaped; // % -> %%
	//endregion

	//region Constructors
	protected Message(final @NotNull String message)
	{
		String errorMessage = null;
		try
		{
			//noinspection unchecked
			messageComponents = (List<MESSAGE_COMPONENT>) METHOD_MESSAGE_COMPONENT_FROM_JSON.invoke(null, message);
		}
		catch(Exception e) { errorMessage = StringUtils.getErrorMessage(e); } // If there was an exception it's very likely that the given message isn't a JSON
		if(messageComponents != null) // The json has been deserialized successful
		{
			json = message; // The given message string was a valid JSON so we are free to send it to the clients
			fallback = getClassicMessage(); // We need a fallback for the console an everything else that isn't a player
		}
		else
		{ // The json has not been deserialized successful, probably a legacy message
			fallback = message; // The message is not a json, so we can use it as a fallback
			if(message.isEmpty())
			{
				messageComponents = new ArrayList<>();
				json = "[\"\",{\"text\":\"\"}]";
				return;
			}

			if(message.length() > 8 && message.charAt(0) != '&' && message.charAt(0) != MessageColor.COLOR_CHAR && StringUtils.containsAny(message, "\"text\":", "\"clickEvent\":"))
			{ // The message contains elements typically found in json messages and does not start with a color format code. It probably should have been parsed as a json. Show a info for the admin.
				System.out.println(ConsoleColor.YELLOW + "It appears that message '" + message + "' is a JSON message, but failed to parse (" + errorMessage + ").\n" +
						                   "If this is a false positive add a legacy format code at the start of your message to suppress this info." + ConsoleColor.RESET);
			}
			//region convert legacy message to json
			legacy = true;
			try
			{
				MessageBuilder<?,MESSAGE_COMPONENT,?> builder = MESSAGE_BUILDER_CONSTRUCTOR.newInstance();
				builder.appendLegacy(message);
				json = builder.getJson();
				messageComponents = builder.getJsonMessageAsList();
			}
			catch(InstantiationException | IllegalAccessException | NullPointerException | InvocationTargetException e)
			{
				e.printStackTrace();
				json = "[\"\"]";
				messageComponents = new ArrayList<>(0);
			}
			//endregion
		}
	}

	protected Message(final @NotNull Collection<? extends MESSAGE_COMPONENT> message)
	{
		messageComponents = new ArrayList<>(message); // Lets save our deserialized JSON into an array (maybe we will need it at a later point, you never know)
		fallback = getClassicMessage(); // We need a fallback for the console and everything else that isn't a player
		json = MessageComponent.GSON.toJson(message); // We need a JSON string to send to the player, so lets generate one from the component list
	}
	//endregion

	public void setPlaceholderApiEnabled(boolean enabled)
	{
		throw new UnsupportedOperationException("Placeholder API is not available for your server type!");
	}

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

	@Override
	public int hashCode()
	{
		return json.hashCode();
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

	public void escapeStringFormatCharacters()
	{
		if (!escaped)
		{
			replaceAll("%", "%%");
			escaped = true;
		}
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

	/**
	 * Fills the placeholders in the message and returns it.
	 *
	 * @param useJson Weather the json or the legacy message should be used. true = JSON
	 * @param args The arguments that should be used to fill the placeholders
	 * @return The message with the filled placeholders
	 */
	public @NotNull String prepareMessage(final boolean useJson, final @Nullable Object... args)
	{
		final String msg = useJson ? json : fallback;
		if(args != null && args.length > 0)
		{
			if(useJson) quoteArgs(args);
			return String.format(msg, args);  // %% will be converted to % automatically
		}
		return escaped ? msg.replaceAll("%%", "%") : msg; // manually convert %% to %
	}
}
