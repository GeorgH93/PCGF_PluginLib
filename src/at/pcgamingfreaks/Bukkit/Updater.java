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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

public class Updater extends at.pcgamingfreaks.Updater.Updater
{
	private final Plugin plugin;
	private Thread thread;

	/**
	 * @param plugin The plugin for which the updater should be initialized.
	 * @param file The jar file of the plugin.
	 * @param announceDownloadProgress If set to true the download progress will be logged in the console.
	 * @param updateProvider The update provider that should be used.
	 * @deprecated There now is a version that no longer needs the file parameter.
	 */
	@Deprecated
	public Updater(JavaPlugin plugin, File file, boolean announceDownloadProgress, UpdateProvider updateProvider)
	{
		this(plugin, file, announceDownloadProgress, false, updateProvider);
	}

	/**
	 * @param plugin The plugin for which the updater should be initialized.
	 * @param file The jar file of the plugin.
	 * @param announceDownloadProgress If set to true the download progress will be logged in the console.
	 * @param downloadDependencies If set to true dependencies will be downloaded (if provided from the update provider).
	 * @param updateProvider The update provider that should be used.
	 * @deprecated There now is a version that no longer needs the file parameter.
	 */
	@Deprecated
	public Updater(JavaPlugin plugin, File file, boolean announceDownloadProgress, boolean downloadDependencies, UpdateProvider updateProvider)
	{
		super(plugin.getDataFolder().getParentFile(), Bukkit.getUpdateFolderFile(), announceDownloadProgress, downloadDependencies, plugin.getLogger(), updateProvider, plugin.getDescription().getVersion(), file.getName());
		this.plugin = plugin;
	}

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
	 * @param updateProvider The update provider that should be used.
	 */
	public Updater(JavaPlugin plugin, boolean announceDownloadProgress, boolean downloadDependencies, UpdateProvider updateProvider)
	{
		this(plugin, Utils.getPluginJarFile(plugin), announceDownloadProgress, downloadDependencies, updateProvider);
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
				plugin.getLogger().log(Level.SEVERE, null, e);
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
			if(!supported) logger.info("Update found but it is not compatible with the used Minecraft version. Using: " + MCVersion.CURRENT_VERSION.getMajorMinecraftVersion().getName() + " Compatible: " + Arrays.toString(mcVersions));
			return supported;
		}
		catch(Exception e)
		{
			logger.severe("Failed to check if Minecraft version is compatible with update!");
			e.printStackTrace();
		}
		return true;
	}
}