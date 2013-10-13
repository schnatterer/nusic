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
package info.schnatterer.nusic.service;

import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.service.android.LoadNewReleasesService;
import info.schnatterer.nusic.service.android.LoadNewReleasesServiceConnectivityReceiver;
import info.schnatterer.nusic.service.event.PreferenceChangedListener;

import java.util.Date;

/**
 * Provides access to the key-value-preferences of the app.
 * 
 * @author schnatterer
 * 
 */
public interface PreferencesService {
	/**
	 * Distinguishes different kinds of app starts: <li>
	 * <ul>
	 * First start ever ({@link #FIRST_TIME})
	 * </ul>
	 * <ul>
	 * First start in this version ({@link #FIRST_TIME_VERSION})
	 * </ul>
	 * <ul>
	 * Normal app start ({@link #NORMAL})
	 * </ul>
	 * 
	 * @author schnatterer
	 * 
	 */
	public enum AppStart {
		FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
	}

	/**
	 * Finds out started for the first time (ever or in the current version).
	 * 
	 * @return the type of app start
	 */
	AppStart checkAppStart();

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
	 * Set last time the {@link Release}s havebeen loaded from the internet.
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
	 * >Also download and display releases that are not available yet.
	 * 
	 * @return
	 */
	boolean isIncludeFutureReleases();

	/**
	 * Returns time period in months (from today back in time) for which
	 * releases are downloaded and displayed.
	 * 
	 * @return
	 */
	int getDownloadReleasesTimePeriod();

	/**
	 * Always update the complete time period.
	 * 
	 * @return
	 */
	boolean isFullUpdate();

	void registerOnSharedPreferenceChangeListener(
			PreferenceChangedListener preferenceChangedListener);

	void unregisterOnSharedPreferenceChangeListener(
			PreferenceChangedListener preferenceChangedListener);

	/**
	 * Ignores the value of {@link PreferencesService#isFullUpdate()} and does a
	 * full update. Useful when
	 * {@link PreferencesService#getDownloadReleasesTimePeriod()} changed, or
	 * some artists were refreshed with errors.
	 * 
	 * @return
	 */
	boolean isForceFullRefresh();

	/**
	 * Forces the next refresh of the releases to be a full refresh. That is,
	 * ignores {@link PreferencesService#isFullUpdate()}. Useful when
	 * {@link PreferencesService#getDownloadReleasesTimePeriod()} changed, or
	 * some artists were refreshed with errors.
	 * 
	 * @return
	 */
	boolean setForceFullRefresh(boolean forceFullRefresh);

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
	 * @return <code>true</code> when
	 *         {@link LoadNewReleasesServiceConnectivityReceiver} is enabled,
	 *         that is starting {@link LoadNewReleasesService} when receiving
	 *         broadcast. Otherwise <code>false</code>.
	 * 
	 */
	boolean isEnabledConnectivityReceiver();

	/**
	 * Setting to <code>true</code> enables
	 * {@link LoadNewReleasesServiceConnectivityReceiver}, that is starting
	 * {@link LoadNewReleasesService} when receiving broadcast.
	 * <code>false</code> disables receiver.
	 * 
	 * @param enabled
	 * @return
	 */
	boolean setEnabledConnectivityReceiver(boolean enabled);
}
