/*
 *   Copyright (C) 2019 GeorgH93
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

import com.google.common.io.ByteStreams;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils
{
	/**
	 * Converts a byte array into a hex string.
	 *
	 * @param bytes The byte array to convert to hex.
	 * @return The hex string matching the given byte array. The chars a-f will be lower case!
	 */
	public static @NotNull String byteArrayToHex(@Nullable byte[] bytes)
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
	 * Checks if an array contains an certain value
	 *
	 * @param array The array that should be checked
	 * @param data  The data that should be searched for in the array
	 * @param <T>   The class of the array and data
	 * @return True if the data is contained within the array, false if not
	 */
	public static <T> boolean arrayContains(@NotNull T[] array, @Nullable T data)
	{
		Validate.notNull(array);
		for(T element : array)
		{
			if(element.equals(data)) return true;
		}
		return false;
	}

	/**
	 * Extracts a file from a loaded .jar file.
	 *
	 * @param pluginClass The main-class of the plugin that holds the file to be extracted.
	 * @param logger      The logger to be used for messages.
	 * @param inJarPath   The files path in the jar file.
	 * @param targetFile  The file where the content should be extracted to.
	 * @return True if the file has been extracted successful, false if not.
	 */
	public static boolean extractFile(@NotNull Class<?> pluginClass, @NotNull Logger logger, @NotNull String inJarPath, @NotNull File targetFile)
	{
		try
		{
			if(targetFile.exists() && !targetFile.delete())
			{
				logger.info("Failed to delete old file (" + targetFile.toString() + ").");
			}
			File parentFile = targetFile.getParentFile();
			if(!parentFile.exists() && !parentFile.mkdirs())
			{
				logger.info("Failed creating directory's! Expected path: " + parentFile.toString());
			}
			if(!targetFile.createNewFile())
			{
				logger.info("Failed create new file (" + targetFile.toString() + ").");
			}
			if(!inJarPath.startsWith("/")) inJarPath = "/" + inJarPath;
			try(InputStream is = pluginClass.getResourceAsStream(inJarPath); OutputStream os = new FileOutputStream(targetFile))
			{
				//noinspection UnstableApiUsage
				ByteStreams.copy(is, os);
				os.flush();
			}
			logger.info("File \"" + inJarPath + "\" extracted successfully!");
			return true;
		}
		catch (IOException | NullPointerException e)
		{
			logger.warning("Failed to extract file \"" + inJarPath + "\"!");
			//e.printStackTrace();
		}
		return false;
	}

	/**
	 * Parses an string into a number. Unlike Javas implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
	 *
	 * @param string The string that should be parsed.
	 * @param fallbackValue The value that should be returned when there was a problem parsing the given string.
	 * @return The parsed value (if the parsing was successful) or the given fallback value (if the parsing was not successful).
	 */
	public static byte tryParse(@NotNull String string, byte fallbackValue)
	{
		try
		{
			return Byte.parseByte(string);
		}
		catch (NumberFormatException ignored) {}
		return fallbackValue;
	}

	/**
	 * Parses an string into a number. Unlike Javas implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
	 *
	 * @param string The string that should be parsed.
	 * @param fallbackValue The value that should be returned when there was a problem parsing the given string.
	 * @return The parsed value (if the parsing was successful) or the given fallback value (if the parsing was not successful).
	 */
	public static short tryParse(@NotNull String string, short fallbackValue)
	{
		try
		{
			return Short.parseShort(string);
		}
		catch (NumberFormatException ignored) {}
		return fallbackValue;
	}

	/**
	 * Parses an string into a number. Unlike Javas implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
	 *
	 * @param string The string that should be parsed.
	 * @param fallbackValue The value that should be returned when there was a problem parsing the given string.
	 * @return The parsed value (if the parsing was successful) or the given fallback value (if the parsing was not successful).
	 */
	public static int tryParse(@NotNull String string, int fallbackValue)
	{
		try
		{
			return Integer.parseInt(string);
		}
		catch (NumberFormatException ignored) {}
		return fallbackValue;
	}

	/**
	 * Parses an string into a number. Unlike Javas implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
	 *
	 * @param string The string that should be parsed.
	 * @param fallbackValue The value that should be returned when there was a problem parsing the given string.
	 * @return The parsed value (if the parsing was successful) or the given fallback value (if the parsing was not successful).
	 */
	public static long tryParse(@NotNull String string, long fallbackValue)
	{
		try
		{
			return Long.parseLong(string);
		}
		catch (NumberFormatException ignored) {}
		return fallbackValue;
	}

	/**
	 * Parses an string into a number. Unlike Javas implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
	 *
	 * @param string The string that should be parsed.
	 * @param fallbackValue The value that should be returned when there was a problem parsing the given string.
	 * @return The parsed value (if the parsing was successful) or the given fallback value (if the parsing was not successful).
	 */
	public static float tryParse(@NotNull String string, float fallbackValue)
	{
		try
		{
			return Float.parseFloat(string);
		}
		catch (NumberFormatException ignored) {}
		return fallbackValue;
	}

	/**
	 * Parses an string into a number. Unlike Javas implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
	 *
	 * @param string The string that should be parsed.
	 * @param fallbackValue The value that should be returned when there was a problem parsing the given string.
	 * @return The parsed value (if the parsing was successful) or the given fallback value (if the parsing was not successful).
	 */
	public static double tryParse(@NotNull String string, double fallbackValue)
	{
		try
		{
			return Double.parseDouble(string);
		}
		catch (NumberFormatException ignored) {}
		return fallbackValue;
	}

	/**
	 * Extracts files from a loaded .jar file.
	 *
	 * @param pluginClass The main-class of the plugin that holds the file to be extracted.
	 * @param inJarPath   The files path in the jar file.
	 * @param targetDir   The directory in which the files should be extracted.
	 * @param overwrite   If existing files should be overwritten.
	 */
	public static void extractFiles(@NotNull Class<?> pluginClass, @NotNull String inJarPath, @NotNull File targetDir, boolean overwrite)
	{
		extractFiles(pluginClass.getProtectionDomain().getCodeSource().getLocation(), inJarPath, targetDir, overwrite);
	}

	/**
	 * Extracts files from a loaded .jar file.
	 *
	 * @param jar         The url to the jar file
	 * @param inJarPath   The files path in the jar file.
	 * @param targetDir   The directory in which the files should be extracted.
	 * @param overwrite   If existing files should be overwritten.
	 */
	public static void extractFiles(@NotNull URL jar, @NotNull String inJarPath, @NotNull File targetDir, boolean overwrite)
	{
		try(ZipInputStream zip = new ZipInputStream(jar.openStream()))
		{
			ZipEntry e;
			while((e = zip.getNextEntry()) != null)
			{
				String name = e.getName();
				if (name.startsWith(inJarPath))
				{
					File target = new File(targetDir, name.replace(inJarPath, ""));
					if(target.exists())
					{
						if(!overwrite) continue;
						//noinspection ResultOfMethodCallIgnored
						target.delete(); // Delete old file
					}
					try(FileOutputStream fileOutputStream = new FileOutputStream(target))
					{
						//noinspection UnstableApiUsage
						ByteStreams.copy(zip, fileOutputStream);
						fileOutputStream.flush();
					}
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}