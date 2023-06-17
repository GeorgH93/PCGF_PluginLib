/*
 *   Copyright (C) 2023 GeorgH93
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

package at.pcgamingfreaks.PluginLib;

import java.io.InputStream;
import java.util.Properties;

public class MagicValues
{
	public static final String PCGF_PLUGIN_LIB_VERSION;

	static
	{
		String pcgfPluginLibVersion = "0";

		try(InputStream propertiesStream = MagicValues.class.getClassLoader().getResourceAsStream("PCGF_PluginLib.properties"))
		{
			Properties properties = new Properties();
			properties.load(propertiesStream);

			pcgfPluginLibVersion = properties.getProperty("PCGFPluginLibVersion");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		PCGF_PLUGIN_LIB_VERSION = pcgfPluginLibVersion;
	}

	private MagicValues() { /* You should not create an instance of this utility class! */ }
}