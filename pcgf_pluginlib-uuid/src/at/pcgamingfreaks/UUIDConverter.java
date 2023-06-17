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

package at.pcgamingfreaks;

import at.pcgamingfreaks.UUID.MojangUuidResolver;
import at.pcgamingfreaks.UUID.UuidCache;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

/**
 * Functions to get UUIDs to player names. This library doesn't cache the results! You will have to do this on your own!
 * Works with Bukkit and BungeeCord!
 *
 * @deprecated Use {@link at.pcgamingfreaks.UUID.UuidConverter} instead
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.0.50")
public final class UUIDConverter
{
	private static final MojangUuidResolver MOJANG_RESOLVER = new MojangUuidResolver(UuidCache.getSHARED_UUID_CACHE(), Logger.getLogger("UUID Converter"));

	private static UUID toUUID(String uuid)
	{
		if(uuid.contains("-"))
		{
			return UUID.fromString(uuid);
		}
		else
		{
			return UUID.fromString(uuid.replaceAll(MojangUuidResolver.UUID_FORMAT_REGEX, MojangUuidResolver.UUID_FORMAT_REPLACE_TO));
		}
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
		return MOJANG_RESOLVER.getName(toUUID(uuid));
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
	 *                          False if null should be returned if the Mojang server doesn't return a UUID.
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
					uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)).toString();
				}
				else
				{
					return null;
				}
			}
		}
		else
		{
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)).toString();
		}
		// Fixing the separators depending on setting.
		if(withSeparators)
		{
			if(!uuid.contains("-"))
			{
				uuid = uuid.replaceAll(MojangUuidResolver.UUID_FORMAT_REGEX, MojangUuidResolver.UUID_FORMAT_REPLACE_TO);
			}
		}
		else
		{
			if(uuid.contains("-"))
			{
				uuid = uuid.replace("-", "");
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
	 *                          False if null should be returned if the Mojang server doesn't return a UUID.
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
	 *                          False if null should be returned if the Mojang server doesn't return a UUID.
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
				uuid = UUID.fromString(sUUID.replaceAll(MojangUuidResolver.UUID_FORMAT_REGEX, MojangUuidResolver.UUID_FORMAT_REPLACE_TO));
			}
			else if(offlineUUIDonFail)
			{
				uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
			}
		}
		else
		{
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
		}
		return uuid;
	}

	public static UUID getUUIDCacheOnly(@NotNull String name, boolean offlineModeFallback)
	{
		UUID uuid = UuidCache.getSHARED_UUID_CACHE().getUuidFromName(name);
		if (uuid == null && offlineModeFallback)
		{
			uuid = getUUIDFromNameAsUUID(name, false);
		}
		return uuid;
	}

	@Deprecated
	private static String getOnlineUUID(@NotNull String name, @Nullable Date at)
	{
		UUID uuid = MOJANG_RESOLVER.getUUID(name, at);
		if(uuid == null) return null;
		return uuid.toString().replace("-", "");
	}

	//region Multi queries
	//TODO: JavaDoc Exception handling, more parameters, fallback

	public static Map<String, String> getUUIDsFromNames(@NotNull Collection<String> names, boolean onlineMode, boolean withSeparators)
	{
		Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for(Map.Entry<String, UUID> entry : getUUIDsFromNamesAsUUIDs(names, onlineMode).entrySet())
		{
			result.put(entry.getKey(), (withSeparators) ? entry.getValue().toString() : entry.getValue().toString().replace("-", ""));
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
		return MOJANG_RESOLVER.getUUIDs(names);
	}
	//endregion
}