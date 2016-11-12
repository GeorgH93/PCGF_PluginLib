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

package at.pcgamingfreaks.PluginLib.Bungee;

import at.pcgamingfreaks.Bungee.Configuration;
import at.pcgamingfreaks.Bungee.Updater;
import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.PluginLib.Database.DatabaseConnectionPool;
import at.pcgamingfreaks.PluginLib.Database.DatabaseConnectionPoolBase;
import at.pcgamingfreaks.PluginLib.PluginLibrary;
import at.pcgamingfreaks.StringUtils;
import at.pcgamingfreaks.Updater.UpdateProviders.AlwaysUpdateProvider;
import at.pcgamingfreaks.Version;

import net.md_5.bungee.api.plugin.Plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginLib extends Plugin implements PluginLibrary
{
	private static final String URL = "https://ci.pcgamingfreaks.at/job/Bukkit_Bungee_PluginLib/lastSuccessfulBuild/artifact/target/Bukkit_Bungee_PluginLib-1.0-SNAPSHOT.jar";

	private static PluginLibrary instance = null;

	private Configuration config;
	private Version version;
	private DatabaseConnectionPoolBase databaseConnectionPool;

	@Override
	public void onEnable()
	{
		this.version = new Version(this.getDescription().getVersion());
		this.config = new Configuration(this, 1);
		if(!this.config.isLoaded())
		{
			this.getLogger().warning(ConsoleColor.RED + "Failed to load config! Can't start up!" + ConsoleColor.RESET);
			return;
		}

		this.databaseConnectionPool = DatabaseConnectionPoolBase.startPool(this.config, this.getLogger(), this.getDataFolder());

		if(this.config.getBool("Misc.AutoUpdate", true))
		{
			Updater updater = new Updater(this, true, new AlwaysUpdateProvider(URL));
			updater.update();
		}
		instance = this;
		this.getLogger().info(StringUtils.getPluginEnabledMessage(this.getDescription().getName(), version));
	}

	@Override
	public void onDisable()
	{
		instance = null;
		Updater updater = null;
		if(this.config.getBool("Misc.AutoUpdate", true))
		{
			updater = new Updater(this, true, new AlwaysUpdateProvider(URL));
			updater.update();
		}
		if(this.databaseConnectionPool != null) this.databaseConnectionPool.close();
		if(updater != null) updater.waitForAsyncOperation();
		this.getLogger().info(StringUtils.getPluginDisabledMessage(this.getDescription().getName(), version));
	}

	public PluginLibrary getInstance()
	{
		return instance;
	}

	@Override
	public @NotNull Version getVersion()
	{
		return version;
	}

	@Override
	public @Nullable DatabaseConnectionPool getDatabaseConnectionPool()
	{
		return databaseConnectionPool;
	}
}