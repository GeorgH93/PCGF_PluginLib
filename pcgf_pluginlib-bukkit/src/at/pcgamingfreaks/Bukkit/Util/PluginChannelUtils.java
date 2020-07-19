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

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.PlatformResolver;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PluginChannelUtils
{
	private static final IPluginChannelUtils INSTANCE = PlatformResolver.createPlatformInstance(IPluginChannelUtils.class);

	/**
	 * Registers an outgoing plugin channel, without enforcing the minecraft naming conventions added with MC 1.13.
	 * This should allow to use legacy plugin channels that might be used by some older mods.
	 * This will call the Bukkit method for MC version older than 1.13.
	 *
	 * @param plugin The plugin that registers the channel.
	 * @param channel The channel to be registered. This will not be checked! make sure that it is allowed with the minecraft version you are using
	 */
	public static void registerOutgoingChannelUnchecked(final @NotNull Plugin plugin, final @NotNull String channel)
	{
		INSTANCE.registerOutgoingChannelUnchecked(plugin, channel);
	}

	/**
	 * Unregisters an outgoing plugin channel, without enforcing the minecraft naming conventions added with MC 1.13.
	 * This should allow to use legacy plugin channels that might be used by some older mods.
	 * This will call the Bukkit method for MC version older than 1.13.
	 *
	 * @param plugin The plugin that unregisters the channel.
	 * @param channel The channel to be unregistered. The channel must have been registered with the {@link PluginChannelUtils#registerOutgoingChannelUnchecked(Plugin, String)} method.
	 */
	public static void unregisterOutgoingChannelUnchecked(final @NotNull Plugin plugin, final @NotNull String channel)
	{
		INSTANCE.unregisterOutgoingChannelUnchecked(plugin, channel);
	}

	/**
	 * Sends a plugin channel message, without enforcing the minecraft naming conventions added with MC 1.13.
	 * This should allow to use legacy plugin channels that might be used by some older mods.
	 * This will call the Bukkit method for MC version older than 1.13.
	 *
	 * @param plugin The plugin that is sending the plugin channel message.
	 * @param player The player that should receiver the plugin channel message.
	 * @param channel The channel that should be used for the plugin message. The channel must have been registered with the {@link PluginChannelUtils#registerOutgoingChannelUnchecked(Plugin, String)} method.
	 * @param message The plugin channel message that should be sent.
	 */
	public static void sendPluginMessageUnchecked(final @NotNull Plugin plugin, final @NotNull Player player, final @NotNull String channel, final @NotNull byte[] message)
	{
		INSTANCE.sendPluginMessageUnchecked(plugin, player, channel, message);
	}

	/**
	 * Builds a plugin channel message from a string array.
	 * It will write each string on it's own to a ByteArrayOutputStream and then get the byte array from the output Stream.
	 *
	 * @param msg The string array to process.
	 * @return The byte array containing the plugin message.
	 */
	public static byte[] buildStringArrayMessage(final String... msg)
	{
		try(ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream))
		{
			for(String param : msg)
			{
				out.writeUTF(param);
			}
			out.flush();
			return stream.toByteArray();
		}
		catch(IOException ignored) {}
		return null;
	}
}