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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.Message.MessageColor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class allows to translate minecraft items names.
 */
public class ItemNameResolver
{
	private final Map<Material, Map<Short, String>> names = new HashMap<>();

	public void load(@NotNull Language language, @NotNull Logger logger)
	{
		if(!language.isLoaded()) return;
		logger.info("Loading item translations ...");
		int translationCount = 0;
		//noinspection ConstantConditions
		for(String key : language.getLang().getKeys(true))
		{
			if(!key.startsWith("Items")) continue;
			String material = key.substring(6), suffix = "";
			short dataValue = -1;
			Material mat = Material.matchMaterial(material);
			if(mat == null) continue;
			if(!names.containsKey(mat))
			{
				names.put(mat, new HashMap<>());
			}
			names.get(mat).put(dataValue, language.getRaw(key, "") + suffix);
			translationCount++;
		}
		logger.info("Finished loading item translations for " + translationCount + " items.");
	}

	public void loadLegacy(@NotNull Language language, @NotNull Logger logger)
	{
		if(!language.isLoaded()) return;
		logger.info("Loading item translations ...");
		int translationCount = 0;
		//noinspection ConstantConditions
		for(String key : language.getLang().getKeys(true))
		{
			String material = key, suffix = "";
			short dataValue = -1;
			if(key.contains(".") || key.contains(":"))
			{
				String[] components = key.split("[.:]");
				material = components[0];
				if(components[1].equals("appendDefault")) continue;
				try
				{
					dataValue = Short.parseShort(components[1]);
				}
				catch(NumberFormatException ignored) {}
				if(language.getLang().getBoolean(material + ".appendDefault", false))
				{
					suffix = language.getRaw(material, language.getRaw(material + ".default", ""));
				}
			}
			Material mat = Material.matchMaterial(material);
			if(mat == null) continue;
			if(!names.containsKey(mat))
			{
				names.put(mat, new HashMap<>());
			}
			names.get(mat).put(dataValue, language.getRaw(key, "") + suffix);
			translationCount++;
		}
		logger.info("Finished loading item translations for " + translationCount + " items.");
	}

	/**
	 * Gets the translated name for a material.
	 *
	 * @param material  The material to get the name for.
	 * @return The translated material name.
	 */
	public @NotNull String getName(@NotNull Material material)
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
	public @NotNull String getName(@NotNull Material material, short dataValue)
	{
		Map<Short, String> namesForMaterial = names.get(material);
		if(namesForMaterial != null)
		{
			if(dataValue >= -1 && namesForMaterial.containsKey(dataValue))
			{
				return namesForMaterial.get(dataValue);
			}
			if(namesForMaterial.containsKey((short) -1))
			{
				return namesForMaterial.get((short) -1);
			}
		}
		//noinspection StringToUpperCaseOrToLowerCaseWithoutLocale
		return material.name().toLowerCase();
	}

	/**
	 * Gets the translated name for a minecraft material.
	 *
	 * @param material  The material to get the name for.
	 * @return The translated material name.
	 */
	public @NotNull String getName(@NotNull MinecraftMaterial material)
	{
		return getName(material.getMaterial(), material.getDataValue());
	}

	/**
	 * Gets the translated name for a block.
	 *
	 * @param block The block to get the name for.
	 * @return The translated block material name.
	 */
	public @NotNull String getName(@NotNull Block block)
	{
		//noinspection deprecation
		return getName(block.getType(), block.getData());
	}

	/**
	 * Gets the translated name for an item-stack.
	 *
	 * @param itemStack The item-stack to get the name for.
	 * @return The translated item-stack material name.
	 */
	public @NotNull String getName(@NotNull ItemStack itemStack)
	{
		if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
		{
			//noinspection ConstantConditions
			return MessageColor.stripColorAndFormat(itemStack.getItemMeta().getDisplayName());
		}
		return getName(itemStack.getType(), itemStack.getDurability());
	}

	/**
	 * Gets the translated name for an item-stack.
	 *
	 * @param itemStack The item-stack to get the name for.
	 * @return The translated item-stack material name.
	 */
	public @NotNull String getDisplayName(@NotNull ItemStack itemStack)
	{
		if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
		{
			return itemStack.getItemMeta().getDisplayName();
		}
		return MessageColor.GRAY + getName(itemStack.getType(), itemStack.getDurability());
	}
}