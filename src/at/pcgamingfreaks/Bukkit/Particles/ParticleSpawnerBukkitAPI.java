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

import org.bukkit.Location;

@Deprecated
class ParticleSpawnerBukkitAPI extends ParticleSpawner
{
	@Override
	public void spawnParticle(Location location, Particle type, Object particleData, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		location.getWorld().spawnParticle(org.bukkit.Particle.valueOf(type.getName()), location, count, offsetX, offsetY, offsetZ, speed, particleData);
	}

	@Override
	public void spawnParticle(Location location, Particle type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		location.getWorld().spawnParticle(org.bukkit.Particle.valueOf(type.getName()), location, count, offsetX, offsetY, offsetZ, speed);
	}
}