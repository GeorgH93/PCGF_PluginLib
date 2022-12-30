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

package at.pcgamingfreaks.Config;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Message.Message;
import at.pcgamingfreaks.Message.Sender.IMetadata;
import at.pcgamingfreaks.Message.Sender.ISendMethod;
import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.ServerType;
import at.pcgamingfreaks.Version;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.logging.Level;

/**
 * This class is not recommended being used directly! Use the Bukkit or Bungee specific Language classes instead!
 */
public class LanguageWithMessageGetter<MESSAGE extends Message<? extends MESSAGE,?,?>> extends Language
{
	private final static MessageClassesReflectionDataHolder AUTO_DETECTED_MESSAGE_CLASSES;

	static
	{
		MessageClassesReflectionDataHolder data = null;

		// Attempt auto resolution
		try
		{
			Class<?> messageClass = null;
			Class<?> sendMethodClass = null;
			if (ServerType.isBukkitCompatible())
			{
				messageClass = Class.forName("at.pcgamingfreaks.Bukkit.Message.Message");
				sendMethodClass = Class.forName("at.pcgamingfreaks.Bukkit.Message.Sender.SendMethod");
			}
			else if (ServerType.isBungeeCordCompatible())
			{
				messageClass = Class.forName("at.pcgamingfreaks.Bungee.Message.Message");
				sendMethodClass = Class.forName("at.pcgamingfreaks.Bungee.Message.Sender.SendMethod");
			}
			if (messageClass != null && sendMethodClass != null)
			{
				Constructor<?> messageConstructor = Reflection.getConstructor(messageClass, String.class);
				Method setSendMethodMethod = Reflection.getMethod(messageClass, "setSendMethod", sendMethodClass);

				//noinspection unchecked
				data = new MessageClassesReflectionDataHolder(messageConstructor, setSendMethodMethod, (Class<? extends ISendMethod>)sendMethodClass);
			}
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		AUTO_DETECTED_MESSAGE_CLASSES = data;
	}

	protected MessageClassesReflectionDataHolder getMessageClasses()
	{
		if (AUTO_DETECTED_MESSAGE_CLASSES != null) return AUTO_DETECTED_MESSAGE_CLASSES;
		throw new MessageClassesReflectionDataNotSetException();
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

	public @NotNull MESSAGE getMessage(final @NotNull String path) throws MessageClassesReflectionDataNotSetException
	{
		final MessageClassesReflectionDataHolder messageClasses = getMessageClasses();
		MESSAGE msg = null;
		try
		{
			final String msgString = getTranslated(path);
			//noinspection unchecked
			msg = (MESSAGE) messageClasses.messageConstructor.newInstance(msgString);
			if(msgString.isEmpty())
			{
				messageClasses.setSendMethod.invoke(msg, Enum.valueOf(messageClasses.enumType, "DISABLED"));
				return msg;
			}
			handleSendMethodAndParameters(msg, path);
		}
		catch(Exception e)
		{
			if(msg == null) logger.log(Level.SEVERE, e, () -> ConsoleColor.RED + "Failed to load message: " + KEY_LANGUAGE + path + " " + ConsoleColor.RESET);
			else logger.log(Level.WARNING, e, () -> ConsoleColor.RED + "Failed generate metadata for: " + KEY_LANGUAGE + path + " " + ConsoleColor.RESET);
		}
		return msg;
	}

	private void handleSendMethodAndParameters(final @NotNull MESSAGE msg, final @NotNull String path) throws InvocationTargetException, IllegalAccessException, YamlKeyNotFoundException
	{
		final String pathSendMethod = KEY_LANGUAGE + path + KEY_ADDITION_SEND_METHOD, pathParameter = KEY_LANGUAGE + path + KEY_ADDITION_PARAMETERS;
		if(yaml.isSet(pathSendMethod))
		{
			final MessageClassesReflectionDataHolder messageClasses = getMessageClasses();
			final String sendMethodName = yaml.getString(pathSendMethod, "CHAT").toUpperCase(Locale.ROOT);
			Object sendMethod = null;
			try
			{
				//noinspection unchecked
				sendMethod = Enum.valueOf(messageClasses.enumType, sendMethodName);
			}
			catch(IllegalArgumentException ignored)
			{
				logger.warning(ConsoleColor.RED + "Unknown send method '" + sendMethodName + "' for message " + KEY_LANGUAGE + path + ConsoleColor.RESET);
			}
			if(sendMethod instanceof ISendMethod)
			{
				messageClasses.setSendMethod.invoke(msg, sendMethod);
				if(yaml.isSet(pathParameter))
				{
					IMetadata meta = ((ISendMethod) sendMethod).parseMetadata(yaml.getString(pathParameter));
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

	//region helper class
	/**
	 * Ignore this class, it's just a helper class for some internal stuff
	 */
	protected static class MessageClassesReflectionDataHolder
	{
		public MessageClassesReflectionDataHolder(Constructor<?> messageConstructor, Method setSendMethod, Class<? extends ISendMethod> enumType)
		{
			this.enumType = enumType;
			this.setSendMethod = setSendMethod;
			this.messageConstructor = messageConstructor;
		}

		public Class enumType;
		public Method setSendMethod;
		public Constructor<?> messageConstructor;
	}

	public static class MessageClassesReflectionDataNotSetException extends IllegalStateException
	{
		public MessageClassesReflectionDataNotSetException()
		{
			super("Message reflection data object not set!");
		}
	}
	//endregion
}