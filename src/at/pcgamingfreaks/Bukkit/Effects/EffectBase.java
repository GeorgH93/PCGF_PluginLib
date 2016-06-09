/*
 *   Copyright (C) 2014-2016 GeorgH93
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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public abstract class EffectBase
{
	/**
	 * Spawns a single material based particle.
	 * Only for Minecraft 1.8 and newer!
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param material     the material of the particle
	 * @param visibleRange the range that the effect will be visible
	 */
	public void spawnParticle(Location location, MaterialEffects type, Material material, double visibleRange)
	{
		spawnParticle(location, type, material, visibleRange, 1, 0, 0, 0, 0);
	}

	/**
	 * Spawns a group of material based particles.
	 * Only for Minecraft 1.8 and newer!
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param material     the material of the particle
	 * @param visibleRange the range that the effect will be visible
	 * @param count        the amount of particles that should be spawned
	 * @param offsetX      is added to the X position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param offsetY      is added to the Y position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param offsetZ      is added to the Z position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param speed        the speed the particles are moving with
	 */
	public void spawnParticle(Location location, MaterialEffects type, Material material, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		spawnParticle(location, type, material, 0, visibleRange, count, offsetX, offsetY, offsetZ, speed);
	}

	/**
	 * Spawns a single material based particle.
	 * Only for Minecraft 1.8 and newer!
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param material     the material of the particle
	 * @param materialData the material data of the particle
	 * @param visibleRange the range that the effect will be visible
	 */
	public void spawnParticle(Location location, MaterialEffects type, Material material, int materialData, double visibleRange)
	{
		spawnParticle(location, type, material, materialData, visibleRange, 1, 0, 0, 0, 0);
	}

	/**
	 * Spawns a group of material based particles.
	 * Only for Minecraft 1.8 and newer!
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param material     the material of the particle
	 * @param materialData the material data of the particle
	 * @param visibleRange the range that the effect will be visible
	 * @param count        the amount of particles that should be spawned
	 * @param offsetX      is added to the X position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param offsetY      is added to the Y position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param offsetZ      is added to the Z position after being multiplied by random.nextGaussian() to spread the particles out
	 * @param speed        the speed the particles are moving with
	 */
	@SuppressWarnings("deprecation")
	public void spawnParticle(Location location, MaterialEffects type, Material material, int materialData, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		int[] data = null;
		switch(type)
		{
			case ITEM_CRACK:
				data = new int[]{material.getId(), materialData};
				break;
			case BLOCK_CRACK:
				data = new int[]{material.getId() + 4096 * materialData};
				break;
			case BLOCK_DUST:
				data = new int[]{material.getId()};
				break;
		}
		spawnParticle(location, type, visibleRange, count, offsetX, offsetY, offsetZ, speed, data);
	}

	protected void spawnParticle(Location location, MaterialEffects type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed, int[] data) {}

	/**
	 * Spawns a single particle.
	 *
	 * @param location     the location where the effect should be spawned
	 * @param type         the type of the effect that should be spawned
	 * @param visibleRange the range that the effect will be visible
	 */
	public void spawnParticle(Location location, Effects type, double visibleRange)
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
	public abstract void spawnParticle(Location location, Effects type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed);

	/**
	 * It is recommended to use this function to get an effect spawner instance! It will give you the right object for your Minecraft version.
	 * If it returns null your Minecraft version is not compatible! Like version < 1.7 and some modded servers.
	 *
	 * @return A for your Minecraft version compatible effect spawner. null if your Minecraft version is not supported.
	 */
	public static EffectBase getEffectSpawner()
	{
		EffectBase eb = null;
		String name = Bukkit.getServer().getClass().getPackage().getName();
		String[] version = name.substring(name.lastIndexOf('.') + 2).split("_");
		try
		{
			if(version[0].equals("1"))
			{
				if(version[1].equals("7"))
				{
					eb = new EffectBukkit_1_7();
				}
				else if(version[1].equals("8") || version[1].equals("9") || version[1].equals("10"))
				{
					eb = new EffectBukkit_1_8_AND_NEWER();
				}
			}
		}
		catch(NoClassDefFoundError | Exception e)
		{
			e.printStackTrace();
			eb = null;
		}
		if(eb == null)
		{
			Bukkit.getServer().getLogger().warning("Could not initialize effect spawner. Running: " + name + ":" + Bukkit.getVersion());
		}
		return eb;
	}
}