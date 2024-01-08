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

package at.pcgamingfreaks.Bukkit.Config;

import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PermissionLoader
{
	private PermissionLoader() { /* Hide implicit constructor */ }

	public static void loadPermissionsFromPlugin(JavaPlugin plugin)
	{
		try
		{
			InputStream resource = plugin.getClass().getClassLoader().getResourceAsStream("plugin.yml");
			if (resource == null)
			{
				plugin.getLogger().warning("Failed to get plugin.yml as stream.");
				return;
			}
			YAML pluginYaml = new YAML(resource);
			loadPermissions(pluginYaml.getSection("permissions"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void loadPermissions(YAML permsYaml)
	{
		try
		{
			for(String perm : permsYaml.getNodeKeys())
			{
				if (!perm.contains(".description")) continue;
				perm = perm.substring(0, perm.length() - ".description".length());
				String description = permsYaml.getString(perm + ".description", "");
				Map<String, Boolean> children = null;
				try
				{
					YAML childPerms = permsYaml.getSection(perm + ".children");
					children = new HashMap<>();
					for(String child : childPerms.getKeys())
					{
						children.put(child, childPerms.getBoolean(child, true));
					}
				}
				catch(YamlKeyNotFoundException ignored){}
				PermissionDefault permDefault = PermissionDefault.getByName(permsYaml.getString(perm + ".default", "op"));
				if (permDefault == null) permDefault = PermissionDefault.OP;
				try
				{
					Bukkit.getServer().getPluginManager().addPermission(new Permission(
							perm, description, permDefault, children
					));
				}
				catch(IllegalArgumentException ignored) {}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}