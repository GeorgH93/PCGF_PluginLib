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

package at.pcgamingfreaks.Updater;

import at.pcgamingfreaks.Updater.UpdateProviders.*;
import at.pcgamingfreaks.Utils;
import at.pcgamingfreaks.yaml.YAML;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ManagedUpdaterBase<UPDATER extends Updater, PLUGIN> implements IUpdater
{
	protected final PLUGIN plugin;
	protected final Logger logger;
	@Getter @Setter private boolean announceDownloadProgress = true, downloadDependencies = false;
	@Getter private @Nullable UPDATER updater = null;
	protected final Map<String, List<UpdateProvider>> updateChannelMap;
	protected UpdateProvider[] providers;
	protected final String defaultChannel, releaseType;
	protected UpdateMode autoUpdateMode = UpdateMode.UPDATE;

	protected ManagedUpdaterBase(final @NotNull PLUGIN plugin, final @NotNull Logger logger)
	{
		this(plugin, logger, null);
	}

	protected ManagedUpdaterBase(final @NotNull PLUGIN plugin, final @NotNull Logger logger, @Nullable String channel)
	{
		this.plugin = plugin;
		this.logger = logger;
		InputStream stream = Utils.getResource(plugin.getClass(), "update.yml");
		Map<String, List<UpdateProvider>> updateChannelMap;
		String defaultChannel = "", releaseType = "";
		try
		{
			if(stream == null) throw new IllegalStateException("update.yml missing!");
			final YAML config = new YAML(stream);
			final Map<String, UpdateProvider> providerMap = getUpdateProviders(config, logger, getPluginName());
			updateChannelMap = getUpdateChannels(config, logger, providerMap);
			defaultChannel = config.getString("DefaultChannel");
			releaseType = config.getString("ReleaseType", "");
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, "Failed to read update.yml", e);
			updateChannelMap = new HashMap<>();
		}
		this.defaultChannel = defaultChannel;
		this.releaseType = releaseType.toLowerCase(Locale.ENGLISH);
		this.updateChannelMap = updateChannelMap;
		setChannel(channel);
	}

	public void setConfig(final @NotNull IUpdateConfiguration config)
	{
		setChannel(config.getUpdateChannel());
		autoUpdateMode = config.getUpdateMode();
	}

	public void setChannel(@Nullable String channel)
	{
		if(channel == null) channel = defaultChannel;
		channel = channel.toLowerCase(Locale.ENGLISH);
		if(releaseType.length() >= 1) channel += '.' + releaseType;
		List<UpdateProvider> providers = updateChannelMap.getOrDefault(channel, new ArrayList<>(0));
		if(providers.size() == 0)
		{
			logger.warning("No update providers for channel: " + channel);
			providers.add(new NullUpdateProvider());
		}
		this.providers = providers.toArray(new UpdateProvider[0]);
	}

	private static @NotNull Map<String, UpdateProvider> getUpdateProviders(final @NotNull YAML config, final @NotNull Logger logger, final @NotNull String pluginName)
	{
		Map<String, UpdateProvider> providerMap = new HashMap<>();
		config.getKeysFiltered("UpdateProviders\\.\\w*\\.Type").forEach(typeKey -> {
			final String key = typeKey.substring(0, typeKey.length() - ".Type".length());
			final String type = config.getString(typeKey, ""), provider = key.substring("UpdateProviders.".length());
			try
			{
				UpdateProvider updateProvider = null;
				switch(type)
				{
					case "Bukkit":
						updateProvider = new BukkitUpdateProvider(config.getInt(key + ".ProjectId"), config.getString(key + ".ApiKey", null), logger);
						break;
					case "Jenkins":
						final String server = config.getString(key + ".Server"), job = config.getString(key + ".Job");
						final String token = config.getString(key + ".Token", null), artifactFilter = config.getString(key + "Filter", null);
						//noinspection LanguageMismatch
						updateProvider = new JenkinsUpdateProvider(server, job, token, logger, artifactFilter);
						break;
					case "GitHub":
						final String owner = config.getString(key + ".Owner"), repo = config.getString(key + ".Repo");
						final String userAgent = config.getString(key + ".UserAgent", repo);
						final String jarSearch = config.getString(key + ".JarSearch", ".*\\.jar"), md5Search = config.getString(key + ".JarSearch", ".*\\.md5");
						//noinspection LanguageMismatch
						updateProvider = new GitHubUpdateProvider(owner, repo, userAgent, jarSearch, md5Search, logger);
						break;
					case "Spigot":
						final int project = config.getInt(key + ".ProjectId");
						updateProvider = new SpigotUpdateProvider(project, logger, config.getString(key + ".Filename", project + ".jar"));
						break;
					case "Simple":
						final String url = config.getString(key + ".URL"), fileName = config.getString(key + ".Filename", "file.jar");
						updateProvider = new AlwaysUpdateProvider(url, fileName, ReleaseType.valueOf(config.getString(key + ".ReleaseType")));
						break;
					default: logger.warning("Unknown updater type: " + type);
				}
				if(updateProvider != null)
				{
					updateProvider.setUserAgent(pluginName + " Updater (PCGF MC-Plugin-Updater)");
					providerMap.put(provider, updateProvider);
				}
			}
			catch(Exception e)
			{
				logger.warning("Unable to read config for update provider: " + type);
			}
		});
		return providerMap;
	}

	private static Map<String, List<UpdateProvider>> getUpdateChannels(final @NotNull YAML config, final @NotNull Logger logger, final @NotNull Map<String, UpdateProvider> providerMap)
	{
		Map<String, List<UpdateProvider>> updateChannelMap = new HashMap<>();
		config.getKeysFiltered("UpdateChannels\\..*").forEach(key -> {
			final String channelName = key.substring("UpdateChannels.".length());
			try
			{
				final List<String> providers = config.getStringList(key);
				final List<UpdateProvider> providerList = new ArrayList<>(providers.size());
				for(String provider : providers)
				{
					final UpdateProvider updateProvider = providerMap.get(provider);
					if(updateProvider == null) logger.warning("Unknown update provider '" + provider + "' in channel '" + channelName + "'! Make sure it is defined!");
					else providerList.add(updateProvider);
				}
				updateChannelMap.put(channelName.toLowerCase(Locale.ENGLISH), providerList);
			}
			catch(Exception e)
			{
				logger.warning("Unable to read config for update channel: " + channelName);
			}
		});
		return updateChannelMap;
	}

	@Override
	public void update(final @Nullable UpdateResponseCallback callback)
	{
		if(isRunning())
		{
			if(callback != null) callback.onDone(UpdateResult.FAIL_UPDATE_ALREADY_IN_PROGRESS);
			return;
		}
		if(updater == null) updater = makeUpdater(providers);
		updater.update(callback);
	}

	@Override
	public void waitForAsyncOperation()
	{
		if(updater != null) updater.waitForAsyncOperation();
	}

	@Override
	public void checkForUpdate(final @Nullable UpdateResponseCallback response)
	{
		if(isRunning())
		{
			if(response != null) response.onDone(UpdateResult.FAIL_UPDATE_ALREADY_IN_PROGRESS);
			return;
		}
		if(updater == null) updater = makeUpdater(providers);
		updater.checkForUpdate(response);
	}

	@Override
	public void update(final @NotNull UpdateMode updateMode, final @Nullable UpdateResponseCallback response)
	{
		if(updateMode == UpdateMode.CHECK) checkForUpdate(response);
		else if(updateMode == UpdateMode.UPDATE) update(response);
	}

	@Override
	public boolean isRunning()
	{
		if(updater == null) return false;
		return updater.isRunning();
	}

	public void autoUpdate()
	{
		update(autoUpdateMode, null);
	}

	protected abstract UPDATER makeUpdater(final @NotNull UpdateProvider[] updateProvider);

	protected abstract String getPluginName();
}