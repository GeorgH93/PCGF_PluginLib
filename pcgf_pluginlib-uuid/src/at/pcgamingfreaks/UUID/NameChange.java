/*
 *   Copyright (C) 2021 GeorgH93
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

package at.pcgamingfreaks.UUID;

import java.util.Date;

/**
 * A helper class to store the name changes and dates
 */
public class NameChange
{
	/**
	 * The name to which the name was changed
	 */
	public String name;

	/**
	 * DateTime of the name change in UNIX time (without milliseconds)
	 */
	public long changedToAt;

	/**
	 * Gets the date of a name change
	 *
	 * @return Date of the name change
	 */
	public Date getChangeDate()
	{
		return new Date(changedToAt);
	}
}