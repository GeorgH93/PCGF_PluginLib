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

import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Message.MessageColor;
import at.pcgamingfreaks.Reflection;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class MessageComponentTest
{
	private IStatisticResolver statisticResolver = null;

	@BeforeClass
	public static void prepClass() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
	}

	@Before
	public void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		statisticResolver = mock(IStatisticResolver.class);
		Reflection.setFinalField(MessageComponent.class.getDeclaredField("STATISTIC_RESOLVER"), null, statisticResolver);
	}

	@After
	public void cleanupTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Reflection.setFinalField(MessageComponent.class.getDeclaredField("STATISTIC_RESOLVER"), null, null);
	}

	@Test
	public void testMessageComponent() throws NoSuchFieldException, IllegalAccessException
	{
		MessageComponent messageComponent = new MessageComponent("Test", MessageColor.BLUE);
		assertEquals("The chat color should match", ChatColor.BLUE, messageComponent.getChatColor());
		assertEquals("The new line object should be equal", new MessageComponent("\n").getClassicMessage(), messageComponent.getNewLineComponent().getClassicMessage());
		TestObjects.setBukkitVersion("1_12_R1");
		assertEquals("The message component should be equal", messageComponent, messageComponent.achievementTooltip(Achievement.BREW_POTION));
		TestObjects.setBukkitVersion("1_8_R1");
		Gson gson = new Gson();
		List<MessageComponent> message = MessageComponent.fromJsonArray(gson.fromJson("[{\"text\":\"test\"}]", JsonArray.class));
		assertEquals("The amount of message components should match", 1, message.size());
		assertEquals("The message text should be equal", "test§r", message.get(0).getClassicMessage());
	}

	@Test
	public void testAchievementTooltip()
	{
		doReturn(null).when(statisticResolver).getAchievementName(any(Achievement.class));
		assertNull("The achievement tooltip should not be set", new MessageComponent().achievementTooltip(Achievement.BREED_COW).getHoverEvent());
	}

	@Test
	public void testStatisticTooltip()
	{
		MessageComponent messageComponent = new MessageComponent();
		doReturn(null).when(statisticResolver).getStatisticName(any(Statistic.class));
		assertNull("The statistic tooltip should not be set", messageComponent.statisticTooltip(Statistic.CAULDRON_USED).getHoverEvent());
		doReturn(null).when(statisticResolver).getStatisticName(any(Statistic.class), any(Material.class));
		assertNull("The statistic tooltip should not be set", messageComponent.statisticTooltip(Statistic.MINE_BLOCK, Material.APPLE).getHoverEvent());
		doReturn(null).when(statisticResolver).getStatisticName(any(Statistic.class), any(EntityType.class));
		assertNull("The statistic tooltip should not be set", messageComponent.statisticTooltip(Statistic.ENTITY_KILLED_BY, EntityType.ARROW).getHoverEvent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStatisticTooltipWithIllegalArguments()
	{
		new MessageComponent().statisticTooltip(Statistic.ENTITY_KILLED_BY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMaterialStatisticTooltipWithBlock()
	{
		new MessageComponent().statisticTooltip(Statistic.MINE_BLOCK, Material.DIRT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMaterialStatisticTooltipWithUntyped()
	{
		new MessageComponent().statisticTooltip(Statistic.CAULDRON_USED, Material.APPLE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMaterialStatisticTooltipWithEntity()
	{
		new MessageComponent().statisticTooltip(Statistic.ENTITY_KILLED_BY, Material.APPLE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEntityStatisticTooltipWithUntyped()
	{
		new MessageComponent().statisticTooltip(Statistic.CAULDRON_USED, EntityType.COW);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEntityStatisticTooltipWithNoEntity()
	{
		new MessageComponent().statisticTooltip(Statistic.MINE_BLOCK, EntityType.COW);
	}
}