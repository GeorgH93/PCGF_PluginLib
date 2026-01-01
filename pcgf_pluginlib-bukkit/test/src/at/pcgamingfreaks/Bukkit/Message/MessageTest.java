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

package at.pcgamingfreaks.Bukkit.Message;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.Message.Sender.*;
import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Message.MessageComponent;
import at.pcgamingfreaks.Message.Sender.TitleMetadata;
import at.pcgamingfreaks.TestClasses.TestBukkitPlayer;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MessageTest
{
	private static boolean skipTests = false; // will be set in prepareTestData
	
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		skipTests = !TestUtils.canMockJdkClasses();
		if (!skipTests)
		{
			Bukkit.setServer(new TestBukkitServer());
			TestObjects.initNMSReflection();
			SendMethod tmp = SendMethod.CHAT_CLASSIC; // Init class, do not remove!
		}
	}

	@Test(expected = ClassCastException.class)
	public void testGenericClass() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		setVersion("1_7");
		Message message = new Message("");
		assertEquals("The send method should be correct", SendMethod.CHAT_CLASSIC, message.getSendMethod());
		Message.class.getDeclaredMethod("send", Object.class, Object[].class).invoke(message, new Object(), new Object[] {});
	}

	@Test
	public void testMessage() throws NoSuchFieldException, IllegalAccessException
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		setVersion("1_8");
		Message message = new Message("", SendMethod.ACTION_BAR);
		assertNotNull("The message object should not be null", message);
		assertEquals("The send method of the message should match", SendMethod.ACTION_BAR, message.getSendMethod());
		List<MessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new MessageComponent());
		assertEquals("The send method of the message should be correct", SendMethod.TITLE, new Message(new MessageComponent[] { messageComponents.get(0) }, SendMethod.TITLE).getSendMethod());
		assertEquals("The send method of the message should be correct", SendMethod.CHAT, new Message(messageComponents, SendMethod.CHAT).getSendMethod());
		assertEquals("The send method of the message should be correct", SendMethod.DISABLED, new Message(new MessageBuilder(), SendMethod.DISABLED).getSendMethod());
		message = new Message(new MessageBuilder());
		message.setOptionalParameters(new TitleMetadata(true));
		assertNotNull("The message should not be null", message);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOptionalParameters() throws NoSuchFieldException, IllegalAccessException
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		setVersion("1_7");
		new Message("").setOptionalParameters(json -> null);
	}

	@Test
	public void testSend() throws NoSuchFieldException, IllegalAccessException
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		int playerCalls = 0;
		int commandSenderCalls = 0;
		final int[] doSendCalls = { 0 };
		List<Player> players = new ArrayList<>();
		CommandSender mockedCommandSender = mock(CommandSender.class);
		TestBukkitPlayer player = new TestBukkitPlayer();
		setVersion("1_8");
		Message message = new Message("[\"\",{\"text\":\"You don't have the permission to do that.\",\"color\":\"red\"}]");
		message.setSendMethod(null);
		message.send(player);
		assertEquals("The send method should be called as often as given", playerCalls, doSendCalls[0]);
		message.send(players);
		assertEquals("The send method should be called as often as given", playerCalls, doSendCalls[0]);
		message.setSendMethod(SendMethod.CHAT_CLASSIC);
		message.send(mockedCommandSender, (Object[]) null);
		verify(mockedCommandSender, times(++commandSenderCalls)).sendMessage(anyString());
		message.send(mockedCommandSender);
		verify(mockedCommandSender, times(++commandSenderCalls)).sendMessage(anyString());
		message.send(mockedCommandSender, "");
		verify(mockedCommandSender, times(++commandSenderCalls)).sendMessage(anyString());
		message.send(player);
		verify(mockedCommandSender, times(commandSenderCalls)).sendMessage(anyString());
		player.sendCalls = commandSenderCalls;
		message.send(players);
		assertEquals("The send method should be called as often as given", commandSenderCalls, player.sendCalls);
		players.add(player);
		players.add(player);
		int playerCount = players.size();
		message.send(players, (Object[]) null);
		commandSenderCalls += playerCount;
		assertEquals("The send method should be called as often as given", commandSenderCalls, player.sendCalls);
		message.send(players);
		commandSenderCalls += playerCount;
		assertEquals("The send method should be called as often as given", commandSenderCalls, player.sendCalls);
		message.send(players, "");
		commandSenderCalls += playerCount;
		assertEquals("The send method should be called as often as given", commandSenderCalls, player.sendCalls);
		message.setSendMethod(SendMethod.CHAT);
		ISender mockedSender = spy(ISender.class);
		doAnswer(invocationOnMock -> {
			doSendCalls[0]++;
			return null;
		}).when(mockedSender).send(any(Player.class), anyString(), any());
		doAnswer(invocationOnMock -> {
			doSendCalls[0]++;
			return null;
		}).when(mockedSender).send(anyList(), anyString(), any());
		Field defaultSender = SendMethod.class.getDeclaredField("activeSender");
		defaultSender.setAccessible(true);
		Object senderBackup = defaultSender.get(SendMethod.CHAT);
		defaultSender.set(SendMethod.CHAT, mockedSender);
		message.send(player, (Object[]) null);
		assertEquals("The send method should be called as often as given", ++playerCalls, doSendCalls[0]);
		message.send(player);
		assertEquals("The send method should be called as often as given", ++playerCalls, doSendCalls[0]);
		message.send(player, false);
		assertEquals("The send method should be called as often as given", ++playerCalls, doSendCalls[0]);
		message.send(players, (Object[]) null);
		assertEquals("The send method should be called as often as given", ++playerCalls, doSendCalls[0]);
		message.send(players);
		assertEquals("The send method should be called as often as given", ++playerCalls, doSendCalls[0]);
		message.send(players, false);
		assertEquals("The send method should be called as often as given", ++playerCalls, doSendCalls[0]);
		defaultSender.set(SendMethod.CHAT, senderBackup);
		defaultSender.setAccessible(false);
	}

	@Test
	public void testBroadcast() throws NoSuchFieldException, IllegalAccessException
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		int currentCalls = 0;
		final int[] broadcastCalls = { 0 };
		setVersion("1_9");
		Message message = new Message("[\"\",{\"text\":\"You don't have the permission to do that.\",\"color\":\"red\"}]");
		ISender mockedSender = mock(ISender.class);
		doAnswer(invocationOnMock -> {
			broadcastCalls[0]++;
			return null;
		}).when(mockedSender).broadcast(anyString(), any());
		Field defaultSender = SendMethod.class.getDeclaredField("activeSender");
		defaultSender.setAccessible(true);
		Object senderBackup = defaultSender.get(SendMethod.CHAT);
		defaultSender.set(SendMethod.CHAT, mockedSender);
		try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class))
		{
			ConsoleCommandSender mockedConsoleCommandSender = mock(ConsoleCommandSender.class);
			doAnswer(invocationOnMock -> {
				broadcastCalls[0]++;
				return null;
			}).when(mockedConsoleCommandSender).sendMessage(anyString());
			mockedBukkit.when(() -> Bukkit.broadcastMessage(anyString())).thenAnswer(invocationOnMock -> {
				broadcastCalls[0]++;
				return null;
			});
			mockedBukkit.when(Bukkit::getConsoleSender).thenReturn(mockedConsoleCommandSender);
			message.broadcast((Object[]) null);
			currentCalls += 2;
			assertEquals("The broadcast method should be called as often as given", currentCalls, broadcastCalls[0]);
			message.broadcast();
			currentCalls += 2;
			assertEquals("The broadcast method should be called as often as given", currentCalls, broadcastCalls[0]);
			message.broadcast(false);
			currentCalls += 2;
			assertEquals("The broadcast method should be called as often as given", currentCalls, broadcastCalls[0]);
			message.setSendMethod(SendMethod.CHAT_CLASSIC);
			message.broadcast((Object[]) null);
			assertEquals("The broadcast method should be called as often as given", ++currentCalls, broadcastCalls[0]);
			message.broadcast();
			assertEquals("The broadcast method should be called as often as given", ++currentCalls, broadcastCalls[0]);
			message.broadcast(false);
			assertEquals("The broadcast method should be called as often as given", ++currentCalls, broadcastCalls[0]);
			message.setSendMethod(null);
			message.broadcast();
			assertEquals("The broadcast method should be called as often as given", currentCalls, broadcastCalls[0]);
		}
		defaultSender.set(SendMethod.CHAT, senderBackup);
		defaultSender.setAccessible(false);
	}

	private void setVersion(String version) throws NoSuchFieldException, IllegalAccessException
	{
		TestObjects.setBukkitVersion(version + "_R1");
		Field versionField = TestUtils.setAccessible(Message.class, null, "PRE_1_8_MC", MCVersion.isOlderThan(MCVersion.MC_1_8));
		TestUtils.setUnaccessible(versionField, null, true);
	}
}