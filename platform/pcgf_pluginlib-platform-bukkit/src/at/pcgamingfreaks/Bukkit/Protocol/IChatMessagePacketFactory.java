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

package at.pcgamingfreaks.Bukkit.Protocol;

import at.pcgamingfreaks.Bukkit.IPlatformDependent;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IChatMessagePacketFactory extends IPlatformDependent
{
	Object makeChatPackage(final @NotNull String json);

	Object makeChatPackage(final @NotNull String json, final @NotNull UUID sender);

	Object makeChatPackageSystem(final @NotNull String json);

	Object makeChatPackageActionBar(final @NotNull String json);
}