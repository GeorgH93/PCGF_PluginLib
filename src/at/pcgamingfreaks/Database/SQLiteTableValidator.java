/*
 *   Copyright (C) 2019 GeorgH93
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

import java.sql.*;

public class SQLiteTableValidator extends SQLTableValidator
{
	@Override
	protected String getCurrentCreateStatement(@NotNull Connection connection, @NotNull String tableName) throws SQLException
	{
		try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT sql FROM sqlite_master WHERE tbl_name=?;"))
		{
			preparedStatement.setString(1, tableName);
			try(ResultSet tableExists = preparedStatement.executeQuery())
			{
				if(tableExists.next())
				{
					return tableExists.getString("sql");
				}
			}
		}
		return "";
	}

	@Override
	protected void addColumn(Connection connection, String tableName, String columnName, String columnDefinition) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE `" + tableName + "` ADD COLUMN `" + columnName + "` " + columnDefinition);
		}
	}

	@Override
	protected void modifyColumn(Connection connection, String tableName, String columnName, String columnDefinition) throws SQLException
	{
		//TODO SQLite doesn't allow to modify columns, rename table, create new table, copy old data
	}

	@Override
	protected void addIndex(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException
	{
		//TODO https://www.sqlite.org/lang_createindex.html
	}

	@Override
	protected void modifyIndex(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException
	{
		//TODO
	}

	@Override
	protected void makeIndexUnique(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException
	{
		//TODO
	}

	@Override
	protected void addUniqueIndex(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException
	{
		//TODO
	}

	@Override
	protected void addConstraint(@NotNull Connection connection, String tableName, String columnName, String foreignKey, String references) throws SQLException
	{
		//TODO
	}

	@Override
	protected void modifyConstraint(@NotNull Connection connection, String tableName, String columnName, String foreignKey, String references) throws SQLException
	{
		//TODO
	}

	@Override
	protected void addPrimaryKey(@NotNull Connection connection, String tableName, String primaryKey) throws SQLException
	{
		//TODO
	}

	@Override
	protected void modifyPrimaryKey(@NotNull Connection connection, String tableName, String primaryKey) throws SQLException
	{
		//TODO
	}
}