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

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NMSReflection;

/**
 * Only available for Minecraft 1.8 and newer!
 */
@SuppressWarnings("unused")
public enum MaterialEffects implements IEffects
{
	@SuppressWarnings("SpellCheckingInspection")
	ITEM_CRACK("iconcrack", "ITEM_CRACK", MCVersion.MC_1_8),
	@SuppressWarnings("SpellCheckingInspection")
	BLOCK_CRACK("blockcrack", "BLOCK_CRACK", MCVersion.MC_1_8),
	@SuppressWarnings("SpellCheckingInspection")
	BLOCK_DUST("blockdust", "BLOCK_DUST", MCVersion.MC_1_8),
	/**
	 * Only for Minecraft 1.10 and newer!
	 */
	@SuppressWarnings("SpellCheckingInspection")
	FALLING_DUST("fallingdust", "FALLING_DUST", MCVersion.MC_1_10);

	private final String name, nameUpperCase, newName;
	private final Enum<?> nmsEnumParticle;
	private final MCVersion minVersion;

	MaterialEffects(String name, String newName, MCVersion minVersion)
	{
		this.name = name;
		nameUpperCase = name.toUpperCase();
		this.newName = newName;
		this.minVersion = minVersion;
		nmsEnumParticle = (MCVersion.isNewerOrEqualThan(minVersion)) ? NMSReflection.getNMSEnum("EnumParticle." + newName) : null;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getNameUpperCase()
	{
		return nameUpperCase;
	}

	@Override
	public String getNewName()
	{
		return newName;
	}

	@Override
	public Enum<?> getEnum()
	{
		return nmsEnumParticle;
	}

	@Override
	public MCVersion getMinVersion()
	{
		return minVersion;
	}
}