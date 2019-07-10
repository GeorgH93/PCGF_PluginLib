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

package at.pcgamingfreaks.Bukkit;

import com.google.common.collect.ImmutableMap;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will take place of the {@link NMSReflection} class if the server is running on cauldron/uranium.
 */
public class NMSReflectionCauldron extends OBCReflection implements NmsReflector
{
	/**
	 * Map of mc-dev simple class name to fully qualified Forge class name.
	 */
	private static Map<String, String> forgeClassMappings;
	/**
	 * Map of Forge fully qualified class names to a map from mc-dev field names to Forge field names.
	 */
	private static Map<String, Map<String, String>> forgeFieldMappings;

	/**
	 * Map of Forge fully qualified class names to a map from mc-dev method names to a map from method signatures to Forge method
	 * names.
	 */
	private static Map<String, Map<String, Map<String, String>>> forgeMethodMappings;
	private static Map<Class<?>, String> primitiveTypes;

	static
	{
		if(!Bukkit.getServer().getName().toLowerCase().contains("cauldron") && !Bukkit.getServer().getName().toLowerCase().contains("uranium"))
		{
			throw new RuntimeException("Using Cauldron Reflections for non Cauldron / Uranium based server!");
		}

		final String nameseg_class = "a-zA-Z0-9$_";
		final String fqn_class = nameseg_class + "/";

		primitiveTypes = ImmutableMap.<Class<?>, String>builder().put(boolean.class, "Z").put(byte.class, "B").put(char.class, "C").put(short.class, "S").put(int.class, "I").put(long.class, "J").put(float.class, "F").put(double.class, "D").put(void.class, "V").build();

		// Initialize the maps by reading the srg file
		forgeClassMappings = new HashMap<>();
		forgeFieldMappings = new HashMap<>();
		forgeMethodMappings = new HashMap<>();
		try
		{
			InputStream stream = Class.forName("net.minecraftforge.common.MinecraftForge").getClassLoader().getResourceAsStream("mappings/" + BUKKIT_VERSION + "/cb2numpkg.srg");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

			// 1: cb-simpleName
			// 2: forge-fullName (Needs dir2fqn())
			Pattern classPattern = Pattern.compile("^CL: net/minecraft/server/([" + nameseg_class + "]+) ([" + fqn_class + "]+)$");
			// 1: cb-simpleName
			// 2: cb-fieldName
			// 3: forge-fullName (Needs dir2fqn())
			// 4: forge-fieldName
			Pattern fieldPattern = Pattern.compile("^FD: net/minecraft/server/([" + nameseg_class + "]+)/([" + nameseg_class + "]+) ([" + fqn_class + "]+)/([" + nameseg_class + "]+)$");
			// 1: cb-simpleName
			// 2: cb-methodName
			// 3: cb-signature-args
			// 4: cb-signature-ret
			// 5: forge-fullName (Needs dir2fqn())
			// 6: forge-methodName
			// 7: forge-signature-args
			// 8: forge-signature-ret
			Pattern methodPattern = Pattern.compile("^MD: net/minecraft/server/([" + fqn_class + "]+)/([" + nameseg_class + "]+) \\(([;\\[" + fqn_class + "]*)\\)([;\\[" + fqn_class + "]+) " + "([" + fqn_class + "]+)/([" + nameseg_class + "]+) \\(([;\\[" + fqn_class + "]*)\\)([;\\[" + fqn_class + "]+)$");

			String line;
			while((line = reader.readLine()) != null)
			{
				Matcher classMatcher = classPattern.matcher(line);
				if(classMatcher.matches())
				{
					// by CB class name
					forgeClassMappings.put(classMatcher.group(1), classMatcher.group(2).replaceAll("/", "."));
					continue;
				}
				Matcher fieldMatcher = fieldPattern.matcher(line);
				if(fieldMatcher.matches())
				{
					// by CB class name
					Map<String, String> innerMap = forgeFieldMappings.computeIfAbsent(fieldMatcher.group(3).replaceAll("/", "."), k -> new HashMap<>());
					// by CB field name to Forge field name
					innerMap.put(fieldMatcher.group(2), fieldMatcher.group(4));
					continue;
				}
				Matcher methodMatcher = methodPattern.matcher(line);
				if(methodMatcher.matches())
				{
					// get by CB class name
					Map<String, Map<String, String>> middleMap = forgeMethodMappings.computeIfAbsent(methodMatcher.group(5).replaceAll("/", "."), k -> new HashMap<>());
					// get by CB method name
					Map<String, String> innerMap = middleMap.computeIfAbsent(methodMatcher.group(2), k -> new HashMap<>());
					// store the parameter strings
					innerMap.put(methodMatcher.group(3), methodMatcher.group(6));
					innerMap.put(methodMatcher.group(7), methodMatcher.group(6));
				}
			}
		}
		catch(ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
			System.err.println("Warning: Running on Cauldron server, but couldn't load mappings file.");
		}
	}

	/**
	 * Gets a net.minecraft.server class reference.
	 *
	 * @param className The name of the class.
	 * @return The class reference. Null if it was not found.
	 */
	public static @Nullable Class<?> getNMSClass(@NotNull String className)
	{
		try
		{
			String forgeName = forgeClassMappings.get(className);
			if(forgeName != null)
			{
				try
				{
					return Class.forName(forgeName);
				}
				catch(ClassNotFoundException ignored)
				{
				}
			}
			else
			{
				throw new RuntimeException("Missing Forge mapping for " + className);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a method reference from a net.minecraft.server class.
	 *
	 * @param className The name of the class.
	 * @param name The name of the method.
	 * @param args The types of the parameters of the method.
	 * @return The method reference. Null if it was not found.
	 */
	public static @Nullable Method getNMSMethod(@NotNull String className, @NotNull String name, @NotNull Class<?>... args)
	{
		Class<?> clazz = getNMSClass(className);
		return getNMSMethod(clazz, name, args);
	}

	public static Method getNMSMethod(@Nullable Class<?> clazz, @NotNull String methodName, @NotNull Class<?>... args)
	{
		if(clazz == null) return null;
		try
		{
			Map<String, String> innerMap = forgeMethodMappings.get(clazz.getName()).get(methodName);
			StringBuilder sb = new StringBuilder();
			for(Class<?> cl : args)
			{
				sb.append(methodSignaturePart(cl));
			}
			return clazz.getMethod(innerMap.get(sb.toString()), args);
		}
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static String methodSignaturePart(Class<?> param)
	{
		if(param.isArray())
		{
			return "[" + methodSignaturePart(param.getComponentType());
		}
		else if(param.isPrimitive())
		{
			return primitiveTypes.get(param);
		}
		else
		{
			return "L" + param.getName().replaceAll("\\.", "/") + ";";
		}
	}

	/*public static Method getMethod(Class<?> clazz, @NotNull String name, Class<?>... args)
	{
		for(Method m : clazz.getMethods())
		{
			if(m.getName().equals(name) && (args.length == 0 || classListEqual(args, m.getParameterTypes())))
			{
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}*/

	/**
	 * Gets a field reference from a net.minecraft.server class.
	 *
	 * @param className The name of the class.
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	public static @Nullable Field getNMSField(@NotNull String className, @NotNull String name)
	{
		return getNMSField(getNMSClass(className), name);
	}

	/**
	 * Gets a field reference from a net.minecraft.server class.
	 *
	 * @param clazz The class, must be a net.minecraft.server class!!!
	 * @param name The name of the field.
	 * @return The field reference. Null if it was not found.
	 */
	@Contract("null, _ -> null")
	public static @Nullable Field getNMSField(@Nullable Class clazz, @NotNull String name)
	{
		try
		{
			return (clazz == null) ? null : clazz.getField(forgeFieldMappings.get(clazz.getName()).get(name));
		}
		catch(NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public static @Nullable Enum<?> getNMSEnum(@NotNull String enumClassAndEnumName)
	{
		int i = enumClassAndEnumName.lastIndexOf('.');
		if(i < 1) throw new RuntimeException("Invalid class + enum name");
		return getNMSEnum(enumClassAndEnumName.substring(0, i), enumClassAndEnumName.substring(i + 1));
	}

	public static @Nullable Enum<?> getNMSEnum(@NotNull String enumClass, @NotNull String enumName)
	{
		Class<?> clazz = getNMSClass(enumClass);
		return clazz == null ? null : getEnum(clazz.getName() + "." + enumName);
	}

	public static @Nullable Object getHandle(@NotNull Object obj)
	{
		try
		{
			//noinspection ConstantConditions
			return getMethod(obj.getClass(), "getHandle").invoke(obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public @Nullable Class<?> getNmsClass(@NotNull String className)
	{
		return getNMSClass(className);
	}

	@Override
	public @Nullable Method getNmsMethod(@NotNull String className, @NotNull String name, @NotNull Class<?>... args)
	{
		return getNMSMethod(className, name, args);
	}

	@Override
	public @Nullable Method getNmsMethod(@Nullable Class<?> clazz, @NotNull String name, @NotNull Class<?>... args)
	{
		return getNMSMethod(clazz, name, args);
	}

	@Override
	public @Nullable Field getNmsField(@NotNull String className, @NotNull String name)
	{
		return getNMSField(className, name);
	}

	@Override
	public @Nullable Field getNmsField(@Nullable Class<?> clazz, @NotNull String name)
	{
		return getNMSField(clazz, name);
	}

	@Override
	public @Nullable Enum<?> getNmsEnum(@NotNull String enumClassAndEnumName)
	{
		return getNMSEnum(enumClassAndEnumName);
	}

	@Override
	public @Nullable Enum<?> getNmsEnum(@NotNull String enumClass, @NotNull String enumName)
	{
		return getNMSEnum(enumClass, enumName);
	}

	@Override
	public @Nullable Object getNmsHandle(@NotNull Object obj)
	{
		return getHandle(obj);
	}
}