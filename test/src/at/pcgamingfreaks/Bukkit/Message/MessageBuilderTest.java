/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Message;

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class MessageBuilderTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
	}

	@Test
	public void testGenericClass()
	{
		at.pcgamingfreaks.Message.MessageBuilder messageBuilder = new MessageBuilder();
		messageBuilder.appendJson("test");
		//noinspection SpellCheckingInspection
		assertEquals("The message text should match", "§rtest§r", messageBuilder.getClassicMessage());
	}

	@Test
	public void testMessageBuilder()
	{
		List<MessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new MessageComponent());
		assertNotNull("The message builder should not be null", new MessageBuilder(messageComponents.get(0)));
		assertNotNull("The message builder should not be null", new MessageBuilder(messageComponents));
		assertNotNull("The message builder should not be null", new MessageBuilder("", MessageColor.BLUE));
		assertNotNull("The message builder should not be null", new MessageBuilder("", new ChatColor[] { ChatColor.BOLD }));
		assertNotNull("The message builder should not be null", MessageBuilder.fromJson(""));
	}

	@Test
	public void testAppendJSON()
	{
		MessageBuilder messageBuilder = new MessageBuilder();
		messageBuilder.appendJson("[test]");
		//noinspection SpellCheckingInspection
		assertEquals("The message text should match", "§rtest§r", messageBuilder.getClassicMessage());
	}

	@Test
	@SuppressWarnings("SpellCheckingInspection")
	public void testTootltips()
	{
		MessageBuilder messageBuilder = new MessageBuilder("Test");
		messageBuilder.achievementTooltip(Achievement.BREED_COW);
		assertTrue("The tooltip should match", messageBuilder.getJson().contains("BREED_COW"));
		messageBuilder.statisticTooltip(Statistic.BANNER_CLEANED);
		assertTrue("The tooltip should match", messageBuilder.getJson().contains("BANNER_CLEANED"));
		messageBuilder.statisticTooltip(Statistic.BREAK_ITEM, Material.ANVIL);
		assertTrue("The tooltip should match", messageBuilder.getJson().contains("ANVIL"));
		messageBuilder.statisticTooltip(Statistic.ENTITY_KILLED_BY, EntityType.ARROW);
		assertTrue("The tooltip should match", messageBuilder.getJson().contains("ARROW"));
		messageBuilder.itemTooltip(new ItemStack(Material.ACTIVATOR_RAIL, 10));
		assertTrue("The tooltip should match", messageBuilder.getJson().contains("ACTIVATOR_RAIL"));
	}
}