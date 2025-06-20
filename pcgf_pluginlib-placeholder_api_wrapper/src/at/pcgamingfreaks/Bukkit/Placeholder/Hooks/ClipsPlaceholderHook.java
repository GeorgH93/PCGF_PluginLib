/*
 *   Copyright (C) 2023 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Placeholder.Hooks;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Bukkit.Placeholder.PlaceholderManager;
import at.pcgamingfreaks.Version;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class ClipsPlaceholderHook extends PlaceholderExpansion implements PlaceholderAPIHook
{
	private final Plugin plugin;
	private final PlaceholderManager placeholderManager;

	public ClipsPlaceholderHook(Plugin plugin, PlaceholderManager placeholderManager)
	{
		this.plugin = plugin;
		this.placeholderManager = placeholderManager;
		if(this.register())
		{
			this.plugin.getLogger().info(ConsoleColor.GREEN + "PlaceholderAPI hook was successfully registered!" + ConsoleColor.RESET);
		}
		else
		{
			this.plugin.getLogger().info(ConsoleColor.RED + "PlaceholderAPI hook failed to registered!" + ConsoleColor.RESET);
			this.plugin.getLogger().info("If you currently have the eCloud extension installed please remove it and try again!");
		}
	}

	@Override
	public String onRequest(final @NotNull OfflinePlayer player, final @NotNull String identifier)
	{
		return placeholderManager.replacePlaceholder(player, identifier);
	}

	@Override
	public void close()
	{
		boolean result = false;
		// PAPI changed how to unregister an expansion with v2.10.7. The old method was removed and a new added within the same update.
		// This hack should make it work on servers that have already updated and servers that have not yet updated.
		try
		{
			result = (boolean) PlaceholderAPI.class.getDeclaredMethod("unregisterExpansion", PlaceholderExpansion.class).invoke(null, this);
		}
		catch(NoSuchMethodException ignored)
		{
			try
			{
				result = (boolean) PlaceholderExpansion.class.getDeclaredMethod("unregister").invoke(this);
			}
			catch(Exception e)
			{
				plugin.getLogger().log(Level.SEVERE, "Failed to unregister placeholder hook from PAPI!", e);
			}
		}
		catch(Exception e)
		{
			plugin.getLogger().log(Level.SEVERE, "Failed to unregister placeholder hook from PAPI!", e);
		}
		if(result)
		{
			plugin.getLogger().info(ConsoleColor.GREEN + "PlaceholderAPI hook was successfully unregistered!" + ConsoleColor.RESET);
		}
		else
		{
			plugin.getLogger().info(ConsoleColor.RED + "PlaceholderAPI hook failed to unregistered!" + ConsoleColor.RESET);
		}
	}

	@Override
	public @NotNull String getName()
	{
		return plugin.getDescription().getName();
	}

	@Override
	public @NotNull String getIdentifier()
	{
		return plugin.getDescription().getName().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public @NotNull String getAuthor()
	{
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public @NotNull String getVersion()
	{
		return plugin.getDescription().getVersion();
	}

	@Override
	public @NotNull List<String> getPlaceholders()
	{
		return placeholderManager.getPlaceholdersList();
	}

	@Override
	public boolean persist()
	{
		return true;
	}

	@Override
	public void testPlaceholders(final @NotNull BufferedWriter writer) throws IOException
	{
		Version pluginVersion = getPlaceholderAPIVersion();
		writer.append("\nPlaceholderAPI (").append(String.valueOf(pluginVersion)).append(") integration test:\n");
		PlaceholderHook hook;
		if (pluginVersion.olderThan("2.11.1"))
		{
			//noinspection UnstableApiUsage
			hook = PlaceholderAPI.getPlaceholders().get(getIdentifier()); // Will only be used for old versions
		}
		else
		{
			hook = getPlaceholderAPI().getLocalExpansionManager().getExpansion(getIdentifier());
		}

		//noinspection ObjectEquality
		if(hook == this)
		{
			writer.append("Success!\n\n");
		}
		else if(hook instanceof ClipsPlaceholderHook)
		{
			writer.append("Failed! ").append(plugin.getName().toLowerCase(Locale.ENGLISH)).append(" placeholders are hooked to the right class, but the wrong instance!");
		}
		else if (hook != null)
		{
			writer.append("Failed! ").append(plugin.getName().toLowerCase(Locale.ENGLISH)).append("_ placeholders are linked to: ").append(hook.getClass().getName());
		}
		else
		{
			writer.append("Failed! ").append(plugin.getName().toLowerCase(Locale.ENGLISH)).append("_ placeholders are not linked.");
		}
	}

	public Version getPlaceholderAPIVersion()
	{
		Plugin papiPlugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
		if (papiPlugin != null)
		{
			return new Version(papiPlugin.getDescription().getVersion());
		}
		return new Version(0);
	}
}