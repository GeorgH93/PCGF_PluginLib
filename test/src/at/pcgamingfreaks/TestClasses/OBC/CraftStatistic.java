/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.TestClasses.OBC;

import org.bukkit.Achievement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

@SuppressWarnings("unused")
public class CraftStatistic
{
	public static at.pcgamingfreaks.TestClasses.NMS.Achievement getNMSAchievement(Achievement achievement) { return new at.pcgamingfreaks.TestClasses.NMS.Achievement(achievement.name()); }

	public static at.pcgamingfreaks.TestClasses.NMS.Statistic getNMSStatistic(Statistic statistic) { return new at.pcgamingfreaks.TestClasses.NMS.Statistic(statistic.name()); }

	public static at.pcgamingfreaks.TestClasses.NMS.Statistic getMaterialStatistic(Statistic statistic, Material material) { return new at.pcgamingfreaks.TestClasses.NMS.Statistic(statistic.name() + "-" + material.name()); }

	public static at.pcgamingfreaks.TestClasses.NMS.Statistic getEntityStatistic(Statistic statistic, EntityType entityType) { return new at.pcgamingfreaks.TestClasses.NMS.Statistic(statistic.name() + "-" + entityType.name()); }
}