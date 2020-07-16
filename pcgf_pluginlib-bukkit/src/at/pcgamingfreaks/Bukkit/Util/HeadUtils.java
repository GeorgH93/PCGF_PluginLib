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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.MCVersion;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.UUID;

public class HeadUtils
{
	public static final Material HEAD_MATERIAL = MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13) ? Material.valueOf("PLAYER_HEAD") : Material.valueOf("SKULL_ITEM");

	public static ItemStack createHeadItemStack()
	{
		return createHeadItemStack(1);
	}

	public static ItemStack createHeadItemStack(final int amount)
	{
		return (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13)) ? new ItemStack(HEAD_MATERIAL, amount) : new ItemStack(HEAD_MATERIAL, amount, (byte) 3);
	}

	public static ItemStack fromUrl(final @NotNull String url, final @Nullable String itemName, final @Nullable UUID ownerUUID)
	{
		return fromBase64(encodeUrl(url), itemName, ownerUUID);
	}

	public static ItemStack fromUrl(final @NotNull String url, final @Nullable String itemName, final @Nullable UUID ownerUUID, final int amount)
	{
		return fromBase64(encodeUrl(url), itemName, ownerUUID, amount);
	}

	public static ItemStack fromUrl(final @NotNull ItemStack item, final @NotNull String url, final @Nullable String itemName, final @Nullable UUID ownerUUID)
	{
		return fromBase64(item, encodeUrl(url), itemName, ownerUUID);
	}

	public static ItemStack fromBase64(final @NotNull String value, final @Nullable String itemName, final @Nullable UUID ownerUUID)
	{
		return fromBase64(createHeadItemStack(), value, itemName, ownerUUID);
	}

	public static ItemStack fromBase64(final @NotNull String value, @Nullable String itemName, @Nullable UUID ownerUUID, int amount)
	{
		return fromBase64(createHeadItemStack(amount), value, itemName, ownerUUID);
	}

	public static ItemStack fromBase64(final @NotNull ItemStack item, final @NotNull String value, final @Nullable String itemName, @Nullable UUID ownerUUID)
	{
		if(ownerUUID == null) ownerUUID = UUID.randomUUID();
		StringBuilder builder = new StringBuilder("{");
		if(itemName != null)
		{
			builder.append("display:{Name:\"");
			if(MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13))
				builder.append("{\\\"text\\\":\\\"").append(itemName).append("\\\"}");
			else
				builder.append(itemName);
			builder.append("\"},");
		}
		builder.append("SkullOwner:{Id:");
		if(MCVersion.isOlderThan(MCVersion.MC_1_16))
		{
			builder.append('"').append(ownerUUID.toString()).append('"');
		}
		else
		{
			long most = ownerUUID.getMostSignificantBits(), least = ownerUUID.getLeastSignificantBits();
			builder.append("[I;");
			builder.append((int) (most >> 32)).append(',').append((int) most).append(',');
			builder.append((int) (least >> 32)).append(',').append((int) least).append(']');
		}
		builder.append(",Properties:{textures:[{Value:\"").append(value).append("\"}]}}}");
		Bukkit.getUnsafe().modifyItemStack(item, builder.toString());
		return item;
	}

	private static String encodeUrl(String url)
	{
		String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
		return Base64.getEncoder().encodeToString(toEncode.getBytes());
	}
}