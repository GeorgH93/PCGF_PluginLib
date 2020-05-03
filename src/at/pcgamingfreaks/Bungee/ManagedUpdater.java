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

package at.pcgamingfreaks.Bungee;

import at.pcgamingfreaks.Updater.ManagedUpdaterBase;
import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;

import net.md_5.bungee.api.plugin.Plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class ManagedUpdater extends ManagedUpdaterBase<Updater, Plugin>
{
	public ManagedUpdater(final @NotNull Plugin plugin)
	{
		this(plugin, plugin.getLogger());
	}

	public ManagedUpdater(final @NotNull Plugin plugin, final @NotNull Logger logger)
	{
		this(plugin, logger, null);
	}

	public ManagedUpdater(final @NotNull Plugin plugin, final @Nullable String channel)
	{
		this(plugin, plugin.getLogger(), channel);
	}

	public ManagedUpdater(final @NotNull Plugin plugin, final @NotNull Logger logger, final @Nullable String channel)
	{
		super(plugin, logger, channel);
	}

	@Override
	protected Updater makeUpdater(final @NotNull UpdateProvider[] updateProviders)
	{
		return new Updater(plugin, isAnnounceDownloadProgress(), isDownloadDependencies(), updateProviders);
	}
}