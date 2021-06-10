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

package at.pcgamingfreaks.Config;

import at.pcgamingfreaks.LanguageConfiguration;
import at.pcgamingfreaks.YamlFileUpdateMethod;

import org.jetbrains.annotations.NotNull;

public interface ILanguageConfiguration extends LanguageConfiguration, IConfig
{
	default @NotNull String getLanguageKey()
	{
		return "Language.Language";
	}

	default @NotNull String getLanguageUpdateModeKey()
	{
		return "Language.UpdateMode";
	}

	/**
	 * Gets the language to use, defined in the configuration.
	 *
	 * @return The language to use.
	 */
	default @NotNull String getLanguage()
	{
		return getConfigE().getString(getLanguageKey(), "en");
	}

	/**
	 * Gets how the language file should be updated, defined in the configuration.
	 *
	 * @return The update method for the language file.
	 */
	default @NotNull YamlFileUpdateMethod getLanguageUpdateMode()
	{
		return YamlFileUpdateMethod.fromString(getConfigE().getString(getLanguageUpdateModeKey(), "upgrade"));
	}
}