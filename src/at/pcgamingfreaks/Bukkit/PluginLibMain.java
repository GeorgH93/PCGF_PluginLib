/*
 *   Copyright (C) 2016 GeorgH93
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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.ConsoleColor;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PluginLibMain extends JavaPlugin implements CommandExecutor
{
	@Override
	public void onEnable()
	{
		System.out.println(ConsoleColor.RED + "TEST" + ConsoleColor.RESET);
		getLogger().info(ConsoleColor.GREEN + "TEST" + ConsoleColor.RESET);
	}

	@Override
	public void onLoad()
	{
		/*Plugin target = null;

		File pluginDir = new File("plugins");
		File pluginFile = new File(pluginDir, "MarriageMaster.jar");
		String url = "http://dev.bukkit.org/media/files/911/575/MarriageMaster.jar";
		if (!pluginFile.isFile())
		{
			try
			{
				URL website = new URL(url);
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				FileOutputStream fos = new FileOutputStream(pluginFile);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				target = Bukkit.getPluginManager().loadPlugin(pluginFile);
				target.onLoad();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}*/
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		return true;
	}
}