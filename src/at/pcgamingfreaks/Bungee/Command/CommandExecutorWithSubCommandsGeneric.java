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

package at.pcgamingfreaks.Bungee.Command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandExecutorWithSubCommandsGeneric<SUB_COMMAND extends SubCommand> extends at.pcgamingfreaks.Command.CommandExecutorWithSubCommands<SUB_COMMAND> implements Listener
{

	/*@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onTab(TabCompleteEvent event)
	{
		String[] args = event.getCursor().toLowerCase().split(" ");
		if(args.length > 1 && commandAliases.contains(args[0].substring(1).toLowerCase()))
		{
			String arg1 = args[1].toLowerCase();
			SUB_COMMAND marryCommand = subCommandMap.get(arg1);
			if(marryCommand != null)
			{
				event.getSuggestions().addAll(marryCommand.doTabComplete((CommandSender) event.getSender(), args[0].substring(1).toLowerCase(), args[1], (args.length > 2) ? Arrays.copyOfRange(args, 2, args.length) : new String[0]));
				return;
			}
			if(args.length == 2)
			{
				List<String> results = new LinkedList<>();
				for(Map.Entry<String, SUB_COMMAND> cmd : subCommandMap.entrySet())
				{
					if(cmd.getKey().startsWith(arg1) && cmd.getValue().canUse((CommandSender) event.getSender()))
					{
						results.add(cmd.getKey());
					}
				}
				event.getSuggestions().addAll(results);
			}
		}
	}*/
}