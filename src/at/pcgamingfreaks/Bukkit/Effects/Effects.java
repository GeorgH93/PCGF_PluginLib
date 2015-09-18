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

@SuppressWarnings("unused")
public enum Effects
{
	Explode(0, "explode", "EXPLOSION_NORMAL"),
	LargeExplosion(1, "largeexplosion", "EXPLOSION_LARGE"),
	HugeExplosion(2, "hugeexplosion", "EXPLOSION_HUGE"),
	FireworksSpark(3, "fireworksSpark", "FIREWORKS_SPARK"),
	Bubble(4, "bubble", "WATER_BUBBLE"),
	Wake(5, "wake", "WATER_WAKE"),
	Splash(6, "splash", "WATER_SPLASH"),
	Suspended(7, "suspended", "SUSPENDED"),
	Townaura(8, "townaura", "TOWN_AURA"),
	Crit(9, "crit"),
	MagicCrit(10, "magicCrit", "CRIT_MAGIC"),
	Smoke(11, "smoke", "SMOKE_NORMAL"),
	LargeSmoke(12, "largesmoke", "SMOKE_LARGE"),
	MobSpell(13, "mobSpell", "SPELL_MOB"),
	InstantSpell(14, "instantSpell", "SPELL_INSTANT"),
	Spell(15, "spell"),
	WitchMagic(17, "witchMagic", "SPELL_WITCH"),
	DripWater(18, "dripWater", "DRIP_WATER"),
	DripLava(19, "dripLava", "DRIP_LAVA"),
	AngryVillager(20, "angryVillager", "VILLAGER_ANGRY"),
	HappyVillager(21, "happyVillager", "VILLAGER_HAPPY"),
	Depthsuspend(22, "depthsuspend", "SUSPENDED_DEPTH"),
	Note(23, "note"),
	Portal(24, "portal"),
	Enchantmenttable(25, "enchantmenttable", "ENCHANTMENT_TABLE"),
	Flame(26, "flame"),
	Lava(27, "lava"),
	Footstep(28, "footstep"),
	Cloud(29, "cloud"),
	Reddust(30, "reddust", "REDSTONE"),
	Snowballpoof(31, "snowballpoof", "SNOWBALL"),
	Snowshovel(32, "snowshovel", "SNOW_SHOVEL"),
	Slime(33, "slime"),
	Heart(34, "heart"),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	Barrier(35, "barrier"),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	Droplet(35, "droplet"),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	Take(36, "take"),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	MobAppearance(36, "mobappearance");
	
	private final int id;
	private final String name, nameUpperCase, newName;
	private final Enum<?> nmsEnumParticle;

	Effects(int ID, String NAME)
	{
		this(ID, NAME, NAME.toUpperCase());
	}

	Effects(int ID, String NAME, String NEWNAME)
	{
		id = ID;
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
	
	public int getID()
	{
		return id;
	}
	
	public Enum<?> getEnum()
	{
		return nmsEnumParticle;
	}
}