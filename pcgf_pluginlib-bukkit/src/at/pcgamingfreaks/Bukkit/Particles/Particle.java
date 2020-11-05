/*
 *   Copyright (C) 2020 GeorgH93
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

import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "SpellCheckingInspection" })
public enum Particle
{
	EXPLOSION       ("explode",         "EXPLOSION_NORMAL"),
	EXPLOSION_LARGE ("largeexplosion"),
	EXPLOSION_HUGE  ("hugeexplosion"),
	FIREWORKS_SPARK ("fireworksSpark"),
	BUBBLE          ("bubble",          "WATER_BUBBLE"),
	WAKE            ("wake",            "WATER_WAKE"),
	SPLASH          ("splash",          "WATER_SPLASH"),
	SUSPENDED,
	TOWNAURA        ("townaura",        "TOWN_AURA"),
	CRIT,
	MAGIC_CRIT      ("magicCrit",       "CRIT_MAGIC"),
	SMOKE           ("smoke",           "SMOKE_NORMAL"),
	SMOKE_LARGE     ("largesmoke"),
	SPELL,
	SPELL_MOB       ("mobSpell"),
	SPELL_INSTANT   ("instantSpell"),
	SPELL_MOB_AMBIENT("mobSpellAmbient"),
	SPELL_WITCH     ("witchMagic"),
	DRIP_WATER      ("dripWater"),
	DRIP_LAVA       ("dripLava"),
	VILLAGER_ANGRY  ("angryVillager"),
	VILLAGER_HAPPY  ("happyVillager"),
	DEPTHSUSPEND    ("depthsuspend",    "SUSPENDED_DEPTH"),
	NOTE,
	PORTAL,
	ENCHANTMENTTABLE("enchantmenttable", "ENCHANTMENT_TABLE"),
	FLAME,
	LAVA,
	FOOTSTEP,
	CLOUD,
	REDSTONEDUST    ("reddust",         "REDSTONE"),
	SNOWBALL        ("snowballpoof"),
	SNOWSHOVEL      ("snowshovel",      "SNOW_SHOVEL"),
	SLIME,
	HEART,
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	BARRIER         (MCVersion.MC_1_8),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	WATER_DROP      (MCVersion.MC_1_8),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	ITEM_TAKE       (MCVersion.MC_1_8),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	MOB_APPEARANCE  (MCVersion.MC_1_8),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	ITEM_CRACK      (MCVersion.MC_1_8, ItemStack.class),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	BLOCK_CRACK     (MCVersion.MC_1_8, MaterialData.class),
	/**
	 * Only for Minecraft 1.8 and newer!
	 */
	BLOCK_DUST      (MCVersion.MC_1_8, MaterialData.class),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	SWEEP_ATTACK    (MCVersion.MC_1_9),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	DRAGON_BREATH   (MCVersion.MC_1_9),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	END_ROD         (MCVersion.MC_1_9),
	/**
	 * Only for Minecraft 1.9 and newer!
	 */
	DAMAGE_INDICATOR(MCVersion.MC_1_9),
	/**
	 * Only for Minecraft 1.10 and newer!
	 */
	FALLING_DUST    (MCVersion.MC_1_10, MaterialData.class),
	/**
	 * Only for Minecraft 1.11 and newer!
	 */
	TOTEM           (MCVersion.MC_1_11),
	/**
	 * Only for Minecraft 1.11 and newer!
	 */
	SPIT            (MCVersion.MC_1_11);

	private static final Map<String, Particle> BY_NAME = new HashMap<>();

	static
	{
		for(Particle particle : values())
		{
			BY_NAME.put(particle.getName(), particle);
		}
	}

	//private final int parameterCount;
	@Getter private final String oldName, name;
	private final Class<?> dataType;
	@Getter private final MCVersion minVersion;

	//region constructors
	Particle(final @NotNull MCVersion minVersion)
	{
		this.name = name();
		this.oldName = name.toLowerCase(Locale.ENGLISH); // Unimportant for MC 1.8 and up
		this.minVersion = minVersion;
		this.dataType = Void.class;
	}

	Particle(final @NotNull MCVersion minVersion, final @NotNull String bukkitName)
	{
		this.name = bukkitName;
		this.oldName = name.toLowerCase(Locale.ENGLISH); // Unimportant for MC 1.8 and up
		this.minVersion = minVersion;
		this.dataType = Void.class;
	}

	Particle(final @NotNull MCVersion minVersion, final @NotNull Class<?> dataType)
	{
		this.name = name();
		this.oldName = name.toLowerCase(Locale.ENGLISH); // Unimportant for MC 1.8 and up
		this.minVersion = minVersion;
		this.dataType = dataType;
	}

	//region MC 1.7 constructors
	Particle()
	{
		this.oldName = name().toLowerCase(Locale.ENGLISH);
		this.name = name();
		this.minVersion = MCVersion.MC_1_7;
		this.dataType = Void.class;
	}

	Particle(final @NotNull String oldName)
	{
		this.oldName = oldName;
		this.name = name();
		this.minVersion = MCVersion.MC_1_7;
		this.dataType = Void.class;
	}

	Particle(final @NotNull String oldName, final @NotNull String bukkitName)
	{
		this.oldName = oldName;
		this.name = bukkitName;
		this.minVersion = MCVersion.MC_1_7;
		this.dataType = Void.class;
	}
	//endregion
	//endregion

	public @NotNull String getOldNameUpperCase()
	{
		return getOldName().toUpperCase(Locale.ROOT);
	}

	/**
	 * Returns the required data type for the particle.
	 * @return The required data type.
	 */
	public Class<?> getDataType()
	{
		return dataType;
	}

	public static @Nullable Particle getFrom(Enum<?> bukkitParticle)
	{
		return BY_NAME.get(bukkitParticle.name());
	}
}