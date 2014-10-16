/* Copyright (C) 2014 Johannes Schnatterer
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
package info.schnatterer.nusic.data.util;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.util.DateUtil;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public final class SqliteUtil {
	private SqliteUtil() {
	}

	public static final Integer FALSE = Integer.valueOf(0);
	public static final Integer TRUE = Integer.valueOf(1);

	/**
	 * Wrapper for {@link ContentValues}'s <code>putTYPE()</code> methods only
	 * puts values != <code>null</code>. This is helpful when updating, as it
	 * ignores all values that are <code>null</code>.
	 * 
	 * @param values
	 * @param column
	 * @param value
	 */
	public static void putIfNotNull(ContentValues values, String column,
			Object value) {
		if (value == null || values == null) {
			return;
		}
		/*
		 * Thanks for not providing a put(String,Object) method to the
		 * Map<String,Object>!! :-(
		 */
		if (value instanceof Byte) {
			values.put(column, (Byte) value);
		} else if (value instanceof Short) {
			values.put(column, (Short) value);
		} else if (value instanceof Integer) {
			values.put(column, (Integer) value);
		} else if (value instanceof Long) {
			values.put(column, (Long) value);
		} else if (value instanceof Float) {
			values.put(column, (Float) value);
		} else if (value instanceof Double) {
			values.put(column, (Double) value);
		} else if (value instanceof Boolean) {
			values.put(column, (Boolean) value);
		} else if (value instanceof byte[]) {
			values.put(column, (byte[]) value);
		} else if (value instanceof String) {
			values.put(column, (String) value);
		} else {
			// Hope for the best and convert it to a string
			Log.w(Constants.LOG, "Column: " + column
					+ "Trying to put non primitive value to ContentValues: "
					+ value + ". Converting to string.");
			values.put(column, value.toString());
		}
	}

	/**
	 * Retrieves a {@link Date} value from a cursor.<br/>
	 * <b>Note: The database field is supposed to be a <code>INTEGER</code>
	 * field.</b>
	 * 
	 * @param cursor
	 *            cursor to retrieve the value from
	 * @param index
	 *            index of the field within the cursor
	 * @return the converted {@link Date} object or <code>null</code> if the
	 *         field is <code>null</code>
	 */
	public static Date loadDate(Cursor cursor, int index) {
		return DateUtil.toDate(loadLong(cursor, index));
	}

	/**
	 * Retrieves a {@link Boolean} value from a cursor.<br/>
	 * <b>Note: The database field is supposed to be a <code>INTEGER</code>
	 * field.</b>
	 * 
	 * @param cursor
	 *            cursor to retrieve the value from
	 * @param index
	 *            index of the field within the cursor
	 * @return the converted {@link Boolean} object or <code>null</code> if the
	 *         field is <code>null</code>
	 */
	public static Boolean loadBoolean(Cursor cursor, int index) {
		Integer intValue = loadInteger(cursor, index);
		return toBoolean(intValue);
	}

	//
	// public static Integer storeBoolean() {
	//
	// }

	/**
	 * Converts an {@link Integer} to {@link Boolean} according to the following
	 * rules:
	 * <ul>
	 * <li><code>null</code> yields <code>null</code></li>
	 * <li>Only <code>0</code> yields {@link Boolean#FALSE}</li>
	 * <li>Everything else yields {@link Boolean#TRUE}</li>
	 * </ul>
	 * 
	 * @param intValue
	 *            the numeric value to convert
	 * @return {@link Boolean} representation of <code>intValue</code>
	 */
	public static Boolean toBoolean(Integer intValue) {
		if (intValue == null) {
			return null;
		}
		if (FALSE.equals(intValue)) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * Converts a {@link Boolean} to {@link Integer} according to the following
	 * rules:
	 * <ul>
	 * <li><code>null</code> yields <code>null</code></li>
	 * <li>Only {@link Boolean#FALSE} yields <code>0</code></li>
	 * <li>Everything else yields <code>1</code></li>
	 * </ul>
	 * 
	 * @param intValue
	 *            the numeric value to convert
	 * @return {@link Boolean} representation of <code>intValue</code>
	 */
	public static Integer toInteger(Boolean boolValue) {
		if (boolValue == null) {
			return null;
		}
		if (Boolean.FALSE.equals(boolValue)) {
			return FALSE;
		}
		return TRUE;
	}

	/**
	 * Retrieves an {@link Integer} value from a cursor. Contrary to
	 * {@link Cursor#getInt(int)} this returns <code>null</code> when the field
	 * is <code>null</code> in the database..
	 * 
	 * @param cursor
	 *            cursor to retrieve the value from
	 * @param index
	 *            index of the field within the cursor
	 * @return the boxed {@link Integer} value or <code>null</code> if the field
	 *         is <code>null</code>
	 */
	public static Integer loadInteger(Cursor cursor, int index) {
		if (!cursor.isNull(index)) {
			return cursor.getInt(index);
		}
		return null;
	}

	/**
	 * Retrieves a {@link Long} value from a cursor. Contrary to
	 * {@link Cursor#getLong(int)} this returns <code>null</code> when the field
	 * is <code>null</code> in the database..
	 * 
	 * @param cursor
	 *            cursor to retrieve the value from
	 * @param index
	 *            index of the field within the cursor
	 * @return the boxed {@link Long} value or <code>null</code> if the field is
	 *         <code>null</code>
	 */
	public static Long loadLong(Cursor cursor, int index) {
		if (!cursor.isNull(index)) {
			return cursor.getLong(index);
		}
		return null;
	}
}
