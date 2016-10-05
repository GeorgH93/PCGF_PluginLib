/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.TestClasses.NMS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unused")
public class NBTTagCompound extends NBTBase
{
	Map<String, Object> elements = new HashMap<>();

	public void add(String name, Object value)
	{
		elements.put(name, value);
	}

	public void set(String name, Object value) { elements.put(name, value); }

	public void set(String name, NBTBase nbtBase) { elements.put(name, nbtBase); }

	public void setInt(String name, int value) { elements.put(name, value); }

	public int getInt(String name) { return (int) elements.get(name); }

	public NBTTagCompound getCompound(String name) { return (NBTTagCompound) elements.get(name); }

	public boolean hasKeyOfType(String name, int integer) { return elements.containsKey(name); }

	@Override
	public String toString()
	{
		if (elements.size() > 0)
		{
			Iterator<String> keys = elements.keySet().iterator();
			String key = keys.next();
			String json = "\"" + key + "\":\"" + elements.get(key) + "\"";
			while (keys.hasNext())
			{
				key = keys.next();
				json += ",\"" + key + "\":\"" + elements.get(key) + "\"";
			}
			return "{" + json + "}";
		}
		return "";
	}
}