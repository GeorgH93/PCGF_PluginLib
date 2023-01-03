/*
 *   Copyright (C) 2023 GeorgH93
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

package at.pcgamingfreaks.Database;

import at.pcgamingfreaks.UUID.UuidConverter;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

@AllArgsConstructor
class UUIDValidator
{
	public static final String MESSAGE_UPDATE_UUIDS = "Start updating database to UUIDs ...", MESSAGE_UPDATED_UUIDS = "Updated %d accounts to UUIDs.";

	private final @NotNull Logger logger;
	private final @NotNull Connection connection;
	private final @NotNull String tableName, nameColumnName, uuidColumnName, idColumnName;
	private final boolean useUuidSeparators, useOnlineUUIDs;

	private final Map<String, UpdateDataUUID> toConvert = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private final List<UpdateDataUUID> toUpdate = new ArrayList<>();

	private void collectUuidsThatNeedFixing() throws SQLException
	{
		toConvert.clear();
		toUpdate.clear();


		@Language("SQL") String queryGetUnsetOrInvalidUUIDs = "SELECT `" + idColumnName + "`,`" + nameColumnName + "`,`" + uuidColumnName + "` FROM `" + tableName +
				"` WHERE `" + uuidColumnName + "` IS NULL OR `" + uuidColumnName + "` " + ((useUuidSeparators) ? "NOT LIKE '%-%-%-%-%'" : "LIKE '%-%'") + ";";

		try(Statement stmt = connection.createStatement(); ResultSet res = stmt.executeQuery(queryGetUnsetOrInvalidUUIDs))
		{
			while(res.next())
			{
				if(res.isFirst())
				{
					logger.info(MESSAGE_UPDATE_UUIDS);
				}
				String uuid = res.getString(uuidColumnName);
				if(uuid == null)
				{
					toConvert.put(res.getString(nameColumnName), new UpdateDataUUID(res.getString(nameColumnName), null, res.getInt(idColumnName)));
				}
				else
				{
					toUpdate.add(new UpdateDataUUID(res.getString(nameColumnName), uuid, res.getInt(idColumnName)));
				}
			}
		}
	}

	void convertNamesToUuids()
	{
		if(!toConvert.isEmpty())
		{
			UuidConverter converter = new UuidConverter(logger);

			Map<String, UUID> newUUIDs = converter.getUUIDs(toConvert.keySet(), true, useOnlineUUIDs);
			for(Map.Entry<String, UUID> entry : newUUIDs.entrySet())
			{
				UpdateDataUUID updateData = toConvert.get(entry.getKey());
				updateData.setUuid((useUuidSeparators) ? entry.getValue().toString() : entry.getValue().toString().replace("-", ""));
				toUpdate.add(updateData);
			}
		}
	}

	void writeUpdatedUuids() throws SQLException
	{
		@Language("SQL") String queryFixUUIDs = "UPDATE `" + tableName + "` SET `" + uuidColumnName + "`=? WHERE `" + idColumnName + "`=?;";

		boolean ok = false;
		do
		{
			try(PreparedStatement ps = connection.prepareStatement(queryFixUUIDs))
			{
				for(UpdateDataUUID updateData : toUpdate)
				{
					DBTools.setParameters(ps, updateData.formatUuid(useUuidSeparators), updateData.getId());
					ps.addBatch();
				}
				ps.executeBatch();
				ok = true;
			}
			catch(SQLException ignored)
			{
				handleDuplicatedUuids();
			}
		} while(!ok);
	}

	void handleDuplicatedUuids() throws SQLException
	{
		@Language("SQL") String queryLoadPlayer = "SELECT * FROM `" + tableName + "` WHERE `" + uuidColumnName + "`=?;";

		Iterator<UpdateDataUUID> updateUUIDsHelperIterator = toUpdate.iterator();
		while(updateUUIDsHelperIterator.hasNext())
		{
			UpdateDataUUID updateData = updateUUIDsHelperIterator.next();
			try(PreparedStatement ps = connection.prepareStatement(queryLoadPlayer))
			{
				ps.setString(1, updateData.formatUuid(useUuidSeparators));
				try(ResultSet rs = ps.executeQuery())
				{
					if(rs.next())
					{
						logger.warning("User " + updateData.getName() + " (db id: " + updateData.getId() + ") has the same UUID as " + rs.getString(nameColumnName) +
								               " (db id: " + rs.getInt(idColumnName) + "), UUID: " + updateData.getUuid());
						updateUUIDsHelperIterator.remove();
					}
				}
			}
		}
	}

	public void validateUUIDs() throws SQLException
	{
		collectUuidsThatNeedFixing();

		if(!toConvert.isEmpty() || !toUpdate.isEmpty())
		{
			convertNamesToUuids();

			writeUpdatedUuids();

			logger.info(String.format(MESSAGE_UPDATED_UUIDS, toUpdate.size()));
		}
	}
}