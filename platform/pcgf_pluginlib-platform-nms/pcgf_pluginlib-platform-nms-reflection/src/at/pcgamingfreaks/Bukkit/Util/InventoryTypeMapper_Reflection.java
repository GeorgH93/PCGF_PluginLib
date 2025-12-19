/*
 *   Copyright (C) 2025 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Reflection;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

final class InventoryTypeMapper_Reflection
{
	private static final Class<?> CLASS_CONTAINERS = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : NmsReflector.INSTANCE.getNmsClass("Containers");
	private static final EnumMap<InventoryType, Object> INVENTORY_TYPE_MAP = new EnumMap<>(InventoryType.class);
	private static final Object[] INVENTORY_TYPE_CHEST = new Object[6];

	static
	{
		if(CLASS_CONTAINERS != null)
		{
			Collection<Field> fields = Reflection.getFieldsIncludeParents(CLASS_CONTAINERS);
			Map<String, Field> fieldMap = new HashMap<>();
			for(Field f : fields)
			{
				//System.out.println(f.getName());
				fieldMap.put(f.getName(), f);
				fieldMap.put(f.getName().toUpperCase(Locale.ENGLISH), f);
			}
			Function<String, Field> resolveField = type -> {
				Field field = fieldMap.get(type); // Fast pass when no remapping is needed
				if(field != null) return field;
				field = NmsReflector.INSTANCE.getNmsField(CLASS_CONTAINERS, type);  // Fallback when remapping is needed
				return field;
			};
			for(InventoryType inventoryType : InventoryType.values())
			{
				String type = inventoryType.name();
				if(inventoryType == InventoryType.CHEST || inventoryType == InventoryType.PLAYER || inventoryType == InventoryType.CREATIVE) continue;
				else if(inventoryType == InventoryType.DISPENSER || inventoryType == InventoryType.DROPPER) type = "GENERIC_3X3";
				else if(inventoryType == InventoryType.BREWING) type = "BREWING_STAND";
				else if(inventoryType == InventoryType.WORKBENCH) type = "CRAFTING";
				else if(inventoryType == InventoryType.ENDER_CHEST) type = "GENERIC_9X3";
				else if(inventoryType == InventoryType.ENCHANTING) type = "ENCHANTMENT";
				else if(type.equals("BARREL")) type = "GENERIC_9X3";
				else if(type.equals("CARTOGRAPHY") && MCVersion.isNewerOrEqualThan(MCVersion.MC_NMS_1_15_R1)) type = "CARTOGRAPHY_TABLE";
				else if(type.equals("COMPOSTER") || type.equals("CHISELED_BOOKSHELF") || type.equals("SMITHING_NEW") || type.equals("JUKEBOX") || type.equals("DECORATED_POT") || type.equals("SHELF")) continue; // They don't have inventory screens
				try
				{
					Field field = resolveField.apply(type);
					if(field == null) continue;
					INVENTORY_TYPE_MAP.put(inventoryType, field.get(null));
				}
				catch(IllegalAccessException ignored) {}
				catch(Throwable t) { t.printStackTrace(); }
			}
			for(int i = 0; i < 6; i++)
			{
				try
				{
					INVENTORY_TYPE_CHEST[i] = resolveField.apply("GENERIC_9X" + (i + 1)).get(null);
				}
				catch(IllegalAccessException | NullPointerException ignored) {}
			}
		}
	}

	static Object getInvContainersObject(final @NotNull Inventory inv)
	{
		if(inv.getType() == InventoryType.CHEST)
		{
			return INVENTORY_TYPE_CHEST[Math.min(6, inv.getSize() / 9) - 1];
		}
		else
		{
			return INVENTORY_TYPE_MAP.get(inv.getType());
		}
	}

	private InventoryTypeMapper_Reflection() {}
}