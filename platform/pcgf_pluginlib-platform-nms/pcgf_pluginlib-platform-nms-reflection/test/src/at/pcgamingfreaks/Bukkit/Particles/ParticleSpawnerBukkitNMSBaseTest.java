/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Particles;

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.Util.IUtils;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.TestClasses.TestBukkitPlayer;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class ParticleSpawnerBukkitNMSBaseTest
{
	IUtils mockedUtils = null;

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
	}

	@Before
	public void prepareTestObjects() throws Exception
	{
		mockedUtils = mock(IUtils.class);
		doNothing().when(mockedUtils).sendPacket(any(Player.class), any());
		Reflection.setFinalField(IUtils.class.getDeclaredField("INSTANCE"), null, mockedUtils);
	}

	@After
	public void cleanupTestObjects() throws NoSuchFieldException, IllegalAccessException
	{
		mockedUtils = null;
		Reflection.setFinalField(IUtils.class.getDeclaredField("INSTANCE"), null, null);
	}

	@Test
	public void testSpawnParticle() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
	{
		ParticleSpawnerBukkitNMSBase effect = new ParticleSpawner_Reflection_1_7();
		Method spawnParticle = ParticleSpawnerBukkitNMSBase.class.getDeclaredMethod("spawnParticle", Location.class, double.class, Object.class);
		spawnParticle.setAccessible(true);
		spawnParticle.invoke(effect, null, 0.0, null);
		verify(mockedUtils, times(0)).sendPacket(any(Player.class), any());
		List<Entity> players = new ArrayList<>();
		Location mockedLocation = mock(Location.class);
		World mockedWorld = mock(World.class);
		doReturn("World1").when(mockedWorld).getName();
		doReturn(players).when(mockedWorld).getEntities();
		doReturn(mockedWorld).when(mockedLocation).getWorld();
		TestBukkitPlayer mockedPlayer = spy(new TestBukkitPlayer());
		Location mockedLocation2 = mock(Location.class);
		World mockedWorld2 = mock(World.class);
		doReturn("World2").when(mockedWorld2).getName();
		doReturn(mockedWorld2).when(mockedLocation2).getWorld();
		doReturn(mockedLocation2).when(mockedPlayer).getLocation();
		players.add(mockedPlayer);
		spawnParticle.invoke(effect, mockedLocation, 10.0, Particle.CLOUD);
		verify(mockedUtils, times(0)).sendPacket(any(Player.class), any());
		doReturn(mockedLocation).when(mockedPlayer).getLocation();
		spawnParticle.invoke(effect, mockedLocation, -10.0, Particle.CLOUD);
		verify(mockedUtils, times(0)).sendPacket(any(Player.class), any());
		spawnParticle.setAccessible(false);
	}
}