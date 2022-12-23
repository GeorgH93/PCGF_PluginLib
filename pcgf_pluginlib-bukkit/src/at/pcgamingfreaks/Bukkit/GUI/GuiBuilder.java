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

package at.pcgamingfreaks.Bukkit.GUI;

import at.pcgamingfreaks.Bukkit.GUI.Navigation.CompactNavigationStyleProducer;
import at.pcgamingfreaks.Bukkit.GUI.Navigation.DefaultNavigationStyleProducer;
import at.pcgamingfreaks.Bukkit.GUI.Navigation.INavigationStyleProducer;
import at.pcgamingfreaks.Bukkit.GUI.Navigation.PagesOnlyNavigationStyleProducer;
import at.pcgamingfreaks.Bukkit.Message.Message;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuiBuilder
{
	private final @NotNull List<GuiButton> buttons = new ArrayList<>();
	@Getter private int minRowsPerPage = 1, maxRowsPerPage = 6;
	@Getter @Setter private @NotNull Message title, multiPageTitleFormat;
	@Getter @Setter private @NotNull INavigationStyleProducer navigationStyleProducer = new DefaultNavigationStyleProducer();

	public GuiBuilder(final @NotNull String title)
	{
		this(new Message(title));
	}

	public GuiBuilder(final @NotNull String title, final @Nullable String multiPageTitleFormat)
	{
		this(new Message(title), multiPageTitleFormat != null ? new Message(multiPageTitleFormat) : null);
	}

	public GuiBuilder(final @NotNull Message title)
	{
		this(title, title);
	}

	public GuiBuilder(final @NotNull Message title, @Nullable Message multiPageTitleFormat)
	{
		this.title = title;
		if(multiPageTitleFormat == null) multiPageTitleFormat = title;
		this.multiPageTitleFormat = multiPageTitleFormat.placeholder("{PageNr}");
	}

	public void setMinRowsPerPage(int rows)
	{
		if (rows < 1 || rows > 6) throw new IllegalArgumentException("The amount of rows must be between 1 and 6!");
		Validate.isTrue(rows <= maxRowsPerPage, "Min rows must be <= max rows (%d)!", maxRowsPerPage);
		minRowsPerPage = rows;
	}

	public void setMaxRowsPerPage(int rows)
	{
		if (rows < 2 || rows > 6) throw new IllegalArgumentException("The amount of rows must be between 2 and 6!");
		maxRowsPerPage = rows;
		if(maxRowsPerPage < minRowsPerPage) minRowsPerPage = rows;
	}

	public void addButton(@Nullable GuiButton button)
	{
		if(button == null) button = GuiButton.EMPTY_BUTTON;
		buttons.add(button);
	}

	public void addButtons(final @NotNull Collection<GuiButton> buttons)
	{
		buttons.forEach(this::addButton);
	}

	public void removeButton(final @NotNull GuiButton button)
	{
		buttons.remove(button);
	}

	public void removeButton(final int index)
	{
		buttons.remove(index);
	}

	public void removeButtons(final @NotNull Collection<GuiButton> buttons)
	{
		this.buttons.removeAll(buttons);
	}

	public void clearButtons()
	{
		buttons.clear();
	}

	public void setNavigationStyle(final NavigationStyle navigationStyle)
	{
		switch(navigationStyle)
		{
			case COMPACT: navigationStyleProducer = new CompactNavigationStyleProducer(); break;
			case DEFAULT: navigationStyleProducer = new DefaultNavigationStyleProducer(); break;
			case PAGES_ONLY: navigationStyleProducer = new PagesOnlyNavigationStyleProducer(); break;
		}
	}

	public int size()
	{
		return buttons.size();
	}

	public int neededRows()
	{
		return (int) Math.ceil(size() / 9.0);
	}

	public int getPageCount()
	{
		return 1 + ((neededRows()) / (maxRowsPerPage - 1));
	}

	public int getItemsPerPage()
	{
		return (maxRowsPerPage * 9) - navigationStyleProducer.getSize();
	}

	public @NotNull IGui build()
	{
		return (size() > maxRowsPerPage * 9) ? buildMultiPageGui() : buildSinglePageGui();
	}

	private @NotNull IGui buildSinglePageGui()
	{
		SimpleGui gui = new SimpleGui(title, Math.max(minRowsPerPage, neededRows()));
		buttons.forEach(gui::addButton);
		return gui;
	}

	private @NotNull IGui buildMultiPageGui()
	{
		final MultiPageGui gui = new MultiPageGui();
		final int itemsPerPage = getItemsPerPage();
		final MultiPageGuiPage[] pages = navigationStyleProducer.setupPages(gui, this);

		//region add buttons
		int count = 0, page = 0;
		for(GuiButton button : buttons)
		{
			pages[page].addButton(button);
			if((++count / itemsPerPage) > page) page++;
		}
		//endregion

		gui.setPages(pages);
		return gui;
	}
}