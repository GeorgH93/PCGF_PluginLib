/*
 *   Copyright (C) 2020 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.NMSReflection;
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
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bukkit.class, NMSReflection.class, PlayerConnection.class, PluginDescriptionFile.class, Reflection.class })
public class Utils_ReflectionTest
{
	private static final TestBukkitServer server = new TestBukkitServer();

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		server.allowPluginManager = true;
		Bukkit.setServer(server);
		TestObjects.initNMSReflection();
		TestUtils.initReflection();
	}

	@Test
	public void testSendPacket()
	{
		Utils_Reflection utils = new Utils_Reflection();
		int sendPacketCalls = 0;
		TestBukkitPlayer player = spy(new TestBukkitPlayer());
		utils.sendPacket(player, new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(""), (byte) 0));
		verify(player, times(++sendPacketCalls)).getHandle();
		doReturn(null).when(player).getHandle();
		utils.sendPacket(player, new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(""), (byte) 0));
		verify(player, times(++sendPacketCalls)).getHandle();
	}

	@Test
	public void testGetPing() throws NoSuchFieldException, IllegalAccessException
	{
		Utils_Reflection utils = new Utils_Reflection();
		TestBukkitPlayer player = spy(new TestBukkitPlayer());
		assertEquals("The Ping should match", 123, utils.getPing(player));
		Field playerPingField = TestUtils.setAccessible(Utils_Reflection.class, null, "PLAYER_PING", EntityPlayer.class.getDeclaredField("failPing"));
		assertEquals("The Ping value should not be able to be retrieved", -1, utils.getPing(player));
		doReturn(null).when(player).getHandle();
		assertEquals("The Ping field should not be found", -1, utils.getPing(player));
		doReturn("Test").when(player).getHandle();
		assertEquals("The Ping field should not be found", -1, utils.getPing(player));
		playerPingField.set(null, null);
		assertEquals("The Ping field should not be found", -1, utils.getPing(player));
		TestUtils.setUnaccessible(playerPingField, null, true);
	}
}