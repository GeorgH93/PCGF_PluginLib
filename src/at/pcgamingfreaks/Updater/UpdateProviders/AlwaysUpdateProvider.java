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

package at.pcgamingfreaks.Updater.UpdateProviders;

import at.pcgamingfreaks.Updater.ChecksumType;
import at.pcgamingfreaks.Updater.ReleaseType;
import at.pcgamingfreaks.Updater.UpdateResult;
import at.pcgamingfreaks.Version;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * An update provider that returns an very high version so that the file gets downloaded every time.
 * The file to download is specified in the constructor.
 * This provider should only be used for preview builds that will always download the newest build from a server.
 */
@SuppressWarnings("RedundantThrows")
public class AlwaysUpdateProvider implements UpdateProvider
{
	private final URL downloadUrl;
	private final String fileName;
	private final ReleaseType releaseType;

	//region constructors
	/**
	 * @param url The url to the file that should be downloaded.
	 */
	@Deprecated
	public AlwaysUpdateProvider(@NotNull String url)
	{
		this(url, "file.jar");
	}

	/**
	 * @param url      The url to the file that should be downloaded.
	 * @param fileName The name of the file.
	 */
	@Deprecated
	public AlwaysUpdateProvider(@NotNull String url, String fileName)
	{
		this(url, fileName, ReleaseType.RELEASE);
	}

	/**
	 * @param url         The url to the file that should be downloaded.
	 * @param fileName    The name of the file.
	 * @param releaseType The release type.
	 */
	@Deprecated
	public AlwaysUpdateProvider(@NotNull String url, String fileName, ReleaseType releaseType)
	{
		this.releaseType = releaseType;
		this.fileName = fileName;
		URL dlURL = null;
		try
		{
			dlURL = new URL(url);
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		downloadUrl = dlURL;
	}

	/**
	 * @param url The url to the file that should be downloaded.
	 */
	public AlwaysUpdateProvider(@NotNull URL url)
	{
		this(url, "file.jar");
	}

	/**
	 * @param url      The url to the file that should be downloaded.
	 * @param fileName The name of the file.
	 */
	public AlwaysUpdateProvider(@NotNull URL url, String fileName)
	{
		this(url, fileName, ReleaseType.RELEASE);
	}

	/**
	 * @param url         The url to the file that should be downloaded.
	 * @param fileName    The name of the file.
	 * @param releaseType The release type.
	 */
	public AlwaysUpdateProvider(@NotNull URL url, String fileName, ReleaseType releaseType)
	{
		this.releaseType = releaseType;
		this.fileName = fileName;
		this.downloadUrl = url;
	}
	//endregion

	@Override
	public @NotNull UpdateResult query()
	{
		return UpdateResult.SUCCESS;
	}

	@Override
	public @NotNull Version getLatestVersion() throws NotSuccessfullyQueriedException
	{
		return new Version(Integer.MAX_VALUE + "." + Integer.MAX_VALUE);
	}

	@Override
	public @NotNull URL getLatestFileURL() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		if(downloadUrl == null) throw new RequestTypeNotAvailableException("The URL used to create the provider was not valid!");
		return downloadUrl;
	}

	@Override
	public @NotNull String getLatestFileName() throws NotSuccessfullyQueriedException
	{
		return fileName;
	}

	@Override
	public @NotNull String getLatestName() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide the name of the latest version.");
	}

	@Override
	public @NotNull String getLatestMinecraftVersion() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide the minecraft version of the latest version.");
	}

	@NotNull
	@Override
	public ReleaseType getLatestReleaseType() throws NotSuccessfullyQueriedException
	{
		return releaseType;
	}

	@Override
	public @NotNull String getLatestChecksum() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide the checksum for the latest file.");
	}

	@Override
	public @NotNull String getLatestChangelog() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide the changelog for the latest file.");
	}

	@Override
	public @NotNull UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide dependencies for the latest file.");
	}

	@Override
	public @NotNull UpdateFile[] getUpdateHistory() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide an update history.");
	}

	//region provider property's
	@Override
	public boolean providesDownloadURL()
	{
		return true;
	}

	@Override
	public boolean providesMinecraftVersion()
	{
		return false;
	}

	@Override
	public boolean providesChangelog()
	{
		return false;
	}

	@Override
	public @NotNull ChecksumType providesChecksum()
	{
		return ChecksumType.NONE;
	}

	@Override
	public boolean providesUpdateHistory()
	{
		return false;
	}

	@Override
	public boolean providesDependencies()
	{
		return false;
	}
	//endregion
}