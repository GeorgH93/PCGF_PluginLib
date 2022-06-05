/*
 *   Copyright (C) 2022 GeorgH93
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

package at.pcgamingfreaks;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version>
{ //TODO support 1.0a and 1.0b (a, b) as alpha/beta markers
	public static final String VERSION_STING_FORMAT = "[vV]?(?<version>\\d+(\\.\\d+)*)(?<tags>(-[^-\\s]+)*)";
	private static final List<String> EMPTY_TAG_LIST = new ArrayList<>(0);
	private static final byte SAME = 0, OLDER = -1, NEWER = 1;
	private static final String VERSION_SPLIT_REGEX = "\\.", TAG_SPLIT_REGEX = "-", UNIMPORTANT_VERSION_PARTS_REGEX = "(\\.0+)*+$", PRE_RELEASE_TAG_FORMAT = "(?<tag>\\w+)\\.?(?<number>\\d+)";
	private static final Pattern PRE_RELEASE_TAG_FORMAT_PATTERN = Pattern.compile(PRE_RELEASE_TAG_FORMAT), VERSION_STING_FORMAT_PATTERN = Pattern.compile(VERSION_STING_FORMAT);
	private static final String[] PRE_RELEASE_TAGS = new String[] { "alpha", "beta", "pre", "rc", "snapshot"};
	private static final Map<String, Integer> PRE_RELEASE_TAG_VALUE_RESOLUTION = new ConcurrentHashMap<>();

	static
	{
		for(int i = 0; i < PRE_RELEASE_TAGS.length; i++)
		{
			PRE_RELEASE_TAG_VALUE_RESOLUTION.put(PRE_RELEASE_TAGS[i], (PRE_RELEASE_TAGS.length + 1 - i) * 10);
		}
	}

	private final String rawVersion;
	@SuppressWarnings("FieldCanBeLocal")
	private final String[] tags;
	private final int[] versionComponents;
	private final int hashCode;
	private final long timestamp, buildNumber;
	private final boolean preRelease;

	public Version(int majorVersion)
	{
		this("" + majorVersion);
	}

	public Version(int majorVersion, int minorVersion)
	{
		this(majorVersion + "." + minorVersion);
	}

	public Version(final int majorVersion, final int minorVersion, final int patchVersion)
	{
		this(majorVersion + "." + minorVersion + "." + patchVersion);
	}

	/**
	 * @param version A string representing this version. Must be in the format: [vV]?\d+(\.\d+)*(-[^-\s]+)*
	 * @throws InvalidVersionStringException The exception is thrown when the given string doesn't match the required format.
	 */
	public Version(final @NotNull String version) throws InvalidVersionStringException
	{
		this(version, false);
	}

	/**
	 * @param rawVersion A string representing this version. Must be in the format: [vV]?\d+(\.\d+)*(-[^-\s]+)*
	 * @param ignoreTags Ignores tags like -alpha for the version comparison.
	 * @throws InvalidVersionStringException The exception is thrown when the given string doesn't match the required format.
	 */
	public Version(final @NotNull String rawVersion, final boolean ignoreTags) throws InvalidVersionStringException
	{
		Matcher versionMatcher = validateInput(rawVersion);
		this.rawVersion = (rawVersion.startsWith("v") || rawVersion.startsWith("V")) ? rawVersion.substring(1) : rawVersion;
		final String version = versionMatcher.group("version").replaceAll(UNIMPORTANT_VERSION_PARTS_REGEX, "");
		// Prepare data
		this.tags = versionMatcher.group("tags").split(TAG_SPLIT_REGEX); // Split the tags
		String[] comps = version.split(VERSION_SPLIT_REGEX);
		List<String> tagsList = (!ignoreTags) ? getAll(this.tags, PRE_RELEASE_TAGS) : EMPTY_TAG_LIST;
		boolean notAFinalVersion = !tagsList.isEmpty();
		this.versionComponents = new int[notAFinalVersion ? comps.length + 1 : comps.length];
		for(int i = 0; i < comps.length; i++)
		{
			this.versionComponents[i] = Integer.parseInt(comps[i]);
		}
		if(notAFinalVersion)
		{
			this.preRelease = true;
			int last = 0;
			for(String str : tagsList)
			{
				if(last == 0) last = Integer.MAX_VALUE;
				int preReleaseTagNumber = 0;
				String tag = str.toLowerCase(Locale.ENGLISH);
				Matcher preMatcher = PRE_RELEASE_TAG_FORMAT_PATTERN.matcher(tag);
				if(preMatcher.matches())
				{
					preReleaseTagNumber = Integer.parseInt(preMatcher.group("number"));
					tag = preMatcher.group("tag");
				}
				last = (last - PRE_RELEASE_TAG_VALUE_RESOLUTION.get(tag)) + preReleaseTagNumber;
			}
			this.versionComponents[this.versionComponents.length - 1] = last;
			if(last > 0)
			{
				for(int i = this.versionComponents.length - 2; i >= 0; i--)
				{
					if(this.versionComponents[i] > 0 || i == 0)
					{
						this.versionComponents[i]--;
						break;
					}
				}
			}
		}
		else
		{
			this.preRelease = false;
		}

		timestamp = getBuildParameter(this.tags, "(t|ts|time(stamp)?)");
		buildNumber = getBuildParameter(this.tags, "(b|build(number)?)");
		this.hashCode = Arrays.hashCode(this.versionComponents);
	}

	private static Matcher validateInput(final String version)
	{
		if(version == null) throw new InvalidVersionStringException("The version string must not be null!");
		Matcher versionMatcher = VERSION_STING_FORMAT_PATTERN.matcher(version);
		if(!versionMatcher.matches()) throw new InvalidVersionStringException();
		return versionMatcher;
	}

	private static long getBuildParameter(@NotNull String[] tags, @NotNull String parameter)
	{
		Pattern searchPattern = Pattern.compile(parameter + "[=:_]?(?<number>\\d+)", Pattern.CASE_INSENSITIVE);
		for(String tag : tags)
		{
			Matcher matcher = searchPattern.matcher(tag);
			if(matcher.matches())
			{
				try
				{
					return Long.parseLong(matcher.group("number"));
				}
				catch(NumberFormatException ignored) {}
			}
		}
		return -1;
	}

	private static @NotNull List<String> getAll(@NotNull String[] source, @NotNull String[] searchForArray)
	{
		List<String> result = new ArrayList<>();
		for(String searchFor : searchForArray)
		{
			result.addAll(StringUtils.getAllContainingIgnoreCase(source, searchFor));
		}
		return result;
	}

	/**
	 * Checks if the given version string matches the required format.
	 *
	 * @param version The String to check.
	 * @return True if the string matches the format. False if not.
	 */
	public static boolean isValidVersionString(@NotNull String version)
	{
		return VERSION_STING_FORMAT_PATTERN.matcher(version).matches();
	}

	private byte compareLength(int c, @NotNull Version otherVersion)
	{
		if(this.versionComponents.length != otherVersion.versionComponents.length)
		{
			boolean otherLonger = otherVersion.versionComponents.length > this.versionComponents.length;
			int[] longer = (otherLonger) ? otherVersion.versionComponents : this.versionComponents;
			for(int i = c; i < longer.length; i++)
			{
				if(longer[i] > 0)
				{
					return ((otherLonger) ? OLDER : NEWER);
				}
			}
		}
		return SAME;
	}

	private byte compareTimeStamp(final long otherTimeStamp)
	{
		if(this.timestamp > 0 && otherTimeStamp > 0)
		{
			if(this.timestamp > otherTimeStamp)
			{
				return NEWER;
			}
			else if(this.timestamp < otherTimeStamp)
			{
				return OLDER;
			}
		}
		return SAME;
	}

	private byte compareBuildNumber(final long otherBuildNumber)
	{
		if(this.buildNumber > 0 && otherBuildNumber > 0)
		{
			if(this.buildNumber > otherBuildNumber)
			{
				return NEWER;
			}
			else if(this.buildNumber < otherBuildNumber)
			{
				return OLDER;
			}
		}
		return SAME;
	}

	/**
	 * Compares two version with each other.
	 *
	 * @param otherVersion The version to compare with.
	 * @return -1 this older than otherVersion, 0 equals, 1 this newer than otherVersion
	 */
	private byte compare(@NotNull Version otherVersion)
	{
		int c = Math.min(this.versionComponents.length, otherVersion.versionComponents.length);
		for(int i = 0; i < c; i++)
		{
			if(otherVersion.versionComponents[i] > this.versionComponents[i])
			{
				return OLDER;
			}
			else if(otherVersion.versionComponents[i] < this.versionComponents[i])
			{
				return NEWER;
			}
		}
		// If both version are the same for the length, the version that has more digits (>0) probably is the newer one.
		final byte lengthResult = compareLength(c, otherVersion);
		if (lengthResult != SAME) return lengthResult;
		// If both versions have the same length we still can compare the build timestamp (if available)
		final byte timeStampResult = compareTimeStamp(otherVersion.timestamp);
		if (timeStampResult != SAME) return timeStampResult;
		// If both versions still can't be distinguished we can use the build number (if available)
		return compareBuildNumber(otherVersion.buildNumber);
	}

	/**
	 * Checks if the version is a pre-release version or not.
	 *
	 * @return True if the version is a pre-release version. False if not.
	 */
	public boolean isPreRelease()
	{
		return preRelease;
	}

	//region Comparison functions
	/**
	 * Checks if the version is newer than the given version.
	 *
	 * @param otherVersion The version to compare with.
	 * @return True if the version is newer, false if not.
	 */
	public boolean newerThan(@NotNull Version otherVersion)
	{
		return compare(otherVersion) == NEWER;
	}

	/**
	 * Checks if the version is newer or the same as the given version.
	 *
	 * @param otherVersion The version to compare with.
	 * @return True if the version is newer or the same, false if not.
	 */
	public boolean newerOrEqualThan(@NotNull Version otherVersion)
	{
		return compare(otherVersion)>= SAME;
	}

	/**
	 * Checks if the version is older than the given version.
	 *
	 * @param otherVersion The version to compare with.
	 * @return True if the version is older, false if not.
	 */
	public boolean olderThan(@NotNull Version otherVersion)
	{
		return compare(otherVersion) == OLDER;
	}

	/**
	 * Checks if the version is older or the same as the given version.
	 *
	 * @param otherVersion The version to compare with.
	 * @return True if the version is older or the same, false if not.
	 */
	public boolean olderOrEqualThan(@NotNull Version otherVersion)
	{
		return compare(otherVersion) <= SAME;
	}

	//region String comparison functions
	/**
	 * Checks if the version is newer than the given version.
	 *
	 * @param otherVersion The version to compare with.
	 * @return True if the version is newer, false if not.
	 */
	public boolean newerThan(@NotNull String otherVersion) throws InvalidVersionStringException
	{
		return compare(new Version(otherVersion)) == NEWER;
	}

	/**
	 * Checks if the version is newer or the same as the given version.
	 *
	 * @param otherVersion The version to compare with.
	 * @return True if the version is newer or the same, false if not.
	 */
	public boolean newerOrEqualThan(@NotNull String otherVersion) throws InvalidVersionStringException
	{
		return compare(new Version(otherVersion))>= SAME;
	}

	/**
	 * Checks if the version is older than the given version.
	 *
	 * @param otherVersion The version to compare with.
	 * @return True if the version is older, false if not.
	 */
	public boolean olderThan(@NotNull String otherVersion) throws InvalidVersionStringException
	{
		return compare(new Version(otherVersion)) == OLDER;
	}

	/**
	 * Checks if the version is older or the same as the given version.
	 *
	 * @param otherVersion The version to compare with.
	 * @return True if the version is older or the same, false if not.
	 */
	public boolean olderOrEqualThan(@NotNull String otherVersion) throws InvalidVersionStringException
	{
		return compare(new Version(otherVersion)) <= SAME;
	}

	public boolean equals(String otherVersion) throws InvalidVersionStringException
	{
		return this.equals(new Version(otherVersion));
	}
	//endregion
	//endregion

	//region Overriding functions
	/**
	 * Returns the original version string. But without the optional "v" at the start.
	 *
	 * @return The string representing this version.
	 */
	@Override
	public @NotNull String toString()
	{
		return this.rawVersion;
	}

	/**
	 * Compares two versions if they are the same.
	 *
	 * @param otherVersion The version to compare with.
	 * @return True if both versions are equal, false if not.
	 */
	@Override
	@Contract("null -> false")
	public boolean equals(Object otherVersion)
	{
		return otherVersion instanceof Version && compare((Version) otherVersion) == SAME;
	}

	@Override
	public int hashCode()
	{
		return hashCode;
	}

	@Override
	public int compareTo(@NotNull Version o)
	{
		return compare(o);
	}
	//endregion

	//region Getters
	public int getMajor()
	{
		return (versionComponents.length > 0) ? versionComponents[0] : 0;
	}

	public int getMinor()
	{
		return (versionComponents.length > 1) ? versionComponents[1] : 0;
	}

	public int getPatch()
	{
		return (versionComponents.length > 2) ? versionComponents[2] : 0;
	}

	public int toInt()
	{
		return ((getMajor() & 0xFF) << 22) | ((getMinor() & 0x7FF) << 12) | (getPatch() & 0xFF);
	}
	//endregion

	/**
	 * This exception is thrown when the string representing a version is invalid.
	 */
	public static class InvalidVersionStringException extends IllegalArgumentException
	{
		private static final String DEFAULT_ERROR = "The version string must be in the format: " + VERSION_STING_FORMAT;

		public InvalidVersionStringException()
		{
			this(DEFAULT_ERROR);
		}

		public InvalidVersionStringException(String string)
		{
			super(string);
		}
	}
}