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

import at.pcgamingfreaks.Message.Message;
import at.pcgamingfreaks.Message.Sender.ISendMethod;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.ServerType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
class ReflectionMessageFactory<MESSAGE extends Message<? extends MESSAGE,?,?>> implements IMessageFactory<MESSAGE>
{
	private static final MessageClassesReflectionDataHolder AUTO_DETECTED_MESSAGE_CLASSES;

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
			//noinspection ConstantValue
			if (messageClass != null && sendMethodClass != null)
			{
				Constructor<?> messageConstructor = Reflection.getConstructor(messageClass, String.class, boolean.class);
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

	private static MessageClassesReflectionDataHolder getMessageClasses()
	{
		if (AUTO_DETECTED_MESSAGE_CLASSES != null) return AUTO_DETECTED_MESSAGE_CLASSES;
		throw new MessageClassesReflectionDataNotSetException();
	}

	@Override
	public MESSAGE produceMessage(String message, boolean useJavaEditionStyleLegacyFormatting) throws InvocationTargetException, InstantiationException, IllegalAccessException
	{
		return (MESSAGE) getMessageClasses().messageConstructor.newInstance(message, useJavaEditionStyleLegacyFormatting);
	}

	@Override
	public ISendMethod getSendMethod(String sendMethodName)
	{
		return (ISendMethod) Enum.valueOf(getMessageClasses().sendMethodClass, sendMethodName);
	}

	@Override
	public void setSendMethod(MESSAGE message, String sendMethodName) throws InvocationTargetException, IllegalAccessException
	{
		setSendMethod(message, getSendMethod(sendMethodName));
	}

	@Override
	public void setSendMethod(MESSAGE message, ISendMethod sendMethod) throws InvocationTargetException, IllegalAccessException
	{
		getMessageClasses().setSendMethod.invoke(message, sendMethod);
	}


	//region helper class
	/**
	 * Ignore this class, it's just a helper class for some internal stuff
	 */
	protected static class MessageClassesReflectionDataHolder
	{
		public MessageClassesReflectionDataHolder(Constructor<?> messageConstructor, Method setSendMethod, Class<? extends ISendMethod> sendMethodClass)
		{
			this.sendMethodClass = sendMethodClass;
			this.setSendMethod = setSendMethod;
			this.messageConstructor = messageConstructor;
		}

		@SuppressWarnings("rawtypes")
		public Class sendMethodClass;
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