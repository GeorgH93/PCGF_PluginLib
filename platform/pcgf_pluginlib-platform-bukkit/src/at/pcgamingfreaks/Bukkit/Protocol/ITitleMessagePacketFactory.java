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

package at.pcgamingfreaks.Bukkit.Protocol;

import at.pcgamingfreaks.Bukkit.IPlatformDependent;
import at.pcgamingfreaks.Bukkit.PlatformResolver;

import org.jetbrains.annotations.NotNull;

public interface ITitleMessagePacketFactory extends IPlatformDependent
{
	ITitleMessagePacketFactory INSTANCE = PlatformResolver.createPlatformInstance(ITitleMessagePacketFactory.class/*, ServerType.isPaperCompatible() && MCVersion.isNewerOrEqualThan(MCVersion.MC_NMS_1_20_R4)*/);

	Object makeTitlePacket(@NotNull String json);
	Object makeSubTitlePacket(@NotNull String json);
	Object makeTitlePacketTime(int fadeIn, int stay, int fadeOut);
	Object makeTitlePacketActionBar(@NotNull String json);
}