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

package at.pcgamingfreaks.TestClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A simple log handler that captures log records for testing purposes.
 * This allows tests to use real Logger instances while still being able
 * to verify logging behavior.
 */
public class LogCapture extends Handler
{
	private final List<LogRecord> records = new ArrayList<>();
	
	@Override
	public void publish(LogRecord record)
	{
		records.add(record);
	}
	
	@Override
	public void flush() { }
	
	@Override
	public void close() throws SecurityException
	{
		records.clear();
	}
	
	public List<LogRecord> getRecords()
	{
		return records;
	}
	
	public int getRecordCount()
	{
		return records.size();
	}
	
	public int getRecordCountByLevel(Level level)
	{
		int count = 0;
		for (LogRecord record : records)
		{
			if (record.getLevel().equals(level))
			{
				count++;
			}
		}
		return count;
	}
	
	public boolean hasRecordWithMessage(String substring)
	{
		for (LogRecord record : records)
		{
			if (record.getMessage() != null && record.getMessage().contains(substring))
			{
				return true;
			}
		}
		return false;
	}
	
	public void clear()
	{
		records.clear();
	}
	
	public static Logger createTestLogger(String name, LogCapture capture)
	{
		Logger logger = Logger.getLogger(name);
		logger.setUseParentHandlers(false);
		logger.addHandler(capture);
		logger.setLevel(Level.ALL);
		return logger;
	}
}
