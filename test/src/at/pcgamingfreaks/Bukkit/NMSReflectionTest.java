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

public class NMSReflectionTest
{
	/*@Rule
	public PowerMockRule powerMock = new PowerMockRule();

	private static Server server;
	private static Class serverClass;

	//@PrepareForTest({ Bukkit.class, NMSReflection.class })
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		server = mock(Server.class);
		//noinspection unchecked
		when(server.getClass()).thenReturn(null);
		//noinspection SpellCheckingInspection
		//when(serverClass.getName()).thenReturn("Bukkit 1.8");
		Field serverField = Bukkit.class.getDeclaredField("server");
		serverField.setAccessible(true);
		serverField.set(null, server);
		serverField.setAccessible(false);
	}

	@Test
	public void testGetVersion()
	{
		assertEquals("The version should match", "", NMSReflection.getVersion());
	}*/
}