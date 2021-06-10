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

package at.pcgamingfreaks.Bukkit.GUI;

import at.pcgamingfreaks.Bukkit.MCVersion;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuiButton
{
	public interface ButtonClickHandler
	{
		void onClick(final @NotNull Player player, final @NotNull ClickType clickType, final @Nullable ItemStack cursor);
	}

	public static final Sound MINECRAFT_CLICK_SOUND;
	public static final GuiButton EMPTY_BUTTON;

	static
	{
		Sound sound = null;
		try
		{
			sound = Sound.valueOf(MCVersion.isNewerOrEqualThan(MCVersion.MC_1_9_2) ? "UI_BUTTON_CLICK" : "CLICK");
		}
		catch(IllegalArgumentException e) { e.printStackTrace(); }
		MINECRAFT_CLICK_SOUND = sound;
		EMPTY_BUTTON = new GuiButton(null, (player, clickType, cursor) -> {}, null);
	}

	private final @Nullable ItemStack item;
	private final @NotNull ButtonClickHandler clickEvent;
	private final @Nullable Sound clickSound;

	public GuiButton(final @Nullable ItemStack itemStack, final @NotNull ButtonClickHandler clickEvent)
	{
		this(itemStack, clickEvent, MINECRAFT_CLICK_SOUND);
	}

	public void onClick(final @NotNull Player player, final @NotNull ClickType clickType, final @Nullable ItemStack cursor)
	{
		if(clickSound != null) player.playSound(player.getEyeLocation(), clickSound, 1, 1);
		clickEvent.onClick(player, clickType, cursor);
	}
}