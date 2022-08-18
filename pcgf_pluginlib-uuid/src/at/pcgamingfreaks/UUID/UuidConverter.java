/*
 *   Copyright (C) 2022 GeorgH93
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class UuidConverter
{
	private static final Pattern UUID_FORMAT_PATTERN = Pattern.compile("([0-9a-fA-F]{8})-?([0-9a-fA-F]{4})-?([0-9a-fA-F]{4})-?([0-9a-fA-F]{4})-?([0-9a-fA-F]{12})");
	private final @NotNull UuidCache cache;
	private final @NotNull MojangUuidResolver mojangUuidResolver;
	private final @NotNull Logger logger;

	//region Constructors
	public UuidConverter()
	{
		this(null, null);
	}

	public UuidConverter(final @NotNull Logger logger)
	{
		this(null, logger);
	}

	public UuidConverter(@Nullable UuidCache uuidCache)
	{
		this(uuidCache, null);
	}

	public UuidConverter(@Nullable UuidCache uuidCache, @Nullable Logger logger)
	{
		if(uuidCache == null) uuidCache = UuidCache.getSHARED_UUID_CACHE();
		if(logger == null) logger = Logger.getLogger("UUID Converter");

		this.cache = uuidCache;
		this.mojangUuidResolver = new MojangUuidResolver(uuidCache);
		this.logger = logger;
	}
	//endregion

	public static @NotNull UUID uuidFromString(@NotNull String uuid) throws IllegalArgumentException
	{
		if(UUID_FORMAT_PATTERN.matcher(uuid).matches())
		{
			if(uuid.length() != 36) uuid = uuid.replaceAll(MojangUuidResolver.UUID_FORMAT_REGEX, MojangUuidResolver.UUID_FORMAT_REPLACE_TO);
			return UUID.fromString(uuid);
		}
		throw new IllegalArgumentException("Invalid uuid format!");
	}

	public static UUID getOfflineModeUUID(final @NotNull String name)
	{
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
	}

	@Contract("_,true->!null")
	public @Nullable UUID getUUIDCacheOnly(final @NotNull String name, boolean offlineModeFallback)
	{
		if(offlineModeFallback && !cache.contains(name)) return getOfflineModeUUID(name);
		return cache.getUuidFromName(name);
	}

	@Contract("_,true->!null")
	public @Nullable UUID getUUID(final @NotNull String name, boolean offlineModeFallback)
	{
		UUID uuid = mojangUuidResolver.getUUID(name, null);
		if(uuid != null || !offlineModeFallback) return uuid;
		return getOfflineModeUUID(name);
	}

	public String getName(final @NotNull UUID uuid)
	{
		return mojangUuidResolver.getName(uuid);
	}
}