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

package at.pcgamingfreaks.TestClasses;

import at.pcgamingfreaks.Message.MessageBuilder;
import at.pcgamingfreaks.Message.MessageComponent;
import at.pcgamingfreaks.Reflection;

import java.util.Collection;

public class TestMessageBuilder extends MessageBuilder<TestMessageBuilder, TestMessage>
{
	public boolean appendNewLineFromFatherClass = false;

	private final MessageComponent messageComponent;

	static
	{
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "MESSAGE_CONSTRUCTOR", Reflection.getConstructor(TestMessage.class, Collection.class));
	}

	public TestMessageBuilder()
	{
		this(new MessageComponent());
	}

	public TestMessageBuilder(MessageComponent initComponent)
	{
		super(initComponent);
		messageComponent = initComponent;
	}

	@Override
	public TestMessageBuilder appendNewLine()
	{
		if (appendNewLineFromFatherClass)
		{
			return super.appendNewLine();
		}
		messageComponent.addExtra(MessageComponent.makeNewLineComponent());
		return this;
	}
}