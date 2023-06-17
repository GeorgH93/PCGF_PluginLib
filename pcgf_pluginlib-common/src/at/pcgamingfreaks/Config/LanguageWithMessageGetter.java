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

package at.pcgamingfreaks.Config;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Message.Message;
import at.pcgamingfreaks.Message.Sender.IMetadata;
import at.pcgamingfreaks.Message.Sender.ISendMethod;
import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.logging.Level;

/**
 * This class is not recommended being used directly! Use the Bukkit or Bungee specific Language classes instead!
 */
public class LanguageWithMessageGetter<MESSAGE extends Message<? extends MESSAGE,?,?>> extends Language
{
	private final IMessageFactory<MESSAGE> reflectionMessageFactory = new ReflectionMessageFactory<>();

	private boolean useJavaEditionFormatting = true;

	protected IMessageFactory<MESSAGE> getMessageFactory()
	{
		return reflectionMessageFactory;
	}

	/**
	 * @param plugin  the plugin instance
	 * @param version the current version of the language file
	 */
	public LanguageWithMessageGetter(@NotNull IPlugin plugin, Version version)
	{
		super(plugin, version);
	}

	/**
	 * @param plugin  The plugin instance
	 * @param version The current version of the language file
	 * @param path    The sub-folder for the language file
	 * @param prefix  The prefix for the language file
	 */
	public LanguageWithMessageGetter(@NotNull IPlugin plugin, Version version, @Nullable String path, @NotNull String prefix)
	{
		super(plugin, version, path, prefix);
	}

	/**
	 * @param plugin      The plugin instance
	 * @param version     The current version of the language file
	 * @param path        The sub-folder for the language file
	 * @param prefix      The prefix for the language file
	 * @param inJarPrefix The prefix for the language file within the jar (e.g.: bungee_)
	 */
	public LanguageWithMessageGetter(@NotNull IPlugin plugin, Version version, @Nullable String path, @NotNull String prefix, @NotNull String inJarPrefix)
	{
		super(plugin, version, path, prefix, inJarPrefix);
	}

	@Override
	protected void loaded()
	{
		super.loaded();
		useJavaEditionFormatting = yaml.getBoolean("JavaEditionLegacyFormatting", !yaml.getBoolean("BedrockEditionLegacyFormatting", false));
	}

	public @NotNull MESSAGE getMessage(final @NotNull String path) throws IllegalStateException
	{
		MESSAGE msg = null;
		try
		{
			final IMessageFactory<MESSAGE> messageFactory = getMessageFactory();
			final String msgString = getTranslated(path);
			msg = messageFactory.produceMessage(msgString, useJavaEditionFormatting);
			if(msgString.isEmpty())
			{
				messageFactory.setSendMethod(msg, "DISABLED");
			}
			else
			{
				handleSendMethodAndParameters(msg, path);
			}
		}
		catch(Exception e)
		{
			if(msg == null) logger.log(Level.SEVERE, e, () -> ConsoleColor.RED + "Failed to load message: " + KEY_LANGUAGE + path + " " + ConsoleColor.RESET);
			else logger.log(Level.WARNING, e, () -> ConsoleColor.RED + "Failed generate metadata for: " + KEY_LANGUAGE + path + " " + ConsoleColor.RESET);
		}
		return msg;
	}

	private void handleSendMethodAndParameters(final @NotNull MESSAGE msg, final @NotNull String path) throws Exception
	{
		final String pathSendMethod = KEY_LANGUAGE + path + KEY_ADDITION_SEND_METHOD, pathParameter = KEY_LANGUAGE + path + KEY_ADDITION_PARAMETERS;
		if(yaml.isSet(pathSendMethod))
		{
			final IMessageFactory<MESSAGE> messageFactory = getMessageFactory();
			final String sendMethodName = yaml.getString(pathSendMethod, "CHAT").toUpperCase(Locale.ROOT);
			ISendMethod sendMethod = null;
			try
			{
				sendMethod = messageFactory.getSendMethod(sendMethodName);
			}
			catch(IllegalArgumentException ignored)
			{
				logger.warning(ConsoleColor.RED + "Unknown send method '" + sendMethodName + "' for message " + KEY_LANGUAGE + path + ConsoleColor.RESET);
			}
			if(sendMethod != null)
			{
				messageFactory.setSendMethod(msg, sendMethod);
				if(yaml.isSet(pathParameter))
				{
					IMetadata meta = sendMethod.parseMetadata(yaml.getString(pathParameter));
					if(meta != null) msg.setOptionalParameters(meta);
				}
			}
		}
		if(yaml.getBoolean(KEY_LANGUAGE + path + KEY_ADDITION_PAPI, false))
		{
			try
			{
				msg.setPlaceholderApiEnabled(true);
			}
			catch(UnsupportedOperationException e)
			{
				logger.warning(ConsoleColor.RED + e.getMessage() + ConsoleColor.RESET);
			}
		}
	}
}