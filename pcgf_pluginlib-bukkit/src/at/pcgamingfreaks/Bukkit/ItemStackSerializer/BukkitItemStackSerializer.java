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
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.ItemStackSerializer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @deprecated Unreliable on many MC versions when being used with items that have NBT-tags. Use {@link ItemStackSerializer#makeNBTItemStackSerializer()} instead.
 */
@Deprecated
final class BukkitItemStackSerializer implements ItemStackSerializer
{
	/**
	 * Deserialize a serialized byte array to an ItemStack array.
	 *
	 * @param data The data that should get deserialized.
	 * @return The deserialized ItemStack array. null if deserialization failed.
	 */
	@Override
	public ItemStack[] deserialize(byte[] data)
	{
		if(data != null)
		{
			try(BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(new ByteArrayInputStream(data)))
			{
				return (ItemStack[]) bukkitObjectInputStream.readObject();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Serializes a ItemStack array to a byte array.
	 *
	 * @param itemStacks The ItemStacks that should be serialized.
	 * @return Serialized ItemsStacks as byte array. null if serialization failed.
	 */
	@Override
	public byte[] serialize(ItemStack[] itemStacks)
	{
		byte[] ba = null;
		if(itemStacks != null)
		{
			try(ByteArrayOutputStream b = new ByteArrayOutputStream(); BukkitObjectOutputStream output = new BukkitObjectOutputStream(b))
			{
				output.writeObject(itemStacks);
				output.flush();
				ba = b.toByteArray();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ba;
	}

	@Override
	public boolean checkIsMCVersionCompatible()
	{
		return isMCVersionCompatible();
	}

	public static boolean isMCVersionCompatible()
	{
		return true;
	}
}