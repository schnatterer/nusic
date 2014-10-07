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
package info.schnatterer.nusic.android.activities;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.logic.ReleaseService;
import info.schnatterer.nusic.logic.ServiceException;
import info.schnatterer.nusic.android.service.ReleasedTodayService;
import info.schnatterer.nusic.logic.event.PreferenceChangedListener;
import info.schnatterer.nusic.logic.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.logic.impl.ReleaseServiceImpl;
import info.schnatterer.nusic.android.fragments.NusicPreferencesFragment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

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
public class NusicPreferencesActivity extends SherlockPreferenceActivity {
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
	private static PreferencesServiceSharedPreferences preferencesService = PreferencesServiceSharedPreferences
			.getInstance();
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
			Preference resetVisibilityButton = findPreferenceActivity(getString(R.string.preferences_key_display_all_releases));
			resetVisibilityButton
					.setOnPreferenceClickListener(createVisibilityButtonListener(this));
			Preference releasedTodayTimePicker = findPreferenceActivity(getString(R.string.preferences_key_released_today_hour_of_day));
			releasedTodayTimePicker
					.setOnPreferenceClickListener(createReleasedTodayTimePickerListener(this));
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
		data.putExtra(EXTRA_RESULT_IS_REFRESH_NECESSARY, isRefreshNecessary);
		data.putExtra(EXTRA_RESULT_IS_CONTENT_CHANGED, isContentChanged);
		setResult(RESULT_OK, data);
		super.finish();
	}

	/**
	 * Creates a preference listener that opens an {@link AlertDialog}. If it is
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
			public boolean onPreferenceClick(Preference preference) {
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
											NusicApplication.toast(e
													.getLocalizedMessageId());
										}
									}
								}).show();
				return true;
			}
		};
	}

	/**
	 * Creates a preference listener that opens a {@link TimePicker}. If a new
	 * time is persisted
	 * {@link ReleasedTodayService#schedule(android.content.Context)} is called.
	 * 
	 * @param activity
	 * @return
	 */
	public static OnPreferenceClickListener createReleasedTodayTimePickerListener(
			final Activity activity) {
		return new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				new TimePickerDialog(
						activity,
						new TimePickerDialog.OnTimeSetListener() {
							// Workaround for onTimeSet() being called twice
							boolean isTimeSet = false;

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								if (!isTimeSet) {
									isTimeSet = true;
									if (preferencesService
											.setReleasedTodaySchedule(
													hourOfDay, minute)) {
										ReleasedTodayService.schedule(activity);
									}
								}
							}
						}, preferencesService
								.getReleasedTodayScheduleHourOfDay(),
						preferencesService.getReleasedTodayScheduleMinute(),
						true).show();
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
