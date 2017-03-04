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

package at.pcgamingfreaks.Bukkit;

import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MinecraftMaterial
{
	private final Material material;
	private final short dataValue;

	public static @Nullable MinecraftMaterial fromInput(@NotNull final String input)
	{
		String[] inputs = input.split(":");
		short dataValue = -1;
		if(inputs.length == 2)
		{
			try
			{
				dataValue = Short.valueOf(inputs[1]);
			}
			catch(NumberFormatException ignored) {}
		}
		Material material = Material.matchMaterial(inputs[0]);
		return (material == null) ? null : new MinecraftMaterial(material, dataValue);
	}

	public MinecraftMaterial(@NotNull Material material)
	{
		this(material, (short) -1);
	}

	public MinecraftMaterial(@NotNull Material material, short dataValue)
	{
		Validate.notNull(material);
		this.material = material;
		this.dataValue = dataValue;
	}

	public MinecraftMaterial(@NotNull ItemStack itemStack)
	{
		this(itemStack.getType(), itemStack.getDurability());
	}

	public MinecraftMaterial(@NotNull Block block)
	{
		//noinspection deprecation
		this(block.getType(), block.getData());
	}

	public Material getMaterial()
	{
		return material;
	}

	/**
	 * The data-value for the material. Negative values match all data-values.
	 *
	 * @return The data-value for the material.
	 */
	public short getDataValue()
	{
		return dataValue;
	}

	public boolean equals(Material material, short dataValue)
	{
		return material == getMaterial() && dataValue == getDataValue();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof ItemStack)
		{
			return ((ItemStack) object).getType() == getMaterial() && (((ItemStack) object).getDurability() == getDataValue() || getDataValue() == -1);
		}
		else if(object instanceof Block)
		{
			return ((Block) object).getType() == getMaterial() && (((Block) object).getData() == getDataValue() || getDataValue() == -1);
		}
		else if(object instanceof Material)
		{
			return object == getMaterial();
		}
		else if(object instanceof MinecraftMaterial)
		{
			return ((MinecraftMaterial) object).getMaterial() == getMaterial() && (((MinecraftMaterial) object).getDataValue() == getDataValue() || getDataValue() == -1 || ((MinecraftMaterial) object).getDataValue() == -1);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return material.hashCode();
	}
}