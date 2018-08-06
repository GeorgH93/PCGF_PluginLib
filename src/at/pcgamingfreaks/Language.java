/*
 *   Copyright (C) 2014-2018 GeorgH93
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class Language extends YamlFileManager
{
	private static final String PATH_ADDITION_SEND_METHOD = "_SendMethod", PATH_ADDITION_PARAMETERS = "_Parameters";
	protected String language = "en";
	protected static MessageClassesReflectionDataHolder messageClasses;

	private YamlFileUpdateMethod updateMode = YamlFileUpdateMethod.OVERWRITE;
	private final String prefix;

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
		return yaml.getString("Language." + path, "§cMessage not found!");
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
		return load(language, (updateMode.equalsIgnoreCase("overwrite")) ? YamlFileUpdateMethod.OVERWRITE : YamlFileUpdateMethod.UPDATE);
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
		this.language = language;
		this.updateMode = updateMode;
		file = language + ".yml";
		yamlFile = new File(baseDir, prefix + file);
		load();
		return isLoaded();
	}

	protected void extractFile()
	{
		if(!Utils.extractFile(getClass(), logger, inJarPrefix + file, yamlFile) && !language.equals("en"))
		{
			Utils.extractFile(getClass(), logger, inJarPrefix + "en.yml", yamlFile);
		}
		extracted = true;
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
			logger.warning("Message reflection data object not set!");
			return null;
		}
		T msg = null;
		try
		{
			//noinspection unchecked
			msg = (T) messageClasses.messageConstructor.newInstance((escapeStringFormatCharacters) ? getTranslated(path).replaceAll("%", "%%") : getTranslated(path));
			String pathSendMethod = "Language." + path + PATH_ADDITION_SEND_METHOD, pathParameter = "Language." + path + PATH_ADDITION_PARAMETERS;
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
			logger.warning("Failed generate metadata for: " + path);
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