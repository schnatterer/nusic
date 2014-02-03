/* Copyright (C) 2013 Johannes Schnatterer
 * 
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *  
 * This file is part of nusic.
 * 
 * nusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.util;

import java.util.Date;

import junit.framework.TestCase;

public final class DateUtilTest extends TestCase {
	public static final int ADD_MINUTES = 42;
	private static final int MILLIS_TO_MINUTES = 1000 * 60;

	public void testAddMinutes() {
		long testStartMillis = new Date().getTime();
		long actualTime = DateUtil.addMinutes(ADD_MINUTES).getTime();
		long expectedTime = testStartMillis + (ADD_MINUTES * MILLIS_TO_MINUTES);

		assertTrue("Added time amount is not great enough",
				actualTime >= expectedTime);
	}
}
