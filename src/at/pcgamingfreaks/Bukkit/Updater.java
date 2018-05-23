/*
 *   Copyright (C) 2016, 2018 GeorgH93
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

import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

public class Updater extends at.pcgamingfreaks.Updater.Updater
{
	private final JavaPlugin plugin;
	private Thread thread;

	public Updater(JavaPlugin plugin, File file, boolean announceDownloadProgress, UpdateProvider updateProvider)
	{
		this(plugin, file, announceDownloadProgress, false, updateProvider);
	}

	public Updater(JavaPlugin plugin, File file, boolean announceDownloadProgress, boolean downloadDependencies, UpdateProvider updateProvider)
	{
		super(plugin.getDataFolder().getParentFile(), Bukkit.getUpdateFolderFile(), announceDownloadProgress, downloadDependencies, plugin.getLogger(), updateProvider, plugin.getDescription().getVersion(), file.getName());
		this.plugin = plugin;
	}

	@Override
	protected void runSync(Runnable runnable)
	{
		plugin.getServer().getScheduler().runTask(plugin, runnable);
	}

	@Override
	protected void runAsync(Runnable runnable)
	{
		thread = new Thread(runnable);
		thread.start();
	}

	@Override
	protected @NotNull String getAuthor()
	{
		return plugin.getDescription().getAuthors().size() > 0 ? plugin.getDescription().getAuthors().get(0) : "";
	}

	@Override
	public void waitForAsyncOperation()
	{
		if (thread != null && thread.isAlive())
		{
			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{
				plugin.getLogger().log(Level.SEVERE, null, e);
			}
		}
	}
}