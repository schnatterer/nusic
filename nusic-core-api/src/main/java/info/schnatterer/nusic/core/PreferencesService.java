/**
 * ﻿Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-core-api.
 *
 * nusic-core-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-core-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-core-api.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.core;

import info.schnatterer.nusic.core.event.PreferenceChangedListener;
import info.schnatterer.nusic.data.model.Release;

import java.util.Date;

/**
 * Provides access to the preferences of the application.
 * 
 * @author schnatterer
 * 
 */
public interface PreferencesService {
	/**
	 * Resets all your preferences. Careful with that!
	 */
	void clearPreferences();

	/**
	 * Gets the last time the {@link Release}s have been loaded from the
	 * internet.
	 * 
	 * This is useful to determine the start date for the next refresh.
	 */
	Date getLastReleaseRefresh();

	/**
	 * Set last time the {@link Release}s have been loaded from the internet.
	 * 
	 * This is useful to determine the start date for the next refresh.
	 * 
	 * @return <code>true</code> if successfully written, otherwise
	 *         <code>false</code>
	 */
	boolean setLastReleaseRefresh(Date date);

	/**
	 * @return <code>true</code> if the user has checked to only download images
	 *         on Wi-Fi. Otherwise <code>false</code>
	 */
	boolean isUseOnlyWifi();

	/**
	 * Returns time period in months (from today back in time) for which
	 * releases are downloaded and displayed.
	 * 
	 * @return
	 */
	int getDownloadReleasesTimePeriod();

	void registerOnSharedPreferenceChangeListener(
			PreferenceChangedListener preferenceChangedListener);

	void unregisterOnSharedPreferenceChangeListener(
			PreferenceChangedListener preferenceChangedListener);

	/**
	 * Amount of days between two scheduled (as opposed to manual) refreshs of
	 * the releases.
	 * 
	 * @return
	 */
	int getRefreshPeriod();

	/**
	 * Time period in days beginning now, which defines the "just" in
	 * "just added".
	 * 
	 * @return
	 */
	int getJustAddedTimePeriod();

	Date getNextReleaseRefresh();

	boolean setNextReleaseRefresh(Date date);

	/**
	 * @return <code>true</code> synchronization of releases is started when the
	 *         connection status changes to "connected". Otherwise
	 *         <code>false</code>.
	 * 
	 */
	boolean isEnabledConnectivityReceiver();

	/**
	 * Setting to <code>true</code> starts synchronization of releases when the
	 * connection status changes to "connected". <code>false</code>
	 * syncronization on connection change.
	 * 
	 * @param enabled
	 * @return
	 */
	boolean setEnabledConnectivityReceiver(boolean enabled);

	/**
	 * @return <code>true</code> if the check for albums getting release today
	 *         is enabled.
	 */
	boolean isEnabledNotifyReleasedToday();

	/**
	 * @return <code>true</code> if a notification is shown when new releases
	 *         are found during syncronization.
	 */
	boolean isEnabledNotifyNewReleases();

	/**
	 * @return the hour of day when the check for new releases is performed if
	 *         {@link #isEnabledNotifyReleasedToday()} is <code>true</code>.
	 */
	int getReleasedTodayScheduleHourOfDay();

	/**
	 * Sets the time when checking for the check for new releases is performed
	 * if {@link #isEnabledNotifyReleasedToday()} is <code>true</code>.
	 * 
	 * @param hourOfDay
	 * @param minute
	 * @return
	 */
	boolean setReleasedTodaySchedule(int hourOfDay, int minute);

	/**
	 * @return the minute when the check for new releases is performed if
	 *         {@link #isEnabledNotifyReleasedToday()} is <code>true</code>.
	 */
	int getReleasedTodayScheduleMinute();
}
