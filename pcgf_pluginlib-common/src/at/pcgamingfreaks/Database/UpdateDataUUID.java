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

package at.pcgamingfreaks.Database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Helper class for converting names to UUID's and for fixing UUID's that are in the wrong format.
 */
@AllArgsConstructor
@Getter
class UpdateDataUUID
{
	private final String name;
	@Setter	private String uuid;
	private final long id;

	public String formatUuid(boolean useSeparators)
	{
		if (useSeparators)
		{
			if (uuid.contains("-"))
			{
				return uuid;
			}
			return uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
		}
		return uuid.replace("-", "");
	}
}