/*
 *   Copyright (C) 2026 GeorgH93
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

import at.pcgamingfreaks.TestClasses.LogCapture;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the abstract {@link SQLTableValidator} base class.
 * <p>
 * Unlike {@link MySQLTableValidatorTest} (which verifies the generated SQL through mocked JDBC objects),
 * these tests use a recording stub implementation to verify which abstract operations the template
 * method {@link SQLTableValidator#validate(Connection, String)} dispatches, together with direct tests
 * for {@link SQLTableValidator#reformatTableDefinition(String)} and {@link SQLTableValidator#getCurrentTableColumns(String)}.
 * <p>
 * The JDBC {@link Connection} and {@link Statement} objects are minimal dynamic proxies, so these tests
 * do not require Mockito and also run on Java 16+ where mocking {@code java.sql.Connection} is not possible.
 */
public class SQLTableValidatorTest
{
	private static final String CURRENT_CREATE_STATEMENT_READ = "getCurrentCreateStatement:test";

	private RecordingTableValidator validator;
	private Connection connection;
	private List<String> executedStatements;

	@Before
	public void setUp()
	{
		executedStatements = new ArrayList<>();
		final Statement statement = (Statement) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Statement.class}, (proxy, method, args) -> {
			if("executeUpdate".equals(method.getName()) && args != null && args.length == 1)
			{
				executedStatements.add((String) args[0]);
			}
			return defaultValue(method.getReturnType());
		});
		connection = (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class}, (proxy, method, args) -> {
			if("createStatement".equals(method.getName()))
			{
				return statement;
			}
			return defaultValue(method.getReturnType());
		});
		validator = new RecordingTableValidator();
	}

	//region reformatTableDefinition tests
	@Test
	public void testReformatSingleLineQuery()
	{
		assertEquals("CREATE TABLE `test` (\n`id` INT NOT NULL,\n `val` VARCHAR(255)\n);",
				SQLTableValidator.reformatTableDefinition("CREATE TABLE `test` (`id` INT NOT NULL, `val` VARCHAR(255));"));
		assertEquals("CREATE TABLE `test` (\n`id` INT NOT NULL,\n `val` VARCHAR(255)\n);",
				SQLTableValidator.reformatTableDefinition("CREATE TABLE `test` (`id` INT NOT NULL, `val` VARCHAR(255))"));
	}

	@Test
	public void testReformatQueryKeepsCommasInTypes()
	{
		// Commas within column types (e.g. DECIMAL(p,s)) must not be turned into line breaks
		assertEquals("CREATE TABLE `test` (\n`amount` DECIMAL(10,2),\n `price` DECIMAL(1,1)\n);",
				SQLTableValidator.reformatTableDefinition("CREATE TABLE `test` (`amount` DECIMAL(10,2), `price` DECIMAL(1,1))"));
	}

	@Test
	public void testReformatQueryWithWindowsLineEndings()
	{
		assertEquals("CREATE TABLE `test` (\n`id` INT NOT NULL\n);",
				SQLTableValidator.reformatTableDefinition("CREATE TABLE `test` (\r\n  `id` INT NOT NULL\r\n);"));
	}

	@Test
	public void testReformatQueryWithEngineClause()
	{
		//noinspection SpellCheckingInspection
		assertEquals("CREATE TABLE `test` (\n`id` INT NOT NULL\n) ENGINE=InnoDB;",
				SQLTableValidator.reformatTableDefinition("CREATE TABLE `test` (`id` INT NOT NULL) ENGINE=InnoDB"));
	}

	@Test
	public void testReformatInvalidQueriesReturnNull()
	{
		assertNull(SQLTableValidator.reformatTableDefinition(""));
		assertNull(SQLTableValidator.reformatTableDefinition("DROP TABLE `test`;"));
		assertNull(SQLTableValidator.reformatTableDefinition("CREATE TABLE `test` (`id` INT NOT NULL"));
		assertNull(SQLTableValidator.reformatTableDefinition("SELECT (1)"));
	}
	//endregion

	//region getCurrentTableColumns tests
	@Test
	public void testGetCurrentTableColumns() throws SQLException
	{
		final List<String> columns = validator.getCurrentTableColumns("CREATE TABLE `test` (\n" +
				"  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
				"  `val` VARCHAR(255) DEFAULT 'abc',\n" +
				"  PRIMARY KEY (`id`),\n" +
				"  INDEX `idx` (`val`)\n" +
				")");
		assertEquals(Arrays.asList("`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT", "`val` VARCHAR(255) DEFAULT 'abc'", "PRIMARY KEY (`id`)", "INDEX `idx` (`val`)"), columns);
	}

	@Test
	public void testGetCurrentTableColumnsReformatsSingleLineQueries() throws SQLException
	{
		assertEquals(Arrays.asList("`id` INT NOT NULL", "`val` VARCHAR(255)"),
				validator.getCurrentTableColumns("CREATE TABLE `test` (`id` INT NOT NULL, `val` VARCHAR(255))"));
	}

	@Test(expected = SQLException.class)
	public void testGetCurrentTableColumnsThrowsOnInvalidStatement() throws SQLException
	{
		validator.getCurrentTableColumns("SHOW TABLES");
	}
	//endregion

	//region validate - table creation and input validation tests
	@Test
	public void testValidateCreatesTableWhenNotExisting() throws SQLException
	{
		validator.setTableExists(false);
		@Language("SQL") final String definition = tableDefinition("`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT", "PRIMARY KEY (`id`)");
		validator.validate(connection, definition);
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
		assertEquals(Arrays.asList(definition), executedStatements);
	}

	@Test
	public void testValidateCreatesTableWhenCurrentStatementIsEmpty() throws SQLException
	{
		validator.setCurrentCreateStatement("");
		@Language("SQL") final String definition = tableDefinition("`id` INT NOT NULL");
		validator.validate(connection, definition);
		assertEquals(Arrays.asList(definition), executedStatements);
	}

	@Test
	public void testValidateCreatesTableFromReformattedDefinition() throws SQLException
	{
		validator.setTableExists(false);
		// A single line definition is reformatted before it is executed
		validator.validate(connection, "CREATE TABLE `test` (`id` INT NOT NULL, PRIMARY KEY (`id`))");
		assertEquals(Arrays.asList("CREATE TABLE `test` (\n`id` INT NOT NULL,\n PRIMARY KEY (`id`)\n);"), executedStatements);
	}

	@Test
	public void testValidateExecutesTrimmedDefinition() throws SQLException
	{
		validator.setTableExists(false);
		@Language("SQL") final String definition = tableDefinition("`id` INT NOT NULL");
		validator.validate(connection, definition + "  \n");
		assertEquals(Arrays.asList(definition), executedStatements);
	}

	@Test
	public void testValidateThrowsOnInvalidDefinition()
	{
		assertValidateRejected("");
		assertValidateRejected("DROP TABLE `test`;");
		assertValidateRejected("CREATE TABLE (`id` INT)");
	}

	@Test
	public void testValidateSupportsLowercaseKeywordsAndPlainTableNames() throws SQLException
	{
		validator.setTableExists(false);
		@Language("SQL") final String definition = "create table if not exists test (\n  `id` INT NOT NULL\n)";
		validator.validate(connection, definition);
		assertEquals(Arrays.asList("getCurrentCreateStatement:test"), validator.getOperations());
		assertEquals(Arrays.asList(definition), executedStatements);
	}
	//endregion

	//region validate - no-op tests
	@Test
	public void testValidateNoChangesOnMatchingLayouts() throws SQLException
	{
		@Language("SQL") final String[] columns = {"`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT", "`val` VARCHAR(255)", "PRIMARY KEY (`id`)", "INDEX `idx` (`val`)", "UNIQUE INDEX `idx2` (`id`)", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`)"};
		validator.setCurrentCreateStatement(tableDefinition(columns));
		validator.validate(connection, tableDefinition(columns));
		// Lowercase keywords, different case in column definitions and reordered flags must not cause updates either
		validator.validate(connection, "create table if not exists `test` (\n" +
				"  `id` int(10) unsigned auto_increment not null,\n" +
				"  `val` VARCHAR(255),\n" +
				"  primary key (`id`),\n" +
				"  INDEX `idx` (`val`),\n" +
				"  UNIQUE INDEX `idx2` (`id`),\n" +
				"  CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`)\n" +
				")");
		// Multiple spaces are normalized before comparing, lines without a column definition (only a name) are ignored
		validator.validate(connection, tableDefinition("`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT", "`val` VARCHAR(255)", "PRIMARY KEY (`id`)", "INDEX `idx` (`val`)", "UNIQUE INDEX `idx2` (`id`)", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`)", "`new`"));
		validator.validate(connection, "CREATE TABLE `test` (\n  `id`   INT(10)  UNSIGNED NOT NULL   AUTO_INCREMENT,\n  `val` VARCHAR(255)\n)");
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, CURRENT_CREATE_STATEMENT_READ, CURRENT_CREATE_STATEMENT_READ, CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
		assertTrue(executedStatements.isEmpty());
	}

	@Test
	public void testValidateWithEngineAndCharsetOptions() throws SQLException
	{
		//noinspection SpellCheckingInspection
		@Language("SQL") final String definition = "CREATE TABLE `test` (\n" +
				"  `id` INT NOT NULL\n" +
				") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;";
		validator.setCurrentCreateStatement(definition);
		validator.validate(connection, definition);
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
		assertTrue(executedStatements.isEmpty());
	}
	//endregion

	//region validate - column tests
	@Test
	public void testValidateAddsColumn() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "`val` VARCHAR(255) NOT NULL DEFAULT 'x'"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addColumn:test:val:VARCHAR(255) NOT NULL DEFAULT 'x'"), validator.getOperations());
		assertTrue(executedStatements.isEmpty());
	}

	@Test
	public void testValidateAddsColumnWithPlainColumnName() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "val VARCHAR(255)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addColumn:test:val:VARCHAR(255)"), validator.getOperations());
	}

	@Test
	public void testValidateModifiesColumnFlags() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL AUTO_INCREMENT"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "modifyColumn:test:id:INT NOT NULL AUTO_INCREMENT"), validator.getOperations());
		// Flags present in the current layout but missing from the definition are ignored, removing a flag does not modify the column
		validator.getOperations().clear();
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL AUTO_INCREMENT"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
	}

	@Test
	public void testValidateModifiesColumnType() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` BIGINT NOT NULL"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "modifyColumn:test:id:BIGINT NOT NULL"), validator.getOperations());
	}

	@Test
	public void testValidateModifiesColumnSize() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT(10) NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT(12) NOT NULL"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "modifyColumn:test:id:INT(12) NOT NULL"), validator.getOperations());
	}
	//endregion

	//region validate - primary key tests
	@Test
	public void testValidateAddsPrimaryKey() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "PRIMARY KEY (`id`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addPrimaryKey:test:(`id`)"), validator.getOperations());
	}

	@Test
	public void testValidateKeepsMatchingPrimaryKey() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "PRIMARY KEY (`id`)"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "CONSTRAINT `prim_key` PRIMARY KEY (`id`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
	}

	@Test
	public void testValidateModifiesPrimaryKey() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "`val` INT NOT NULL", "PRIMARY KEY (`id`)"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "`val` INT NOT NULL", "PRIMARY KEY (`id`, `val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "modifyPrimaryKey:test:(`id`, `val`)"), validator.getOperations());
	}
	//endregion

	//region validate - index tests
	@Test
	public void testValidateAddsIndex() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "INDEX `idx` (`val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addIndex:test:idx:`val`"), validator.getOperations());
		// Unnamed indexes are dispatched with an empty name
		validator.getOperations().clear();
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "INDEX (`val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addIndex:test::`val`"), validator.getOperations());
	}

	@Test
	public void testValidateKeepsMatchingIndex() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "INDEX `idx` (`val`)"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "KEY `idx` (`val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
		// Unnamed indexes are matched by their columns
		validator.getOperations().clear();
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "INDEX (`val`)"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "KEY (`val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
	}

	@Test
	public void testValidateModifiesIndex() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "INDEX `idx` (`id`)"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "INDEX `idx` (`id`, `val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "modifyIndex:test:idx:`id`, `val`"), validator.getOperations());
	}
	//endregion

	//region validate - unique index tests
	@Test
	public void testValidateAddsUniqueIndex() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "UNIQUE INDEX `idx` (`val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addUniqueIndex:test:idx:`val`"), validator.getOperations());
		// Unnamed unique indexes are dispatched with a null name
		validator.getOperations().clear();
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "UNIQUE INDEX (`val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addUniqueIndex:test:null:`val`"), validator.getOperations());
	}

	@Test
	public void testValidateKeepsMatchingUniqueIndex() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "UNIQUE KEY `idx` (`val`)"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "UNIQUE INDEX `idx` (`val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
	}

	@Test
	public void testValidateModifiesUniqueIndex() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "UNIQUE INDEX `idx` (`id`)"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "UNIQUE KEY `idx` (`id`, `val`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "makeIndexUnique:test:idx:`id`, `val`"), validator.getOperations());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateThrowsOnInvalidUniqueIndex() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "UNIQUE KEY idx"));
	}
	//endregion

	//region validate - foreign key tests
	@Test
	public void testValidateAddsForeignKey() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addConstraint:test:fk:`id`:`table2` (`id`) ON DELETE CASCADE "), validator.getOperations());
		// Unnamed foreign keys are dispatched with a null name
		validator.getOperations().clear();
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "FOREIGN KEY (`id`) REFERENCES `table2` (`id`)"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addConstraint:test:null:`id`:`table2` (`id`) "), validator.getOperations());
	}

	@Test
	public void testValidateKeepsMatchingForeignKey() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE"));
		// Matching ON UPDATE options keep the constraint as well
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON UPDATE CASCADE"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON UPDATE CASCADE"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
	}

	@Test
	public void testValidateModifiesForeignKeyOnUpdateOption() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON UPDATE CASCADE"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON UPDATE NO ACTION"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "modifyConstraint:test:fk:`id`:`table2` (`id`) ON UPDATE NO ACTION"), validator.getOperations());
	}

	@Test
	public void testValidateIgnoresDefaultReferenceOptions() throws SQLException
	{
		// Adding ON DELETE RESTRICT or NO ACTION to an option-less constraint is not treated as a change, they are the default behavior
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`)"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE RESTRICT"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE NO ACTION"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, CURRENT_CREATE_STATEMENT_READ), validator.getOperations());
		// Every other option does modify the constraint
		validator.getOperations().clear();
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE SET NULL"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "modifyConstraint:test:fk:`id`:`table2` (`id`) ON DELETE SET NULL "), validator.getOperations());
	}

	@Test
	public void testValidateModifiesForeignKey() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`)"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "CONSTRAINT `fk` FOREIGN KEY (`id`) REFERENCES `table2` (`id`) ON DELETE CASCADE"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "modifyConstraint:test:fk:`id`:`table2` (`id`) ON DELETE CASCADE "), validator.getOperations());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateThrowsOnInvalidForeignKey() throws SQLException
	{
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.validate(connection, tableDefinition("`id` INT NOT NULL", "FOREIGN KEY (`id`) REFERENCES `table2` (`id`, `val`)"));
	}
	//endregion

	//region validate - error handling tests
	@Test
	public void testValidateLogsErrorAndRethrowsOnWriteFailure() throws SQLException
	{
		final LogCapture logCapture = new LogCapture();
		final Logger logger = LogCapture.createTestLogger("SQLTableValidatorTest", logCapture);
		final SQLException writeFailure = new SQLException("write failed");
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.setWriteFailure(writeFailure);
		try
		{
			validator.validate(connection, tableDefinition("`id` INT NOT NULL", "`val` INT NOT NULL"), logger);
			fail("Expected a SQLException to be thrown");
		}
		catch(SQLException caught)
		{
			assertSame(writeFailure, caught);
		}
		assertEquals(1, logCapture.getRecordCountByLevel(Level.SEVERE));
		assertTrue(logCapture.hasRecordWithMessage("Failed to update table definition for test"));
		assertEquals(Arrays.asList(CURRENT_CREATE_STATEMENT_READ, "addColumn:test:val:INT NOT NULL"), validator.getOperations());
	}

	@Test
	public void testValidateWithoutLoggerRethrowsOnWriteFailure() throws SQLException
	{
		final SQLException writeFailure = new SQLException("write failed");
		validator.setCurrentCreateStatement(tableDefinition("`id` INT NOT NULL"));
		validator.setWriteFailure(writeFailure);
		try
		{
			validator.validate(connection, tableDefinition("`id` INT NOT NULL", "`val` INT NOT NULL"));
			fail("Expected a SQLException to be thrown");
		}
		catch(SQLException caught)
		{
			assertSame(writeFailure, caught);
		}
	}
	//endregion

	//region test helpers
	private void assertValidateRejected(@Language("SQL") final String definition)
	{
		try
		{
			validator.validate(connection, definition);
			fail("Expected an IllegalArgumentException to be thrown for: " + definition);
		}
		catch(IllegalArgumentException ignored)
		{
		}
		catch(SQLException e)
		{
			fail("Expected an IllegalArgumentException to be thrown for: " + definition);
		}
	}

	private static String tableDefinition(final String... columns)
	{
		final StringBuilder builder = new StringBuilder("CREATE TABLE `test` (\n");
		for(int i = 0; i < columns.length; i++)
		{
			builder.append("  ").append(columns[i]);
			if(i < columns.length - 1) builder.append(',');
			builder.append('\n');
		}
		return builder.append(')').toString();
	}

	private static Object defaultValue(final Class<?> type)
	{
		if(!type.isPrimitive() || type == void.class) return null;
		if(type == boolean.class) return false;
		if(type == long.class) return 0L;
		if(type == float.class) return 0F;
		if(type == double.class) return 0D;
		if(type == byte.class) return (byte) 0;
		if(type == short.class) return (short) 0;
		if(type == char.class) return '\0';
		return 0;
	}
	//endregion

	/**
	 * A minimal recording implementation of the abstract methods of {@link SQLTableValidator}.
	 * Each call is recorded as {@code <method>:<tableName>:<args joined with ':'>} and can be asserted by the tests.
	 */
	private static class RecordingTableValidator extends SQLTableValidator
	{
		private final List<String> operations = new ArrayList<>();
		private String currentCreateStatement = "CREATE TABLE `test` (\n  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT\n)";
		private SQLException writeFailure;
		private boolean tableExists = true;

		private void setCurrentCreateStatement(@Language("SQL") final String currentCreateStatement)
		{
			this.currentCreateStatement = currentCreateStatement;
		}

		private void setTableExists(final boolean tableExists)
		{
			this.tableExists = tableExists;
		}

		private void setWriteFailure(final SQLException writeFailure)
		{
			this.writeFailure = writeFailure;
		}

		private List<String> getOperations()
		{
			return operations;
		}

		private void record(@NotNull final String entry) throws SQLException
		{
			operations.add(entry);
			if(writeFailure != null) throw writeFailure;
		}

		@Override
		protected String getCurrentCreateStatement(@NotNull final Connection connection, @NotNull final String tableName) throws SQLException
		{
			operations.add("getCurrentCreateStatement:" + tableName);
			if(!tableExists) throw new SQLException("Table '" + tableName + "' doesn't exist");
			return currentCreateStatement;
		}

		@Override
		protected void addColumn(final Connection connection, final String tableName, final String columnName, final String columnDefinition) throws SQLException
		{
			record("addColumn:" + tableName + ':' + columnName + ':' + columnDefinition);
		}

		@Override
		protected void modifyColumn(final Connection connection, final String tableName, final String columnName, final String columnDefinition) throws SQLException
		{
			record("modifyColumn:" + tableName + ':' + columnName + ':' + columnDefinition);
		}

		@Override
		protected void addIndex(@NotNull final Connection connection, final String tableName, final String columnName, final String indexString) throws SQLException
		{
			record("addIndex:" + tableName + ':' + columnName + ':' + indexString);
		}

		@Override
		protected void modifyIndex(@NotNull final Connection connection, final String tableName, final String columnName, final String indexString) throws SQLException
		{
			record("modifyIndex:" + tableName + ':' + columnName + ':' + indexString);
		}

		@Override
		protected void makeIndexUnique(@NotNull final Connection connection, final String tableName, final String columnName, final String indexString) throws SQLException
		{
			record("makeIndexUnique:" + tableName + ':' + columnName + ':' + indexString);
		}

		@Override
		protected void addUniqueIndex(@NotNull final Connection connection, final String tableName, @Nullable final String columnName, final String indexString) throws SQLException
		{
			record("addUniqueIndex:" + tableName + ':' + columnName + ':' + indexString);
		}

		@Override
		protected void addConstraint(@NotNull final Connection connection, final String tableName, final String columnName, final String foreignKey, final String references) throws SQLException
		{
			record("addConstraint:" + tableName + ':' + columnName + ':' + foreignKey + ':' + references);
		}

		@Override
		protected void modifyConstraint(@NotNull final Connection connection, final String tableName, final String columnName, final String foreignKey, final String references) throws SQLException
		{
			record("modifyConstraint:" + tableName + ':' + columnName + ':' + foreignKey + ':' + references);
		}

		@Override
		protected void addPrimaryKey(@NotNull final Connection connection, final String tableName, final String primaryKey) throws SQLException
		{
			record("addPrimaryKey:" + tableName + ':' + primaryKey);
		}

		@Override
		protected void modifyPrimaryKey(@NotNull final Connection connection, final String tableName, final String primaryKey) throws SQLException
		{
			record("modifyPrimaryKey:" + tableName + ':' + primaryKey);
		}
	}
}
