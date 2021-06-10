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

package at.pcgamingfreaks;

import org.jetbrains.annotations.NotNull;

@Deprecated
public interface LanguageConfiguration
{
	/**
	 * Gets the language to use, defined in the configuration.
	 *
	 * @return The language to use.
	 */
	@NotNull String getLanguage();

	/**
	 * Gets how the language file should be updated, defined in the configuration.
	 *
	 * @return The update method for the language file.
	 */
	@NotNull YamlFileUpdateMethod getLanguageUpdateMode();
}