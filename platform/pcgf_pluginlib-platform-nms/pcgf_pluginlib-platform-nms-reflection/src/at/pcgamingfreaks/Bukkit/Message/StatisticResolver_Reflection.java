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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Message;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Bukkit.OBCReflection;
import at.pcgamingfreaks.Reflection;

import org.bukkit.Achievement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class StatisticResolver_Reflection implements IStatisticResolver
{
	//region Reflection Variables
	private static final transient Class<?> CRAFT_STATISTIC = OBCReflection.getOBCClass("CraftStatistic");
	@SuppressWarnings({ "ConstantConditions" })
	private static final transient Method GET_NMS_ACHIEVEMENT =  (MCVersion.isOlderThan(MCVersion.MC_1_12)) ? OBCReflection.getMethod(CRAFT_STATISTIC, "getNMSAchievement", Reflection.getClass("org.bukkit.Achievement")) : null;
	@SuppressWarnings("ConstantConditions")
	private static final transient Method GET_NMS_STATISTIC = OBCReflection.getMethod(CRAFT_STATISTIC, "getNMSStatistic", Statistic.class);
	private static final transient Method GET_MATERIAL_STATISTIC = OBCReflection.getMethod(CRAFT_STATISTIC, "getMaterialStatistic", Statistic.class, Material.class);
	private static final transient Method GET_ENTITY_STATISTIC = OBCReflection.getMethod(CRAFT_STATISTIC, "getEntityStatistic", Statistic.class, EntityType.class);
	private static final transient Method GET_STATISTIC_NAME = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? NmsReflector.INSTANCE.getNmsMethod("IScoreboardCriteria", "getName") : null;
	private static final transient Field FIELD_STATISTIC_NAME = MCVersion.isOlderThan(MCVersion.MC_1_13) ? NmsReflector.INSTANCE.getNmsField("Statistic", "name") : null;
	//endregion

	private static String getStatisticName(Object statistic) throws Exception
	{
		if(FIELD_STATISTIC_NAME != null) return (String) FIELD_STATISTIC_NAME.get(statistic);
		else return (String) GET_STATISTIC_NAME.invoke(statistic);
	}

	@Override
	public @Nullable String getStatisticName(final @NotNull Statistic statistic)
	{
		try
		{
			return getStatisticName(GET_NMS_STATISTIC.invoke(null, statistic));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public @Nullable String getStatisticName(final @NotNull Statistic statistic, final @NotNull Material material)
	{
		try
		{
			return getStatisticName(GET_MATERIAL_STATISTIC.invoke(null, statistic, material));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public @Nullable String getStatisticName(final @NotNull Statistic statistic, final @NotNull EntityType entityType)
	{
		try
		{
			return getStatisticName(GET_ENTITY_STATISTIC.invoke(null, statistic, entityType));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public @Nullable String getAchievementName(final @NotNull Achievement achievement)
	{
		try
		{
			return getStatisticName(GET_NMS_ACHIEVEMENT.invoke(null, achievement));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public @Nullable String getAchievementName(final @NotNull Advancement advancement)
	{
		return null;
	}
}