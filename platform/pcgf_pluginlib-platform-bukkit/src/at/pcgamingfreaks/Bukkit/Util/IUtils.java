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

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.IPlatformDependent;
import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.PlatformResolver;
import at.pcgamingfreaks.ServerType;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IUtils extends IPlatformDependent
{
	IUtils INSTANCE = PlatformResolver.createPlatformInstance(IUtils.class, ServerType.isPaperCompatible() && MCVersion.isNewerOrEqualThan(MCVersion.MC_NMS_1_20_R4));

	Object getHandle(final @NotNull Player player);
	int getPing(@NotNull Player player);
	void sendPacket(@NotNull Player player, @NotNull Object packet);
	Object jsonToIChatComponent(@NotNull String json);
}