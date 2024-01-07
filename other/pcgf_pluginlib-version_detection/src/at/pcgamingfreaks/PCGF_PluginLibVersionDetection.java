/*
 *   Copyright (C) 2024 GeorgH93
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

package at.pcgamingfreaks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class PCGF_PluginLibVersionDetection
{
	private PCGF_PluginLibVersionDetection() { /* hide implicit constructor */ }

	/**
	 * Gets the version of the PCGF_PluginLib or null if the Bukkit version of the plugin lib is not installed.
	 * @return The version of the lib or null if not installed.
	 */
	public static @Nullable String getVersionBukkit()
	{
		if (checkIfPluginLibExists("at.pcgamingfreaks.PluginLib.Bukkit.PluginLib"))
		{
			return getVersion();
		}
		return null;
	}

	/**
	 * Gets the version of the PCGF_PluginLib or null if the Bungee version of the plugin lib is not installed.
	 * @return The version of the lib or null if not installed.
	 */
	public static @Nullable String getVersionBungee()
	{
		if (checkIfPluginLibExists("at.pcgamingfreaks.PluginLib.Bungee.PluginLib"))
		{
			return getVersion();
		}
		return null;
	}

	/**
	 * Gets the version of the PCGF_PluginLib
	 * @return Version of the lib or 0.0 if there was an issue detecting the version.
	 */
	public static @NotNull String getVersion()
	{
		try
		{
			Class<?> magicValuesClass = Class.forName("at.pcgamingfreaks.PluginLib.MagicValues");
			Field versionField = magicValuesClass.getDeclaredField("PCGF_PLUGIN_LIB_VERSION");
			return (String) versionField.get(null);
		}
		catch(Exception ignored) {}
		return "0.0";
	}

	private static boolean checkIfPluginLibExists(final @NotNull String platformMainClass)
	{
		try
		{
			Class.forName(platformMainClass);
			return true;
		}
		catch(Exception ignored) {}
		return false;
	}
}