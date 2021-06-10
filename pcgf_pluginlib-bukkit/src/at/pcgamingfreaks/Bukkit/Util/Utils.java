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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class Utils extends at.pcgamingfreaks.Utils
{
	private static final IUtils INSTANCE = IUtils.INSTANCE;

	private static final Method METHOD_JAVA_PLUGIN_GET_FILE = Reflection.getMethod(JavaPlugin.class, "getFile");

	public static final ChatColor[] CHAT_COLORS;

	static
	{
		CHAT_COLORS = new ChatColor[16];
		int i = 0;
		for(ChatColor c : ChatColor.values())
		{
			if(c.isColor() && c != ChatColor.RESET) CHAT_COLORS[i++] = c;
		}
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
	public static void warnIfPerWorldPluginsIsInstalled(final @NotNull Logger logger)
	{
		warnIfPerWorldPluginsIsInstalled(logger, 5);
	}

	/**
	 * Shows a warning message if per world plugins is installed and blocks the executing thread for a given time.
	 *
	 * @param logger The logger to output the warning
	 * @param pauseTime The time in seconds the function should be blocking if PerWorldPlugins is installed.
	 */
	public static void warnIfPerWorldPluginsIsInstalled(final @NotNull Logger logger, final int pauseTime)
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
			blockThread(pauseTime);
		}
	}

	/**
	 * Gets the jar file of a Bukkit JavaPlugin.
	 *
	 * @param plugin The plugin to get the jar file of.
	 * @return The jar file of the given plugin.
	 * @throws RuntimeException If there was a problem obtaining the plugin.
	 */
	public static File getPluginJarFile(final @NotNull JavaPlugin plugin) throws RuntimeException
	{
		try
		{
			//noinspection ConstantConditions
			return (File) METHOD_JAVA_PLUGIN_GET_FILE.invoke(plugin);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to retrieve jar file for plugin " + plugin.getName(), e);
		}
	}

	public static @NotNull List<String> getPlayerNamesStartingWith(@NotNull String startingWith, final @NotNull CommandSender exclude)
	{
		String excludeName = exclude.getName().toLowerCase(Locale.ROOT);
		startingWith = startingWith.toLowerCase(Locale.ROOT);
		List<String> names = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			String nameLower = player.getName().toLowerCase(Locale.ROOT);
			if(!nameLower.equals(excludeName) && nameLower.startsWith(startingWith)) names.add(player.getName());
		}
		return names;
	}

	/**
	 * Gets the ping for a player.
	 *
	 * @param player The player for witch the ping should be retrieved.
	 * @return The ping of the player.
	 */
	public static int getPing(final @NotNull Player player)
	{
		return INSTANCE.getPing(player);
	}

	/**
	 * Sends a nms packet to the client
	 *
	 * @param player The player that should receive the packet
	 * @param packet The packet that should be sent to the client
	 */
	public static void sendPacket(final @NotNull Player player, final @NotNull Object packet)
	{
		INSTANCE.sendPacket(player, packet);
	}

	//region Location stuff
	/**
	 * Calculates the distance between two players
	 * Unlike Bukkit's built in function this will not cause an exception if the players aren't in the same world but return {@link Double#POSITIVE_INFINITY}
	 *
	 * @param player1 The first player
	 * @param player2 The second player
	 * @return The distance between the players. {@link Double#POSITIVE_INFINITY} if the players aren't in the same world
	 */
	public static double getDistance(final @NotNull Player player1, final @NotNull Player player2)
	{
		if(player1.equals(player2))
		{
			return 0;
		}
		if(player1.getWorld().getName().equalsIgnoreCase(player2.getWorld().getName()))
		{
			return player1.getLocation().distance(player2.getLocation());
		}
		return Double.POSITIVE_INFINITY;
	}

	/**
	 * Calculates the squared distance between two players
	 * Unlike Bukkit's built in function this will not cause an exception if the players aren't in the same world but return {@link Double#POSITIVE_INFINITY}
	 *
	 * @param player1 The first player
	 * @param player2 The second player
	 * @return The distance between the players. {@link Double#POSITIVE_INFINITY} if the players aren't in the same world
	 */
	public static double getDistanceSquared(final @NotNull Player player1, final @NotNull Player player2)
	{
		if(player1.equals(player2))
		{
			return 0;
		}
		if(player1.getWorld().getName().equalsIgnoreCase(player2.getWorld().getName()))
		{
			return player1.getLocation().distanceSquared(player2.getLocation());
		}
		return Double.POSITIVE_INFINITY;
	}

	/**
	 * Checks if two players are within a certain range from each other.
	 *
	 * @param player1 The first player.
	 * @param player2 The second player.
	 * @param maxDistance The max distance between the two players. Negative values will always return true.
	 * @return True if the players are within the given range, false if not.
	 */
	public static boolean inRange(final @NotNull Player player1, final @NotNull Player player2, final double maxDistance)
	{
		if(maxDistance < 0) return true;
		double distance = getDistanceSquared(player1, player2);
		return (maxDistance == 0 && distance != Double.POSITIVE_INFINITY) || distance <= maxDistance * maxDistance;
	}

	/**
	 * Checks if two players are within a certain range from each other.
	 *
	 * @param player1 The first player.
	 * @param player2 The second player.
	 * @param maxDistanceSquared The max squared distance between the two players. Negative values will always return true.
	 * @return True if the players are within the given range, false if not.
	 */
	public static boolean inRangeSquared(final @NotNull Player player1, final @NotNull Player player2, final double maxDistanceSquared)
	{
		if(maxDistanceSquared < 0) return true;
		double distance = getDistanceSquared(player1, player2);
		return (maxDistanceSquared == 0 && distance != Double.POSITIVE_INFINITY) || distance <= maxDistanceSquared;
	}

	/**
	 * Checks if two players are within a certain range from each other.
	 *
	 * @param player1 The first player.
	 * @param player2 The second player.
	 * @param maxDistance The max distance between the two players. Negative values will always return true.
	 * @param bypassPermission If one of the players has the permission this function will return true.
	 * @return True if the players are within the given range, false if not.
	 */
	public static boolean inRange(final @NotNull Player player1, final @NotNull Player player2, final double maxDistance, final @NotNull String bypassPermission)
	{
		return player1.hasPermission(bypassPermission) || player2.hasPermission(bypassPermission) || inRange(player1, player2, maxDistance);
	}

	/**
	 * Checks if two players are within a certain range from each other.
	 *
	 * @param player1 The first player.
	 * @param player2 The second player.
	 * @param maxDistanceSquared The max squared distance between the two players. Negative values will always return true.
	 * @param bypassPermission If one of the players has the permission this function will return true.
	 * @return True if the players are within the given range, false if not.
	 */
	public static boolean inRangeSquared(final @NotNull Player player1, final @NotNull Player player2, final double maxDistanceSquared, final @NotNull String bypassPermission)
	{
		return player1.hasPermission(bypassPermission) || player2.hasPermission(bypassPermission) || inRangeSquared(player1, player2, maxDistanceSquared);
	}
	//endregion

	public static @Nullable Object jsonToIChatComponent(final @NotNull String json)
	{
		return INSTANCE.jsonToIChatComponent(json);
	}

	/**
	 * Checks if the settings.bungeecord config option is enabled in the spigot.yml
	 *
	 * @return True if bungeecord is enabled, false if it is disabled or the server is using bukkit.
	 */
	public static boolean detectBungeeCord()
	{
		try
		{
			Object spigotServer = Server.class.getMethod("spigot").invoke(Bukkit.getServer());
			Method getConfigMethod = spigotServer.getClass().getMethod("getConfig");
			getConfigMethod.setAccessible(true);
			YamlConfiguration spigotConfig = (YamlConfiguration) getConfigMethod.invoke(spigotServer);
			boolean bungee = spigotConfig.getBoolean("settings.bungeecord");
			return bungee;
		}
		catch(Exception ignored) {}
		return false;
	}
}