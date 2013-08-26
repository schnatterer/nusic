package info.schnatterer.nusic.ui.activities;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.service.event.PreferenceChangedListener;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.ui.fragments.NusicPreferencesFragment;
import android.annotation.SuppressLint;
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
	@SuppressLint("NewApi")
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
