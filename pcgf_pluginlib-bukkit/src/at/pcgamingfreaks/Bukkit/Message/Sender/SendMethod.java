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

package at.pcgamingfreaks.Bukkit.Message.Sender;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Message.Sender.IMetadata;
import at.pcgamingfreaks.Message.Sender.ISendMethod;
import at.pcgamingfreaks.Message.Sender.TitleMetadata;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

import java.util.Collection;
import java.util.function.Supplier;

import static at.pcgamingfreaks.Bukkit.MCVersion.MC_1_8;

public enum SendMethod implements ISendMethod, ISender
{
	CHAT_CLASSIC(new DisabledSender(), null),
	CHAT(MCVersion.isOlderThan(MC_1_8) ? null : new ChatSender(), CHAT_CLASSIC),
	TITLE(MCVersion.isOlderThan(MC_1_8) ? null : new TitleSender(), TitleMetadata.class, TitleMetadata::new, CHAT),
	ACTION_BAR(MCVersion.isOlderThan(MC_1_8) ? null : (MCVersion.isNewerOrEqualThan(MCVersion.MC_1_11)) ? new ActionBarTitleSender() : new ActionBarTitleSender(), CHAT),
	//BOSS_BAR(new BossBarSender(), BossBarMetadata.class, BossBarMetadata::new, ACTION_BAR), //TODO
	DISABLED(new DisabledSender(), null);

	@Getter @Nullable private final ISender sender;
	@Getter @NotNull private final ISender activeSender;
	@Getter @Nullable private final Class<? extends IMetadata> metadataClass;
	@Getter @NotNull private final SendMethod fallbackSendMethod;
	@Getter @Nullable private final Supplier<? extends IMetadata> metadataSupplier;

	SendMethod(@Nullable ISender sender, @Nullable SendMethod fallback)
	{
		this(sender, null, null, fallback);
	}

	/**
	 * @param sender An instance to an implementation of the Sender class.
	 * @param metadataClass An optional class defining the metadata for this send method.
	 * @param metadataSupplier A supplier that produces new metadata objects.
	 * @param fallback A fallback send method that should be used if the used send method is not available on the used MC version.
	 *                    There should always be one available if the send method is not available on all MC versions.
	 */
	SendMethod(@Nullable ISender sender, @Nullable Class<? extends IMetadata> metadataClass, @Nullable Supplier<? extends IMetadata> metadataSupplier, @Nullable SendMethod fallback)
	{
		this.sender = sender;
		this.metadataClass = metadataClass;
		this.metadataSupplier = metadataSupplier;
		if(fallback != null)
		{
			if(!fallback.isAvailable()) fallback = fallback.getFallbackSendMethod();
		}
		else fallback = this;
		fallbackSendMethod = fallback;
		activeSender = (isAvailable()) ? sender : fallback.getActiveSender();
	}

	/**
	 * Checks if the send method is available on the used server version
	 *
	 * @return True if the send method is available on the used server version. False if not.
	 */
	public boolean isAvailable()
	{
		return sender != null;
	}

	/**
	 * Checks if the send method allows to set additional parameters via a metadata class.
	 *
	 * @return True if the send method supports additional send method parameters. False if not.
	 */
	public boolean hasMetadata()
	{
		return metadataClass != null;
	}

	public @Nullable IMetadata parseMetadata(final @NotNull String json)
	{
		if(metadataSupplier == null) return null;
		IMetadata metadata = metadataSupplier.get();
		metadata.parseJson(json);
		return metadata;
	}

	@Override
	public void doSend(@NotNull Player player, @NotNull String json)
	{
		activeSender.doSend(player, json);
	}

	@Override
	public void doSend(@NotNull Player player, @NotNull String json, @Nullable Object optionalMetadata)
	{
		activeSender.doSend(player, json, optionalMetadata);
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json)
	{
		activeSender.doSend(players, json);
	}

	@Override
	public void doSend(@NotNull Collection<? extends Player> players, @NotNull String json, @Nullable Object optionalMetadata)
	{
		activeSender.doSend(players, json, optionalMetadata);
	}

	@Override
	public void doBroadcast(@NotNull String json)
	{
		activeSender.doBroadcast(json);
	}

	@Override
	public void doBroadcast(@NotNull String json, @Nullable Object optionalMetadata)
	{
		activeSender.doBroadcast(json, optionalMetadata);
	}
}