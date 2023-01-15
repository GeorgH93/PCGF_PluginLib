/*
 *   Copyright (C) 2023 GeorgH93
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

package at.pcgamingfreaks.Message;

import at.pcgamingfreaks.TestClasses.TestMessageBuilder;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LegacyMessageParserTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		TestUtils.initReflection();
	}

	@Test
	public void testParse()
	{
		TestMessageBuilder builder = new TestMessageBuilder();
		LegacyMessageParser parser = new LegacyMessageParser(builder);
		parser.parse(MessageColor.RED + "Test message");
		assertEquals("[\"\",{\"text\":\"Test message\",\"color\":\"red\"}]", builder.getJson());
		builder.clear();
		parser.parse(MessageColor.translateAlternateColorCodes("&eMarried players list&r&f - &r&6showing page {CurrentPage}/{MaxPage}"));
		assertEquals("[\"\",{\"text\":\"Married players list\",\"color\":\"yellow\"},{\"text\":\" - \",\"color\":\"white\"},{\"text\":\"showing page {CurrentPage}/{MaxPage}\",\"color\":\"gold\"}]", builder.getJson());
	}

	@Test
	public void testParseRGB()
	{
		TestMessageBuilder builder = new TestMessageBuilder();
		LegacyMessageParser parser = new LegacyMessageParser(builder);
		parser.parse(MessageColor.getColor("#123456") + "Test message");
		assertEquals("[\"\",{\"text\":\"Test message\",\"color\":\"#123456\"}]", builder.getJson());
	}

	@Test
	public void testParseShortRGB()
	{
		TestMessageBuilder builder = new TestMessageBuilder();
		LegacyMessageParser parser = new LegacyMessageParser(builder);
		parser.parse(MessageColor.translateAlternateColorCodes("&x123456") + "Test message");
		assertEquals("[\"\",{\"text\":\"Test message\",\"color\":\"#123456\"}]", builder.getJson());
	}

	@Test
	public void testParseFormatted()
	{
		TestMessageBuilder builder = new TestMessageBuilder();
		LegacyMessageParser parser = new LegacyMessageParser(builder);
		parser.parse(MessageColor.translateAlternateColorAndFormatCodes("&kTest &6this &nmessage &owith &8formatting."));
		assertEquals("[\"\",{\"text\":\"Test \",\"obfuscated\":true},{\"text\":\"this \",\"color\":\"gold\"},{\"text\":\"message \",\"color\":\"gold\",\"underlined\":true},{\"text\":\"with \",\"color\":\"gold\",\"italic\":true,\"underlined\":true},{\"text\":\"formatting.\",\"color\":\"dark_gray\"}]", builder.getJson());
	}

	@Test
	public void testParseFormattedBedrockStyle()
	{
		TestMessageBuilder builder = new TestMessageBuilder();
		LegacyMessageParser parser = new LegacyMessageParser(builder, false);
		parser.parse(MessageColor.translateAlternateColorAndFormatCodes("&kTest &6this &nmessage &owith &8formatting."));
		assertEquals("[\"\",{\"text\":\"Test \",\"obfuscated\":true},{\"text\":\"this \",\"color\":\"gold\",\"obfuscated\":true},{\"text\":\"message \",\"color\":\"gold\",\"underlined\":true,\"obfuscated\":true},{\"text\":\"with \",\"color\":\"gold\",\"italic\":true,\"underlined\":true,\"obfuscated\":true},{\"text\":\"formatting.\",\"color\":\"dark_gray\",\"italic\":true,\"underlined\":true,\"obfuscated\":true}]", builder.getJson());
		builder.clear();
		parser.parse(MessageColor.translateAlternateColorAndFormatCodes("&kTest &r&6this &nmessage &owith &8formatting."));
		assertEquals("[\"\",{\"text\":\"Test \",\"obfuscated\":true},{\"text\":\"this \",\"color\":\"gold\"},{\"text\":\"message \",\"color\":\"gold\",\"underlined\":true},{\"text\":\"with \",\"color\":\"gold\",\"italic\":true,\"underlined\":true},{\"text\":\"formatting.\",\"color\":\"dark_gray\",\"italic\":true,\"underlined\":true}]", builder.getJson());
	}
}