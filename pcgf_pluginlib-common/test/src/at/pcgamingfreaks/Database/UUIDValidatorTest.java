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
import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.UUID.UuidConverter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link UUIDValidator}.
 * <p>
 * Like {@link SQLTableValidatorTest}, the JDBC {@link Connection}, {@link Statement}, {@link ResultSet}
 * and {@link PreparedStatement} objects are minimal recording dynamic proxies, so these tests do not
 * require Mockito for JDBC types and also run on Java 16+ where mocking {@code java.sql.Connection}
 * is not possible. UUID conversion runs against the real {@link UuidConverter} in offline mode
 * ({@code useOnlineUUIDs=false}), which is deterministic ({@code UUID.nameUUIDFromBytes} of
 * {@code "OfflinePlayer:" + name}) and does not access the network. The online mode is not
 * intercepted here because construction mocking is unavailable on this JDK in this project.
 */
public class UUIDValidatorTest
{
	private static final String TABLE = "users", ID_COLUMN = "id", NAME_COLUMN = "name", UUID_COLUMN = "uuid";
	private static final String UUID_WITH_SEPARATORS    = "069a79f4-4c9a-4efc-a9b1-6ab5a2f1b1a9";
	private static final String UUID_WITHOUT_SEPARATORS = "069a79f44c9a4efca9b16ab5a2f1b1a9";

	private Connection connection;
	private final List<String> executedQueries = new ArrayList<>();
	private final List<PreparedStatementHandler> preparedStatements = new ArrayList<>();
	private final Set<String> existingUuidsInDb = new HashSet<>();
	private List<Map<String, Object>> collectRows = new ArrayList<>();
	private Map<String, Object> duplicateRow = new HashMap<>();
	private int batchFailuresRemaining = 0;

	@Before
	public void setUp()
	{
		executedQueries.clear();
		preparedStatements.clear();
		existingUuidsInDb.clear();
		collectRows = new ArrayList<>();
		duplicateRow = new HashMap<>();
		duplicateRow.put(NAME_COLUMN, "OtherUser");
		duplicateRow.put(ID_COLUMN, 42);
		batchFailuresRemaining = 0;
		connection = (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class}, (proxy, method, args) -> {
			if("createStatement".equals(method.getName()))
			{
				return createCollectStatement();
			}
			else if("prepareStatement".equals(method.getName()) && args != null)
			{
				PreparedStatementHandler handler = new PreparedStatementHandler((String) args[0]);
				preparedStatements.add(handler);
				return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PreparedStatement.class}, handler);
			}
			return defaultValue(method.getReturnType());
		});
	}

	//region validateUUIDs tests
	@Test
	public void testValidateWithoutRowsIsANoOp() throws SQLException
	{
		LogCapture logCapture = new LogCapture();
		newValidator(logCapture, true, false).validateUUIDs();
		assertEquals("The collect query should have been executed once", 1, executedQueries.size());
		assertTrue("No update statements should have been prepared", preparedStatements.isEmpty());
		assertEquals("Nothing should have been logged", 0, logCapture.getRecordCount());
	}

	@Test
	public void testCollectQueryReflectsSeparatorSetting() throws SQLException
	{
		newValidator(new LogCapture(), true, false).validateUUIDs();
		newValidator(new LogCapture(), false, false).validateUUIDs();
		assertEquals(2, executedQueries.size());
		String queryWithSeparators = executedQueries.get(0);
		assertTrue(queryWithSeparators.contains("SELECT `" + ID_COLUMN + "`,`" + NAME_COLUMN + "`,`" + UUID_COLUMN + "` FROM `" + TABLE + "`"));
		assertTrue(queryWithSeparators.contains("`" + UUID_COLUMN + "` IS NULL"));
		assertTrue(queryWithSeparators.contains("`" + UUID_COLUMN + "` NOT LIKE '%-%-%-%-%'"));
		String queryWithoutSeparators = executedQueries.get(1);
		assertTrue(queryWithoutSeparators.contains("`" + UUID_COLUMN + "` LIKE '%-%'"));
		assertFalse(queryWithoutSeparators.contains("NOT LIKE"));
	}

	@Test
	public void testFixesUuidsStoredWithSeparatorsWhenSeparatorsDisabled() throws SQLException
	{
		LogCapture logCapture = new LogCapture();
		collectRows.add(row(5, "LegacyUser", UUID_WITH_SEPARATORS));
		newValidator(logCapture, false, false).validateUUIDs();
		assertSingleUpdate(logCapture, UUID_WITHOUT_SEPARATORS, 5L);
	}

	@Test
	public void testKeepsUuidFormatWhenSeparatorsEnabled() throws SQLException
	{
		LogCapture logCapture = new LogCapture();
		collectRows.add(row(5, "LegacyUser", UUID_WITH_SEPARATORS));
		newValidator(logCapture, true, false).validateUUIDs();
		assertSingleUpdate(logCapture, UUID_WITH_SEPARATORS, 5L);
	}

	@Test
	public void testConvertsNamesToUuidsInOfflineMode() throws SQLException
	{
		LogCapture logCapture = new LogCapture();
		collectRows.add(row(1, "PlayerOne", null));
		collectRows.add(row(2, "PlayerTwo", null));
		newValidator(logCapture, true, false).validateUUIDs();

		assertTrue(logCapture.hasRecordWithMessage(UUIDValidator.MESSAGE_UPDATE_UUIDS));
		assertTrue(logCapture.hasRecordWithMessage(String.format(UUIDValidator.MESSAGE_UPDATED_UUIDS, 2)));
		assertEquals("Exactly the start and finished messages should have been logged", 2, logCapture.getRecordCountByLevel(Level.INFO));

		Map<Long, String> writtenUuids = writtenUuidsById();
		assertEquals(2, writtenUuids.size());
		assertEquals(offlineUuid("PlayerOne"), writtenUuids.get(1L));
		assertEquals(offlineUuid("PlayerTwo"), writtenUuids.get(2L));
	}

	@Test
	public void testConvertsNamesToUuidsToCompactFormat() throws SQLException
	{
		LogCapture logCapture = new LogCapture();
		collectRows.add(row(1, "PlayerOne", null));
		newValidator(logCapture, false, false).validateUUIDs();

		assertTrue(logCapture.hasRecordWithMessage(String.format(UUIDValidator.MESSAGE_UPDATED_UUIDS, 1)));
		Map<Long, String> writtenUuids = writtenUuidsById();
		assertEquals(1, writtenUuids.size());
		assertEquals(offlineUuid("PlayerOne").replace("-", ""), writtenUuids.get(1L));
	}

	@Test
	public void testMixedRowsAreFixedAndConvertedInOneRun() throws SQLException
	{
		LogCapture logCapture = new LogCapture();
		collectRows.add(row(1, "Alpha", null));
		collectRows.add(row(2, "Beta", UUID_WITH_SEPARATORS));
		newValidator(logCapture, true, false).validateUUIDs();

		assertTrue(logCapture.hasRecordWithMessage(String.format(UUIDValidator.MESSAGE_UPDATED_UUIDS, 2)));
		Map<Long, String> writtenUuids = writtenUuidsById();
		assertEquals(offlineUuid("Alpha"), writtenUuids.get(1L));
		assertEquals(UUID_WITH_SEPARATORS, writtenUuids.get(2L));
	}

	@Test
	public void testRemovesDuplicatedUuidsAndLogsWarning() throws SQLException
	{
		LogCapture logCapture = new LogCapture();
		collectRows.add(row(1, "DuplicatedUser", "badformat"));
		existingUuidsInDb.add("badformat");
		batchFailuresRemaining = 1;
		newValidator(logCapture, false, false).validateUUIDs();

		assertTrue(logCapture.hasRecordWithMessage("has the same UUID as"));
		assertTrue(logCapture.hasRecordWithMessage("DuplicatedUser"));
		assertTrue(logCapture.hasRecordWithMessage("OtherUser"));
		assertEquals(1, logCapture.getRecordCountByLevel(Level.WARNING));
		// The duplicated entry was removed before the retry, so no update was written anymore
		assertTrue(logCapture.hasRecordWithMessage(String.format(UUIDValidator.MESSAGE_UPDATED_UUIDS, 0)));

		List<PreparedStatementHandler> updateHandlers = updateHandlers();
		assertEquals("The update should have been prepared twice (initial try and retry)", 2, updateHandlers.size());
		assertEquals("The first try should have contained one batch before failing", 1, updateHandlers.get(0).getBatchCount());
		assertEquals("The retry should not contain any batches anymore", 0, updateHandlers.get(1).getBatchCount());
		assertEquals("The duplicate check should have queried the database once", 1, duplicateCheckHandlers().size());
	}
	//endregion

	//region writeUpdatedUuids / handleDuplicatedUuids tests
	@Test
	public void testWriteUpdatedUuidsRetriesAfterBatchFailure() throws Exception
	{
		LogCapture logCapture = new LogCapture();
		UUIDValidator validator = newValidator(logCapture, true, false);
		List<UpdateDataUUID> updates = new ArrayList<>();
		updates.add(new UpdateDataUUID("TestUser", UUID_WITH_SEPARATORS, 7L));
		TestUtils.initReflection();
		TestUtils.setAccessible(UUIDValidator.class, validator, "toUpdate", updates);
		batchFailuresRemaining = 1;

		validator.writeUpdatedUuids();

		List<PreparedStatementHandler> updateHandlers = updateHandlers();
		assertEquals("The update should have been prepared twice (failing try and successful retry)", 2, updateHandlers.size());
		PreparedStatementHandler retryHandler = updateHandlers.get(1);
		assertEquals(1, retryHandler.getBatchCount());
		assertEquals(UUID_WITH_SEPARATORS, retryHandler.getBatches().get(0).get(1));
		assertEquals(Long.valueOf(7L), retryHandler.getBatches().get(0).get(2));
		assertEquals("The duplicate check should have run after the failed batch", 1, duplicateCheckHandlers().size());
		assertFalse("No duplicate warning should have been logged", logCapture.hasRecordWithMessage("has the same UUID as"));
	}

	@Test
	public void testHandleDuplicatedUuidsRemovesOnlyMatchingEntries() throws Exception
	{
		LogCapture logCapture = new LogCapture();
		UUIDValidator validator = newValidator(logCapture, false, false);
		List<UpdateDataUUID> updates = new ArrayList<>();
		updates.add(new UpdateDataUUID("DuplicatedUser", UUID_WITHOUT_SEPARATORS, 1L));
		updates.add(new UpdateDataUUID("UniqueUser", UUID_WITHOUT_SEPARATORS.replace('0', '1'), 2L));
		TestUtils.initReflection();
		TestUtils.setAccessible(UUIDValidator.class, validator, "toUpdate", updates);
		existingUuidsInDb.add(UUID_WITHOUT_SEPARATORS);

		validator.handleDuplicatedUuids();

		assertEquals("Only the duplicated entry should remain removed", 1, updates.size());
		assertEquals("UniqueUser", updates.get(0).getName());
		assertTrue(logCapture.hasRecordWithMessage("has the same UUID as"));
		assertEquals(2, duplicateCheckHandlers().size());
	}
	//endregion

	//region helpers
	private UUIDValidator newValidator(LogCapture logCapture, boolean useUuidSeparators, boolean useOnlineUUIDs)
	{
		Logger logger = LogCapture.createTestLogger("UUIDValidatorTest", logCapture);
		return new UUIDValidator(logger, connection, TABLE, NAME_COLUMN, UUID_COLUMN, ID_COLUMN, useUuidSeparators, useOnlineUUIDs);
	}

	private static Map<String, Object> row(long id, String name, String uuid)
	{
		Map<String, Object> row = new HashMap<>();
		row.put(ID_COLUMN, id);
		row.put(NAME_COLUMN, name);
		row.put(UUID_COLUMN, uuid);
		return row;
	}

	private static String offlineUuid(String name)
	{
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)).toString();
	}

	private void assertSingleUpdate(LogCapture logCapture, String expectedUuid, long expectedId)
	{
		assertTrue(logCapture.hasRecordWithMessage(UUIDValidator.MESSAGE_UPDATE_UUIDS));
		assertTrue(logCapture.hasRecordWithMessage(String.format(UUIDValidator.MESSAGE_UPDATED_UUIDS, 1)));
		List<PreparedStatementHandler> updateHandlers = updateHandlers();
		assertEquals("Exactly one update statement should have been prepared", 1, updateHandlers.size());
		PreparedStatementHandler handler = updateHandlers.get(0);
		assertEquals(1, handler.getBatchCount());
		assertEquals(expectedUuid, handler.getBatches().get(0).get(1));
		assertEquals(Long.valueOf(expectedId), handler.getBatches().get(0).get(2));
	}

	private Map<Long, String> writtenUuidsById()
	{
		Map<Long, String> written = new HashMap<>();
		for(PreparedStatementHandler handler : updateHandlers())
		{
			for(Map<Integer, Object> batch : handler.getBatches())
			{
				assertNotNull(batch.get(2));
				written.put((Long) batch.get(2), (String) batch.get(1));
			}
		}
		return written;
	}

	private List<PreparedStatementHandler> updateHandlers()
	{
		return handlersFor("UPDATE");
	}

	private List<PreparedStatementHandler> duplicateCheckHandlers()
	{
		return handlersFor("SELECT");
	}

	private List<PreparedStatementHandler> handlersFor(String sqlPrefix)
	{
		List<PreparedStatementHandler> handlers = new ArrayList<>();
		for(PreparedStatementHandler handler : preparedStatements)
		{
			if(handler.getSql().startsWith(sqlPrefix)) handlers.add(handler);
		}
		return handlers;
	}

	private Statement createCollectStatement()
	{
		return (Statement) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Statement.class}, (proxy, method, args) -> {
			if("executeQuery".equals(method.getName()) && args != null && args.length == 1)
			{
				executedQueries.add((String) args[0]);
				return createResultSet(collectRows);
			}
			return defaultValue(method.getReturnType());
		});
	}

	private ResultSet createResultSet(final List<Map<String, Object>> rows)
	{
		final int[] cursor = {-1};
		return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ResultSet.class}, (proxy, method, args) -> {
			switch(method.getName())
			{
				case "next":
					cursor[0]++;
					return cursor[0] < rows.size();
				case "isFirst":
					return cursor[0] == 0;
				case "getString":
					return rows.get(cursor[0]).get((String) args[0]);
				case "getInt":
					Object value = rows.get(cursor[0]).get((String) args[0]);
					return (value == null) ? 0 : ((Number) value).intValue();
				default:
					return defaultValue(method.getReturnType());
			}
		});
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
	 * Recording invocation handler for {@link PreparedStatement} proxies.
	 * Records bound parameters ({@code setString}/{@code setLong}/{@code setInt}/{@code setObject})
	 * and batches added via {@code addBatch}. {@code executeBatch} can be configured to fail a
	 * certain number of times (used to drive the retry logic in {@link UUIDValidator#writeUpdatedUuids()}),
	 * {@code executeQuery} answers the duplicate check based on the UUIDs registered in
	 * {@code existingUuidsInDb}.
	 */
	private final class PreparedStatementHandler implements InvocationHandler
	{
		private final String sql;
		private final Map<Integer, Object> parameters = new HashMap<>();
		private final List<Map<Integer, Object>> batches = new ArrayList<>();

		private PreparedStatementHandler(String sql)
		{
			this.sql = sql;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			switch(method.getName())
			{
				case "setString":
				case "setLong":
				case "setInt":
				case "setObject":
					parameters.put((Integer) args[0], args[1]);
					return null;
				case "addBatch":
					batches.add(new HashMap<>(parameters));
					return null;
				case "executeBatch":
					if(batchFailuresRemaining > 0)
					{
						batchFailuresRemaining--;
						throw new SQLException("Simulated batch failure");
					}
					return new int[batches.size()];
				case "executeQuery":
					Object uuidParameter = parameters.get(1);
					boolean duplicate = uuidParameter != null && existingUuidsInDb.contains(uuidParameter);
					return createResultSet(duplicate ? Collections.singletonList(duplicateRow) : Collections.<Map<String, Object>>emptyList());
				default:
					return defaultValue(method.getReturnType());
			}
		}

		private String getSql()
		{
			return sql;
		}

		private List<Map<Integer, Object>> getBatches()
		{
			return batches;
		}

		private int getBatchCount()
		{
			return batches.size();
		}
	}
}
