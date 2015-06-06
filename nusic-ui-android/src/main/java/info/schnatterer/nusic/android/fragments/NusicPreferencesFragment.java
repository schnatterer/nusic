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
package info.schnatterer.nusic.android.fragments;

import info.schnatterer.nusic.ui.R;
import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.android.listeners.PreferenceReleasedTodayTimePickerListener;
import info.schnatterer.nusic.android.listeners.PreferenceVisibilityButtonListener;

import javax.inject.Inject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@SuppressLint("NewApi")
public class NusicPreferencesFragment extends PreferenceFragment {

	@Inject
	private PreferenceReleasedTodayTimePickerListener releaseTodayTimePickerListener;
	@Inject
	private PreferenceVisibilityButtonListener preferenceVisibilityButtonListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		preferenceVisibilityButtonListener.setActivity(getActivity());
		findPreference(getString(R.string.preferences_key_display_all_releases))
				.setOnPreferenceClickListener(
						preferenceVisibilityButtonListener);

		releaseTodayTimePickerListener.setContext(getActivity());
		findPreference(
				getString(R.string.preferences_key_released_today_hour_of_day))
				.setOnPreferenceClickListener(releaseTodayTimePickerListener);

		findPreference(getString(R.string.preferences_key_about)).setTitle(
				getString(R.string.preferences_category_about,
						getString(R.string.app_name)));
		findPreference(getString(R.string.preferences_key_version)).setSummary(
				NusicApplication.getCurrentVersionName());
	}
}
