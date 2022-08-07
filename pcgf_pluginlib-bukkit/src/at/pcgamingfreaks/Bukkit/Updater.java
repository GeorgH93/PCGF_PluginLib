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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Bukkit.Util.Utils;
import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.logging.Level;

public class Updater extends at.pcgamingfreaks.Updater.Updater
{
	private final Plugin plugin;
	private Thread thread;

	/**
	 * @param plugin The plugin for which the updater should be initialized.
	 * @param announceDownloadProgress If set to true the download progress will be logged in the console.
	 * @param updateProvider The update provider that should be used.
	 */
	public Updater(JavaPlugin plugin, boolean announceDownloadProgress, UpdateProvider updateProvider)
	{
		this(plugin, announceDownloadProgress, false, updateProvider);
	}

	/**
	 * @param plugin The plugin for which the updater should be initialized.
	 * @param announceDownloadProgress If set to true the download progress will be logged in the console.
	 * @param downloadDependencies If set to true dependencies will be downloaded (if provided from the update provider).
	 * @param updateProviders The update providers that should be used.
	 */
	public Updater(JavaPlugin plugin, boolean announceDownloadProgress, boolean downloadDependencies, UpdateProvider... updateProviders)
	{
		super(plugin.getDataFolder().getParentFile(), Bukkit.getUpdateFolderFile(), announceDownloadProgress, downloadDependencies, plugin.getLogger(), updateProviders, plugin.getDescription().getVersion(), Utils.getPluginJarFile(plugin).getName());
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
		if (isRunning())
		{
			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public boolean isRunning()
	{
		return thread != null && thread.isAlive();
	}

	@Override
	protected boolean checkCompatibility()
	{
		try
		{
			String[] versions = updateProvider.getLatestMinecraftVersions();
			MCVersion[] mcVersions = new MCVersion[versions.length];
			for(int i = 0; i < versions.length; i++)
			{
				mcVersions[i] = MCVersion.getFromVersionName(versions[i]);
			}
			boolean supported = ArrayUtils.contains(mcVersions, MCVersion.CURRENT_VERSION.getMajorMinecraftVersion());
			if(!supported) logger.info("Update found, but it is not compatible with the used Minecraft version. Using: " + MCVersion.CURRENT_VERSION.getMajorMinecraftVersion().getName() + " Compatible: " + Arrays.toString(mcVersions));
			return supported;
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, "Failed to check if Minecraft version is compatible with update!", e);
		}
		return true;
	}
}