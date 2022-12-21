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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bungee.Message;

import at.pcgamingfreaks.Message.MessageComponent;
import at.pcgamingfreaks.Bungee.Message.Sender.BossBarMetadata;
import at.pcgamingfreaks.Bungee.Message.Sender.ISender;
import at.pcgamingfreaks.Bungee.Message.Sender.SendMethod;
import at.pcgamingfreaks.Bungee.Message.Sender.TitleMetadata;
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Message.MessageFormat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MessageTest
{
	@Test(expected = ClassCastException.class)
	public void testGenericClass()
	{
		at.pcgamingfreaks.Message.Message message = new Message("");
		//noinspection unchecked
		message.send(new Object(), "");
	}

	@Test
	public void testMessage()
	{
		assertEquals("The send method of the new message should match", SendMethod.TITLE, new Message("Another test message", SendMethod.TITLE).getSendMethod());
		assertEquals("The extended message should match", MessageFormat.ITALIC.toString() + MessageColor.BLUE.toString() + "Test Message 3" + MessageColor.RESET, new Message(new MessageComponent[] { new MessageComponent("Test Message 3", MessageColor.BLUE, MessageFormat.ITALIC) }, SendMethod.ACTION_BAR).getClassicMessage());
		List<MessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new MessageComponent("Test message 2"));
		assertEquals("The message should match", "Test message 2" + MessageColor.RESET, new Message(messageComponents, SendMethod.DISABLED).getClassicMessage());
		assertEquals("The message component count should match", 1, new Message(messageComponents).getMessageComponents().length);
		assertEquals("The message from the message builder should match", MessageColor.RESET.toString(), new Message(new MessageBuilder()).getClassicMessage());
		assertEquals("The send method from the message builder constructor of the message class should match", SendMethod.CHAT, new Message(new MessageBuilder(), SendMethod.CHAT).getSendMethod());
	}

	@Test
	public void testSetOptionalParameters() throws NoSuchFieldException, IllegalAccessException
	{
		Message message = new Message("");
		TitleMetadata titleMetadata = new TitleMetadata(10, 20, 30);
		BossBarMetadata bossBarMetadata = new BossBarMetadata();
		Field optionalParameters = at.pcgamingfreaks.Message.Message.class.getDeclaredField("optionalParameters");
		optionalParameters.setAccessible(true);
		message.setOptionalParameters(titleMetadata);
		assertEquals("The title metadata should match", titleMetadata, optionalParameters.get(message));
		message.setOptionalParameters(bossBarMetadata);
		assertEquals("The boss bar metadata should match", bossBarMetadata, optionalParameters.get(message));
		optionalParameters.setAccessible(false);
	}

	@Test
	public void testSend() throws Exception
	{
		int sendMessageCalls = 0;
		int doSendCalls = 0;
		Message message = new Message("");
		CommandSender mockedCommandSender = mock(CommandSender.class);
		ProxiedPlayer mockedProxiedPlayer = mock(ProxiedPlayer.class);
		Field method = Message.class.getDeclaredField("sendMethod");
		method.setAccessible(true);
		ISender mockedSender = mock(ISender.class);
		doNothing().when(mockedSender).send(any(ProxiedPlayer.class), anyString(), any());
		SendMethod sendMethod = (SendMethod) method.get(message);
		Field defaultSender = sendMethod.getClass().getDeclaredField("sender");
		defaultSender.setAccessible(true);
		defaultSender.set(sendMethod, mockedSender);
		message.send(mockedCommandSender, (Object[]) null);
		//noinspection deprecation
		verify(mockedCommandSender, times(++sendMessageCalls)).sendMessage(anyString());
		message.send(mockedCommandSender);
		//noinspection deprecation
		verify(mockedCommandSender, times(++sendMessageCalls)).sendMessage(anyString());
		message.send(mockedCommandSender, "Test");
		//noinspection deprecation
		verify(mockedCommandSender, times(++sendMessageCalls)).sendMessage(anyString());
		message.send(mockedProxiedPlayer, (Object[]) null);
		verify(mockedSender, times(++doSendCalls)).send(any(ProxiedPlayer.class), anyString(), any());
		message.send(mockedProxiedPlayer);
		verify(mockedSender, times(++doSendCalls)).send(any(ProxiedPlayer.class), anyString(), any());
		message.send(mockedProxiedPlayer, "Test");
		verify(mockedSender, times(++doSendCalls)).send(any(ProxiedPlayer.class), anyString(), any());
		doSendCalls = 0;
		List<ProxiedPlayer> proxiedPlayers = new ArrayList<>();
		message.send(proxiedPlayers);
		verify(mockedSender, times(doSendCalls)).send(anyCollectionOf(ProxiedPlayer.class), anyString(), any());
		proxiedPlayers.add(mockedProxiedPlayer);
		message.send(proxiedPlayers, (Object[]) null);
		verify(mockedSender, times(++doSendCalls)).send(anyCollectionOf(ProxiedPlayer.class), anyString(), any());
		message.send(proxiedPlayers);
		verify(mockedSender, times(++doSendCalls)).send(anyCollectionOf(ProxiedPlayer.class), anyString(), any());
		message.send(proxiedPlayers, "Test");
		verify(mockedSender, times(++doSendCalls)).send(anyCollectionOf(ProxiedPlayer.class), anyString(), any());
		message.setSendMethod(null);
		message.send(mockedCommandSender);
		//noinspection deprecation
		verify(mockedCommandSender, times(sendMessageCalls)).sendMessage(anyString());
		message.send(proxiedPlayers);
		verify(mockedSender, times(doSendCalls)).send(anyCollectionOf(ProxiedPlayer.class), anyString(), any());
		defaultSender.setAccessible(false);
		method.setAccessible(false);
	}

	@PrepareForTest({ ProxyServer.class })
	@Test
	public void testBroadcast() throws NoSuchFieldException, IllegalAccessException
	{
		int sendMessageCalls = 0;
		int doBroadcastCalls = 0;
		Message message = new Message("");
		Field method = Message.class.getDeclaredField("sendMethod");
		method.setAccessible(true);
		ISender mockedSender = mock(ISender.class);
		doNothing().when(mockedSender).broadcast(anyString(), any());
		SendMethod sendMethod = (SendMethod) method.get(message);
		Field defaultSender = sendMethod.getClass().getDeclaredField("sender");
		defaultSender.setAccessible(true);
		defaultSender.set(sendMethod, mockedSender);
		CommandSender mockedCommandSender = mock(CommandSender.class);
		//noinspection deprecation
		doNothing().when(mockedCommandSender).sendMessage(anyString());
		ProxyServer mockedProxyServer = mock(ProxyServer.class);
		when(mockedProxyServer.getConsole()).thenReturn(mockedCommandSender);
		Field proxyServer = ProxyServer.class.getDeclaredField("instance");
		proxyServer.setAccessible(true);
		proxyServer.set(mockedProxyServer, mockedProxyServer);
		message.broadcast("Test");
		//noinspection deprecation
		verify(mockedCommandSender, times(++sendMessageCalls)).sendMessage(anyString());
		verify(mockedSender, times(++doBroadcastCalls)).broadcast(anyString(), any());
		message.broadcast((Object[]) null);
		//noinspection deprecation
		verify(mockedCommandSender, times(++sendMessageCalls)).sendMessage(anyString());
		verify(mockedSender, times(++doBroadcastCalls)).broadcast(anyString(), any());
		message.broadcast();
		//noinspection deprecation
		verify(mockedCommandSender, times(++sendMessageCalls)).sendMessage(anyString());
		verify(mockedSender, times(++doBroadcastCalls)).broadcast(anyString(), any());
		message.setSendMethod(null);
		message.broadcast();
		//noinspection deprecation
		verify(mockedCommandSender, times(sendMessageCalls)).sendMessage(anyString());
		verify(mockedSender, times(doBroadcastCalls)).broadcast(anyString(), any());
		defaultSender.setAccessible(false);
		proxyServer.setAccessible(false);
		method.setAccessible(false);
	}
}