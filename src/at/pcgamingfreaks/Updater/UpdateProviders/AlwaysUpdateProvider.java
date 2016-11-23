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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * An update provider that returns an very high version so that the file gets downloaded every time.
 * The file to download is specified in the constructor.
 * This provider should only be used for preview builds that will always download the newest build from a ci server.
 */
public class AlwaysUpdateProvider implements UpdateProvider
{
	private URL downloadUrl = null;
	private String fileName;
	private ReleaseType releaseType;

	/**
	 * @param url The url to the file that should be downloaded.
	 */
	public AlwaysUpdateProvider(String url)
	{
		this(url, "file.jar");
	}

	/**
	 * @param url      The url to the file that should be downloaded.
	 * @param fileName The name of the file.
	 */
	public AlwaysUpdateProvider(String url, String fileName)
	{
		this(url, fileName, ReleaseType.RELEASE);
	}

	/**
	 * @param url         The url to the file that should be downloaded.
	 * @param fileName    The name of the file.
	 * @param releaseType The release type.
	 */
	public AlwaysUpdateProvider(String url, String fileName, ReleaseType releaseType)
	{
		this.releaseType = releaseType;
		this.fileName = fileName;
		try
		{
			downloadUrl = new URL(url);
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public UpdateResult query(Logger logger)
	{
		return UpdateResult.SUCCESS;
	}

	@Override
	public String getLatestVersionAsString() throws NotSuccessfullyQueriedException
	{
		return Integer.MAX_VALUE + "." + Integer.MAX_VALUE;
	}

	@Override
	public Version getLatestVersion() throws NotSuccessfullyQueriedException
	{
		return new Version(getLatestVersionAsString());
	}

	@Override
	public URL getLatestFileURL() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		return downloadUrl;
	}

	@Override
	public String getLatestVersionFileName() throws NotSuccessfullyQueriedException
	{
		return fileName;
	}

	@Override
	public String getLatestName() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide the name of the latest version.");
	}

	@Override
	public String getLatestMinecraftVersion() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide the minecraft version of the latest version.");
	}

	@Override
	public ReleaseType getLatestReleaseType() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		return releaseType;
	}

	@Override
	public String getLatestChecksum() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide the checksum for the latest file.");
	}

	@Override
	public String getLatestChangelog() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide the changelog for the latest file.");
	}

	@Override
	public UpdateFile[] getLatestDependencies() throws RequestTypeNotAvailableException, NotSuccessfullyQueriedException
	{
		throw new RequestTypeNotAvailableException("This provider does not provide dependencies for the latest file.");
	}

	@Override
	public boolean provideDownloadURL()
	{
		return true;
	}

	@Override
	public boolean provideMinecraftVersion()
	{
		return false;
	}

	@Override
	public boolean provideChangelog()
	{
		return false;
	}

	@Override
	public boolean provideMD5Checksum()
	{
		return false;
	}

	@Override
	public boolean provideReleaseType()
	{
		return true;
	}

	@Override
	public boolean provideUpdateHistory()
	{
		return false;
	}

	@Override
	public boolean provideDependencies()
	{
		return false;
	}
}