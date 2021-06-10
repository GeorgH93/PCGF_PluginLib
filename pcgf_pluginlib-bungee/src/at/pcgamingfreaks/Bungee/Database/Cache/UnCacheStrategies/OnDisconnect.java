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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
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

public class OnDisconnect extends BaseUnCacheStrategy implements Listener
{
	private final Plugin plugin;

	public OnDisconnect(final @NotNull Plugin plugin, final @NotNull IPlayerCache cache)
	{
		super(cache);
		this.plugin = plugin;
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}

	@EventHandler(priority = Byte.MAX_VALUE)
	public void playerLeaveEvent(PlayerDisconnectEvent event)
	{
		ICacheablePlayer player = cache.getCachedPlayer(event.getPlayer().getUniqueId());
		if(player != null && !player.isOnline() && player.canBeUncached()) // We only uncache unmarried player.
		{
			this.cache.unCache(player);
		}
	}

	@Override
	public void close()
	{
		plugin.getProxy().getPluginManager().unregisterListener(this);
	}
}