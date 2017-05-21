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
import at.pcgamingfreaks.TestClasses.NMS.EntityPlayer;
import at.pcgamingfreaks.TestClasses.NMS.IChatBaseComponent;
import at.pcgamingfreaks.TestClasses.NMS.PacketPlayOutChat;
import at.pcgamingfreaks.TestClasses.NMS.PlayerConnection;
import at.pcgamingfreaks.TestClasses.TestBukkitPlayer;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;

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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bukkit.class, NMSReflection.class, PlayerConnection.class, PluginDescriptionFile.class, Reflection.class })
public class UtilsTest
{
	@SuppressWarnings("SpellCheckingInspection")
	private static TestBukkitServer server = new TestBukkitServer();

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		server.allowPluginManager = true;
		Bukkit.setServer(server);
		TestObjects.initNMSReflection();
		TestUtils.initReflection();
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
}
