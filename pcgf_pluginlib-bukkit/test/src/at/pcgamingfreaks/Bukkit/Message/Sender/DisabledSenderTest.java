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

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.Util.Utils;
import at.pcgamingfreaks.TestClasses.TestBukkitPlayer;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DisabledSenderTest
{
	private static boolean skipTests = false; // will be set in prepareTestData
	
	@BeforeClass
	public static void prepareTestData() throws Exception
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
		TestObjects.initBukkitOnlinePlayers();
	}

	@Test
	public void testSend()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class))
		{
			int sendPacketCalls = 0;
			TestBukkitPlayer player = new TestBukkitPlayer();
			List<Player> players = new ArrayList<>();
			players.add(player);
			players.add(player);
			DisabledSender disabledSender = new DisabledSender();
			disabledSender.send(player, "");
			mockedUtils.verify(() -> Utils.sendPacket(any(Player.class), any()), times(sendPacketCalls));
			disabledSender.send(player, "", null);
			mockedUtils.verify(() -> Utils.sendPacket(any(Player.class), any()), times(sendPacketCalls));
			disabledSender.send(players, "");
			mockedUtils.verify(() -> Utils.sendPacket(any(Player.class), any()), times(sendPacketCalls));
			disabledSender.send(players, "", null);
			mockedUtils.verify(() -> Utils.sendPacket(any(Player.class), any()), times(sendPacketCalls));
		}
	}

	@Test
	public void testBroadcast()
	{
		try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class))
		{
			int sendPacketCalls = 0;
			DisabledSender disabledSender = new DisabledSender();
			disabledSender.broadcast("");
			mockedUtils.verify(() -> Utils.sendPacket(any(Player.class), any()), times(sendPacketCalls));
			disabledSender.broadcast("", null);
			mockedUtils.verify(() -> Utils.sendPacket(any(Player.class), any()), times(sendPacketCalls));
		}
	}
}