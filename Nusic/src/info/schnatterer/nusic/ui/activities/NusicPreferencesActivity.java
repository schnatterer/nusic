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
package info.schnatterer.nusic.ui.activities;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.service.event.PreferenceChangedListener;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.ui.fragments.NusicPreferencesFragment;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class NusicPreferencesActivity extends PreferenceActivity {
	public static final String RETURN_KEY_IS_REFRESH_NECESSARY = "isRefreshNecessary";

	private TimePeriodPreferenceChangedListener timePeriodPreferenceChangedListener = new TimePeriodPreferenceChangedListener();
	private PreferencesServiceSharedPreferences preferencesService = PreferencesServiceSharedPreferences
			.getInstance();
	private boolean isRefreshNecessary = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		addPreferencesFromResource(R.xml.preferences);
	}

	/**
	 * Wraps {@link #onCreate(Bundle)} code for Android >= 3 (i.e. API lvl >=
	 * 11).
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void onCreatePreferenceFragment() {
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new NusicPreferencesFragment())
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
		data.putExtra(RETURN_KEY_IS_REFRESH_NECESSARY, isRefreshNecessary);
		setResult(RESULT_OK, data);
		super.finish();
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
			if (key.equals(preferencesService.KEY_DOWNLOAD_RELEASES_TIME_PERIOD)) {
				// Trigger refresh
				isRefreshNecessary = true;
			}
		}
	}
}
