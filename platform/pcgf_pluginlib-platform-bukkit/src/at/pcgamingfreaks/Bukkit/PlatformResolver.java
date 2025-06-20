/*
 *   Copyright (C) 2024 GeorgH93
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

package at.pcgamingfreaks.Bukkit;

import at.pcgamingfreaks.ServerType;

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
		return createPlatformInstance(clazz, false);
	}

	/**
	 * @param clazz The class/interface that defines the platform depending class. Must implement {@link IPlatformDependent} and if it is an interface, it's name must start with an I.
	 * @param <T> The type of the class.
	 * @return The created instance of the platform dependent class. <b>WARNING: Will always return null during unit tests!!!!</b>
	 */
	@SneakyThrows
	public static @NotNull <T extends IPlatformDependent> T createPlatformInstance(final Class<T> clazz, boolean forceReflection)
	{
		//region setup
		Matcher classMatcher = (clazz.isInterface() ? INTERFACE_NAME_PATTERN : CLASS_NAME_PATTERN).matcher(clazz.getName());
		if(!classMatcher.matches()) throw new IllegalArgumentException("The given class is not valid");
		String className = classMatcher.group("package") + "." + classMatcher.group("class");
		//endregion
		return createPlatformInstance(clazz, className, forceReflection);
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
		return createPlatformInstance(clazz, className, false);
	}

	/**
	 * @param clazz The class/interface that defines the platform depending class. Must implement {@link IPlatformDependent}.
	 * @param className The name that should be used as the base for the platform dependent class name.
	 * @param <T> The type of the class.
	 * @return The created instance of the platform dependent class. <b>WARNING: Will always return null during unit tests!!!!</b>
	 */
	@SneakyThrows
	public static @NotNull <T extends IPlatformDependent> T createPlatformInstance(final Class<T> clazz, final @NotNull String className, boolean forceReflection)
	{
		//region check if running as Test
		StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
		String runnerClass = stackTraceElements[stackTraceElements.length - 1].getClassName();
		if(runnerClass.contains("surefire") || runnerClass.contains("junit"))
		{
			return null;
		}
		//endregion
		Class<?> tmp = null;
		if(getClass("net.glowstone.GlowServer") != null)
		{
			tmp = getClass(className + "_Glowstone");
		}
		else
		{
			String nmsServerVersion = MCVersion.CURRENT_VERSION.getIdentifier();
			if (ServerType.isPaperCompatible() && MCVersion.isNewerOrEqualThan(MCVersion.MC_NMS_1_20_R4))
				tmp = getClass(className + "_" + nmsServerVersion + "_Paper");
			if (tmp == null)
				tmp = getClass(className + "_" + nmsServerVersion);
			if(tmp == null || forceReflection)
			{
				tmp = getClass(className + "_Reflection");
			}
		}
		if(tmp == null) throw new ClassNotFoundException("Could not find a platform implementation for " + clazz.getName());
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