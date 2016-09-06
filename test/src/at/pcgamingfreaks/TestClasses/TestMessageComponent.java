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

import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Message.MessageComponent;

import com.google.gson.GsonBuilder;

public class TestMessageComponent extends MessageComponent<MessageComponent, Enum>
{
	static
	{
		GSON = new GsonBuilder().registerTypeAdapter(TestMessageComponent.class, new TestMessageComponent()).create();
		messageComponentClass = TestMessageComponent.class;
		try
		{
			messageComponentConstructor = TestMessageComponent.class.getConstructor();
		}
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}
	}

	public TestMessageComponent() {}

	public TestMessageComponent(String text)
	{
		super(text);
	}

	public TestMessageComponent(String text, MessageColor... styles)
	{
		super(text, styles);
	}

	@Override
	public MessageComponent getNewLineComponent()
	{
		return new TestMessageComponent("\n");
	}
}