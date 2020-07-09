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

package at.pcgamingfreaks.Message.Sender;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface ISendMethod
{
	/**
	 * Gets the metadata class for the used option.
	 *
	 * @return The metadata class.
	 */
	@Nullable Class<?> getMetadataClass();

	/**
	 * Gets the method to convert a JSON in a metadata object.
	 *
	 * @return The static method to convert a JSON in a metadata object.
	 */
	@Nullable Method getMetadataFromJsonMethod();

	default @Nullable Object parseMetadata(String metadataJson) throws InvocationTargetException, IllegalAccessException
	{
		if(getMetadataFromJsonMethod() == null) return null;
		return getMetadataFromJsonMethod().invoke(null, metadataJson);
	}
}