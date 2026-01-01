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
import at.pcgamingfreaks.TestClasses.TestBukkitPlayer;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import sun.misc.Unsafe;

import static org.mockito.Mockito.*;

public class ParticleSpawner_ReflectionTest
{
	private static boolean skipTests = false; // set in prepareTestData
	IUtils mockedUtils = null;

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		skipTests = !TestUtils.canMockJdkClasses();
		if (!skipTests)
		{
			Bukkit.setServer(new TestBukkitServer());
			TestObjects.initNMSReflection();
		}
	}

	@Before
	public void prepareTestObjects() throws Exception
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		mockedUtils = mock(IUtils.class);
		doNothing().when(mockedUtils).sendPacket(any(Player.class), any());
		TestUtils.setFieldValue(null, IUtils.class.getDeclaredField("INSTANCE"), mockedUtils);
	}

	@After
	public void cleanupTestObjects() throws NoSuchFieldException, IllegalAccessException
	{
		mockedUtils = null;
		TestUtils.setFieldValue(null, IUtils.class.getDeclaredField("INSTANCE"), null);
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
		// Use Unsafe to set the static final field PACKET_CONSTRUCTOR to null
		Field packetConstructorField = ParticleSpawner_Reflection.class.getDeclaredField("PACKET_CONSTRUCTOR");
		Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
		unsafeField.setAccessible(true);
		Unsafe unsafe = (Unsafe) unsafeField.get(null);
		Object base = unsafe.staticFieldBase(packetConstructorField);
		long offset = unsafe.staticFieldOffset(packetConstructorField);
		Constructor<?> packetConstructor = (Constructor<?>) unsafe.getObject(base, offset);
		unsafe.putObject(base, offset, null);
		effect.spawnParticle(mockedLocation, Particle.CLOUD, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f);
		verify(mockedUtils, times(sendPacketCalls)).sendPacket(any(TestBukkitPlayer.class), any());
		effect.spawnParticle(mockedLocation, Particle.BLOCK_CRACK, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f, new int[] { 0 });
		verify(mockedUtils, times(sendPacketCalls)).sendPacket(any(TestBukkitPlayer.class), any());
		unsafe.putObject(base, offset, packetConstructor);
		unsafeField.setAccessible(false);
	}
}