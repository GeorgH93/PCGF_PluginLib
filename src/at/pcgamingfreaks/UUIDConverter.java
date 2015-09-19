/*
 *   Copyright (C) 2014-2015 GeorgH93
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

/**
 * Functions to get UUIDs to player names. This library doesn't cache the results! You will have to do this on your own!
 * Works with Bukkit and BungeeCord!
 */
public class UUIDConverter
{
	/**
	 * Gets the current name of the player from the Mojang servers.
	 * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
	 *
	 * @param uuid The UUID of the player.
	 * @return The name of the player.
	 */
	public static String getNameFromUUID(UUID uuid)
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
	public static String getNameFromUUID(String uuid)
	{
		String name = null;
		try
		{
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replaceAll("-", ""));
			Scanner jsonScanner = new Scanner(url.openConnection().getInputStream(), "UTF-8");
			name = (((JsonObject)new JsonParser().parse(jsonScanner.next())).get("name")).toString();
			jsonScanner.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * A helper class to store the name changes and dates
	 */
	public class NameChange
	{
		/**
		 * The name to witch the name was changed
		 */
		public String name;

		/**
		 * Datetime of the name change in UNIX time (without milliseconds)
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

	/**
	 * Gets the name history of a player from the Mojang servers.
	 * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
	 *
	 * @param uuid The UUID of the player.
	 * @return The names and name change dates of the player.
	 */
	public static NameChange[] getNamesFromUUID(UUID uuid)
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
	public static NameChange[] getNamesFromUUID(String uuid)
	{
		NameChange[] names = null;
		try
		{
			URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.replaceAll("-", "") + "/names");
			Scanner jsonScanner = new Scanner(url.openConnection().getInputStream(), "UTF-8");
			names = (new Gson()).fromJson(jsonScanner.next(), NameChange[].class);
			jsonScanner.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return names;
	}

	/**
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(String name, boolean onlineMode)
	{
		return getUUIDFromName(name, onlineMode, false, false, null);
	}

	/**
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param lastKnownDate The last time you know that the player had this name.
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(String name, boolean onlineMode, Date lastKnownDate)
	{
		return getUUIDFromName(name, onlineMode, false, false, lastKnownDate);
	}

	/**
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param withSeparators True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
	 *                       False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(String name, boolean onlineMode, boolean withSeparators)
	{
		return getUUIDFromName(name, onlineMode, withSeparators, false, null);
	}

	/**
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param withSeparators True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
	 *                       False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
	 * @param lastKnownDate The last time you know that the player had this name.
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(String name, boolean onlineMode, boolean withSeparators, Date lastKnownDate)
	{
		return getUUIDFromName(name, onlineMode, withSeparators, false, lastKnownDate);
	}

	/**
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param withSeparators True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
	 *                       False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
	 * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
	 *                          False if null should be returned if the Mojang server doesn't return an UUID.
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(String name, boolean onlineMode, boolean withSeparators, boolean offlineUUIDonFail)
	{
		return getUUIDFromName(name, onlineMode, withSeparators, offlineUUIDonFail, null);
	}

	/**
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param withSeparators True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
	 *                       False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
	 * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
	 *                          False if null should be returned if the Mojang server doesn't return an UUID.
	 * @param lastKnownDate The last time you know that the player had this name.
	 * @return The requested UUID.
	 */
	public static String getUUIDFromName(String name, boolean onlineMode, boolean withSeparators, boolean offlineUUIDonFail, Date lastKnownDate)
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
                uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
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
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @return The requested UUID.
	 */
	public static UUID getUUIDFromNameAsUUID(String name, boolean onlineMode)
	{
		return getUUIDFromNameAsUUID(name, onlineMode, false, null);
	}

	/**
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param lastKnownDate The last time you know that the player had this name.
	 * @return The requested UUID.
	 */
	public static UUID getUUIDFromNameAsUUID(String name, boolean onlineMode, Date lastKnownDate)
	{
		return getUUIDFromNameAsUUID(name, onlineMode, false, lastKnownDate);
	}

	/**
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
	 *                          False if null should be returned if the Mojang server doesn't return an UUID.
	 * @return The requested UUID.
	 */
	public static UUID getUUIDFromNameAsUUID(String name, boolean onlineMode, boolean offlineUUIDonFail)
	{
		return getUUIDFromNameAsUUID(name, onlineMode, offlineUUIDonFail, null);
	}

	/**
	 * @param name The name of the player you want the uuid from.
	 * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
	 * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
	 *                          False if null should be returned if the Mojang server doesn't return an UUID.
	 * @param lastKnownDate The last time you know that the player had this name.
	 * @return The requested UUID.
	 */
	public static UUID getUUIDFromNameAsUUID(String name, boolean onlineMode, boolean offlineUUIDonFail, Date lastKnownDate)
	{
		UUID uuid = null;
		if(onlineMode)
		{
			String sUUID = getOnlineUUID(name, lastKnownDate);
			if(sUUID != null)
			{
				uuid = UUID.fromString(sUUID.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
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

	private static String getOnlineUUID(String name, Date at)
	{
		String uuid = null;
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name + ((at != null) ? "?at=" + at.getTime() : "")).openStream()));
			uuid = (((JsonObject)new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "");
			in.close();
		}
		catch (Exception e)
		{
			if(at == null)
			{
				System.out.println("Unable to get UUID for: " + name + "!");
			}
			else
			{
				System.out.println("Unable to get UUID for: " + name + " at " + at.getTime() + "! Trying without date!");
				uuid = getOnlineUUID(name, null);
			}
			//e.printStackTrace();
		}
		return uuid;
	}
}