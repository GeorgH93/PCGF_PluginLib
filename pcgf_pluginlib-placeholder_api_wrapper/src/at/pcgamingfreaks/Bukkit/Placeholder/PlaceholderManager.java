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

package at.pcgamingfreaks.Bukkit.Placeholder;

import at.pcgamingfreaks.Bukkit.Placeholder.Hooks.ClipsPlaceholderHook;
import at.pcgamingfreaks.Bukkit.Placeholder.Hooks.MVdWPlaceholderReplacer;
import at.pcgamingfreaks.Bukkit.Placeholder.Hooks.PlaceholderAPIHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class PlaceholderManager
{
	private static MVdWPlaceholderReplacer mVdWPlaceholderReplacer = null; // The MVdWPlaceholder API does not allow unregistering hooked placeholders
	private final Plugin plugin;
	private final Map<String, at.pcgamingfreaks.Bukkit.Placeholder.PlaceholderReplacer> placeholders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private final List<PlaceholderAPIHook> hooks = new ArrayList<>(2);
	private final List<String> placeholdersList = new ArrayList<>();

	private static void hockWithMVdWPlaceholderAPI(final @NotNull Plugin plugin, final @NotNull PlaceholderManager manager)
	{
		if(mVdWPlaceholderReplacer == null) mVdWPlaceholderReplacer = new MVdWPlaceholderReplacer(plugin, manager);
		else mVdWPlaceholderReplacer.set(plugin, manager); // Workaround because we can't unregister from MVdWPlaceholders
	}

	public PlaceholderManager(Plugin plugin)
	{
		this.plugin = plugin;
		if(!(isPluginEnabled("MVdWPlaceholderAPI") || isPluginEnabled("PlaceholderAPI"))) return; // No supported placeholder API installed
		generatePlaceholdersMap();
		//region MVdW Placeholders
		if(isPluginEnabled("MVdWPlaceholderAPI"))
		{
			hockWithMVdWPlaceholderAPI(plugin, this);
			hooks.add(mVdWPlaceholderReplacer);
		}
		//endregion
		//region Chips PlaceholderAPI
		if(isPluginEnabled("PlaceholderAPI"))
		{
			hooks.add(new ClipsPlaceholderHook(plugin, this));
		}
		//endregion
	}
	
	private static boolean isPluginEnabled(String pluginName)
	{
		return Bukkit.getPluginManager().isPluginEnabled(pluginName);
	}

	public void close()
	{
		for(PlaceholderAPIHook hook : hooks)
		{
			hook.close();
		}
		hooks.clear();
		placeholdersList.clear();
		placeholders.clear();
	}

	public Map<String, PlaceholderReplacer> getPlaceholders()
	{
		return placeholders;
	}

	public String replacePlaceholder(OfflinePlayer player, String identifier)
	{
		if(player == null) return "Player needed!";
		PlaceholderReplacer replacer = placeholders.get(identifier);
		return replacer == null ? null : replacer.replace(player);
	}

	protected abstract void generatePlaceholdersMap();

	public void registerPlaceholder(PlaceholderReplacer placeholder)
	{
		placeholders.put(placeholder.getName(), placeholder);
		final PlaceholderReplacer finalPlaceholder = placeholder;
		placeholder.getAliases().forEach(alias -> placeholders.put(alias, finalPlaceholder));
	}

	public List<String> getPlaceholdersList()
	{
		if(placeholdersList.isEmpty())
		{
			for(String key : placeholders.keySet())
			{
				placeholdersList.add(plugin.getDescription().getName() + '_' + key);
			}
		}
		return placeholdersList;
	}

	public void testPlaceholders(final @NotNull BufferedWriter writer) throws IOException
	{
		for(PlaceholderAPIHook hook : hooks)
		{
			hook.testPlaceholders(writer);
		}
	}
}