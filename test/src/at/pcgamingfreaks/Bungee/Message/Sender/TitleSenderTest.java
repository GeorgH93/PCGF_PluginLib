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

public class TitleSenderTest
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
		int playerCount = players.size();
		TitleSender titleSender = new TitleSender();
		titleSender.doSend(TestObjects.getPlayer(), "");
		sendCalls += 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		titleSender.doSend(TestObjects.getPlayer(), "", 1);
		sendCalls += 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		titleSender.doSend(TestObjects.getPlayer(), "", new TitleMetadata());
		sendCalls += 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		TitleSender.send(TestObjects.getPlayer(), new Message(""));
		sendCalls += 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		TitleSender.send(TestObjects.getPlayer(), new Message(""), new TitleMetadata(10, 10, 20));
		sendCalls += 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		titleSender.doSend(players, "");
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		titleSender.doSend(players, "", 1);
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		titleSender.doSend(players, "", new TitleMetadata());
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		TitleSender.send(players, new Message(""));
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		TitleSender.send(players, new Message(""), new TitleMetadata(true));
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
	}

	@Test
	public void testBroadcast() throws NoSuchFieldException, IllegalAccessException
	{
		TestObjects.initProxyServer();
		int sendCalls = 0;
		int playerCount = TestObjects.getPlayers().size();
		TitleSender titleSender = new TitleSender();
		titleSender.doBroadcast("");
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		titleSender.doBroadcast("", 1);
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		titleSender.doBroadcast("", new TitleMetadata());
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		TitleSender.broadcast(new Message(""));
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
		TitleSender.broadcast(new Message(""), TitleMetadata.fromJson("[{\"fadeIn\": 10, \"fadeOut\": 20, \"stay\": 30, \"subtitle\": false}]"));
		sendCalls += playerCount * 2;
		verify(TestObjects.getPlayer().unsafe(), times(sendCalls)).sendPacket(any(Chat.class));
	}
}