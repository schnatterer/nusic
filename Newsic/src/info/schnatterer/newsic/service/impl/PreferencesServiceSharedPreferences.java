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
	private static final Date DEFAULT_LAST_RELEASES_REFRESH = null;

	private final String KEY_DOWLOAD_ONLY_ON_WIFI;
	private final Boolean DEFAULT_DOWLOAD_ONLY_ON_WIFI;

	private final String KEY_INCLUDE_FUTURE_RELEASES;
	private final Boolean DEFAULT_INCLUDE_FUTURE_RELEASES;

	private final String KEY_DOWNLOAD_RELEASES_TIME_PERIOD;
	private final Integer DEFAULT_DOWNLOAD_RELEASES_TIME_PERIOD;
	
	private final String KEY_FULL_UPDATE;
	private final Boolean DEFAULT_FULL_UPDATE;


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

			KEY_INCLUDE_FUTURE_RELEASES = getContext().getString(
					R.string.preferences_key_include_future_releases);
			DEFAULT_INCLUDE_FUTURE_RELEASES = getContext().getResources()
					.getBoolean(
							R.bool.preferences_default_include_future_releases);

			KEY_DOWNLOAD_RELEASES_TIME_PERIOD = getContext().getString(
					R.string.preferences_key_download_releases_time_period);
			String prefValue = getContext().getResources().getString(
					R.string.preferences_default_download_releases_time_period);
			try {
				DEFAULT_DOWNLOAD_RELEASES_TIME_PERIOD = Integer
						.parseInt(prefValue);
			} catch (NumberFormatException e) {
				throw new RuntimeException(
						"Unable to parse integer from constant \""
								+ R.string.preferences_default_download_releases_time_period
								+ "\", value:" + prefValue, e);
			}
			
			KEY_FULL_UPDATE = getContext().getString(
					R.string.preferences_key_full_update);
			DEFAULT_FULL_UPDATE = getContext().getResources()
					.getBoolean(
							R.bool.preferences_default_full_update);

		} else {
			// e.g. for Testing
			KEY_DOWLOAD_ONLY_ON_WIFI = null;
			DEFAULT_DOWLOAD_ONLY_ON_WIFI = null;

			KEY_INCLUDE_FUTURE_RELEASES = null;
			DEFAULT_INCLUDE_FUTURE_RELEASES = null;

			KEY_DOWNLOAD_RELEASES_TIME_PERIOD = null;
			DEFAULT_DOWNLOAD_RELEASES_TIME_PERIOD = null;
			
			KEY_FULL_UPDATE = null;
			DEFAULT_FULL_UPDATE = null;
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
	public Date getLastSuccessfullReleaseRefresh() {
		long lastReleaseRefreshMillis= sharedPreferences.getLong(
				KEY_LAST_RELEASES_REFRESH, 0);
		if (lastReleaseRefreshMillis == 0) {
			return DEFAULT_LAST_RELEASES_REFRESH;
		}
		return DateUtils.loadDate(lastReleaseRefreshMillis);
	}

	@Override
	public boolean setLastSuccessfullReleaseRefresh(Date date) {
		return sharedPreferences.edit().putLong(KEY_LAST_RELEASES_REFRESH,
				DateUtils.persistDate(date)).commit();
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
	public boolean isIncludeFutureReleases() {
		return sharedPreferences.getBoolean(KEY_INCLUDE_FUTURE_RELEASES,
				DEFAULT_INCLUDE_FUTURE_RELEASES);
	}

	@Override
	public int getDownloadReleasesTimePeriod() {
		String prefValue = sharedPreferences.getString(
				KEY_DOWNLOAD_RELEASES_TIME_PERIOD,
				DEFAULT_DOWNLOAD_RELEASES_TIME_PERIOD.toString());
		try {
			return Integer.parseInt(prefValue);
		} catch (NumberFormatException e) {
			throw new RuntimeException(
					"Unable to parse integer from property \""
							+ KEY_DOWNLOAD_RELEASES_TIME_PERIOD + "\", value:"
							+ prefValue, e);
		}
	}
	
	@Override
	public boolean isFullUpdate() {
		return sharedPreferences.getBoolean(KEY_FULL_UPDATE,
				DEFAULT_FULL_UPDATE);
	}

	protected static Context getContext() {
		return Application.getContext();
	}
}
