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

import at.pcgamingfreaks.Bukkit.Reflection;

import org.bukkit.Location;

import java.lang.reflect.Constructor;

public class EffectBukkit_1_8 extends EffectBukkit
{
	private final Constructor packetConstructor;

	public EffectBukkit_1_8() throws NoSuchMethodException, NullPointerException
	{
		//noinspection ConstantConditions
		packetConstructor = Reflection.getNMSClass("PacketPlayOutWorldParticles").getConstructor(Reflection.getNMSClass("EnumParticle"), boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
	}

	@Override
	public void spawnParticle(Location location, Effects type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed)
	{
		try
		{
			spawnParticle(location, visibleRange, packetConstructor.newInstance(type.getEnum(), false, (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count, new int[]{}));
		}
		catch(Exception e)
		{
			System.out.println("Unable to spawn particle " + type.getName() + ". (Version 1.8)");
			e.printStackTrace();
		}
	}

	@Override
	protected void spawnParticle(Location location, MaterialEffects type, double visibleRange, int count, float offsetX, float offsetY, float offsetZ, float speed, int[] data)
	{
		try
		{
			Object packet = packetConstructor.newInstance(type.getEnum(), false, (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count, data);
			spawnParticle(location, visibleRange, packet);
		}
		catch(Exception e)
		{
			System.out.println("Unable to spawn particle " + type.getName() + ". (Version 1.8)");
			e.printStackTrace();
		}
	}
}