/*
 *   Copyright (C) 2014-2015 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Effects;

import at.pcgamingfreaks.Bukkit.Reflection;

/**
 * Only available for Minecraft 1.8 and newer!
 */
@SuppressWarnings("unused")
public enum MaterialEffects
{
	ITEM_CRACK("iconcrack", "ITEM_CRACK"),
	BLOCK_CRACK("blockcrack", "BLOCK_CRACK"),
	BLOCK_DUST("blockdust", "BLOCK_DUST");

	private final String name, nameUpperCase, newName;
	private final Enum<?> nmsEnumParticle;

	MaterialEffects(String NAME)
	{
		this(NAME, NAME.toUpperCase());
	}

	MaterialEffects(String NAME, String NEWNAME)
	{
		name = NAME;
		nameUpperCase = name.toUpperCase();
		newName = NEWNAME;
		nmsEnumParticle = (Reflection.getVersion().contains("1_8")) ? Reflection.getEnum("net.minecraft.server." + Reflection.getVersion() + ".EnumParticle." + newName) : null;
	}

	public String getName()
	{
		return name;
	}

	public String getNameUpperCase()
	{
		return nameUpperCase;
	}

	public String getNewName()
	{
		return newName;
	}

	public Enum<?> getEnum()
	{
		return nmsEnumParticle;
	}
}