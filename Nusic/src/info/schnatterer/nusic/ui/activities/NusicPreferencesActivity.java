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

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.service.ReleaseService;
import info.schnatterer.nusic.service.ServiceException;
import info.schnatterer.nusic.service.event.PreferenceChangedListener;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.service.impl.ReleaseServiceImpl;
import info.schnatterer.nusic.ui.fragments.NusicPreferencesFragment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class NusicPreferencesActivity extends SherlockPreferenceActivity {
	public static final String RETURN_KEY_IS_REFRESH_NECESSARY = "isRefreshNecessary";
	public static final String RETURN_KEY_IS_CONTENT_CHANGED = "isContentChanged";

	public static final int KEY_DISPLAY_ALL_RELEASES = R.string.preferences_key_display_all_releases;

	private TimePeriodPreferenceChangedListener timePeriodPreferenceChangedListener = new TimePeriodPreferenceChangedListener();
	private PreferencesServiceSharedPreferences preferencesService = PreferencesServiceSharedPreferences
			.getInstance();
	private boolean isRefreshNecessary = false;
	private boolean isContentChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the back arrow in the header (left of the icon)
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			onCreatePreferenceActivity();
			Preference resetVisibilityButton = findPreferenceActivity(getString(KEY_DISPLAY_ALL_RELEASES));
			resetVisibilityButton
					.setOnPreferenceClickListener(createVisibilityButtonListener(this));
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
		data.putExtra(RETURN_KEY_IS_CONTENT_CHANGED, isContentChanged);
		setResult(RESULT_OK, data);
		super.finish();
	}

	/**
	 * Creates a preference listener that opens an alert dialog. If it is
	 * answered with yes, all releases and artists are set to visible (
	 * {@link Artist#setHidden(Boolean)}=<code>true</code>).
	 * 
	 * @param activity
	 * @return
	 */
	public static OnPreferenceClickListener createVisibilityButtonListener(
			final Activity activity) {
		return new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				new AlertDialog.Builder(activity)
						// .setTitle(
						// R.string.preferences_title_display_all_releases)
						.setMessage(
								R.string.preferences_message_display_all_releases)
						// .setIcon(android.R.drawable.ic_dialog_alert)
						.setNegativeButton(android.R.string.no, null)
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										ReleaseService releaseService = new ReleaseServiceImpl(
												activity);
										try {
											releaseService.showAll();
											// Trigger reload in main activity
											activity.onContentChanged();
										} catch (ServiceException e) {
											Application.toast(e
													.getLocalizedMessageId());
										}
									}
								}).show();
				return true;
			}
		};
	}

	// @Override
	// public boolean onMenuItemSelected(int featureId, MenuItem item) {
	// if (item.getItemId() == android.R.id.home) {
	// // When the back arrow in the header (left of the icon) is clicked,
	// // "go back one activity"
	// finish();
	// }
	// return super.onMenuItemSelected(featureId, item);
	// }

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
			if (key.equals(preferencesService.KEY_DOWNLOAD_RELEASES_TIME_PERIOD)) {
				// Trigger refresh
				isRefreshNecessary = true;
			}
		}
	}
}
