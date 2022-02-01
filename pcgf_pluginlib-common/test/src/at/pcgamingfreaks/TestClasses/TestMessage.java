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

import at.pcgamingfreaks.Message.Message;
import at.pcgamingfreaks.Message.MessageComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class TestMessage extends Message<TestMessage, Object, Object>
{
	static
	{
		Constructor<TestMessageBuilder> builderConstructor = null;
		try
		{
			builderConstructor = TestMessageBuilder.class.getConstructor();
		}
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		setMessageComponentClass(builderConstructor);
	}

	public TestMessage(String message)
	{
		super(message);
	}

	public TestMessage(Collection<? extends MessageComponent> message)
	{
		super(message);
	}

	@Override
	public void send(@NotNull Object target, @Nullable Object... args) { }

	@Override
	public void send(@NotNull Collection targets, @Nullable Object... args) { }

	@Override
	public void broadcast(@Nullable Object... args) { }

	@SuppressWarnings("unused")
	public void setSendMethod(TestSendMethod sendMethod) { }
}