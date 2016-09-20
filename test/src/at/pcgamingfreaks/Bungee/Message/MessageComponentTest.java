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

package at.pcgamingfreaks.Bungee.Message;

import at.pcgamingfreaks.Message.MessageColor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import net.md_5.bungee.api.ChatColor;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageComponentTest
{
	@Test
	public void testMessageComponent()
	{
		MessageComponent messageComponent = new MessageComponent("Text", MessageColor.BLUE);
		assertEquals("The chat color should match", ChatColor.BLUE, messageComponent.getChatColor());
		assertEquals("The new line object should be equal", new MessageComponent("\n").getClassicMessage(), messageComponent.getNewLineComponent().getClassicMessage());
		Gson gson = new Gson();
		List<MessageComponent> message = MessageComponent.fromJsonArray(gson.fromJson("[{\"text\":\"test\"}]", JsonArray.class));
		assertEquals("The amount of message components should match", 1, message.size());
		assertEquals("The message text should be equal", "test§r", message.get(0).getClassicMessage());
	}
}