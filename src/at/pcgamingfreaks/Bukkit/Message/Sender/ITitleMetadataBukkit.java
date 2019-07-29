/*
 *   Copyright (C) 2019 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Message.Sender.ITitleMetadata;

import org.jetbrains.annotations.NotNull;

interface ITitleMetadataBukkit extends ITitleMetadata
{
	/**
	 * Gets the NMS type of the title. This will be used from the {@link TitleSender} class.
	 *
	 * @return The matching enum for the display type of the title. NMS!!!
	 */
	@NotNull Enum<?> getTitleType();
}