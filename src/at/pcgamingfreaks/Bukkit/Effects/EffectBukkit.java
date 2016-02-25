/*
 *   Copyright (C) 2016 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Effects;

import at.pcgamingfreaks.Bukkit.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public abstract class EffectBukkit extends EffectBase
{
	protected void spawnParticle(Location location, double visibleRange, Object particle) throws IllegalAccessException, InvocationTargetException
	{
		if(particle == null) return;
		for(Entity entity : location.getWorld().getEntities())
		{
			if(entity instanceof Player && entity.getLocation().getWorld().equals(location.getWorld()) && entity.getLocation().distance(location) < visibleRange)
			{
				Utils.sendPacket((Player)entity, particle);
			}
		}
	}
}