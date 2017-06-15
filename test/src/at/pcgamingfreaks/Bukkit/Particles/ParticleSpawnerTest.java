/*
 * Copyright (C) 2016 MarkusWME
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Particles;

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.Utils;
import at.pcgamingfreaks.TestClasses.TestBukkitPlayer;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class, Utils.class })
public class ParticleSpawnerTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
	}

	@Before
	public void prepareTestObjects() throws Exception
	{
		mockStatic(Utils.class);
		doNothing().when(Utils.class, "sendPacket", any(Player.class), anyObject());
	}

	@Test
	public void testSpawnParticle() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException
	{
		World mockedWorld = mock(World.class);
		Location mockedLocation = spy(new Location(mockedWorld, 10.0, 20.0, 30.0));
		ParticleSpawner effect = new ParticleSpawnerBukkit_1_7();
		Method spawnParticle = ParticleSpawner.class.getDeclaredMethod("spawnParticle", Location.class, Particle.class, MaterialData.class, double.class);
		spawnParticle.invoke(effect, mockedLocation, Particle.ITEM_CRACK, new MaterialData(Material.ACACIA_DOOR), 100.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.invoke(effect, mockedLocation, Particle.BLOCK_DUST, new MaterialData(Material.REDSTONE_COMPARATOR_ON), 300.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.invoke(effect, mockedLocation, Particle.FALLING_DUST, new MaterialData(Material.ACTIVATOR_RAIL), 200.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.invoke(effect, mockedLocation, Particle.EXPLOSION, new MaterialData(Material.TNT), 10.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle = ParticleSpawner.class.getDeclaredMethod("spawnParticle", Location.class, Particle.class, double.class);
		spawnParticle.invoke(effect, mockedLocation, Particle.SLIME, 10.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle = ParticleSpawner.class.getDeclaredMethod("spawnParticle", Location.class, Particle.class, ItemStack.class, double.class);
		spawnParticle.invoke(effect, mockedLocation, Particle.ITEM_CRACK, new ItemStack(Material.ACTIVATOR_RAIL, 10), 10.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle = ParticleSpawner.class.getDeclaredMethod("spawnParticle", Location.class, Particle.class, Object.class, double.class, int.class, float.class, float.class, float.class, float.class);
		spawnParticle.invoke(effect, mockedLocation, Particle.ITEM_CRACK, null, 10.0, 100, 10.0f, 10.0f, 10.0f, 10.0f);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.invoke(effect, mockedLocation, Particle.FALLING_DUST, null, 10.0, 100, 10.0f, 10.0f, 10.0f, 10.0f);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.invoke(effect, mockedLocation, Particle.FALLING_DUST, new MaterialData(Material.TNT), 10.0, 100, 10.0f, 10.0f, 10.0f, 10.0f);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.invoke(effect, mockedLocation, Particle.FALLING_DUST, new ItemStack(Material.TNT, 20), 10.0, 100, 10.0f, 10.0f, 10.0f, 10.0f);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.invoke(effect, mockedLocation, Particle.FALLING_DUST, new TestBukkitPlayer(), 10.0, 100, 10.0f, 10.0f, 10.0f, 10.0f);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection")
	public void testGetParticleSpawner() throws NoSuchFieldException, IllegalAccessException
	{
		TestObjects.setBukkitVersion("0_7_R1");
		assertNull("The effect object should be null", ParticleSpawner.getParticleSpawner());
		TestObjects.setBukkitVersion("1_7_R1");
		//noinspection ConstantConditions
		assertEquals("The effect class should be correct", ParticleSpawnerBukkit_1_7.class, ParticleSpawner.getParticleSpawner().getClass());
		TestObjects.setBukkitVersion("1_9_R1");
		//noinspection ConstantConditions
		assertEquals("The effect class should be correct", ParticleSpawnerBukkit_1_8_AndNewer.class, ParticleSpawner.getParticleSpawner().getClass());
	}
}