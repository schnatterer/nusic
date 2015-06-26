/**
 * ﻿Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-ui-android.
 *
 * nusic-ui-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-ui-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-ui-android.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.android.activities;

import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.android.fragments.NusicPreferencesFragment;
import info.schnatterer.nusic.android.listeners.PreferenceReleasedTodayTimePickerListener;
import info.schnatterer.nusic.android.listeners.PreferenceVisibilityButtonListener;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.event.PreferenceChangedListener;
import info.schnatterer.nusic.ui.R;

import javax.inject.Inject;

import roboguice.activity.RoboAppCompatPreferenceActivity;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.view.MenuItem;

/**
 * Activity that realizes the preferences of the app.
 * 
 * When the activity is finished it returns an intent that contains the
 * following extras:
 * <ul>
 * <li>{@link #EXTRA_RESULT_IS_CONTENT_CHANGED}</li>
 * <li>{@link #EXTRA_RESULT_IS_REFRESH_NECESSARY}</li>
 * </ul>
 * 
 * @author schnatterer
 *
 */
public class NusicPreferencesActivity extends RoboAppCompatPreferenceActivity {
	/**
	 * Key to the resulting intent's extras that contains the boolean value that
	 * informs if a check for new releases must be performed.<br/>
	 * See {@link #isRefreshNecessary}.
	 */
	public static final String EXTRA_RESULT_IS_REFRESH_NECESSARY = "nusic.intent.extra.preferences.result.isRefreshNecessary";
	/**
	 * Key to the resulting intent's extras that contains the boolean value that
	 * informs if the content of the release tabs needs to be reloaded. <br/>
	 * See {@link #isContentChanged}.
	 */
	public static final String EXTRA_RESULT_IS_CONTENT_CHANGED = "nusic.intent.extra.preferences.result.isContentChanged";

	private TimePeriodPreferenceChangedListener timePeriodPreferenceChangedListener = new TimePeriodPreferenceChangedListener();

	@Inject
	private PreferencesService preferencesService;
	@Inject
	private PreferenceReleasedTodayTimePickerListener releaseTodayTimePickerListener;

	@Inject
	private PreferenceVisibilityButtonListener preferenceVisibilityButtonListener;

	/**
	 * <code>true</code> if a check for new releases must be performed. <br/>
	 * Value is passed back to the calling activity, see
	 * {@value #EXTRA_RESULT_IS_REFRESH_NECESSARY}.
	 */
	private boolean isRefreshNecessary = false;
	/**
	 * <code>true</code> if the release tabs needs to be reloaded. <br/>
	 * Value is passed back to the calling activity, see
	 * {@value #EXTRA_RESULT_IS_CONTENT_CHANGED}.
	 */
	private boolean isContentChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the back arrow in the header (left of the icon)
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			onCreatePreferenceActivity();

			preferenceVisibilityButtonListener.setActivity(this);
			findPreferenceActivity(
					getString(R.string.preferences_key_display_all_releases))
					.setOnPreferenceClickListener(
							preferenceVisibilityButtonListener);

			releaseTodayTimePickerListener.setContext(this);
			findPreferenceActivity(
					getString(R.string.preferences_key_released_today_hour_of_day))
					.setOnPreferenceClickListener(
							releaseTodayTimePickerListener);
			findPreferenceActivity(getString(R.string.preferences_key_about))
					.setTitle(
							getString(R.string.preferences_category_about,
									getString(R.string.app_name)));
			findPreferenceActivity(getString(R.string.preferences_key_version))
					.setSummary(NusicApplication.getCurrentVersionName());
		} else {
			onCreatePreferenceFragment();
		}
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		isContentChanged = true;
	}

	/**
	 * Wraps legacy {@link #findPreference(CharSequence)} code for Android < 3
	 * (i.e. API lvl < 11).
	 */
	@SuppressWarnings("deprecation")
	private Preference findPreferenceActivity(String key) {
		return findPreference(key);
	}

	/**
	 * Wraps legacy {@link #onCreate(Bundle)} code for Android < 3 (i.e. API lvl
	 * < 11).
	 */
	@SuppressWarnings("deprecation")
	private void onCreatePreferenceActivity() {
		addPreferencesFromResource(R.xml.preferences);
	}

	/**
	 * Wraps {@link #onCreate(Bundle)} code for Android >= 3 (i.e. API lvl >=
	 * 11).
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void onCreatePreferenceFragment() {
		getFragmentManager()
				.beginTransaction()
				.replace(
						android.R.id.content,
						Fragment.instantiate(this,
								NusicPreferencesFragment.class.getName()))
				.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		preferencesService
				.registerOnSharedPreferenceChangeListener(timePeriodPreferenceChangedListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		preferencesService
				.unregisterOnSharedPreferenceChangeListener(timePeriodPreferenceChangedListener);
	}

	@Override
	public void finish() {
		// Prepare data intent
		Intent data = new Intent();
		data.putExtra(EXTRA_RESULT_IS_REFRESH_NECESSARY, isRefreshNecessary);
		data.putExtra(EXTRA_RESULT_IS_CONTENT_CHANGED, isContentChanged);
		setResult(RESULT_OK, data);
		super.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// When the back arrow in the header (left of the icon) is clicked,
			// "go back one activity"
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Listens for a change in the
	 * {@link PreferencesServiceSharedPreferences#KEY_DOWNLOAD_RELEASES_TIME_PERIOD}
	 * preference and triggers an update of the releases.
	 * 
	 * @author schnatterer
	 * 
	 */
	private class TimePeriodPreferenceChangedListener implements
			PreferenceChangedListener {
		@Override
		public void onPreferenceChanged(String key, Object newValue) {
			if (key.equals(NusicPreferencesActivity.this
					.getString(R.string.preferences_key_download_releases_time_period))) {
				// Trigger refresh
				isRefreshNecessary = true;
			}
		}
	}
}
