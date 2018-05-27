/*
 *   Copyright (C) 2018 GeorgH93
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

import org.apache.commons.lang3.Validate;
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

	public ItemNameResolver load(@NotNull Language language, @NotNull Logger logger)
	{
		if(!language.isLoaded()) return this;
		logger.info("Loading item translations ...");
		int translationCount = 0;
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
					suffix = language.getLang().getString(material, language.getLang().getString(material + ".default", ""));
				}
			}
			Material mat = Material.matchMaterial(material);
			if(!names.containsKey(mat))
			{
				names.put(mat, new HashMap<Short, String>());
			}
			names.get(mat).put(dataValue, language.get(key) + suffix);
			translationCount++;
		}
		logger.info("Finished loading item translations for " + translationCount + " items.");
		return this;
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