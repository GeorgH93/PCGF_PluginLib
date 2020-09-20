/*
 *   Copyright (C) 2020 GeorgH93
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

package at.pcgamingfreaks.Bukkit;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import lombok.SneakyThrows;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlatformResolver
{
	private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("^(?<package>.+)\\.(?<class>[\\w]*)$");
	private static final Pattern INTERFACE_NAME_PATTERN = Pattern.compile("^(?<package>.+)\\.I(?<class>[\\w]*)$");

	/**
	 * @param clazz The class/interface that defines the platform depending class. Must implement {@link IPlatformDependent} and if it is an interface, it's name must start with an I.
	 * @param <T> The type of the class.
	 * @return The created instance of the platform dependent class. <b>WARNING: Will always return null during unit tests!!!!</b>
	 */
	@SneakyThrows
	public static @NotNull <T extends IPlatformDependent> T createPlatformInstance(final Class<T> clazz)
	{
		//region setup
		Matcher classMatcher = (clazz.isInterface() ? INTERFACE_NAME_PATTERN : CLASS_NAME_PATTERN).matcher(clazz.getName());
		if(!classMatcher.matches()) throw new IllegalArgumentException("The given class is not valid");
		String className = classMatcher.group("package") + "." + classMatcher.group("class");
		//endregion
		return createPlatformInstance(clazz, className);
	}

	/**
	 * @param clazz The class/interface that defines the platform depending class. Must implement {@link IPlatformDependent}.
	 * @param className The name that should be used as the base for the platform dependent class name.
	 * @param <T> The type of the class.
	 * @return The created instance of the platform dependent class. <b>WARNING: Will always return null during unit tests!!!!</b>
	 */
	@SneakyThrows
	public static @NotNull <T extends IPlatformDependent> T createPlatformInstance(final Class<T> clazz, final @NotNull String className)
	{
		//region check if running as Test
		StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
		String runnerClass = stackTraceElements[stackTraceElements.length - 1].getClassName();
		if(runnerClass.contains("surefire") || runnerClass.contains("junit"))
		{
			return null;
		}
		//endregion

		String nmsServerVersion = Bukkit.getServer().getClass().getName().split("\\.")[3].substring(1);
		//TODO detect glowstone
		Class<?> tmp = getClass(className + "_" + nmsServerVersion);
		if(tmp == null)
		{
			tmp = getClass(className + "_Reflection");
		}
		if(tmp == null) throw new ClassNotFoundException("Could not found a Platform implementation for " + clazz.getName());
		if(tmp.isInterface()) throw new IllegalStateException("Found platform class '" + tmp.getName() + "' is an interface!");
		if(tmp.isInstance(clazz)) throw new IllegalStateException("Found platform class '" + tmp.getName() + "' is not of the expected type!");
		//noinspection unchecked
		return (T) tmp.newInstance();
	}

	private static Class<?> getClass(String className)
	{
		try
		{
			return Class.forName(className);
		}
		catch(Exception ignored) {}
		return null;
	}
}