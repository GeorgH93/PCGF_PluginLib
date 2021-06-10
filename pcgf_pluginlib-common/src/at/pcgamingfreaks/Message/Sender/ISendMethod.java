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

package at.pcgamingfreaks.Message.Sender;

import org.jetbrains.annotations.Nullable;

public interface ISendMethod
{
	/**
	 * Gets the metadata class for the used option.
	 *
	 * @return The metadata class.
	 */
	@Deprecated
	@Nullable Class<? extends IMetadata> getMetadataClass();

	/**
	 * Creates a metadata object from a json representation of the object.
	 *
	 * @param metadataJson The json representing the metadata that should be parsed.
	 * @return The parsed metadata as a metadata object to be used with the senders.
	 */
	@Nullable IMetadata parseMetadata(String metadataJson);
}