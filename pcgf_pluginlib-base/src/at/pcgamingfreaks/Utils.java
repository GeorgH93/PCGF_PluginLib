/*
 *   Copyright (C) 2024 GeorgH93
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

package at.pcgamingfreaks;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils
{
	protected Utils() { /* Prevent instance creation of this static helper class */ }

	/**
	 * Converts a byte array into a hex string.
	 *
	 * @param bytes The byte array to convert to hex.
	 * @return The hex string matching the given byte array. The chars a-f will be lower-case!
	 */
	public static @NotNull String byteArrayToHex(byte[] bytes)
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
	 * Blocks the thread for a given time and restores teh interrupted state if an interrupt occurred
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
			catch(InterruptedException ignored)
			{
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Checks if an array contains a certain value
	 *
	 * @param array The array that should be checked
	 * @param data  The data that should be searched for in the array
	 * @param <T>   The class of the array and data
	 * @return True if the data is contained within the array, false if not
	 */
	public static <T> boolean arrayContains(@NotNull T[] array, @Nullable T data)
	{
		for(T element : array)
		{
			if(element.equals(data)) return true;
		}
		return false;
	}

	@SuppressWarnings("UnusedReturnValue")
	public static long streamCopy(final @NotNull InputStream from, final @NotNull OutputStream to) throws IOException
	{
		byte[] buf = new byte[8192];
		long total = 0;
		while (true)
		{
			int r = from.read(buf);
			if (r == -1) break;
			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}

	/**
	 * Extracts a file from a loaded .jar file.
	 *
	 * @param pluginClass The main-class of the plugin that holds the file to be extracted.
	 * @param logger      The logger to be used for messages.
	 * @param inJarPath   The file's path in the jar file.
	 * @param targetFile  The file where the content should be extracted to.
	 * @return True if the file has been extracted successful, false if not.
	 */
	public static boolean extractFile(final @NotNull Class<?> pluginClass, final @NotNull Logger logger, @NotNull String inJarPath, final @NotNull File targetFile)
	{
		try
		{
			if(targetFile.exists() && !targetFile.delete())
			{
				logger.log(Level.WARNING, "Failed to delete old file ({0}).", targetFile);
			}
			File parentFile = targetFile.getParentFile();
			if(!parentFile.exists() && !parentFile.mkdirs())
			{
				logger.log(Level.WARNING, "Failed creating directory''s! Expected path: {0}", parentFile);
			}
			if(!targetFile.createNewFile())
			{
				logger.log(Level.WARNING, "Failed create new file ({0}).", targetFile);
			}
			if(!inJarPath.startsWith("/")) inJarPath = "/" + inJarPath;
			try(InputStream is = pluginClass.getResourceAsStream(inJarPath); OutputStream os = new FileOutputStream(targetFile))
			{
				if(is == null)
				{
					logger.log(Level.SEVERE, "Failed to extract \"{0}\" because it does not exist!", inJarPath);
					return false;
				}
				streamCopy(is, os);
				os.flush();
			}
			logger.log(Level.INFO, "File \"{0}\" extracted successfully!", inJarPath);
			return true;
		}
		catch (IOException | NullPointerException e)
		{
			logger.severe("Failed to extract file \"" + inJarPath + "\"! Reason: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Parses a string into a number. Unlike Java's implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
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
	 * Parses a string into a number. Unlike Java's implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
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
	 * Parses a string into a number. Unlike Java's implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
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
	 * Parses a string into a number. Unlike Java's implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
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
	 * Parses a string into a number. Unlike Java's implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
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
	 * Parses a string into a number. Unlike Java's implementation this method doesn't throw an exception, but will return a given value, when the given string couldn't be parsed.
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
	 * @param inJarPath   The file path in the jar file.
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
	 * @param inJarPath   The file path in the jar file.
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
					if (!target.getCanonicalPath().startsWith(targetDir.getCanonicalPath()))
					{
						System.out.println("Skip file \"" + name + "\" in \"" + jar.getPath() + "\" because it is outside the target directory.");
						continue;
					}
					if(target.exists())
					{
						if(!overwrite) continue;
						//noinspection ResultOfMethodCallIgnored
						target.delete(); // Delete old file
					}
					try(FileOutputStream fileOutputStream = new FileOutputStream(target))
					{
						streamCopy(zip, fileOutputStream);
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

	public static @Nullable InputStream getResource(final @NotNull Class<?> pluginMainClass, final @NotNull String filename)
	{
		try
		{
			final URL url = pluginMainClass.getClassLoader().getResource(filename);
			if (url == null) return null;
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		}
		catch (IOException ignored)
		{
			return null;
		}
	}

	public static <T extends Enum<T>> @NotNull T getEnum(final @NotNull String valueName, final @NotNull T defaultValue)
	{
		if(defaultValue == null) return null; // Fix needed because of old code using this method wrong
		return getEnum(valueName, defaultValue, defaultValue.getClass());
	}

	@Contract("_,!null,_->!null")
	public static <T extends Enum<T>> @Nullable T getEnum(final @NotNull String valueName, final @Nullable T defaultValue, final @NotNull Class<? extends Enum> clazz)
	{
		return getEnum(valueName, defaultValue, clazz, null);
	}

	public static <T extends Enum<T>> @NotNull T getEnum(final @NotNull String valueName, final @NotNull T defaultValue, final @Nullable Logger logger)
	{
		return getEnum(valueName, defaultValue, defaultValue.getClass(), logger);
	}

	@Contract("_,!null,_,_->!null")
	public static <T extends Enum<T>> @Nullable T getEnum(@NotNull String valueName, final @Nullable T defaultValue, final @NotNull Class<? extends Enum> clazz, final @Nullable Logger logger)
	{
		valueName = valueName.toUpperCase(Locale.ENGLISH);
		T v = null;
		try
		{
			//noinspection unchecked
			v = (T) T.valueOf(clazz, valueName);
		}
		catch(IllegalArgumentException ignored)
		{
			if(logger != null)
				logger.info(valueName + " is not a valid option for " + clazz.getSimpleName());
		}
		return (v == null) ? defaultValue : v;
	}

	/**
	 * Inserts a element at a certain position in an array list.
	 * If the array list does not contain enough elements new elements with null value will be added.
	 * @param list The list to which the element should be added.
	 * @param element The element that should be added.
	 * @param index The index at which the element should be added.
	 * @param <T> The type of the list and element.
	 */
	public static <T> void insertAt(ArrayList<T> list, T element, int index)
	{
		if(index < list.size())
		{
			list.set(index, element);
		}
		else if(index == list.size())
		{
			list.add(element);
		}
		else
		{
			while(index > list.size())
			{
				list.add(null);
			}
			list.add(element);
		}
	}
}