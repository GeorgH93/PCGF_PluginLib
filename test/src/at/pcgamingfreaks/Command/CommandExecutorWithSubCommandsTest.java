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

import at.pcgamingfreaks.TestClasses.*;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandExecutorWithSubCommandsTest
{
	@Test
	public void testCommandExecutorWithSubCommands() throws NoSuchFieldException
	{
		CommandExecutor commandExecutor = new CommandExecutor();
		at.pcgamingfreaks.TestClasses.SubCommand subCommand1 = new at.pcgamingfreaks.TestClasses.SubCommand("Command1", "Description", "", new String[] { "Sub1", "Sub2", "Sub3" });
		at.pcgamingfreaks.TestClasses.SubCommand subCommand2 = new at.pcgamingfreaks.TestClasses.SubCommand("Command2", "Description", "", new String[] { "Sub1", "Sub2", "Sub3" });
		at.pcgamingfreaks.TestClasses.SubCommand subCommand3 = new at.pcgamingfreaks.TestClasses.SubCommand("Command3", "Description", "", new String[] { "Sub1", "Sub2", "Sub3" });
		assertEquals("The CommandExecutor should not contain any sub commands", 0, commandExecutor.getCommandList().size());
		commandExecutor.registerSubCommand(subCommand1);
		commandExecutor.registerSubCommand(subCommand2);
		commandExecutor.registerSubCommand(subCommand3);
		assertEquals("The CommandExecutor should now contain 3 sub commands", 3, commandExecutor.getCommandList().size());
		commandExecutor.unRegisterSubCommand(subCommand2);
		assertEquals("The CommandExecutor should now contain 2 sub commands", 2, commandExecutor.getCommandList().size());
		assertTrue("The subCommand1 should be contained", commandExecutor.getCommandList().contains(subCommand1));
		assertFalse("The subCommand2 should not be contained", commandExecutor.getCommandList().contains(subCommand2));
		assertTrue("The subCommand3 should be contained", commandExecutor.getCommandList().contains(subCommand3));
		assertNull("The default command should be null", commandExecutor.getDefaultSubCommand());
		commandExecutor.setDefaultSubCommand(subCommand1);
		assertEquals("The default SubCommand should be set correctly", subCommand1, commandExecutor.getDefaultSubCommand());
		commandExecutor.close();
		assertEquals("The CommandExecutor should now contain 0 sub commands", 0, commandExecutor.getCommandList().size());
		assertEquals("The CommandExecutor should now contain 0 sub commands", 0, commandExecutor.getCommandMap().size());
	}
}