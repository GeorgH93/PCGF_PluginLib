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

package at.pcgamingfreaks.PluginLib;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

import lombok.Getter;
import lombok.Setter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Slf4jToJavaLogger implements SLF4JServiceProvider, ILoggerFactory, org.slf4j.Logger
{
	@Getter @Setter private static Logger targetLogger;

	public static String REQUESTED_API_VERSION = "2.0.99"; // !final

	private final IMarkerFactory markerFactory = new BasicMarkerFactory();
	private final MDCAdapter mdcAdapter = new NOPMDCAdapter();

	@Getter private Logger logger;

	@Override
	public ILoggerFactory getLoggerFactory()
	{
		return this;
	}

	@Override
	public IMarkerFactory getMarkerFactory()
	{
		return markerFactory;
	}

	@Override
	public MDCAdapter getMDCAdapter()
	{
		return mdcAdapter;
	}

	@Override
	public String getRequestedApiVersion()
	{
		return REQUESTED_API_VERSION;
	}

	@Override
	public void initialize()
	{
		logger = targetLogger;
	}

	@Override
	public org.slf4j.Logger getLogger(String name)
	{
		return this;
	}

	@Override
	public String getName()
	{
		return "";
	}

	@Override public boolean isTraceEnabled() { return false; }
	@Override public void trace(String msg) {}
	@Override public void trace(String format, Object arg) {}
	@Override public void trace(String format, Object arg1, Object arg2) {}
	@Override public void trace(String format, Object... arguments) {}
	@Override public void trace(String msg, Throwable t) {}
	@Override public boolean isTraceEnabled(Marker marker) { return false; }
	@Override public void trace(Marker marker, String msg) {}
	@Override public void trace(Marker marker, String format, Object arg) {}
	@Override public void trace(Marker marker, String format, Object arg1, Object arg2) {}
	@Override public void trace(Marker marker, String format, Object... argArray) {}
	@Override public void trace(Marker marker, String msg, Throwable t) {}
	@Override public boolean isDebugEnabled() { return false; }
	@Override public void debug(String msg) {}
	@Override public void debug(String format, Object arg) {}
	@Override public void debug(String format, Object arg1, Object arg2) {}
	@Override public void debug(String format, Object... arguments) {}
	@Override public void debug(String msg, Throwable t) {}
	@Override public boolean isDebugEnabled(Marker marker) { return false; }
	@Override public void debug(Marker marker, String msg) {}
	@Override public void debug(Marker marker, String format, Object arg) {}
	@Override public void debug(Marker marker, String format, Object arg1, Object arg2) {}
	@Override public void debug(Marker marker, String format, Object... arguments) {}
	@Override public void debug(Marker marker, String msg, Throwable t) {}
	
	@Override public boolean isInfoEnabled() { return logger.isLoggable(Level.INFO); }
	@Override public void info(String msg) { logger.info(msg); }
	@Override public void info(String format, Object arg) { logger.info(MessageFormatter.format(format, arg).getMessage()); }
	@Override public void info(String format, Object arg1, Object arg2){ logger.info(MessageFormatter.format(format, arg1, arg2).getMessage()); }
	@Override public void info(String format, Object... arguments){ logger.info(MessageFormatter.basicArrayFormat(format, arguments)); }
	@Override public void info(String msg, Throwable t) { logger.log(Level.INFO, msg, t); }
	@Override public boolean isInfoEnabled(Marker marker) { return logger.isLoggable(Level.INFO); }
	@Override public void info(Marker marker, String msg) { info(msg); }
	@Override public void info(Marker marker, String format, Object arg) { info(format, arg); }
	@Override public void info(Marker marker, String format, Object arg1, Object arg2) { info(format, arg1, arg2); }
	@Override public void info(Marker marker, String format, Object... arguments) { info(format, arguments); }
	@Override public void info(Marker marker, String msg, Throwable t) { info(msg, t); }
	@Override public boolean isWarnEnabled() { return logger.isLoggable(Level.WARNING); }

	@Override public void warn(String msg) { logger.warning(msg); }
	@Override public void warn(String format, Object arg) { logger.warning(MessageFormatter.format(format, arg).getMessage()); }
	@Override public void warn(String format, Object arg1, Object arg2) { logger.warning(MessageFormatter.format(format, arg1, arg2).getMessage()); }
	@Override public void warn(String format, Object... arguments) { logger.warning(MessageFormatter.basicArrayFormat(format, arguments)); }
	@Override public void warn(String msg, Throwable t) { logger.log(Level.WARNING, msg, t); }
	@Override public boolean isWarnEnabled(Marker marker) { return logger.isLoggable(Level.WARNING); }
	@Override public void warn(Marker marker, String msg) { warn(msg); }
	@Override public void warn(Marker marker, String format, Object arg) { warn(format, arg); }
	@Override public void warn(Marker marker, String format, Object arg1, Object arg2) { warn(format, arg1, arg2); }
	@Override public void warn(Marker marker, String format, Object... arguments) { warn(format, arguments); }
	@Override public void warn(Marker marker, String msg, Throwable t) { warn(msg, t); }
	@Override public boolean isErrorEnabled() { return logger.isLoggable(Level.SEVERE); }

	@Override public void error(String msg) { logger.severe(msg); }
	@Override public void error(String format, Object arg) { logger.severe(MessageFormatter.format(format, arg).getMessage()); }
	@Override public void error(String format, Object arg1, Object arg2) { logger.severe(MessageFormatter.format(format, arg1, arg2).getMessage()); }
	@Override public void error(String format, Object... arguments) { logger.severe(MessageFormatter.basicArrayFormat(format, arguments)); }
	@Override public void error(String msg, Throwable t) { logger.log(Level.SEVERE, msg, t); }
	@Override public boolean isErrorEnabled(Marker marker) { return logger.isLoggable(Level.SEVERE); }
	@Override public void error(Marker marker, String msg) { error(msg); }
	@Override public void error(Marker marker, String format, Object arg) { error(format, arg); }
	@Override public void error(Marker marker, String format, Object arg1, Object arg2) { error(format, arg1, arg2); }
	@Override public void error(Marker marker, String format, Object... arguments) { error(format, arguments); }
	@Override public void error(Marker marker, String msg, Throwable t) { error(msg, t); }
}
