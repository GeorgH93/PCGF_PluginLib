package at.pcgamingfreaks.Bukkit.Util;

<#if 100140000 <= mcVersion>
import org.bukkit.craftbukkit.v${nmsVersion}.entity.CraftPlayer;
import net.minecraft.server.v${nmsVersion}.ChatMessage;
import net.minecraft.server.v${nmsVersion}.Containers;
import net.minecraft.server.v${nmsVersion}.EntityPlayer;
import net.minecraft.server.v${nmsVersion}.PacketPlayOutOpenWindow;
</#if>
import net.minecraft.server.v${nmsVersion}.NBTTagCompound;
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
 * Reference: http://freemarker.org
 * See template: ${.main_template_name}
 */
public final class InventoryUtils_${nmsVersion} extends InventoryUtils_Reflection
{
	@Override
	public void updateInventoryTitle(final @NotNull Player player, final @NotNull String newTitle)
	{
		<#if 100140000 <= mcVersion>
		InventoryView view = player.getOpenInventory();
		Inventory topInv = view.getTopInventory();
		if(topInv.getType() == InventoryType.CRAFTING) return;

		EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
		PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(entityPlayer.activeContainer.windowId, (Containers) getInvContainersObject(topInv), new ChatMessage(newTitle));
		entityPlayer.playerConnection.sendPacket(packet);
		entityPlayer.updateInventory(entityPlayer.activeContainer);
		</#if>
	}

	@Override
	public String convertItemStackToJson(final @NotNull ItemStack itemStack, final @NotNull Logger logger)
	{
		return CraftItemStack.asNMSCopy(itemStack).save(new NBTTagCompound()).toString();
	}
}