/*
 * Copyright (C) 2016 GeorgH93
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

package at.pcgamingfreaks.Bungee.Message;

import at.pcgamingfreaks.Message.MessageColor;

import net.md_5.bungee.api.ChatColor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BungeeMessageTest
{
	@Before
	public void init()
	{
	}

	@Test
	public void testToClassicMessage()
	{
		assertEquals(new Message("Test Message").getClassicMessage(), "Test Message");
		assertEquals(new Message(new MessageComponent[] { new MessageComponent("Test Message 2", ChatColor.RED) }).getClassicMessage(), MessageColor.RED + "Test Message 2" + MessageColor.RESET);
		assertEquals(new Message(new MessageComponent[] { new MessageComponent("Test Message 3", ChatColor.BLUE, ChatColor.ITALIC) }).getClassicMessage(),
		             MessageColor.ITALIC + MessageColor.BLUE.toString() + "Test Message 3" + MessageColor.RESET);
	}
}