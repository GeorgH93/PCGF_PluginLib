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

package at.pcgamingfreaks.PluginLib.Bukkit;

import at.pcgamingfreaks.Bukkit.GUI.GuiListener;
import at.pcgamingfreaks.Bukkit.ItemNameResolver;
import at.pcgamingfreaks.Bukkit.Language;
import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.ManagedUpdater;
import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Calendar.BasicTimeSpanFormat;
import at.pcgamingfreaks.Calendar.TimeSpan;
import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Database.ConnectionProvider.ConnectionProvider;
import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.PluginLib.Config;
import at.pcgamingfreaks.PluginLib.Database.DatabaseConnectionPoolBase;
import at.pcgamingfreaks.PluginLib.PluginLibrary;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.StringUtils;
import at.pcgamingfreaks.Updater.UpdateResponseCallback;
import at.pcgamingfreaks.Version;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public final class PluginLib extends JavaPlugin implements PluginLibrary, IPlugin
{
	@Getter @Setter(AccessLevel.PRIVATE) private static PluginLibrary instance = null;

	@Getter private ManagedUpdater updater;
	private Config config;
	@Getter private Version version;
	@Getter private DatabaseConnectionPoolBase databaseConnectionPool;
	@Getter private ItemNameResolver itemNameResolver;

	@Override
	public void onLoad()
	{
		new Message(""); // Prefetch message class
	}

	@Override
	public void onEnable()
	{
		updater = new ManagedUpdater(this);
		this.version = new Version(this.getDescription().getVersion());
		this.config = new Config(this, new Version(1));
		if(!this.config.isLoaded())
		{
			this.getLogger().warning(ConsoleColor.RED + "Failed to load config! Can't start up!" + ConsoleColor.RESET);
			this.setEnabled(false);
			this.config = null;
			return;
		}
		updater.setConfig(config);
		updater.autoUpdate();

		if(MCVersion.is(MCVersion.UNKNOWN))
		{
			this.getLogger().warning(ConsoleColor.RED + "You are using an unknown version of Minecraft! Please check for updates! (Your MC version: " + Bukkit.getVersion() + ") " + ConsoleColor.RESET);
		}

		this.databaseConnectionPool = DatabaseConnectionPoolBase.startPool(this.config, this.getLogger(), this.getDataFolder());

		itemNameResolver = new at.pcgamingfreaks.PluginLib.Bukkit.ItemNameResolver(this);

		Language commonLanguage = new Language(this, 2, 2, File.separator + "lang", "common_");
		commonLanguage.load(config.getLanguage(), config.getLanguageUpdateMode());
		if(commonLanguage.isLoaded())
		{
			String[] unitNames = new String[] { commonLanguage.get("Date.Units.Year"), commonLanguage.get("Date.Units.Years"), commonLanguage.get("Date.Units.Month"), commonLanguage.get("Date.Units.Months"), commonLanguage.get("Date.Units.Day"), commonLanguage.get("Date.Units.Days"), commonLanguage.get("Date.Units.Hour"), commonLanguage.get("Date.Units.Hours"), commonLanguage.get("Date.Units.Minute"), commonLanguage.get("Date.Units.Minutes"), commonLanguage.get("Date.Units.Second"), commonLanguage.get("Date.Units.Seconds") };
			try
			{
				//noinspection ConstantConditions
				Reflection.getField(BasicTimeSpanFormat.class, "unitNames").set(Reflection.getField(TimeSpan.class, "DEFAULT_TIME_SPAN_FORMAT").get(null), unitNames);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		getServer().getPluginManager().registerEvents(new GuiListener(), this);

		setInstance(this);
		this.getLogger().info(StringUtils.getPluginEnabledMessage(this.getDescription().getFullName()));
	}

	@Override
	public void onDisable()
	{
		setInstance(null);
		if(config == null) return;
		updater.autoUpdate();
		HandlerList.unregisterAll(this); // Stop the listeners
		if(this.databaseConnectionPool != null) this.databaseConnectionPool.shutdown();
		updater.waitForAsyncOperation();
		this.getLogger().info(StringUtils.getPluginDisabledMessage(this.getDescription().getFullName()));
	}

	@Deprecated
	public void update(final @Nullable UpdateResponseCallback responseCallback)
	{
		updater.update(responseCallback);
	}

	Config getConfiguration()
	{
		return config;
	}

	@Override
	public @Nullable ConnectionProvider getConnectionProvider()
	{
		return databaseConnectionPool;
	}
}
