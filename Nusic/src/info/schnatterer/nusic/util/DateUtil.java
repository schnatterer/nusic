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
}
