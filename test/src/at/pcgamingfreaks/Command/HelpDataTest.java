/*
 * Copyright (C) 2018 MarkusWME
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

package at.pcgamingfreaks.Command;

import at.pcgamingfreaks.Message.MessageClickEvent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelpDataTest
{
	@Test
	public void testHelpData()
	{
		HelpData helpData = new HelpData("Test", "Parameter", "Description");
		assertEquals("The translated sub command should match", "Test", helpData.getTranslatedSubCommand());
		assertEquals("The parameter should match", "Parameter", helpData.getParameter());
		assertEquals("The description should match", "Description", helpData.getDescription());
		helpData = new HelpData("Test1", null, "Description1");
		assertEquals("The translated sub command should match", "Test1", helpData.getTranslatedSubCommand());
		assertEquals("The parameter should match an empty string when null parameter has been given", "", helpData.getParameter());
		assertEquals("The description should match", "Description1", helpData.getDescription());
		assertEquals("The click event action should match", MessageClickEvent.ClickEventAction.SUGGEST_COMMAND, helpData.getClickAction());
	}
}