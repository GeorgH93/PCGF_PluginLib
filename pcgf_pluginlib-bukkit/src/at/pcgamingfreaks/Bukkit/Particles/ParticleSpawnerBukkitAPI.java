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

import org.bukkit.Location;

import java.util.EnumMap;
import java.util.Map;

final class ParticleSpawnerBukkitAPI extends ParticleSpawner
{
	private static final Map<Particle, org.bukkit.Particle> PARTICLE_MAP = new EnumMap<>(Particle.class);

	static
	{
		for(Particle particle : Particle.values())
		{
			try
			{
				PARTICLE_MAP.put(particle, org.bukkit.Particle.valueOf(particle.getName()));
			}
			catch(IllegalArgumentException ignored) {}
		}
	}

	@Override
	public void spawnParticle(Location location, Particle type, Object particleData, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		org.bukkit.Particle particle = PARTICLE_MAP.get(type);
		if(particle == null) return;
		location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed, particleData);
	}

	@Override
	public void spawnParticle(Location location, Particle type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		org.bukkit.Particle particle = PARTICLE_MAP.get(type);
		if(particle == null) return;
		location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
	}
}