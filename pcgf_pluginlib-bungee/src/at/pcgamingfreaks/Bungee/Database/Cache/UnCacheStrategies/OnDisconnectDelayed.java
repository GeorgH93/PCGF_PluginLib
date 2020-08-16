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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bungee.Database.Cache.UnCacheStrategies;

import at.pcgamingfreaks.Database.Cache.ICacheablePlayer;
import at.pcgamingfreaks.Database.Cache.IPlayerCache;
import at.pcgamingfreaks.Database.Cache.BaseUnCacheStrategy;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class OnDisconnectDelayed extends BaseUnCacheStrategy implements Listener
{
	private final Plugin plugin;
	private final long delay;

	public OnDisconnectDelayed(final @NotNull Plugin plugin, final @NotNull IPlayerCache cache, final long delay)
	{
		super(cache);
		this.plugin = plugin;
		this.delay = delay;
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}

	@EventHandler(priority = Byte.MAX_VALUE)
	public void playerLeaveEvent(PlayerDisconnectEvent event)
	{
		final ICacheablePlayer player = cache.getCachedPlayer(event.getPlayer().getUniqueId());
		if(player != null && player.canBeUncached()) // We only uncache unmarried player.
		{
			plugin.getProxy().getScheduler().schedule(plugin, () -> {
				if(!player.isOnline())
				{
					cache.unCache(player);
				}
			}, delay, TimeUnit.SECONDS);
		}
	}

	@Override
	public void close()
	{
		plugin.getProxy().getPluginManager().unregisterListener(this);
	}
}