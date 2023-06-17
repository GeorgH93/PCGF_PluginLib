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

package at.pcgamingfreaks.Bungee.Message;

import at.pcgamingfreaks.Bungee.Message.Sender.BossBarMetadata;
import at.pcgamingfreaks.Bungee.Message.Sender.SendMethod;
import at.pcgamingfreaks.Bungee.Message.Sender.TitleMetadata;
import at.pcgamingfreaks.Message.MessageComponent;
import at.pcgamingfreaks.Message.Sender.IMetadata;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

public final class Message extends at.pcgamingfreaks.Message.Message<Message, ProxiedPlayer, CommandSender> implements IMessage
{
	static
	{
		Constructor<MessageBuilder> builderConstructor = null;
		try
		{
			builderConstructor = MessageBuilder.class.getConstructor();
		}
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		setMessageComponentClass(builderConstructor);
	}

	//region Variables
	/**
	 * Gets the method used to display this message on the client.
	 */
	@Getter @NotNull private SendMethod sendMethod = SendMethod.CHAT;
	//endregion

	//region Constructors
	/**
	 * Creates a new Message instance from a string which can be a JSON or just a simple text.
	 * The messages send method will be the players chat.
	 *
	 * @param message The text represented by the message object. Can be a normal string or a JSON.
	 */
	public Message(final @NotNull String message)
	{
		super(message);
	}
	/**
	 * Creates a new Message instance from a string which can be a JSON or just a simple text.
	 * The messages send method will be the players chat.
	 *
	 * @param message The text represented by the message object. Can be a normal string or a JSON.
	 * @param useJavaEditionFormatting If set to false it will use Bedrock Edition style formatting (format codes persist color changes) when loading legacy messages
	 */
	public Message(@NotNull String message, final boolean useJavaEditionFormatting)
	{
		super(message, useJavaEditionFormatting);
	}

	/**
	 * Creates a new Message instance from a string which can be a JSON or just a simple text.
	 *
	 * @param message The text represented by the message object. Can be a normal string or a JSON.
	 * @param method  The method used to display the message on the player's client.
	 */
	public Message(@NotNull String message, @NotNull SendMethod method)
	{
		this(message);
		setSendMethod(method);
	}

	/**
	 * Creates a new Message instance from an array of {@link MessageComponent}'s.
	 *
	 * @param message The message represented by an array of {@link MessageComponent}'s.
	 */
	public Message(@NotNull MessageComponent[] message)
	{
		this(Arrays.asList(message));
	}

	/**
	 * Creates a new Message instance from an array of {@link MessageComponent}'s.
	 *
	 * @param message The message represented by an array of {@link MessageComponent}'s.
	 * @param method  The method used to display the message on the player's client.
	 */
	public Message(@NotNull MessageComponent[] message, @NotNull SendMethod method)
	{
		this(message);
		setSendMethod(method);
	}

	/**
	 * Creates a new Message instance from a {@link Collection} of {@link MessageComponent}'s.
	 *
	 * @param message The message represented by a {@link Collection} of {@link MessageComponent}'s.
	 */
	public Message(@NotNull Collection<? extends MessageComponent> message)
	{
		super(message);
	}

	/**
	 * Creates a new Message instance from a collection of {@link MessageComponent}'s.
	 *
	 * @param message The message represented by a collection of {@link MessageComponent}'s.
	 * @param method  The method used to display the message on the player's client.
	 */
	public Message(@NotNull Collection<MessageComponent> message, @NotNull SendMethod method)
	{
		this(message);
		setSendMethod(method);
	}

	/**
	 * Creates a new Message instance from a {@link MessageBuilder}.
	 *
	 * @param messageBuilder The {@link MessageBuilder} used to build the message.
	 */
	public Message(@NotNull MessageBuilder messageBuilder)
	{
		this(messageBuilder.getJsonMessageAsList());
	}

	/**
	 * Creates a new Message instance from a {@link MessageBuilder}.
	 *
	 * @param messageBuilder The {@link MessageBuilder} used to build the message.
	 * @param method         The method used to display the message on the player's client.
	 */
	public Message(@NotNull MessageBuilder messageBuilder, @NotNull SendMethod method)
	{
		this(messageBuilder.getJsonMessageAsList(), method);
	}
	//endregion

	/**
	 * Changes the method used to display this message on the client.
	 *
	 * @param method The new send/display method for this message. Null is the same as {@link SendMethod}.{@code DISABLED}
	 */
	public void setSendMethod(@Nullable SendMethod method)
	{
		this.sendMethod = (method == null) ? SendMethod.DISABLED : method;
	}

	/**
	 * Sets the optional metadata object for the message (only works with Title and BossBar send method).
	 *
	 * @param optionalParameters The object containing the optional metadata. Has to be an instance of {@link TitleMetadata} or {@link BossBarMetadata}.
	 */
	@Override
	public void setOptionalParameters(final @NotNull IMetadata optionalParameters)
	{
		if(!(optionalParameters instanceof TitleMetadata || optionalParameters instanceof BossBarMetadata)) throw new IllegalArgumentException("The metadata object needs to be an instance of TitleMetadata or BossBarMetadata");
		super.setOptionalParameters(optionalParameters);
	}

	/**
	 * Sends the message to a target.
	 *
	 * @param target The target that should receive the message.
	 * @param args   An optional array of arguments.
	 *                  If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                  This can be used to add variable data into the message.
	 */
	@Override
	public void send(final @NotNull CommandSender target, final @Nullable Object... args)
	{
		if(getSendMethod() == SendMethod.DISABLED) return;
		if(target instanceof ProxiedPlayer)
		{
			sendMethod.getSender().send((ProxiedPlayer) target, prepareMessage(true, args), optionalParameters);
		}
		else
		{
			//noinspection deprecation
			target.sendMessage(prepareMessage(false, args));
		}
	}

	/**
	 * Sends the message to a {@link Collection} of targets.
	 *
	 * @param targets The targets that should receive the message.
	 * @param args    An optional array of arguments.
	 *                   If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                   This can be used to add variable data into the message.
	 */
	@Override
	public void send(final @NotNull Collection<? extends ProxiedPlayer> targets, final @Nullable Object... args)
	{
		if(getSendMethod() == SendMethod.DISABLED || targets.isEmpty()) return;
		sendMethod.getSender().send(targets, prepareMessage(true, args), optionalParameters);
	}

	/**
	 * Sends the message to all online players on the server, as well as the console.
	 *
	 * @param args    An optional array of arguments.
	 *                   If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                   This can be used to add variable data into the message.
	 */
	@Override
	public void broadcast(@Nullable Object... args)
	{
		if(getSendMethod() == SendMethod.DISABLED) return;
		//noinspection deprecation
		ProxyServer.getInstance().getConsole().sendMessage(prepareMessage(false, args)); // Send the message to the console
		sendMethod.getSender().broadcast(prepareMessage(true, args), optionalParameters);
	}
}