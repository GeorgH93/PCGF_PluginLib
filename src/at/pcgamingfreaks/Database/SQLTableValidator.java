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

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is not thread save! Use one instance with one thread only!
 */
public abstract class SQLTableValidator
{
	//TODO use named regex groups where possible, cleanup/refactor code, make more generic, test test test

	private static final Pattern CURRENT_TABLE_INFO = Pattern.compile("^\\w*(CREATE TABLE)( IF NOT EXISTS)?\\s+(`(?<tableNameEsc>\\w+)`|(?<tableName>\\w+))\\s+\\(\\n(?<columns>[\\s\\S]*)\\n\\)(?<engine>\\s+ENGINE=\\w+)?(\\s+AUTO_INCREMENT=\\d+)?(\\s+DEFAULT\\s+CHARSET=(?<charset>\\w+))?;?$", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_NAME_EXTRACTOR_PATTERN = Pattern.compile("^(`([^`]+)`|\\w+) (.*)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_CONSTRAINT_CHECKER_PATTERN = Pattern.compile("^(CONSTRAINT\\s*(`(\\w*)`|\\w*)\\s+)?(PRIMARY KEY|UNIQUE KEY|UNIQUE INDEX|FOREIGN KEY)\\s+(.*)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_KEY_CHECKER_PATTERN = Pattern.compile("^(INDEX|KEY)\\s+(`(\\w*)`|(\\w*))?\\s?\\(((`\\w*`|\\w*)(,\\s*(`\\w*`|\\w*))*)\\)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_TYPE_EXTRACTOR_PATTERN = Pattern.compile("^(\\w*(\\(\\d+(,\\d+)?\\))?)\\s+(.*)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern UNIQUE_INDEX_PATTERN = Pattern.compile("^(`(\\w+)`|\\w+)?\\s*\\((.*)\\)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern FOREIGN_KEY_PATTERN = Pattern.compile("^(`(\\w+)`|\\w+)?\\s*\\(([^)]*)\\)\\s+REFERENCES\\s+(`\\w+`|\\w+)\\s+\\(([^)]*)\\)\\s*(ON DELETE (RESTRICT|CASCADE|SET NULL|NO ACTION))?\\s*(ON UPDATE (RESTRICT|CASCADE|SET NULL|NO ACTION))?$", Pattern.CASE_INSENSITIVE);

	/**
	 * Updates the database so that the given table exists and matches the schema after using this function
	 * <b>Important:</b> Currently only tested and optimised for MySQL! No warranty that it works with SQL databases other than MySQL.
	 *
	 * @param connection The JDBC database connection
	 * @param tableDefinition A MySQL create query using the following style guidelines:
	 *                        1.) One create query per function call
	 *                        2.) Using correct basic create query syntax (syntax: CREATE TABLE [IF NOT EXISTS] tbl_name (\n create_definition,... \n);)
	 *                        3.) A create_definition can contain following:
	 *                        3.1) col_name column_definition
	 *                        3.2) [CONSTRAINT [symbol]] PRIMARY KEY (index_col_name,...)
	 *                        3.3) [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY] [index_name] (index_col_name,...)
	 *                        3.4) [CONSTRAINT [symbol]] FOREIGN KEY [index_name] (index_col_name,...) reference_definition
	 *                        3.4.1) A reference_definition contains: REFERENCES tbl_name (index_col_name,...) [MATCH FULL|MATCH PARTIAL|MATCH SIMPLE] [ON DELETE reference_option] [ON UPDATE reference_option]
	 *                        3.4.2) A reference_option is one of the following options: RESTRICT, CASCADE, SET NULL, NO ACTION
	 *                        3.5) {INDEX|KEY} [index_name] (index_col_name,...)
	 *                        4.) Write PRIMARY KEY and others like CONSTRAINT in a new line, don't add it to the definition of the column (for example "`column` INT, PRIMARY KEY(`column`)" instead of "`column` INT PRIMARY KEY")
	 *                        5.) Write a create query with every (column) definition in a new line (using \n)
	 * @throws IllegalArgumentException If the create query is not in the right format
	 * @throws SQLException If any handling with the database failed
	 */
	public void validate(@NotNull Connection connection, @NotNull @Language("SQL") String tableDefinition) throws IllegalArgumentException, SQLException
	{
		//region validate and prepare table definition
		tableDefinition = tableDefinition.trim();
		Matcher definitionTableInfoMatch = CURRENT_TABLE_INFO.matcher(tableDefinition);
		if(!definitionTableInfoMatch.find())
		{
			tableDefinition = reformatTableDefinition(tableDefinition); // Try to format the table definition properly
			if(tableDefinition != null) definitionTableInfoMatch = CURRENT_TABLE_INFO.matcher(tableDefinition);
			if(!definitionTableInfoMatch.find()) throw new IllegalArgumentException("Invalid format of create query detected!");
		}
		String tableName = definitionTableInfoMatch.group("tableNameEsc");
		if(tableName == null) tableName = definitionTableInfoMatch.group("tableName");
		if(tableName == null || tableName.isEmpty()) throw new IllegalArgumentException("Invalid format of create query detected!");
		String[] definitionTableColumns = definitionTableInfoMatch.group("columns").split(",\n\\s?");
		//endregion
		//region get current definition
		List<String> currentTableColumns;
		try
		{
			currentTableColumns = getCurrentTableColumns(connection, tableName);
		}
		catch(SQLException ignored)
		{
			try(Statement statement = connection.createStatement())
			{
				statement.executeUpdate(tableDefinition);
			}
			return;
		}
		//endregion
		//region compare and update
		for(int i = 0; i < definitionTableColumns.length; i++)
		{
			definitionTableColumns[i] = definitionTableColumns[i].trim().replaceAll("\\s+", " "); //TODO there are places where multiple spaces must not be removed
			Matcher columnMatcher = COLUMN_CONSTRAINT_CHECKER_PATTERN.matcher(definitionTableColumns[i]);
			if(columnMatcher.find())
			{
				processConstraint(connection, columnMatcher, tableName, definitionTableColumns[i], currentTableColumns);
				continue;
			}
			columnMatcher = COLUMN_KEY_CHECKER_PATTERN.matcher(definitionTableColumns[i]);
			if(columnMatcher.find())
			{
				processKey(connection, columnMatcher, tableName, definitionTableColumns[i], currentTableColumns);
				continue;
			}
			columnMatcher = COLUMN_NAME_EXTRACTOR_PATTERN.matcher(definitionTableColumns[i]);
			if(columnMatcher.find()) processName(connection, columnMatcher, tableName, definitionTableColumns[i], currentTableColumns);
		}
		//endregion
	}

	protected List<String> getCurrentTableColumns(@NotNull Connection connection, @NotNull String tableName) throws SQLException
	{
		List<String> currentTableColumns = new LinkedList<>();
		@Language("SQL") String currentCreateStatement = getCurrentCreateStatement(connection, tableName);
		if(!CURRENT_TABLE_INFO.matcher(currentCreateStatement).matches()) currentCreateStatement = reformatTableDefinition(currentCreateStatement);
		if(currentCreateStatement == null || currentCreateStatement.isEmpty()) throw new SQLException();
		Collections.addAll(currentTableColumns, currentCreateStatement.split("(,|^\\()?\n\\s*"));
		currentTableColumns.remove(0);
		currentTableColumns.remove(currentTableColumns.size() - 1);
		return currentTableColumns;
	}

	protected void processConstraint(@NotNull Connection connection, @NotNull Matcher columnMatcher, @NotNull String tableName, @NotNull String definitionColumn, @NotNull List<String> currentTableColumns) throws SQLException
	{
		String columnName = (columnMatcher.group(3) == null ? (columnMatcher.group(2) == null ? "" : columnMatcher.group(2)) : columnMatcher.group(3));
		boolean keyExists = false, update = false;
		String tempValue;
		String[] createKeyArray, tempArray, currentReferenceColumns, currentTargetColumns;
		Iterator<String> currentTableColumnsIterator;
		Matcher currentMatcher, tempKeyMatcher;
		switch(columnMatcher.group(4).toUpperCase())
		{
			case "PRIMARY KEY":
				createKeyArray = columnMatcher.group(5).replaceAll("[`()]", "").split(",\\s*");
				currentTableColumnsIterator = currentTableColumns.iterator();
				while(currentTableColumnsIterator.hasNext())
				{
					currentMatcher = COLUMN_CONSTRAINT_CHECKER_PATTERN.matcher(currentTableColumnsIterator.next());
					if(currentMatcher.find() && currentMatcher.group(4).equalsIgnoreCase("PRIMARY KEY"))
					{
						keyExists = true;
						currentTableColumnsIterator.remove();
						tempArray = currentMatcher.group(5).replaceAll("[`()]", "").split(",\\s*");
						if(createKeyArray.length != tempArray.length)
						{
							update = true;
							break;
						}
						for(int index = 0; index < createKeyArray.length; index++)
						{
							if(!createKeyArray[index].equalsIgnoreCase(tempArray[index]))
							{
								update = true;
								break;
							}
						}
						break;
					}
				}
				if(!keyExists)
				{
					addPrimaryKey(connection, tableName, columnMatcher.group(5));
				}
				else if(update)
				{
					modifyPrimaryKey(connection, tableName, columnMatcher.group(5));
				}
				break;
			case "UNIQUE INDEX":
			case "UNIQUE KEY":
				tempKeyMatcher = UNIQUE_INDEX_PATTERN.matcher(columnMatcher.group(5));
				if(tempKeyMatcher.find())
				{
					if(columnName.length() == 0)
					{
						columnName = tempKeyMatcher.group(2) == null ? tempKeyMatcher.group(1) : tempKeyMatcher.group(2);
					}
					createKeyArray = tempKeyMatcher.group(3).replaceAll("`", "").split(",\\s*");
					currentTableColumnsIterator = currentTableColumns.iterator();
					if(columnName == null)
					{
						while(currentTableColumnsIterator.hasNext())
						{
							currentMatcher = COLUMN_CONSTRAINT_CHECKER_PATTERN.matcher(currentTableColumnsIterator.next());
							if(currentMatcher.find() && (currentMatcher.group(4).equalsIgnoreCase("UNIQUE INDEX") || currentMatcher.group(4).equalsIgnoreCase("UNIQUE KEY")))
							{
								keyExists = true;
								currentMatcher = UNIQUE_INDEX_PATTERN.matcher(currentMatcher.group(5));
								if(currentMatcher.find())
								{
									tempArray = currentMatcher.group(3).replaceAll("`", "").split(",\\s*");
									if(createKeyArray.length != tempArray.length)
									{
										keyExists = false;
										continue;
									}
									for(int index = 0; index < createKeyArray.length; index++)
									{
										if(!createKeyArray[index].equalsIgnoreCase(tempArray[index]))
										{
											keyExists = false;
											break;
										}
									}
									if(keyExists) break;
								}
							}
						}
					}
					else
					{
						update = false;
						while(currentTableColumnsIterator.hasNext())
						{
							currentMatcher = COLUMN_CONSTRAINT_CHECKER_PATTERN.matcher(currentTableColumnsIterator.next());
							if(currentMatcher.find() && (currentMatcher.group(4).equalsIgnoreCase("UNIQUE INDEX") || currentMatcher.group(4).equalsIgnoreCase("UNIQUE KEY")))
							{
								tempValue = (columnMatcher.group(3) == null ? (columnMatcher.group(2) == null ? "" : columnMatcher.group(2)) : columnMatcher.group(3));
								currentMatcher = UNIQUE_INDEX_PATTERN.matcher(currentMatcher.group(5));
								if(currentMatcher.find())
								{
									if(tempValue.length() == 0)
									{
										tempValue = currentMatcher.group(2) == null ? currentMatcher.group(1) : currentMatcher.group(2);
									}
									if(tempValue != null && tempValue.equalsIgnoreCase(columnName))
									{
										keyExists = true;
										currentTableColumnsIterator.remove();
										tempArray = currentMatcher.group(3).replaceAll("`", "").split(",\\s*");
										if(createKeyArray.length != tempArray.length)
										{
											update = true;
											break;
										}
										for(int index = 0; index < createKeyArray.length; index++)
										{
											if(!createKeyArray[index].equalsIgnoreCase(tempArray[index]))
											{
												update = true;
												break;
											}
										}
										break;
									}
								}
							}
						}
						if(update)
						{
							makeIndexUnique(connection, tableName, columnName, tempKeyMatcher.group(3));
						}
					}
				}
				else
				{
					throw new IllegalArgumentException("Invalid format of create query detected - invalid unique index definition!");
				}
				if(!keyExists)
				{
					addUniqueIndex(connection, tableName, columnName,  tempKeyMatcher.group(3));
				}
				break;
			case "FOREIGN KEY":
				tempKeyMatcher = FOREIGN_KEY_PATTERN.matcher(columnMatcher.group(5));
				if(!tempKeyMatcher.find()) throw new IllegalArgumentException("Invalid format of create query detected - invalid reference detected!");
				if(columnName.length() == 0)
				{
					columnName = tempKeyMatcher.group(2) == null ? tempKeyMatcher.group(1) : tempKeyMatcher.group(2);
				}
				createKeyArray = tempKeyMatcher.group(3).replaceAll("`", "").split(",\\s*");
				tempArray = tempKeyMatcher.group(5).replaceAll("`", "").split(",\\s*");
				if(createKeyArray.length != tempArray.length) throw new IllegalArgumentException("Invalid format of create query detected - invalid reference detected!");
				currentTableColumnsIterator = currentTableColumns.iterator();
				if(columnName == null)
				{
					while(currentTableColumnsIterator.hasNext())
					{
						currentMatcher = COLUMN_CONSTRAINT_CHECKER_PATTERN.matcher(currentTableColumnsIterator.next());
						if(currentMatcher.find() && currentMatcher.group(4).equalsIgnoreCase("FOREIGN KEY"))
						{
							keyExists = true;
							currentMatcher = FOREIGN_KEY_PATTERN.matcher(currentMatcher.group(5));
							if(currentMatcher.find())
							{
								currentReferenceColumns = currentMatcher.group(3).replaceAll("`", "").split(",\\s*");
								currentTargetColumns = currentMatcher.group(5).replaceAll("`", "").split(",\\s*");
								if(currentReferenceColumns.length != currentTargetColumns.length) throw new IllegalArgumentException("Invalid format of create query detected - invalid reference detected!");
								if(createKeyArray.length != currentReferenceColumns.length)
								{
									keyExists = false;
									continue;
								}
								if(currentMatcher.group(7) != null)
								{
									if(tempKeyMatcher.group(7) == null || !currentMatcher.group(7).equalsIgnoreCase(tempKeyMatcher.group(7)))
									{
										keyExists = false;
										continue;
									}
								}
								else if(tempKeyMatcher.group(7) != null)
								{
									keyExists = false;
									continue;
								}
								if(currentMatcher.group(9) != null)
								{
									if(tempKeyMatcher.group(9) == null || !currentMatcher.group(9).equalsIgnoreCase(tempKeyMatcher.group(9)))
									{
										keyExists = false;
										continue;
									}
								}
								else if(tempKeyMatcher.group(9) != null)
								{
									keyExists = false;
									continue;
								}
								for(int index = 0; index < createKeyArray.length; index++)
								{
									if(!(createKeyArray[index].equalsIgnoreCase(currentReferenceColumns[index]) && tempArray[index].equalsIgnoreCase(currentTargetColumns[index])))
									{
										keyExists = false;
										break;
									}
								}
								if(keyExists) break;
							}
						}
					}
				}
				else
				{
					update = false;
					while(currentTableColumnsIterator.hasNext())
					{
						currentMatcher = COLUMN_CONSTRAINT_CHECKER_PATTERN.matcher(currentTableColumnsIterator.next());
						if(currentMatcher.find() && currentMatcher.group(4).equalsIgnoreCase("FOREIGN KEY"))
						{
							tempValue = (currentMatcher.group(3) == null ? currentMatcher.group(2) : currentMatcher.group(3));
							currentMatcher = FOREIGN_KEY_PATTERN.matcher(currentMatcher.group(5));
							if(currentMatcher.find())
							{
								if(tempValue.length() == 0)
								{
									tempValue = currentMatcher.group(2) == null ? currentMatcher.group(1) : currentMatcher.group(2);
								}
								if(tempValue != null && tempValue.equalsIgnoreCase(columnName))
								{
									keyExists = true;
									currentTableColumnsIterator.remove();
									currentReferenceColumns = currentMatcher.group(3).replaceAll("`", "").split(",\\s*");
									currentTargetColumns = currentMatcher.group(5).replaceAll("`", "").split(",\\s*");
									if(currentReferenceColumns.length != currentTargetColumns.length) throw new IllegalArgumentException("Invalid format of create query detected - invalid reference detected!");
									if(createKeyArray.length != currentReferenceColumns.length)
									{
										update = true;
										break;
									}
									for(int index = 0; index < createKeyArray.length; index++)
									{
										if(!(createKeyArray[index].equalsIgnoreCase(currentReferenceColumns[index]) && tempArray[index].equalsIgnoreCase(currentTargetColumns[index])))
										{
											update = true;
											break;
										}
									}
									if(currentMatcher.group(7) != null)
									{
										if(tempKeyMatcher.group(7) == null || !currentMatcher.group(7).equalsIgnoreCase(tempKeyMatcher.group(7))) update = true;
									}
									else if(tempKeyMatcher.group(7) != null)
									{
										update = true;
									}
									if(currentMatcher.group(9) != null)
									{
										if(tempKeyMatcher.group(9) == null || !currentMatcher.group(9).equalsIgnoreCase(tempKeyMatcher.group(9))) update = true;
									}
									else if(tempKeyMatcher.group(9) != null) update = true;
									break;
								}
							}
						}
					}
					if(update)
					{
						modifyConstraint(connection, tableName, columnName, tempKeyMatcher.group(3), tempKeyMatcher.group(4) + " (" + tempKeyMatcher.group(5) + ") " + (tempKeyMatcher.group(6) == null ? "" : tempKeyMatcher.group(6) + " ") + (tempKeyMatcher.group(8) == null ? "" : tempKeyMatcher.group(8)));
					}
				}
				if(!keyExists)
				{
					addConstraint(connection, tableName, columnName, tempKeyMatcher.group(3), tempKeyMatcher.group(4) + " (" + tempKeyMatcher.group(5) + ") " + (tempKeyMatcher.group(6) == null ? "" : tempKeyMatcher.group(6) + " ") + (tempKeyMatcher.group(8) == null ? "" : tempKeyMatcher.group(8)));
				}
				break;
		}
	}

	protected void processKey(@NotNull Connection connection, @NotNull Matcher columnMatcher, @NotNull String tableName, @NotNull String definitionColumn, @NotNull List<String> currentTableColumns) throws SQLException
	{
		String columnName = columnMatcher.group(3) == null ? columnMatcher.group(2) : columnMatcher.group(3);
		boolean keyExists = false, update = false;
		String[] createKeyArray = columnMatcher.group(5).replaceAll("[`\\s]", "").split(",");
		if(columnName.length() > 0)
		{
			Iterator<String> currentTableColumnsIterator = currentTableColumns.iterator();
			while(currentTableColumnsIterator.hasNext())
			{
				Matcher tempKeyMatcher = COLUMN_KEY_CHECKER_PATTERN.matcher(currentTableColumnsIterator.next());
				if(tempKeyMatcher.find())
				{
					if((tempKeyMatcher.group(3) == null ? tempKeyMatcher.group(2) : tempKeyMatcher.group(3)).equalsIgnoreCase(columnName))
					{
						currentTableColumnsIterator.remove();
						keyExists = true;
						String[] tempArray = tempKeyMatcher.group(5).replaceAll("`", "").split(",\\s*");
						if(tempArray.length != createKeyArray.length)
						{
							update = true;
							break;
						}
						for(int i = 0; i < createKeyArray.length; i++)
						{
							if(!createKeyArray[i].equalsIgnoreCase(tempArray[i]))
							{
								update = true;
								break;
							}
						}
						break;
					}
				}
			}
			if(update)
			{
				modifyIndex(connection, tableName, columnName, columnMatcher.group(5));
				return;
			}
		}
		else
		{
			for(String currentTableColumn : currentTableColumns)
			{
				Matcher tempKeyMatcher = COLUMN_KEY_CHECKER_PATTERN.matcher(currentTableColumn);
				if(tempKeyMatcher.find())
				{
					String[] tempArray = tempKeyMatcher.group(5).replaceAll("[`\\s]", "").split(",");
					if(tempArray.length != createKeyArray.length) continue;
					for(int i = 0; i < createKeyArray.length; i++)
					{
						if(createKeyArray[i].equalsIgnoreCase(tempArray[i]))
						{
							keyExists = true;
							break;
						}
					}
				}
			}
		}
		if(!keyExists)
		{
			addIndex(connection, tableName, columnName, columnMatcher.group(5));
		}
	}

	protected void processName(@NotNull Connection connection, @NotNull Matcher columnMatcher, @NotNull String tableName, @NotNull String definitionColumn, @NotNull List<String> currentTableColumns) throws SQLException
	{
		String columnName = columnMatcher.group(2) == null ? columnMatcher.group(1) : columnMatcher.group(2);
		boolean keyExists = false, update = true;
		Iterator<String> currentTableColumnsIterator = currentTableColumns.iterator();
		while(currentTableColumnsIterator.hasNext())
		{
			String tempValue = currentTableColumnsIterator.next();
			if(COLUMN_CONSTRAINT_CHECKER_PATTERN.matcher(tempValue).find()) continue;
			if(COLUMN_KEY_CHECKER_PATTERN.matcher(tempValue).find()) continue;
			Matcher tempKeyMatcher = COLUMN_NAME_EXTRACTOR_PATTERN.matcher(tempValue);
			if(tempKeyMatcher.find() && (tempKeyMatcher.group(2) == null ? tempKeyMatcher.group(1) : tempKeyMatcher.group(2)).equalsIgnoreCase(columnName))
			{
				currentTableColumnsIterator.remove();
				keyExists = true;
				if(!columnMatcher.group(3).equalsIgnoreCase(tempKeyMatcher.group(3)))
				{
					Matcher currentMatcher = COLUMN_TYPE_EXTRACTOR_PATTERN.matcher(tempKeyMatcher.group(3));
					tempKeyMatcher = COLUMN_TYPE_EXTRACTOR_PATTERN.matcher(columnMatcher.group(3));
					if(currentMatcher.find() && tempKeyMatcher.find() && currentMatcher.group(1).equalsIgnoreCase(tempKeyMatcher.group(1)))
					{
						List<String> currentFlags = new LinkedList<>(Arrays.asList(currentMatcher.group(4).split("\\s+")));
						for(String flag : tempKeyMatcher.group(4).split("\\s+"))
						{
							update = true;
							Iterator<String> currentFlagsIterator = currentFlags.iterator();
							while(currentFlagsIterator.hasNext())
							{
								tempValue = currentFlagsIterator.next();
								if(flag.equalsIgnoreCase(tempValue))
								{
									update = false;
									currentFlagsIterator.remove();
									break;
								}
							}
							if(update) break;
						}
					}
					if(update)
					{
						modifyColumn(connection, tableName, columnName, columnMatcher.group(3));
					}
				}
			}
		}
		if(!keyExists)
		{
			addColumn(connection, tableName, columnName, columnMatcher.group(3));
		}
	}

	//region SQL commands to be implemented by database specific classes
	protected abstract String getCurrentCreateStatement(@NotNull Connection connection, @NotNull String tableName) throws SQLException;

	protected abstract void addColumn(Connection connection, String tableName, String columnName, String columnDefinition) throws SQLException;

	protected abstract void modifyColumn(Connection connection, String tableName, String columnName, String columnDefinition) throws SQLException;

	protected abstract void addIndex(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException;

	protected abstract void modifyIndex(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException;

	protected abstract void makeIndexUnique(@NotNull Connection connection, String tableName, String columnName, String indexString) throws SQLException;

	protected abstract void addUniqueIndex(@NotNull Connection connection, String tableName, @Nullable String columnName, String indexString) throws SQLException;

	protected abstract void addConstraint(@NotNull Connection connection, String tableName, String columnName, String foreignKey, String references) throws SQLException;

	protected abstract void modifyConstraint(@NotNull Connection connection, String tableName, String columnName, String foreignKey, String references) throws SQLException;

	protected abstract void addPrimaryKey(@NotNull Connection connection, String tableName, String primaryKey) throws SQLException;

	protected abstract void modifyPrimaryKey(@NotNull Connection connection, String tableName, String primaryKey) throws SQLException;
	//endregion

	//region reformat create query stuff
	private static final Pattern QUERY_END = Pattern.compile("\\)(?<engine>\\s+ENGINE=\\w+)?\\s*;?$", Pattern.CASE_INSENSITIVE);
	private static final Pattern QUERY_BEGIN = Pattern.compile("^\\w*(CREATE TABLE IF NOT EXISTS|CREATE TABLE)\\s+(`(\\w+)`|\\w+)\\s+\\(", Pattern.CASE_INSENSITIVE);

	protected static String reformatTableDefinition(@Language("SQL") String query)
	{
		query = query.replaceAll("\r", "").replaceAll("\n", " ");
		Matcher tempMatcher = QUERY_END.matcher(query);
		if(!tempMatcher.find()) return null;
		String temp = tempMatcher.group("engine");
		query = tempMatcher.replaceAll("");
		String queryEnd = ")" + ((temp != null) ? temp : "") + ";";
		tempMatcher = QUERY_BEGIN.matcher(query);
		if(!tempMatcher.find()) return null;
		String queryBegin = tempMatcher.group();
		query = tempMatcher.replaceAll("").trim();
		return queryBegin + "\n" + query.replaceAll(",(?=([^\"'`]*[\"'`][^\"'`]*[\"'`])*[^\"'`]*$)", ",\n").replaceAll("(,\\n)(?=[^(]*?\\))", ",") + "\n" + queryEnd;
	}
	//endregion
}