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

package at.pcgamingfreaks.PluginLib;

import at.pcgamingfreaks.Config.Configuration;
import at.pcgamingfreaks.Config.ILanguageConfiguration;
import at.pcgamingfreaks.Database.DatabaseConnectionConfiguration;
import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.Updater.IUpdateConfiguration;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;

public final class Config extends Configuration implements IUpdateConfiguration, ILanguageConfiguration, DatabaseConnectionConfiguration
{
	public Config(final @NotNull IPlugin plugin, final Version version)
	{
		super(plugin, version);
	}
}