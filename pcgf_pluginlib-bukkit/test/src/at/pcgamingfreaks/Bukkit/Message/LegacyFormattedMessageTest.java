/*
 *   Copyright (C) 2025 GeorgH93
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

package at.pcgamingfreaks.Bukkit.Message;

import at.pcgamingfreaks.Bukkit.Config.Language;
import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Config.YamlFileUpdateMethod;
import at.pcgamingfreaks.Plugin.IPlugin;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;
import at.pcgamingfreaks.TestClasses.TestUtils;
import at.pcgamingfreaks.Version;

import com.google.common.io.Files;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import static org.junit.Assert.assertEquals;

public class LegacyFormattedMessageTest
{
	private static boolean skipTests = !TestUtils.canMockJdkClasses();
	
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		if (!skipTests)
		{
			TestBukkitServer server = new TestBukkitServer();
			server.serverVersion = "git-Paper-138 (MC: 1.12.2)";
			Bukkit.setServer(server);
			TestObjects.initNMSReflection();
			TestObjects.initMockedJavaPlugin();

			setupTestFile("enJson.yml");
			setupTestFile("enLegacy.yml");
		}
	}

	private static void setupTestFile(String name)
	{
		File targetFile = new File(TestObjects.getJavaPlugin().getDataFolder(), name);
		if(targetFile.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			targetFile.delete();
		}
		URL resource = LegacyFormattedMessageTest.class.getResource('/' + name);
		if(resource != null)
		{
			try
			{
				//noinspection deprecation
				Files.copy(new File(URLDecoder.decode(resource.getFile())), targetFile);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@AfterClass
	public static void cleanupTestData()
	{
		new File("enJson.yml").delete();
		new File("enLegacy.yml").delete();
	}

	@Test
	public void testLegacyToJsonResults()
	{
		Assume.assumeTrue("Skip on Java 16+", !skipTests);
		IPlugin plugin = TestObjects.getIPlugin();
		Language jsonLang = new Language(plugin, new Version(0));
		Language legacyLang = new Language(plugin, new Version(0));
		jsonLang.load("enJson", YamlFileUpdateMethod.UPGRADE);
		legacyLang.load("enLegacy", YamlFileUpdateMethod.UPGRADE);
		for(String key : jsonLang.getYaml().getKeys())
		{
			if(legacyLang.getYaml().isSet(key) && key.startsWith("Language."))
			{
				key = key.substring("Language.".length());
				Message msgJson = jsonLang.getMessage(key);
				if(msgJson.isLegacy()) continue; // Skip messages that are a legacy but in the json file
				Message msgLegacy = legacyLang.getMessage(key);
				if(!msgJson.getJson().startsWith("[\"\",")) continue; // The legacy to json converter does not support global formatting
				if(!msgJson.getJson().equals(msgLegacy.getJson()))
				{
					System.out.println(msgJson.getJson());
					System.out.println(msgLegacy.getJson());
				}
				assertEquals(msgJson.getJson(), msgLegacy.getJson());
			}
		}
	}
}