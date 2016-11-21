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
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class, Utils.class })
@SuppressWarnings("SpellCheckingInspection")
public class ParticleSpawnerBukkit_1_8_AndNewerTest
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
	public void testSpawnParticle() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException
	{
		int sendPacketCalls = 0;
		World mockedWorld = mock(World.class);
		doReturn("World1").when(mockedWorld).getName();
		Location mockedLocation = spy(new Location(mockedWorld, 10.0, 20.0, 30.0));
		List<Entity> entities = new ArrayList<>();
		TestBukkitPlayer mockedPlayer = spy(new TestBukkitPlayer());
		doCallRealMethod().when(mockedPlayer).getHandle();
		doReturn(mockedLocation).when(mockedPlayer).getLocation();
		entities.add(mockedPlayer);
		Entity mockedEntity = mock(Entity.class);
		entities.add(mockedEntity);
		doReturn(entities).when(mockedWorld).getEntities();
		ParticleSpawnerBukkit_1_8_AndNewer effect = new ParticleSpawnerBukkit_1_8_AndNewer();
		effect.spawnParticle(mockedLocation, Particle.CLOUD, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f);
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(TestBukkitPlayer.class), anyObject());
		effect.spawnParticle(mockedLocation, Particle.BLOCK_CRACK, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f, new int[] { 0 });
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(TestBukkitPlayer.class), anyObject());
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		Field packetConstructorField = ParticleSpawnerBukkit_1_8_AndNewer.class.getDeclaredField("PACKET_CONSTRUCTOR");
		packetConstructorField.setAccessible(true);
		modifiers.set(packetConstructorField, packetConstructorField.getModifiers() & ~Modifier.FINAL);
		Constructor packetConstructor = (Constructor) packetConstructorField.get(null);
		packetConstructorField.set(null, null);
		effect.spawnParticle(mockedLocation, Particle.CLOUD, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f);
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(TestBukkitPlayer.class), anyObject());
		effect.spawnParticle(mockedLocation, Particle.BLOCK_CRACK, 100.0, 4000, 10.0f, 10.0f, 10.0f, 1.0f, new int[] { 0 });
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(TestBukkitPlayer.class), anyObject());
		packetConstructorField.set(null, packetConstructor);
		modifiers.set(packetConstructorField, packetConstructorField.getModifiers() | Modifier.FINAL);
		packetConstructorField.setAccessible(false);
		modifiers.setAccessible(false);
	}
}