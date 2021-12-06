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

package at.pcgamingfreaks.Bukkit.Message;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.Message.Sender.BossBarMetadata;
import at.pcgamingfreaks.Bukkit.Message.Sender.SendMethod;
import at.pcgamingfreaks.Bukkit.Util.IUtils;
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Message.Sender.IMetadata;
import at.pcgamingfreaks.Message.Sender.TitleMetadata;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Message extends at.pcgamingfreaks.Message.Message<Message, Player, CommandSender, MessageComponent> implements IMessage
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
		setMessageComponentClass(MessageComponent.class, builderConstructor);
	}

	//region Variables
	private static final Pattern RGB_COLOR_DETECTION = Pattern.compile("\"color\"\\s*:\\s*\"#(?<rgb>[\\dA-Fa-f]{6})\"");
	private static final boolean PRE_1_8_MC = MCVersion.isOlderThan(MCVersion.MC_1_8);

	/**
	 * Gets the method used to display this message on the client.
	 */
	@Getter private @NotNull SendMethod sendMethod = PRE_1_8_MC ? SendMethod.CHAT_CLASSIC : SendMethod.CHAT;
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
		super(message);
		if(!MCVersion.supportsRgbColors())
		{
			boolean found = false;
			Matcher matcher = RGB_COLOR_DETECTION.matcher(json);
			StringBuffer sb = new StringBuffer();
			while(matcher.find())
			{
				found = true;
				MessageColor color = MessageColor.getDefaultColor(matcher.group("rgb"));
				matcher.appendReplacement(sb, color.getName());
			}
			if(found)
			{
				matcher.appendTail(sb);
				json = sb.toString();
			}
		}
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
		if(method == null) method = SendMethod.DISABLED;
		else if(!method.isAvailable()) method = method.getFallbackSendMethod();
		this.sendMethod = method;
	}

	/**
	 * Sets the optional metadata object for the message (only works with Title and BossBar send method).
	 *
	 * @param optionalParameters The object containing the optional metadata. Has to be an instance of {@link TitleMetadata} or {@link BossBarMetadata}.
	 */
	@Override
	public void setOptionalParameters(@NotNull IMetadata optionalParameters)
	{
		Validate.isTrue(optionalParameters instanceof TitleMetadata || optionalParameters instanceof BossBarMetadata, "The metadata object needs to be an instance of TitleMetadata or BossBarMetadata");
		super.setOptionalParameters(optionalParameters);
	}

	@Override
	public void setPlaceholderApiEnabled(boolean enabled)
	{
		placeholderApiEnabled = enabled && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
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
		if(getSendMethod() == SendMethod.DISABLED) return;
		Validate.notNull(target, "The target that should receive the message should not be null!");
		if(target instanceof Player && getSendMethod() != SendMethod.CHAT_CLASSIC)
		{
			String jsonMsg = prepareMessage(true, args);
			if(isPlaceholderApiEnabled()) jsonMsg = PlaceholderAPI.setPlaceholders((Player) target, jsonMsg);
			sendMethod.getActiveSender().send((Player) target, jsonMsg, optionalParameters);
		}
		else
		{
			String msg = prepareMessage(false, args);
			if(isPlaceholderApiEnabled() && target instanceof Player) msg = PlaceholderAPI.setPlaceholders((Player) target, msg);
			target.sendMessage(msg);
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
	public void send(@NotNull Collection<? extends Player> targets, @Nullable Object... args)
	{
		if(getSendMethod() == SendMethod.DISABLED || targets.size() == 0) return;
		Validate.notNull(targets, "The targets that should receive the message should not be null!");
		if(getSendMethod() == SendMethod.CHAT_CLASSIC)
		{
			String msg = prepareMessage(false, args);
			for(Player player : targets)
			{
				player.sendMessage((isPlaceholderApiEnabled()) ? PlaceholderAPI.setPlaceholders(player, msg) : msg);
			}
		}
		else
		{
			String jsonMsg = prepareMessage(true, args);
			if(isPlaceholderApiEnabled())
			{
				for(Player player : targets)
				{
					sendMethod.getActiveSender().send(player, PlaceholderAPI.setPlaceholders(player, jsonMsg));
				}
			}
			else
			{
				sendMethod.getActiveSender().send(targets, jsonMsg, optionalParameters);
			}
		}
	}

	/**
	 * Sends the message to all online players on the server, as well as the console.
	 * This function will ignore PlaceholderAPI placeholders even if PlaceholderAPI is enabled!
	 *
	 * @param args An optional array of arguments.
	 *                If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                This can be used to add variable data into the message.
	 */
	@Override
	public void broadcast(@Nullable Object... args)
	{
		if(getSendMethod() == SendMethod.DISABLED) return;
		if(getSendMethod() == SendMethod.CHAT_CLASSIC)
		{
			Bukkit.broadcastMessage(prepareMessage(false, args));
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage(prepareMessage(false, args)); // Send the message to the console
			sendMethod.getActiveSender().broadcast(prepareMessage(true, args), optionalParameters);
		}
	}

	/**
	 * Sends the message to a target.
	 *
	 * @param target The target that should receive the message.
	 * @param playerForPAPI The player that should be used to resolve the PlaceholderAPI placeholders.
	 * @param args   An optional array of arguments.
	 *                  If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                  This can be used to add variable data into the message.
	 */
	public void send(@NotNull CommandSender target, @NotNull OfflinePlayer playerForPAPI, @Nullable Object... args)
	{
		Validate.notNull(target, "The target that should receive the message should not be null!");
		if(getSendMethod() == SendMethod.DISABLED) return;
		if(target instanceof Player && getSendMethod() != SendMethod.CHAT_CLASSIC)
		{
			String jsonMsg = prepareMessage(true, args);
			if(isPlaceholderApiEnabled()) jsonMsg = PlaceholderAPI.setPlaceholders(playerForPAPI, jsonMsg);
			sendMethod.getActiveSender().send((Player) target, jsonMsg, optionalParameters);
		}
		else
		{
			String msg = prepareMessage(false, args);
			if(isPlaceholderApiEnabled()) msg = PlaceholderAPI.setPlaceholders(playerForPAPI, msg);
			target.sendMessage(msg);
		}
	}

	/**
	 * Sends the message to a {@link Collection} of targets.
	 *
	 * @param targets The targets that should receive the message.
	 * @param playerForPAPI The player that should be used to resolve the PlaceholderAPI placeholders.
	 * @param args    An optional array of arguments.
	 *                   If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                   This can be used to add variable data into the message.
	 */
	public void send(@NotNull Collection<? extends Player> targets, OfflinePlayer playerForPAPI, @Nullable Object... args)
	{
		Validate.notNull(targets, "The targets that should receive the message should not be null!");
		if(getSendMethod() == SendMethod.DISABLED || targets.size() == 0) return;
		if(getSendMethod() == SendMethod.CHAT_CLASSIC)
		{
			String msg = prepareMessage(false, args);
			if(isPlaceholderApiEnabled()) msg = PlaceholderAPI.setPlaceholders(playerForPAPI, msg);
			for(Player player : targets)
			{
				player.sendMessage(msg);
			}
		}
		else
		{
			String jsonMsg = prepareMessage(true, args);
			if(isPlaceholderApiEnabled()) jsonMsg = PlaceholderAPI.setPlaceholders(playerForPAPI, jsonMsg);
			sendMethod.getActiveSender().send(targets, jsonMsg, optionalParameters);
		}
	}

	/**
	 * Sends the message to all online players on the server, as well as the console.
	 *
	 * @param playerForPAPI The player that should be used to resolve the PlaceholderAPI placeholders.
	 * @param args An optional array of arguments.
	 *                If this is used they will be passed together with the message itself to the String.format() function, before the message gets send to the client.
	 *                This can be used to add variable data into the message.
	 */
	public void broadcast(@NotNull OfflinePlayer playerForPAPI, @Nullable Object... args)
	{
		if(getSendMethod() == SendMethod.DISABLED) return;
		String msg = prepareMessage(false, args);
		if(isPlaceholderApiEnabled()) msg = PlaceholderAPI.setPlaceholders(playerForPAPI, msg);
		if(getSendMethod() == SendMethod.CHAT_CLASSIC)
		{
			Bukkit.broadcastMessage(msg);
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage(msg); // Send the message to the console
			String jsonMsg = prepareMessage(true, args);
			if(isPlaceholderApiEnabled()) jsonMsg = PlaceholderAPI.setPlaceholders(playerForPAPI, jsonMsg);
			sendMethod.broadcast(jsonMsg, optionalParameters);
		}
	}

	/**
	 * Fills the placeholders in the message and returns it.
	 * The message will be returned as a legacy formatted message.
	 *
	 * @param args The arguments that should be used to fill the placeholders
	 * @return The message with the filled placeholders
	 */
	public @NotNull String prepareChatLegacy(@Nullable Object... args)
	{
		return prepareMessage(false, args);
	}

	/**
	 * Fills the placeholders in the message and returns it.
	 * The message will be returned as an NMS ChatComponent that can be used with most NMS functions.
	 *
	 * @param args The arguments that should be used to fill the placeholders
	 * @return The message with the filled placeholders
	 */
	public @NotNull Object prepareChatComponent(@Nullable Object... args)
	{
		String json = prepareMessage(true, args);
		return IUtils.INSTANCE.jsonToIChatComponent(json);
	}
}
