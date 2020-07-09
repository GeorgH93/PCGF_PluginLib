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
import at.pcgamingfreaks.Bukkit.GUI.MultiPageGui;
import at.pcgamingfreaks.Bukkit.GUI.MultiPageGuiPage;

import org.jetbrains.annotations.NotNull;

public class CompactNavigationStyleProducer extends INavigationStyleProducer
{
	@Override
	public int getSize()
	{
		return 2;
	}

	@Override
	public @NotNull MultiPageGuiPage[] setupPages(final @NotNull MultiPageGui gui, final @NotNull GuiBuilder builder)
	{
		final int pageCount = builder.getPageCount(), slotPrev = builder.getMaxRowsPerPage() * 9 - 2, slotNext = slotPrev + 1;
		final MultiPageGuiPage[] pages = new MultiPageGuiPage[pageCount];
		for(int i = 0; i < pageCount; i++)
		{
			// add controls
			final MultiPageGuiPage page = pages[i] = new MultiPageGuiPage(String.format(builder.getMultiPageTitleFormat(), i + 1), builder.getMaxRowsPerPage());
			page.setButton(slotPrev, navigationButtonProducer.producePreviousButton(gui, i + 1, pageCount));
			page.setButton(slotNext, navigationButtonProducer.produceNextButton(gui, i + 1, pageCount));
		}
		return pages;
	}
}