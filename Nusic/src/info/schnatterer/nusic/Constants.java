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

public interface Constants {

	/**
	 * Enums that keeps track of the notification types used in this
	 * application. Just uses {@link #ordinal()} as numeric ID.
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

	String LOG = "info.schnatterer.nusic";
	String APPLICATION_URL = "https://github.com/schnatterer/nusic";
}
