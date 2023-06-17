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

package at.pcgamingfreaks.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MojangUuidResolver
{
	public static final String UUID_FORMAT_REGEX = "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})";
	public static final String UUID_FORMAT_REPLACE_TO = "$1-$2-$3-$4-$5";

	private static final Gson GSON = new Gson();
	private static final Pattern API_MAX_PROFILE_BATCH_SIZE_PATTERN = Pattern.compile(".*Not more that (?<batchSize>\\d+) profile name per call is allowed.*");
	private static final long MOJANG_QUERY_RETRY_TIME = 600000L;
	private static int BATCH_SIZE = 100; // Limit from Mojang

	@Nullable private final UuidCache cache;
	@Nullable private final Logger logger;
	@NotNull  private final String mojangApiHost;
	@NotNull  private final String nameResolverHost;

	public MojangUuidResolver(final @Nullable UuidCache cache)
	{
		this(cache, null);
	}

	public MojangUuidResolver(final @Nullable Logger logger)
	{
		this(null, logger);
	}

	//@SneakyThrows // Hardcoded valid URL, will never throw the exception
	public MojangUuidResolver(final @Nullable UuidCache cache, final @Nullable Logger logger)
	{
		this(cache, logger, "https://api.mojang.com/", "https://sessionserver.mojang.com/");
	}

	public MojangUuidResolver(final @NotNull String customMojangApiHost, final @NotNull String customNameResolverHost, final @Nullable UuidCache cache, final @Nullable Logger logger) throws MalformedURLException
	{
		this(cache, logger, customMojangApiHost, customNameResolverHost);
		if(!customMojangApiHost.startsWith("http") || customMojangApiHost.length() < 9) throw new MalformedURLException("Url should start with https");
		new URL(mojangApiHost); // Test if url is valid
		if(!customNameResolverHost.startsWith("http") || customNameResolverHost.length() < 9) throw new MalformedURLException("Url should start with https");
		new URL(customNameResolverHost); // Test if url is valid
	}

	private MojangUuidResolver(final @Nullable UuidCache cache, final @Nullable Logger logger, final @NotNull String customMojangApiHost, final @NotNull String customNameResolverHost)
	{
		this.cache = cache;
		this.logger = logger;
		this.mojangApiHost = customMojangApiHost.endsWith("/") ? customMojangApiHost : customMojangApiHost + '/';
		this.nameResolverHost = customNameResolverHost.endsWith("/") ? customNameResolverHost : customNameResolverHost + '/';
	}

	private void log(Level level, String message)
	{
		if(logger != null)
		{
			logger.log(level, message);
		}
	}

	private void log(Level level, String message, Throwable throwable)
	{
		if(logger != null)
		{
			logger.log(level, message, throwable);
		}
	}

	public @Nullable UUID getUUID(final @NotNull String name, final @Nullable Date at)
	{
		if((at == null || at.after(new Date(System.currentTimeMillis() - 1000L*24*3600* 30))) && (cache != null && cache.contains(name)))
		{
			return cache.getUuidFromName(name);
		}
		String url = mojangApiHost + "users/profiles/minecraft/" + name + ((at != null) ? "?at=" + (at.getTime()/1000L) : "");
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8)))
		{
			String uuidString = (((JsonObject) new JsonParser().parse(in)).get("id")).getAsString();
			UUID uuid = uuidString != null ? UUID.fromString(uuidString.replaceAll(UUID_FORMAT_REGEX, UUID_FORMAT_REPLACE_TO)) : null;
			if(uuid != null && (at == null || at.after(new Date(System.currentTimeMillis() - 1000L*24*3600* 30))) && cache != null)
			{
				cache.addToCache(uuid, name);
			}
			return uuid;
		}
		catch(MalformedURLException e) // There is something going wrong!
		{
			log(Level.SEVERE, "Failed to get uuid cause of a malformed url! This should not have happened!\n URL: \"" + url + "\"", e);
		}
		catch(IOException e)
		{
			if(e.getMessage().contains("HTTP response code: 429"))
			{
				log(Level.SEVERE, "You have reached the request limit of the mojang api! Please retry later!");
			}
			else
			{
				log(Level.SEVERE, "Looks like there is a problem with the connection with mojang. Please retry later.", e);
			}
		}
		catch(Exception e)
		{
			if(at == null) // We can't resolve the uuid for the player
			{
				log(Level.WARNING, "Unable to get UUID for: " + name + "!");
			}
			else if(at.getTime() == 0) // If it's not his first name maybe it's his current name
			{
				log(Level.INFO, "Unable to get UUID for: " + name + " at 0! Trying without date!");
				return getUUID(name, null);
			}
			else // If we can't get the player with the date he was here last time it's likely that it is his first name
			{
				log(Level.INFO, "Unable to get UUID for: " + name + " at " + at.getTime()/1000L + "! Trying at=0!");
				return getUUID(name, new Date(0));
			}
		}
		return null;
	}

	public Map<String, UUID> getUUIDs(final @NotNull Collection<String> names)
	{
		List<String> batch = new ArrayList<>(BATCH_SIZE);
		Iterator<String> players = names.iterator();
		Map<String, UUID> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		boolean success;
		int fromCache = 0, fromWeb = 0;
		while (players.hasNext())
		{
			while (players.hasNext() && batch.size() < BATCH_SIZE)
			{
				String name = players.next();
				if(cache != null && cache.contains(name))
				{
					result.put(name, cache.getUuidFromName(name));
					fromCache++;
				}
				else
				{
					batch.add(name);
				}
			}
			do
			{
				HttpURLConnection connection = null;
				try
				{
					connection = (HttpURLConnection) new URL(mojangApiHost + "profiles/minecraft").openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json; encoding=UTF-8");
					connection.setUseCaches(false);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					try(OutputStream out = connection.getOutputStream())
					{
						out.write(GSON.toJson(batch).getBytes(StandardCharsets.UTF_8));
					}
					MojangProfile[] profiles;
					try(Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
					{
						profiles = GSON.fromJson(in, MojangProfile[].class);
					}
					for (MojangProfile profile : profiles)
					{
						result.put(profile.name, profile.getUUID());
						if(cache != null) cache.addToCache(profile.getUUID(), profile.name);
						fromWeb++;
					}
				}
				catch(IOException e)
				{
					try
					{
						if(connection != null)
						{
							if(connection.getResponseCode() == 429)
							{
								log(Level.INFO,"Reached the request limit of the mojang api!\nConverting will be paused for 10 minutes and then continue!");
								//TODO: better fail handling
								Thread.sleep(MOJANG_QUERY_RETRY_TIME);
								success = false;
								continue;
							}
							else
							{
								try(BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(connection.getErrorStream())))
								{
									StringBuilder errorBuilder = new StringBuilder();
									String line;
									while((line = errorStreamReader.readLine()) != null)
									{
										errorBuilder.append(line);
									}
									String errorMessage = errorBuilder.toString();
									Matcher matcher = API_MAX_PROFILE_BATCH_SIZE_PATTERN.matcher(errorMessage);
									if(connection.getResponseCode() == 400 && matcher.matches())
									{
										BATCH_SIZE = Integer.parseInt(matcher.group("batchSize"));
										log(Level.INFO, "Mojang requests smaller batch size. Reducing batch size to " + BATCH_SIZE + " and try again ...");
										return getUUIDs(names);
									}
									else
									{
										log(Level.SEVERE, "Mojang responded with status code: " + connection.getResponseCode() + " Message: " + errorMessage, e);
									}
								}
							}
						}
						else
						{
							log(Level.SEVERE, "Unexpected error trying to resolve UUIDs", e);
						}
					}
					catch(InterruptedException | IOException ignore) {}
					log(Level.SEVERE, "Could not convert all names to uuids because of an issue. Please check the log.");
					return result;
				}
				batch.clear();
				success = true;
			} while(!success);
		}
		log(Level.INFO, "Converted " + (fromCache + fromWeb) + "/" + names.size() + " UUIDs (" + fromCache + " of them from the cache and " + fromWeb + " from Mojang).");
		return result;
	}

	public @NotNull String getName(final @NotNull UUID uuid)
	{
		if (cache != null)
		{
			String name = cache.getNameFromUuid(uuid);
			if (name != null) return name;
		}
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL(nameResolverHost + "session/minecraft/profile/" + uuid.toString().replaceAll("-", "")).openStream(), StandardCharsets.UTF_8)))
		{
			return (((JsonObject) JsonParser.parseReader(in)).get("name")).getAsString();
		}
		catch(IOException e)
		{
			log(Level.WARNING, "Looks like there is a problem with the connection with Mojang. Please retry later.");
			if(e.getMessage().contains("HTTP response code: 429"))
			{
				log(Level.WARNING, "You have reached the request limit of the Mojang api! Please retry later!");
			}
			else
			{
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			log(Level.WARNING, "Looks like there is no player with this uuid!\n UUID: \"" + uuid + "\"");
			if (logger != null)
			{
				logger.log(Level.SEVERE, "Failed to query player name!", e);
			}
		}
		return "unknown";
	}
}