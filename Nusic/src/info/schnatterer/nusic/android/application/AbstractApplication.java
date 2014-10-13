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
package info.schnatterer.nusic.android.application;

import info.schnatterer.nusic.Constants;

import java.io.InputStream;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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

	/** Name of the resource type "string" as in <code>@string/...</code> */
	private static final String DEF_TYPE_STRING = "string";

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
	 * version).<br/>
	 * <b>Note:</b> Be careful when using this for creating welcome screens,
	 * e.g. in {@link android.app.Activity}.onCreate(), as this information is gathered on
	 * the first start and kept in memory statically. The app might be redrawn
	 * multiple times during the lifetime of this information. That is
	 * onCreate() will be called multiple times but the welcome screen is
	 * supposed to be shown only once. So addition logic is necessary within the
	 * activity to distinguish if this is the first time the activity is
	 * created.
	 *
	 * @return the type of app start
	 */
	public static AppStart getAppStart() {
		return appStart;
	}

	/**
	 * Returns the string value of a string resource (e.g. defined in
	 * <code>values.xml</code>).
	 * 
	 * @param name
	 * @return the value of the string resource or <code>null</code> if no
	 *         resource found for id
	 */
	public static String getStringByName(String name) {
		int resourceId = getResourceId(DEF_TYPE_STRING, name);
		if (resourceId != 0) {
			return getContext().getString(resourceId);
		} else {
			return null;
		}
	}

	/**
	 * Finds the numeric id of a string resource (e.g. defined in
	 * <code>values.xml</code>).
	 * 
	 * @param defType
	 *            Optional default resource type to find, if "type/" is not
	 *            included in the name. Can be null to require an explicit type.
	 * 
	 * @param name
	 *            the name of the desired resource
	 * @return the associated resource identifier. Returns 0 if no such resource
	 *         was found. (0 is not a valid resource ID.)
	 */
	private static int getResourceId(String defType, String name) {
		return getContext().getResources().getIdentifier(name, defType,
				getContext().getPackageName());
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

	public Bitmap createScaledBitmap(InputStream inputStream) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return createScaledBitmapLegacy(inputStream);
		} else {
			return createScaledBitmapModern(inputStream);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private Bitmap createScaledBitmapModern(InputStream inputStream) {
		Bitmap artwork = null;
		artwork = Bitmap.createScaledBitmap(
				BitmapFactory.decodeStream(inputStream),
				(int) this.getResources().getDimension(
						android.R.dimen.notification_large_icon_width),
				(int) this.getResources().getDimension(
						android.R.dimen.notification_large_icon_height), false);
		return artwork;
	}

	private Bitmap createScaledBitmapLegacy(InputStream inputStream) {
		/*
		 * As we don't know the size of the notification icon bellow API lvl 11,
		 * theses devices will just use the standard icon.
		 */
		return null;
	}

}