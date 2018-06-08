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

import java.util.LinkedList;
import java.util.List;

public class SubCommand extends at.pcgamingfreaks.Command.SubCommand<TestBukkitPlayer>
{
	public boolean canUse = false;

	public SubCommand(@NotNull String name, @NotNull String description, @Nullable String permission, @Nullable String[] aliases)
	{
		super(name, description, permission, aliases);
	}

	@Override
	public boolean canUse(@NotNull TestBukkitPlayer testBukkitPlayer) { return canUse; }

	@Override
	public void showHelp(@NotNull TestBukkitPlayer sendTo, @NotNull String usedMainCommandAlias) { }

	@Nullable
	@Override
	public List<HelpData> getHelp(@NotNull TestBukkitPlayer requester) { return new LinkedList<>(); }

	@Override
	public void doExecute(@NotNull TestBukkitPlayer testBukkitPlayer, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args) { }

	@Override
	public void execute(@NotNull TestBukkitPlayer testBukkitPlayer, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args) { }

	@Override
	public List<String> doTabComplete(@NotNull TestBukkitPlayer testBukkitPlayer, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args) { return null; }

	@Override
	public List<String> tabComplete(@NotNull TestBukkitPlayer testBukkitPlayer, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args) { return null; }
}