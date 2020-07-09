/*
 * Copyright (C) 2018 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.TestClasses;

import at.pcgamingfreaks.Command.HelpData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SubCommand<TESTBUKKITPLAYER> extends at.pcgamingfreaks.Command.SubCommand<TESTBUKKITPLAYER>
{
	public boolean canUse = false;

	public SubCommand(@NotNull String name, @NotNull String description, @Nullable String permission, @Nullable String[] aliases)
	{
		super(name, description, permission, aliases);
	}

	@Override
	public boolean canUse(@NotNull TESTBUKKITPLAYER testBukkitPlayer) { return canUse; }

	@Override
	public void showHelp(@NotNull TESTBUKKITPLAYER sendTo, @NotNull String usedMainCommandAlias) { }

	@Nullable
	@Override
	public List<HelpData> getHelp(@NotNull TESTBUKKITPLAYER requester) { return new ArrayList<>(); }

	@Override
	public void doExecute(@NotNull TESTBUKKITPLAYER testBukkitPlayer, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args) { }

	@Override
	public void execute(@NotNull TESTBUKKITPLAYER testBukkitPlayer, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args) { }

	@Override
	public List<String> doTabComplete(@NotNull TESTBUKKITPLAYER testBukkitPlayer, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args) { return null; }

	@Override
	public List<String> tabComplete(@NotNull TESTBUKKITPLAYER testBukkitPlayer, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args) { return null; }
}