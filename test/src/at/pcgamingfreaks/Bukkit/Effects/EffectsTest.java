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

package at.pcgamingfreaks.Bukkit.Effects;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.TestClasses.NMS.EnumParticle;
import at.pcgamingfreaks.TestClasses.TestBukkitServer;
import at.pcgamingfreaks.TestClasses.TestObjects;

import org.bukkit.Bukkit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MCVersion.class, NMSReflection.class })
public class EffectsTest
{
	@BeforeClass
	public static void prepareTestData() throws Exception
	{
		Bukkit.setServer(new TestBukkitServer());
		final int[] counter = { 0 };
		mockStatic(MCVersion.class);
		doAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				return ++counter[0] >= 36;
			}
		}).when(MCVersion.class, "isNewerOrEqualThan", anyObject());
		TestObjects.initNMSReflection();
	}

	@Test
	public void testEffects() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		assertEquals("The name of the enum constant should match", "explode", Effects.EXPLODE.getName());
		assertEquals("The name of the enum constant should match", "EXPLODE", Effects.EXPLODE.getNameUpperCase());
		assertEquals("The new name of the enum constant should match", "EXPLOSION_NORMAL", Effects.EXPLODE.getNewName());
		assertEquals("The id of the enum constant should match", 0, Effects.EXPLODE.getID());
		assertNull("The enum of the enum constant should be null", Effects.EXPLODE.getEnum());
		assertEquals("The enum of the enum constant should match", EnumParticle.BARRIER, Effects.BARRIER.getEnum());
		assertEquals("The min version should match", MCVersion.MC_1_9, Effects.DAMAGE_INDICATOR.getMinVersion());
	}
}