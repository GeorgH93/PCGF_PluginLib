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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class ParticleSpawner_ReflectionTest
{
	IUtils mockedUtils = null;

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
	}

	@Before
	public void prepareTestObjects() throws NoSuchFieldException, IllegalAccessException
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
	public void testSpawnParticle() throws IllegalAccessException, NoSuchFieldException
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
		ParticleSpawner_Reflection effect = new ParticleSpawner_Reflection();
		effect.spawnParticle(mockedLocation, Particle.CLOUD, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f);
		verify(mockedUtils, times(++sendPacketCalls)).sendPacket(any(TestBukkitPlayer.class), any());
		effect.spawnParticle(mockedLocation, Particle.BLOCK_CRACK, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f, new int[] { 0 });
		verify(mockedUtils, times(++sendPacketCalls)).sendPacket(any(TestBukkitPlayer.class), any());
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		Field packetConstructorField = ParticleSpawner_Reflection.class.getDeclaredField("PACKET_CONSTRUCTOR");
		packetConstructorField.setAccessible(true);
		modifiers.set(packetConstructorField, packetConstructorField.getModifiers() & ~Modifier.FINAL);
		Constructor<?> packetConstructor = (Constructor<?>) packetConstructorField.get(null);
		packetConstructorField.set(null, null);
		effect.spawnParticle(mockedLocation, Particle.CLOUD, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f);
		verify(mockedUtils, times(sendPacketCalls)).sendPacket(any(TestBukkitPlayer.class), any());
		effect.spawnParticle(mockedLocation, Particle.BLOCK_CRACK, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f, new int[] { 0 });
		verify(mockedUtils, times(sendPacketCalls)).sendPacket(any(TestBukkitPlayer.class), any());
		packetConstructorField.set(null, packetConstructor);
		modifiers.set(packetConstructorField, packetConstructorField.getModifiers() | Modifier.FINAL);
		packetConstructorField.setAccessible(false);
		modifiers.setAccessible(false);
	}
}