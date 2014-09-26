/* Copyright (C) 2014 Johannes Schnatterer
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
package info.schnatterer.nusic.application;

import info.schnatterer.nusic.Constants;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * Extended version of {@link Application} that contains a version number
 * mechanism similar to the one of
 * {@link android.database.sqlite.SQLiteOpenHelper}: It calls
 * {@link #onUpgrade(int, int)} on the derived concrete application class.<br/>
 * <br/>
 * <b>Important:</b> In the concrete class, call <code>super.onCreate()</code>
 * at the end of the concrete class' {@link #onCreate()} method.
 * 
 * @author schnatterer
 *
 */
public abstract class AbstractApplication extends Application {
	static final String KEY_LAST_APP_VERSION = "last_app_version";
	/** Last app version on first start ever of the app. */
	static final int DEFAULT_LAST_APP_VERSION = -1;

	private static Context context;

	private SharedPreferences sharedPreferences;

	private static int lastVersionCode;
	private static int currentVersionCode;
	private static String currentVersionName;
	private static AppStart appStart = null;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();

		sharedPreferences = getApplicationContext().getSharedPreferences(
				"AbstractApplicationPreferences", Context.MODE_PRIVATE);
		currentVersionName = createVersionName();
		appStart = handleAppVersion();
	}

	/**
	 * Reads the human readable version name from the package manager.
	 * 
	 * @return the version or <code>ErrorReadingVersion</code> if an error
	 *         Occurs.
	 */
	private String createVersionName() {
		String versionName;
		try {
			versionName = getApplicationContext()
					.getPackageManager()
					.getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.w(Constants.LOG, "Unable to read version name", e);
			versionName = "ErrorReadingVersion";
		}
		return versionName;
	}

	/**
	 * Finds out if app was started for the first time (after an upgrade). If so
	 * calls {@link #onUpgrade(int, int)}.
	 * 
	 * @return
	 * 
	 */
	AppStart handleAppVersion() {
		PackageInfo pInfo;
		try {
			pInfo = getApplicationContext().getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(), 0);
			lastVersionCode = sharedPreferences.getInt(KEY_LAST_APP_VERSION,
					DEFAULT_LAST_APP_VERSION);

			// String versionName = pInfo.versionName;
			currentVersionCode = pInfo.versionCode;

			// Update version in preferences
			if (currentVersionCode != lastVersionCode) {
				sharedPreferences.edit()
						.putInt(KEY_LAST_APP_VERSION, currentVersionCode)
						.commit();
				if (lastVersionCode == DEFAULT_LAST_APP_VERSION) {
					onFirstCreate();
					return AppStart.FIRST;
				} else {
					onUpgrade(lastVersionCode, currentVersionCode);
					return AppStart.UPGRADE;
				}
			}
		} catch (NameNotFoundException e) {
			Log.w(Constants.LOG,
					"Unable to determine current app version from pacakge manager. Defenisvely assuming normal app start.");
		}
		return AppStart.NORMAL;
	}

	/**
	 * Called when app is started for the first time after a new installation.
	 * This is where an initial welcome screen could be shown.
	 * 
	 * @return
	 */
	protected abstract void onFirstCreate();

	/**
	 * Called when app was first started after an upgrade. The implementation
	 * should use this method to clean up preferences no longer needed show
	 * change log or do anything else it needs to upgrade to the new version.
	 * 
	 * @param oldVersion
	 *            the version the app was last started with
	 * @param newVersion
	 *            the current version of the app
	 */
	protected abstract void onUpgrade(int oldVersion, int newVersion);

	/**
	 * @return the human readable version name.
	 */
	public static String getCurrentVersionName() {
		return currentVersionName;
	}

	/**
	 * Finds out if app was started for the first time (ever or in the current
	 * version).
	 *
	 * @return the type of app start
	 */
	public static AppStart getAppStart() {
		return appStart;
	}

	/**
	 * Distinguishes different kinds of app starts: <li>
	 * <ul>
	 * First start ever ({@link #FIRST})
	 * </ul>
	 * <ul>
	 * First start in this version ({@link #UPGRADE})
	 * </ul>
	 * <ul>
	 * Normal app start ({@link #NORMAL})
	 * </ul>
	 *
	 * @author schnatterer
	 *
	 */
	public enum AppStart {
		FIRST, UPGRADE, NORMAL;
	}

	public static int getLastVersionCode() {
		return lastVersionCode;
	}

	public static int getCurrentVersionCode() {
		return currentVersionCode;
	}

	protected static void setAppStart(AppStart appStart) {
		AbstractApplication.appStart = appStart;
	}

	protected static void setLastVersionCode(int lastVersionCode) {
		AbstractApplication.lastVersionCode = lastVersionCode;
	}

	/**
	 * Returns a "static" application context. Don't try to create dialogs on
	 * this, it's not gonna work!
	 * 
	 * @return
	 */
	public static Context getContext() {
		return context;
	}
}