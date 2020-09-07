/*
 * Copyright (C) 2016 GeorgH93
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bungee.Message.Sender;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.Title;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

final class ActionBarSender implements ISender
{
	private static final byte ACTION_BAR_ACTION = 2;

	@Override
	public void doSend(@NotNull ProxiedPlayer player, @NotNull String json)
	{
		if(player.getPendingConnection().getVersion() < ProtocolConstants.MINECRAFT_1_11)
			player.unsafe().sendPacket(new Chat(json, ACTION_BAR_ACTION));
		else
		{
			Title actionBarPacket = new Title();
			actionBarPacket.setText(json);
			actionBarPacket.setAction(Title.Action.ACTIONBAR);
		}
	}

	@Override
	public void doSend(@NotNull Collection<? extends ProxiedPlayer> players, @NotNull String json)
	{
		Title actionBarTitlePacket = new Title();
		actionBarTitlePacket.setText(json);
		actionBarTitlePacket.setAction(Title.Action.ACTIONBAR);
		Chat actionBarChatPacket = new Chat(json, ACTION_BAR_ACTION);
		for(ProxiedPlayer player : players)
		{
			if(player.getPendingConnection().getVersion() < ProtocolConstants.MINECRAFT_1_11)
				player.unsafe().sendPacket(actionBarChatPacket);
			else
				player.unsafe().sendPacket(actionBarTitlePacket);
		}
	}
}