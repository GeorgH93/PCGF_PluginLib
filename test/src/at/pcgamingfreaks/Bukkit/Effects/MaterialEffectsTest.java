/*
 * Copyright (C) 2016 MarkusWME
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MaterialEffectsTest
{
	//@Test
	public void testMaterialEffects()
	{
		MaterialEffects itemCrack = MaterialEffects.ITEM_CRACK;
		//noinspection SpellCheckingInspection
		assertEquals("The name of item crack should match", "iconcrack", itemCrack.getName());
		//noinspection SpellCheckingInspection
		assertEquals("The uppercase name of item crack should match", "ICONCRACK", itemCrack.getNameUpperCase());
		assertEquals("The new name of item crack should match", "ITEM_CRACK", itemCrack.getNewName());
		assertNotNull("The enum of item crack should not be null", itemCrack.getEnum());
	}
}