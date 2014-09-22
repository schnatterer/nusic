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
package info.schnatterer.nusic.ui.fragments;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.application.NusicApplication;
import info.schnatterer.nusic.ui.activities.NusicPreferencesActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@SuppressLint("NewApi")
public class NusicPreferencesFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		findPreference(getString(R.string.preferences_key_display_all_releases))
				.setOnPreferenceClickListener(
						NusicPreferencesActivity
								.createVisibilityButtonListener(getActivity()));
		findPreference(
				getString(R.string.preferences_key_released_today_hour_of_day))
				.setOnPreferenceClickListener(
						NusicPreferencesActivity
								.createReleasedTodayTimePickerListener(getActivity()));

		findPreference(getString(R.string.preferences_key_about)).setTitle(
				getString(R.string.preferences_category_about,
						getString(R.string.app_name)));
		findPreference(getString(R.string.preferences_key_version)).setSummary(
				NusicApplication.getVersionName());
	}

}
