/*
 *   Copyright (C) 2020 GeorgH93
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

package at.pcgamingfreaks.Bungee;

import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import org.jetbrains.annotations.NotNull;

public class Updater extends at.pcgamingfreaks.Updater.Updater
{
	private final Plugin plugin;
	private ScheduledTask task = null;

	public Updater(Plugin plugin, boolean announceDownloadProgress, UpdateProvider updateProvider)
	{
		this(plugin, announceDownloadProgress, false, updateProvider);
	}

	public Updater(Plugin plugin, boolean announceDownloadProgress, boolean downloadDependencies, UpdateProvider updateProvider)
	{
		super(plugin.getDataFolder().getParentFile(), announceDownloadProgress, downloadDependencies, plugin.getLogger(), updateProvider, plugin.getDescription().getVersion(), plugin.getDescription().getFile().getName());
		this.plugin = plugin;
	}

	public Updater(Plugin plugin, boolean announceDownloadProgress, boolean downloadDependencies, UpdateProvider[] updateProviders)
	{
		super(plugin.getDataFolder().getAbsoluteFile(), announceDownloadProgress, downloadDependencies, plugin.getLogger(), updateProviders, plugin.getDescription().getVersion(), plugin.getDescription().getFile().getName());
		this.plugin = plugin;
	}

	@Override
	protected void runSync(Runnable runnable)
	{
		runnable.run(); // BungeeCord runs everything async
		task = null;
	}

	@Override
	protected void runAsync(Runnable runnable)
	{
		task = plugin.getProxy().getScheduler().runAsync(plugin, runnable);
	}


	@Override
	protected @NotNull String getAuthor()
	{
		return plugin.getDescription().getAuthor() != null ? plugin.getDescription().getAuthor() : "";
	}

	@Override
	public void waitForAsyncOperation() {} // We can't wait for the async operation to finish, BungeeCord doesn't allow us to start threads

	@Override
	public boolean isRunning()
	{
		return task != null;
	}
}