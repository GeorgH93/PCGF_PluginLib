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

package at.pcgamingfreaks;

import at.pcgamingfreaks.Util.BaseStringUtils;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Helper class to figure out which server software is being used to run the server.
 */
public final class ServerType
{
	private ServerType() {}

	@Getter private static final boolean server, bukkit, bukkitCompatible,  spigot, spigotCompatible, paper, paperCompatible, glowstone, glowstoneCompatible;
	@Getter private static final boolean proxy, bungeeCord, bungeeCordCompatible, waterfall, waterfallCompatible, velocity, velocityCompatible;

	static
	{
		boolean bukkitComp = false, spigotComp = false, paperComp = false, glowComp = false, bungeeComp = false, waterfallComp = false, velocityComp = false;
		boolean isBukkit = false, isSpigot = false, isPaper = false, isGlowstone = false, isBungee = false, isWaterfall = false, isVelocity = false;

		// Detecting current server type
		Class<?> bukkitClass = Reflection.getClassSilent("org.bukkit.Bukkit");
		if(bukkitClass != null)
		{ // Is bukkit compatible server
			bukkitComp = true;
			boolean unknown = false;
			try
			{
				String version  = (String) Reflection.getMethod(bukkitClass, "getVersion").invoke(null);
				if (version == null) unknown = true;
				else if(BaseStringUtils.containsIgnoreCase(version, "bukkit"))
				{
					isBukkit = true;
				}
				else if(BaseStringUtils.containsIgnoreCase(version, "spigot"))
				{
					if(BaseStringUtils.containsIgnoreCase(version, "universe"))
					{
						isPaper = true;
						spigotComp = true;
						paperComp = true;
					}
					else
					{
						isSpigot = true;
						spigotComp = true;
					}
				}
				else if(BaseStringUtils.containsIgnoreCase(version, "paper"))
				{
					isPaper = true;
					spigotComp = true;
					paperComp = true;
				}
				else if(BaseStringUtils.containsIgnoreCase(version, "glowstone"))
				{
					isGlowstone = true;
					glowComp = true;
				}
				else
				{
					unknown = true;
				}
			}
			catch(IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
				unknown = true;
			}
			catch(NullPointerException ignored)
			{ // Fix for people that have the bukkit server interface class on their bungee install
				bukkitClass = null;
				bukkitComp = false;
			}
			if(unknown)
			{ // Unknown server implementation, fall back to checking for existing classes
				Class<?> spigotServerClass = Reflection.getClassSilent("org.bukkit.Server$Spigot");
				if(spigotServerClass != null)
				{
					spigotComp = true;
					Method methodGetPaperConfig = Reflection.getMethodSilent(spigotServerClass, "getPaperConfig");
					if(methodGetPaperConfig != null)
					{
						paperComp = true;
					}
				}
			}
		}
		if (bukkitClass == null)
		{ // Is not bukkit compatible server, probably a proxy
			Class<?> bungeeProxyServerClass = Reflection.getClassSilent("net.md_5.bungee.api.ProxyServer");
			if(bungeeProxyServerClass != null)
			{ // Is BungeeCompatible proxy
				bungeeComp = true;
				try
				{
					Object bungeeInstance = Reflection.getMethod(bungeeProxyServerClass, "getInstance").invoke(null);
					String proxyName = (String) Reflection.getMethod(bungeeProxyServerClass, "getName").invoke(bungeeInstance);
					if("BungeeCord".equals(proxyName))
					{
						isBungee = true;
					}
					else if("Waterfall".equals(proxyName))
					{
						isWaterfall = true;
						waterfallComp = true;
					}
					else
					{ // Unknown bungeecord implementation
						//TODO check for waterfall compatibility
					}
				}
				catch(IllegalAccessException | InvocationTargetException e)
				{
					e.printStackTrace();
				}
			}
			else
			{ // Something else
				Class<?> velocityServerClass = Reflection.getClassSilent("com.velocitypowered.api.proxy.ProxyServer");
				if(velocityServerClass != null)
				{
					isVelocity = true;
					velocityComp = true;
				}
			}
		}


		bukkitCompatible = bukkitComp;
		spigotCompatible = spigotComp;
		paperCompatible = paperComp;
		glowstoneCompatible = glowComp;
		server = bukkitComp;
		proxy = bungeeComp || velocityComp;
		bungeeCordCompatible = bungeeComp;
		waterfallCompatible = waterfallComp;
		velocityCompatible = velocityComp;

		bukkit = isBukkit;
		spigot = isSpigot;
		paper = isPaper;
		glowstone = isGlowstone;
		bungeeCord = isBungee;
		waterfall = isWaterfall;
		velocity = isVelocity;
	}
}
