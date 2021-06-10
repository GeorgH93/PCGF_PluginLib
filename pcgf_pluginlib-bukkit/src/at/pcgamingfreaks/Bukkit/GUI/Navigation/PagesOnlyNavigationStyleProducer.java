/*
 *   Copyright (C) 2021 GeorgH93
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

import at.pcgamingfreaks.Bukkit.GUI.GuiBuilder;
import at.pcgamingfreaks.Bukkit.GUI.GuiButton;
import at.pcgamingfreaks.Bukkit.GUI.MultiPageGui;
import at.pcgamingfreaks.Bukkit.GUI.MultiPageGuiPage;

import org.jetbrains.annotations.NotNull;

public class PagesOnlyNavigationStyleProducer extends INavigationStyleProducer
{
	@Override
	public int getSize()
	{
		return 9;
	}

	@Override
	public @NotNull MultiPageGuiPage[] setupPages(final @NotNull MultiPageGui gui, final @NotNull GuiBuilder builder)
	{
		final int pageCount = builder.getPageCount(), slot = (builder.getMaxRowsPerPage() - 1) * 9;
		//region setup page buttons
		final GuiButton[] pageButtons = new GuiButton[pageCount];
		for(int i = 0; i < pageCount; i++)
		{
			pageButtons[i] = navigationButtonProducer.produceButton(gui, i + 1, pageCount);
		}
		//endregion
		//region setup gui pages
		final MultiPageGuiPage[] pages = new MultiPageGuiPage[pageCount];
		for(int i = 0; i < pageCount; i++)
		{
			// add controls
			final MultiPageGuiPage page = pages[i] = new MultiPageGuiPage(builder.getMultiPageTitleFormat(), builder.getMaxRowsPerPage(), i + 1);
			if(pageCount > 9)
			{
				int center = Math.min(Math.max(i, 4), pageButtons.length - 5);
				page.setButton(slot, getPageButton(pageButtons, gui, 0, i)); // First page
				page.setButton(slot + 3, getPageButton(pageButtons, gui, center - 1, i)); // Center -1 page
				page.setButton(slot + 4, getPageButton(pageButtons, gui, center, i)); // Center page
				page.setButton(slot + 5, getPageButton(pageButtons, gui, center + 1, i)); // Center +1 page
				int x = Math.max(1, Math.min((int) Math.round(center/3.0), center - 3));
				page.setButton(slot + 1, getPageButton(pageButtons, gui, x, i));
				x = Math.max(x, Math.min((int) Math.round((center * 2)/3.0), center - 2));
				page.setButton(slot + 2, getPageButton(pageButtons, gui, x, i));
				x = Math.max(center + 2, Math.min((int) Math.round(center + (pageCount - center) / 3.0), pageCount - 3));
				page.setButton(slot + 6, getPageButton(pageButtons, gui, x, i));
				x = Math.max(x + 1, Math.min((int) Math.round(center + 2 * (pageCount - center) / 3.0), pageCount - 2));
				page.setButton(slot + 7, getPageButton(pageButtons, gui, x, i));

				page.setButton(slot + 8, getPageButton(pageButtons, gui, pageCount - 1, i)); // Last page
			}
			else
			{
				for(int j = 0; j < pageCount; j++)
				{
					page.setButton(slot + j, getPageButton(pageButtons, gui, j, i));
				}
			}
		}
		//endregion
		return pages;
	}
}