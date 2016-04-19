/*
 *   Copyright (C) 2016 GeorgH93
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

import at.pcgamingfreaks.Utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilsTest
{
	private final static byte[] byteArray1 = new byte[] { 0x01, 0x02, 0x03 }, byteArray2 = new byte[] { (byte) 0xfd, (byte) 0xda, 0x11 };
	@SuppressWarnings("SpellCheckingInspection")
	private final static String final1 = "010203", final2 = "fdda11";

	@Test
	public void testByteArrayToHex()
	{
		String out1 = Utils.byteArrayToHex(byteArray1), out2 = Utils.byteArrayToHex(byteArray2);
		assertEquals(final1, out1);
		assertEquals(final2, out2);
	}

	private final static String longText = "This is a long text, without any meaningful content.", shortText = "Just a text.", exactText = "This text should not change.";
	private final static int maxLength = exactText.length();
	private final static String longTextFinal = longText.substring(0, maxLength - 1), shortTextFinal = shortText, exactTextFinal = exactText;

	@Test
	public void testLimitStringLength()
	{
		String outLongText = Utils.limitLength(longText, maxLength), outShortText = Utils.limitLength(shortText, maxLength), outExactText = Utils.limitLength(exactText, maxLength);
		assertEquals(longTextFinal, outLongText);
		assertEquals(shortTextFinal, outShortText);
		assertEquals(exactTextFinal, outExactText);
	}

	@Test
	public void testBlockThread()
	{
		//region no blocking
		long startTime = System.currentTimeMillis();
		Utils.blockThread(0);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		assertTrue(elapsedTime < 10);
		//endregion
		//region with blocking
		startTime = System.currentTimeMillis();
		Utils.blockThread(1);
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		assertTrue(elapsedTime > 900 && elapsedTime < 1100); // Give it some tolerance
		//endregion
	}
}