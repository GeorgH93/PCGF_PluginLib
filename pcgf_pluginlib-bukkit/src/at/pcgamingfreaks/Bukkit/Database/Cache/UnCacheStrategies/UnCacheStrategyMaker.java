/*
 *   Copyright (C) 2025 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Database.Cache.UnCacheStrategies;

import at.pcgamingfreaks.Database.Cache.BaseUnCacheStrategy;
import at.pcgamingfreaks.Database.Cache.IPlayerCache;
import at.pcgamingfreaks.Database.Cache.IUnCacheStrategyConfig;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class UnCacheStrategyMaker
{
	private UnCacheStrategyMaker()
	{}

	/**
	 * Makes a BungeeCord uncache strategy using the provided settings.
	 *
	 * @param plugin The plugin owning the cache and uncache strategy
	 * @param cache The cache for which the uncache strategy should be generated
	 * @param config The config for the uncache strategy
	 * @return The created uncache strategy
	 */
	@Deprecated
	public static @NotNull BaseUnCacheStrategy make(final @NotNull Plugin plugin, final @NotNull IPlayerCache cache, final @NotNull IUnCacheStrategyConfig config)
	{
		FoliaLib foliaLib = new FoliaLib(plugin);
		long delay = config.getUnCacheDelay() * 20L, interval = config.getUnCacheInterval() * 20L;
		switch(config.getUnCacheStrategy())
		{
			case ON_DISCONNECT: return new OnDisconnect(plugin, cache);
			case ON_DISCONNECT_DELAYED: return new OnDisconnectDelayed(plugin, cache, delay);
			case INTERVAL_DELAYED: return new IntervalDelayed(foliaLib.getScheduler(), cache, delay, interval);
			case INTERVAL: default: return new Interval(foliaLib.getScheduler(), cache, delay, interval);
		}
	}

	/**
	 * Makes a BungeeCord uncache strategy using the provided settings.
	 *
	 * @param plugin The plugin owning the cache and uncache strategy
	 * @param cache The cache for which the uncache strategy should be generated
	 * @param config The config for the uncache strategy
	 * @return The created uncache strategy
	 */
	public static @NotNull BaseUnCacheStrategy make(final @NotNull Plugin plugin, final @NotNull PlatformScheduler scheduler, final @NotNull IPlayerCache cache, final @NotNull IUnCacheStrategyConfig config)
	{
		long delay = config.getUnCacheDelay() * 20L, interval = config.getUnCacheInterval() * 20L;
		switch(config.getUnCacheStrategy())
		{
			case ON_DISCONNECT: return new OnDisconnect(plugin, cache);
			case ON_DISCONNECT_DELAYED: return new OnDisconnectDelayed(plugin, cache, delay);
			case INTERVAL_DELAYED: return new IntervalDelayed(scheduler, cache, delay, interval);
			case INTERVAL: default: return new Interval(scheduler, cache, delay, interval);
		}
	}
}