/*
 *   Copyright (C) 2017 GeorgH93
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

package at.pcgamingfreaks.PluginLib.Bukkit;

import at.pcgamingfreaks.Bukkit.Language;
import at.pcgamingfreaks.Bukkit.MinecraftMaterial;

import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class allows to translate minecraft items names.
 */
public final class ItemNameResolver
{
	//region static stuff
	private static ItemNameResolver instance = new ItemNameResolver();

	public static ItemNameResolver getInstance()
	{
		return instance;
	}
	//endregion

	private final Map<Material, Map<Short, String>> names = new HashMap<>();
	private final Language langReader;

	private ItemNameResolver()
	{
		langReader = null;
	}

	/**
	 * Packet local so that only the lib itself can create an instance.
	 */
	ItemNameResolver(@NotNull PluginLib plugin)
	{
		plugin.getLogger().info("Loading item translations ...");
		langReader = new Language(plugin, -1, File.separator + "lang", "items_");
		langReader.load(plugin.getConfiguration().getLanguage(), plugin.getConfiguration().getItemLangUpdateMode());
		int translationCount = load();
		plugin.getLogger().info("Finished loading item translations for " + translationCount + " items.");
		instance = this;
	}

	private int load()
	{
		int translationCount = 0;
		for(String key : langReader.getLang().getKeys(true))
		{
			String material = key, suffix = "";
			short dataValue = -1;
			if(key.contains("."))
			{
				String[] components = key.split("\\.");
				material = components[0];
				if(components[1].equals("appendDefault")) continue;
				try
				{
					dataValue = Short.parseShort(components[1]);
				}
				catch(NumberFormatException ignored) {}
				if(langReader.getLang().getBoolean(material + ".appendDefault", false))
				{
					suffix = langReader.getLang().getString(material, "");
				}
			}
			if(material.contains(":"))
			{
				String[] components = material.split(":");
				material = components[0];
				if(components[1].equals("appendDefault")) continue;
				try
				{
					dataValue = Short.parseShort(components[1]);
				}
				catch(NumberFormatException ignored) {}
				if(langReader.getLang().getBoolean(material + ".appendDefault", false))
				{
					suffix = langReader.getLang().getString(material, langReader.getLang().getString(material + ".default", ""));
				}
			}
			Material mat = Material.matchMaterial(material);
			if(!names.containsKey(mat))
			{
				names.put(mat, new HashMap<Short, String>());
			}
			names.get(mat).put(dataValue, langReader.get(key) + suffix);
			translationCount++;
		}
		return translationCount;
	}

	/**
	 * Gets the translated name for a material.
	 *
	 * @param material  The material to get the name for.
	 * @return The translated material name.
	 */
	public String getName(@NotNull Material material)
	{
		return getName(material, (short) -1);
	}

	/**
	 * Gets the translated name for a material with a data-value.
	 *
	 * @param material  The material to get the name for.
	 * @param dataValue The data-value for sub materials.
	 * @return The translated material name.
	 */
	public String getName(@NotNull Material material, short dataValue)
	{
		Validate.notNull(material);
		Map<Short, String> namesForMaterial = names.get(material);
		if(namesForMaterial != null)
		{
			if(dataValue >= -1 && namesForMaterial.containsKey(dataValue))
			{
				return namesForMaterial.get(dataValue);
			}
			if(dataValue != -1 && namesForMaterial.containsKey((short) -1))
			{
				return namesForMaterial.get((short) -1);
			}
		}
		return material.name().toLowerCase();
	}

	/**
	 * Gets the translated name for a minecraft material.
	 *
	 * @param material  The material to get the name for.
	 * @return The translated material name.
	 */
	public String getName(@NotNull MinecraftMaterial material)
	{
		Validate.notNull(material);
		return getName(material.getMaterial(), material.getDataValue());
	}

	/**
	 * Gets the translated name for a block.
	 *
	 * @param block The block to get the name for.
	 * @return The translated block material name.
	 */
	public String getName(@NotNull Block block)
	{
		Validate.notNull(block);
		//noinspection deprecation
		return getName(block.getType(), block.getData());
	}

	/**
	 * Gets the translated name for an item-stack.
	 *
	 * @param itemStack The item-stack to get the name for.
	 * @return The translated item-stack material name.
	 */
	public String getName(@NotNull ItemStack itemStack)
	{
		Validate.notNull(itemStack);
		//noinspection deprecation
		return getName(itemStack.getType(), itemStack.getDurability());
	}
}