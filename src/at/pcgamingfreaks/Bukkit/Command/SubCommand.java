/*
 *   Copyright (C) 2018 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface SubCommand
{
	void close();

	@NotNull String getName();

	/**
	 * Gets a list with the aliases of the sub command.
	 *
	 * @return List of aliases of the sub command.
	 */
	@NotNull Collection<String> getAliases();

	/**
	 * Checks if a user can use the command.
	 * This check will be used to hide commands the user can't use from the tab complete list.
	 *
	 * @param sender The player/console that should be checked.
	 * @return True if he can use the command, false if not.
	 */
	boolean canUse(@NotNull CommandSender sender);

	/**
	 * Runs the command.
	 *
	 * @param sender           The player/console that has executed the command.
	 * @param mainCommandAlias The alias of the plugins main command which has been used.
	 * @param alias            The alias of the command which has been used.
	 * @param args             The command arguments for the sub command..
	 */
	void doExecute(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args);

	/**
	 * Executes some basic checks and generates list for tab completion.
	 *
	 * @param sender           The player/console that has requested the tab complete.
	 * @param mainCommandAlias The alias of the plugins main command which has been used.
	 * @param alias            The alias that has been used.
	 * @param args             The arguments passed to the command, including final partial argument to be completed and command label.
	 * @return A List of possible completions for the final argument or null as default for the command executor.
	 */
	List<String> doTabComplete(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args);
}