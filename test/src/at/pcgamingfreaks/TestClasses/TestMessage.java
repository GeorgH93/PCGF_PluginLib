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

package at.pcgamingfreaks.TestClasses;

import at.pcgamingfreaks.Message.Message;
import at.pcgamingfreaks.Message.MessageComponent;

import java.util.Collection;

public class TestMessage extends Message
{
	public TestMessage()
	{
		super("", TestMessageComponent.class);
	}

	public TestMessage(String message)
	{
		super(message, TestMessageComponent.class);
	}

	public TestMessage(Collection<? extends MessageComponent> message)
	{
		super(message);
	}
}