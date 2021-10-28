/*
 *   Copyright (C) 2021 GeorgH93
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

package at.pcgamingfreaks.UUID;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UuidCache
{
	@Getter private static final UuidCache SHARED_UUID_CACHE = new UuidCache();

	private final Map<UUID, String> uuidToNameMap = new ConcurrentHashMap<>();
	private final Map<String, UUID> nameToUuidMap = new ConcurrentHashMap<>();

	public UuidCache()
	{
		this(true);
	}

	public UuidCache(final boolean loadMinecraftServerCache)
	{
		if(loadMinecraftServerCache)
		{
			System.out.println("Loading local uuid cache.");
			int loaded = 0;
			//noinspection SpellCheckingInspection
			File uuidCache = new File("usercache.json");
			if(uuidCache.exists())
			{
				try(JsonReader reader = new JsonReader(new FileReader(uuidCache)))
				{
					ServerUserCacheData[] dat = new Gson().fromJson(reader, ServerUserCacheData[].class);
					Date now = new Date();
					for(ServerUserCacheData d : dat)
					{
						if(now.before(d.getExpiresDate()))
						{
							loaded++;
							addToCache(d.getUUID(), d.name);
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
	}

	public void addToCache(final @NotNull UUID uuid, final @NotNull String name)
	{
		uuidToNameMap.put(uuid, name);
		nameToUuidMap.put(name.toLowerCase(Locale.ROOT), uuid);
	}

	public @Nullable UUID getUuidFromName(final @NotNull String name)
	{
		return nameToUuidMap.get(name);
	}

	public @Nullable String getNameFromUuid(final @NotNull UUID uuid)
	{
		return uuidToNameMap.get(uuid);
	}

	public boolean contains(final @NotNull String name)
	{
		return nameToUuidMap.containsKey(name);
	}

	public boolean contains(final @NotNull UUID uuid)
	{
		return uuidToNameMap.containsKey(uuid);
	}
}