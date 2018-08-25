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

import at.pcgamingfreaks.Bukkit.Configuration;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class TestBukkitConfiguration extends Configuration
{
	public TestBukkitConfiguration(@NotNull JavaPlugin plugin, int version)
	{
		super(plugin, version);
	}

	public boolean getIsBukkitVersionUUIDCompatible()
	{
		return isBukkitVersionUUIDCompatible();
	}
}