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

package at.pcgamingfreaks.Bukkit.Message;

import at.pcgamingfreaks.Bukkit.IPlatformDependent;

import org.bukkit.Achievement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IStatisticResolver extends IPlatformDependent
{
	@Nullable String getStatisticName(final @NotNull Statistic statistic);
	@Nullable String getStatisticName(final @NotNull Statistic statistic, final @NotNull Material material);
	@Nullable String getStatisticName(final @NotNull Statistic statistic, final @NotNull EntityType entityType);
	@Nullable String getAchievementName(final @NotNull Achievement achievement);
	@Nullable String getAchievementName(final @NotNull Advancement advancement);
}