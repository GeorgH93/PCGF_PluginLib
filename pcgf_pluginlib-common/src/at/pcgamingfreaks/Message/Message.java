/*
 *   Copyright (C) 2023 GeorgH93
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
import at.pcgamingfreaks.Message.Placeholder.Placeholder;
import at.pcgamingfreaks.Message.Placeholder.Processors.IPlaceholderProcessor;
import at.pcgamingfreaks.Message.Sender.IMetadata;
import at.pcgamingfreaks.Util.StringUtils;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Message<MESSAGE extends Message<?,?,?>, PLAYER, COMMAND_SENDER> implements IMessage<PLAYER, COMMAND_SENDER>, IPlaceholderable
{
	private static Constructor<? extends MessageBuilder> MESSAGE_BUILDER_CONSTRUCTOR;

	protected static void setMessageComponentClass(Constructor<? extends MessageBuilder> messageBuilderConstructor)
	{
		MESSAGE_BUILDER_CONSTRUCTOR = messageBuilderConstructor;
	}

	//region Variables
	@Setter private static boolean defaultUseJavaEditionFormatting = true;
	@Setter @Getter protected IMetadata optionalParameters = null;
	@Getter protected String json, fallback;
	protected List<MessageComponent> messageComponents = null;
	@Getter protected boolean placeholderApiEnabled = false;
	@Getter private boolean legacy = false;

	private PlaceholderHandler placeholderHandler = null;
	//endregion

	//region Constructors
	protected Message(final @NotNull String message)
	{
		this(message, defaultUseJavaEditionFormatting);
	}

	protected Message(final @NotNull String message, final boolean useJavaEditionFormatting)
	{
		String errorMessage = null;
		try
		{
			messageComponents = MessageComponent.fromJson(message);
		}
		catch(Exception e) { errorMessage = StringUtils.getErrorMessage(e); } // If there was an exception it's very likely that the given message isn't a JSON
		if(messageComponents != null) // The json has been deserialized successful
		{
			json = message; // The given message string was a valid JSON, so we are free to send it to the clients
			fallback = getClassicMessage(); // We need a fallback for the console and everything else that isn't a player
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
				MessageBuilder<?,?> builder = MESSAGE_BUILDER_CONSTRUCTOR.newInstance();
				builder.appendLegacy(message, useJavaEditionFormatting);
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

	protected Message(final @NotNull Collection<? extends MessageComponent> message)
	{
		messageComponents = new ArrayList<>(message); // Lets save our deserialized JSON into an array (maybe we will need it at a later point, you never know)
		fallback = getClassicMessage(); // We need a fallback for the console and everything else that isn't a player
		json = MessageComponent.GSON.toJson(message); // We need a JSON string to send to the player, so let's generate one from the component list
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
	public @NotNull MessageComponent[] getMessageComponents()
	{
		return messageComponents.toArray((MessageComponent[])Array.newInstance(MessageComponent.class, 0));
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
		return this == otherObject || (otherObject instanceof Message<?,?,?> && json.equals(((Message<?,?,?>) otherObject).json));
	}

	@Override
	public int hashCode()
	{
		return json.hashCode();
	}

	@Override
	public @NotNull MESSAGE placeholder(@NotNull String placeholder)
	{
		return registerPlaceholder(placeholder, null);
	}

	@Override
	public @NotNull MESSAGE placeholder(@NotNull String placeholder, IPlaceholderProcessor placeholderProcessor)
	{
		return registerPlaceholder(placeholder, placeholderProcessor);
	}

	@Override
	public @NotNull MESSAGE registerPlaceholder(@NotNull String placeholder, IPlaceholderProcessor placeholderProcessor)
	{
		return registerPlaceholders(new Placeholder(placeholder, placeholderProcessor));
	}

	@Override
	public @NotNull MESSAGE registerPlaceholder(@NotNull String placeholder, IPlaceholderProcessor placeholderProcessor, int parameterIndex)
	{
		return registerPlaceholders(new Placeholder(placeholder, placeholderProcessor, parameterIndex));
	}

	@Override
	public @NotNull MESSAGE placeholders(@NotNull Placeholder... placeholders)
	{
		return registerPlaceholders(placeholders);
	}

	public @NotNull MESSAGE staticPlaceholder(@NotNull String placeholder, @Nullable IPlaceholderProcessor placeholderProcessor, Object parameter)
	{
		boolean canOptimize = placeholderHandler == null;
		registerPlaceholders(new Placeholder(placeholder, placeholderProcessor, parameter));
		if (canOptimize)
		{
			fallback = placeholderHandler.formatLegacy();
			json = placeholderHandler.format();
			placeholderHandler = null;
		}
		return (MESSAGE) this;
	}

	@Override
	public @NotNull MESSAGE registerPlaceholders(@NotNull Placeholder... placeholders)
	{
		if (placeholderHandler == null) placeholderHandler = new PlaceholderHandler(this);
		placeholderHandler.register(placeholders);
		//noinspection unchecked
		return (MESSAGE) this;
	}

	@Override
	public @NotNull MESSAGE placeholders(final @NotNull String... placeholderNames)
	{
		Placeholder[] placeholders = new Placeholder[placeholderNames.length];
		for(int i = 0; i < placeholderNames.length; i++)
		{
			placeholders[i] = new Placeholder(placeholderNames[i], null, Placeholder.AUTO_INCREMENT_INDIVIDUALLY);
		}
		return registerPlaceholders(placeholders);
	}

	@Override
	public @NotNull MESSAGE placeholderRegex(@NotNull @Language("RegExp") String placeholder)
	{
		return registerPlaceholderRegex(placeholder, null);
	}

	@Override
	public @NotNull MESSAGE placeholderRegex(@NotNull @Language("RegExp") String placeholder, IPlaceholderProcessor placeholderProcessor)
	{
		return registerPlaceholderRegex(placeholder, placeholderProcessor);
	}

	@Override
	public @NotNull MESSAGE registerPlaceholderRegex(@NotNull @Language("RegExp") String placeholder, @Nullable IPlaceholderProcessor placeholderProcessor)
	{
		return registerPlaceholders(new Placeholder(placeholder, placeholderProcessor, true));
	}

	@Override
	public @NotNull MESSAGE registerPlaceholderRegex(@NotNull @Language("RegExp") String placeholder, IPlaceholderProcessor placeholderProcessor, int parameterIndex)
	{
		return registerPlaceholders(new Placeholder(placeholder, placeholderProcessor, parameterIndex, true));
	}

	protected void quoteArgs(final Object[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			if(args[i] instanceof String)
			{
				args[i] = StringUtils.escapeJsonString((String) args[i]);
			}
		}
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
		if(placeholderHandler != null)
		{
			if(useJson)
				return placeholderHandler.format(args);
			else
				return placeholderHandler.formatLegacy(args);
		}
		return useJson ? json : fallback;
	}
}
