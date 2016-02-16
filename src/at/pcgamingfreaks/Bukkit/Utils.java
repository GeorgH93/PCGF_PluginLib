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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collection of static functions that are may be useful for plugins.
 */
public class Utils
{
	private static final Class<?> craftItemStackClazz = Reflection.getOBCClass("inventory.CraftItemStack");
	private static final Class<?> nmsItemStackClazz = Reflection.getNMSClass("ItemStack");
	private static final Class<?> nbtTagCompoundClazz = Reflection.getNMSClass("NBTTagCompound");
	private static final Method asNMSCopyMethod = Reflection.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);
	private static final Method saveNmsItemStackMethod = Reflection.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

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
			if(saveNmsItemStackMethod == null || asNMSCopyMethod == null || nbtTagCompoundClazz == null)
			{
				logger.log(Level.SEVERE, "Failed to serialize item stack to NMS item! Bukkit Version: " + Bukkit.getServer().getVersion() +
						"\nIt one or more of the reflection variables is null! Looks like your bukkit version is not compatible. Please check for updates.");
			}
			else
			{
				return saveNmsItemStackMethod.invoke(asNMSCopyMethod.invoke(null, itemStack), nbtTagCompoundClazz.newInstance()).toString();
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
			logger.warning(ChatColor.RED + "   !!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.warning(ChatColor.RED + "   !!!!!! - WARNING - !!!!!!");
			logger.warning(ChatColor.RED + "   !!!!!!!!!!!!!!!!!!!!!!!!!\n");
			logger.warning(ChatColor.RED + "We have detected that you are using \"PerWorldPlugins\"!");
			logger.warning(ChatColor.GOLD + "Please allow this plugin to run in " + ChatColor.BLUE + "ALL" + ChatColor.GOLD + " worlds.");
			logger.warning(ChatColor.GOLD + "If you block it from running in all worlds there probably will be problems!");
			logger.warning(ChatColor.GOLD + "If you don't want you players to use this plugin in certain worlds please use permissions and the plugins config!");
			logger.warning(ChatColor.RED + "There will be no support for bugs caused by \"PerWorldPlugins\"!");
			logger.warning(ChatColor.YELLOW + "Waiting " + pauseTime + " seconds till loading will resume!");

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
}