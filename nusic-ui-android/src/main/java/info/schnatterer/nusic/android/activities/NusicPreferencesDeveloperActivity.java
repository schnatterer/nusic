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

import info.schnatterer.nusic.android.fragments.NusicPreferencesDeveloperFragment;
import info.schnatterer.nusic.android.util.Log;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.event.PreferenceChangedListener;
import info.schnatterer.nusic.ui.R;

import javax.inject.Inject;

import roboguice.activity.RoboAppCompatPreferenceActivity;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
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
public class NusicPreferencesDeveloperActivity extends
		RoboAppCompatPreferenceActivity {
	private LogLevelPreferenceChangedListener logLevelPreferenceChangedListener = new LogLevelPreferenceChangedListener();

	@Inject
	private PreferencesService preferencesService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the back arrow in the header (left of the icon)
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			onCreatePreferenceActivity();
		} else {
			onCreatePreferenceFragment();
		}
	}

	/**
	 * Wraps legacy {@link #onCreate(Bundle)} code for Android < 3 (i.e. API lvl
	 * < 11).
	 */
	@SuppressWarnings("deprecation")
	private void onCreatePreferenceActivity() {
		addPreferencesFromResource(R.xml.preferences_developer);
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
								NusicPreferencesDeveloperFragment.class
										.getName())).commit();
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

	@Override
	protected void onResume() {
		super.onResume();
		preferencesService
				.registerOnSharedPreferenceChangeListener(logLevelPreferenceChangedListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		preferencesService
				.unregisterOnSharedPreferenceChangeListener(logLevelPreferenceChangedListener);
	}

	/**
	 * Listens for a change in the
	 * {@link PreferencesServiceSharedPreferences#KEY_DOWNLOAD_RELEASES_TIME_PERIOD}
	 * preference and triggers an update of the releases.
	 * 
	 * @author schnatterer
	 * 
	 */
	private class LogLevelPreferenceChangedListener implements
			PreferenceChangedListener {
		@Override
		public void onPreferenceChanged(String key, Object newValue) {
			if (key.equals(NusicPreferencesDeveloperActivity.this
					.getString(R.string.preferences_key_log_level))) {
				Log.setRootLogLevel(newValue.toString());
			}
		}
	}
}
