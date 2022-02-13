/*
 *   Copyright (C) 2022 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Message.Placeholder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StringPlaceholderEngineTest
{
	@Test
	public void testPlaceholders()
	{
		StringPlaceholderEngine placeholderEngine = new StringPlaceholderEngine("test {TestPlaceholder} test more.");
		assertEquals("test {TestPlaceholder} test more.", placeholderEngine.processPlaceholders("test2"));
		placeholderEngine.registerPlaceholder("{TestPlaceholder}", 0, Object::toString);
		assertEquals("test test2 test more.", placeholderEngine.processPlaceholders("test2"));
		placeholderEngine = new StringPlaceholderEngine("{TestPlaceholder} test more.");
		assertEquals("{TestPlaceholder} test more.", placeholderEngine.processPlaceholders("test2"));
		placeholderEngine.registerPlaceholder("{TestPlaceholder}", 0, Object::toString);
		assertEquals("test2 test more.", placeholderEngine.processPlaceholders("test2"));
	}
}