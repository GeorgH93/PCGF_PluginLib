/*
 *   Copyright (C) 2023 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Message.Placeholder.Processors;

import at.pcgamingfreaks.Message.MessageBuilder;
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Message.MessageComponent;
import at.pcgamingfreaks.Message.Placeholder.Processors.IFormattedPlaceholderProcessor;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDisplayNamePlaceholderProcessor implements IFormattedPlaceholderProcessor
{
	private static final MessageComponent NULL_COMPONENT = new MessageComponent("null");

	public static final PlayerDisplayNamePlaceholderProcessor INSTANCE = new PlayerDisplayNamePlaceholderProcessor();

	@Override
	public @NotNull String process(@Nullable Object parameter)
	{
		if (parameter instanceof Player) return ((Player) parameter).getDisplayName();
		else if (parameter instanceof OfflinePlayer)
		{
			String name = ((OfflinePlayer) parameter).getName();
			return MessageColor.GRAY + (name == null ? "null" : name);
		}
		return "Unknown";
	}

	@Override
	public @NotNull MessageComponent processFormatted(@Nullable Object parameter)
	{
		if (parameter instanceof Player)
		{
			MessageBuilder builder = new MessageBuilder((MessageComponent) null);
			builder.appendLegacy(((Player) parameter).getDisplayName());
			return builder.getAsComponent();
		}
		else if (parameter instanceof OfflinePlayer)
		{
			String name = ((OfflinePlayer) parameter).getName();
			return new MessageComponent(name == null ? "null" : name, MessageColor.GRAY);
		}
		return NULL_COMPONENT;
	}
}
