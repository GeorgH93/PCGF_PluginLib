/*
 *   Copyright (C) 2018 GeorgH93
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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandExecutorWithSubCommandsGeneric<SUB_COMMAND extends SubCommand> extends at.pcgamingfreaks.Command.CommandExecutorWithSubCommands<SUB_COMMAND> implements CommandExecutor, TabExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if(args.length > 0)
		{
			SubCommand subCommand = subCommandMap.get(args[0].toLowerCase());
			if(subCommand != null)
			{
				subCommand.doExecute(sender, alias, args[0], (args.length > 1) ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
				return true;
			}
		}
		if(defaultSubCommand != null)
		{
			defaultSubCommand.doExecute(sender, alias, defaultSubCommand.getName(), args);
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if(args.length > 0)
		{
			String arg = args[0].toLowerCase();
			SubCommand subCommand = subCommandMap.get(arg);
			if(subCommand != null)
			{
				return subCommand.doTabComplete(sender, alias, args[0], (args.length > 1) ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
			}
			List<String> results = new LinkedList<>();
			if(args.length == 1)
			{
				for(Map.Entry<String, SUB_COMMAND> cmd : subCommandMap.entrySet())
				{
					if(cmd.getKey().startsWith(arg) && cmd.getValue().canUse(sender))
					{
						results.add(cmd.getKey());
					}
				}
			}
			arg = args[args.length - 1].toLowerCase();
			for(Player player : Bukkit.getServer().getOnlinePlayers())
			{
				if(player.getName().toLowerCase().startsWith(arg))
				{
					results.add(player.getName());
				}
			}
			return results;
		}
		return null;
	}
}