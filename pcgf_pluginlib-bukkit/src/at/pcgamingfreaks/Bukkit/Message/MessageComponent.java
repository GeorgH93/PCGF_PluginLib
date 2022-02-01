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
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Message.MessageFormat;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Message component class.
 *
 * @deprecated Use {@link at.pcgamingfreaks.Message.MessageComponent} instead. Use MessageBuilder if you need the tooltips.
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.0.37")
public final class MessageComponent extends at.pcgamingfreaks.Message.MessageComponent
{
	//region Constructors
	/**
	 * Creates a new empty MessageComponent instance.
	 */
	public MessageComponent() {}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text   The text for the {@link MessageComponent}.
	 * @param formats The style for the {@link MessageComponent}.
	 */
	public MessageComponent(String text, final MessageFormat... formats)
	{
		super(text, formats);
	}

	/**
	 * Creates a new empty MessageComponent instance.
	 *
	 * @param text    The text for the {@link MessageComponent}.
	 * @param color   The color for the {@link MessageComponent}.
	 * @param formats The style for the {@link MessageComponent}.
	 */
	public MessageComponent(final @NotNull String text, final @Nullable MessageColor color, final MessageFormat... formats)
	{
		super(text, color, formats);
	}
	//endregion

	//region Message modifier (getter/setter).
	/**
	 * Gets the color of the component.
	 *
	 * @return The color of the component as a {@link ChatColor}, null if no color is defined.
	 *
	 * @deprecated Use {@link at.pcgamingfreaks.Message.MessageComponent#getColor()} instead.
	 */
	@Deprecated
	public ChatColor getChatColor()
	{
		return ChatColor.valueOf(getColorString().toUpperCase(Locale.ROOT));
	}
	//endregion

	/**
	 * Sets the color of the component.
	 *
	 * @param color The new color of the component.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).
	 */
	public MessageComponent setColor(MessageColor color) throws IllegalArgumentException
	{
		if(color != null && color.isRGB() && !MCVersion.supportsRgbColors()) color = color.getFallbackColor(); // Old MC versions do not support RGB colors, convert them to supported colors
		super.setColor(color);
		return this;
	}

	/**
	 * Set the behavior of the component to display information about an achievement when the client hovers over the text.
	 * Not supported in MC 1.12 and newer.
	 *
	 * @param achievement The achievement to display.
	 * @return This message component instance.
	 */
	public MessageComponent achievementTooltip(Object achievement)
	{
		MessageTooltipFactory.achievementTooltip(this, achievement);
		return this;
	}

	/**
	 * Set the behavior of the component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param statistic The statistic to display.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied.
	 */
	public MessageComponent statisticTooltip(Statistic statistic) throws IllegalArgumentException
	{
		MessageTooltipFactory.statisticTooltip(this, statistic);
		return this;
	}

	/**
	 * Set the behavior of the component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param statistic The statistic to display.
	 * @param material The sole material parameter to the statistic.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
	 */
	public MessageComponent statisticTooltip(Statistic statistic, Material material) throws IllegalArgumentException
	{
		MessageTooltipFactory.statisticTooltip(this, statistic, material);
		return this;
	}

	/**
	 * Set the behavior of the component to display information about a parameterless statistic when the client hovers over the text.
	 *
	 * @param statistic The statistic to display.
	 * @param entity The sole entity type parameter to the statistic.
	 * @return This message component instance.
	 * @exception IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
	 */
	public MessageComponent statisticTooltip(Statistic statistic, EntityType entity) throws IllegalArgumentException
	{
		MessageTooltipFactory.statisticTooltip(this, statistic, entity);
		return this;
	}

	/**
	 * Set the behavior of the component to display information about an item when the client hovers over the text.
	 *
	 * @param itemStack The stack for which to display information.
	 * @return This message component instance.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public MessageComponent itemTooltip(ItemStack itemStack)
	{
		MessageTooltipFactory.itemTooltip(this, itemStack);
		return this;
	}
}