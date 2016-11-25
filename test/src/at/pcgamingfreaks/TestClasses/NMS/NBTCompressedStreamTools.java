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

import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("unused")
public class NBTCompressedStreamTools
{
	public static void a(NBTTagCompound tagCompound, OutputStream outputStream) {}

	public static NBTTagCompound a(InputStream inputStream)
	{
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.add("size", 10);
		NBTTagCompound itemStack = new NBTTagCompound();
		itemStack.add("id", 0);
		itemStack.add("Count", 20);
		tagCompound.add("0", itemStack);
		return tagCompound;
	}

	public static NBTTagCompound fakeFunction(InputStream inputStream)
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.set("size", 1);
		return compound;
	}
}