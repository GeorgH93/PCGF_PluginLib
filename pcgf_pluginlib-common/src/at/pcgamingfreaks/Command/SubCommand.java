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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents a ready to use base class for sub commands.
 *
 * @param <COMMAND_SENDER> The command sender class of either Bukkit/Spigot or BungeeCord
 */
public abstract class SubCommand<COMMAND_SENDER>
{
	private final String name, description,permission;
	private final List<String> aliases;

	protected SubCommand(@NotNull String name, @NotNull String description, @Nullable String permission, @Nullable String... aliases)
	{
		this.name = name.toLowerCase(Locale.ROOT);
		if(aliases != null)
		{
			this.aliases = new ArrayList<>(aliases.length + 1);
			for(String alias : aliases)
			{
				if(alias != null) this.aliases.add(alias.toLowerCase(Locale.ROOT));
			}
		}
		else
		{
			this.aliases = new ArrayList<>(1);
		}
		if(!this.aliases.contains(this.name)) this.aliases.add(this.name);
		this.description = description;
		this.permission = permission;
	}

	//region Getters
	/**
	 * Gets a list with the aliases of the command.
	 *
	 * @return List of aliases of the command.
	 */
	public @NotNull List<String> getAliases()
	{
		return this.aliases;
	}

	/**
	 * Gets the name of the command.
	 *
	 * @return The commands name.
	 */
	public @NotNull String getName()
	{
		return this.name;
	}

	/**
	 * Gets the name of the command in the used language.
	 *
	 * @return The commands name translated into the used language.
	 */
	public @NotNull String getTranslatedName()
	{
		return this.aliases.get(0);
	}

	/**
	 * Gets the permission that is needed for the command.
	 *
	 * @return The permission for the command.
	 */
	public @Nullable String getPermission()
	{
		return this.permission;
	}

	/**
	 * Gets the description of the command.
	 *
	 * @return The commands description.
	 */
	public @NotNull String getDescription()
	{
		return description;
	}
	//endregion

	//region Command Management Stuff
	/**
	 * The command got closed. Close stuff you no longer need.
	 */
	public void close() {}

	@Deprecated
	public void registerSubCommands() {}

	@Deprecated
	public void unRegisterSubCommands() {}

	/**
	 * Allows performing actions before the sub-command will be registered in the command manager.
	 */
	public void beforeRegister() {}

	/**
	 * Allows performing actions after the sub-command has been registered in the command manager.
	 */
	public void afterRegister() {}

	/**
	 * Allows performing actions before the sub-command will be un-registered from the command manager.
	 */
	public void beforeUnregister() {}

	/**
	 * Allows performing actions after the sub-command has been un-registered from the command manager.
	 */
	public void afterUnRegister() {}
	//endregion

	/**
	 * Checks if a user can use the command.
	 *
	 * @param sender The player/console that should be checked.
	 * @return True if he can use the command, false if not.
	 */
	public abstract boolean canUse(@NotNull COMMAND_SENDER sender);

	//region Command Help Stuff
	/**
	 * Executes some basic checks and gets the help.
	 *
	 * @param requester The command sender that requested help.
	 * @return The list of help data elements.
	 */
	public @Nullable List<HelpData> doGetHelp(@NotNull COMMAND_SENDER requester)
	{
		return (canUse(requester)) ? getHelp(requester) : null;
	}

	/**
	 * Shows the help to a given command sender.
	 *
	 * @param sendTo               The command sender that requested help.
	 * @param usedMainCommandAlias The used alias of the main command.
	 */
	public abstract void showHelp(@NotNull COMMAND_SENDER sendTo, @NotNull String usedMainCommandAlias);

	/**
	 * Gets the help for a given command sender.
	 *
	 * @param requester The command sender that requested help.
	 * @return All the help data for this command.
	 */
	public abstract @Nullable List<HelpData> getHelp(@NotNull COMMAND_SENDER requester);
	//endregion

	//region Command Execution Stuff
	/**
	 * Executes some basic checks and runs the command afterwards.
	 *
	 * @param sender           Source of the command.
	 * @param mainCommandAlias Alias of the plugins main command which has been used.
	 * @param alias            Alias of the command which has been used.
	 * @param args             Passed command arguments.
	 */
	public abstract void doExecute(@NotNull COMMAND_SENDER sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args);

	/**
	 * Executes the given command returning its success.
	 *
	 * @param sender           Source of the command.
	 * @param mainCommandAlias Alias of the plugins main command which has been used.
	 * @param alias            Alias of the command which has been used.
	 * @param args             Passed command arguments.
	 */
	public abstract void execute(@NotNull COMMAND_SENDER sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args);
	//endregion

	//region Tab Complete Stuff
	/**
	 * Executes some basic checks and generates list for tab completion.
	 *
	 * @param sender           Source of the command.
	 * @param mainCommandAlias Alias of the plugins main command which has been used.
	 * @param alias            The alias that has been used.
	 * @param args             The arguments passed to the command, including final partial argument to be completed and command label.
	 * @return A List of possible completions for the final argument or null as default for the command executor.
	 */
	public abstract List<String> doTabComplete(@NotNull COMMAND_SENDER sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args);

	/**
	 * Generates list for tab completion.
	 *
	 * @param sender  Source of the command.
	 * @param mainCommandAlias Alias of the plugins main command which was used.
	 * @param alias   The alias that is used.
	 * @param args    The arguments passed to the command, including final partial argument to be completed and command label.
	 * @return A List of possible completions for the final argument or null as default for the command executor.
	 */
	public abstract List<String> tabComplete(@NotNull COMMAND_SENDER sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args);
	//endregion
}