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

import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class GameModePlaceholderProcessor implements IPlaceholderProcessor
{
	private final @NotNull Map<GameMode, String> gameModeTranslations;

	public GameModePlaceholderProcessor()
	{
		this(null);
	}

	public GameModePlaceholderProcessor(final @Nullable Map<GameMode, String> translations)
	{
		if (translations == null)
		{
			this.gameModeTranslations = new EnumMap<>(GameMode.class);
		}
		else
		{
			this.gameModeTranslations = new EnumMap<>(translations);
		}
		// Make sure that there is a translation for every game-mode
		for(GameMode gm : GameMode.values())
		{
			gameModeTranslations.computeIfAbsent(gm, gameMode -> gameMode.name().toLowerCase(Locale.ROOT));
		}
	}

	@Override
	public @NotNull String process(final @Nullable Object parameter)
	{
		if (parameter instanceof GameMode)
		{
			return gameModeTranslations.get(parameter);
		}
		else if (parameter instanceof Collection)
		{
			StringBuilder builder = new StringBuilder("[");
			for(Object o : ((Collection<?>)parameter))
			{
				if (o instanceof GameMode)
				{
					if (builder.length() > 1) builder.append(", ");
					builder.append(gameModeTranslations.get(o));
				}
			}
			builder.append("]");
			return builder.toString();
		}
		return "unknown";
	}
}