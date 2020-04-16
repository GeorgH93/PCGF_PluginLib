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

package at.pcgamingfreaks.Bukkit.GUI.Navigation;

import at.pcgamingfreaks.Bukkit.GUI.GuiBuilder;
import at.pcgamingfreaks.Bukkit.GUI.GuiButton;
import at.pcgamingfreaks.Bukkit.GUI.MultiPageGui;
import at.pcgamingfreaks.Bukkit.GUI.MultiPageGuiPage;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public abstract class INavigationStyleProducer
{
	@Getter @Setter protected  @NotNull INavigationButtonProducer navigationButtonProducer = new DefaultNavigationButtonProducer();

	public abstract int getSize();

	protected  @NotNull GuiButton getPageButton(GuiButton[] pageButtons, MultiPageGui gui, int pageId, int currentPageId)
	{
		return (pageId == currentPageId) ? navigationButtonProducer.produceButtonCurrentPage(gui, pageId + 1, pageButtons.length) : pageButtons[pageId];
	}

	public abstract @NotNull MultiPageGuiPage[] setupPages(final @NotNull MultiPageGui gui, final @NotNull GuiBuilder builder);
}