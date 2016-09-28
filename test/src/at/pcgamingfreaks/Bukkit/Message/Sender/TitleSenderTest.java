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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BaseSender.class, Bukkit.class, NMSReflection.class, Utils.class })
public class TitleSenderTest
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
	public void testSend() throws Exception
	{
		int sendPacketCalls = 0;
		//noinspection SpellCheckingInspection
		TestBukkitPlayer player = new TestBukkitPlayer();
		List<Player> players = new ArrayList<>();
		players.add(player);
		players.add(player);
		int playerCount = players.size();
		TitleSender.send(player, new Message(""));
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		TitleSender.send(player, new Message(""), new TitleMetadata(20, 20, 20));
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		TitleSender.send(players, new Message(""));
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		TitleSender.send(players, new Message(""), new TitleMetadata(10, 20, 30, false));
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		TitleSender titleSender = spy(new TitleSender());
		titleSender.doSend(player, "");
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		titleSender.doSend(player, "", new TitleMetadata());
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		titleSender.doSend(player, "", 56);
		verifyStatic(times(++sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		titleSender.doSend(players, "");
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		titleSender.doSend(players, "", false);
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		titleSender.doSend(players, "", new TitleMetadata());
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		Field chatSerializerMethodAField = BaseSender.class.getDeclaredField("CHAT_SERIALIZER_METHOD_A");
		chatSerializerMethodAField.setAccessible(true);
		modifiers.set(chatSerializerMethodAField, chatSerializerMethodAField.getModifiers() & ~Modifier.FINAL);
		Method chatSerializerMethodA = (Method) chatSerializerMethodAField.get(null);
		chatSerializerMethodAField.set(null, null);
		TitleSender.send(player, "", new TitleMetadata());
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		TitleSender.send(players, "", new TitleMetadata());
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		chatSerializerMethodAField.set(null, chatSerializerMethodA);
		modifiers.set(chatSerializerMethodAField, chatSerializerMethodAField.getModifiers() | Modifier.FINAL);
		chatSerializerMethodAField.setAccessible(false);
		Field packetPlayOutTitleConstructorField = TitleSender.class.getDeclaredField("PACKET_PLAY_OUT_TITLE_CONSTRUCTOR");
		packetPlayOutTitleConstructorField.setAccessible(true);
		modifiers.set(packetPlayOutTitleConstructorField, packetPlayOutTitleConstructorField.getModifiers() & ~Modifier.FINAL);
		Constructor<?> packetPlayOutTitleConstructor = (Constructor<?>) packetPlayOutTitleConstructorField.get(null);
		packetPlayOutTitleConstructorField.set(null, null);
		TitleSender.send(player, "", new TitleMetadata());
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		TitleSender.send(players, "", new TitleMetadata());
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		packetPlayOutTitleConstructorField.set(null, packetPlayOutTitleConstructor);
		modifiers.set(packetPlayOutTitleConstructorField, packetPlayOutTitleConstructorField.getModifiers() | Modifier.FINAL);
		packetPlayOutTitleConstructorField.setAccessible(false);
		modifiers.setAccessible(false);
		mockStatic(BaseSender.class);
		doThrow(new IllegalAccessException()).when(BaseSender.class, "finalizeJson", anyString());
		TitleSender.send(player, "", new TitleMetadata());
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		TitleSender.send(players, "", new TitleMetadata());
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
	}

	@Test
	public void testBroadcast() throws Exception
	{
		TestObjects.initBukkitOnlinePlayers();
		int sendPacketCalls = 0;
		int playerCount = Bukkit.getOnlinePlayers().size();
		TitleSender.broadcast(new Message(""));
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		TitleSender.broadcast(new Message(""), new TitleMetadata());
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		TitleSender titleSender = new TitleSender();
		titleSender.doBroadcast("");
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		titleSender.doBroadcast("", new TitleMetadata());
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
		titleSender.doBroadcast("", 34);
		sendPacketCalls += playerCount;
		verifyStatic(times(sendPacketCalls));
		Utils.sendPacket(any(Player.class), Matchers.anyObject());
	}
}