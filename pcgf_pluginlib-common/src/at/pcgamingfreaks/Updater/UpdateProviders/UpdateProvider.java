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

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public interface UpdateProvider
{
	/**
	 * Sets the user agent that should be used when sending web requests.
	 * @param userAgent The user agent that should be used when sending web requests.
	 */
	void setUserAgent(final @NotNull String userAgent);

	/**
	 * @return The name of the update provider.
	 */
	@NotNull String getName();

	/**
	 * Make a connection to the provider a requests the file's details.
	 *
	 * @return The update result from the query.
	 */
	@NotNull UpdateResult query();

	//region getter for the latest version
	/**
	 * Gets the latest version's version (such as 1.32)
	 *
	 * @return The latest version.
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull Version getLatestVersion() throws NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's direct download url.
	 *
	 * @return latest version's file download url.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull URL getLatestFileURL() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's file name.
	 *
	 * @return latest version's file name.
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull String getLatestFileName() throws NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's name (such as "Project v1.0").
	 *
	 * @return latest version's name.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull String getLatestName() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's game version (such as "CB 1.7.2-R0.3" or "1.9").
	 *
	 * @return latest version's game version.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull String getLatestMinecraftVersion() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's compatible game versions as an string array (each element is a compatible version such as "CB 1.7.2-R0.3" or "1.9").
	 *
	 * @return latest version's game version.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull String[] getLatestMinecraftVersions() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's release type.
	 *
	 * @return latest version's release type.
	 *
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull ReleaseType getLatestReleaseType() throws NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's checksum (md5).
	 *
	 * @return latest version's MD5 checksum.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull String getLatestChecksum() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's changelog.
	 *
	 * @return latest version's changelog.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull String getLatestChangelog() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's dependencies.
	 *
	 * @return latest version's dependencies. Empty collection if there are no dependencies.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get a collection of the latest versions.
	 *
	 * @return Array with the updates.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	@NotNull UpdateFile[] getUpdateHistory() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;
	//endregion

	//region provider property's
	boolean providesDownloadURL();

	boolean providesMinecraftVersion();

	boolean providesMinecraftVersions();

	boolean providesChangelog();

	/**
	 * Checks if the provider provides a checksum.
	 *
	 * @return The type of the checksum. ChecksumType.None if the provider doesn't provide a checksum.
	 */
	@NotNull ChecksumType providesChecksum();

	boolean providesUpdateHistory();

	boolean providesDependencies();
	//endregion

	/**
	 * Opens a connection with all the required connection properties (like API keys)
	 * @param url The url to connect to
	 * @return The established connection. Null if a redirect loop was detected.
	 * @throws IOException The exception caused while connecting.
	 */
	@Nullable HttpURLConnection connect(URL url) throws IOException;

	@Data
	class UpdateFile
	{
		private URL downloadURL = null;
		private String name = null, fileName = null, checksum = null, changelog = "", gameVersion = null;
		private Version version = null;
		private String[] gameVersions = null;

		public UpdateFile() {}

		public UpdateFile(URL downloadURL, String name, Version version, String fileName, String checksum, String changelog, String gameVersion)
		{
			this.downloadURL = downloadURL;
			this.name = name;
			this.version = version;
			this.fileName = fileName;
			this.checksum = checksum;
			this.changelog = changelog;
			this.gameVersion = gameVersion;
		}
	}
}