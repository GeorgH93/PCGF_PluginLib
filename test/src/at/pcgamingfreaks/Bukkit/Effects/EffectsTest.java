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
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ NMSReflection.class })
public class EffectsTest
{
	@BeforeClass
	public static void prepareTestData() throws Exception
	{
		Bukkit.setServer(new TestBukkitServer());
		final int[] counter = { 0 };
		mockStatic(NMSReflection.class);
		doAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				counter[0]++;
				if (counter[0] < 4)
				{
					return "1_7";
				}
				else if (counter[0] < 41)
				{
					return "1_8";
				}
				else if (counter[0] < 47)
				{
					return "1_9";
				}
				return "1_10";
			}
		}).when(NMSReflection.class, "getVersion");
		doCallRealMethod().when(NMSReflection.class, "getNMSEnum", anyString());
		TestObjects.initNMSReflection();
	}

	@Test
	public void testEffects() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		assertEquals("The name of the enum constant should match", "explode", Effects.EXPLODE.getName());
		assertEquals("The new name of the enum constant should match", "EXPLOSION_NORMAL", Effects.EXPLODE.getNewName());
		assertEquals("The id of the enum constant should match", 0, Effects.EXPLODE.getID());
		assertNull("The enum of the enum constant should be null", Effects.EXPLODE.getEnum());
		assertEquals("The enum of the enum constant should match", EnumParticle.BARRIER, Effects.BARRIER.getEnum());
		assertNull("The enum of the enum constant should be null", Effects.SWEEP_ATTACK.getEnum());
		assertEquals("The enum of the enum constant should match", EnumParticle.DRAGON_BREATH, Effects.DRAGON_BREATH.getEnum());
		Method getNMSEnumParticle = Effects.class.getDeclaredMethod("getNMSEnumParticle", int.class, String.class);
		getNMSEnumParticle.setAccessible(true);
		assertNull("The enum of the invalid effect should be null", getNMSEnumParticle.invoke(null, 41, "INVALID"));
		getNMSEnumParticle.setAccessible(false);
	}
}