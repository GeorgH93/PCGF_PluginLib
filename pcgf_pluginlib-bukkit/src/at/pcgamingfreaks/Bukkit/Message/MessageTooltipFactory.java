/*
 *   Copyright (C) 2022 GeorgH93
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
import at.pcgamingfreaks.Bukkit.PlatformResolver;
import at.pcgamingfreaks.Bukkit.Util.InventoryUtils;
import at.pcgamingfreaks.Message.MessageHoverEvent;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public final class MessageTooltipFactory
{
	private static final IStatisticResolver STATISTIC_RESOLVER = PlatformResolver.createPlatformInstance(IStatisticResolver.class);

	/**
	 * Set the behavior of the component to display information about an achievement when the client hovers over the text.
	 * Not supported in MC 1.12 and newer.
	 *
	 * @param messageComponent MessageComponent that the tooltip should be added to.
	 * @param achievement The achievement to display.
	 * @exception IllegalStateException If the minecraft version is incompatible with achievements.
	 */
	public static void achievementTooltip(at.pcgamingfreaks.Message.MessageComponent messageComponent, Object achievement)
	{
		if(MCVersion.isOlderThan(MCVersion.MC_1_12))
		{
			assert achievement instanceof Achievement;
			messageComponent.onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, STATISTIC_RESOLVER.getAchievementName((Achievement) achievement));
		}
		else
		{
			throw new IllegalStateException("Achievements are not supported in your minecraft version!");
		}
	}

	/**
	 * Set the behavior of the component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param messageComponent MessageComponent that the tooltip should be added to.
	 * @param statistic The statistic to display.
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied.
	 */
	public static void statisticTooltip(at.pcgamingfreaks.Message.MessageComponent messageComponent, Statistic statistic) throws IllegalArgumentException
	{
		Statistic.Type type = statistic.getType();
		if (type != Statistic.Type.UNTYPED) throw new IllegalArgumentException("That statistic requires an additional " + type + " parameter!");
		messageComponent.onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, STATISTIC_RESOLVER.getStatisticName(statistic));
	}

	/**
	 * Set the behavior of the component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param messageComponent MessageComponent that the tooltip should be added to.
	 * @param statistic The statistic to display.
	 * @param material The sole material parameter to the statistic.
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
	 */
	public static void statisticTooltip(at.pcgamingfreaks.Message.MessageComponent messageComponent, Statistic statistic, Material material) throws IllegalArgumentException
	{
		Statistic.Type type = statistic.getType();
		if (type == Statistic.Type.UNTYPED) throw new IllegalArgumentException("That statistic needs no additional parameter!");
		if ((type == Statistic.Type.BLOCK && material.isBlock()) || type == Statistic.Type.ENTITY) throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		messageComponent.onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, STATISTIC_RESOLVER.getStatisticName(statistic, material));
	}

	/**
	 * Set the behavior of the component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param messageComponent MessageComponent that the tooltip should be added to.
	 * @param statistic The statistic to display.
	 * @param entity The sole entity type parameter to the statistic.
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
	 */
	public static void statisticTooltip(at.pcgamingfreaks.Message.MessageComponent messageComponent, Statistic statistic, EntityType entity) throws IllegalArgumentException
	{
		Statistic.Type type = statistic.getType();
		if (type == Statistic.Type.UNTYPED) throw new IllegalArgumentException("That statistic needs no additional parameter!");
		if (type != Statistic.Type.ENTITY) throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		messageComponent.onHover(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, STATISTIC_RESOLVER.getStatisticName(statistic, entity));
	}

	/**
	 * Set the behavior of the component to display information about an item when the client hovers over the text.
	 *
	 * @param messageComponent MessageComponent that the tooltip should be added to.
	 * @param itemStack The stack for which to display information.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static void itemTooltip(at.pcgamingfreaks.Message.MessageComponent messageComponent, ItemStack itemStack)
	{
		messageComponent.itemTooltip(InventoryUtils.convertItemStackToJson(itemStack, Bukkit.getLogger()));
	}
}