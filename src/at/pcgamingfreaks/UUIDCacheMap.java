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

package at.pcgamingfreaks;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This map implements a {@link ConcurrentHashMap} to store names case insensitive as key and UUIDs as String without the "-" separator.
 * It's used to cache the UUID's resolved by the {@link UUIDConverter}.
 */
class UUIDCacheMap extends ConcurrentHashMap<String, String>
{
	@Override
	public String put(@NotNull String key, @NotNull String value)
	{
		Validate.notNull(key);
		Validate.notNull(value);
		return super.put(key.toLowerCase(), value.replaceAll("-", "").toLowerCase());
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends String> m)
	{
		Validate.notNull(m);
		for(Entry<? extends String, ? extends String> entry : m.entrySet())
		{
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public boolean contains(@NotNull Object value)
	{
		Validate.notNull(value);
		return value instanceof String && super.contains(((String) value).replaceAll("-", "").toLowerCase());
	}

	@Override
	public boolean containsKey(@NotNull Object key)
	{
		Validate.notNull(key);
		return key instanceof String && super.containsKey(((String) key).toLowerCase());
	}

	@Override
	public String get(@NotNull Object key)
	{
		Validate.notNull(key);
		return key instanceof String ? super.get(((String) key).toLowerCase()) : null;
	}

	@Override
	public String remove(@NotNull Object key)
	{
		Validate.notNull(key);
		return key instanceof String ? super.remove(((String) key).toLowerCase()) : null;
	}
}