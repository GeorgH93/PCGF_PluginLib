/*
 *   Copyright (C) 2024 GeorgH93
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

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This enum allows comparing minecraft version. Useful for reflection and features only available in certain minecraft versions.
 */
public enum MCVersion
{
	UNKNOWN(0, -1, "", "UNKNOWN"),
	MC_1_7(11, 3, "1_7", "1.7", false),
	MC_1_7_1(11, 3, "1_7", "1.7.1", MC_1_7, false),
	MC_1_7_2(11, 4, "1_7", "1.7.2", MC_1_7, false),
	MC_1_7_3(11, 4, "1_7", "1.7.3", MC_1_7, false),
	MC_1_7_4(11, 4, "1_7", "1.7.4", MC_1_7, false),
	MC_NMS_1_7_R1(11, 4, "1_7", "1.7_NMS_R1", MC_1_7, false),
	MC_1_7_5(12, 5, "1_7", "1.7.5", MC_1_7),
	MC_1_7_6(12, 5, "1_7", "1.7.6", MC_1_7),
	MC_1_7_7(12, 5, "1_7", "1.7.7", MC_1_7),
	MC_NMS_1_7_R2(12, 5, "1_7", "1.7_NMS_R2", MC_1_7),
	MC_1_7_8(13, 5, "1_7", "1.7.8", MC_1_7),
	MC_1_7_9(13, 5, "1_7", "1.7.9", MC_1_7),
	MC_NMS_1_7_R3(13, 5, "1_7", "1.7_NMS_R3", MC_1_7),
	MC_1_7_10(14, 5, "1_7", "1.7.10", MC_1_7),
	MC_NMS_1_7_R4(14, 5, "1_7", "1.7_NMS_R4", MC_1_7),
	MC_1_8(21, 47, "1_8", "1.8"),
	MC_1_8_1(21, 47, "1_8", "1.8.1", MC_1_8),
	MC_1_8_2(21, 47, "1_8", "1.8.2", MC_1_8),
	MC_NMS_1_8_R1(21, 47, "1_8", "1.8_NMS_R1", MC_1_8),
	MC_1_8_3(22, 47, "1_8", "1.8.3", MC_1_8),
	MC_1_8_4(22, 47, "1_8", "1.8.4", MC_1_8),
	MC_1_8_5(22, 47, "1_8", "1.8.5", MC_1_8),
	MC_1_8_6(22, 47, "1_8", "1.8.6", MC_1_8),
	MC_1_8_7(22, 47, "1_8", "1.8.7", MC_1_8),
	MC_NMS_1_8_R2(22, 47, "1_8", "1.8_NMS_R2", MC_1_8),
	MC_1_8_8(23, 47, "1_8", "1.8.8", MC_1_8),
	MC_1_8_9(23, 47, "1_8", "1.8.9", MC_1_8),
	MC_NMS_1_8_R3(23, 47, "1_8", "1.8_NMS_R3", MC_1_8),
	MC_1_9(31, 107, "1_9", "1.9"),
	MC_1_9_1(31, 108, "1_9", "1.9.1", MC_1_9),
	MC_1_9_2(31, 109, "1_9", "1.9.2", MC_1_9),
	MC_NMS_1_9_R1(31, 109, "1_9", "1.9_NMS_R1", MC_1_9),
	MC_1_9_3(32, 110, "1_9", "1.9.3", MC_1_9),
	MC_1_9_4(32, 110, "1_9", "1.9.4", MC_1_9),
	MC_NMS_1_9_R2(32, 110, "1_9", "1.9_NMS_R2", MC_1_9),
	MC_1_10(41, 210, "1_10", "1.10"),
	MC_1_10_1(41, 210, "1_10", "1.10.1", MC_1_10),
	MC_1_10_2(41, 210, "1_10", "1.10.2", MC_1_10),
	MC_NMS_1_10_R1(41, 210, "1_10", "1.10_NMS_R1", MC_1_10),
	MC_1_11(51, 315, "1_11", "1.11"),
	MC_1_11_1(51, 316, "1_11", "1.11.1", MC_1_11),
	MC_1_11_2(51, 316, "1_11", "1.11.2", MC_1_11),
	MC_NMS_1_11_R1(51, 316, "1_11", "1.11_NMS_R1"),
	MC_1_12(61, 335, "1_12", "1.12"),
	MC_1_12_1(61, 338, "1_12", "1.12.1", MC_1_12),
	MC_1_12_2(61, 340, "1_12", "1.12.2", MC_1_12),
	MC_NMS_1_12_R1(61, 340, "1_12", "1.12_NMS_R1", MC_1_12),
	MC_1_13(71, 393, "1_13", "1.13"),
	MC_NMS_1_13_R1(71, 393, "1_13", "1.13_NMS_R1", MC_1_13),
	MC_1_13_1(72, 401, "1_13", "1.13.1", MC_1_13),
	MC_1_13_2(72, 404, "1_13", "1.13.2", MC_1_13),
	MC_NMS_1_13_R2(72, 404, "1_13", "1.13_NMS_R2", MC_1_13),
	MC_1_14(81, 477, "1_14", "1.14"),
	MC_NMS_1_14_R1(81, 498, "1_14", "1.14_NMS_R1", MC_1_14),
	MC_1_14_1(81, 480, "1_14", "1.14.1", MC_1_14),
	MC_1_14_2(81, 485, "1_14", "1.14.2", MC_1_14),
	MC_1_14_3(81, 490, "1_14", "1.14.3", MC_1_14),
	MC_1_14_4(81, 498, "1_14", "1.14.4", MC_1_14),
	MC_1_15(91, 573, "1_15", "1.15"),
	MC_1_15_1(91, 575, "1_15", "1.15.1", MC_1_15),
	MC_1_15_2(91, 578, "1_15", "1.15.2", MC_1_15),
	MC_NMS_1_15_R1(91, 578, "1_15", "1.15_NMS_R1", MC_1_15),
	MC_1_16(101, 735, "1_16", "1.16"),
	MC_1_16_1(101, 736, "1_16", "1.16.1", MC_1_16),
	MC_NMS_1_16_R1(101, 736, "1_16", "1.16_NMS_R1", MC_1_16),
	MC_1_16_2(102, 751, "1_16", "1.16.2", MC_1_16),
	MC_1_16_3(102, 753, "1_16", "1.16.3", MC_1_16),
	MC_NMS_1_16_R2(102, 751, "1_16", "1.16_NMS_R2", MC_1_16),
	MC_1_16_4(103, 754, "1_16", "1.16.4", MC_1_16),
	MC_1_16_5(103, 754, "1_16", "1.16.5", MC_1_16),
	MC_NMS_1_16_R3(103, 754, "1_16", "1.16_NMS_R3", MC_1_16),
	MC_1_17(111, 755, "1_17", "1.17"),
	MC_1_17_1(111, 756, "1_17", "1.17.1", MC_1_17),
	MC_NMS_1_17_R1(111, 756, "1_17", "1.17_NMS_R1", MC_1_17),
	MC_1_18(121, 757, "1_18", "1.18"),
	MC_1_18_1(121, 757, "1_18", "1.18.1"),
	MC_NMS_1_18_R1(121, 757, "1_18", "1.18_NMS_R1", MC_1_18),
	MC_1_18_2(122, 758, "1_18", "1.18.2"),
	MC_NMS_1_18_R2(122, 758, "1_18", "1.18_NMS_R2", MC_1_18),
	MC_1_19(131, 759, "1_19", "1.19"),
	MC_1_19_1(131, 760, "1_19", "1.19.1", "1"),
	MC_1_19_2(131, 761, "1_19", "1.19.2", "1"),
	MC_NMS_1_19_R1(131, 759, "1_19", "1.19_NMS_R1", MC_1_19),
	MC_NMS_1_19_R1_1(131, 760, "1_19", "1.19_NMS_R1_1", MC_1_19),
	MC_1_19_3(132, 761, "1_19", "1.19.3"),
	MC_NMS_1_19_R2(132, 761, "1_19", "1.19_NMS_R2", MC_1_19),
	MC_1_19_4(133, 762, "1_19", "1.19.4"),
	MC_NMS_1_19_R3(133, 762, "1_19", "1.19_NMS_R3", MC_1_19),
	MC_1_20(141, 763, "1_20", "1.20"),
	MC_1_20_1(141, 763, "1_20", "1.20.1"),
	MC_NMS_1_20_R1(141, 763, "1_20", "1.20_NMS_R1", MC_1_20),
	MC_1_20_2(142, 764, "1_20", "1.20.2"),
	MC_NMS_1_20_R2(142, 764, "1_20", "1.20_NMS_R2", MC_1_20),
	MC_1_20_3(143, 765, "1_20", "1.20.3"),
	MC_1_20_4(143, 765, "1_20", "1.20.4"),
	MC_NMS_1_20_R3(143, 766, "1_20", "1.20_NMS_R3", MC_1_20),
	MC_1_20_5(144, 766, "1_20", "1.20.5"),
	MC_1_20_6(144, 766, "1_20", "1.20.6"),
	MC_NMS_1_20_R4(144, 766, "1_20", "1.20_NMS_R4", MC_1_20),
	MC_1_21(151, 767, "1_21", "1.21"),
	MC_1_21_1(151, 767, "1_21", "1.21.1"),
	MC_NMS_1_21_R1(151, 767, "1_21", "1.21_NMS_R1", MC_1_21),
	MC_1_21_2(152, 768, "1_21", "1.21.2"),
	MC_1_21_3(152, 768, "1_21", "1.21.3"),
	MC_NMS_1_21_R2(152, 768, "1_21", "1.21_NMS_R2", MC_1_21),
	MC_1_21_4(153, 769, "1_21", "1.21.4"),
	MC_NMS_1_21_R3(153, 769, "1_21", "1.21_NMS_R3", MC_1_21),
	MC_1_22(161, Integer.MAX_VALUE, "1_22", "1.22"),
	MC_NMS_1_22_R1(161, Integer.MAX_VALUE, "1_22", "1.22_NMS_R1", MC_1_22);

	private static final Map<String, MCVersion> VERSION_MAP = new HashMap<>();
	private static final Map<String, MCVersion> NMS_VERSION_MAP = new HashMap<>();
	private static final Map<Integer, MCVersion> PROTOCOL_VERSION_MAP = new HashMap<>();

	private static final Pattern VERSION_PATTERN = Pattern.compile("\\(MC: (?<version>\\d(.\\d{1,5}){1,2})\\)");

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
				VERSION_MAP.put(version.name, version);
				PROTOCOL_VERSION_MAP.put(version.protocolVersion, version);
			}
		}
		MCVersion currentVersion = UNKNOWN;
		try
		{
			Matcher matcher = VERSION_PATTERN.matcher(Bukkit.getVersion());
			if(matcher.find())
			{
				currentVersion = getFromVersionName(matcher.group("version"));
			}
			if(currentVersion == UNKNOWN)
			{ // Could not identify version from API, trying to use OBC
				currentVersion = getFromServerVersion(Bukkit.getServer().getClass().getName().split("\\.")[3]);
			}
		}
		catch(Throwable ignored)
		{
			System.out.println("Failed to obtain server version!");
		}
		CURRENT_VERSION = currentVersion;
	}

	public static Collection<MCVersion> getProtocolVersions()
	{
		return PROTOCOL_VERSION_MAP.values();
	}

	public static Collection<MCVersion> getVersions()
	{
		return VERSION_MAP.values();
	}

	private final int versionID;
	@Getter private final int protocolVersion;
	@Getter private final String identifier;
	private final MCVersion mainVersion;
	private final boolean supportsUUIDs, supportsRgbColors;
	@Getter private final boolean dualWielding;
	@Getter private final String name;

	MCVersion(int versionID, int protocolVersion, String mainVersionString, String versionString)
	{
		this(versionID, protocolVersion, mainVersionString, versionString, true);
	}

	MCVersion(int versionID, int protocolVersion, String mainVersionString, String versionString, String nmsPatch)
	{
		this(versionID, protocolVersion, mainVersionString, versionString, true, nmsPatch);
	}

	MCVersion(int versionID, int protocolVersion, String mainVersionString, String versionString, boolean supportsUUIDs)
	{
		this(versionID, protocolVersion, mainVersionString, versionString, supportsUUIDs, "");
	}

	MCVersion(int versionID, int protocolVersion, String mainVersionString, String versionString, MCVersion mainVersion)
	{
		this(versionID, protocolVersion, mainVersionString, versionString, mainVersion, true);
	}

	MCVersion(int versionID, int protocolVersion, String mainVersionString, String versionString, MCVersion mainVersion, boolean supportsUUIDs)
	{
		this(versionID, protocolVersion, mainVersionString, versionString, mainVersion, supportsUUIDs, "");
	}

	private static String buildIdentifier(String mainVersionString, int versionID, String nmsPatch)
	{
		return mainVersionString + "_R" + versionID % 10 + (nmsPatch.isEmpty() ? "" : "_" + nmsPatch);
	}

	MCVersion(int versionID, int protocolVersion, String mainVersionString, String versionString, boolean supportsUUIDs, String nmsPatch)
	{
		this.versionID = versionID;
		this.protocolVersion = protocolVersion;
		this.identifier = buildIdentifier(mainVersionString, versionID, nmsPatch);
		this.mainVersion = this;
		this.supportsUUIDs = supportsUUIDs;
		this.name = versionString;
		dualWielding = protocolVersion >= 107;
		supportsRgbColors = protocolVersion >= 735;
	}

	MCVersion(int versionID, int protocolVersion, String mainVersionString, String versionString, MCVersion mainVersion, boolean supportsUUIDs, String nmsPatch)
	{
		this.versionID = versionID;
		this.protocolVersion = protocolVersion;
		this.identifier = buildIdentifier(mainVersionString, versionID, nmsPatch);
		this.mainVersion = mainVersion;
		this.supportsUUIDs = supportsUUIDs;
		this.name = versionString;
		dualWielding = protocolVersion >= 107;
		supportsRgbColors = protocolVersion >= 735;
	}

	public MCVersion getMajorMinecraftVersion()
	{
		return mainVersion;
	}

	public boolean areUUIDsSupported()
	{
		return supportsUUIDs;
	}

	public boolean isRgbColorSupported() { return supportsRgbColors; }

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
	 * while MC 1.7.10 and MC 1.8.8 are MC 1.7 and MC 1.8 and will therefore result in false.
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
	 * while MC 1.7.10 and MC 1.8.8 are MC 1.7 and MC 1.8 and will therefore result in false.
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

	public static boolean supportsRgbColors() { return CURRENT_VERSION.isRgbColorSupported(); }

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

	public static @NotNull MCVersion getFromVersionName(final String versionName)
	{
		MCVersion version = VERSION_MAP.get(versionName);
		if(version == null) version = UNKNOWN;
		return version;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
