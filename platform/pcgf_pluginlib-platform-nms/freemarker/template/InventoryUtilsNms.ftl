/*
 *   Copyright (C) 2021 GeorgH93
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

<#if mojangMapped>
import net.minecraft.nbt.CompoundTag;
	<#if mcVersion < 100190000>
import net.minecraft.network.chat.TranslatableComponent;
	</#if>
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
<#else>
	<#if mcVersion < 100170000>
		<#if 100140000 <= mcVersion>
import net.minecraft.server.v${nmsVersion}.IChatBaseComponent;
import net.minecraft.server.v${nmsVersion}.ChatMessage;
import net.minecraft.server.v${nmsVersion}.Containers;
import net.minecraft.server.v${nmsVersion}.EntityPlayer;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutOpenWindow;
		</#if>
import net.minecraft.server.v${nmsVersion}.NBTTagCompound;
	<#else>
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Containers;
	</#if>
</#if>

<#if !mojangMapped>
import org.bukkit.craftbukkit.v${nmsVersion}.entity.CraftPlayer;
</#if>
import org.bukkit.craftbukkit.v${nmsVersion}.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: https://freemarker.apache.org/
 * See template: ${.main_template_name}
 */
public final class InventoryUtils_${nmsVersion}${nmsPatchLevel}${nmsExtension} implements IInventoryUtils
{
	@Override
	public String convertItemStackToJson(final @NotNull ItemStack itemStack, final @NotNull Logger logger)
	{
		<#if mcVersion < 100200005>
		return CraftItemStack.asNMSCopy(itemStack).save(new <#if mojangMapped>CompoundTag<#else>NBTTagCompound</#if>()).toString();
		<#else>
		return CraftItemStack.asNMSCopy(itemStack).save(net.minecraft.core.RegistryAccess.EMPTY).toString();
		</#if>
	}

	@Override
	public Object prepareTitleForUpdateInventoryTitle(final @NotNull String title)
	{
		<#if mojangMapped>
			<#if mcVersion < 100190000>
		return new TranslatableComponent(title);
			<#else>
		return Component.literal(title);
			</#if>
		<#else>
			<#if 100140000 <= mcVersion>
		return new ChatMessage(title);
			<#else>
		return null;
			</#if>
		</#if>
	}

	@Override
	public void updateInventoryTitle(final @NotNull Player player, final @NotNull String newTitle)
	{
		<#if 100140000 <= mcVersion>
		updateInventoryTitlePrepared(player, prepareTitleForUpdateInventoryTitle(newTitle));
		</#if>
	}

	@Override
	public void updateInventoryTitlePrepared(final @NotNull Player player, final @NotNull Object newTitle)
	{
		<#if 100140000 <= mcVersion>
		InventoryView view = player.getOpenInventory();
		Inventory topInv = view.getTopInventory();
		if(topInv.getType() == InventoryType.CRAFTING) return;

			<#if mojangMapped>
		ServerPlayer entityPlayer = (ServerPlayer) IUtils.INSTANCE.getHandle(player);
		ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(entityPlayer.containerMenu.containerId, (MenuType<?>) InventoryTypeMapper_Reflection.getInvContainersObject(topInv), (Component) newTitle);
		entityPlayer.connection.send(packet);
		entityPlayer.containerMenu.sendAllDataToRemote();
			<#else>
		EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
		PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(entityPlayer.<#if mcVersion < 100170000>activeContainer.windowId<#else>bV.j</#if>, (Containers) InventoryTypeMapper_Reflection.getInvContainersObject(topInv), (IChatBaseComponent) newTitle);
		entityPlayer.<#if mcVersion < 100170000>playerConnection<#else>b</#if>.sendPacket(packet);
				<#if mcVersion < 100170000>
		entityPlayer.updateInventory(entityPlayer.activeContainer);
				<#else>
		entityPlayer.bV.updateInventory();
				</#if>
			</#if>
		</#if>
	}

	@Override
	public Object prepareTitleForOpenInventoryWithCustomTitle(final @NotNull String title)
	{
		<#if mcVersion < 100140000>
		return prepareTitleForSetInventoryTitle(title);
		<#else>
		return prepareTitleForUpdateInventoryTitle(title);
		</#if>
	}

	@Override
	public void openInventoryWithCustomTitle(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull String title)
	{
		<#if mcVersion < 100140000>
		Object currentTitle = getInventoryTitle(inventory);
		setInventoryTitle(inventory, title);
		</#if>
		player.openInventory(inventory);
		<#if 100140000 <= mcVersion>
		updateInventoryTitle(player, title);
		<#else>
		setInventoryTitlePrepared(inventory, currentTitle);
		</#if>
	}

	@Override
	public void openInventoryWithCustomTitlePrepared(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull Object title)
	{
		<#if mcVersion < 100140000>
		Object currentTitle = getInventoryTitle(inventory);
		setInventoryTitlePrepared(inventory, title);
		</#if>
		player.openInventory(inventory);
		<#if 100140000 <= mcVersion>
		updateInventoryTitlePrepared(player, title);
		<#else>
		setInventoryTitlePrepared(inventory, currentTitle);
		</#if>
	}

	@Override
	public Object getInventoryTitle(final @NotNull Inventory inventory)
	{
		return null;
	}

	@Override
	public void setInventoryTitle(final @NotNull Inventory inventory, final @NotNull String newTitle)
	{
	}

	@Override
	public Object prepareTitleForSetInventoryTitle(@NotNull String title)
	{
		return null;
	}


	@Override
	public void setInventoryTitlePrepared(final @NotNull Inventory inventory, final @NotNull Object newTitle)
	{
	}

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