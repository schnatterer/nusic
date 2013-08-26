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
