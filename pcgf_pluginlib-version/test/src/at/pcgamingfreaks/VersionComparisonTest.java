/*
 *   Copyright (C) 2022 GeorgH93
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static at.pcgamingfreaks.VersionTest.*;
import static org.junit.Assert.*;

/**
 * Parameterized tests for Version comparison methods.
 * Each row encodes: (a, b, aNewerThanB) where:
 *   aNewerThanB = 1  means a is strictly newer than b
 *   aNewerThanB = 0  means a and b are equal in ordering
 *   aNewerThanB = -1 means a is strictly older than b
 */
@RunWith(Parameterized.class)
public class VersionComparisonTest
{
	@Parameterized.Parameters(name = "{2} vs {3}: expected={4}")
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][] {
			{ version_1_2,              version_1,                       "1.2",                           "1",                              1 },
			{ version_1_2,              version_1_0,                     "1.2",                           "1.0",                            1 },
			{ version_1_2,              version_v1_0,                    "1.2",                           "v1.0",                           1 },
			{ version_1_2,              version_1_2_snapshot,            "1.2",                           "1.2-SNAPSHOT",                   1 },
			{ version_1_2,              version_1_2_beta,                "1.2",                           "1.2-Beta",                       1 },
			{ version_1_2,              version_1_2_beta2,               "1.2",                           "1.2-BETA2",                      1 },
			{ version_1_2_it,           version_1,                       "1.2(it)",                       "1",                              1 },
			{ version_1_2_it,           version_1_0,                     "1.2(it)",                       "1.0",                            1 },
			{ version_1_2_it,           version_v1_0,                    "1.2(it)",                       "v1.0",                           1 },
			{ version_1_2_it,           version_1_2_snapshot,            "1.2(it)",                       "1.2-SNAPSHOT",                   1 },
			{ version_1_2_it,           version_1_2_beta,                "1.2(it)",                       "1.2-Beta",                       1 },
			{ version_1_2_it,           version_1_2_beta2,               "1.2(it)",                       "1.2-BETA2",                      1 },
			{ version_1_2_snapshot,     version_1,                       "1.2-SNAPSHOT",                  "1",                              1 },
			{ version_1_2_snapshot,     version_1_0,                     "1.2-SNAPSHOT",                  "1.0",                            1 },
			{ version_1_2_snapshot,     version_v1_0,                    "1.2-SNAPSHOT",                  "v1.0",                           1 },
			{ version_1_2_snapshot,     version_1_2_beta,                "1.2-SNAPSHOT",                  "1.2-Beta",                       1 },
			{ version_1_2_snapshot,     version_1_2_beta2,               "1.2-SNAPSHOT",                  "1.2-BETA2",                      1 },
			{ version_1_2_snapshot_it,  version_1,                       "1.2-SNAPSHOT(it)",              "1",                              1 },
			{ version_1_2_snapshot_it,  version_1_0,                     "1.2-SNAPSHOT(it)",              "1.0",                            1 },
			{ version_1_2_snapshot_it,  version_v1_0,                    "1.2-SNAPSHOT(it)",              "v1.0",                           1 },
			{ version_1_2_snapshot_it,  version_1_2_snapshot,            "1.2-SNAPSHOT(it)",              "1.2-SNAPSHOT",                   1 },
			{ version_1_2_snapshot_it,  version_1_2_beta,                "1.2-SNAPSHOT(it)",              "1.2-Beta",                       1 },
			{ version_1_2_snapshot_it,  version_1_2_beta2,               "1.2-SNAPSHOT(it)",              "1.2-BETA2",                      1 },
			{ version_1_2_beta,         version_1,                       "1.2-Beta",                      "1",                              1 },
			{ version_1_2_beta,         version_1_0,                     "1.2-Beta",                      "1.0",                            1 },
			{ version_1_2_beta,         version_v1_0,                    "1.2-Beta",                      "v1.0",                           1 },
			{ version_1_2_beta2,        version_1,                       "1.2-BETA2",                     "1",                              1 },
			{ version_1_2_beta2,        version_1_0,                     "1.2-BETA2",                     "1.0",                            1 },
			{ version_1_2_beta2,        version_v1_0,                    "1.2-BETA2",                     "v1.0",                           1 },
			{ version_1_2_beta2,        version_1_2_beta,                "1.2-BETA2",                     "1.2-Beta",                       1 },
			{ version_2_0_snapshot,     version_1_2,                     "2.0-SNAPSHOT",                  "1.2",                            1 },
			{ version_v2_0,             version_2_0_snapshot,            "v2.0",                          "2.0-SNAPSHOT",                   1 },
			{ version_1_2_snapshot_b_8, version_1_2_snapshot_b_5,        "1.2-SNAPSHOT-Build=8",          "1.2-SNAPSHOT-Build5",            1 },
			{ version_1_2_snapshot_t_201703081212, version_1_2_snapshot_t_201603081212, "1.2-SNAPSHOT-T201703081212", "1.2-SNAPSHOT-Timestamp=201603081212", 1 },
			{ version_1,                version_1,                       "1",                             "1",                              0 },
			{ version_1,                version_1_0,                     "1",                             "1.0",                            0 },
			{ version_1,                version_v1_0,                    "1",                             "v1.0",                           0 },
			{ version_1_0,              version_1,                       "1.0",                           "1",                              0 },
			{ version_1_0,              version_1_0,                     "1.0",                           "1.0",                            0 },
			{ version_1_0,              version_v1_0,                    "1.0",                           "v1.0",                           0 },
			{ version_v1_0,             version_1,                       "v1.0",                          "1",                              0 },
			{ version_v1_0,             version_1_0,                     "v1.0",                          "1.0",                            0 },
			{ version_v1_0,             version_v1_0,                    "v1.0",                          "v1.0",                           0 },
			{ version_1_2,              version_1_2,                     "1.2",                           "1.2",                            0 },
			{ version_1_2,              version_1_2_it,                  "1.2",                           "1.2(it)",                        0 },
			{ version_1_2,              version_1_2_snapshot_it,         "1.2",                           "1.2-SNAPSHOT(it)",               0 },
			{ version_1_2_it,           version_1_2,                     "1.2(it)",                       "1.2",                            0 },
			{ version_1_2_it,           version_1_2_it,                  "1.2(it)",                       "1.2(it)",                        0 },
			{ version_1_2_it,           version_1_2_snapshot_it,         "1.2(it)",                       "1.2-SNAPSHOT(it)",               0 },
			{ version_1_2_snapshot,     version_1_2_snapshot,            "1.2-SNAPSHOT",                  "1.2-SNAPSHOT",                   0 },
			{ version_1_2_snapshot_it,  version_1_2,                     "1.2-SNAPSHOT(it)",              "1.2",                            0 },
			{ version_1_2_snapshot_it,  version_1_2_it,                  "1.2-SNAPSHOT(it)",              "1.2(it)",                        0 },
			{ version_1_2_snapshot_it,  version_1_2_snapshot_it,         "1.2-SNAPSHOT(it)",              "1.2-SNAPSHOT(it)",               0 },
			{ version_1_2_beta,         version_1_2_beta,                "1.2-Beta",                      "1.2-Beta",                       0 },
			{ version_1_2_beta2,        version_1_2_beta2,               "1.2-BETA2",                     "1.2-BETA2",                      0 },
			{ version_v2_0,             version_v2_0,                    "v2.0",                          "v2.0",                           0 },
			{ version_1,                version_1_2,                     "1",                             "1.2",                            -1 },
			{ version_1,                version_1_2_it,                  "1",                             "1.2(it)",                        -1 },
			{ version_1,                version_1_2_snapshot,            "1",                             "1.2-SNAPSHOT",                   -1 },
			{ version_1,                version_1_2_snapshot_it,         "1",                             "1.2-SNAPSHOT(it)",               -1 },
			{ version_1,                version_1_2_beta,                "1",                             "1.2-Beta",                       -1 },
			{ version_1,                version_1_2_beta2,               "1",                             "1.2-BETA2",                      -1 },
			{ version_1,                version_v2_0,                    "1",                             "v2.0",                           -1 },
			{ version_1_0,              version_1_2,                     "1.0",                           "1.2",                            -1 },
			{ version_1_0,              version_1_2_it,                  "1.0",                           "1.2(it)",                        -1 },
			{ version_1_0,              version_1_2_snapshot,            "1.0",                           "1.2-SNAPSHOT",                   -1 },
			{ version_1_0,              version_1_2_snapshot_it,         "1.0",                           "1.2-SNAPSHOT(it)",               -1 },
			{ version_1_0,              version_1_2_beta,                "1.0",                           "1.2-Beta",                       -1 },
			{ version_1_0,              version_1_2_beta2,               "1.0",                           "1.2-BETA2",                      -1 },
			{ version_1_0,              version_v2_0,                    "1.0",                           "v2.0",                           -1 },
			{ version_v1_0,             version_1_2,                     "v1.0",                          "1.2",                            -1 },
			{ version_v1_0,             version_1_2_it,                  "v1.0",                          "1.2(it)",                        -1 },
			{ version_v1_0,             version_1_2_snapshot,            "v1.0",                          "1.2-SNAPSHOT",                   -1 },
			{ version_v1_0,             version_1_2_snapshot_it,         "v1.0",                          "1.2-SNAPSHOT(it)",               -1 },
			{ version_v1_0,             version_1_2_beta,                "v1.0",                          "1.2-Beta",                       -1 },
			{ version_v1_0,             version_1_2_beta2,               "v1.0",                          "1.2-BETA2",                      -1 },
			{ version_v1_0,             version_v2_0,                    "v1.0",                          "v2.0",                           -1 },
			{ version_1_2_snapshot,     version_1_2,                     "1.2-SNAPSHOT",                  "1.2",                            -1 },
			{ version_1_2_snapshot,     version_1_2_it,                  "1.2-SNAPSHOT",                  "1.2(it)",                        -1 },
			{ version_1_2_snapshot,     version_1_2_snapshot_it,         "1.2-SNAPSHOT",                  "1.2-SNAPSHOT(it)",               -1 },
			{ version_1_2_snapshot,     version_v2_0,                    "1.2-SNAPSHOT",                  "v2.0",                           -1 },
			{ version_1_2,              version_v2_0,                    "1.2",                           "v2.0",                           -1 },
			{ version_1_2_snapshot_it,  version_v2_0,                    "1.2-SNAPSHOT(it)",              "v2.0",                           -1 },
			{ version_1_2_it,           version_v2_0,                    "1.2(it)",                       "v2.0",                           -1 },
			{ version_1_2_beta,         version_1_2,                     "1.2-Beta",                      "1.2",                            -1 },
			{ version_1_2_beta,         version_1_2_beta2,               "1.2-Beta",                      "1.2-BETA2",                      -1 },
			{ version_1_2_beta,         version_1_2_it,                  "1.2-Beta",                      "1.2(it)",                        -1 },
			{ version_1_2_beta,         version_1_2_snapshot,            "1.2-Beta",                      "1.2-SNAPSHOT",                   -1 },
			{ version_1_2_beta,         version_1_2_snapshot_it,         "1.2-Beta",                      "1.2-SNAPSHOT(it)",               -1 },
			{ version_1_2_beta,         version_v2_0,                    "1.2-Beta",                      "v2.0",                           -1 },
			{ version_1_2_beta2,        version_1_2,                     "1.2-BETA2",                     "1.2",                            -1 },
			{ version_1_2_beta2,        version_1_2_it,                  "1.2-BETA2",                     "1.2(it)",                        -1 },
			{ version_1_2_beta2,        version_1_2_snapshot,            "1.2-BETA2",                     "1.2-SNAPSHOT",                   -1 },
			{ version_1_2_beta2,        version_1_2_snapshot_it,         "1.2-BETA2",                     "1.2-SNAPSHOT(it)",               -1 },
			{ version_1_2_beta2,        version_v2_0,                    "1.2-BETA2",                     "v2.0",                           -1 },
			{ version_2_0_snapshot,     version_v2_0,                    "2.0-SNAPSHOT",                  "v2.0",                           -1 },
			{ version_1_2_snapshot_t_201603081212, version_1_2_snapshot_t_201703081212, "1.2-SNAPSHOT-Timestamp=201603081212", "1.2-SNAPSHOT-T201703081212", -1 },
		});
	}

	@Parameterized.Parameter(0) public Version a;
	@Parameterized.Parameter(1) public Version b;
	@Parameterized.Parameter(2) public String aName;
	@Parameterized.Parameter(3) public String bName;
	@Parameterized.Parameter(4) public int expected;

	@Test
	public void testNewerThan()
	{
		if(expected > 0)
			assertTrue(aName + " should be newer than " + bName, a.newerThan(b));
		else
			assertFalse(aName + " should not be newer than " + bName, a.newerThan(b));
	}

	@Test
	public void testNewerOrEqualThan()
	{
		if(expected >= 0)
			assertTrue(aName + " should be newer or equal to " + bName, a.newerOrEqualThan(b));
		else
			assertFalse(aName + " should not be newer or equal to " + bName, a.newerOrEqualThan(b));
	}

	@Test
	public void testOlderThan()
	{
		if(expected < 0)
			assertTrue(aName + " should be older than " + bName, a.olderThan(b));
		else
			assertFalse(aName + " should not be older than " + bName, a.olderThan(b));
	}

	@Test
	public void testOlderOrEqualThan()
	{
		if(expected <= 0)
			assertTrue(aName + " should be older or equal to " + bName, a.olderOrEqualThan(b));
		else
			assertFalse(aName + " should not be older or equal to " + bName, a.olderOrEqualThan(b));
	}
}
