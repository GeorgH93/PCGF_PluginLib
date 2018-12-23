/*
 *   Copyright (C) 2018 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Particles;

import at.pcgamingfreaks.Bukkit.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.lang.reflect.InvocationTargetException;

@Deprecated
abstract class ParticleSpawnerBukkitNMSBase extends ParticleSpawner
{
	@SuppressWarnings("deprecation")
	public void spawnParticle(Location location, Particle type, Object particleData, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		int[] data;
		if(type.getDataType().equals(ItemStack.class))
		{
			if(!(particleData instanceof ItemStack))
			{
				data = new int[]{0,0};
			}
			else
			{
				ItemStack its = (ItemStack) particleData;
				data = new int[] { its.getType().getId(), its.getDurability() };
			}
		}
		else if(type.getDataType().equals(MaterialData.class))
		{
			if(particleData == null || (!(particleData instanceof MaterialData) && !(particleData instanceof ItemStack)))
			{
				data = new int[]{0};
			}
			else
			{
				MaterialData matData = (particleData instanceof MaterialData) ? (MaterialData) particleData : ((ItemStack) particleData).getData();
				data = new int[] { matData.getItemTypeId() + (((int)matData.getData()) << 12) };
			}
		}
		else data = new int[0];
		spawnParticle(location, type, visibleRange, count, offsetX, offsetY, offsetZ, speed, data);
	}

	protected void spawnParticle(Location location, double visibleRange, Object particlePacket) throws IllegalAccessException, InvocationTargetException
	{
		if(particlePacket == null) return;
		for(Entity entity : location.getWorld().getEntities())
		{
			if(entity instanceof Player && entity.getLocation().getWorld().getName().equalsIgnoreCase(location.getWorld().getName()) && entity.getLocation().distance(location) < visibleRange)
			{
				Utils.sendPacket((Player)entity, particlePacket);
			}
		}
	}

	protected abstract void spawnParticle(Location location, Particle type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed, int[] data);
}