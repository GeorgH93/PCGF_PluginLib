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

	ChecksumType(@NotNull String algorithm)
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

	/**
	 * Checks if the system supports the checksum type.
	 *
	 * @return True if the checksum type is supported. False if not.
	 */
	public boolean isSupported()
	{
		return supported;
	}

	/**
	 * The algorithm for the {@see java.security.MessageDigest} hash provider.
	 *
	 * @return The algorithm name. Null for the NONE checksum type.
	 */
	public @Nullable String getAlgorithm()
	{
		return algorithm;
	}

	/**
	 * Gets an instance of the {@see java.security.MessageDigest} hash provider.
	 *
	 * @return The instance of the hash provider.
	 * @throws NoSuchAlgorithmException If the algorithm is not available on the system.
	 */
	public @NotNull MessageDigest getInstance() throws NoSuchAlgorithmException
	{
		return MessageDigest.getInstance(algorithm);
	}

	/**
	 * Gets an instance of the {@see java.security.MessageDigest} hash provider.
	 *
	 * @return The instance of the hash provider. Null if the algorithm is not available on the system.
	 */
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