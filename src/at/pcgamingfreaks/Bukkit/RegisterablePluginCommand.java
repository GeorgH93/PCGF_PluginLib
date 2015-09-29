package at.pcgamingfreaks.Bukkit;

import org.apache.commons.lang.Validate;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a {@link Command} belonging to a plugin.
 * It can be created at any point.
 *
 * It doesn't extends {@link PluginCommand} cause it's a final class. However it implements all the functions from it.
 */
@SuppressWarnings("unused")
public class RegisterablePluginCommand extends Command implements PluginIdentifiableCommand
{
	private final Plugin owningPlugin;
	private CommandExecutor executor;
	private TabCompleter completer;

	public RegisterablePluginCommand(Plugin owner, String name, String... aliases)
	{
		super(name);
		this.executor = owner;
		this.owningPlugin = owner;
		this.usageMessage = "/<command>";
		List<String> aliasesList = new ArrayList<>();
		for(String alias : aliases)
		{
			if(alias.equalsIgnoreCase(name)) continue;
			aliasesList.add(alias);
		}
		this.setAliases(aliasesList);
	}

	/**
	 * Registers the command to Bukkit. Command will get executed.
	 */
	public void registerCommand()
	{
		try
		{
			final Field bukkitCommandMap = owningPlugin.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMap.setAccessible(true);
			((CommandMap) bukkitCommandMap.get(owningPlugin.getServer())).register(this.getName(), this);
		}
		catch(Exception e)
		{
			owningPlugin.getLogger().warning("Failed registering command!");
			e.printStackTrace();
		}
	}

	/**
	 * Un-Register command from Bukkit. Command will no longer get executed.
	 */
	public void unRegisterCommand()
	{
		try
		{
			Object result = Reflection.getField(owningPlugin.getServer().getPluginManager().getClass(), "commandMap");
			SimpleCommandMap commandMap = (SimpleCommandMap) result;
			Object map = Reflection.getField(commandMap.getClass(), "knownCommands");
			HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
			knownCommands.remove(this.getName());
			for (String alias : this.getAliases())
			{
				if(knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName()))
				{
					knownCommands.remove(alias);
				}
			}
		}
		catch (Exception e)
		{
			owningPlugin.getLogger().warning("Failed unregistering command!");
			e.printStackTrace();
		}
	}

	/**
	 * Executes the command, returning its success
	 *
	 * @param sender       Source object which is executing this command
	 * @param commandLabel The alias of the command used
	 * @param args         All arguments passed to the command, split via ' '
	 * @return true if the command was successful, otherwise false
	 */
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args)
	{
		boolean success = false;
		if(owningPlugin.isEnabled())
		{
			try
			{
				success = executor.onCommand(sender, this, commandLabel, args);
			}
			catch(Throwable ex)
			{
				throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + owningPlugin.getDescription().getFullName(), ex);
			}
			if(!success && usageMessage.length() > 0)
			{
				for(String line : usageMessage.replace("<command>", commandLabel).split("\n"))
				{
					sender.sendMessage(line);
				}
			}
		}
		return success;
	}

	/**
	 * Sets the {@link CommandExecutor} to run when parsing this command
	 *
	 * @param executor New executor to run
	 */
	public void setExecutor(CommandExecutor executor)
	{
		this.executor = (executor == null) ? owningPlugin : executor;
	}

	/**
	 * Gets the {@link CommandExecutor} associated with this command
	 *
	 * @return CommandExecutor object linked to this command
	 */
	public CommandExecutor getExecutor()
	{
		return executor;
	}

	/**
	 * Sets the {@link TabCompleter} to run when tab-completing this command.
	 * <p/>
	 * If no TabCompleter is specified, and the command's executor implements
	 * TabCompleter, then the executor will be used for tab completion.
	 *
	 * @param completer New tab completer
	 */
	public void setTabCompleter(TabCompleter completer)
	{
		this.completer = completer;
	}

	/**
	 * Gets the {@link TabCompleter} associated with this command.
	 *
	 * @return TabCompleter object linked to this command
	 */
	public TabCompleter getTabCompleter()
	{
		return completer;
	}

	/**
	 * Gets the owner of this PluginCommand
	 *
	 * @return Plugin that owns this command
	 */
	public Plugin getPlugin()
	{
		return owningPlugin;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Delegates to the tab completer if present.
	 * <p/>
	 * If it is not present or returns null, will delegate to the current
	 * command executor if it implements {@link TabCompleter}. If a non-null
	 * list has not been found, will default to standard player name
	 * completion in {@link
	 * Command#tabComplete(CommandSender, String, String[])}.
	 * <p/>
	 * This method does not consider permissions.
	 *
	 * @throws CommandException         if the completer or executor throw an
	 *                                  exception during the process of tab-completing.
	 * @throws IllegalArgumentException if sender, alias, or args is null
	 */
	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException
	{
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(args, "Arguments cannot be null");
		Validate.notNull(alias, "Alias cannot be null");

		List<String> completions = null;
		try
		{
			if(completer != null)
			{
				completions = completer.onTabComplete(sender, this, alias, args);
			}
			if(completions == null && executor instanceof TabCompleter)
			{
				completions = ((TabCompleter) executor).onTabComplete(sender, this, alias, args);
			}
		}
		catch(Throwable ex)
		{
			StringBuilder message = new StringBuilder();
			message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
			for(String arg : args)
			{
				message.append(arg).append(' ');
			}
			message.deleteCharAt(message.length() - 1).append("' in plugin ").append(owningPlugin.getDescription().getFullName());
			throw new CommandException(message.toString(), ex);
		}

		if(completions == null)
		{
			return super.tabComplete(sender, alias, args);
		}
		return completions;
	}

	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder(super.toString());
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append(", ").append(owningPlugin.getDescription().getFullName()).append(')');
		return stringBuilder.toString();
	}
}