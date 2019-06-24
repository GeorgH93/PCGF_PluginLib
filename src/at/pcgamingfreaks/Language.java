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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks;

import at.pcgamingfreaks.Message.Message;
import at.pcgamingfreaks.yaml.YAML;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class Language extends YamlFileManager
{
	private static final String MESSAGE_NOT_FOUND = "§cMessage not found!";
	private static final String KEY_LANGUAGE = "Language.", KEY_ADDITION_SEND_METHOD = "_SendMethod", KEY_ADDITION_PARAMETERS = "_Parameters";
	protected static MessageClassesReflectionDataHolder messageClasses;

	private final String prefix;
	protected String language = "en", fallbackLanguage = "en";
	private boolean extractedFallback = false;

	//region constructors
	//region alternative constructors
	/**
	 * @param logger  The logger instance of the plugin
	 * @param baseDir The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version the current version of the language file
	 */
	public Language(@NotNull Logger logger, @NotNull File baseDir, int version)
	{
		this(logger, baseDir, version, File.separator + "lang", "");
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 */
	public Language(@NotNull Logger logger, @NotNull File baseDir, int version, int upgradeThreshold)
	{
		this(logger, baseDir, version, upgradeThreshold, File.separator + "lang", "");
	}

	/**
	 * @param logger  The logger instance of the plugin
	 * @param baseDir The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version The current version of the language file
	 * @param path    The sub-folder for the language file
	 * @param prefix  The prefix for the language file
	 */
	public Language(@NotNull Logger logger, @NotNull File baseDir, int version, @Nullable String path, @NotNull String prefix)
	{
		this(logger, baseDir, version, path, prefix, "");
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 * @param path             The sub-folder for the language file
	 * @param prefix           The prefix for the language file
	 */
	public Language(@NotNull Logger logger, @NotNull File baseDir, int version, int upgradeThreshold, @Nullable String path, @NotNull String prefix)
	{
		this(logger, baseDir, version, upgradeThreshold, path, prefix, "");
	}

	/**
	 * @param logger      The logger instance of the plugin
	 * @param baseDir     The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version     the current version of the language file
	 * @param path        the sub-folder for the language file
	 * @param prefix      the prefix for the language file
	 * @param inJarPrefix the prefix for the language file within the jar (e.g.: bungee_)
	 */
	public Language(@NotNull Logger logger, @NotNull File baseDir, int version, @Nullable String path, @NotNull String prefix, @NotNull String inJarPrefix)
	{
		this(logger, baseDir, version, -1, path, prefix, inJarPrefix);
	}

	/**
	 * @param logger           The logger instance of the plugin
	 * @param baseDir          The base directory where the language file should be saved (normally plugin_instance.getDataFolder())
	 * @param version          The current version of the language file
	 * @param upgradeThreshold Versions below this will be upgraded (settings copied into a new language file) instead of updated
	 * @param path             The sub-folder for the language file
	 * @param prefix           The prefix for the language file
	 * @param inJarPrefix      The prefix for the language file within the jar (e.g.: bungee_)
	 */
	public Language(@NotNull Logger logger, @NotNull File baseDir, int version, int upgradeThreshold, @Nullable String path, @NotNull String prefix, @NotNull String inJarPrefix)
	{
		this(logger, baseDir, version, upgradeThreshold, path, prefix, inJarPrefix, null);
	}
	//endregion

	private Language(@NotNull Logger logger, @NotNull File baseDir, int version, int upgradeThreshold, @Nullable String path, @NotNull String prefix, @NotNull String inJarPrefix, @Nullable YAML yaml)
	{
		super(logger, baseDir, version, upgradeThreshold, path, prefix, "/lang/" + inJarPrefix, yaml);
		this.prefix = prefix;
		setFileDescription("language");
	}
	//endregion

	/**
	 * @param path the path to the searched language value
	 * @return returns the language data
	 */
	public @NotNull String get(@NotNull String path)
	{
		String msg = yaml.getString(KEY_LANGUAGE + path, MESSAGE_NOT_FOUND);
		//noinspection StringEquality
		if(msg == MESSAGE_NOT_FOUND) // == is correct! We want to check if the string is the given fallback string object and not if it is the same text. If someone would put the fallback text in the language file no info should be shown.
		{
			logger.info("No translation for key: " + path);
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
		load(language, updateMode);
	}

	//region load methods
	/**
	 * Loads the language file
	 *
	 * @param config the config with the settings that should be used to load the language file
	 * @return True if it's loaded successfully. False if not.
	 */
	public boolean load(@NotNull Configuration config)
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
	public boolean load(@NotNull String language, @NotNull String updateMode)
	{
		return load(language, YamlFileUpdateMethod.fromString(updateMode));
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
		this.language = language.toLowerCase();
		this.updateMode = updateMode;
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
		if(extracted || !Utils.extractFile(getClass(), logger, inJarPrefix + file, yamlFile))
		{
			if(!language.equals(fallbackLanguage))
			{
				Utils.extractFile(getClass(), logger, inJarPrefix + fallbackLanguage + YAML_FILE_EXT, yamlFile);
				extractedFallback = true;
			}
			else
			{
				logger.warning(ConsoleColor.RED + "Fallback language file failed to extract!" + ConsoleColor.RESET);
			}
		}
		extracted = true;
	}

	protected void decideUpdateMode()
	{
		if((getVersion() < upgradeThreshold || updateMode == YamlFileUpdateMethod.UPGRADE) && !extractedFallback)
		{
			upgrade();
		}
		else
		{
			update();
		}
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
		logger.info("No custom " + fileDescription + " upgrade code. Copying data from old file to new one.");
		for(String key : yaml.getKeys())
		{
			if(oldYamlFile.yaml.isSet(key))
			{
				if(key.equals(KEY_YAML_VERSION)) continue;
				yaml.set(key, oldYamlFile.yaml.getString(key, null));
				if(oldYamlFile.yaml.isSet(key + KEY_ADDITION_SEND_METHOD)) yaml.set(key + KEY_ADDITION_SEND_METHOD, oldYamlFile.yaml.getString(key + KEY_ADDITION_SEND_METHOD, null));
				if(oldYamlFile.yaml.isSet(key + KEY_ADDITION_PARAMETERS)) yaml.set(key + KEY_ADDITION_PARAMETERS, oldYamlFile.yaml.getString(key + KEY_ADDITION_PARAMETERS, null));
			}
		}
	}

	public @Nullable YAML getLang()
	{
		return yaml;
	}

	public @NotNull String getTranslated(@NotNull String path)
	{
		return get(path);
	}

	protected @Nullable <T extends Message> T getMessage(boolean escapeStringFormatCharacters, @NotNull String path)
	{
		if(messageClasses == null)
		{
			logger.warning(ConsoleColor.RED + "Message reflection data object not set!" + ConsoleColor.RESET);
			return null;
		}
		T msg = null;
		try
		{
			//noinspection unchecked
			msg = (T) messageClasses.messageConstructor.newInstance((escapeStringFormatCharacters) ? getTranslated(path).replaceAll("%", "%%") : getTranslated(path));
			String pathSendMethod = KEY_LANGUAGE + path + KEY_ADDITION_SEND_METHOD, pathParameter = KEY_LANGUAGE + path + KEY_ADDITION_PARAMETERS;
			if(yaml.isSet(pathSendMethod))
			{
				Object sendMethod = Enum.valueOf(messageClasses.enumType, yaml.getString(pathSendMethod, "CHAT").toUpperCase());
				messageClasses.setSendMethod.invoke(msg, sendMethod);
				if(yaml.isSet(pathParameter))
				{
					Object metaFromJsonMethod = messageClasses.getMetadataFromJsonMethod.invoke(sendMethod);
					if(metaFromJsonMethod != null)
					{
						msg.setOptionalParameters(((Method) metaFromJsonMethod).invoke(null, yaml.getString(pathParameter)));
					}
				}
			}
		}
		catch(Exception e)
		{
			if(msg == null) logger.warning(ConsoleColor.RED + "Failed to load message: " + KEY_LANGUAGE + path + " " + ConsoleColor.RESET);
			else logger.warning(ConsoleColor.RED + "Failed generate metadata for: " + KEY_LANGUAGE + path + " " + ConsoleColor.RESET);
			e.printStackTrace();
		}
		return msg;
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
		logger.info(ConsoleColor.GREEN + fileDescriptionCapitalized + " file successfully loaded. Language: " + getLanguage() + author + ConsoleColor.RESET);
	}


	//region helper class
	/**
	 * Ignore this class, it's just a helper class for some internal stuff
	 */
	protected static class MessageClassesReflectionDataHolder
	{
		public MessageClassesReflectionDataHolder(Constructor messageConstructor, Method setSendMethod, Method getMetadataFromJsonMethod, Class enumType)
		{
			this.enumType = enumType;
			this.setSendMethod = setSendMethod;
			this.messageConstructor = messageConstructor;
			this.getMetadataFromJsonMethod = getMetadataFromJsonMethod;
		}

		public Class enumType;
		public Constructor messageConstructor;
		public Method setSendMethod, getMetadataFromJsonMethod;
	}
	//endregion
}