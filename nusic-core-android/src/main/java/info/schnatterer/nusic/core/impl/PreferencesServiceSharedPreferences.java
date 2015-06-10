/**
 * ﻿Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-core-android.
 *
 * nusic-core-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-core-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-core-android.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.core.impl;

import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.event.PreferenceChangedListener;
import info.schnatterer.nusic.util.DateUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import roboguice.inject.ContextSingleton;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.google.inject.BindingAnnotation;
import com.google.inject.Provider;

/**
 * Provides access to the preferences of the application via android's
 * {@link SharedPreferences}.
 * 
 * @author schnatterer
 *
 */
@ContextSingleton
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
	private final Integer DEFAULT_JUST_ADDED_TIME_PERIOD;
	// Define in constructor!

	public final String KEY_ENABLED_CONNECTIVITY_RECEIVER = "connectivityReceiver";
	public final Boolean DEFAULT_ENABLED_CONNECTIVITY_RECEIVER = Boolean.FALSE;

	/*
	 * Preferences that are defined in constants_prefernces.xml -> accessible
	 * for preferences.xml
	 */

	/*
	 * TODO find less verbose solution for passing XML values from APK to core
	 * See also Moduel, where annotations are defined
	 */
	@Inject
	@PreferencesKeyDownloadOnlyOnWifi
	private String KEY_DOWLOAD_ONLY_ON_WIFI;
	@Inject
	@PreferencesDefaultDownloadOnlyOnWifi
	private Boolean DEFAULT_DOWLOAD_ONLY_ON_WIFI;

	@Inject
	@PreferencesKeyDownloadReleasesTimePeriod
	private String KEY_DOWNLOAD_RELEASES_TIME_PERIOD;
	@Inject
	@PreferencesDefaultDownloadReleasesTimePeriod
	private String DEFAULT_DOWNLOAD_RELEASES_TIME_PERIOD;

	@Inject
	@PreferencesKeyRefreshPeriod
	private String KEY_REFRESH_PERIOD;
	@Inject
	@PreferencesDefaultRefreshPeriod
	private String DEFAULT_REFRESH_PERIOD;

	@Inject
	@PreferencesKeyIsEnabledNotifyReleasedToday
	private String KEY_ENABLED_NOTIFY_RELEASED_TODAY;
	@Inject
	@PreferencesDefaultIsEnabledNotifyReleasedToday
	private Boolean DEFAULT_ENABLED_NOTIFY_RELEASED_TODAY;

	@Inject
	@PreferencesKeyIsEnabledNotifyNewReleases
	private String KEY_ENABLED_NOTIFY_NEW_RELEASES;
	@Inject
	@PreferencesDefaultIsEnabledNotifyNewReleases
	private Boolean DEFAULT_ENABLED_NOTIFY_NEW_RELEASES;

	@Inject
	@PreferencesKeyReleasedTodayHourOfDay
	private String KEY_RELEASED_TODAY_HOUR_OF_DAY;
	@Inject
	@PreferencesDefaultReleasedTodayHourOfDay
	private Integer DEFAULT_RELEASED_TODAY_HOUR_OF_DAY;

	@Inject
	@PreferencesKeyReleasedTodayMinute
	private String KEY_RELEASED_TODAY_MINUTE;
	@Inject
	@PreferencesDefaultReleasedTodayMinute
	private Integer DEFAULT_RELEASED_TODAY_MINUTE;

	private final SharedPreferences sharedPreferences;

	private Set<PreferenceChangedListener> preferenceChangedListeners = new HashSet<PreferenceChangedListener>();

	@Inject
	private static Provider<Context> contextProvider;

	@Inject
	public PreferencesServiceSharedPreferences(
			@PreferencesKeyRefreshPeriod String keyRefreshPeriod,
			@PreferencesDefaultRefreshPeriod String defaultRefreshPerioid) {
		Context context = contextProvider.get();

		this.sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (sharedPreferences != null) {
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		}

		DEFAULT_JUST_ADDED_TIME_PERIOD = parseIntOrThrow(keyRefreshPeriod,
				defaultRefreshPerioid);
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

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesKeyDownloadOnlyOnWifi {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesDefaultDownloadOnlyOnWifi {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesKeyDownloadReleasesTimePeriod {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesDefaultDownloadReleasesTimePeriod {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesKeyRefreshPeriod {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesDefaultRefreshPeriod {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesKeyIsEnabledNotifyReleasedToday {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesDefaultIsEnabledNotifyReleasedToday {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesKeyIsEnabledNotifyNewReleases {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesDefaultIsEnabledNotifyNewReleases {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesKeyReleasedTodayHourOfDay {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesDefaultReleasedTodayHourOfDay {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesKeyReleasedTodayMinute {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	@BindingAnnotation
	public @interface PreferencesDefaultReleasedTodayMinute {
	}
}
