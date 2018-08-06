/*
 *   Copyright (C) 2016, 2017 GeorgH93
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

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * API to spawn particles.
 * This particle API is compatible with MC versions 1.7 to 1.12.
 * Bukkit has a particle api build in now, please use that if you don't need support for very old MC versions!
 */
@Deprecated
public abstract class ParticleSpawner
{
	/*
	 * Spawns a single material based particle.
	 * Only for Minecraft 1.8 and newer!
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param particleData the data (material) of the particle
	 * @param visibleRange the range that the effect will be visible
	 */
	/*public void spawnParticle(Location location, Particle type, Object particleData, double visibleRange)
	{
		spawnParticle(location, type, particleData, visibleRange, 1, 0, 0, 0, 0);
	}*/

	/**
	 * Spawns a single material based particle.
	 * Only for Minecraft 1.8 and newer!
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param itemStack    the item of the particle
	 * @param visibleRange the range that the effect will be visible
	 */
	public void spawnParticle(Location location, Particle type, ItemStack itemStack, double visibleRange)
	{
		spawnParticle(location, type, itemStack, visibleRange, 1, 0, 0, 0, 0);
	}

	/**
	 * Spawns a single material based particle.
	 * Only for Minecraft 1.8 and newer!
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param materialData the material of the particle
	 * @param visibleRange the range that the effect will be visible
	 */
	public void spawnParticle(Location location, Particle type, MaterialData materialData, double visibleRange)
	{
		spawnParticle(location, type, materialData, visibleRange, 1, 0, 0, 0, 0);
	}

	/**
	 * Spawns a group of material based particles.
	 * Only for Minecraft 1.8 and newer!
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param particleData the data (material) of the particle, please use the right type for the particle (Particle.getDataType)
	 * @param visibleRange the range that the effect will be visible
	 * @param count        the amount of particles that should be spawned
	 * @param offsetX      is added to the X position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param offsetY      is added to the Y position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param offsetZ      is added to the Z position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param speed        the speed the particles are moving with
	 */
	@SuppressWarnings("deprecation")
	public void spawnParticle(Location location, Particle type, Object particleData, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		int[] data;
		if(type.getDataType().equals(ItemStack.class))
		{
			if(particleData == null || !(particleData instanceof ItemStack))
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

	protected abstract void spawnParticle(Location location, Particle type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed, int[] data);

	/**
	 * Spawns a single particle.
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param visibleRange the range that the effect will be visible
	 */
	public void spawnParticle(Location location, Particle type, double visibleRange)
	{
		spawnParticle(location, type, visibleRange, 1, 0, 0, 0, 0);
	}

	/**
	 * Spawns a group of particles.
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param visibleRange the range that the effect will be visible
	 * @param count        the amount of particles that should be spawned
	 * @param offsetX      is added to the X position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param offsetY      is added to the Y position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param offsetZ      is added to the Z position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param speed        the speed the particles are moving with
	 */
	public abstract void spawnParticle(Location location, Particle type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed);

	/**
	 * It is recommended to use this function to get an effect spawner instance! It will give you the right object for your Minecraft version.
	 * If it returns null your Minecraft version is not compatible! Like version &lt; 1.7 and some modded servers.
	 *
	 * @return A for your Minecraft version compatible effect spawner. null if your Minecraft version is not supported.
	 */
	public static ParticleSpawner getParticleSpawner()
	{
		if (MCVersion.isOlderThan(MCVersion.MC_1_8))
		{
			return new ParticleSpawnerBukkit_1_7();
		}
		else if (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_8))
		{
			return new ParticleSpawnerBukkit_1_8_AndNewer();
		}
		else
		{
			return null;
		}
	}
}