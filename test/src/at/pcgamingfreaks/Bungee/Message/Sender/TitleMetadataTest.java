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

package at.pcgamingfreaks.Bungee.Message.Sender;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TitleMetadataTest
{
	@Test
	public void testTitleMetadata()
	{
		TitleMetadata titleMetadata = new TitleMetadata();
		titleMetadata.setFadeIn(10);
		titleMetadata.setFadeOut(20);
		titleMetadata.setStay(50);
		titleMetadata.setSubtitle(false);
		assertEquals("The fade in time should match", 10, titleMetadata.getFadeIn());
		assertEquals("The fade out time should match", 20, titleMetadata.getFadeOut());
		assertEquals("The stay time should match", 50, titleMetadata.getStay());
		assertFalse("The title should not be a subtitle", titleMetadata.isSubtitle());
	}
}