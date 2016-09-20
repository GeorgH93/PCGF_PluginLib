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

package at.pcgamingfreaks.Bungee.Message.Sender;

import at.pcgamingfreaks.Bungee.Message.Message;
import at.pcgamingfreaks.TestClasses.TestObjects;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Chat;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BossBarSenderTest
{
	@Before
	public void prepareTestObjects()
	{
		TestObjects.initMockedPlayer();
	}

	@Test
	public void testSend()
	{
		List<ProxiedPlayer> players = new ArrayList<>();
		players.add(TestObjects.getPlayer());
		players.add(TestObjects.getPlayer());
		int sendCalls = 0;
		BossBarSender bossBarSender = new BossBarSender();
		bossBarSender.doSend(TestObjects.getPlayer(), "");
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		bossBarSender.doSend(TestObjects.getPlayer(), "", 1);
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		BossBarSender.send(TestObjects.getPlayer(), new Message(""));
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		bossBarSender.doSend(players, "");
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		bossBarSender.doSend(players, "", 1);
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
	}

	@Test
	public void testBroadcast() throws NoSuchFieldException, IllegalAccessException
	{
		TestObjects.initProxyServer();
		int sendCalls = 0;
		BossBarSender bossBarSender = new BossBarSender();
		bossBarSender.doBroadcast("");
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		bossBarSender.doBroadcast("", 1);
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		BossBarSender.broadcast(new Message(""));
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
	}
}