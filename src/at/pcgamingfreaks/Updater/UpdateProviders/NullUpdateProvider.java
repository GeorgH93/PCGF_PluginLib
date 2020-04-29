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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class NullUpdateProvider implements UpdateProvider
{
	@Override
	public @NotNull String getName()
	{
		return "null";
	}

	@Override
	public @NotNull UpdateResult query()
	{
		return UpdateResult.NO_UPDATE;
	}

	@Override
	public @NotNull Version getLatestVersion()
	{
		return new Version(Integer.MAX_VALUE);
	}

	@Override
	public @NotNull URL getLatestFileURL() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The null update provider does not implement anything!");
	}

	@Override
	public @NotNull String getLatestFileName()
	{
		return "null.jar";
	}

	@Override
	public @NotNull String getLatestName() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The null update provider does not implement anything!");
	}

	@Override
	public @NotNull String getLatestMinecraftVersion() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The null update provider does not implement anything!");
	}

	@Override
	public @NotNull String[] getLatestMinecraftVersions() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The null update provider does not implement anything!");
	}

	@Override
	public @NotNull ReleaseType getLatestReleaseType()
	{
		return ReleaseType.UNKNOWN;
	}

	@Override
	public @NotNull String getLatestChecksum() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The null update provider does not implement anything!");
	}

	@Override
	public @NotNull String getLatestChangelog() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The null update provider does not implement anything!");
	}

	@Override
	public @NotNull UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The null update provider does not implement anything!");
	}

	@Override
	public @NotNull UpdateFile[] getUpdateHistory() throws RequestTypeNotAvailableException
	{
		throw new RequestTypeNotAvailableException("The null update provider does not implement anything!");
	}

	@Override
	public boolean providesDownloadURL()
	{
		return false;
	}

	@Override
	public boolean providesMinecraftVersion()
	{
		return false;
	}

	@Override
	public boolean providesMinecraftVersions()
	{
		return false;
	}

	@Override
	public boolean providesChangelog()
	{
		return false;
	}

	@Override
	public @NotNull ChecksumType providesChecksum()
	{
		return ChecksumType.NONE;
	}

	@Override
	public boolean providesUpdateHistory()
	{
		return false;
	}

	@Override
	public boolean providesDependencies()
	{
		return false;
	}
}