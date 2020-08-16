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

package at.pcgamingfreaks.Config;

import at.pcgamingfreaks.Configuration;
import at.pcgamingfreaks.yaml.YAML;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public interface IConfig
{
	@NotNull YAML getConfigE() throws Configuration.ConfigNotInitializedException;

	@NotNull Logger getLogger();
}