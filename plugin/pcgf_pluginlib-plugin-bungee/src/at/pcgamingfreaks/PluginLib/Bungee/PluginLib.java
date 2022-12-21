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

package at.pcgamingfreaks.PluginLib.Bungee;

import at.pcgamingfreaks.Bungee.ManagedUpdater;
import at.pcgamingfreaks.Calendar.BasicTimeSpanFormat;
import at.pcgamingfreaks.Calendar.TimeSpan;
import at.pcgamingfreaks.Config.Language;
import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Database.ConnectionProvider.ConnectionProvider;
import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.PluginLib.Config;
import at.pcgamingfreaks.PluginLib.Database.DatabaseConnectionPoolBase;
import at.pcgamingfreaks.PluginLib.PluginLibrary;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.Util.StringUtils;
import at.pcgamingfreaks.Version;

import net.md_5.bungee.api.plugin.Plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.logging.Level;

public final class PluginLib extends Plugin implements PluginLibrary, IPlugin
{
	@Getter @Setter(AccessLevel.PRIVATE) private static PluginLibrary instance = null;

	@Getter private ManagedUpdater updater;
	private Config config;
	@Getter private Version version;
	@Getter private DatabaseConnectionPoolBase databaseConnectionPool;

	@Override
	public void onEnable()
	{
		this.updater = new ManagedUpdater(this);
		this.version = new Version(this.getDescription().getVersion());
		this.config = new Config(this, new Version(1));
		if(!this.config.isLoaded())
		{
			this.getLogger().warning(ConsoleColor.RED + "Failed to load config! Can't start up!" + ConsoleColor.RESET);
			this.config = null;
			return;
		}
		updater.setConfig(config);
		updater.autoUpdate();

		this.databaseConnectionPool = DatabaseConnectionPoolBase.startPool(this.config, this.getLogger(), this.getDataFolder());


		Language commonLanguage = new Language(this, new Version(2), File.separator + "lang", "common_");
		commonLanguage.load(config.getLanguage(), config.getLanguageUpdateMode());
		if(commonLanguage.isLoaded())
		{
			String[] unitNames = new String[] { commonLanguage.get("Date.Units.Year"), commonLanguage.get("Date.Units.Years"), commonLanguage.get("Date.Units.Month"), commonLanguage.get("Date.Units.Months"), commonLanguage.get("Date.Units.Day"), commonLanguage.get("Date.Units.Days"), commonLanguage.get("Date.Units.Hour"), commonLanguage.get("Date.Units.Hours"), commonLanguage.get("Date.Units.Minute"), commonLanguage.get("Date.Units.Minutes"), commonLanguage.get("Date.Units.Second"), commonLanguage.get("Date.Units.Seconds") };
			try
			{
				//noinspection ConstantConditions
				Reflection.getField(BasicTimeSpanFormat.class, "unitNames").set(Reflection.getField(TimeSpan.class, "DEFAULT_TIME_SPAN_FORMAT").get(null), unitNames);
			}
			catch(IllegalAccessException e)
			{
				this.getLogger().log(Level.SEVERE, "Failed to set unit names for time span.", e);
			}
		}

		setInstance(this);
		this.getLogger().info(StringUtils.getPluginEnabledMessage(this.getDescription().getName(), version));
	}

	@Override
	public void onDisable()
	{
		setInstance(null);
		if(config == null) return;
		updater.autoUpdate();
		if(this.databaseConnectionPool != null) this.databaseConnectionPool.shutdown();
		if(updater != null) updater.waitForAsyncOperation();
		this.getLogger().info(StringUtils.getPluginDisabledMessage(this.getDescription().getName(), version));
	}

	@Override
	public @Nullable ConnectionProvider getConnectionProvider()
	{
		return databaseConnectionPool;
	}

	@Override
	public @NotNull String getName()
	{
		return getDescription().getName();
	}
}