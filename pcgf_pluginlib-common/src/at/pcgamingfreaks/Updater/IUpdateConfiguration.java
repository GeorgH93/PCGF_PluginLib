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

package at.pcgamingfreaks.Updater;

import at.pcgamingfreaks.Config.IConfig;
import at.pcgamingfreaks.Utils;

import org.jetbrains.annotations.Nullable;

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
		return Utils.getEnum(updateMode, UpdateMode.UPDATE, getLogger());
	}

	default @Nullable String getUpdateChannel()
	{
		return getConfigE().getString("Misc.AutoUpdate.Channel", null);
	}
}