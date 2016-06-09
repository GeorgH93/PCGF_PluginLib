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

import org.apache.commons.lang3.Validate;
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
public class Utils extends at.pcgamingfreaks.Utils
{
	private static final Class<?> CRAFT_ITEM_STACK_CLASS = NMSReflection.getOBCClass("inventory.CraftItemStack");
	private static final Class<?> NMS_ITEM_STACK_CLASS = NMSReflection.getNMSClass("ItemStack");
	private static final Class<?> NBT_TAG_COMPOUND_CLASS = NMSReflection.getNMSClass("NBTTagCompound");
	private static final Method AS_NMS_COPY_METHOD = NMSReflection.getMethod(CRAFT_ITEM_STACK_CLASS, "asNMSCopy", ItemStack.class);
	private static final Method SAVE_NMS_ITEM_STACK_METHOD = NMSReflection.getMethod(NMS_ITEM_STACK_CLASS, "save", NBT_TAG_COMPOUND_CLASS);

	/**
	 * Converts an item stack into a json string used for chat messages.
	 *
	 * @param itemStack The item stack that should be converted into a json string
	 * @param logger The logger that should display the error message in case of an problem
	 * @return The item stack as a json string. empty string if the conversation failed
	 */
	public static String convertItemStackToJson(@NotNull ItemStack itemStack, @NotNull Logger logger)
	{
		Validate.notNull(logger, "The logger can't be null.");
		Validate.notNull(itemStack, "The item stack can't be null.");
		try
		{
			if(SAVE_NMS_ITEM_STACK_METHOD == null || AS_NMS_COPY_METHOD == null || NBT_TAG_COMPOUND_CLASS == null)
			{
				logger.log(Level.SEVERE, "Failed to serialize item stack to NMS item! Bukkit Version: " + Bukkit.getServer().getVersion() +
						"\nOne or more of the reflection variables is null! Looks like your bukkit version is not compatible. Please check for updates.");
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
	public static void warnIfPerWorldPluginsIsInstalled(@NotNull Logger logger)
	{
		warnIfPerWorldPluginsIsInstalled(logger, 5);
	}

	/**
	 * Shows a warning message if per world plugins is installed and blocks the executing thread for a given time.
	 *
	 * @param logger The logger to output the warning
	 * @param pauseTime The time in seconds the function should be blocking if PerWorldPlugins is installed.
	 */
	public static void warnIfPerWorldPluginsIsInstalled(@NotNull Logger logger, int pauseTime)
	{
		Validate.notNull(logger, "The logger can't be null.");
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
			blockThread(pauseTime);
		}
	}

	/**
	 * Calculates the distance between two players
	 *
	 * @param player1 The first player
	 * @param player2 The second player
	 * @return The distance between the two players in meter/blocks. Double.POSITIVE_INFINITY if they aren't in the same world.
	 */
	public static double getDistance(@NotNull Player player1, @NotNull Player player2)
	{
		Validate.notNull(player1, "None of the players can be null!");
		Validate.notNull(player2, "None of the players can be null!");
		if(player1.equals(player2))
		{
			return 0;
		}
		if(player1.getWorld().equals(player2.getWorld()))
		{
			return player1.getLocation().distance(player2.getLocation());
		}
		return Double.POSITIVE_INFINITY;
	}


	//region Reflection constants for the send packet method
	private final static Class<?> ENTITY_PLAYER = NMSReflection.getNMSClass("EntityPlayer");
	private final static Method SEND_PACKET = NMSReflection.getMethod(NMSReflection.getNMSClass("PlayerConnection"), "sendPacket");
	private final static Field PLAYER_CONNECTION = NMSReflection.getField(ENTITY_PLAYER, "playerConnection");
	//endregion

	/**
	 * Sends a nms packet to the client
	 *
	 * @param player The player that should receive the packet
	 * @param packet The packet that should be sent to the client
	 */
	public static void sendPacket(@NotNull Player player, @NotNull Object packet) throws IllegalAccessException, InvocationTargetException
	{
		Validate.notNull(player, "The player that should receive this packet can't be null!");
		Validate.notNull(packet, "The packet to send can't be null!");
		if(SEND_PACKET == null || PLAYER_CONNECTION == null) return;
		Object handle = NMSReflection.getHandle(player);
		if(handle != null && handle.getClass() == ENTITY_PLAYER) // If it's not a real player we can't send him the packet
		{
			SEND_PACKET.invoke(PLAYER_CONNECTION.get(handle), packet);
		}
	}
}