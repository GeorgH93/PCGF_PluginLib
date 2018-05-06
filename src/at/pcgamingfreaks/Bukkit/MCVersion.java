/*
 *   Copyright (C) 2016-2018 GeorgH93
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This enum allows to compare minecraft version. Useful for reflection and version depending stuff.
 */
@SuppressWarnings("unused")
public enum MCVersion
{
	UNKNOWN(0, ""),
	MC_1_7(1, "1_7"),
	MC_1_7_1(1, "1_7"),
	MC_1_7_2(1, "1_7"),
	MC_1_7_3(1, "1_7"),
	MC_1_7_4(1, "1_7"),
	MC_NMS_1_7_R1(1, "1_7"),
	MC_1_7_5(2, "1_7"),
	MC_1_7_6(2, "1_7"),
	MC_1_7_7(2, "1_7"),
	MC_NMS_1_7_R2(2, "1_7"),
	MC_1_7_8(3, "1_7"),
	MC_1_7_9(3, "1_7"),
	MC_NMS_1_7_R3(3, "1_7"),
	MC_1_7_10(4, "1_7"),
	MC_NMS_1_7_R4(4, "1_7"),
	MC_1_8(11, "1_8"),
	MC_1_8_1(11, "1_8"),
	MC_1_8_2(11, "1_8"),
	MC_NMS_1_8_R1(11, "1_8"),
	MC_1_8_3(12, "1_8"),
	MC_1_8_4(12, "1_8"),
	MC_1_8_5(12, "1_8"),
	MC_1_8_6(12, "1_8"),
	MC_1_8_7(12, "1_8"),
	MC_NMS_1_8_R2(12, "1_8"),
	MC_1_8_8(13, "1_8"),
	MC_1_8_9(13, "1_8"),
	MC_NMS_1_8_R3(13, "1_8"),
	MC_1_9(21, "1_9"),
	MC_1_9_1(21, "1_9"),
	MC_1_9_2(21, "1_9"),
	MC_NMS_1_9_R1(21, "1_9"),
	MC_1_9_3(21, "1_9"),
	MC_1_9_4(21, "1_9"),
	MC_NMS_1_9_R2(22, "1_9"),
	MC_1_10(31, "1_10"),
	MC_1_10_1(31, "1_10"),
	MC_1_10_2(31, "1_10"),
	MC_NMS_1_10_R1(31, "1_10"),
	MC_1_11(41, "1_11"),
	MC_1_11_1(41, "1_11"),
	MC_1_11_2(41, "1_11"),
	MC_NMS_1_11_R1(41, "1_11"),
	MC_1_12(51, "1_12"),
	MC_NMS_1_12_R1(51, "1_12"),
	MC_1_13(61, "1_13"),
	MC_NMS_1_13_R1(61, "1_13");

	private static final Map<String, MCVersion> NMS_VERSION_MAP = new ConcurrentHashMap<>();

	/**
	 * The current version of the minecraft server.
	 */
	public static final MCVersion CURRENT_VERSION;

	static
	{
		for (MCVersion version : values())
		{
			if(version.name().contains("NMS"))
			{
				MCVersion.NMS_VERSION_MAP.put(version.identifier, version);
			}
		}
		CURRENT_VERSION = getFromServerVersion(NMSReflection.getVersion());
	}

	private final int versionID;
	private final String identifier;

	MCVersion(int versionID, String mainVersionString)
	{
		this.versionID = versionID;
		this.identifier = mainVersionString + "_R" + versionID % 10;
	}

	public boolean isSame(MCVersion other)
	{
		return this.versionID == other.versionID;
	}

	public boolean newerThan(MCVersion other)
	{
		return this.versionID > other.versionID && other != UNKNOWN;
	}

	public boolean newerOrEqualThan(MCVersion other)
	{
		return isSame(other) || newerThan(other);
	}

	public boolean olderThan(MCVersion other)
	{
		return this.versionID < other.versionID && this != UNKNOWN;
	}

	public boolean olderOrEqualThan(MCVersion other)
	{
		return isSame(other) || olderThan(other);
	}

	public static boolean is(MCVersion other)
	{
		return CURRENT_VERSION.versionID == other.versionID;
	}

	public static boolean isNewerThan(MCVersion other)
	{
		return CURRENT_VERSION.versionID > other.versionID && other != UNKNOWN;
	}

	public static boolean isNewerOrEqualThan(MCVersion other)
	{
		return is(other) || isNewerThan(other);
	}

	public static boolean isOlderThan(MCVersion other)
	{
		return CURRENT_VERSION.versionID < other.versionID && CURRENT_VERSION != UNKNOWN;
	}

	public static boolean isOlderOrEqualThan(MCVersion other)
	{
		return is(other) || isOlderThan(other);
	}

	public static @Nullable MCVersion getFromServerVersion(@NotNull String serverVersion)
	{
		for(Map.Entry<String, MCVersion> entry : NMS_VERSION_MAP.entrySet())
		{
			if(serverVersion.contains(entry.getKey()))
			{
				return entry.getValue();
			}
		}
		return UNKNOWN;
	}
}