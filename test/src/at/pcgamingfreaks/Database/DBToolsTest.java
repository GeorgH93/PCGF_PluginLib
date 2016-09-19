/*
 * Copyright (C) 2016 MarkusWME
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

package at.pcgamingfreaks.Database;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DBToolsTest
{
	private static Connection mockedConnection;
	private static Statement mockedStatement;
	private static ResultSet mockedResultSet;

	@BeforeClass
	public static void prepareTestData()
	{
		new DBTools();
	}

	@Before
	public void prepareTestObjects() throws SQLException
	{
		mockedStatement = mock(Statement.class);
		mockedConnection = mock(Connection.class);
		mockedResultSet = mock(ResultSet.class);
		when(mockedResultSet.next()).thenReturn(true);
		when(mockedConnection.createStatement()).thenReturn(mockedStatement);
	}

	@Test
	public void testCreateTable() throws SQLException
	{
		String createTable = "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `value` INT(10) NOT NULL DEFAULT '0',\n" +
				"  PRIMARY KEY (`id`)\n" +
				")";
		when(mockedStatement.executeQuery(anyString())).thenThrow(new SQLException());
		DBTools.updateDB(mockedConnection, createTable);
		verify(mockedStatement, times(1)).executeUpdate(createTable);
	}

	@Test
	public void testAddRow() throws SQLException
	{
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  `value` INT(10) NOT NULL DEFAULT '0',\n" +
				                                              "  PRIMARY KEY (`id`)\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `value` INT(10) NOT NULL DEFAULT '0',\n" +
				"  `name` VARCHAR(100) NOT NULL DEFAULT 'Test',\n" +
				"  PRIMARY KEY (`id`)\n" +
				")");
		verify(mockedStatement, times(1)).executeUpdate(anyString());
	}

	@Test
	public void testUpdateRow() throws SQLException
	{
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(5) UNSIGNED NOT NULL PRIMARY KEY\n" +
				")");
		verify(mockedStatement, times(1)).executeUpdate(anyString());
	}

	@Test
	public void testAddPrimaryKey() throws SQLException
	{
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  `id2` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  UNIQUE INDEX `id_UNIQUE` (`id`)\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  PRIMARY KEY (`id`)\n" +
				")");
		verify(mockedStatement, times(1)).executeUpdate(contains("ALTER TABLE"));
	}

	@Test
	public void testUpdatePrimaryKey() throws SQLException
	{
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  `id2` INT(10) UNSIGNED NOT NULL,\n" +
				                                              "  PRIMARY KEY (`id`)\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `id2` INT(10) UNSIGNED NOT NULL,\n" +
				"  PRIMARY KEY (`id`, `id2`)\n" +
				")");
		verify(mockedStatement, times(1)).executeUpdate(contains("DROP PRIMARY KEY"));
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  `id2` INT(10) UNSIGNED NOT NULL,\n" +
				                                              "  PRIMARY KEY (`id`)\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `id2` INT(10) UNSIGNED NOT NULL,\n" +
				"  PRIMARY KEY (`id2`)\n" +
				")");
		verify(mockedStatement, times(2)).executeUpdate(contains("DROP PRIMARY KEY"));
	}

	@Test
	public void testAddUniqueIndex() throws SQLException
	{
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  `id2` INT(10) UNSIGNED NOT NULL,\n" +
				                                              "  `id3` INT(10) UNSIGNED NOT NULL\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `id2` INT(10) UNSIGNED NOT NULL,\n" +
				"  `id3` INT(10) UNSIGNED NOT NULL,\n" +
				"  CONSTRAINT UNIQUE KEY `id_UNIQUE` (`id`),\n" +
				"  UNIQUE INDEX (`id2`),\n" +
				"  CONSTRAINT id3_UNIQUE UNIQUE INDEX (id3)\n" +
				")");
		verify(mockedStatement, times(3)).executeUpdate(contains("ADD UNIQUE INDEX"));
	}

	@Test
	public void testUpdateUniqueIndex() throws SQLException
	{
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  `id2` INT(10) UNSIGNED NOT NULL,\n" +
				                                              "  `id3` INT(10) UNSIGNED NOT NULL,\n" +
				                                              "  `id5` INT(10) UNSIGNED NOT NULL,\n" +
				                                              "  UNIQUE KEY `new_id_UNIQUE` (`id`),\n" +
				                                              "  CONSTRAINT UNIQUE INDEX (`id2`, `id3`),\n" +
				                                              "  CONSTRAINT `constraint` UNIQUE INDEX `idx` (`id2`)\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `id2` INT(10) UNSIGNED NOT NULL,\n" +
				"  `id4` INT(10) UNSIGNED NOT NULL,\n" +
				"  CONSTRAINT UNIQUE KEY `id_UNIQUE` (`id`),\n" +
				"  UNIQUE INDEX (`id2`),\n" +
				"  CONSTRAINT `constraint` UNIQUE INDEX `idx` (`id4`),\n" +
				"  CONSTRAINT `constraint` UNIQUE INDEX `idx` (`id4`),\n" +
				"  CONSTRAINT id3_UNIQUE UNIQUE KEY (id2, id4)\n" +
				")");
		verify(mockedStatement, times(3)).executeUpdate(contains("DROP INDEX"));
	}

	@Test
	public void testAddForeignKey() throws SQLException
	{
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  `id2` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `id2` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT `fk_reference` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
				"  CONSTRAINT FOREIGN KEY (`id2`) REFERENCES `table2` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
				"  FOREIGN KEY (`id2`) REFERENCES `table2` (`id`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
				")");
		verify(mockedStatement, times(3)).executeUpdate(contains("ADD CONSTRAINT"));
	}

	@Test
	public void testUpdateForeignKey() throws SQLException
	{
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  `id2` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				                                              "  CONSTRAINT `fk_reference` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
				                                              "  CONSTRAINT `fk_ref` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
				                                              "  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `id2` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT `fk_reference` FOREIGN KEY (`id2`) REFERENCES `table2` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
				"  CONSTRAINT FOREIGN KEY (`id2`) REFERENCES `table2` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
				"  FOREIGN KEY (`id2`) REFERENCES `table2` (`id`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
				")");
		verify(mockedStatement, times(1)).executeUpdate(contains("DROP FOREIGN KEY"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateDBWithInvalidQuery() throws SQLException
	{
		DBTools.updateDB(null, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddUniqueIndexWithError() throws SQLException
	{
		//noinspection SpellCheckingInspection
		when(mockedResultSet.getString(2)).thenReturn("CREATE TABLE `table1` (\n" +
				                                              "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				                                              ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		when(mockedStatement.executeQuery(anyString())).thenReturn(mockedResultSet);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `table1` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  UNIQUE KEY `` (`id`)\n" +
				")");
		verify(mockedStatement, times(2)).executeUpdate(contains("ADD UNIQUE INDEX"));
	}
}