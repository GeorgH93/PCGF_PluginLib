/*
 *   Copyright (C) 2016, 2017 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Particles;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NMSReflection;

import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "unused", "SpellCheckingInspection" })
public enum Particle
{
	EXPLOSION       ("explode",         "EXPLOSION_NORMAL"),
	EXPLOSION_LARGE ("largeexplosion",  "EXPLOSION_LARGE"),
	EXPLOSION_HUGE  ("hugeexplosion",   "EXPLOSION_HUGE"),
	FIREWORKS_SPARK ("fireworksSpark",  "FIREWORKS_SPARK"),
	BUBBLE          ("bubble",          "WATER_BUBBLE"),
	WAKE            ("wake",            "WATER_WAKE"),
	SPLASH          ("splash",          "WATER_SPLASH"),
	SUSPENDED       ("suspended",       "SUSPENDED"),
	TOWNAURA        ("townaura",        "TOWN_AURA"),
	CRIT            ("crit"),
	MAGIC_CRIT      ("magicCrit",       "CRIT_MAGIC"),
	SMOKE           ("smoke",           "SMOKE_NORMAL"),
	SMOKE_LARGE     ("largesmoke",      "SMOKE_LARGE"),
	SPELL           ("spell"),
	SPELL_MOB       ("mobSpell",        "SPELL_MOB"),
	SPELL_INSTANT   ("instantSpell",    "SPELL_INSTANT"),
	SPELL_MOB_AMBIENT("mobSpellAmbient", "SPELL_MOB_AMBIENT"),
	WITCH_MAGIC     ("witchMagic",      "SPELL_WITCH"),
	DRIP_WATER      ("dripWater",       "DRIP_WATER"),
	DRIP_LAVA       ("dripLava",        "DRIP_LAVA"),
	VILLAGER_ANGRY  ("angryVillager",   "VILLAGER_ANGRY"),
	VILLAGER_HAPPY  ("happyVillager",   "VILLAGER_HAPPY"),
	DEPTHSUSPEND    ("depthsuspend",    "SUSPENDED_DEPTH"),
	NOTE            ("note"),
	PORTAL          ("portal"),
	ENCHANTMENTTABLE("enchantmenttable", "ENCHANTMENT_TABLE"),
	FLAME           ("flame"),
	LAVA            ("lava"),
	FOOTSTEP        ("footstep"),
	CLOUD           ("cloud"),
	REDSTONEDUST    ("reddust",         "REDSTONE"),
	SNOWBALLPOOF    ("snowballpoof",    "SNOWBALL"),
	SNOWSHOVEL      ("snowshovel",      "SNOW_SHOVEL"),
	SLIME           ("slime"),
	HEART           ("heart"),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	BARRIER         ("barrier",         MCVersion.MC_1_8),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	WATER_DROP      ("droplet",         "WATER_DROP",   MCVersion.MC_1_8),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	ITEM_TAKE       ("take",            "ITEM_TAKE",    MCVersion.MC_1_8),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	MOB_APPEARANCE  ("mobappearance",   "MOB_APPEARANCE", MCVersion.MC_1_8),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	ITEM_CRACK      ("iconcrack",       "ITEM_CRACK",   MCVersion.MC_1_8, ItemStack.class),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	BLOCK_CRACK     ("blockcrack",      "BLOCK_CRACK",  MCVersion.MC_1_8, MaterialData.class),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	BLOCK_DUST      ("blockdust",       "BLOCK_DUST",   MCVersion.MC_1_8, MaterialData.class),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	SWEEP_ATTACK    ("sweepAttack",     "SWEEP_ATTACK", MCVersion.MC_1_9),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	DRAGON_BREATH   ("dragonBreath",    "DRAGON_BREATH", MCVersion.MC_1_9),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	END_ROD         ("endRod",          "END_ROD",      MCVersion.MC_1_9),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	DAMAGE_INDICATOR("damageIndicator", "DAMAGE_INDICATOR", MCVersion.MC_1_9),
	/**
	 * Only for Minecraft 1.10 and newer!
	 */
	FALLING_DUST    ("fallingdust",     "FALLING_DUST",     MCVersion.MC_1_10, MaterialData.class),
	/**
	 * Only for Minecraft 1.11 and newer!
	 */
	TOTEM           ("totem",           MCVersion.MC_1_11),
	/**
	 * Only for Minecraft 1.11 and newer!
	 */
	SPIT            ("spit",            MCVersion.MC_1_11);

	private static final Map<String, Particle> BY_NAME = new HashMap<>();

	static
	{
		for(Particle particle : values())
		{
			BY_NAME.put(particle.getName(), particle);
		}
	}

	//private final int parameterCount;
	private final String oldName, name;
	private final Enum<?> nmsEnumParticle;
	private final Class<?> dataType;
	private final MCVersion minVersion;

	Particle(String oldName)
	{
		this(oldName, oldName.toUpperCase());
	}

	Particle(String oldName, String name)
	{
		this(oldName, name, MCVersion.MC_1_7);
	}

	Particle(String oldName, MCVersion minVersion)
	{
		this(oldName, oldName.toUpperCase(), minVersion);
	}

	Particle(String oldName, String name, MCVersion minVersion)
	{
		this(oldName, name, minVersion, Void.class);
	}

	Particle(String oldName, String name, MCVersion minVersion, Class<?> dataType)
	{
		this.name = name;
		this.oldName = oldName;
		this.minVersion = minVersion;
		Enum<?> nmsEnum = null;
		if(MCVersion.isNewerOrEqualThan(minVersion))
		{
			nmsEnum = NMSReflection.getNMSEnum("EnumParticle." + name);
		}
		this.nmsEnumParticle = nmsEnum;
		this.dataType = dataType;
	}

	public @NotNull String getOldName()
	{
		return oldName;
	}

	public @NotNull String getName()
	{
		return name;
	}

	public @Nullable Enum<?> getEnum()
	{
		return nmsEnumParticle;
	}

	public @NotNull String getOldNameUpperCase()
	{
		return getOldName().toUpperCase();
	}

	public @NotNull MCVersion getMinVersion()
	{
		return minVersion;
	}

	/**
	 * Returns the required data type for the particle.
	 * @return The required data type.
	 */
	public Class<?> getDataType()
	{
		return dataType;
	}

	public static @Nullable Particle getFrom(org.bukkit.Particle bukkitParticle)
	{
		return BY_NAME.get(bukkitParticle.name());
	}
}