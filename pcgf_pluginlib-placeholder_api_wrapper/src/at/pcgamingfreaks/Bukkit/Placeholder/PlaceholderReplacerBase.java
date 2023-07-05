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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PlaceholderReplacerBase implements PlaceholderReplacer
{
	@Override
	public @NotNull String getName()
	{
		if(getClass().isAnnotationPresent(PlaceholderName.class))
		{
			String name = getClass().getAnnotation(PlaceholderName.class).name();
			if(!name.isEmpty()) return name;
		}
		return this.getClass().getSimpleName();
	}

	@Override
	public @NotNull Collection<String> getAliases()
	{
		List<String> aliasesList = new ArrayList<>();
		if(getClass().isAnnotationPresent(PlaceholderName.class))
		{
			String[] aliases = getClass().getAnnotation(PlaceholderName.class).aliases();
			for(String alias : aliases)
			{
				if(!alias.isEmpty()) aliasesList.add(alias);
			}
		}
		return aliasesList;
	}
}
