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

public abstract class AbstractApplication extends Application {
	static final String KEY_LAST_APP_VERSION = "last_app_version";
	static final int DEFAULT_LAST_APP_VERSION = -1;

	private SharedPreferences sharedPreferences;

	private static boolean wasUpgraded = false;

	private static String versionName;

	@Override
	public void onCreate() {
		super.onCreate();

		sharedPreferences = getApplicationContext().getSharedPreferences(
				"AbstractApplicationPreferences", Context.MODE_PRIVATE);
		versionName = createVersionName();
		handleAppVersion();
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
	 */
	void handleAppVersion() {
		PackageInfo pInfo;
		try {
			pInfo = getApplicationContext().getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(), 0);
			int lastVersionCode = sharedPreferences.getInt(
					KEY_LAST_APP_VERSION, DEFAULT_LAST_APP_VERSION);
			// String versionName = pInfo.versionName;
			int currentVersionCode = pInfo.versionCode;

			// Update version in preferences
			if (currentVersionCode != lastVersionCode) {
				sharedPreferences.edit()
						.putInt(KEY_LAST_APP_VERSION, currentVersionCode)
						.commit();
				wasUpgraded = true;
				onUpgrade(lastVersionCode, currentVersionCode);
			}
		} catch (NameNotFoundException e) {
			Log.w(Constants.LOG,
					"Unable to determine current app version from pacakge manager. Defenisvely assuming normal app start.");
		}
	}

	protected abstract void onUpgrade(int oldVersion, int newVersion);

	/**
	 * @return the human readable version name.
	 */
	public static String getVersionName() {
		return versionName;
	}

	/**
	 * @return <code>true</code> if the runs the first time after an upgrade.
	 *         Otherwise <code>false</code>.
	 */
	public static boolean wasUpgraded() {
		return wasUpgraded;
	}
}