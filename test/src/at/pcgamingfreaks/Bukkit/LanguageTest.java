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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JavaPlugin.class, NMSReflection.class, Reflection.class })
public class LanguageTest
{
	@Before
	public void prepareTestData() throws Exception
	{
		TestObjects.initMockedJavaPlugin();
		whenNew(at.pcgamingfreaks.Language.class).withAnyArguments().thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		});
		suppress(at.pcgamingfreaks.Language.class.getDeclaredMethods());
	}

	@Test
	public void testLanguage()
	{
		assertNotNull("The language object should not be null", new Language(TestObjects.getJavaPlugin(), 1));
		//noinspection SpellCheckingInspection
		assertNotNull("The language object should not be null", new Language(TestObjects.getJavaPlugin(), 1, "bukkit", "bukkit"));
	}

	@Test
	public void testGetMessage() throws Exception
	{
		//noinspection SpellCheckingInspection
		TestBukkitServer server = new TestBukkitServer();
		server.serverVersion = "TestServer-1_7";
		Bukkit.setServer(server);
		mockStatic(NMSReflection.class);
		given(NMSReflection.getOBCClass(anyString())).willAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		});
		given(NMSReflection.getVersion()).willReturn("TestServer-1_7");
		mockStatic(Reflection.class);
		given(Reflection.getMethod(any(Class.class), anyString())).willAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		});
		Language mockedLanguage = spy(new Language(TestObjects.getJavaPlugin(), 1));
		doReturn("TestText").when(mockedLanguage).get("test");
		assertEquals("The language text should match", "TestText", mockedLanguage.getMessage("test").getClassicMessage());
		assertEquals("The language text should match", "TestText", mockedLanguage.getMessage("test", false).getClassicMessage());
		given(NMSReflection.getVersion()).willReturn("TestServer-1_1");
		assertEquals("The language text should match", "TestText", mockedLanguage.getMessage("test").getClassicMessage());
	}
}