/*
 *   Copyright (C) 2018 GeorgH93
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

package at.pcgamingfreaks;

import org.jetbrains.annotations.Nullable;

/**
 * Allows to check if a version is within a given range.
 */
public class VersionRange
{
	private final Version minVersion, maxVersion;

	public VersionRange(@Nullable String minVersion, @Nullable String maxVersion)
	{
		this((minVersion != null) ? new Version(minVersion) : null, (maxVersion != null) ? new Version(maxVersion) : null);
	}

	public VersionRange(@Nullable Version minVersion, @Nullable Version maxVersion)
	{
		this.minVersion = minVersion;
		this.maxVersion = maxVersion;
	}

	public boolean inRange(String version)
	{
		return inRange(new Version(version));
	}

	public boolean inRange(Version version)
	{
		return (minVersion == null || minVersion.olderOrEqualThan(version)) && (maxVersion == null || maxVersion.newerOrEqualThan(version));
	}

	public boolean inRangeExclusive(String version)
	{
		return inRangeExclusive(new Version(version));
	}

	public boolean inRangeExclusive(Version version)
	{
		return (minVersion == null || minVersion.olderThan(version)) && (maxVersion == null || maxVersion.newerThan(version));
	}
}