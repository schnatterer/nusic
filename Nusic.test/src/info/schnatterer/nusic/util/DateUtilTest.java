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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Assert;
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

	/**
	 * Test for {@link DateUtil#midnightUtc(Calendar)} where in UTC it is the
	 * 21st of July, but our local date is already 22nd of July (GMT+x).
	 */
	public void testMidnightUtcPlus() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC+2"));
		// 2014-07-22T01:44:55+02:00 (UTC+2) equals 2014-07-21T23:44:55Z (UTC).
		cal.set(2014, Calendar.JULY, 22, 1, 44, 55);
		// 2014-07-22T00:00:00Z,
		long expected = 1405987200000l;
		long actual = DateUtil.midnightUtc(cal);
		Assert.assertEquals("Unexpected date returned", expected, actual);
		// Don't return 2014-07-21T00:00:00Z
	}

	/**
	 * Test for {@link DateUtil#midnightUtc(Calendar)} where in UTC it is the
	 * 23rd of July, but our local date still is 22nd of July (GMT-x).
	 */
	public void testMidnightUtcMinus() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-2"));
		// 2014-07-22T23:44:55-02:00 (UTC+2) equals 2014-07-23T01:44:55Z (UTC).
		cal.set(2014, Calendar.JULY, 22, 23, 44, 55);
		// 2014-07-22T00:00:00Z,
		long expected = 1405987200000l;
		long actual = DateUtil.midnightUtc(cal);
		Assert.assertEquals("Unexpected date returned", expected, actual);
		// Don't return 2014-07-23T00:00:00Z
	}
}
