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
 *
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

public final class DateUtil {
	public static final int MILLIS_TO_MINUTES = 60000;

	private DateUtil() {
	}

	public static Long toLong(Date date) {
		if (date != null) {
			return date.getTime();
		}
		return null;
	}

	/**
	 * Converts a "serialized" date from a {@link Long} object. <b>Note:</b> If
	 * the long is implicitly a primitive long, it does not support
	 * <code>null</code> values and will therefore always result in 1st January
	 * 1970.
	 * 
	 * You
	 * 
	 * @param date
	 * @return
	 */
	public static Date toDate(Long date) {
		if (date != null) {
			return new Date(date);
		}
		return null;
	}

	/**
	 * Returns the current date + a number of <code>minutes</code>.
	 * 
	 * @param minutes
	 * @return
	 */
	public static Date addMinutes(int minutes) {
		return new Date(System.currentTimeMillis() + minutes
				* MILLIS_TO_MINUTES);
	}

	/**
	 * Extracts the date (not the time) from <code>cal</code> and returns the
	 * corresponding value for this date and midnight in UTC.<br/>
	 * <br/>
	 * For example: Input <code>2014-07-22T01:11:11+02:00</code> (UTC+2) equals
	 * <code>2014-07-<b>21</b>T23:11:11Z</code> (UTC). But the when passed to
	 * the method this returns <code>2014-07-<b>22</b>T00:00:00Z</code>, that is
	 * input date at midnight UTC.
	 * 
	 * @param cal
	 * @return
	 */
	public static long midnightUtc(Calendar cal) {
		// The "local date"
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		// Switch to UTC
		cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// Make sure to get EXACTLY midnight (set ms part of the date)
		cal.setTimeInMillis(0);
		// Set the local date for UTC and midnight
		cal.set(year, month, day, 0, 0, 0);
		return cal.getTimeInMillis();
	}

	/**
	 * @return the timestamp for tomorrow's date at midnight in UTC.
	 * @see #midnightUtc(Calendar)
	 */
	public static long tomorrowMidnightUtc() {
		// Get timestamp for tomorrow midnight.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return midnightUtc(cal);
	}

	/**
	 * @return the timestamp for today's date at midnight in UTC.
	 * @see #midnightUtc(Calendar)
	 */
	public static long todayMidnightUtc() {
		return midnightUtc(Calendar.getInstance());
	}
}
