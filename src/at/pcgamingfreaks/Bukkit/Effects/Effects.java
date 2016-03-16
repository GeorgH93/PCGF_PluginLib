/*
 *   Copyright (C) 2014-2016 GeorgH93
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
	EXPLODE(0, "explode", "EXPLOSION_NORMAL"),
	LARGE_EXPLOSION(1, "largeexplosion", "EXPLOSION_LARGE"),
	HUGE_EXPLOSION(2, "hugeexplosion", "EXPLOSION_HUGE"),
	FIREWORKS_SPARK(3, "fireworksSpark", "FIREWORKS_SPARK"),
	BUBBLE(4, "bubble", "WATER_BUBBLE"),
	WAKE(5, "wake", "WATER_WAKE"),
	SPLASH(6, "splash", "WATER_SPLASH"),
	SUSPENDED(7, "suspended", "SUSPENDED"),
	TOWNAURA(8, "townaura", "TOWN_AURA"),
	CRIT(9, "crit"),
	MAGIC_CRIT(10, "magicCrit", "CRIT_MAGIC"),
	SMOKE(11, "smoke", "SMOKE_NORMAL"),
	LARGE_SMOKE(12, "largesmoke", "SMOKE_LARGE"),
	MOB_SPELL(13, "mobSpell", "SPELL_MOB"),
	INSTANT_SPELL(14, "instantSpell", "SPELL_INSTANT"),
	SPELL(15, "spell"),
	WITCH_MAGIC(17, "witchMagic", "SPELL_WITCH"),
	DRIP_WATER(18, "dripWater", "DRIP_WATER"),
	DRIP_LAVA(19, "dripLava", "DRIP_LAVA"),
	ANGRY_VILLAGER(20, "angryVillager", "VILLAGER_ANGRY"),
	HAPPY_VILLAGER(21, "happyVillager", "VILLAGER_HAPPY"),
	DEPTHSUSPEND(22, "depthsuspend", "SUSPENDED_DEPTH"),
	NOTE(23, "note"),
	PORTAL(24, "portal"),
	ENCHANTMENTTABLE(25, "enchantmenttable", "ENCHANTMENT_TABLE"),
	FLAME(26, "flame"),
	LAVA(27, "lava"),
	FOOTSTEP(28, "footstep"),
	CLOUD(29, "cloud"),
	REDDUST(30, "reddust", "REDSTONE"),
	SNOWBALLPOOF(31, "snowballpoof", "SNOWBALL"),
	SNOWSHOVEL(32, "snowshovel", "SNOW_SHOVEL"),
	SLIME(33, "slime"),
	HEART(34, "heart"),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	BARRIER(35, "barrier"),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	SWEEP_ATTACK(36, "sweepAttack", "SWEEP_ATTACK"),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	DRAGON_BREATH(37, "dragonBreath", "DRAGON_BREATH"),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	END_ROD(38, "endRod", "END_ROD"),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	DAMAGE_INDICATOR(39, "damageIndicator", "DAMAGE_INDICATOR");

	private final int id;
	private final String name, newName;
	private final Enum<?> nmsEnumParticle;

	Effects(int id, String name)
	{
		this(id, name, name.toUpperCase());
	}

	Effects(int id, String name, String newName)
	{
		this.id = id;
		this.name = name;
		this.newName = newName;
		if(Reflection.getVersion().contains("1_8") || Reflection.getVersion().contains("1_9"))
		{
			nmsEnumParticle = (this.id < 36 || (this.id >= 36 && Reflection.getVersion().contains("1_9"))) ? Reflection.getNMSEnum("EnumParticle." + this.newName) : null;
		}
		else
		{
			nmsEnumParticle = null;
		}
	}

	public String getName()
	{
		return name;
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