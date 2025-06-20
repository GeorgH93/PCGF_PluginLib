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
import net.minecraft.nbt.*;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;

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
	<#if mcVersion < 100210006>
	private static final int DATA_VERSION = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
	<#else>
	private static final int DATA_VERSION = SharedConstants.getCurrentVersion().dataVersion().version();
	</#if>
	private static final DataFixer DATA_FIXER = ((CraftServer) Bukkit.getServer()).getServer().fixerUpper;

	@Setter private Logger logger = null;

	private final HolderLookup.Provider registry = MinecraftServer.getServer().registryAccess();


	private CompoundTag readData(byte[] data) throws IOException
	{
		String error = "";
		try
		{
			return NbtIo.read(new DataInputStream(new ByteArrayInputStream(data)));
		}
		catch(Exception e)
		{
			error = e.getMessage();
		}
		try
		{
			return NbtIo.readCompressed(new ByteArrayInputStream(data), NbtAccounter.unlimitedHeap());
		}
		catch(Exception e)
		{
			error += " | " + e.getMessage();
		}
		throw new IOException(error);
	}

	@Override
	public ItemStack[] deserialize(byte[] data)
	{
		if (data != null)
		{
			try
			{
				CompoundTag tag = readData(data);
				<#if mcVersion < 100210005>
				int size = tag.getInt(KEY_SIZE), dataVersion = DATA_VERSION;
				if (tag.contains(KEY_DATA_VERSION, CompoundTag.TAG_INT)) dataVersion = tag.getInt(KEY_DATA_VERSION);
				<#else>
				int size = tag.getInt(KEY_SIZE).orElseThrow(), dataVersion = tag.getIntOr(KEY_DATA_VERSION, DATA_VERSION);
				</#if>
				if (!tag.contains(KEY_INVENTORY)) { convertOldFormatToNew(tag, size); }
				if (dataVersion < DATA_VERSION)
				{ // Update data
					tag = DataFixTypes.PLAYER.updateToCurrentVersion(DATA_FIXER, tag, dataVersion);
				}
				ItemStack[] its = new ItemStack[size];
				<#if mcVersion < 100210005>
				ListTag list = tag.getList(KEY_INVENTORY, CompoundTag.TAG_COMPOUND);
				<#else>
				ListTag list = tag.getList(KEY_INVENTORY).orElseThrow();
				</#if>
				int listSize = list.size();
				for (int i = 0; i < listSize; i++)
				{
					CompoundTag itemTag = null;
					try
					{
						<#if mcVersion < 100210005>
						itemTag = list.getCompound(i);
						byte slot = itemTag.getByte(KEY_SLOT);
						<#else>
						itemTag = list.getCompound(i).orElseThrow();
						byte slot = itemTag.getByte(KEY_SLOT).orElseThrow();
						</#if>
						<#if mcVersion < 100210006>
						Optional<net.minecraft.world.item.ItemStack> item = net.minecraft.world.item.ItemStack.parse(registry, itemTag);
						<#else>
						Optional<net.minecraft.world.item.ItemStack> item =  net.minecraft.world.item.ItemStack.CODEC.parse(registry.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), itemTag).resultOrPartial((s) -> {
							logger.severe(String.format("Tried to load invalid item: '%s'", s));
						});
						</#if>
						its[slot] = CraftItemStack.asBukkitCopy(item.orElse(net.minecraft.world.item.ItemStack.EMPTY));
					}
					catch(Exception ignored)
					{
						if(logger != null) logger.warning("Failed to restore item on slot " + i + " with json:\n" + (itemTag == null ? "null" : itemTag.toString()));
					}
				}
				return its;
			}
			catch (Exception e)
			{
				if (logger != null) logger.log(Level.SEVERE, "Failed to deserialize NBTItemStack (size: " + data.length + ")", e);
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
			String is = String.valueOf(i);
	<#if mcVersion < 100210005>
			if (tag.contains(is, CompoundTag.TAG_COMPOUND))
			{
				CompoundTag itemTag = tag.getCompound(is);
	<#else>
			if (tag.contains(is))
			{
				CompoundTag itemTag = tag.getCompound(is).orElseThrow();
	</#if>
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
					try
					{
						CompoundTag itemTag = new CompoundTag();
						itemTag.putByte(KEY_SLOT, (byte) i);
						<#if mcVersion < 100210006>
						Tag t = CraftItemStack.asNMSCopy(itemStacks[i]).save(registry, itemTag);
						<#else>
						net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(itemStacks[i]);
						Tag t = net.minecraft.world.item.ItemStack.CODEC.encode(stack, MinecraftServer.getServer().registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), itemTag).getOrThrow();
						</#if>

						list.addTag(++used, t);
					}
					catch (Exception ignored)
					{
						logger.warning("Failed to store item on slot " + i + ":\n" + itemStacks[i].toString());
					}
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