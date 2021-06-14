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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Message;

import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MessageBuilderTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		if (Bukkit.getServer() == null) {
			Bukkit.setServer(new TestBukkitServer());
		}
		//TestObjects.initNMSReflection();
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
	public void testTootltips() throws NoSuchFieldException, IllegalAccessException
	{
		/*IStatisticResolver statisticResolver = mock(IStatisticResolver.class);
		//Reflection.setFinalField(MessageComponent.class.getDeclaredField("STATISTIC_RESOLVER"), null, statisticResolver);

		MessageBuilder messageBuilder = new MessageBuilder("Test");
		doReturn("BREED_COW").when(statisticResolver).getAchievementName(any(Achievement.class));
		//messageBuilder.achievementTooltip(Achievement.BREED_COW);
		assertFalse("The tooltip should match", messageBuilder.getJson().contains("BREED_COW"));
		doReturn("BANNER_CLEANED").when(statisticResolver).getStatisticName(any(Statistic.class));
		//messageBuilder.statisticTooltip(Statistic.BANNER_CLEANED);
		assertFalse("The tooltip should match", messageBuilder.getJson().contains("BANNER_CLEANED"));
		doReturn("ANVIL").when(statisticResolver).getStatisticName(any(Statistic.class), any(Material.class));
		//messageBuilder.statisticTooltip(Statistic.BREAK_ITEM, Material.ANVIL);
		assertFalse("The tooltip should match", messageBuilder.getJson().contains("ANVIL"));
		doReturn("ARROW").when(statisticResolver).getStatisticName(any(Statistic.class), any(EntityType.class));
		//messageBuilder.statisticTooltip(Statistic.ENTITY_KILLED_BY, EntityType.ARROW);
		assertFalse("The tooltip should match", messageBuilder.getJson().contains("ARROW"));

		//Reflection.setFinalField(MessageComponent.class.getDeclaredField("STATISTIC_RESOLVER"), null, null);*/
	}
}