/*
 *   Copyright (C) 2014-2018 GeorgH93
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

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Functions to get UUIDs to player names. This library doesn't cache the results! You will have to do this on your own!
 * Works with Bukkit and BungeeCord!
 */
public final class UUIDConverter
{
	private static final String UUID_FORMAT_REGEX = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})";
	private static final String UUID_FORMAT_REPLACE_TO = "$1-$2-$3-$4-$5";
	private static final long MOJANG_QUERY_RETRY_TIME;

	private static final Gson GSON = new Gson();
	private static final UUIDCacheMap UUID_CACHE; // Cache object for resolved UUIDs

	static
	{
		MOJANG_QUERY_RETRY_TIME = 600000L;
		UUID_CACHE = new UUIDCacheMap();
		System.out.println("Loading local uuid cache.");
		int loaded = 0;
		//noinspection SpellCheckingInspection
		File uuidCache = new File("usercache.json");
		if(uuidCache.exists())
		{
			try(JsonReader reader = new JsonReader(new FileReader(uuidCache)))
			{
				CacheData[] dat = new Gson().fromJson(reader, CacheData[].class);
				Date now = new Date();
				for(CacheData d : dat)
				{
					if(now.before(d.getExpiresDate()))
					{
						loaded++;
						UUID_CACHE.put(d.name, d.uuid);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("Loaded " + loaded + " UUIDs from local cache.");
	}

	/**
	 * Gets the current name of the player from the Mojang servers.
	 * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
	 *
	 * @param uuid The UUID of the player.
	 * @return The name of the player.
	 */
	public static String getNameFromUUID(@NotNull UUID uuid)
	{
		return getNameFromUUID(uuid.toString());
	}

	/**
	 * Gets the current name of the player from the Mojang servers.
	 * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
	 *
	 * @param uuid The UUID of the player.
	 * @return The name of the player.
	 */
	public static String getNameFromUUID(@NotNull String uuid)
	{
		NameChange[] names = getNamesFromUUID(uuid);
		return names[names.length - 1].name;
	}

	/**
	 * Gets the name history of a player from the Mojang servers.
	 * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
	 *
	 * @param uuid The UUID of the player.
	 * @return The names and name change dates of the player.
	 */
	public static NameChange[] getNamesFromUUID(@NotNull UUID uuid)
	{
		return getNamesFromUUID(uuid.toString());
	}

	/**
	 * Gets the name history of a player from the Mojang servers.
	 * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
	 *
	 * @param uuid The UUID of the player.
	 * @return The names and name change dates of the player.
	 */
	public static NameChange[] getNamesFromUUID(@NotNull String uuid)
	{
		NameChange[] names = null;
		try
		{
			Scanner jsonScanner = new Scanner((new URL("https://api.mojang.com/user/profiles/" + uuid.replaceAll("-", "") + "/names")).openConnection().getInputStream(), "UTF-8");
			names = GSON.fromJson(jsonScanner.next(), NameChange[].class);
			jsonScanner.close();
		}
		catch(IOException e)
		{
			System.out.println("Looks like there is a problem with the connection with Mojang. Please retry later.");
			if(e.getMessage().contains("HTTP response code: 429"))
			{
				System.out.println("You have reached the request limit of the Mojang api! Please retry later!");
			}
			else
			{
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			System.out.println("Looks like there is no player with this uuid!\n UUID: \"" + uuid + "\"");
			e.printStackTrace();
		}
		return names;
	}

	/**
	 * @param name       The name of the player you want the UUID from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @return The requested UUID (without separators).
	 */
	public static String getUUIDFromName(@NotNull String name, boolean onlineMode)
	{
		return getUUIDFromName(name, onlineMode, false, false, null);
	}

	/**
	 * @param name          The name of the player you want the UUID from.
	 * @param onlineMode    True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param lastKnownDate The last time you know that the player had this name.
	 * @return The requested UUID (without separators).
	 */
	public static String getUUIDFromName(@NotNull String name, boolean onlineMode, @Nullable Date lastKnownDate)
	{
		return getUUIDFromName(name, onlineMode, false, false, lastKnownDate);
	}

	/**
	 * @param name           The name of the player you want the UUID from.
	 * @param onlineMode     True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param withSeparators True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
	 *                       False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(@NotNull String name, boolean onlineMode, boolean withSeparators)
	{
		return getUUIDFromName(name, onlineMode, withSeparators, false, null);
	}

	/**
	 * @param name           The name of the player you want the UUID from.
	 * @param onlineMode     True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param withSeparators True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
	 *                       False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
	 * @param lastKnownDate  The last time you know that the player had this name.
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(@NotNull String name, boolean onlineMode, boolean withSeparators, @Nullable Date lastKnownDate)
	{
		return getUUIDFromName(name, onlineMode, withSeparators, false, lastKnownDate);
	}

	/**
	 * @param name              The name of the player you want the UUID from.
	 * @param onlineMode        True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param withSeparators    True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
	 *                          False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
	 * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
	 *                          False if null should be returned if the Mojang server doesn't return an UUID.
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(@NotNull String name, boolean onlineMode, boolean withSeparators, boolean offlineUUIDonFail)
	{
		return getUUIDFromName(name, onlineMode, withSeparators, offlineUUIDonFail, null);
	}

	/**
	 * @param name              The name of the player you want the UUID from.
	 * @param onlineMode        True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param withSeparators    True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
	 *                          False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
	 * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
	 *                          False if null should be returned if the Mojang server doesn't return an UUID.
	 * @param lastKnownDate     The last time you know that the player had this name.
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(@NotNull String name, boolean onlineMode, boolean withSeparators, boolean offlineUUIDonFail, @Nullable Date lastKnownDate)
	{
		String uuid;
		if(onlineMode)
		{
			uuid = getOnlineUUID(name, lastKnownDate);
			if(uuid == null)
			{
				if(offlineUUIDonFail)
				{
					System.out.println("Using offline uuid for '" + name + "'.");
					uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)).toString();
				}
				else
				{
					return null;
				}
			}
		}
		else
		{
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)).toString();
		}
		// Fixing the separators depending on setting.
		if(withSeparators)
		{
			if(!uuid.contains("-"))
			{
				uuid = uuid.replaceAll(UUID_FORMAT_REGEX, UUID_FORMAT_REPLACE_TO);
			}
		}
		else
		{
			if(uuid.contains("-"))
			{
				uuid = uuid.replaceAll("-", "");
			}
		}
		return uuid;
	}

	/**
	 * @param name       The name of the player you want to retrieve the UUID from.
	 * @param onlineMode True if the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @return The requested UUID object.
	 */
	public static UUID getUUIDFromNameAsUUID(@NotNull String name, boolean onlineMode)
	{
		return getUUIDFromNameAsUUID(name, onlineMode, false);
	}

	/**
	 * @param name          The name of the player you want to retrieve the UUID from.
	 * @param onlineMode    True if the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param lastKnownDate The last time you know that the player had this name.
	 * @return The requested UUID object.
	 */
	public static UUID getUUIDFromNameAsUUID(@NotNull String name, boolean onlineMode, @Nullable Date lastKnownDate)
	{
		return getUUIDFromNameAsUUID(name, onlineMode, false, lastKnownDate);
	}

	/**
	 * @param name              The name of the player you want to retrieve the UUID from.
	 * @param onlineMode        True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
	 *                          False if null should be returned if the Mojang server doesn't return an UUID.
	 * @return The requested UUID object.
	 */
	public static UUID getUUIDFromNameAsUUID(@NotNull String name, boolean onlineMode, boolean offlineUUIDonFail)
	{
		return getUUIDFromNameAsUUID(name, onlineMode, offlineUUIDonFail, null);
	}

	/**
	 * @param name              The name of the player you want to retrieve the UUID from.
	 * @param onlineMode        True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
	 *                          False if null should be returned if the Mojang server doesn't return an UUID.
	 * @param lastKnownDate     The last time you know that the player had this name.
	 * @return The requested UUID object.
	 */
	public static UUID getUUIDFromNameAsUUID(@NotNull String name, boolean onlineMode, boolean offlineUUIDonFail, @Nullable Date lastKnownDate)
	{
		UUID uuid = null;
		if(onlineMode)
		{
			String sUUID = getOnlineUUID(name, lastKnownDate);
			if(sUUID != null)
			{
				uuid = UUID.fromString(sUUID.replaceAll(UUID_FORMAT_REGEX, UUID_FORMAT_REPLACE_TO));
			}
			else if(offlineUUIDonFail)
			{
				uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
			}
		}
		else
		{
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
		}
		return uuid;
	}

	private static String getOnlineUUID(@NotNull String name, @Nullable Date at)
	{
		if((at == null || at.after(new Date(System.currentTimeMillis() - 1000L*24*3600* 30))) && UUID_CACHE.containsKey(name))
		{
			return UUID_CACHE.get(name);
		}
		String uuid = null;
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name + ((at != null) ? "?at=" + (at.getTime()/1000L) : "")).openStream(), "UTF-8")))
		{
			uuid = (((JsonObject) new JsonParser().parse(in)).get("id")).getAsString();
			if(uuid != null && (at == null || at.after(new Date(System.currentTimeMillis() - 1000L*24*3600* 30))))
			{
				UUID_CACHE.put(name, uuid);
			}
		}
		catch(MalformedURLException e) // There is something going wrong!
		{
			System.out.println("Failed to get uuid cause of a malformed url!\n Name: \"" + name + "\" Date: " + ((at != null) ? "?at=" + at.getTime()/1000L : "null"));
			e.printStackTrace();
		}
		catch(IOException e)
		{
			System.out.println("Looks like there is a problem with the connection with mojang. Please retry later.");
			if(e.getMessage().contains("HTTP response code: 429"))
			{
				System.out.println("You have reached the request limit of the mojang api! Please retry later!");
			}
			e.printStackTrace();
		}
		catch(Exception e)
		{
			if(at == null) // We can't resolve the uuid for the player
			{
				System.out.println("Unable to get UUID for: " + name + "!");
			}
			else if(at.getTime() == 0) // If it's not his first name maybe it's his current name
			{
				System.out.println("Unable to get UUID for: " + name + " at 0! Trying without date!");
				uuid = getOnlineUUID(name, null);
			}
			else // If we cant get the player with the date he was here last time it's likely that it is his first name
			{
				System.out.println("Unable to get UUID for: " + name + " at " + at.getTime()/1000L + "! Trying at=0!");
				uuid = getOnlineUUID(name, new Date(0));
			}
			//e.printStackTrace();
		}
		return uuid;
	}

	//region Multi querys
	//TODO: JavaDoc Exception handling, more parameters, fallback
	private static final int BATCH_SIZE = 100; // Limit from Mojang

	public static Map<String, String> getUUIDsFromNames(@NotNull Collection<String> names, boolean onlineMode, boolean withSeparators)
	{
		Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for(Map.Entry<String, UUID> entry : getUUIDsFromNamesAsUUIDs(names, onlineMode).entrySet())
		{
			result.put(entry.getKey(), (withSeparators) ? entry.getValue().toString() : entry.getValue().toString().replaceAll("-", ""));
		}
		return result;
	}

	public static Map<String, UUID> getUUIDsFromNamesAsUUIDs(@NotNull Collection<String> names, boolean onlineMode)
	{
		if(onlineMode)
		{
			return getUUIDsFromNamesAsUUIDs(names);
		}
		Map<String, UUID> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for(String name : names)
		{
			result.put(name, getUUIDFromNameAsUUID(name, false));
		}
		return result;
	}

	public static Map<String, UUID> getUUIDsFromNamesAsUUIDs(@NotNull Collection<String> names)
	{
		List<String> batch = new LinkedList<>();
		Iterator<String> players = names.iterator();
		Map<String, UUID> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		boolean success;
		int fromCache = 0, fromWeb = 0;
		while (players.hasNext())
		{
			while (players.hasNext() && batch.size() < BATCH_SIZE)
			{
				String name = players.next();
				if(UUID_CACHE.containsKey(name))
				{
					result.put(name, UUID.fromString(UUID_CACHE.get(name).replaceAll(UUID_FORMAT_REGEX, UUID_FORMAT_REPLACE_TO)));
					fromCache++;
				}
				else
				{
					batch.add(name);
				}
			}
			do
			{
				HttpURLConnection connection = null;
				try
				{
					connection = (HttpURLConnection) new URL("https://api.mojang.com/profiles/minecraft").openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json; encoding=UTF-8");
					connection.setUseCaches(false);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					try(OutputStream out = connection.getOutputStream())
					{
						out.write(GSON.toJson(batch).getBytes(Charsets.UTF_8));
					}
					Profile[] profiles;
					try(Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")))
					{
						profiles = GSON.fromJson(in, Profile[].class);
					}
					for (Profile profile : profiles)
					{
						result.put(profile.name, profile.getUUID());
						UUID_CACHE.put(profile.name, profile.getUUID().toString());
						fromWeb++;
					}
				}
				catch(IOException e)
				{
					try
					{
						if(connection != null && connection.getResponseCode() == 429)
						{
							System.out.println("Reached the request limit of the mojang api!\nConverting will be paused for 10 minutes and then continue!");
							//TODO: better fail handling
							Thread.sleep(MOJANG_QUERY_RETRY_TIME);
							success = false;
							continue;
						}
						else
						{
							e.printStackTrace();
						}
					}
					catch(InterruptedException | IOException ignore) {}
					return result;
				}
				batch.clear();
				success = true;
			} while(!success);
		}
		System.out.println("Converted " + (fromCache + fromWeb) + "/" + names.size() + " UUIDs (" + fromCache + " of them from the cache and " + fromWeb + " from Mojang).");
		return result;
	}
	//endregion

	//region Helper Classes
	/**
	 * A helper class to store the name changes and dates
	 */
	public static class NameChange
	{
		/**
		 * The name to which the name was changed
		 */
		public String name;

		/**
		 * DateTime of the name change in UNIX time (without milliseconds)
		 */
		public long changedToAt;

		/**
		 * Gets the date of a name change
		 *
		 * @return Date of the name change
		 */
		public Date getChangeDate()
		{
			return new Date(changedToAt);
		}
	}

	private static class Profile
	{
		public String id;
		public String name;

		public UUID getUUID()
		{
			return UUID.fromString(id.replaceAll(UUID_FORMAT_REGEX, UUID_FORMAT_REPLACE_TO));
		}
	}

	private static class CacheData
	{
		public String name, uuid, expiresOn;

		public Date getExpiresDate()
		{
			try
			{
				//noinspection SpellCheckingInspection
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(expiresOn);
			}
			catch(ParseException e)
			{
				e.printStackTrace();
			}
			return new Date(); // When we failed to parse the date we return the current time stamp
		}
	}
	//endregion
}