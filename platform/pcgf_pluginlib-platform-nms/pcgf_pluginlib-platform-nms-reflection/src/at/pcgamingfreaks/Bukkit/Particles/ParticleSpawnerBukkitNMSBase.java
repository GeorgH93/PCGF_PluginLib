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

package at.pcgamingfreaks.Bukkit.Particles;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.Util.IUtils;
import at.pcgamingfreaks.Reflection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

abstract class ParticleSpawnerBukkitNMSBase extends ParticleSpawner
{
	private static final Method METHOD_MATERIAL_DATA_GET_ITEM_TYPE_ID = MCVersion.isOlderThan(MCVersion.MC_1_13) ? Reflection.getMethod(MaterialData.class, "getItemTypeId") : null;

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
			if((!(particleData instanceof MaterialData) && !(particleData instanceof ItemStack)))
			{
				data = new int[]{0};
			}
			else
			{
				MaterialData matData = (particleData instanceof MaterialData) ? (MaterialData) particleData : ((ItemStack) particleData).getData();
				if(METHOD_MATERIAL_DATA_GET_ITEM_TYPE_ID != null)
				{
					try
					{
						data = new int[] { (int) METHOD_MATERIAL_DATA_GET_ITEM_TYPE_ID.invoke(matData) + (((int) matData.getData()) << 12) };
					}
					catch(IllegalAccessException | InvocationTargetException e)
					{
						data = new int[] { 0 };
						e.printStackTrace();
					}
				}
				else data = new int[] { 0 };
			}
		}
		else data = new int[0];
		spawnParticle(location, type, visibleRange, count, offsetX, offsetY, offsetZ, speed, data);
	}

	protected void spawnParticle(Location location, double visibleRange, Object particlePacket) throws IllegalAccessException, InvocationTargetException
	{
		if(particlePacket == null) return;
		double visibleRangeSquared = visibleRange * visibleRange;
		String worldName = location.getWorld().getName();
		for(Player player : location.getWorld().getPlayers())
		{
			// Getting entitys from a world sometimes returns players which are not actually in that world, so they need to be verified. Also checking world.equals(otherWorld) is sometimes not reliable (on MC 1.11).
			if(player.getLocation().getWorld().getName().equalsIgnoreCase(worldName) && player.getLocation().distanceSquared(location) < visibleRangeSquared)
			{
				IUtils.INSTANCE.sendPacket(player, particlePacket);
			}
		}
	}

	protected abstract void spawnParticle(Location location, Particle type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed, int[] data);
}