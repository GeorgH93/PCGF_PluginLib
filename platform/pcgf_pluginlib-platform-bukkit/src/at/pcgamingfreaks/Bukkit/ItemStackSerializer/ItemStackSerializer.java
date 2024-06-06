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

import at.pcgamingfreaks.Bukkit.IPlatformDependent;
import at.pcgamingfreaks.Bukkit.PlatformResolver;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public interface ItemStackSerializer extends IPlatformDependent
{
	String KEY_INVENTORY = "Inventory", KEY_DATA_VERSION = "DataVersion";
	String KEY_SIZE = "size", KEY_SLOT = "Slot";

	/**
	 * Deserialize a serialized byte array to an ItemStack array.
	 *
	 * @param data The data that should get deserialized.
	 * @return The deserialized ItemStack array.
	 */
	ItemStack[] deserialize(byte[] data);

	/**
	 * Serializes a ItemStack array to a byte array.
	 *
	 * @param itemStacks The ItemStacks that should be serialized.
	 * @return Serialized ItemsStacks as byte array. Null if serialization failed.
	 */
	byte[] serialize(ItemStack[] itemStacks);

	/**
	 * Checks if the minecraft version of the server is compatible with the logger!
	 *
	 * @return True if the servers minecraft version is compatible with the logger.
	 */
	boolean checkIsMCVersionCompatible();

	/**
	 * Sets the logger that should be used by the serializer.
	 *
	 * @param logger The logger that should be used. null for no logger.
	 */
	default void setLogger(final @Nullable Logger logger) {}

	/**
	 * Creates a BukkitItemStackSerializer.
	 * The serializer uses the Bukkit API and therefore should work on every server.
	 * However, the implementation of it is not stable in all server implementations
	 * and it is advisable to use the NBTItemStackSerializerGen2 (created with {@link ItemStackSerializer#makeNBTItemStackSerializer} instead.
	 *
	 * @return The created BukkitItemStackSerializer. Null if there was a problem creating the serializer.
	 * @deprecated Unreliable on many MC versions when being used with items that have NBT-tags. Use {@link ItemStackSerializer#makeNBTItemStackSerializer()} instead.
	 */
	@Deprecated
	static @Nullable ItemStackSerializer makeBukkitItemStackSerializer()
	{
		try
		{
			return (ItemStackSerializer) Class.forName("at.pcgamingfreaks.Bukkit.ItemStackSerializer.BukkitItemStackSerializer").newInstance();
		}
		catch(InstantiationException | IllegalAccessException | NullPointerException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	static ItemStackSerializer makeNBTItemStackSerializer()
	{
		return PlatformResolver.createPlatformInstance(ItemStackSerializer.class, "at.pcgamingfreaks.Bukkit.ItemStackSerializer.NBTItemStackSerializer");
	}

	static ItemStackSerializer makeNBTItemStackSerializer(final @Nullable Logger logger)
	{
		ItemStackSerializer serializer = makeNBTItemStackSerializer();
		serializer.setLogger(logger);
		return serializer;
	}

	static boolean isNBTItemStackSerializerAvailable()
	{
		return makeNBTItemStackSerializer().checkIsMCVersionCompatible();
	}
}