package info.schnatterer.newsic.service.impl;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.service.PreferencesService;
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
	private static final String LAST_APP_VERSION = "last_app_version";

	private final SharedPreferences sharedPreferences;
	private final Context context;
	private static PreferencesService instance = null;

	/**
	 * Caches the result of {@link #checkAppStart()}. To allow idempotent mehtod
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
	 * Creates a {@link PreferencesService} that uses the {@link Application}'s
	 * context and the default shared preferences.
	 */
	protected PreferencesServiceSharedPreferences() {
		this(Application.getContext());
	}

	/**
	 * Creates a {@link PreferencesService} that uses a specific
	 * <code>context</code> and the default shared preferences of that context.
	 */
	protected PreferencesServiceSharedPreferences(Context context) {
		this(context, PreferenceManager.getDefaultSharedPreferences(context));

	}

	/**
	 * Creates a {@link PreferencesService} that uses a specific
	 * <code>context</code> and specific shared preferences.
	 */
	protected PreferencesServiceSharedPreferences(Context context,
			SharedPreferences sharedPrefernces) {
		this.context = context;
		this.sharedPreferences = sharedPrefernces;
	}

	@Override
	public AppStart checkAppStart() {
		if (appStart == null) {
			PackageInfo pInfo;
			try {
				pInfo = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0);
				int lastVersionCode = sharedPreferences.getInt(
						LAST_APP_VERSION, -1);
				// String versionName = pInfo.versionName;
				int currentVersionCode = pInfo.versionCode;
				appStart = checkAppStart(currentVersionCode, lastVersionCode);
				// Update version in preferences
				sharedPreferences.edit()
						.putInt(LAST_APP_VERSION, currentVersionCode).commit();
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
	public void clearPreferences() {
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();

	}
}
