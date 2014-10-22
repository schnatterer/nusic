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
package info.schnatterer.nusic.logic.impl;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.logic.PreferencesService;
import info.schnatterer.nusic.logic.event.PreferenceChangedListener;
import info.schnatterer.nusic.util.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

/**
 * Provides access to the preferences of the application via android's
 * {@link SharedPreferences}.
 * 
 * @author schnatterer
 *
 */
public class PreferencesServiceSharedPreferences implements PreferencesService,
		OnSharedPreferenceChangeListener {

	/*
	 * Preferences that are not accessible through preferences menu
	 * (preferences.xml)
	 */
	public final String KEY_LAST_RELEASES_REFRESH = "last_release_refresh";
	public final Date DEFAULT_LAST_RELEASES_REFRESH = null;

	public final String KEY_NEXT_RELEASES_REFRESH = "next_release_refresh";
	public final Date DEFAULT_NEXT_RELEASES_REFRESH = null;

	private final String KEY_JUST_ADDED_TIME_PERIOD = "just_added_time_period";
	// Define in constructor!

	public final String KEY_ENABLED_CONNECTIVITY_RECEIVER = "connectivityReceiver";
	public final Boolean DEFAULT_ENABLED_CONNECTIVITY_RECEIVER = Boolean.FALSE;

	/*
	 * Preferences that are defined in constants_prefernces.xml -> accessible
	 * for preferences.xml
	 */
	public final String KEY_DOWLOAD_ONLY_ON_WIFI = getContext().getString(
			R.string.preferences_key_download_only_on_wifi);
	public final Boolean DEFAULT_DOWLOAD_ONLY_ON_WIFI = getContext()
			.getResources().getBoolean(
					R.bool.preferences_default_download_only_on_wifi);

	public final String KEY_DOWNLOAD_RELEASES_TIME_PERIOD = getContext()
			.getString(R.string.preferences_key_download_releases_time_period);
	public final String DEFAULT_DOWNLOAD_RELEASES_TIME_PERIOD = getContext()
			.getResources().getString(
					R.string.preferences_default_download_releases_time_period);

	public final String KEY_REFRESH_PERIOD = getContext().getString(
			R.string.preferences_key_refresh_period);
	public final String DEFAULT_REFRESH_PERIOD = getContext().getResources()
			.getString(R.string.preferences_default_refresh_period);

	private final Integer DEFAULT_JUST_ADDED_TIME_PERIOD = parseIntOrThrow(
			KEY_REFRESH_PERIOD, DEFAULT_REFRESH_PERIOD);

	public final String KEY_ENABLED_NOTIFY_RELEASED_TODAY = getContext()
			.getString(
					R.string.preferences_key_is_enabled_notify_released_today);
	public final Boolean DEFAULT_ENABLED_NOTIFY_RELEASED_TODAY = getContext()
			.getResources()
			.getBoolean(
					R.bool.preferences_default_is_enabled_notify_released_today);

	public final String KEY_ENABLED_NOTIFY_NEW_RELEASES = getContext()
			.getString(R.string.preferences_key_is_enabled_notify_new_releases);
	public final Boolean DEFAULT_ENABLED_NOTIFY_NEW_RELEASES = getContext()
			.getResources().getBoolean(
					R.bool.preferences_default_is_enabled_notify_new_releases);

	public final String KEY_RELEASED_TODAY_HOUR_OF_DAY = getContext()
			.getString(R.string.preferences_key_released_today_hour_of_day);
	public final Integer DEFAULT_RELEASED_TODAY_HOUR_OF_DAY = parseIntOrThrow(
			KEY_RELEASED_TODAY_HOUR_OF_DAY,
			getContext().getString(
					R.string.preferences_default_released_today_hour_of_day));

	public final String KEY_RELEASED_TODAY_MINUTE = getContext().getString(
			R.string.preferences_key_released_today_minute);
	public final Integer DEFAULT_RELEASED_TODAY_MINUTE = parseIntOrThrow(
			KEY_RELEASED_TODAY_MINUTE,
			getContext().getString(
					R.string.preferences_default_released_today_minute));

	private final SharedPreferences sharedPreferences;
	// private static Context context = null;
	private static PreferencesServiceSharedPreferences instance = new PreferencesServiceSharedPreferences();

	private Set<PreferenceChangedListener> preferenceChangedListeners = new HashSet<PreferenceChangedListener>();

	/**
	 * 
	 * @return A singleton of this class
	 */
	public static final PreferencesServiceSharedPreferences getInstance() {
		return instance;
	}

	/**
	 * Creates a {@link PreferencesService} the default shared preferences.
	 */
	protected PreferencesServiceSharedPreferences() {
		// PreferencesServiceSharedPreferences.context = context;
		this.sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());

		if (sharedPreferences != null) {
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		}
	}

	private Integer parseIntFromPreferenceOrThrow(String key,
			String defaultValue) {
		String prefValue = sharedPreferences.getString(key, defaultValue);
		return parseIntOrThrow(key, prefValue);
	}

	private Integer parseIntOrThrow(String key, String prefValue) {
		try {
			return Integer.parseInt(prefValue);
		} catch (NumberFormatException e) {
			throw new RuntimeException(
					"Unable to parse integer from property \"" + key
							+ "\", value:" + prefValue, e);
		}
	}

	@Override
	public Date getLastReleaseRefresh() {
		long lastReleaseRefreshMillis = sharedPreferences.getLong(
				KEY_LAST_RELEASES_REFRESH, 0);
		if (lastReleaseRefreshMillis == 0) {
			return DEFAULT_LAST_RELEASES_REFRESH;
		}
		return DateUtil.toDate(lastReleaseRefreshMillis);
	}

	@Override
	public boolean setLastReleaseRefresh(Date date) {
		return sharedPreferences.edit()
				.putLong(KEY_LAST_RELEASES_REFRESH, DateUtil.toLong(date))
				.commit();
	}

	@Override
	public Date getNextReleaseRefresh() {
		long nextReleaseRefreshMillis = sharedPreferences.getLong(
				KEY_NEXT_RELEASES_REFRESH, 0);
		if (nextReleaseRefreshMillis == 0) {
			return DEFAULT_NEXT_RELEASES_REFRESH;
		}
		return DateUtil.toDate(nextReleaseRefreshMillis);
	}

	@Override
	public boolean setNextReleaseRefresh(Date date) {
		return sharedPreferences.edit()
				.putLong(KEY_NEXT_RELEASES_REFRESH, DateUtil.toLong(date))
				.commit();
	}

	@Override
	public void clearPreferences() {
		sharedPreferences.edit().clear().commit();
	}

	@Override
	public boolean isUseOnlyWifi() {
		return sharedPreferences.getBoolean(KEY_DOWLOAD_ONLY_ON_WIFI,
				DEFAULT_DOWLOAD_ONLY_ON_WIFI);
	}

	@Override
	public int getDownloadReleasesTimePeriod() {
		return parseIntFromPreferenceOrThrow(KEY_DOWNLOAD_RELEASES_TIME_PERIOD,
				DEFAULT_DOWNLOAD_RELEASES_TIME_PERIOD);
	}

	@Override
	public int getRefreshPeriod() {
		return parseIntFromPreferenceOrThrow(KEY_REFRESH_PERIOD,
				DEFAULT_REFRESH_PERIOD);
	}

	@Override
	public int getJustAddedTimePeriod() {
		return sharedPreferences.getInt(KEY_JUST_ADDED_TIME_PERIOD,
				DEFAULT_JUST_ADDED_TIME_PERIOD);
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(
			PreferenceChangedListener preferenceChangedListener) {
		preferenceChangedListeners.add(preferenceChangedListener);
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(
			PreferenceChangedListener preferenceChangedListener) {
		preferenceChangedListeners.remove(preferenceChangedListener);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		for (PreferenceChangedListener preferenceChangedListener : preferenceChangedListeners) {
			preferenceChangedListener.onPreferenceChanged(key,
					sharedPreferences.getAll().get(key));
		}
	}

	@Override
	public boolean isEnabledConnectivityReceiver() {
		return sharedPreferences.getBoolean(KEY_ENABLED_CONNECTIVITY_RECEIVER,
				DEFAULT_ENABLED_CONNECTIVITY_RECEIVER);
	}

	@Override
	public boolean setEnabledConnectivityReceiver(boolean enabled) {
		return sharedPreferences.edit()
				.putBoolean(KEY_ENABLED_CONNECTIVITY_RECEIVER, enabled)
				.commit();
	}

	protected static Context getContext() {
		return NusicApplication.getContext();
	}

	@Override
	public boolean isEnabledNotifyReleasedToday() {
		return sharedPreferences.getBoolean(KEY_ENABLED_NOTIFY_RELEASED_TODAY,
				DEFAULT_ENABLED_NOTIFY_RELEASED_TODAY);
	}

	@Override
	public boolean isEnabledNotifyNewReleases() {
		return sharedPreferences.getBoolean(KEY_ENABLED_NOTIFY_NEW_RELEASES,
				DEFAULT_ENABLED_NOTIFY_NEW_RELEASES);
	}

	@Override
	public int getReleasedTodayScheduleHourOfDay() {
		return sharedPreferences.getInt(KEY_RELEASED_TODAY_HOUR_OF_DAY,
				DEFAULT_RELEASED_TODAY_HOUR_OF_DAY);
	}

	@Override
	public boolean setReleasedTodaySchedule(int hourOfDay, int minute) {
		return sharedPreferences.edit()
				.putInt(KEY_RELEASED_TODAY_HOUR_OF_DAY, hourOfDay)
				.putInt(KEY_RELEASED_TODAY_MINUTE, minute).commit();
	}

	@Override
	public int getReleasedTodayScheduleMinute() {
		return sharedPreferences.getInt(KEY_RELEASED_TODAY_MINUTE,
				DEFAULT_RELEASED_TODAY_MINUTE);
	}

}
