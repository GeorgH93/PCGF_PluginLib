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

import org.junit.BeforeClass;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.powermock.api.mockito.PowerMockito.whenNew;

public class ConfigurationTest
{
	@BeforeClass
	public static void prepareTestData() throws Exception
	{
		whenNew(at.pcgamingfreaks.Configuration.class).withAnyArguments().thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return null;
			}
		});
	}

	/*@Test
	public void testConfiguration()
	{
		TestJavaPlugin plugin = new TestJavaPlugin();
		Configuration configuration = new Configuration(plugin, 3);
		assertEquals("The class of the configuration should match", Configuration.class, configuration.getClass());
	}*/
}