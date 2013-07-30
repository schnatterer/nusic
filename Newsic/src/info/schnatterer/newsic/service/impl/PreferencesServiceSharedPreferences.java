package info.schnatterer.newsic.service.impl;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.model.Release;
import info.schnatterer.newsic.service.PreferencesService;
import info.schnatterer.newsic.util.DateUtils;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Implements {@link PreferencesService} using Android's
 * {@link SharedPreferences}.
 * 
 * @author schnatterer
 * 
 */
public class PreferencesServiceSharedPreferences implements PreferencesService {

	/**
	 * The app version code (not the version name!) that was used on the last
	 * start of the app.
	 */
	private static final String KEY_LAST_APP_VERSION = "last_app_version";
	private static final int DEFAULT_LAST_APP_VERSION = -1;

	/**
	 * Last time the {@link Release}s have been loaded from the internet
	 */
	private static final String KEY_LAST_RELEASES_REFRESH = "last_release_refresh";
	private static final int DEFAULT_LAST_RELEASES_REFRESH = 0;

	private final String KEY_DOWLOAD_ONLY_ON_WIFI;
	private final Boolean DEFAULT_DOWLOAD_ONLY_ON_WIFI;

	private final SharedPreferences sharedPreferences;
	// private static Context context = null;
	private static PreferencesService instance = null;

	/**
	 * Caches the result of {@link #checkAppStart()}. To allow idempotent method
	 * calls.
	 */
	private static AppStart appStart = null;

	/**
	 * 
	 * @return A singleton of this class
	 */
	public static final PreferencesService getInstance() {
		if (instance == null) {
			instance = new PreferencesServiceSharedPreferences();
		}
		return instance;
	}

	/**
	 * Creates a {@link PreferencesService} the default shared preferences.
	 */
	protected PreferencesServiceSharedPreferences() {
		this(PreferenceManager.getDefaultSharedPreferences(getContext()));
	}

	/**
	 * Creates a {@link PreferencesService} that uses specific shared
	 * preferences.
	 * 
	 * Useful for testing.
	 */
	protected PreferencesServiceSharedPreferences(
			SharedPreferences sharedPrefernces) {
		// PreferencesServiceSharedPreferences.context = context;
		this.sharedPreferences = sharedPrefernces;

		// // Initialize new preferences with defaults from xml
		// PreferenceManager.setDefaultValues(getContext(), R.xml.preferences,
		// false);

		if (getContext() != null) {
			KEY_DOWLOAD_ONLY_ON_WIFI = getContext().getString(
					R.string.preferences_key_download_only_on_wifi);
			DEFAULT_DOWLOAD_ONLY_ON_WIFI = getContext().getResources()
					.getBoolean(
							R.bool.preferences_default_download_only_on_wifi);
		} else {
			// e.g. for Testing
			KEY_DOWLOAD_ONLY_ON_WIFI = null;
			DEFAULT_DOWLOAD_ONLY_ON_WIFI = null;
		}
	}

	@Override
	public AppStart checkAppStart() {
		if (appStart == null) {
			PackageInfo pInfo;
			try {
				pInfo = getContext().getPackageManager().getPackageInfo(
						getContext().getPackageName(), 0);
				int lastVersionCode = sharedPreferences.getInt(
						KEY_LAST_APP_VERSION, DEFAULT_LAST_APP_VERSION);
				// String versionName = pInfo.versionName;
				int currentVersionCode = pInfo.versionCode;
				appStart = checkAppStart(currentVersionCode, lastVersionCode);
				// Update version in preferences
				sharedPreferences.edit()
						.putInt(KEY_LAST_APP_VERSION, currentVersionCode)
						.commit();
			} catch (NameNotFoundException e) {
				Log.w(Constants.LOG,
						"Unable to determine current app version from pacakge manager. Defenisvely assuming normal app start.");
			}
		}
		return appStart;
	}

	public AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
		if (lastVersionCode == -1) {
			return AppStart.FIRST_TIME;
		} else if (lastVersionCode < currentVersionCode) {
			return AppStart.FIRST_TIME_VERSION;
		} else if (lastVersionCode > currentVersionCode) {
			Log.w(Constants.LOG, "Current version code (" + currentVersionCode
					+ ") is less then the one recognized on last startup ("
					+ lastVersionCode
					+ "). Defenisvely assuming normal app start.");
			return AppStart.NORMAL;
		} else {
			return AppStart.NORMAL;
		}
	}

	@Override
	public Date getLastReleaseRefresh() {
		return DateUtils.loadDate(sharedPreferences.getLong(
				KEY_LAST_RELEASES_REFRESH, DEFAULT_LAST_RELEASES_REFRESH));
	}

	@Override
	public void setLastReleaseRefresh(Date date) {
		sharedPreferences.edit().putLong(KEY_LAST_RELEASES_REFRESH,
				DateUtils.persistDate(date));
	}

	@Override
	public void clearPreferences() {
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();

	}

	@Override
	public boolean isUseOnlyWifi() {
		/*
		 * Should never return null, as Preference is initialized from XML in
		 * constructor.
		 */
		return sharedPreferences.getBoolean(KEY_DOWLOAD_ONLY_ON_WIFI,
				DEFAULT_DOWLOAD_ONLY_ON_WIFI);
	}

	protected static Context getContext() {
		return Application.getContext();
	}
}
