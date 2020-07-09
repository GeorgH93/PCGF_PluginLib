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

package at.pcgamingfreaks.Bungee;

import at.pcgamingfreaks.Bungee.Message.Message;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

public class LanguageTest
{
	@Before
	public void prepareTestObjects()
	{
		TestObjects.initMockedPlugin();
	}

	@Test
	public void testLanguage() throws Exception
	{
		Language language = new Language(TestObjects.getPlugin(), 1);
		assertNotNull("The language object should not be null", language);
		language = spy(language);
		doReturn("TestText").when(language).get("test");
		doReturn(new Message("MessageText")).when(language, at.pcgamingfreaks.Language.class.getDeclaredMethod("getMessage", boolean.class, String.class)).withArguments(true, "test");
		assertEquals("The option string should match", "TestText", language.getString("test"));
		assertEquals("The translated string should match", "TestText", language.getTranslated("test"));
		assertEquals("The base component should be ready to send", 1, language.getReady("test").length);
		assertEquals("The message should be equal", "TestText", language.getMessage("test").getClassicMessage());
	}
}