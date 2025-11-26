/*
 *   Copyright (C) 2025 GeorgH93
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

package at.pcgamingfreaks;

/**
 * A stupid little class to force slf4j to init with a custom logger
 */
public final class Slf4jLoggerForcer implements AutoCloseable
{
	private final String loggerClassName;
	private boolean enabled = false;
	private String slf4jPropBackup = null;
	private String slf4jInternalVerbosityBackup = null;

	public Slf4jLoggerForcer(String loggerClassName)
	{
		this.loggerClassName = loggerClassName;
	}

	public Slf4jLoggerForcer(Class<?> loggerClass)
	{
		this.loggerClassName = loggerClass.getName();
	}

	public void enable() { enable("ERROR"); }

	public void enable(String level)
	{
		if (enabled) disable();
		slf4jPropBackup = System.getProperty(org.slf4j.LoggerFactory.PROVIDER_PROPERTY_KEY);
		slf4jInternalVerbosityBackup = System.getProperty(org.slf4j.helpers.Reporter.SLF4J_INTERNAL_VERBOSITY_KEY);
		System.setProperty(org.slf4j.LoggerFactory.PROVIDER_PROPERTY_KEY, loggerClassName);
		System.setProperty(org.slf4j.helpers.Reporter.SLF4J_INTERNAL_VERBOSITY_KEY, level);
		enabled = true;
	}

	public void disable()
	{
		if (!enabled) return;
		if (slf4jPropBackup != null) System.setProperty(org.slf4j.LoggerFactory.PROVIDER_PROPERTY_KEY, slf4jPropBackup);
		else System.clearProperty(org.slf4j.LoggerFactory.PROVIDER_PROPERTY_KEY);
		if (slf4jInternalVerbosityBackup != null) System.setProperty(org.slf4j.helpers.Reporter.SLF4J_INTERNAL_VERBOSITY_KEY, slf4jInternalVerbosityBackup);
		else System.clearProperty(org.slf4j.helpers.Reporter.SLF4J_INTERNAL_VERBOSITY_KEY);
		enabled = false;
	}

	@Override
	public void close()
	{
		disable();
	}
}
