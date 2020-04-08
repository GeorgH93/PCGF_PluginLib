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

package at.pcgamingfreaks.Bukkit;

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
		String name = "";
		if(itemName != null)
		{
			name = (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_13)) ? "{\\\"text\\\":\\\"" + itemName + "\\\"}" : itemName;
			name = "display:{Name:\"" + name + "\"},";
		}
		if(ownerUUID == null)
		{
			ownerUUID = UUID.randomUUID();
		}
		String str = "{" + name + "SkullOwner:{Id:\"" + ownerUUID + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}";
		Bukkit.getUnsafe().modifyItemStack(item, str);
		return item;
	}

	private static String encodeUrl(String url)
	{
		String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
		return Base64.getEncoder().encodeToString(toEncode.getBytes());
	}
}