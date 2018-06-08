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

import at.pcgamingfreaks.TestClasses.SubCommand;
import at.pcgamingfreaks.TestClasses.TestBukkitPlayer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SubCommandTest
{
	@Test
	public void testSubCommand()
	{
		at.pcgamingfreaks.TestClasses.SubCommand subCommand = new SubCommand("Command", "Description", null, new String[] { "Command", "Sub1", null, "Sub3" });
		assertEquals("The name of the SubCommand should match", "command", subCommand.getName());
		assertEquals("The translated name of the SubCommand should match", "command", subCommand.getTranslatedName());
		assertNull("The permission of the SubCommand should match", subCommand.getPermission());
		assertEquals("The description of the SubCommand should match", "Description", subCommand.getDescription());
		subCommand = new SubCommand("Command", "Description", null, null);
		assertNull("The should be no help for the SubCommand", subCommand.doGetHelp(new TestBukkitPlayer()));
		subCommand.canUse = true;
		assertNotNull("The should be help for the SubCommand", subCommand.doGetHelp(new TestBukkitPlayer()));
	}
}