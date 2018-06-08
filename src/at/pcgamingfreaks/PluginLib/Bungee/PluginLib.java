/*
 *   Copyright (C) 2016-2018 GeorgH93
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

import at.pcgamingfreaks.*;
import at.pcgamingfreaks.Bungee.Configuration;
import at.pcgamingfreaks.Bungee.Updater;
import at.pcgamingfreaks.Calendar.TimeSpan;
import at.pcgamingfreaks.PluginLib.Database.DatabaseConnectionPool;
import at.pcgamingfreaks.PluginLib.Database.DatabaseConnectionPoolBase;
import at.pcgamingfreaks.PluginLib.PluginLibrary;
import at.pcgamingfreaks.Updater.UpdateProviders.JenkinsUpdateProvider;

import net.md_5.bungee.api.plugin.Plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public final class PluginLib extends Plugin implements PluginLibrary
{
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
			Updater updater = new Updater(this, true, new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", getLogger()));
			updater.update();
		}

		Language commonLanguage = new at.pcgamingfreaks.Bungee.Language(this, 1, File.separator + "lang", "common_");
		commonLanguage.load("en", YamlFileUpdateMethod.UPGRADE);
		if(commonLanguage.isLoaded())
		{
			String[] unitNames = new String[] { commonLanguage.get("Date.Units.Year"), commonLanguage.get("Date.Units.Years"), commonLanguage.get("Date.Units.Month"), commonLanguage.get("Date.Units.Months"), commonLanguage.get("Date.Units.Day"), commonLanguage.get("Date.Units.Days"), commonLanguage.get("Date.Units.Hour"), commonLanguage.get("Date.Units.Hours"), commonLanguage.get("Date.Units.Minute"), commonLanguage.get("Date.Units.Minutes"), commonLanguage.get("Date.Units.Second"), commonLanguage.get("Date.Units.Seconds") };
			try
			{
				//noinspection ConstantConditions
				Reflection.getField(TimeSpan.class, "timeUnitNames").set(null, unitNames);
			}
			catch(IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		instance = this;
		this.getLogger().info(StringUtils.getPluginEnabledMessage(this.getDescription().getName(), version));
	}

	@Override
	public void onDisable()
	{
		instance = null;
		Updater updater =  (this.config.getBool("Misc.AutoUpdate", true)) ? update(null) : null;
		if(this.databaseConnectionPool != null) this.databaseConnectionPool.close();
		if(updater != null) updater.waitForAsyncOperation();
		this.getLogger().info(StringUtils.getPluginDisabledMessage(this.getDescription().getName(), version));
	}

	public @NotNull Updater update(@Nullable Updater.UpdaterResponse responseCallback)
	{
		Updater updater = new Updater(this, true, new JenkinsUpdateProvider("https://ci.pcgamingfreaks.at", "PluginLib", getLogger()));
		updater.update(responseCallback);
		return updater;
	}

	public static PluginLibrary getInstance()
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