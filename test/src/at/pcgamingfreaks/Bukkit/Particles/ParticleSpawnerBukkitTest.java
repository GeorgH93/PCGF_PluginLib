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
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class, Utils.class })
@SuppressWarnings("SpellCheckingInspection")
public class ParticleSpawnerBukkitTest
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
	public void testSpawnParticle() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException
	{
		ParticleSpawnerBukkit effect = new ParticleSpawnerBukkit_1_7();
		Method spawnParticle = ParticleSpawnerBukkit.class.getDeclaredMethod("spawnParticle", Location.class, double.class, Object.class);
		spawnParticle.setAccessible(true);
		spawnParticle.invoke(effect, null, 0.0, null);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
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
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		doReturn(mockedLocation).when(mockedPlayer).getLocation();
		spawnParticle.invoke(effect, mockedLocation, -10.0, Particle.CLOUD);
		verifyStatic(times(0));
		Utils.sendPacket(any(Player.class), anyObject());
		spawnParticle.setAccessible(false);
	}
}