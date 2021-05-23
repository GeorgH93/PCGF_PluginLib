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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLTableValidator extends SQLTableValidator
{
	@Override
	protected String getCurrentCreateStatement(@NotNull Connection connection, @NotNull String tableName) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			try(ResultSet tableExists = statement.executeQuery("SHOW CREATE TABLE `" + tableName + "`"))
			{
				if(tableExists.next())
				{
					return tableExists.getString(2);
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
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE `" + tableName + "` MODIFY COLUMN `" + columnName + "` " + columnDefinition);
		}
	}

	@Override
	protected void addIndex(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE " + tableName + " ADD INDEX " + (columnName != null && columnName.length() > 0 ? "`" + columnName + "` " : "") + "(" + indexString + ")");
		}
	}

	@Override
	protected void modifyIndex(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE " + tableName + " DROP INDEX `" + columnName + "`, ADD INDEX `" + columnName + "` (" + indexString + ")");
		}
	}

	@Override
	protected void makeIndexUnique(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE " + tableName + " DROP INDEX `" + columnName + "`, ADD UNIQUE INDEX `" + columnName + "` (" + indexString + ")");
		}
	}

	@Override
	protected void addUniqueIndex(@NotNull Connection connection, String tableName, @Nullable String columnName, String indexString) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE " + tableName + " ADD UNIQUE INDEX " + (columnName == null ? "" : "`" + columnName + "` ") + "(" + indexString + ")");
		}
	}

	@Override
	protected void addConstraint(@NotNull Connection connection, String tableName, String columnName, String foreignKey, String references) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE " + tableName + " ADD CONSTRAINT " + (columnName == null ? "" : "`" + columnName + "` ") + "FOREIGN KEY (" + foreignKey + ") REFERENCES " + references);
		}
	}

	@Override
	protected void modifyConstraint(@NotNull Connection connection, String tableName, String columnName, String foreignKey, String references) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE " + tableName + " DROP FOREIGN KEY `" + columnName + "`");
		}
		addConstraint(connection, tableName, columnName, foreignKey, references);
	}

	@Override
	protected void addPrimaryKey(@NotNull Connection connection, String tableName, String primaryKey) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE " + tableName + " ADD PRIMARY KEY " + primaryKey);
		}
	}

	@Override
	protected void modifyPrimaryKey(@NotNull Connection connection, String tableName, String primaryKey) throws SQLException
	{
		try(Statement statement = connection.createStatement())
		{
			statement.executeUpdate("ALTER TABLE " + tableName + " DROP PRIMARY KEY, ADD PRIMARY KEY " + primaryKey);
		}
	}
}