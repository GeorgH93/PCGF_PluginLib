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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.TestClasses.*;
import at.pcgamingfreaks.TestClasses.NMS.EntityPlayer;
import at.pcgamingfreaks.TestClasses.NMS.IChatBaseComponent;
import at.pcgamingfreaks.TestClasses.NMS.PacketPlayOutChat;
import at.pcgamingfreaks.TestClasses.NMS.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bukkit.class, NMSReflection.class, PlayerConnection.class, PluginDescriptionFile.class, Reflection.class })
public class UtilsTest
{
	@SuppressWarnings("SpellCheckingInspection")
	private static TestBukkitServer server = new TestBukkitServer();
	private static final World WORLD_1 = Mockito.mock(World.class), WORLD_2 = Mockito.mock(World.class);
	private static final Player PLAYER1 = Mockito.mock(Player.class), PLAYER2 = Mockito.mock(Player.class), PLAYER3 = Mockito.mock(Player.class), PLAYER4 = Mockito.mock(Player.class);

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		server.allowPluginManager = true;
		Bukkit.setServer(server);
		TestObjects.initNMSReflection();
		TestUtils.initReflection();
		doReturn("Lobby").when(WORLD_1).getName();
		doReturn("Survival").when(WORLD_2).getName();
		doReturn(false).when(PLAYER1).hasPermission("bypass.rangelimit");
		doReturn(false).when(PLAYER2).hasPermission("bypass.rangelimit");
		doReturn(false).when(PLAYER3).hasPermission("bypass.rangelimit");
		doReturn(true).when(PLAYER4).hasPermission("bypass.rangelimit");
		doReturn(WORLD_1).when(PLAYER1).getWorld();
		doReturn(WORLD_1).when(PLAYER2).getWorld();
		doReturn(WORLD_2).when(PLAYER3).getWorld();
		doReturn(WORLD_2).when(PLAYER4).getWorld();
		Location locationPlayer1 = new Location(WORLD_1, 100, 200, 300);
		Location locationPlayer2 = new Location(WORLD_1, 300, 200, 300);
		Location locationPlayer3 = new Location(WORLD_2, 300, 200, 300);
		doReturn(locationPlayer1).when(PLAYER1).getLocation();
		doReturn(locationPlayer2).when(PLAYER2).getLocation();
		doReturn(locationPlayer3).when(PLAYER3).getLocation();
		doReturn(locationPlayer3).when(PLAYER4).getLocation();
	}

	@Before
	public void prepareTestObjects()
	{
		new Utils();
	}

	@Test
	public void testWarnIfPerWorldPluginInstalled()
	{
		Logger mockedLogger = spy(server.getLogger());
		Utils.warnIfPerWorldPluginsIsInstalled(mockedLogger);
		verify(mockedLogger, times(0)).warning(anyString());
		server.perWorldPlugins = true;
		Utils.warnIfPerWorldPluginsIsInstalled(mockedLogger, 0);
		verify(mockedLogger, times(8)).warning(anyString());
	}

	@Test
	public void testGetDistance()
	{
		Player mockedPlayer1 = mock(Player.class);
		Player mockedPlayer2 = mock(Player.class);
		World mockedWorld1 = mock(World.class);
		doReturn("World1").when(mockedWorld1).getName();
		World mockedWorld2 = mock(World.class);
		doReturn("World2").when(mockedWorld2).getName();
		Location mockedLocation = mock(Location.class);
		assertEquals("The distance should be 0 if the players are the same", 0.0, Utils.getDistance(mockedPlayer1, mockedPlayer1), 0.1);
		doReturn(mockedWorld1).when(mockedPlayer1).getWorld();
		doReturn(mockedWorld2).when(mockedPlayer2).getWorld();
		assertEquals("The distance should be positive infinity if the players are in different worlds", Double.POSITIVE_INFINITY, Utils.getDistance(mockedPlayer1, mockedPlayer2), 0.1);
		doReturn(mockedWorld1).when(mockedPlayer2).getWorld();
		doReturn(mockedLocation).when(mockedPlayer1).getLocation();
		doReturn(mockedLocation).when(mockedPlayer2).getLocation();
		doReturn(5.0).when(mockedLocation).distance(mockedLocation);
		assertEquals("The distance should be correct if the players are in the same world", 5.0, Utils.getDistance(mockedPlayer1, mockedPlayer2), 0.1);
	}

	@Test
	public void testConvertItemStackToJson() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException
	{
		Logger mockedLogger = spy(server.getLogger());
		ItemStack itemStack = new ItemStack(Material.STONE, 23);
		assertEquals("The converted ItemStack should match", "{\"id\":\"STONE\",\"Count\":\"23\"}", Utils.convertItemStackToJson(itemStack, mockedLogger));
		Field field = TestUtils.setAccessible(Utils.class, null, "NBT_TAG_COMPOUND_CLASS", Utils.class);
		assertEquals("The converted ItemStack should match", "", Utils.convertItemStackToJson(itemStack, mockedLogger));
		verify(mockedLogger, times(1)).log(any(Level.class), anyString(), any(Throwable.class));
		field.set(null, null);
		assertEquals("The converted ItemStack should match", "", Utils.convertItemStackToJson(itemStack, mockedLogger));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(Utils.class, null, "AS_NMS_COPY_METHOD", null);
		assertEquals("The converted ItemStack should match", "", Utils.convertItemStackToJson(itemStack, mockedLogger));
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(Utils.class, null, "SAVE_NMS_ITEM_STACK_METHOD", null);
		assertEquals("The converted ItemStack should match", "", Utils.convertItemStackToJson(itemStack, mockedLogger));
		TestUtils.setUnaccessible(field, null, true);
		verify(mockedLogger, times(3)).log(any(Level.class), anyString());
	}

	@Test
	public void testSendPacket() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException
	{
		int sendPacketCalls = 0;
		//noinspection SpellCheckingInspection
		TestBukkitPlayer player = spy(new TestBukkitPlayer());
		Utils.sendPacket(player, new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(""), (byte) 0));
		verify(player, times(++sendPacketCalls)).getHandle();
		doReturn(null).when(player).getHandle();
		Utils.sendPacket(player, new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(""), (byte) 0));
		verify(player, times(++sendPacketCalls)).getHandle();
		Field field = TestUtils.setAccessible(Utils.class, null, "SEND_PACKET", null);
		Utils.sendPacket(player, new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(""), (byte) 0));
		verify(player, times(sendPacketCalls)).getHandle();
		TestUtils.setUnaccessible(field, null, true);
		field = TestUtils.setAccessible(Utils.class, null, "PLAYER_CONNECTION", null);
		Utils.sendPacket(player, new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(""), (byte) 0));
		verify(player, times(sendPacketCalls)).getHandle();
		TestUtils.setUnaccessible(field, null, true);
		player = new TestBukkitPlayer();
		player.isEntityPlayerHandle = false;
		Utils.sendPacket(player, new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(""), (byte) 0));
		//verify(player, times(++sendPacketCalls)).getHandle();
		player.isEntityPlayerHandle = true;
	}

	@Test
	public void testGetPing() throws NoSuchFieldException, IllegalAccessException
	{
		TestBukkitPlayer player = spy(new TestBukkitPlayer());
		assertEquals("The Ping should match", 123, Utils.getPing(player));
		Field playerPingField = TestUtils.setAccessible(Utils.class, null, "PLAYER_PING", EntityPlayer.class.getDeclaredField("failPing"));
		assertEquals("The Ping value should not be able to be retrieved", -1, Utils.getPing(player));
		doReturn(null).when(player).getHandle();
		assertEquals("The Ping field should not be found", -1, Utils.getPing(player));
		doReturn("Test").when(player).getHandle();
		assertEquals("The Ping field should not be found", -1, Utils.getPing(player));
		playerPingField.set(null, null);
		assertEquals("The Ping field should not be found", -1, Utils.getPing(player));
		TestUtils.setUnaccessible(playerPingField, null, true);
	}

	@Test
	public void testInRange()
	{
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, -1.0));
		assertTrue(Utils.inRange(PLAYER1, PLAYER4, -1.0));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, 0));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, 300));
		assertFalse(Utils.inRange(PLAYER1, PLAYER4, 1.0));
		assertFalse(Utils.inRange(PLAYER1, PLAYER3, 1.0));
		assertFalse(Utils.inRange(PLAYER1, PLAYER2, 1.0));
		assertFalse(Utils.inRange(PLAYER1, PLAYER3, 0));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, -1.0, "bypass.rangelimit"));
		assertTrue(Utils.inRange(PLAYER1, PLAYER4, -1.0, "bypass.rangelimit"));
		assertTrue(Utils.inRange(PLAYER1, PLAYER4, 1.0, "bypass.rangelimit"));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, 0, "bypass.rangelimit"));
		assertTrue(Utils.inRange(PLAYER1, PLAYER2, 300, "bypass.rangelimit"));
		assertFalse(Utils.inRange(PLAYER1, PLAYER3, 1.0, "bypass.rangelimit"));
		assertFalse(Utils.inRange(PLAYER1, PLAYER2, 1.0, "bypass.rangelimit"));
		assertFalse(Utils.inRange(PLAYER1, PLAYER3, 0, "bypass.rangelimit"));
		assertTrue(Utils.inRange(PLAYER4, PLAYER3, 0, "bypass.rangelimit"));
	}

	@Test
	public void testDropInventory()
	{
		Utils.dropInventory(new TestInventory(), new Location(WORLD_1, 0.0, 1.0, 2.0));
		Utils.dropInventory(new TestInventory(), new Location(WORLD_1, 0.0, 1.0, 2.0), false);
	}
}