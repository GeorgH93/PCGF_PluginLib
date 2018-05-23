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

import java.sql.*;

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
		doReturn(true).when(mockedResultSet).next();
		doReturn(mockedStatement).when(mockedConnection).createStatement();
		doReturn(mockedResultSet).when(mockedStatement).executeQuery(anyString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidTableDefinition() throws SQLException
	{
		DBTools.updateDB(mockedConnection, "");
	}

	@Test
	public void testCreateWhenNoTableExists() throws SQLException
	{
		doThrow(new SQLException()).when(mockedStatement).executeQuery(anyString());
		String tableDefinition = "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  PRIMARY KEY (`id`)\n" +
				")";
		DBTools.updateDB(mockedConnection, tableDefinition);
		verify(mockedStatement, times(1)).executeUpdate(tableDefinition);
	}

	@Test
	public void testAddPrimaryKey() throws SQLException
	{
		int addCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  PRIMARY KEY (`id`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD PRIMARY KEY (`id`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT PRIMARY KEY (`id`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD PRIMARY KEY (`id`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT `prim_key` PRIMARY KEY (`id`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD PRIMARY KEY (`id`)"));
	}

	@Test
	public void testUpdatePrimaryKey() throws SQLException
	{
		int updateCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  `usr` INT(10) UNSIGNED NOT NULL,\n" +
				         "  UNIQUE INDEX (`usr`),\n" +
				         "  PRIMARY KEY (`id`)\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  PRIMARY KEY (`id`)\n" +
				")");
		verify(mockedStatement, times(0)).executeUpdate(anyString());
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  PRIMARY KEY (`val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP PRIMARY KEY, ADD PRIMARY KEY (`val`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT PRIMARY KEY (`val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP PRIMARY KEY, ADD PRIMARY KEY (`val`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `prim_key` PRIMARY KEY (`val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP PRIMARY KEY, ADD PRIMARY KEY (`val`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  PRIMARY KEY (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(1)).executeUpdate(contains("DROP PRIMARY KEY, ADD PRIMARY KEY (`id`, `val`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  PRIMARY KEY (`val`, `id`)\n" +
				")");
		verify(mockedStatement, times(1)).executeUpdate(contains("DROP PRIMARY KEY, ADD PRIMARY KEY (`val`, `id`)"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidUniqueIndex() throws SQLException
	{
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  UNIQUE KEY id\n" +
				")");
	}

	@Test
	public void testAddUniqueIndex() throws SQLException
	{
		int addCalls = 0;
		int addWithNameCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  PRIMARY KEY (`id`),\n" +
				         "  CONSTRAINT UNIQUE INDEX `not_used` (`val`),\n" +
				         "  CONSTRAINT `test` UNIQUE KEY (``),\n" +
				         "  CONSTRAINT UNIQUE KEY (`val`)\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  UNIQUE INDEX (`id`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD UNIQUE INDEX (`id`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  UNIQUE KEY (`id`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD UNIQUE INDEX (`id`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  UNIQUE INDEX `idx` (`id`)\n" +
				")");
		verify(mockedStatement, times(++addWithNameCalls)).executeUpdate(contains("ADD UNIQUE INDEX `idx` (`id`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  UNIQUE KEY `idx` (`id`)\n" +
				")");
		verify(mockedStatement, times(++addWithNameCalls)).executeUpdate(contains("ADD UNIQUE INDEX `idx` (`id`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT UNIQUE INDEX (`id`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD UNIQUE INDEX (`id`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT `idx` UNIQUE INDEX (`id`)\n" +
				")");
		verify(mockedStatement, times(++addWithNameCalls)).executeUpdate(contains("ADD UNIQUE INDEX `idx` (`id`)"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT `idx` UNIQUE INDEX `key` (`id`)\n" +
				")");
		verify(mockedStatement, times(++addWithNameCalls)).executeUpdate(contains("ADD UNIQUE INDEX `idx` (`id`)"));
		int addUniqueKey = addCalls + addWithNameCalls;
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT UNIQUE INDEX `test_name` (`id`)\n" +
				")");
		verify(mockedStatement, times(++addUniqueKey)).executeUpdate(contains("ADD UNIQUE INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT UNIQUE KEY `test_name` (`id`)\n" +
				")");
		verify(mockedStatement, times(++addUniqueKey)).executeUpdate(contains("ADD UNIQUE INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT UNIQUE KEY (`id`)\n" +
				")");
		verify(mockedStatement, times(++addUniqueKey)).executeUpdate(contains("ADD UNIQUE INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT UNIQUE KEY (`val`, `id`)\n" +
				")");
		verify(mockedStatement, times(++addUniqueKey)).executeUpdate(contains("ADD UNIQUE INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT UNIQUE KEY (`val`)\n" +
				")");
		verify(mockedStatement, times(addUniqueKey)).executeUpdate(contains("ADD UNIQUE INDEX"));
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  CONSTRAINT UNIQUE INDEX `` (``),\n" +
				         "  CONSTRAINT `valid` UNIQUE INDEX (`id`)\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT UNIQUE KEY (`val`)\n" +
				")");
		verify(mockedStatement, times(++addUniqueKey)).executeUpdate(contains("ADD UNIQUE INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `new` UNIQUE KEY (`val`)\n" +
				")");
		verify(mockedStatement, times(++addUniqueKey)).executeUpdate(contains("ADD UNIQUE INDEX"));
	}

	@Test
	public void testUpdateUniqueIndex() throws SQLException
	{
		int updateCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  PRIMARY KEY (`id`),\n" +
				         "  CONSTRAINT `idx` UNIQUE INDEX (`id`),\n" +
				         "  CONSTRAINT `key` UNIQUE KEY (`id`),\n" +
				         "  CONSTRAINT test UNIQUE INDEX (`val`),\n" +
				         "  CONSTRAINT UNIQUE KEY `named` (`id`)\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `idx` UNIQUE INDEX (`val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT idx UNIQUE KEY (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `idx` UNIQUE INDEX `index` (`val`, `id`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT key UNIQUE INDEX (`val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `key` UNIQUE KEY `index` (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `key` UNIQUE INDEX (`val`, `id`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT UNIQUE INDEX `named` (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT UNIQUE KEY `named` (`val`, `id`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT UNIQUE KEY `named` (`id`)\n" +
				")");
		verify(mockedStatement, times(updateCalls)).executeUpdate(contains("DROP INDEX"));
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  CONSTRAINT UNIQUE INDEX (`val`),\n" +
				         "  PRIMARY KEY (`id`)\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `idx` UNIQUE INDEX (`id`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `idx` UNIQUE INDEX (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidForeignKey() throws SQLException
	{
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) `fk_id` REFERENCES `table2` (`id`) ON DELETE CASCADE\n" +
				")");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidForeignKeyReference() throws SQLException
	{
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `table2` (`id`, `val`) ON DELETE CASCADE\n" +
				")");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidForeignKeyUpdateReference() throws SQLException
	{
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  CONSTRAINT `named` FOREIGN KEY (`id`) REFERENCES `table2` (`id`, `val`) ON DELETE CASCADE\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  CONSTRAINT `named` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE\n" +
				")");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidForeignKeyReferenceInDatabase() throws SQLException
	{
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `table2` (`id`, `val`)\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE\n" +
				")");
	}

	@Test
	public void testAddForeignKey() throws SQLException
	{
		int addCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  PRIMARY KEY (`id`),\n" +
				         "  CONSTRAINT FOREIGN KEY (``),\n" +
				         "  `usr` INT(10) UNSIGNED,\n" +
				         "  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`val`),\n" +
				         "  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test3` (`id`) ON UPDATE CASCADE,\n" +
				         "  CONSTRAINT `fk_single` FOREIGN KEY (`id`) REFERENCES `test2` (`id`),\n" +
				         "  CONSTRAINT `fk_multiple` FOREIGN KEY (`id`, `val`) REFERENCES `test2` (`id`, `val`) ON DELETE NO ACTION\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `fk_id` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY `fk_id` (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  FOREIGN KEY (`id`) REFERENCES `test2` (`id`)\n" +
				")");
		verify(mockedStatement, times(addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  FOREIGN KEY (`id`, `val`) REFERENCES `table2` (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  FOREIGN KEY (`id`, `val`) REFERENCES `test2` (`id`, `tree`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  FOREIGN KEY (`id`, `tree`) REFERENCES `test2` (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON UPDATE NO ACTION\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON DELETE CASCADE\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON DELETE NO ACTION\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON UPDATE NO ACTION\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON UPDATE CASCADE,\n" +
				         "  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test3` (`id`) ON DELETE NO ACTION\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON DELETE CASCADE\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`val`) REFERENCES `test2` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`val`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON UPDATE NO ACTION\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`val`) ON UPDATE CASCADE\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`val`) REFERENCES `test2` (`id`) ON UPDATE CASCADE\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`val`) REFERENCES `test2` (`val`) ON UPDATE CASCADE\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `` FOREIGN KEY (`val`) REFERENCES `test2` (`val`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON UPDATE CASCADE\n" +
				")");
		verify(mockedStatement, times(addCalls)).executeUpdate(contains("ADD CONSTRAINT"));
	}

	@Test
	public void testUpdateForeignKey() throws SQLException
	{
		int updateCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  PRIMARY KEY (`id`),\n" +
				         "  CONSTRAINT FOREIGN KEY (``),\n" +
				         "  CONSTRAINT FOREIGN KEY `key` (`id`) REFERENCES `test2` (`val`),\n" +
				         "  CONSTRAINT FOREIGN KEY key2 (`id`) REFERENCES `test2` (`val`),\n" +
				         "  CONSTRAINT `fk_single` FOREIGN KEY (`id`) REFERENCES `test2` (`id`),\n" +
				         "  CONSTRAINT fk_multiple FOREIGN KEY (`id`, `val`) REFERENCES `test2` (`id`, `val`) ON DELETE NO ACTION ON UPDATE CASCADE\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `fk_single` FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON DELETE CASCADE\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `fk_single` FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON UPDATE CASCADE\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `fk_single` FOREIGN KEY (`id`) REFERENCES `test2` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `fk_single` FOREIGN KEY (`id`) REFERENCES `test2` (`val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `fk_single` FOREIGN KEY (`val`) REFERENCES `test2` (`id`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `fk_single` FOREIGN KEY (`val`) REFERENCES `test2` (`val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `fk_single` FOREIGN KEY (`id`, `val`) REFERENCES `test2` (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT fk_multiple FOREIGN KEY (`id`, `val`) REFERENCES `test2` (`id`, `val`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT `fk_multiple` FOREIGN KEY (`id`, `val`) REFERENCES `test2` (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  CONSTRAINT fk_multiple FOREIGN KEY (`id`, `val`) REFERENCES `test2` (`id`, `val`) ON DELETE NO ACTION ON UPDATE CASCADE\n" +
				")");
		verify(mockedStatement, times(updateCalls)).executeUpdate(contains("DROP FOREIGN KEY"));
	}

	@Test
	public void testAddIndex() throws SQLException
	{
		int addCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  INDEX (`val`)\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  INDEX `idx` (id)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  INDEX idx (`id`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  INDEX (`id`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  INDEX (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  INDEX (`val`)\n" +
				")");
		verify(mockedStatement, times(addCalls)).executeUpdate(contains("ADD INDEX"));
	}

	@Test
	public void testUpdateIndex() throws SQLException
	{
		int updateCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         "  `val` VARCHAR(255),\n" +
				         "  INDEX `idx` (`val`)\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  INDEX `idx` (`id`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  INDEX `idx` (`id`, `val`)\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("DROP INDEX"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255),\n" +
				"  INDEX `idx` (`val`)\n" +
				")");
		verify(mockedStatement, times(updateCalls)).executeUpdate(contains("DROP INDEX"));
	}

	@Test
	public void testAddColumn() throws SQLException
	{
		int addCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  val VARCHAR(255)\n" +
				")");
		verify(mockedStatement, times(++addCalls)).executeUpdate(contains("ADD COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `new`\n" +
				")");
		verify(mockedStatement, times(addCalls)).executeUpdate(contains("ADD COLUMN"));
	}

	@Test
	public void testUpdateColumn() throws SQLException
	{
		int updateCalls = 0;
		doReturn("CREATE TABLE `test` (\n" +
				         "  `idü*\\2`,\n" +
				         "  PRIMARY KEY(`id2`),\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL,\n" +
				         "  val VARCHAR(255),\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("MODIFY COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) AUTO_INCREMENT UNSIGNED NOT NULL\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("MODIFY COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(12) NOT NULL\n" +
				")");
		verify(mockedStatement, times(++updateCalls)).executeUpdate(contains("MODIFY COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id2`    \n" +
				")");
		verify(mockedStatement, times(updateCalls)).executeUpdate(contains("MODIFY COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `vü*al` INT(10) NOT NULL\n" +
				")");
		verify(mockedStatement, times(updateCalls)).executeUpdate(contains("MODIFY COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL\n" +
				")");
		verify(mockedStatement, times(updateCalls)).executeUpdate(contains("MODIFY COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) NOT NULL UNSIGNED\n" +
				")");
		verify(mockedStatement, times(updateCalls)).executeUpdate(contains("MODIFY COLUMN"));
	}

	@Test
	public void testDoNothing() throws SQLException
	{
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				         ")").when(mockedResultSet).getString(2);
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				")");
		verify(mockedStatement, times(0)).executeUpdate(contains("ADD COLUMN"));
		verify(mockedStatement, times(0)).executeUpdate(contains("MODIFY COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				")");
		verify(mockedStatement, times(0)).executeUpdate(contains("ADD COLUMN"));
		verify(mockedStatement, times(0)).executeUpdate(contains("MODIFY COLUMN"));
		DBTools.updateDB(mockedConnection, "CREATE TABLE `test` (\n" +
				"  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				")");
		verify(mockedStatement, times(0)).executeUpdate(contains("ADD COLUMN"));
		verify(mockedStatement, times(0)).executeUpdate(contains("MODIFY COLUMN"));
	}

	@Test
	public void testWithEngineParameter() throws SQLException
	{
		//noinspection SpellCheckingInspection
		doReturn("CREATE TABLE `test` (\n" +
				         "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				         ") ENGINE=InnoDB").when(mockedResultSet).getString(2);
		//noinspection SpellCheckingInspection
		String createStatement = "CREATE TABLE `test` (\n" +
				"  INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				") ENGINE=InnoDB;";
		DBTools.updateDB(mockedConnection, createStatement);
		verify(mockedStatement, times(0)).executeUpdate(createStatement);
		//noinspection SpellCheckingInspection
		createStatement = "CREATE TABLE `test` (\n" +
				"  INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n" +
				") engine=InnoDB;";
		DBTools.updateDB(mockedConnection, createStatement);
		verify(mockedStatement, times(0)).executeUpdate(createStatement);
		doThrow(new SQLException()).when(mockedStatement).executeQuery(anyString());
		DBTools.updateDB(mockedConnection, createStatement);
		verify(mockedStatement, times(1)).executeUpdate(createStatement);
	}

	@Test
	public void testRunStatementWithoutException() throws SQLException
	{
		PreparedStatement mockedPreparedStatement = mock(PreparedStatement.class);
		doReturn(mockedPreparedStatement).when(mockedConnection).prepareStatement(anyString());
		DBTools.runStatementWithoutException(mockedConnection, "SELECT * FROM table WHERE id = ? AND name = ?", 3, "TEST");
		verify(mockedPreparedStatement, times(1)).execute();
		DBTools.runStatementWithoutException(mockedConnection, "SELECT * FROM table WHERE id = ? AND name = ?", (Object) null);
		verify(mockedPreparedStatement, times(2)).execute();
		doThrow(new SQLException()).when(mockedConnection).prepareStatement(anyString());
		DBTools.runStatementWithoutException(mockedConnection, "SELECT * FROM table WHERE id = ? AND name = ?", 3, "TEST");
		verify(mockedPreparedStatement, times(2)).execute();
	}
}