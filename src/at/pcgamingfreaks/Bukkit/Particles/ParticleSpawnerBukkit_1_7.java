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

package at.pcgamingfreaks.Bukkit.Particles;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NMSReflection;

import org.apache.commons.lang3.Validate;
import org.bukkit.Location;

import java.lang.reflect.Constructor;

class ParticleSpawnerBukkit_1_7 extends ParticleSpawnerBukkit
{
	private static final Constructor PACKET_CONSTRUCTOR = NMSReflection.getConstructor(NMSReflection.getNMSClass("PacketPlayOutWorldParticles"), String.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class);

	@Override
	protected void spawnParticle(Location location, Particle type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed, int[] data) {}

	public void spawnParticle(Location location, Particle type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		Validate.isTrue(type.getMinVersion().olderOrEqualThan(MCVersion.MC_1_7_10), "The %s particle is not available in your minecraft version!", type.getName());
		try
		{
			//noinspection ConstantConditions
			spawnParticle(location, visibleRange, PACKET_CONSTRUCTOR.newInstance(type.getOldName(), (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count));
		}
		catch(Exception e)
		{
			System.out.println("Unable to spawn particle " + type.getOldName() + ". (Version 1.7)");
			e.printStackTrace();
		}
	}
}