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

import android.database.Cursor;

public final class DateUtils {
	private DateUtils() {
	}

	public static Long persistDate(Date date) {
		if (date != null) {
			return date.getTime();
		}
		return null;
	}

	/**
	 * Loads a "serialized" date from a {@link Long} object. <b>Note:</b> If the
	 * long is implicitly a primitive long, it does not support
	 * <code>null</code> values and will therefore always result in 1st January
	 * 1970.
	 * 
	 * You
	 * 
	 * @param date
	 * @return
	 */
	public static Date loadDate(Long date) {
		if (date != null) {
			return new Date(date);
		}
		return null;
	}

	/**
	 * Same as {@link #loadDate(Long)}, but returns <code>null</code> (instead
	 * of 1st January 1970), if the specific colum of the cursor is
	 * <code>null</code>.
	 * 
	 * @param cursor
	 * @param index
	 * @return
	 */
	public static Date loadDate(Cursor cursor, int index) {
		if (cursor.isNull(index)) {
			return null;
		}
		return loadDate(cursor.getLong(index));
	}
}
