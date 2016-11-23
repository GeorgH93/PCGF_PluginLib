/*
 *   Copyright (C) 2016 GeorgH93
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

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import java.net.URL;
import java.util.logging.Logger;

public interface UpdateProvider
{
	/**
	 * Make a connection to the provider an requests the file's details.
	 *
	 * @return The update result from the query.
	 */
	UpdateResult query(Logger logger);

	//region getter for the latest version
	/**
	 * Gets the latest version's version name (such as "1.32")
	 *
	 * @return The latest version string.
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	String getLatestVersionAsString() throws NotSuccessfullyQueriedException;

	/**
	 * Gets the latest version's version (such as 1.32)
	 *
	 * @return The latest version string.
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	Version getLatestVersion() throws NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's direct download url.
	 *
	 * @return latest version's file download url.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	URL getLatestFileURL() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's file name.
	 *
	 * @return latest version's file name.
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	String getLatestVersionFileName() throws NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's name (such as "Project v1.0").
	 *
	 * @return latest version's name.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	String getLatestName() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's game version (such as "CB 1.7.2-R0.3" or "1.9").
	 *
	 * @return latest version's game version.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	String getLatestMinecraftVersion() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's release type.
	 *
	 * @return latest version's release type.
	 * @see ReleaseType
	 *
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	ReleaseType getLatestReleaseType() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's checksum (md5).
	 *
	 * @return latest version's MD5 checksum.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	String getLatestChecksum() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's changelog.
	 *
	 * @return latest version's changelog.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	String getLatestChangelog() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;

	/**
	 * Get the latest version's dependencies.
	 *
	 * @return latest version's dependencies. Null if there are no dependencies.
	 * @throws RequestTypeNotAvailableException If the provider doesn't support the request type
	 * @throws NotSuccessfullyQueriedException  If the provider has not been queried successfully before
	 */
	UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException;
	//endregion

	//region provider property's
	boolean provideDownloadURL();

	boolean provideMinecraftVersion();

	boolean provideChangelog();

	boolean provideMD5Checksum();

	boolean provideReleaseType();

	boolean provideUpdateHistory();

	boolean provideDependencies();
	//endregion

	class UpdateFile
	{
		private URL downloadURL;
		private String name, version, fileName;

		public UpdateFile(URL downloadURL, String name, String version, String fileName)
		{
			this.downloadURL = downloadURL;
			this.name = name;
			this.version = version;
			this.fileName = fileName;
		}

		public URL getDownloadURL()
		{
			return downloadURL;
		}

		public String getName()
		{
			return name;
		}

		public String getFileName()
		{
			return fileName;
		}

		public String getVersion()
		{
			return version;
		}
	}
}