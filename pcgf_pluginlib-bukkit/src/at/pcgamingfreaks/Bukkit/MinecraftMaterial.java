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

package at.pcgamingfreaks.Bukkit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

public final class MinecraftMaterial
{
	@Getter private final Material material;

	/**
	 * The data-value for the material. Negative values match all data-values.
	 */
	@Getter private final short dataValue;

	public static @Nullable MinecraftMaterial fromInput(final @NotNull String input)
	{
		String[] inputs = input.split(":");
		short dataValue = -1;
		if(inputs.length == 2)
		{
			try
			{
				dataValue = Short.parseShort(inputs[1]);
			}
			catch(NumberFormatException ignored) {}
		}
		Material material = Material.matchMaterial(inputs[0]);
		return (material == null) ? null : new MinecraftMaterial(material, dataValue);
	}

	public MinecraftMaterial(final @NotNull Material material)
	{
		this(material, (short) -1);
	}

	public MinecraftMaterial(final @NotNull Material material, short dataValue)
	{
		this.material = material;
		this.dataValue = dataValue;
	}

	public MinecraftMaterial(final @NotNull ItemStack itemStack)
	{
		this(itemStack.getType(), itemStack.getDurability());
	}

	public MinecraftMaterial(final @NotNull Block block)
	{
		//noinspection deprecation
		this(block.getType(), block.getData());
	}

	public boolean is(final @Nullable Material material, final short dataValue)
	{
		return material == getMaterial() && dataValue == getDataValue();
	}

	@Contract("null->false")
	public boolean is(final @Nullable Object object)
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
	public boolean equals(final @Nullable Object object)
	{
		return is(object);
	}

	@Override
	public int hashCode()
	{
		return material.hashCode();
	}
}