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
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Reflection;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import java.lang.reflect.Constructor;
import java.util.EnumMap;
import java.util.Map;

final class ParticleSpawner_Reflection extends ParticleSpawnerBukkitNMSBase
{
	private static final Map<Particle, Enum<?>> PARTICLE_MAP = new EnumMap<>(Particle.class);

	static
	{
		for(Particle particle : Particle.values())
		{
			if(MCVersion.isNewerOrEqualThan(particle.getMinVersion()))
				PARTICLE_MAP.put(particle, NmsReflector.INSTANCE.getNmsEnum("EnumParticle", particle.getName()));
		}
	}

	private static final Constructor<?> PACKET_CONSTRUCTOR = Reflection.getConstructor(NmsReflector.INSTANCE.getNmsClass("PacketPlayOutWorldParticles"), NmsReflector.INSTANCE.getNmsClass("EnumParticle"), boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);

	@Override
	public void spawnParticle(Location location, Particle particle, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		spawnParticle(location, particle, visibleRange, count, offsetX, offsetY, offsetZ, speed, new int[0]);
	}

	@Override
	protected void spawnParticle(Location location, Particle particle, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed, int[] data)
	{
		Validate.isTrue(MCVersion.isNewerOrEqualThan(particle.getMinVersion()), "The %s particle is not available in your minecraft version!", particle.getName());
		try
		{
			//noinspection ConstantConditions
			spawnParticle(location, visibleRange, PACKET_CONSTRUCTOR.newInstance(PARTICLE_MAP.get(particle), false, (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count, data));
		}
		catch(Exception e)
		{
			System.out.println("Unable to spawn particle " + particle.getOldName() + ". (Version 1.8 - 1.12)");
			e.printStackTrace();
		}
	}
}