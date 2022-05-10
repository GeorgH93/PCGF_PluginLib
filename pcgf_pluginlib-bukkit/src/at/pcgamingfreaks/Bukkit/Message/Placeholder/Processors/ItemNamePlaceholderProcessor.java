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

package at.pcgamingfreaks.Bukkit.Message.Placeholder.Processors;

import at.pcgamingfreaks.Bukkit.ItemNameResolver;
import at.pcgamingfreaks.Bukkit.Message.Placeholder.Processors.Wrappers.ItemStackWrapper;
import at.pcgamingfreaks.Bukkit.MinecraftMaterial;
import at.pcgamingfreaks.Message.MessageComponent;
import at.pcgamingfreaks.Message.Placeholder.Processors.FormattedStringPlaceholderProcessor;
import at.pcgamingfreaks.Message.Placeholder.Processors.IFormattedPlaceholderProcessor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ItemNamePlaceholderProcessor implements IFormattedPlaceholderProcessor
{
	private final @NotNull ItemNameResolver itemNameResolver;

	@Override
	public @NotNull MessageComponent processFormatted(@Nullable Object parameter)
	{
		return FormattedStringPlaceholderProcessor.INSTANCE.processFormatted(getName(parameter));
	}

	@Override
	public @NotNull String process(@Nullable Object parameter)
	{
		return FormattedStringPlaceholderProcessor.INSTANCE.process(getName(parameter));
	}

	private @NotNull String getName(@Nullable Object parameter)
	{
		if (parameter instanceof ItemStack)
		{
			return itemNameResolver.getDisplayName((ItemStack) parameter);
		}
		else if (parameter instanceof ItemStackWrapper)
		{
			return ((ItemStackWrapper) parameter).getItemName();
		}
		else if (parameter instanceof Block)
		{
			return itemNameResolver.getName((Block) parameter);
		}
		else if (parameter instanceof Material)
		{
			return itemNameResolver.getName((Material) parameter);
		}
		else if (parameter instanceof MinecraftMaterial)
		{
			return itemNameResolver.getName((MinecraftMaterial) parameter);
		}
		return itemNameResolver.getName(Material.AIR);
	}
}