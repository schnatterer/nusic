package info.schnatterer.newsic.util;

import java.util.Date;

public final class DateUtils {
	private DateUtils() {
	}

	public static long persistDate(Date date) {
		return date.getTime();
	}

	public static Date loadDate(long date) {
		return new Date(date);
	}
}
