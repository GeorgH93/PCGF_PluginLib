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

package at.pcgamingfreaks.Bungee.Message.Sender;

import at.pcgamingfreaks.Bungee.Message.Message;
import at.pcgamingfreaks.Message.Sender.ISenderGeneric;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * This abstract class is used as base class for all senders.
 * This way we can tell the {@link Message} class which sender should get used without using reflection.
 */
public interface Sender extends ISenderGeneric<ProxiedPlayer>
{
}