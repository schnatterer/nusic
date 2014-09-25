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
import info.schnatterer.nusic.Constants.Notification;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.service.android.ReleasedTodayService;
import info.schnatterer.nusic.ui.activities.MainActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class NusicApplication extends AbstractApplication {
	private static final String DEPRECATED_PREFERENCES_KEY_REFRESH_SUCCESFUL = "last_release_refresh_succesful";
	private static final String DEPRECATED_PREFERENCES_KEY_FULL_UPDATE = "fullUpdate";
	private static final String DEPRECATED_PREFERENCES_KEY_FUTURE_RELEASES = "includeFutureReleases";
	private static final String DEPRECATED_PREFERENCES_KEY_LAST_APP_VERSION = "last_app_version";

	public static interface NusicVersion {
		/**
		 * v.0.6 last Version before 1.0
		 */
		int V_0_6 = 10;
	}

	private static Context context;

	@Override
	public void onCreate() {
		context = getApplicationContext();

		/*
		 * Create global configuration and initialize ImageLoader with this
		 * configuration
		 */
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).memoryCacheSize(2 * 1024 * 1024)
				.build();
		ImageLoader.getInstance().init(config);

		// Causes onUpgrade() to be called, etc.
		super.onCreate();
	}

	@Override
	protected void onFirstCreate() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());

		/*
		 * Is this actually an first start ever or is it an upgrade from version
		 * < V_0_6? Before using the onUpgrade() mechanism from
		 * AbstractApplication the last version was stored in default shared
		 * preferences. Do all the clean up for this version here.
		 */
		int lastAppVersion = sharedPreferences.getInt(
				DEPRECATED_PREFERENCES_KEY_FUTURE_RELEASES,
				DEFAULT_LAST_APP_VERSION);
		if (lastAppVersion > DEFAULT_LAST_APP_VERSION) {
			// Clean up preferences
			sharedPreferences.edit()
					.remove(DEPRECATED_PREFERENCES_KEY_FUTURE_RELEASES)
					.commit();
			sharedPreferences.edit()
					.remove(DEPRECATED_PREFERENCES_KEY_LAST_APP_VERSION)
					.commit();
			sharedPreferences.edit()
					.remove(DEPRECATED_PREFERENCES_KEY_FULL_UPDATE).commit();
			sharedPreferences.edit()
					.remove(DEPRECATED_PREFERENCES_KEY_REFRESH_SUCCESFUL)
					.commit();
			setLastVersionCode(lastAppVersion);
			setAppStart(AppStart.UPGRADE);
		} else {
			// TODO This is actually the first start ever. Show the welcome
			// first
			// time screen.
		}
		/*
		 * Make sure the Release Today service is scheduled (if not switched off
		 * in preferences). Schedule it only after updates and new installations
		 * to avoid overhead.
		 */
		ReleasedTodayService.schedule(this);
	}

	@Override
	protected void onUpgrade(int oldVersion, int newVersion) {
		// if (oldVersion <= NusicVersion.V_0_6) {
		// TODO show the changelog/release notes
		/*
		 * Make sure the Release Today service is scheduled (if not switched off
		 * in preferences). Schedule it only after updates and new installations
		 * to avoid overhead.
		 */
		ReleasedTodayService.schedule(this);

	}

	/**
	 * Puts out a notification containing a warning symbol. Overwrites any
	 * previous instances of this notification.
	 * 
	 * @param text
	 *            first line of text to put out
	 * @param subtext
	 *            second line of text to put out
	 */
	public static void notifyWarning(String text, String subtext) {
		/*
		 * Don't pass any extras, make Activity open in the default tab.
		 */
		notify(Notification.WARNING, text, subtext,
				android.R.drawable.ic_dialog_alert, null, MainActivity.class,
				null);
	}

	/**
	 * Puts out a notification containing a warning symbol. Overwrites any
	 * previous instances of this notification.
	 * 
	 * @param text
	 *            text to put out verbatim
	 */
	public static void notifyWarning(String text, Object... args) {
		/*
		 * Don't pass any extras, make Activity open in the default tab.
		 */
		notify(Notification.WARNING, String.format(text, args), null,
				android.R.drawable.ic_dialog_alert, null, MainActivity.class,
				null);
	}

	/**
	 * Puts out a notification containing a warning symbol. Overwrites any
	 * previous instances of this notification.
	 * 
	 * @param stringId
	 *            ID of a localized string
	 */
	public static void notifyWarning(int stringId, Object... args) {
		notifyWarning(getContext().getString(stringId), args);
	}

	/**
	 * Writes an android notification that has the localized title of the app.
	 * Overwrites any previous with the same <code>id</code>.
	 * 
	 * @param id
	 *            An identifier for this notification unique within your
	 *            application, can be used to change the notification later
	 * @param text
	 *            first line of text bellow the title
	 * @param subtext
	 *            second line of text bellow the title
	 * @param smallIconId
	 *            A resource ID in the application's package of the drawable to
	 *            use.
	 * @param largeIcon
	 *            large icon that is shown in the ticker and notification.
	 * @param cls
	 *            activity to be launched on click
	 * @param extras
	 *            extended data to be passed to the activity
	 */
	public static void notify(Notification id, String text, String subtext,
			int smallIconId, Bitmap largeIcon, Class<? extends Context> cls,
			Bundle extras) {
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(getContext(), cls);
		/*
		 * Make sure the intent is also delivered when the application is
		 * already running in a task.
		 * 
		 * Note: On Android 2.x the intent is not delivered when another
		 * activity of the same application is running. Instead the application
		 * is only brought to foreground.
		 */
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (extras != null) {
			resultIntent.putExtras(extras);
		}
		/*
		 * The stack builder object will contain an artificial back stack for
		 * the started Activity. This ensures that navigating backward from the
		 * Activity leads out of your application to the home screen.
		 */
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(cls);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		/*
		 * Make sure to deliver the intent just created even though there
		 * already is an intent with the same request ID.
		 */
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
				id.ordinal(), PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				getContext()).setSmallIcon(smallIconId).setLargeIcon(largeIcon)
				.setContentIntent(resultPendingIntent).setAutoCancel(true);
		if (subtext != null
				&& Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			/*
			 * Subtext seems to be available starting with 4.1?! So we don't
			 * show the app name in order to have more room for information. The
			 * user still has the icon to identify the app.
			 */

			notificationBuilder.setContentTitle(text).setContentText(subtext);
		} else {
			notificationBuilder
					.setContentTitle(getContext().getString(R.string.app_name))
					.setContentText(text).setSubText(subtext);
		}
		NotificationManager notificationManager = (NotificationManager) getContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// id allows you to update the notification later on.
		notificationManager.notify(id.ordinal(), notificationBuilder.build());
		Log.i(Constants.LOG, "Notifcation: " + text + subtext);
	}

	/**
	 * Error message for {@link Throwable}s that might not contain a localized
	 * error message.
	 * 
	 * Tells the user that something unexpected has happened in his language and
	 * adds the name of the exception
	 * 
	 * @param t
	 * @return
	 */
	public static String createGenericErrorMessage(Throwable t) {
		return String.format(context.getString(R.string.GenericError), t
				.getClass().getSimpleName());
	}

	public static void toast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void toast(int stringId) {
		Toast.makeText(context, context.getString(stringId), Toast.LENGTH_LONG)
				.show();
	}

	public static void toast(String message, Object... args) {
		toast(String.format(message, args));
	}

	public static void toast(int stringId, Object... args) {
		toast(String.format(context.getString(stringId), args));
	}

	/**
	 * Returns a "static" application context.
	 * 
	 * @return
	 */
	public static Context getContext() {
		return context;
	}
}
