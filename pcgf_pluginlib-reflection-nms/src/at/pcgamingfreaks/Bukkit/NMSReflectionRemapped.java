/*
 *   Copyright (C) 2021 GeorgH93
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

import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.ServerType;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class NMSReflectionRemapped implements NmsReflector
{
	private static final Map<String, Class<?>> CLASS_MAP = new ConcurrentHashMap<>();
	private static final Map<String, String> CLASS_NAME_MAP = new HashMap<>();
	private static final Map<String, String> FIELD_NAME_MAP = new HashMap<>();
	private static final Map<String, String> METHOD_NAME_MAP = new HashMap<>();

	static
	{
		loadMappings(CLASS_NAME_MAP, "Class");
		loadMappings(FIELD_NAME_MAP, "Field");
		loadMappings(METHOD_NAME_MAP, "Method");
	}

	private static void loadMappings(final @NotNull Map<String, String> map, final @NotNull String type)
	{
		InputStream fieldMappingStream = null;
		if (ServerType.isPaperCompatible() && MCVersion.isNewerOrEqualThan(MCVersion.MC_NMS_1_20_R4))
			fieldMappingStream = NMSReflectionRemapped.class.getClassLoader().getResourceAsStream("mappings/" + MCVersion.CURRENT_VERSION.getIdentifier() + "_Paper/" + type + "Mappings.txt");
		if (fieldMappingStream == null)
			fieldMappingStream = NMSReflectionRemapped.class.getClassLoader().getResourceAsStream("mappings/" + MCVersion.CURRENT_VERSION.getIdentifier() + "/" + type + "Mappings.txt");
		if(fieldMappingStream == null) return;
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(fieldMappingStream)))
		{
			String line;
			while((line = reader.readLine()) != null)
			{
				if(line.startsWith("#")) continue;
				String[] l = line.split(" ");
				if(l.length != 2) continue;
				map.put(l[0], l[1]);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public Class<?> findClass(@NotNull String className)
	{
		try(ZipInputStream zipStream = new ZipInputStream(Bukkit.getServer().getClass().getProtectionDomain().getCodeSource().getLocation().openStream()))
		{
			ZipEntry e;
			while((e = zipStream.getNextEntry()) != null)
			{
				String name = e.getName();
				if(name.startsWith("net/minecraft") && name.endsWith("/" + className + ".class"))
				{
					name = name.replace('/', '.');
					name = name.substring(0, name.length() - ".class".length());
					return Reflection.getClass(name);
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public @Nullable Class<?> getNmsClass(@NotNull String className)
	{
		Class<?> clazz = CLASS_MAP.get(className);
		if(clazz != null) return clazz;
		clazz = Reflection.getClassSilent(CLASS_NAME_MAP.getOrDefault(className, NMSReflection.NMS_CLASS_PATH + className));
		if(clazz == null) clazz = findClass(className);
		if(clazz == null) Reflection.getClass(className);
		if(clazz != null) CLASS_MAP.put(className, clazz);
		return clazz;
	}

	@Override
	@Contract("null,_,_->null")
	public @Nullable Method getNmsMethod(@Nullable Class<?> clazz, @NotNull String name, @NotNull Class<?>... args)
	{
		if(clazz == null) return null;
		String remapped = METHOD_NAME_MAP.get(clazz.getName() + "#" + name);
		if(remapped != null) name = remapped;
		return Reflection.getMethod(clazz, name, args);
	}

	@Override
	@Contract("null,_->null")
	public @Nullable Field getNmsField(@Nullable Class<?> clazz, @NotNull String name)
	{
		if(clazz == null) return null;
		String remapped = FIELD_NAME_MAP.get(clazz.getName() + "#" + name);
		if(remapped != null) name = remapped;
		return Reflection.getField(clazz, name);
	}

	@Override
	public @Nullable Enum<?> getNmsEnum(@NotNull String enumClassAndEnumName)
	{
		int i = enumClassAndEnumName.lastIndexOf('.');
		if(i < 1) throw new RuntimeException("Invalid class + enum name");
		return getNmsEnum(enumClassAndEnumName.substring(0, i), enumClassAndEnumName.substring(i + 1));
	}

	@Override
	public @Nullable Enum<?> getNmsEnum(@NotNull String enumClass, @NotNull String enumName)
	{
		Class<?> clazz = getNmsClass(enumClass);
		if(clazz == null) return null;
		String remapped = FIELD_NAME_MAP.get(clazz.getName() + "#" + enumName);
		if(remapped != null) enumName = remapped;
		return Reflection.getEnum(clazz, enumName);
	}
}