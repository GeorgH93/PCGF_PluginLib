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

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.Utils;
import at.pcgamingfreaks.TestClasses.TestBukkitPlayer;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bukkit.class, NMSReflection.class, Utils.class })
public class ActionBarSenderTest
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
	public void testSend() throws InvocationTargetException, IllegalAccessException
	{
		int sendPacketCalls = 0;
		//noinspection SpellCheckingInspection
		TestBukkitPlayer player = new TestBukkitPlayer();
		List<Player> players = new ArrayList<>();
		players.add(player);
		players.add(player);
		int playerCount = players.size();
		ActionBarSender.send(player, new Message(""));
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		ActionBarSender.send(players, new Message(""));
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		ActionBarSender actionBarSender = new ActionBarSender();
		actionBarSender.doSend(player, "");
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		actionBarSender.doSend(player, "", false);
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		actionBarSender.doSend(players, "");
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		actionBarSender.doSend(players, "", true);
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
	}

	@Test
	public void testBroadcast() throws Exception
	{
		TestObjects.initBukkitOnlinePlayers();
		int sendPacketCalls = 0;
		int playerCount = Bukkit.getOnlinePlayers().size();
		ActionBarSender.broadcast(new Message(""));
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		ActionBarSender actionBarSender = new ActionBarSender();
		actionBarSender.doBroadcast("");
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		actionBarSender.doBroadcast("", 34);
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
	}
}