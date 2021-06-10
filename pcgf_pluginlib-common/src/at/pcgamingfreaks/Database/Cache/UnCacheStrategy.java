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

package at.pcgamingfreaks.Database.Cache;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum UnCacheStrategy
{
	ON_DISCONNECT,
	ON_DISCONNECT_DELAYED,
	INTERVAL,
	INTERVAL_DELAYED;

	/**
	 * @param value The name of the uncache strategy.
	 * @return The uncache strategy that representet by the given name.
	 * @throws IllegalArgumentException Thrown if no uncache strategy matches the name
	 */
	@SuppressWarnings("SpellCheckingInspection")
	public static UnCacheStrategy getStrategy(@NotNull String value) throws IllegalArgumentException
	{
		value = value.toLowerCase(Locale.ENGLISH);
		switch(value)
		{
			case "ondisconnect": case "on_disconnect": return ON_DISCONNECT;
			case "ondisconnectdelayed": case "on_disconnect_delayed": return ON_DISCONNECT_DELAYED;
			case "intervalchecked": case "interval_delayed": return INTERVAL_DELAYED;
			case "interval": return INTERVAL;
		}
		throw new IllegalArgumentException("Unknown UnCacheStrategy '" + value + "'");
	}
}