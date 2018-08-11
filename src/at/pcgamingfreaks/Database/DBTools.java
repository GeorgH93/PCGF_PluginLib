/*
 *   Copyright (C) 2016 GeorgH93
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

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBTools
{
	private static final Pattern CURRENT_TABLE_INFO = Pattern.compile("^\\w*(CREATE TABLE IF NOT EXISTS|CREATE TABLE)\\s+(`(\\w+)`|\\w+)\\s+\\(\\n([\\s\\S]*)\\n\\)(\\s+ENGINE=\\w+)?;?$", Pattern.CASE_INSENSITIVE);
	private static final Pattern COLUMN_NAME_EXTRACTOR_PATTERN = Pattern.compile("^(`(\\w+)`|\\w+) (.*)$", Pattern.CASE_INSENSITIVE);
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
	@SuppressWarnings("SqlNoDataSourceInspection")
	public static void updateDB(@NotNull Connection connection, @NotNull @Language("SQL") String tableDefinition) throws IllegalArgumentException, SQLException
	{
		Matcher currentTableInfoMatch = CURRENT_TABLE_INFO.matcher(tableDefinition.trim());
		if(!currentTableInfoMatch.find()) throw new IllegalArgumentException("Invalid format of create query detected!");
		String tableName;
		String[] tableColumns;
		List<String> currentTableColumns;
		tableName = currentTableInfoMatch.group(currentTableInfoMatch.groupCount() - 2);
		try(Statement statement = connection.createStatement())
		{
			try(ResultSet tableExists = statement.executeQuery("SHOW CREATE TABLE " + tableName + ""))
			{
				tableExists.next();
				tableColumns = tableExists.getString(2).split("(,|^\\()?\n\\s*");
				currentTableColumns = new LinkedList<>();
				Collections.addAll(currentTableColumns, tableColumns);
				currentTableColumns.remove(0);
				currentTableColumns.remove(currentTableColumns.size() - 1);
			}
			catch(SQLException ignored)
			{
				statement.executeUpdate(tableDefinition);
				return;
			}
			tableColumns = currentTableInfoMatch.group(currentTableInfoMatch.groupCount() - 1).split(",\n\\s?");
			Matcher columnMatcher, currentMatcher, tempKeyMatcher;
			String columnName, tempValue;
			String[] tempArray, createKeyArray, currentReferenceColumns, currentTargetColumns;
			List<String> currentFlags;
			Iterator<String> currentTableColumnsIterator, currentFlagsIterator;
			boolean keyExists, update;
			int index;
			for(int i = 0; i < tableColumns.length; i++)
			{
				tableColumns[i] = tableColumns[i].trim().replaceAll("\\s+", " ");
				columnMatcher = COLUMN_CONSTRAINT_CHECKER_PATTERN.matcher(tableColumns[i]);
				if(columnMatcher.find())
				{
					columnName = (columnMatcher.group(3) == null ? (columnMatcher.group(2) == null ? "" : columnMatcher.group(2)) : columnMatcher.group(3));
					switch(columnMatcher.group(4).toUpperCase())
					{
						case "PRIMARY KEY":
							keyExists = false;
							update = false;
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
									for(index = 0; index < createKeyArray.length; index++)
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
								statement.executeUpdate("ALTER TABLE " + tableName + " ADD PRIMARY KEY " + columnMatcher.group(5));
							}
							else if(update)
							{
								statement.executeUpdate("ALTER TABLE " + tableName + " DROP PRIMARY KEY, ADD PRIMARY KEY " + columnMatcher.group(5));
							}
							break;
						case "UNIQUE INDEX":
						case "UNIQUE KEY":
							keyExists = false;
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
												for(index = 0; index < createKeyArray.length; index++)
												{
													if(!createKeyArray[index].equalsIgnoreCase(tempArray[index]))
													{
														keyExists = false;
														break;
													}
												}
												if(keyExists)
												{
													break;
												}
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
													for(index = 0; index < createKeyArray.length; index++)
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
										statement.executeUpdate("ALTER TABLE " + tableName + " DROP INDEX `" + columnName + "`, ADD UNIQUE INDEX `" + columnName + "` (" + tempKeyMatcher.group(3) + ")");
									}
								}
							}
							else
							{
								throw new IllegalArgumentException("Invalid format of create query detected - invalid unique index definition!");
							}
							if(!keyExists)
							{
								statement.executeUpdate("ALTER TABLE " + tableName + " ADD UNIQUE INDEX " + (columnName == null ? "" : "`" + columnName + "` ") + "(" + tempKeyMatcher.group(3) + ")");
							}
							break;
						case "FOREIGN KEY":
							keyExists = false;
							tempKeyMatcher = FOREIGN_KEY_PATTERN.matcher(columnMatcher.group(5));
							if(!tempKeyMatcher.find()) throw new IllegalArgumentException("Invalid format of create query detected - invalid reference detected!");
							if(columnName.length() == 0)
							{
								columnName = tempKeyMatcher.group(2) == null ? tempKeyMatcher.group(1) : tempKeyMatcher.group(2);
							}
							createKeyArray = tempKeyMatcher.group(3).replaceAll("`", "").split(",\\s*");
							tempArray = tempKeyMatcher.group(5).replaceAll("`", "").split(",\\s*");
							if(createKeyArray.length != tempArray.length)
							{
								throw new IllegalArgumentException("Invalid format of create query detected - invalid reference detected!");
							}
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
											if(currentReferenceColumns.length != currentTargetColumns.length)
											{
												throw new IllegalArgumentException("Invalid format of create query detected - invalid reference detected!");
											}
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
											for(index = 0; index < createKeyArray.length; index++)
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
												if(currentReferenceColumns.length != currentTargetColumns.length)
												{
													throw new IllegalArgumentException("Invalid format of create query detected - invalid reference detected!");
												}
												if(createKeyArray.length != currentReferenceColumns.length)
												{
													update = true;
													break;
												}
												for(index = 0; index < createKeyArray.length; index++)
												{
													if(!(createKeyArray[index].equalsIgnoreCase(currentReferenceColumns[index]) && tempArray[index].equalsIgnoreCase(currentTargetColumns[index])))
													{
														update = true;
														break;
													}
												}
												if(currentMatcher.group(7) != null)
												{
													if(tempKeyMatcher.group(7) == null || !currentMatcher.group(7).equalsIgnoreCase(tempKeyMatcher.group(7)))
													{
														update = true;
													}
												}
												else if(tempKeyMatcher.group(7) != null)
												{
													update = true;
												}
												if(currentMatcher.group(9) != null)
												{
													if(tempKeyMatcher.group(9) == null || !currentMatcher.group(9).equalsIgnoreCase(tempKeyMatcher.group(9)))
													{
														update = true;
													}
												}
												else if(tempKeyMatcher.group(9) != null)
												{
													update = true;
												}
												break;
											}
										}
									}
								}
								if(update)
								{
									statement.executeUpdate("ALTER TABLE " + tableName + " DROP FOREIGN KEY `" + columnName + "`,  ADD CONSTRAINT `" + columnName + "` FOREIGN KEY (" + tempKeyMatcher.group(3) + ") REFERENCES " + tempKeyMatcher.group(4) + " (" + tempKeyMatcher.group(5) + ") " + (tempKeyMatcher.group(6) == null ? "" : tempKeyMatcher.group(6) + " ") + (tempKeyMatcher.group(8) == null ? "" : tempKeyMatcher.group(8)));
								}
							}
							if(!keyExists)
							{
								statement.executeUpdate("ALTER TABLE " + tableName + " ADD CONSTRAINT " + (columnName == null ? "" : "`" + columnName + "` ") + "FOREIGN KEY (" + tempKeyMatcher.group(3) + ") REFERENCES " + tempKeyMatcher.group(4) + " (" + tempKeyMatcher.group(5) + ") " + (tempKeyMatcher.group(6) == null ? "" : tempKeyMatcher.group(6) + " ") + (tempKeyMatcher.group(8) == null ? "" : tempKeyMatcher.group(8)));
							}
							break;
					}
					continue;
				}
				columnMatcher = COLUMN_KEY_CHECKER_PATTERN.matcher(tableColumns[i]);
				if(columnMatcher.find())
				{
					columnName = columnMatcher.group(3) == null ? columnMatcher.group(2) : columnMatcher.group(3);
					keyExists = false;
					update = false;
					if(columnName.length() > 0)
					{
						createKeyArray = columnMatcher.group(5).replaceAll("[`\\s]", "").split(",");
						currentTableColumnsIterator = currentTableColumns.iterator();
						while(currentTableColumnsIterator.hasNext())
						{
							tempKeyMatcher = COLUMN_KEY_CHECKER_PATTERN.matcher(currentTableColumnsIterator.next());
							if(tempKeyMatcher.find())
							{
								if((tempKeyMatcher.group(3) == null ? tempKeyMatcher.group(2) : tempKeyMatcher.group(3)).equalsIgnoreCase(columnName))
								{
									currentTableColumnsIterator.remove();
									keyExists = true;
									tempArray = tempKeyMatcher.group(5).replaceAll("`", "").split(",\\s*");
									if(tempArray.length != createKeyArray.length)
									{
										update = true;
										break;
									}
									for(index = 0; index < createKeyArray.length; index++)
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
						if(update)
						{
							statement.executeUpdate("ALTER TABLE " + tableName + " DROP INDEX `" + columnName + "`, ADD INDEX `" + columnName + "` (" + columnMatcher.group(5) + ")");
							continue;
						}
					}
					else
					{
						keyExists = false;
						createKeyArray = columnMatcher.group(5).replaceAll("[`\\s]", "").split(",");
						currentTableColumnsIterator = currentTableColumns.iterator();
						while(currentTableColumnsIterator.hasNext())
						{
							tempKeyMatcher = COLUMN_KEY_CHECKER_PATTERN.matcher(currentTableColumnsIterator.next());
							if(tempKeyMatcher.find())
							{
								tempArray = tempKeyMatcher.group(5).replaceAll("[`\\s]", "").split(",");
								if(tempArray.length != createKeyArray.length)
								{
									continue;
								}
								for(index = 0; index < createKeyArray.length; index++)
								{
									if(createKeyArray[index].equalsIgnoreCase(tempArray[index]))
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
						statement.executeUpdate("ALTER TABLE " + tableName + " ADD INDEX " + (columnName.length() > 0 ? "`" + columnName + "` " : "") + "(" + columnMatcher.group(5) + ")");
					}
					continue;
				}
				columnMatcher = COLUMN_NAME_EXTRACTOR_PATTERN.matcher(tableColumns[i]);
				if(columnMatcher.find())
				{
					columnName = columnMatcher.group(2) == null ? columnMatcher.group(1) : columnMatcher.group(2);
					keyExists = false;
					update = true;
					currentTableColumnsIterator = currentTableColumns.iterator();
					while(currentTableColumnsIterator.hasNext())
					{
						tempValue = currentTableColumnsIterator.next();
						tempKeyMatcher = COLUMN_CONSTRAINT_CHECKER_PATTERN.matcher(tempValue);
						if(tempKeyMatcher.find())
						{
							continue;
						}
						tempKeyMatcher = COLUMN_KEY_CHECKER_PATTERN.matcher(tempValue);
						if(tempKeyMatcher.find())
						{
							continue;
						}
						tempKeyMatcher = COLUMN_NAME_EXTRACTOR_PATTERN.matcher(tempValue);
						if(tempKeyMatcher.find() && (tempKeyMatcher.group(2) == null ? tempKeyMatcher.group(1) : tempKeyMatcher.group(2)).equalsIgnoreCase(columnName))
						{
							currentTableColumnsIterator.remove();
							keyExists = true;
							if(!columnMatcher.group(3).equalsIgnoreCase(tempKeyMatcher.group(3)))
							{
								currentMatcher = COLUMN_TYPE_EXTRACTOR_PATTERN.matcher(tempKeyMatcher.group(3));
								tempKeyMatcher = COLUMN_TYPE_EXTRACTOR_PATTERN.matcher(columnMatcher.group(3));
								if(currentMatcher.find() && tempKeyMatcher.find() && currentMatcher.group(1).equalsIgnoreCase(tempKeyMatcher.group(1)))
								{
									currentFlags = new LinkedList<>();
									tempArray = currentMatcher.group(4).split("\\s+");
									for(index = 0; index < tempArray.length; index++)
									{
										currentFlags.add(tempArray[index]);
									}
									for(String flag : tempKeyMatcher.group(4).split("\\s+"))
									{
										update = true;
										currentFlagsIterator = currentFlags.iterator();
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
										if(update)
										{
											break;
										}
									}
								}
								if(update)
								{
									statement.executeUpdate("ALTER TABLE " + tableName + " MODIFY COLUMN `" + columnName + "` " + columnMatcher.group(3));
								}
							}
						}
					}
					if(!keyExists)
					{
						statement.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN `" + columnName + "` " + columnMatcher.group(3));
					}
				}
			}
		}
	}

	/**
	 * Creates an {@link PreparedStatement} form the {@link Connection}, fills it with the data given and executes it.
	 * This method is not async! And the connection is not closed after it is done!
	 *
	 * @param connection The connection used for the query.
	 * @param query The query to execute.
	 * @param args The arguments used for the query.
	 * @throws SQLException If there was a problem executing the SQL statement
	 */
	public static void runStatement(final @NotNull Connection connection, final @NotNull @Language("SQL") String query, final @Nullable Object... args) throws SQLException
	{
		try(PreparedStatement preparedStatement = connection.prepareStatement(query))
		{
			setParameters(preparedStatement, args);
			preparedStatement.execute();
		}
	}

	/**
	 * Creates an {@link PreparedStatement} form the {@link Connection}, fills it with the data given and executes it.
	 * This method is not async! And the connection is not closed after it is done!
	 *
	 * @param connection The connection used for the query.
	 * @param query The query to execute.
	 * @param args The arguments used for the query.
	 */
	public static void runStatementWithoutException(final @NotNull Connection connection, final @NotNull @Language("SQL") String query, final @Nullable Object... args)
	{
		try
		{
			runStatement(connection, query, args);
		}
		catch(SQLException e)
		{
			System.out.println("\nQuery: " + query + "\n" + "Data: " + Arrays.toString(args)); //TODO remove debug output
			e.printStackTrace();
		}
	}

	/**
	 * Method to batch set parameters of a prepared statement.
	 * Starts with 1.
	 *
	 * @param preparedStatement The prepared statement to set the parameters.
	 * @param args The values to be set.
	 * @throws SQLException If there was a problem.
	 */
	public static void setParameters(@NotNull PreparedStatement preparedStatement, @Nullable Object... args) throws SQLException
	{
		for(int i = 0; args != null && i < args.length; i++)
		{
			preparedStatement.setObject(i + 1, args[i]);
		}
	}
}