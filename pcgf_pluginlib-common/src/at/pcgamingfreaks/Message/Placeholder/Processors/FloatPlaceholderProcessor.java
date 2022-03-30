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

package at.pcgamingfreaks.Message.Placeholder.Processors;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class FloatPlaceholderProcessor implements IPlaceholderProcessor
{
	private final DecimalFormat decimalFormat;

	public FloatPlaceholderProcessor(int precision)
	{
		decimalFormat = new DecimalFormat("0");
		decimalFormat.setMaximumFractionDigits(precision);
	}

	public FloatPlaceholderProcessor(final @NotNull DecimalFormat decimalFormat)
	{
		this.decimalFormat = decimalFormat;
	}

	@Override
	public @NotNull String process(Object parameter)
	{
		return decimalFormat.format(parameter);
	}
}