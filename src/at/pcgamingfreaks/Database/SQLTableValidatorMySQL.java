/*
 *   Copyright (C) 2018 GeorgH93
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

package at.pcgamingfreaks.Database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLTableValidatorMySQL extends SQLTableValidator
{
	@Override
	protected String getCurrentCreateStatement(@NotNull Connection connection, @NotNull String tableName) throws SQLException
	{
		try(PreparedStatement preparedStatement = connection.prepareStatement("SHOW CREATE TABLE %;"))
		{
			preparedStatement.setString(1, tableName);
			try(ResultSet tableExists = preparedStatement.executeQuery())
			{
				if(tableExists.next())
				{
					return tableExists.getString(2);
				}
			}
		}
		return "";
	}
}