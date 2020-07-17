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
import at.pcgamingfreaks.Bukkit.Util.Utils;
import at.pcgamingfreaks.TestClasses.TestBukkitPlayer;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class, Utils.class })
public class ParticleSpawnerBukkit_1_7Test
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
		doNothing().when(Utils.class, "sendPacket", any(Player.class), any());
	}

	@Test
	public void testSpawnParticle() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException
	{
		int sendPacketCalls = 0;
		World mockedWorld = mock(World.class);
		doReturn("World1").when(mockedWorld).getName();
		Location mockedLocation = spy(new Location(mockedWorld, 10.0, 20.0, 30.0));
		List<Player> players = new ArrayList<>();
		TestBukkitPlayer mockedPlayer = spy(new TestBukkitPlayer());
		doCallRealMethod().when(mockedPlayer).getHandle();
		doReturn(mockedLocation).when(mockedPlayer).getLocation();
		players.add(mockedPlayer);
		doReturn(players).when(mockedWorld).getPlayers();
		ParticleSpawnerBukkit_1_7 effect = new ParticleSpawnerBukkit_1_7();
		effect.spawnParticle(mockedLocation, Particle.CLOUD, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f);
		verifyStatic(Utils.class, times(++sendPacketCalls));
		Utils.sendPacket(any(TestBukkitPlayer.class), any());
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		Field packetConstructorField = ParticleSpawnerBukkit_1_7.class.getDeclaredField("PACKET_CONSTRUCTOR");
		packetConstructorField.setAccessible(true);
		modifiers.set(packetConstructorField, packetConstructorField.getModifiers() & ~Modifier.FINAL);
		Constructor<?> packetConstructor = (Constructor<?>) packetConstructorField.get(null);
		packetConstructorField.set(null, null);
		effect.spawnParticle(mockedLocation, Particle.CLOUD, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f);
		verifyStatic(Utils.class, times(sendPacketCalls));
		Utils.sendPacket(any(TestBukkitPlayer.class), any());
		packetConstructorField.set(null, packetConstructor);
		modifiers.set(packetConstructorField, packetConstructorField.getModifiers() | Modifier.FINAL);
		packetConstructorField.setAccessible(false);
		modifiers.setAccessible(false);
	}

	@Test
	public void testProtectedSpawnParticle() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		ParticleSpawnerBukkit_1_7 particleSpawner = new ParticleSpawnerBukkit_1_7();
		Method spawnParticle = ParticleSpawnerBukkit_1_7.class.getDeclaredMethod("spawnParticle", Location.class, Particle.class, double.class, int.class, float.class, float.class, float.class, float.class, int[].class);
		spawnParticle.setAccessible(true);
		spawnParticle.invoke(particleSpawner, null, null, 0.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f, null);
		spawnParticle.setAccessible(false);
	}
}