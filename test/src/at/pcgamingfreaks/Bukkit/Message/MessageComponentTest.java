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

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class MessageComponentTest
{
	private static Field modifiers;
	private static Method method;

	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException, IllegalAccessException
	{
		Bukkit.setServer(new TestBukkitServer());
		TestObjects.initNMSReflection();
		modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
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
	public void testAchievementTooltip() throws NoSuchFieldException, IllegalAccessException
	{
		Field getNAchievementField = setMethod("GET_NMS_ACHIEVEMENT");
		assertNull("The achievement tooltip should not be set", new MessageComponent().achievementTooltip(Achievement.BREED_COW).getHoverEvent());
		resetMethod(getNAchievementField);
	}

	@Test
	public void testStatisticTooltip() throws NoSuchFieldException, IllegalAccessException
	{
		Field getNMSStatisticField = setMethod("GET_NMS_STATISTIC");
		MessageComponent messageComponent = new MessageComponent();
		assertNull("The statistic tooltip should not be set", messageComponent.statisticTooltip(Statistic.CAULDRON_USED).getHoverEvent());
		resetMethod(getNMSStatisticField);
		Field getMaterialStatisticField = setMethod("GET_MATERIAL_STATISTIC");
		assertNull("The statistic tooltip should not be set", messageComponent.statisticTooltip(Statistic.MINE_BLOCK, Material.APPLE).getHoverEvent());
		resetMethod(getMaterialStatisticField);
		Field getEntityStatisticField = setMethod("GET_ENTITY_STATISTIC");
		assertNull("The statistic tooltip should not be set", messageComponent.statisticTooltip(Statistic.ENTITY_KILLED_BY, EntityType.ARROW).getHoverEvent());
		resetMethod(getEntityStatisticField);
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

	private Field setMethod(String name) throws NoSuchFieldException, IllegalAccessException
	{
		Field field = MessageComponent.class.getDeclaredField(name);
		field.setAccessible(true);
		modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);
		method = (Method) field.get(null);
		field.set(null, null);
		return field;
	}

	private void resetMethod(Field field) throws IllegalAccessException
	{
		field.set(null, method);
		modifiers.set(field, field.getModifiers() | Modifier.FINAL);
		field.setAccessible(false);
	}

	@AfterClass
	public static void cleanupTestData()
	{
		modifiers.setAccessible(false);
	}
}