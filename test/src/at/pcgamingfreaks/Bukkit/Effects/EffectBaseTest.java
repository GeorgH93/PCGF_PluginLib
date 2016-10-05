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

package at.pcgamingfreaks.Bukkit.Effects;

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.Utils;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
public class EffectBaseTest
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
	public void testSpawnParticle() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
	{
		World mockedWorld = mock(World.class);
		Location mockedLocation = spy(new Location(mockedWorld, 10.0, 20.0, 30.0));
		EffectBase effect = new EffectBukkit_1_7();
		Method spawnParticle = EffectBase.class.getDeclaredMethod("spawnParticle", Location.class, MaterialEffects.class, Material.class, double.class);
		spawnParticle.invoke(effect, mockedLocation, MaterialEffects.ITEM_CRACK, Material.ACACIA_DOOR, 100.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.invoke(effect, mockedLocation, MaterialEffects.BLOCK_DUST, Material.REDSTONE_COMPARATOR_ON, 300.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.invoke(effect, mockedLocation, MaterialEffects.FALLING_DUST, Material.ACTIVATOR_RAIL, 200.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		EffectBase.class.getDeclaredMethod("spawnParticle", Location.class, MaterialEffects.class, Material.class, int.class, double.class).invoke(effect, mockedLocation, MaterialEffects.BLOCK_CRACK, Material.DIAMOND, 5, 100.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		EffectBase.class.getDeclaredMethod("spawnParticle", Location.class, Effects.class, double.class).invoke(effect, mockedLocation, Effects.DRIP_LAVA, 20.0);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection")
	public void testGetEffectSpawner() throws NoSuchFieldException, IllegalAccessException
	{
		TestObjects.setBukkitVersion("0_7_R1");
		assertNull("The effect object should be null", EffectBase.getEffectSpawner());
		TestObjects.setBukkitVersion("1_7_R1");
		//noinspection ConstantConditions
		assertEquals("The effect class should be correct", EffectBukkit_1_7.class, EffectBase.getEffectSpawner().getClass());
		TestObjects.setBukkitVersion("1_9_R1");
		//noinspection ConstantConditions
		assertEquals("The effect class should be correct", EffectBukkit_1_8_AND_NEWER.class, EffectBase.getEffectSpawner().getClass());
	}
}