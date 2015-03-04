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
package info.schnatterer.nusic;

import android.app.AlarmManager;
import android.app.PendingIntent;

public interface Constants {

	/**
	 * Enums that keeps track of the notification types used in this
	 * application. Uses {@link #ordinal()} as numeric ID.
	 * 
	 * @author schnatterer
	 */
	public enum Notification {
		/** Generic warning. */
		WARNING,
		/** Found new releases (recently added tab). */
		NEW_RELEASE,
		/** A release is published today. */
		RELEASED_TODAY
	}

	/**
	 * Enum that keeps track of application-wide request codes that are used for
	 * setting repeating alarms {@link PendingIntent}s with {@link AlarmManager}
	 * . Uses {@link #ordinal()} as numeric ID.
	 * 
	 * @author schnatterer
	 *
	 */
	public enum Alarms {
		NEW_RELEASES, RELEASED_TODAY;
	}

	/**
	 * List of that keeps track of application-wide loaders, making sure the IDs
	 * are unique.
	 * 
	 * @author schnatterer
	 *
	 */
	public interface Loaders {
		public static final int RELEASE_LOADER_ALL = 0;
		public static final int RELEASE_LOADER_JUST_ADDED = 1;
		public static final int RELEASE_LOADER_ANNOUNCED = 2;
		public static final int RELEASE_LOADER_AVAILABLE = 3;
	}

	String LOG = "info.schnatterer.nusic";
	String APPLICATION_URL = "https://github.com/schnatterer/nusic";
}
