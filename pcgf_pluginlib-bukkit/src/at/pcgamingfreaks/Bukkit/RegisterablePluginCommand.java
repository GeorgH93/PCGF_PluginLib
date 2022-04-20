/*
 *   Copyright (C) 2022 GeorgH93
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

package at.pcgamingfreaks.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a {@link Command} belonging to a plugin.
 * It can be created, registered and unregistered at any point, and must not be defined upfront in the plugin.yml file.
 *
 * It doesn't extend {@link PluginCommand} because it's a final class. However, it implements all the functions from it.
 *
 * @deprecated Moved to {@link at.pcgamingfreaks.Bukkit.Command.RegisterablePluginCommand}!
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.0.40")
public class RegisterablePluginCommand extends at.pcgamingfreaks.Bukkit.Command.RegisterablePluginCommand
{
	/**
	 * Creates a new command that can be registered. Don't forget to register it!
	 *
	 * @param owner   The plugin this command belongs to
	 * @param name    The name of the command will be used for bukkit's help
	 * @param aliases The aliases of the command
	 */
	public RegisterablePluginCommand(@NotNull Plugin owner, @NotNull String name, @Nullable String... aliases)
	{
		super(owner, name, aliases);
	}
}