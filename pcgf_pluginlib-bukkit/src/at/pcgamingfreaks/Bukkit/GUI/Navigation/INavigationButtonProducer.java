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

package at.pcgamingfreaks.Bukkit.GUI.Navigation;

import at.pcgamingfreaks.Bukkit.GUI.GuiButton;
import at.pcgamingfreaks.Bukkit.GUI.MultiPageGui;

import org.jetbrains.annotations.NotNull;

public interface INavigationButtonProducer
{
	@NotNull GuiButton produceButton(final @NotNull MultiPageGui gui, final int page, final int pages);

	@NotNull GuiButton produceButtonCurrentPage(final @NotNull MultiPageGui gui, final int page, final int pages);

	@NotNull GuiButton produceNextButton(final @NotNull MultiPageGui gui, final int currentPage, final int pages);

	@NotNull GuiButton producePreviousButton(final @NotNull MultiPageGui gui, final int currentPage, final int pages);
}