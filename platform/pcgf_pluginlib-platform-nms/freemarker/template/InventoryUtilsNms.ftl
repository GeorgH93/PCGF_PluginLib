package at.pcgamingfreaks.Bukkit.Util;

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

import org.bukkit.craftbukkit.v${nmsVersion}.entity.CraftPlayer;
import org.bukkit.craftbukkit.v${nmsVersion}.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: https://freemarker.apache.org/
 * See template: ${.main_template_name}
 */
public final class InventoryUtils_${nmsVersion} extends InventoryUtils_Reflection
{
	@Override
	public String convertItemStackToJson(final @NotNull ItemStack itemStack, final @NotNull Logger logger)
	{
		return CraftItemStack.asNMSCopy(itemStack).save(new NBTTagCompound()).toString();
	}

	@Override
	public Object prepareTitleForUpdateInventoryTitle(final @NotNull String title)
	{
		<#if 100140000 <= mcVersion>
		return new ChatMessage(title);
		<#else>
		return null;
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

		EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
		PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(entityPlayer.<#if mcVersion < 100170000>activeContainer.windowId<#else>bV.j</#if>, (Containers) getInvContainersObject(topInv), (IChatBaseComponent) newTitle);
		entityPlayer.<#if mcVersion < 100170000>playerConnection<#else>b</#if>.sendPacket(packet);
		<#if mcVersion < 100170000>
		entityPlayer.updateInventory(entityPlayer.activeContainer);
		<#else>
		entityPlayer.bV.updateInventory();
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
}