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

package at.pcgamingfreaks.Updater;

import at.pcgamingfreaks.Config.IConfig;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface IUpdateConfiguration extends IConfig
{
	default boolean useUpdater()
	{
		return getConfigE().getBoolean("Misc.AutoUpdate.Enable", getConfigE().getBoolean("Misc.AutoUpdate", true));
	}

	default UpdateMode getUpdateMode()
	{
		if(!useUpdater()) return UpdateMode.DISABLED;
		String updateMode = getConfigE().getString("Misc.AutoUpdate.UpdateMode", "UPDATE");
		try
		{
			return UpdateMode.valueOf(updateMode.toUpperCase(Locale.ENGLISH));
		}
		catch(IllegalArgumentException e)
		{
			getLogger().warning("Unknown update mode: " + updateMode);
		}
		return UpdateMode.UPDATE;
	}

	default @Nullable String getUpdateChannel()
	{
		return getConfigE().getString("Misc.AutoUpdate.Channel", null);
	}
}