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

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DBToolsTest
{
	@BeforeClass
	public static void prepareTestData()
	{
		new DBTools();
	}

	@Test
	public void testRunStatementWithoutException() throws SQLException
	{
		Connection mockedConnection = mock(Connection.class);
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