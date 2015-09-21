/*
 * Copyright (C) 2014-2015 GeorgH93
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.ItemStackSerializer;

import at.pcgamingfreaks.Bukkit.Reflection;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Method;

public class NBTItemStackSerializer implements ItemStackSerializer
{
	Class NBTTagCompound = Reflection.getNMSClass("NBTTagCompound"), NBTCompressedStreamTools = Reflection.getNMSClass("NBTCompressedStreamTools");
	Class CraftItemStack = Reflection.getOBCClass("inventory.CraftItemStack"), NMSItemStack = Reflection.getNMSClass("ItemStack");

	Method setInt = Reflection.getMethod(NBTTagCompound, "setInt", String.class, int.class), a = Reflection.getMethod(NBTCompressedStreamTools, "a", NBTTagCompound, OutputStream.class);
	Method set = Reflection.getMethod(NBTTagCompound, "set", String.class, Reflection.getNMSClass("NBTBase")), save = Reflection.getMethod(NMSItemStack, "save", NBTTagCompound);
	Method asNMSCopy = Reflection.getMethod(CraftItemStack, "asNMSCopy", ItemStack.class), getInt = Reflection.getMethod(NBTTagCompound, "getInt", String.class);
	Method hasKeyOfType = Reflection.getMethod(NBTTagCompound, "hasKeyOfType", String.class, int.class), getCompound = Reflection.getMethod(NBTTagCompound, "getCompound", String.class);
	Method createStack = Reflection.getMethod(NMSItemStack, "createStack", NBTTagCompound), asBukkitCopy = Reflection.getMethod(CraftItemStack, "asBukkitCopy", NMSItemStack);
	Method ain = Reflection.getMethod(NBTCompressedStreamTools, "a", InputStream.class);

	/**
	 * Deserialize a serialized byte array to an ItemStack array.
	 *
	 * @param data The data that should get deserialized.
	 * @return The deserialized ItemStack array.
	 */
	@Override
	public ItemStack[] deserialize(byte[] data)
	{
		try
		{
			if (data != null)
			{
				Object localNBTTagCompound = ain.invoke(null, new ByteArrayInputStream(data));
				int size = (int)getInt.invoke(localNBTTagCompound, "size");
				ItemStack[] its = new ItemStack[size];
				for (int i = 0; i < size; i++)
				{
					if ((boolean)hasKeyOfType.invoke(localNBTTagCompound, String.valueOf(i), 10))
					{
						its[i] = (ItemStack)asBukkitCopy.invoke(null, createStack.invoke(null, getCompound.invoke(localNBTTagCompound, String.valueOf(i))));
					}
				}
				return its;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Serializes a ItemStack array to a byte array.
	 *
	 * @param itemStacks The ItemStacks that should be serialized.
	 * @return Serialized ItemsStacks as byte array. Null if serialization failed.
	 */
	@Override
	public byte[] serialize(ItemStack[] itemStacks)
	{
		byte[] ba = null;
		try
		{
			Object localNBTTagCompound = NBTTagCompound.newInstance();
			setInt.invoke(localNBTTagCompound, "size", itemStacks.length);
			for (int i = 0; i < itemStacks.length; i++)
			{
				if (itemStacks[i] != null)
				{
					set.invoke(localNBTTagCompound, String.valueOf(i), save.invoke(asNMSCopy.invoke(null, itemStacks[i]), NBTTagCompound.newInstance()));
				}
			}
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
			a.invoke(null, localNBTTagCompound, dataOutputStream);
			dataOutputStream.flush();
			ba = byteArrayOutputStream.toByteArray();
			dataOutputStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ba;
	}
}