/*
 *   Copyright (C) 2016 GeorgH93
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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.ConsoleColor;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collection of static functions that are may be useful for plugins.
 */
public class Utils
{
	private static final Class<?> CRAFT_ITEM_STACK_CLASS = Reflection.getOBCClass("inventory.CraftItemStack");
	private static final Class<?> NMS_ITEM_STACK_CLASS = Reflection.getNMSClass("ItemStack");
	private static final Class<?> NBT_TAG_COMPOUND_CLASS = Reflection.getNMSClass("NBTTagCompound");
	private static final Method AS_NMS_COPY_METHOD = Reflection.getMethod(CRAFT_ITEM_STACK_CLASS, "asNMSCopy", ItemStack.class);
	private static final Method SAVE_NMS_ITEM_STACK_METHOD = Reflection.getMethod(NMS_ITEM_STACK_CLASS, "save", NBT_TAG_COMPOUND_CLASS);

	/**
	 * Converts an item stack into a json string used for chat messages.
	 *
	 * @param itemStack The item stack that should be converted into a json string
	 * @param logger The logger that should display the error message in case of an problem
	 * @return The item stack as a json string. empty string if the conversation failed
	 */
	public static String convertItemStackToJson(ItemStack itemStack, Logger logger)
	{
		try
		{
			if(SAVE_NMS_ITEM_STACK_METHOD == null || AS_NMS_COPY_METHOD == null || NBT_TAG_COMPOUND_CLASS == null)
			{
				logger.log(Level.SEVERE, "Failed to serialize item stack to NMS item! Bukkit Version: " + Bukkit.getServer().getVersion() +
						"\nIt one or more of the reflection variables is null! Looks like your bukkit version is not compatible. Please check for updates.");
			}
			else
			{
				return SAVE_NMS_ITEM_STACK_METHOD.invoke(AS_NMS_COPY_METHOD.invoke(null, itemStack), NBT_TAG_COMPOUND_CLASS.newInstance()).toString();
			}
		}
		catch (Throwable t)
		{
			logger.log(Level.SEVERE, "Failed to serialize item stack to NMS item! Bukkit Version: " + Bukkit.getServer().getVersion() + "\n", t);
		}
		return "";
	}

	/**
	 * Checks if per world plugins is installed. Used to check
	 *
	 * @return true if PerWorldPlugins is installed
	 */
	public static boolean isPerWorldPluginsInstalled()
	{
		return Bukkit.getServer().getPluginManager().getPlugin("PerWorldPlugins") != null;
	}

	/**
	 * Shows a warning message if per world plugins is installed and blocks the executing thread for 5 seconds.
	 *
	 * @param logger The logger to output the warning
	 */
	public static void warnIfPerWorldPluginsIsInstalled(Logger logger)
	{
		warnIfPerWorldPluginsIsInstalled(logger, 5);
	}

	/**
	 * Shows a warning message if per world plugins is installed and blocks the executing thread for a given time.
	 *
	 * @param logger The logger to output the warning
	 * @param pauseTime The time in seconds the function should be blocking if PerWorldPlugins is installed.
	 */
	public static void warnIfPerWorldPluginsIsInstalled(Logger logger, int pauseTime)
	{
		if(isPerWorldPluginsInstalled())
		{
			logger.warning(ConsoleColor.RED    + "   !!!!!!!!!!!!!!!!!!!!!!!!!" + ConsoleColor.RESET);
			logger.warning(ConsoleColor.RED    + "   !!!!!! - WARNING - !!!!!!" + ConsoleColor.RESET);
			logger.warning(ConsoleColor.RED    + "   !!!!!!!!!!!!!!!!!!!!!!!!!" + ConsoleColor.RESET + "\n");
			logger.warning(ConsoleColor.RED    + " We have detected that you are using \"PerWorldPlugins\"!" + ConsoleColor.RESET);
			logger.warning(ConsoleColor.YELLOW + " Please allow this plugin to run in " + ConsoleColor.BLUE + " ALL " + ConsoleColor.YELLOW + " worlds." + ConsoleColor.RESET);
			logger.warning(ConsoleColor.YELLOW + " If you block it from running in all worlds there probably will be problems!" + ConsoleColor.RESET);
			logger.warning(ConsoleColor.YELLOW + " If you don't want you players to use this plugin in certain worlds please use permissions and the plugins config!" + ConsoleColor.RESET);
			logger.warning(ConsoleColor.RED    + " There will be no support for bugs caused by \"PerWorldPlugins\"!" + ConsoleColor.RESET);
			logger.warning(ConsoleColor.YELLOW + " Waiting " + pauseTime + " seconds till loading will resume!" + ConsoleColor.RESET);

			if(pauseTime > 0) // If there is a valid time we pause the server startup for some seconds to give the admins the chance to read the message
			{
				try
				{
					Thread.sleep(pauseTime * 1000L);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Calculates the distance between two players
	 *
	 * @param p1 The first player
	 * @param p2 The second player
	 * @return The distance between the two players in meter/blocks. Double.POSITIVE_INFINITY if they aren't in the same world.
	 */
	public static double getDistance(Player p1, Player p2)
	{
		if(p1.equals(p2))
		{
			return 0;
		}
		if(p1.getWorld().equals(p2.getWorld()))
		{
			return p1.getLocation().distance(p2.getLocation());
		}
		return Double.POSITIVE_INFINITY;
	}


	//region Reflection constants for the send packet method
	private final static Class<?> ENTITY_PLAYER = Reflection.getNMSClass("EntityPlayer");
	private final static Method SEND_PACKET = Reflection.getMethod(Reflection.getNMSClass("PlayerConnection"), "sendPacket");
	private final static Field PLAYER_CONNECTION = Reflection.getField(ENTITY_PLAYER, "playerConnection");
	//endregion

	/**
	 * Sends a nms packet to the client
	 *
	 * @param player The player that should receive the packet
	 * @param packet The packet that should be sent to the client
	 */
	public static void sendPacket(@NotNull Player player, @NotNull Object packet) throws IllegalAccessException, InvocationTargetException
	{
		if(SEND_PACKET == null || PLAYER_CONNECTION == null) return;
		Object handle = Reflection.getHandle(player);
		if(handle != null && handle.getClass() == ENTITY_PLAYER) // If it's not a real player we can't send him the packet
		{
			SEND_PACKET.invoke(PLAYER_CONNECTION.get(handle), packet);
		}
	}

	/**
	 * Limits the length of a given string to a given amount of characters.
	 *
	 * @param text      The text that should be limited in it's length.
	 * @param maxLength The max amount of characters the text should be limited to.
	 * @return The text in it's limited length.
	 */
	public static String limitLength(@NotNull String text, int maxLength)
	{
		Validate.notNull(text, "The text can't be null.");
		Validate.isTrue(maxLength >= 0, "The max length can't be negative!");
		if(text.length() == 0 || maxLength == 0) return "";
		if(text.length() <= maxLength) return text; // No need to create a new object if the string has not changed
		return text.substring(0, Math.min(maxLength, text.length()));
	}
}