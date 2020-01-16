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

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This enum allows to compare minecraft version. Useful for reflection and version depending stuff.
 */
@SuppressWarnings("unused")
public enum MCVersion
{
	UNKNOWN(0, -1, ""),
	MC_1_7(11, 3, "1_7", false),
	MC_1_7_1(11, 3, "1_7", MC_1_7, false),
	MC_1_7_2(11, 4, "1_7", MC_1_7, false),
	MC_1_7_3(11, 4, "1_7", MC_1_7, false),
	MC_1_7_4(11, 4, "1_7", MC_1_7, false),
	MC_NMS_1_7_R1(11, 4, "1_7", MC_1_7, false),
	MC_1_7_5(12, 5, "1_7", MC_1_7),
	MC_1_7_6(12, 5, "1_7", MC_1_7),
	MC_1_7_7(12, 5, "1_7", MC_1_7),
	MC_NMS_1_7_R2(12, 5, "1_7", MC_1_7),
	MC_1_7_8(13, 5, "1_7", MC_1_7),
	MC_1_7_9(13, 5, "1_7", MC_1_7),
	MC_NMS_1_7_R3(13, 5, "1_7", MC_1_7),
	MC_1_7_10(14, 5, "1_7", MC_1_7),
	MC_NMS_1_7_R4(14, 5, "1_7", MC_1_7),
	MC_1_8(21, 47, "1_8"),
	MC_1_8_1(21, 47, "1_8", MC_1_8),
	MC_1_8_2(21, 47, "1_8", MC_1_8),
	MC_NMS_1_8_R1(21, 47, "1_8", MC_1_8),
	MC_1_8_3(22, 47, "1_8", MC_1_8),
	MC_1_8_4(22, 47, "1_8", MC_1_8),
	MC_1_8_5(22, 47, "1_8", MC_1_8),
	MC_1_8_6(22, 47, "1_8", MC_1_8),
	MC_1_8_7(22, 47, "1_8", MC_1_8),
	MC_NMS_1_8_R2(22, 47, "1_8", MC_1_8),
	MC_1_8_8(23, 47, "1_8", MC_1_8),
	MC_1_8_9(23, 47, "1_8", MC_1_8),
	MC_NMS_1_8_R3(23, 47, "1_8", MC_1_8),
	MC_1_9(31, 107, "1_9"),
	MC_1_9_1(31, 108, "1_9", MC_1_9),
	MC_1_9_2(31, 109, "1_9", MC_1_9),
	MC_NMS_1_9_R1(31, 109, "1_9", MC_1_9),
	MC_1_9_3(32, 110, "1_9", MC_1_9),
	MC_1_9_4(32, 110, "1_9", MC_1_9),
	MC_NMS_1_9_R2(32, 110, "1_9", MC_1_9),
	MC_1_10(41, 210, "1_10"),
	MC_1_10_1(41, 210, "1_10", MC_1_10),
	MC_1_10_2(41, 210, "1_10", MC_1_10),
	MC_NMS_1_10_R1(41, 210, "1_10", MC_1_10),
	MC_1_11(51, 315, "1_11"),
	MC_1_11_1(51, 316, "1_11", MC_1_11),
	MC_1_11_2(51, 316, "1_11", MC_1_11),
	MC_NMS_1_11_R1(51, 316, "1_11"),
	MC_1_12(61, 335, "1_12"),
	MC_1_12_1(61, 338, "1_12", MC_1_12),
	MC_1_12_2(61,340, "1_12", MC_1_12),
	MC_NMS_1_12_R1(61, 340, "1_12", MC_1_12),
	MC_1_13(71, 393, "1_13"),
	MC_NMS_1_13_R1(71, 393, "1_13", MC_1_13),
	MC_1_13_1(72, 401, "1_13", MC_1_13),
	MC_1_13_2(72, 404, "1_13", MC_1_13),
	MC_NMS_1_13_R2(72, 404, "1_13", MC_1_13),
	MC_1_14(81, 477, "1_14"),
	MC_NMS_1_14_R1(81, 498, "1_14", MC_1_14),
	MC_1_14_1(81, 480, "1_14", MC_1_14),
	MC_1_14_2(81, 485, "1_14", MC_1_14),
	MC_1_14_3(81, 490, "1_14", MC_1_14),
	MC_1_14_4(81, 498, "1_14", MC_1_14),
	MC_1_15(91, 573, "1_15"),
	MC_1_15_1(91, 575, "1_15", MC_1_15),
	MC_NMS_1_15_R1(91, 575, "1_15", MC_1_15),
	MC_1_16(101, Integer.MAX_VALUE, "1_16"),
	MC_NMS_1_16_R1(101, Integer.MAX_VALUE, "1_16", MC_1_16);

	private static final Map<String, MCVersion> NMS_VERSION_MAP = new ConcurrentHashMap<>();
	private static final Map<Integer, MCVersion> PROTOCOL_VERSION_MAP = new ConcurrentHashMap<>();

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
			else
			{
				PROTOCOL_VERSION_MAP.put(version.protocolVersion, version);
			}
		}
		MCVersion currentVersion = UNKNOWN;
		try
		{
			currentVersion = getFromServerVersion(NMSReflection.getVersion());
		}
		catch(Throwable ignored)
		{
			System.out.println("Failed to obtain server version!");
		}
		CURRENT_VERSION = currentVersion;
	}

	private final int versionID;
	@Getter private final int protocolVersion;
	private final String identifier;
	private final MCVersion mainVersion;
	private final boolean supportsUUIDs;
	@Getter private final boolean dualWielding;

	MCVersion(int versionID, int protocolVersion, String mainVersionString)
	{
		this(versionID, protocolVersion, mainVersionString, true);
	}

	MCVersion(int versionID, int protocolVersion, String mainVersionString, boolean supportsUUIDs)
	{
		this.versionID = versionID;
		this.protocolVersion = protocolVersion;
		this.identifier = mainVersionString + "_R" + versionID % 10;
		this.mainVersion = this;
		this.supportsUUIDs = supportsUUIDs;
		dualWielding = protocolVersion >= 107;
	}

	MCVersion(int versionID, int protocolVersion, String mainVersionString, MCVersion mainVersion)
	{
		this(versionID, protocolVersion, mainVersionString, mainVersion, true);
	}

	MCVersion(int versionID, int protocolVersion, String mainVersionString, MCVersion mainVersion, boolean supportsUUIDs)
	{
		this.versionID = versionID;
		this.protocolVersion = protocolVersion;
		this.identifier = mainVersionString + "_R" + versionID % 10;
		this.mainVersion = mainVersion;
		this.supportsUUIDs = supportsUUIDs;
		dualWielding = protocolVersion >= 107;
	}

	public MCVersion getMajorMinecraftVersion()
	{
		return mainVersion;
	}

	public boolean areUUIDsSupported()
	{
		return supportsUUIDs;
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

	/**
	 * Checks weather the given version is from the same major MC version.
	 * e.g. MC 1.7.2 and MC 1.7.10 are both MC 1.7 and will result in true.
	 * while MC 1.7.10 and MC 1.8.8 are MC 1.7 and MC 1.8 and will therefor result in false.
	 *
	 * @param other The other version to compare with
	 * @return True if both are from the same major MC version. false if not.
	 */
	public boolean isSameMajorVersion(MCVersion other)
	{
		return this.versionID / 10 == other.versionID / 10;
	}

	/**
	 * Checks weather the given version is from the same major MC version as the currently running server version.
	 * e.g. MC 1.7.2 and MC 1.7.10 are both MC 1.7 and will result in true.
	 * while MC 1.7.10 and MC 1.8.8 are MC 1.7 and MC 1.8 and will therefor result in false.
	 *
	 * @param other The other version to compare with
	 * @return True if both are from the same major MC version. false if not.
	 */
	public static boolean isAny(MCVersion other)
	{
		return CURRENT_VERSION.isSameMajorVersion(other);
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

	public static boolean isUUIDsSupportAvailable() { return CURRENT_VERSION.areUUIDsSupported(); }

	public static boolean isDualWieldingMC() { return CURRENT_VERSION.isDualWielding(); }

	public static @NotNull MCVersion getFromServerVersion(final @NotNull String serverVersion)
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

	public static @NotNull MCVersion getFromProtocolVersion(final int protocolVersion)
	{
		MCVersion version = PROTOCOL_VERSION_MAP.get(protocolVersion);
		if(version == null) version = UNKNOWN;
		return version;
	}
}