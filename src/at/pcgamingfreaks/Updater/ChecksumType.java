/*
 *   Copyright (C) 2018 GeorgH93
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

package at.pcgamingfreaks.Updater;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum ChecksumType
{
	NONE,
	MD5("MD5"),
	SHA1("SHA1"),
	SHA224("SHA-224"),
	SHA256("SHA-256"),
	SHA384("SHA-384"),
	SHA512("SHA-512");

	private final String algorithm;
	private final boolean supported;

	ChecksumType()
	{
		supported = false;
		algorithm = null;
	}

	ChecksumType(@NonNls String algorithm)
	{
		this.algorithm = algorithm;
		boolean ok = false;
		try
		{
			MessageDigest.getInstance(algorithm);
			ok = true;
		}
		catch(NoSuchAlgorithmException e)
		{
			System.out.println("Hashing algorithm " + algorithm + " is not available on your system.");
		}
		supported = ok;
	}

	public boolean isSupported()
	{
		return supported;
	}

	public String getAlgorithm()
	{
		return algorithm;
	}

	public @NotNull MessageDigest getInstance() throws NoSuchAlgorithmException
	{
		return MessageDigest.getInstance(algorithm);
	}

	public @Nullable MessageDigest getInstanceOrNull()
	{
		if(!supported) return null;
		try
		{
			return getInstance();
		}
		catch(NoSuchAlgorithmException ignored) {}
		return null;
	}
}