/*
 * Copyright (C) 2016 GeorgH93
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bungee.Message;

import at.pcgamingfreaks.Bungee.Message.Sender.BossBarMetadata;
import at.pcgamingfreaks.Bungee.Message.Sender.TitleMetadata;
import at.pcgamingfreaks.Bungee.Message.Sender.SendMethod;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public final class Message extends at.pcgamingfreaks.Message.Message<Message, ProxiedPlayer, CommandSender>
{
	//region Variables
	private SendMethod method = SendMethod.CHAT;
	//endregion

	//region Constructors
	/**
	 * Creates a new Message instance from a string which can be a JSON or just a simple text.
	 * The messages send method will be the players chat.
	 *
	 * @param message The text represented by the message object. Can be a normal string or a JSON.
	 */
	public Message(@NotNull String message)
	{
		super(message, MessageComponent.class);
	}

	/**
	 * Creates a new Message instance from a string which can be a JSON or just a simple text.
	 *
	 * @param message The text represented by the message object. Can be a normal string or a JSON.
	 * @param method  The method used to display the message on the players client.
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
	 * @param method  The method used to display the message on the players client.
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
	 * @param method  The method used to display the message on the players client.
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
	 * @param method         The method used to display the message on the players client.
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
		this.method = (method == null) ? SendMethod.DISABLED : method;
	}

	/**
	 * Gets the method used to display this message on the client.
	 *
	 * @return The send/display method for this message.
	 */
	public @NotNull SendMethod getSendMethod()
	{
		return method;
	}

	/**
	 * Sets the optional metadata object for the message (only works with Title and BossBar send method).
	 *
	 * @param optionalParameters The object containing the optional metadata. Has to be an instance of {@link TitleMetadata} or {@link BossBarMetadata}.
	 */
	@Override
	public void setOptionalParameters(@NotNull Object optionalParameters)
	{
		Validate.isTrue(optionalParameters instanceof TitleMetadata || optionalParameters instanceof BossBarMetadata, "The metadata object needs to be an instance of TitleMetadata or BossBarMetadata");
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
	public void send(@NotNull CommandSender target, @Nullable Object... args)
	{
		Validate.notNull(target, "The target that should receive the message should not be null!");
		if(getSendMethod() == SendMethod.DISABLED) return;
		if(target instanceof ProxiedPlayer)
		{
			method.getSender().doSend((ProxiedPlayer) target, (args != null && args.length > 0) ? String.format(json, quoteArgs(args)) : json, optionalParameters);
		}
		else
		{
			//noinspection deprecation
			target.sendMessage((args != null && args.length > 0) ? String.format(fallback, args) : fallback);
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
	public void send(@NotNull Collection<? extends ProxiedPlayer> targets, @Nullable Object... args)
	{
		Validate.notNull(targets, "The targets that should receive the message should not be null!");
		if(getSendMethod() == SendMethod.DISABLED || targets.size() == 0) return;
		method.getSender().doSend(targets, (args != null && args.length > 0) ? String.format(json, quoteArgs(args)) : json, optionalParameters);
	}

	/**
	 * Sends the message to all online players on the server, as well as the console.
	 *
	 * @param args    An optional array of arguments.
	 *                   If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                   This can be used to add variable data into the message.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void broadcast(@Nullable Object... args)
	{
		if(getSendMethod() == SendMethod.DISABLED) return;
		ProxyServer.getInstance().getConsole().sendMessage((args != null && args.length > 0) ? String.format(fallback, args) : fallback); // Send the message to the console
		method.getSender().doBroadcast((args != null && args.length > 0) ? String.format(json, quoteArgs(args)) : json, optionalParameters);
	}
}