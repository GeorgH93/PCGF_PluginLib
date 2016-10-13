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

import at.pcgamingfreaks.Message.MessageBuilder;
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Reflection;

import java.util.Collection;

public class TestMessageBuilder extends MessageBuilder<TestMessageBuilder, TestMessageComponent, TestMessage, Enum>
{
	public boolean appendNewLineFromFatherClass = false;

	private TestMessageComponent messageComponent;

	static
	{
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "NEW_LINE_HELPER", new TestMessageComponent("\n"));
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "EMPTY_COMPONENT_CONSTRUCTOR", Reflection.getConstructor(TestMessageComponent.class));
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "INIT_COMPONENT_CONSTRUCTOR", Reflection.getConstructor(TestMessageComponent.class, String.class, MessageColor[].class));
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "MESSAGE_CONSTRUCTOR", Reflection.getConstructor(TestMessage.class, Collection.class));
		Reflection.setStaticField(at.pcgamingfreaks.Message.MessageBuilder.class, "COMPONENT_CLASS", TestMessageComponent.class);
	}

	public TestMessageBuilder(TestMessageComponent initComponent)
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
		messageComponent.addExtra(messageComponent.getNewLineComponent());
		return this;
	}

	@Override
	public TestMessageBuilder appendJson(String json)
	{
		return this;
	}
}