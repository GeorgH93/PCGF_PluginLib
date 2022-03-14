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

package at.pcgamingfreaks.Config;

import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.Utils;
import at.pcgamingfreaks.Version;
import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlGetter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.io.File;
import java.util.LinkedList;
import java.util.Locale;
import java.util.logging.Logger;

public class Language extends YamlFileManager
{
	protected static final String KEY_LANGUAGE = "Language.", KEY_ADDITION_SEND_METHOD = "_SendMethod", KEY_ADDITION_PARAMETERS = "_Parameters", KEY_ADDITION_PAPI = "_PAPI";
	private static final String MESSAGE_NOT_FOUND = MessageColor.RED + "Message not found!";

	private final Object plugin;
	private final String prefix;
	protected String language = "en", fallbackLanguage = "en";
	private boolean extractedFallback = false;
	@Getter private YamlFileUpdateMethod yamlUpdateMode;

	//region constructors
	//region alternative constructors
	/**
	 * @param plugin  the plugin instance
	 * @param version the current version of the language file
	 */
	public Language(@NotNull IPlugin plugin, Version version)
	{
		this(plugin, version, File.separator + "lang", "");
	}

	/**
	 * @param plugin  The plugin instance
	 * @param version The current version of the language file
	 * @param path    The sub-folder for the language file
	 * @param prefix  The prefix for the language file
	 */
	public Language(@NotNull IPlugin plugin, Version version, @Nullable String path, @NotNull String prefix)
	{
		this(plugin, version, path, prefix, prefix);
	}

	/**
	 * @param plugin      The plugin instance
	 * @param version     The current version of the language file
	 * @param path        The sub-folder for the language file
	 * @param prefix      The prefix for the language file
	 * @param inJarPrefix The prefix for the language file within the jar (e.g.: bungee_)
	 */
	public Language(@NotNull IPlugin plugin, Version version, @Nullable String path, @NotNull String prefix, @NotNull String inJarPrefix)
	{
		this(plugin, plugin.getLogger(), plugin.getDataFolder(), version, path, prefix, inJarPrefix);
	}
	//endregion

	public Language(final @NotNull Object plugin, @NotNull Logger logger, @NotNull File baseDir, Version version, @Nullable String path, @NotNull String prefix, @NotNull String inJarPrefix)
	{
		super(logger, baseDir, version, path, prefix, "/lang/" + inJarPrefix, null);
		this.prefix = prefix;
		setFileDescription("language");
		this.plugin = plugin;
	}
	//endregion

	/**
	 * @param path the path to the searched language value
	 * @return returns the language data
	 */
	public @NotNull String get(@NotNull String path)
	{
		String msg = yaml.getString(KEY_LANGUAGE + path, null);
		if(msg == null)
		{
			logger.warning("No translation for key: " + path);
			return MESSAGE_NOT_FOUND;
		}
		return msg;
	}

	/**
	 * Gets the string value of a given key. It is the same as calling "getLang().getString(key, defaultValue)".
	 * No prefixes are applied.
	 *
	 * @param key The key to get the value from.
	 * @param defaultValue The default value that should be returned if the file doesn't contain the requested key.
	 * @return The value stored for the given key, the default value if the key is not found in the language file.
	 */
	@Contract("_, !null -> !null")
	public @Nullable String getRaw(@NotNull String key, @Nullable String defaultValue)
	{
		return yaml.getString(key, defaultValue);
	}

	protected void set(@NotNull String path, @NotNull String value)
	{
		yaml.set(path, value);
	}

	/**
	 * Reloads the language file
	 */
	public void reload()
	{
		load(language, yamlUpdateMode);
	}

	//region load methods
	/**
	 * Loads the language file
	 *
	 * @param config the config with the settings that should be used to load the language file
	 * @return True if it's loaded successfully. False if not.
	 */
	public boolean load(@NotNull ILanguageConfiguration config)
	{
		return load(config.getLanguage(), config.getLanguageUpdateMode());
	}

	/**
	 * Loads the language file
	 *
	 * @param language   the language to load
	 * @param updateMode how the language file should be updated
	 * @return True if it's loaded successfully. False if not.
	 */
	public boolean load(@NotNull String language, @NotNull YamlFileUpdateMethod updateMode)
	{
		return load(language, updateMode, "en");
	}

	/**
	 * Loads the language file
	 *
	 * @param language   the language to load
	 * @param updateMode how the language file should be updated
	 * @param fallbackLanguage   the language to load in case the main language does not exist
	 * @return True if it's loaded successfully. False if not.
	 */
	public boolean load(@NotNull String language, @NotNull YamlFileUpdateMethod updateMode, @NotNull String fallbackLanguage)
	{
		this.language = language.toLowerCase(Locale.ROOT);
		this.yamlUpdateMode = updateMode;
		this.fallbackLanguage = fallbackLanguage;
		this.extractedFallback = false;
		extracted = false;
		file = language + YAML_FILE_EXT;
		yamlFile = new File(baseDir, prefix + file);
		load();
		return isLoaded();
	}
	//endregion

	protected void extractFile()
	{
		if(extracted || !Utils.extractFile(jarClass(), logger, inJarPrefix + file, yamlFile))
		{
			if(!language.equals(fallbackLanguage))
			{
				Utils.extractFile(jarClass(), logger, inJarPrefix + fallbackLanguage + YAML_FILE_EXT, yamlFile);
				extractedFallback = true;
			}
			else
			{
				logger.warning(ConsoleColor.RED + "Fallback language file failed to extract!" + ConsoleColor.RESET);
			}
		}
		extracted = true;
	}

	protected @NotNull YamlFileUpdateMethod decideYamlUpdateMode()
	{
		if(extractedFallback) return YamlFileUpdateMethod.UPDATE;
		if(getYamlUpdateMode() != null) return getYamlUpdateMode();
		return YamlFileUpdateMethod.UPGRADE;
	}

	/**
	 * Allows inheriting classes to implement own code for the yaml file upgrade
	 * If no special code is implemented all keys (which exists in both files) will be copied 1:1 into the new yaml file
	 * also if the old file defines a _SendMethod or _Parameters sub keys they will be copied too.
	 *
	 * @param oldYamlFile the old yaml file
	 */
	@Override
	protected void doUpgrade(@NotNull YamlFileManager oldYamlFile)
	{
		logger.info("No custom " + getFileDescription() + " upgrade code. Copying data from old file to new one.");
		for(String key : yaml.getKeys())
		{
			if(oldYamlFile.yaml.isSet(key))
			{
				if(key.equals(KEY_YAML_VERSION)) continue;
				if(oldYamlFile.yaml.isList(key))
				{
					yaml.set(key, oldYamlFile.yaml.getStringList(key, new LinkedList<>()));
				}
				else
				{
					yaml.set(key, oldYamlFile.yaml.getString(key, null));
					if(oldYamlFile.yaml.isSet(key + KEY_ADDITION_SEND_METHOD)) yaml.set(key + KEY_ADDITION_SEND_METHOD, oldYamlFile.yaml.getString(key + KEY_ADDITION_SEND_METHOD, null));
					if(oldYamlFile.yaml.isSet(key + KEY_ADDITION_PARAMETERS)) yaml.set(key + KEY_ADDITION_PARAMETERS, oldYamlFile.yaml.getString(key + KEY_ADDITION_PARAMETERS, null));
				}
			}
		}
	}

	/**
	 * Gets the {@link YAML} language configuration instance for direct read/write.
	 *
	 * @return The language configuration instance. null if the language file is not loaded.
	 */
	public @Nullable YAML getLang()
	{
		return getYaml();
	}

	/**
	 * Gets the {@link YAML} language configuration instance for direct read/write.
	 *
	 * @return The language configuration instance.
	 * @throws LanguageNotInitializedException If the language configuration has not been loaded successful.
	 */
	public @NotNull YAML getLangE() throws LanguageNotInitializedException
	{
		if(yaml == null) throw new LanguageNotInitializedException();
		return yaml;
	}

	/**
	 * Gets the {@link YAML} language configuration as a {@link YamlGetter} instance for direct read (read-only).
	 *
	 * @return The language configuration instance.
	 * @throws LanguageNotInitializedException If the language configuration has not been loaded successful.
	 */
	public @NotNull YamlGetter getLangReadOnly() throws LanguageNotInitializedException
	{
		return getLangE();
	}

	/**
	 * Gets the message from the language file and replaces bukkit color codes (&amp;) to minecraft color codes (§)
	 *
	 * @param path the path to the searched language value
	 * @return returns the language data
	 */
	public @NotNull String getTranslated(final @NotNull String path)
	{
		return MessageColor.translateAlternateColorAndFormatCodes(get(path));
	}

	//region language file property getters
	public @NotNull String getLanguage()
	{
		//noinspection ConstantConditions
		return getLang().getString("LanguageName", language);
	}

	public @NotNull String getAuthor()
	{
		//noinspection ConstantConditions
		return getLang().getString("Author", "Unknown");
	}
	//endregion

	@Override
	protected void loaded()
	{
		String author = getAuthor().equals("Unknown") ? "" : "  Author: " + getAuthor();
		logger.info(ConsoleColor.GREEN + getFileDescriptionCapitalized() + " file successfully loaded. Language: " + getLanguage() + author + ConsoleColor.RESET);
	}

	public static class LanguageNotInitializedException extends RuntimeException
	{
		private LanguageNotInitializedException()
		{
			super("The language file has not been loaded successful");
		}
	}

	@Override
	protected Class<?> jarClass()
	{
		return plugin.getClass();
	}
}
