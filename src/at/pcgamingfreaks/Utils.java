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

package at.pcgamingfreaks;

import at.pcgamingfreaks.Calendar.TimeSpan;

import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class Utils
{
	/**
	 * Converts a byte array into a hex string.
	 *
	 * @param bytes The byte array to convert to hex.
	 * @return The hex string matching the given byte array. The chars a-f will be lower case!
	 */
	public static String byteArrayToHex(@Nullable byte[] bytes)
	{
		if(bytes == null || bytes.length == 0) return "";
		StringBuilder hexBuilder = new StringBuilder(bytes.length * 2);
		for(byte b: bytes)
		{
			hexBuilder.append(String.format("%02x", b));
		}
		return hexBuilder.toString();
	}

	/**
	 * Shows a warning message if the Java version is still 1.7.
	 *
	 * @param logger The logger to output the warning
	 */
	public static void warnOnJava_1_7(@NotNull Logger logger)
	{
		warnOnJava_1_7(logger, 0);
	}

	/**
	 * Shows a warning message if the Java version is still 1.7.
	 *
	 * @param logger The logger to output the warning
	 * @param pauseTime The time in seconds the function should be blocking (in seconds) if Java is outdated. Values below 1 wont block.
	 */
	public static void warnOnJava_1_7(@NotNull Logger logger, int pauseTime)
	{
		Validate.notNull(logger, "The logger must not be null.");
		if (System.getProperty("java.version").startsWith("1.7"))
		{
			TimeSpan ts = new TimeSpan(1430438401000L, true);
			logger.warning(ConsoleColor.RED + "You are still using Java 1.7. The support end of Java 1.7 was " + ts.getYears() + " years and " + ts.getMonths() + " months ago! You should really update to Java 1.8!" + ConsoleColor.RESET);
			logger.info(ConsoleColor.YELLOW + "For now this plugin will still work fine with Java 1.7 but no warranty that this won't change in the future." + ConsoleColor.RESET);
			blockThread(pauseTime);
		}
	}

	/**
	 * Blocks the thread for a given time
	 *
	 * @param pauseTime The time in seconds that the thread should be blocked
	 */
	public static void blockThread(int pauseTime)
	{
		if(pauseTime > 0) // If there is a valid time we pause the current thread for that time
		{
			try
			{
				Thread.sleep(pauseTime * 1000L);
			}
			catch(InterruptedException ignored) {}
		}
	}

	/**
	 * This function calculates the distance between two players.
	 * Unlike Bukkit's built in function this will not cause an exception if the players aren't in the same world but return {@link Double#POSITIVE_INFINITY}.
	 *
	 * @param player1 The first player.
	 * @param player2 The second player.
	 * @return The distance between the players. {@link Double#POSITIVE_INFINITY} if the players aren't in the same world.
	 */
	public static double getDistance(@NotNull Player player1, @NotNull Player player2)
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
	 * Checks if two players are within a certain range from each other.
	 *
	 * @param player1 The first player.
	 * @param player2 The second player.
	 * @param maxDistance The max distance between the two players. Negative values will always return true.
	 * @return True if the players are within the given range, false if not.
	 */
	public static boolean inRange(@NotNull Player player1, @NotNull Player player2, double maxDistance)
	{
		if(maxDistance < 0) return true;
		double distance = getDistance(player1, player2);
		return (maxDistance == 0 && distance != Double.POSITIVE_INFINITY) || distance <= maxDistance;
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
	public static boolean inRange(@NotNull Player player1, @NotNull Player player2, double maxDistance, @NotNull String bypassPermission)
	{
		return player1.hasPermission(bypassPermission) || player2.hasPermission(bypassPermission) || inRange(player1, player2, maxDistance);
	}
}