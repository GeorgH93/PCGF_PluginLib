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

import at.pcgamingfreaks.Message.Placeholder.Processors.IPlaceholderProcessor;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ItemAmountPlaceholderProcessor implements IPlaceholderProcessor
{
	public static final ItemAmountPlaceholderProcessor INSTANCE = new ItemAmountPlaceholderProcessor();

	@Override
	public @NotNull String process(@Nullable Object parameter)
	{
		if (parameter instanceof ItemStack)
		{
			return Objects.toString(((ItemStack) parameter).getAmount());
		}
		return "0";
	}
}