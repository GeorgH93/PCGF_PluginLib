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

package at.pcgamingfreaks.Bukkit.ItemStackSerializer;

<#if mcVersion < 100200005>
public final class NBTItemStackSerializer_${nmsVersion}${nmsPatchLevel}${nmsExtension} extends NBTItemStackSerializer_Reflection
{}
<#else>
import at.pcgamingfreaks.Bukkit.MCVersion;

import com.mojang.datafixers.DataFixer;

import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import net.minecraft.util.datafix.DataFixTypes;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v${nmsVersion}.CraftServer;
import org.bukkit.craftbukkit.v${nmsVersion}.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import lombok.Setter;

import java.io.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * NOTE: Generated code !! DO NOT EDIT !!
 * Reference: https://freemarker.apache.org/
 * See template: ${.main_template_name}
 */
public class NBTItemStackSerializer_${nmsVersion}${nmsPatchLevel}${nmsExtension} implements ItemStackSerializer
{
	private static final int DATA_VERSION = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
	private static final DataFixer DATA_FIXER = ((CraftServer) Bukkit.getServer()).getServer().fixerUpper;

	@Setter private Logger logger = null;

	@Override
	public ItemStack[] deserialize(byte[] data)
	{
		if (data != null)
		{
			try
			{
				CompoundTag tag = NbtIo.read(new DataInputStream(new ByteArrayInputStream(data)));
				int size = tag.getInt(KEY_SIZE), dataVersion = DATA_VERSION;
				if (tag.contains(KEY_DATA_VERSION, CompoundTag.TAG_INT)) dataVersion = tag.getInt(KEY_DATA_VERSION);
				if (!tag.contains(KEY_INVENTORY)) { convertOldFormatToNew(tag, size); }
				if (dataVersion < DATA_VERSION)
				{ // Update data
					DataFixTypes.PLAYER.updateToCurrentVersion(DATA_FIXER, tag, dataVersion);
				}
				ItemStack[] its = new ItemStack[size];
				ListTag list = tag.getList(KEY_INVENTORY, CompoundTag.TAG_COMPOUND);
				int listSize = list.size();
				for (int i = 0; i < listSize; i++)
				{
					CompoundTag itemTag = list.getCompound(i);
					byte slot = itemTag.getByte(KEY_SLOT);
					Optional<net.minecraft.world.item.ItemStack> item = net.minecraft.world.item.ItemStack.parse(RegistryAccess.EMPTY, itemTag);
					its[slot] = CraftItemStack.asBukkitCopy(item.orElse(net.minecraft.world.item.ItemStack.EMPTY));
				}
				return its;
			}
			catch (Exception e)
			{
				if (logger != null) logger.log(Level.SEVERE, "Failed to deserialize NBTItemStack", e);
				else e.printStackTrace();
			}
		}
		return null;
	}

	private void convertOldFormatToNew(CompoundTag tag, int size)
	{
		ListTag list = new ListTag();
		tag.put(KEY_INVENTORY, list);
		for(int i = 0; i < size; i++)
		{
			if (tag.contains(String.valueOf(i), CompoundTag.TAG_COMPOUND))
			{
				CompoundTag itemTag = tag.getCompound(String.valueOf(i));
				itemTag.putByte(KEY_SLOT, (byte) i);
				list.add(itemTag);
			}
		}
	}

	@Override
	public byte[] serialize(ItemStack[] itemStacks)
	{
		if(itemStacks != null)
		{
			try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream))
			{
				CompoundTag tag = new CompoundTag();
				tag.putInt(KEY_SIZE, itemStacks.length);
				tag.putInt(KEY_DATA_VERSION, DATA_VERSION);
				ListTag list = new ListTag();
				tag.put(KEY_INVENTORY, list);
				for(int i = 0, used = -1; i < itemStacks.length; i++)
				{
					if (itemStacks[i] == null) continue;
					CompoundTag itemTag = new CompoundTag();
					itemTag.putByte(KEY_SLOT, (byte) i);
					Tag t = CraftItemStack.asNMSCopy(itemStacks[i]).save(RegistryAccess.EMPTY, itemTag);
					list.addTag(++used, t);
				}
				NbtIo.write(tag, dataOutputStream);
				dataOutputStream.flush();
				return byteArrayOutputStream.toByteArray();
			}
			catch(Exception e)
			{
				if (logger != null) logger.log(Level.SEVERE, "Failed to serialize NBTItemStack", e);
				else e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean checkIsMCVersionCompatible()
	{
		return MCVersion.is(MCVersion.MC_NMS_${nmsVersion});
	}
}
</#if>