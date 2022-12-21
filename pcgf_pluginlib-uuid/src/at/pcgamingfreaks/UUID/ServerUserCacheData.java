/*
 *   Copyright (C) 2021 GeorgH93
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

package at.pcgamingfreaks.UUID;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public final class ServerUserCacheData
{
	public String name, uuid, expiresOn;

	public Date getExpiresDate()
	{
		try
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(expiresOn);
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		return new Date(); // When we failed to parse the date we return the current time stamp
	}

	public UUID getUUID()
	{
		return UUID.fromString(uuid.replaceAll(MojangUuidResolver.UUID_FORMAT_REGEX, MojangUuidResolver.UUID_FORMAT_REPLACE_TO));
	}
}