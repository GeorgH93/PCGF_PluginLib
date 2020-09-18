/*
 *   Copyright (C) 2020 GeorgH93
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
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Particles;

import at.pcgamingfreaks.Bukkit.MCVersion;

import org.bukkit.material.MaterialData;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MCVersion.class })
public class ParticleTest
{
	@BeforeClass
	public static void prepareTestData() throws Exception
	{
		final int[] counter = { 0 };
		mockStatic(MCVersion.class);
		doAnswer((Answer<Boolean>) invocationOnMock -> ++counter[0] >= 36 && counter[0] < 45).when(MCVersion.class, "isNewerOrEqualThan", any());
		doReturn(true).when(MCVersion.class, "isOlderThan", any());
	}

	@Test
	public void testParticles()
	{
		assertEquals("The name of the enum constant should match", "explode", Particle.EXPLOSION.getOldName());
		assertEquals("The name of the enum constant should match", "EXPLODE", Particle.EXPLOSION.getOldNameUpperCase());
		assertEquals("The new name of the enum constant should match", "EXPLOSION_NORMAL", Particle.EXPLOSION.getName());
		assertEquals("The min version should match", MCVersion.MC_1_9, Particle.DAMAGE_INDICATOR.getMinVersion());
		assertEquals("The Particle object should match", Particle.FALLING_DUST, Particle.getFrom(org.bukkit.Particle.FALLING_DUST));
		assertEquals("The data type should match", MaterialData.class, Particle.FALLING_DUST.getDataType());
		assertEquals("The data type should match", Void.class, Particle.SPIT.getDataType());
	}

	@Test
	public void testParticleNames()
	{
		for(Particle particle : Particle.values())
		{
			assertNotNull(org.bukkit.Particle.valueOf(particle.getName()));
		}
	}

	@Test
	public void testParticleFromBukkitParticle()
	{
		for(org.bukkit.Particle particle : org.bukkit.Particle.values())
		{
			assertNotNull("There should be a particle representing " + particle, Particle.getFrom(particle));
		}
	}

	@Test
	public void testParticleDataType()
	{
		for(Particle particle : Particle.values())
		{
			assertEquals(org.bukkit.Particle.valueOf(particle.getName()).getDataType(), particle.getDataType());
		}
	}
}