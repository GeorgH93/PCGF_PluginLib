/*
 *   Copyright (C) 2024 GeorgH93
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
import at.pcgamingfreaks.Bukkit.OBCReflection;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.Util.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import static at.pcgamingfreaks.Bukkit.Util.Utils_Reflection.*;

@SuppressWarnings("ConstantConditions")
public class InventoryUtils_Reflection implements IInventoryUtils
{
	private static final Class<?> CLASS_CONTAINER = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : NmsReflector.INSTANCE.getNmsClass("Container");
	private static final Class<?> CLASS_CONTAINERS = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : NmsReflector.INSTANCE.getNmsClass("Containers");
	private static final Constructor<?> CONSTRUCTOR_CHAT_MESSAGE = (MCVersion.isOlderThan(MCVersion.MC_1_14) || MCVersion.isNewerOrEqualThan(MCVersion.MC_1_19)) ? null : Reflection.getConstructor(NmsReflector.INSTANCE.getNmsClass("ChatMessage"), String.class, Object[].class);
	private static final Constructor<?> CONSTRUCTOR_PACKET_PLAY_OUT_OPEN_WINDOW = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : Reflection.getConstructor(NmsReflector.INSTANCE.getNmsClass("PacketPlayOutOpenWindow"), int.class, CLASS_CONTAINERS, NmsReflector.INSTANCE.getNmsClass("IChatBaseComponent"));
	private static final Field FIELD_ACTIVE_CONTAINER = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : (MCVersion.isOlderThan(MCVersion.MC_1_17)) ? Reflection.getFieldIncludeParents(ENTITY_PLAYER, "activeContainer") : NmsReflector.INSTANCE.getNmsField("EntityHuman", "activeContainer");
	private static final Field FIELD_CONTAINER_WINDOW_ID = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : NmsReflector.INSTANCE.getNmsField(CLASS_CONTAINER, "windowId");
	private static final Method METHOD_ENTITY_PLAYER_UPDATE_INVENTORY = (MCVersion.isOlderThan(MCVersion.MC_1_14) || MCVersion.isNewerOrEqualThan(MCVersion.MC_1_17)) ? null : NmsReflector.INSTANCE.getNmsMethod(ENTITY_PLAYER, "updateInventory", CLASS_CONTAINER);
	private static final Method METHOD_CONTAINER_UPDATE_INVENTORY = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_17) ? NmsReflector.INSTANCE.getNmsMethod(CLASS_CONTAINER, "updateInventory") : null;
	private static final Method METHOD_COMPONENT_LITERAL = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_19) ? NmsReflector.INSTANCE.getNmsMethod("IChatBaseComponent", "literal", String.class) : null;

	private static final Class<?> NBT_TAG_COMPOUND_CLASS = NmsReflector.INSTANCE.getNmsClass("NBTTagCompound");
	private static final Constructor<?> NBT_TAG_COMPOUND_CONSTRUCTOR = Reflection.getConstructor(NBT_TAG_COMPOUND_CLASS);
	private static final Method AS_NMS_COPY_METHOD = OBCReflection.getOBCMethod("inventory.CraftItemStack", "asNMSCopy", ItemStack.class);
	private static final Method SAVE_NMS_ITEM_STACK_METHOD = NmsReflector.INSTANCE.getNmsMethod("ItemStack", "save", NBT_TAG_COMPOUND_CLASS);

	private static final Method METHOD_GET_INVENTORY = OBCReflection.getOBCMethod("inventory.CraftInventory", "getInventory");
	private static final Method METHOD_CRAFT_CHAT_MESSAGE_FROM_STRING = MCVersion.isAny(MCVersion.MC_1_13) ? OBCReflection.getOBCMethod("util.CraftChatMessage", "wrapOrNull", String.class) : null;
	private static final Field FIELD_TITLE = OBCReflection.getOBCField("inventory.CraftInventoryCustom$MinecraftInventory", "title");
	private static final String SERIALISATION_FAILED_LOG_MESSAGE = "Failed to serialize item stack to JSON! Bukkit Version: " + Bukkit.getServer().getVersion();

	@Override
	public String convertItemStackToJson(final @NotNull ItemStack itemStack, final @NotNull Logger logger)
	{
		try
		{
			return SAVE_NMS_ITEM_STACK_METHOD.invoke(AS_NMS_COPY_METHOD.invoke(null, itemStack), NBT_TAG_COMPOUND_CONSTRUCTOR.newInstance()).toString();
		}
		catch (Throwable t)
		{
			logger.log(Level.SEVERE, SERIALISATION_FAILED_LOG_MESSAGE, t);
		}
		return "";
	}

	@Override
	public Object prepareTitleForUpdateInventoryTitle(final @NotNull String title)
	{
		try
		{
			if (CONSTRUCTOR_CHAT_MESSAGE != null)
			{
				return CONSTRUCTOR_CHAT_MESSAGE.newInstance(title, new Object[0]);
			}
			else
			{
				METHOD_COMPONENT_LITERAL.invoke(null, title);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void updateInventoryTitle(final @NotNull Player player, final @NotNull String newTitle)
	{
		if(MCVersion.isOlderThan(MCVersion.MC_1_14)) return;
		updateInventoryTitlePrepared(player, prepareTitleForUpdateInventoryTitle(newTitle));
	}

	@Override
	public void updateInventoryTitlePrepared(final @NotNull Player player, final @NotNull Object title)
	{
		if(MCVersion.isOlderThan(MCVersion.MC_1_14)) return;
		InventoryView view = player.getOpenInventory();
		Inventory topInv = view.getTopInventory();
		if(topInv.getType() == InventoryType.CRAFTING) return;
		try
		{
			Object entityPlayer = NmsReflector.getHandle(player);
			if(entityPlayer == null || entityPlayer.getClass() != ENTITY_PLAYER) return; // Not a real player
			Object activeContainer = FIELD_ACTIVE_CONTAINER.get(entityPlayer);
			Object windowId = FIELD_CONTAINER_WINDOW_ID.get(activeContainer);
			Object packet = CONSTRUCTOR_PACKET_PLAY_OUT_OPEN_WINDOW.newInstance(windowId, InventoryTypeMapper_Reflection.getInvContainersObject(topInv), title);
			SEND_PACKET.invoke(PLAYER_CONNECTION.get(entityPlayer), packet);
			if(METHOD_ENTITY_PLAYER_UPDATE_INVENTORY != null)
				METHOD_ENTITY_PLAYER_UPDATE_INVENTORY.invoke(entityPlayer, activeContainer);
			else if(METHOD_CONTAINER_UPDATE_INVENTORY != null)
				METHOD_CONTAINER_UPDATE_INVENTORY.invoke(activeContainer);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Object prepareTitleForOpenInventoryWithCustomTitle(final @NotNull String title)
	{
		if(MCVersion.isOlderThan(MCVersion.MC_1_14)) return prepareTitleForSetInventoryTitle(title);
		else return prepareTitleForUpdateInventoryTitle(title);
	}

	@Override
	public void openInventoryWithCustomTitle(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull String title)
	{
		Object currentTitle = null;
		if(MCVersion.isOlderThan(MCVersion.MC_1_14))
		{
			currentTitle = getInventoryTitle(inventory);
			setInventoryTitle(inventory, title);
		}
		player.openInventory(inventory);
		if(currentTitle != null) setInventoryTitlePrepared(inventory, currentTitle);
		else updateInventoryTitle(player, title);
	}

	@Override
	public void openInventoryWithCustomTitlePrepared(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull Object title)
	{
		Object currentTitle = null;
		if(MCVersion.isOlderThan(MCVersion.MC_1_14))
		{
			currentTitle = getInventoryTitle(inventory);
			setInventoryTitlePrepared(inventory, title);
		}
		player.openInventory(inventory);
		if (currentTitle != null) setInventoryTitlePrepared(inventory, currentTitle);
		else updateInventoryTitlePrepared(player, title);
	}

	@Override
	public Object getInventoryTitle(final @NotNull Inventory inventory)
	{
		try
		{
			return FIELD_TITLE.get(METHOD_GET_INVENTORY.invoke(inventory));
		}
		catch(IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	//region set inventory title

	@Override
	public void setInventoryTitle(final @NotNull Inventory inventory, final @NotNull String newTitle)
	{
		setInventoryTitlePrepared(inventory, prepareTitleForSetInventoryTitle(newTitle));
	}

	@Override
	public Object prepareTitleForSetInventoryTitle(@NotNull String title)
	{
		if(MCVersion.isAny(MCVersion.MC_1_13))
		{
			try
			{
				//noinspection ConstantConditions
				return METHOD_CRAFT_CHAT_MESSAGE_FROM_STRING.invoke(null, title);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			return StringUtils.limitLength(title, 32);
		}
		return null;
	}

	@Override
	public void setInventoryTitlePrepared(final @NotNull Inventory inventory, final @NotNull Object newTitle)
	{
		try
		{
			FIELD_TITLE.set(METHOD_GET_INVENTORY.invoke(inventory), newTitle);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//endregion

	@Override
	public @Nullable Inventory getClickedInventory(final @NotNull InventoryClickEvent event)
	{
		if (event.getRawSlot() < 0) return null;

		InventoryView view = event.getView();
		Inventory topInventory = view.getTopInventory();
		if (event.getRawSlot() < topInventory.getSize())
			return topInventory;
		else
			return view.getBottomInventory();
	}

	@Override
	public @Nullable Inventory getPlayerTopInventory(final @NotNull Player player)
	{
		InventoryView view = player.getOpenInventory();
		if(view == null) return null;
		return view.getTopInventory();
	}
}