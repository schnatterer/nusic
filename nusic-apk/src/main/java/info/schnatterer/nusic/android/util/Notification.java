/* Copyright (C) 2015 Johannes Schnatterer
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
package info.schnatterer.nusic.android.util;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.android.activities.MainActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Convenience wrapper around android's notification mechanism.
 * 
 * @author schnatterer
 *
 */
public class Notification {

	/**
	 * Enums that keeps track of the notification types used in this
	 * application. Uses {@link #ordinal()} as numeric ID.
	 * 
	 * @author schnatterer
	 */
	public enum NotificationId {
		/** Generic warning. */
		WARNING,
		/** Found new releases (recently added tab). */
		NEW_RELEASE,
		/** A release is published today. */
		RELEASED_TODAY
	}

	/**
	 * Puts out a notification containing a warning symbol. Overwrites any
	 * previous instances of this notification.
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @param text
	 *            first line of text to put out
	 * @param subtext
	 *            second line of text to put out
	 */
	public static void notifyWarning(Context context, String text,
			String subtext) {
		/*
		 * Don't pass any extras, make Activity open in the default tab.
		 */
		notify(context, NotificationId.WARNING, text, subtext,
				android.R.drawable.ic_dialog_alert, null, MainActivity.class,
				null);
	}

	/**
	 * Puts out a notification containing a warning symbol. Overwrites any
	 * previous instances of this notification.
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @param text
	 *            text to put out verbatim
	 */
	public static void notifyWarning(Context context, String text,
			Object... args) {
		/*
		 * Don't pass any extras, make Activity open in the default tab.
		 */
		notify(context, NotificationId.WARNING, String.format(text, args),
				null, android.R.drawable.ic_dialog_alert, null,
				MainActivity.class, null);
	}

	/**
	 * Puts out a notification containing a warning symbol. Overwrites any
	 * previous instances of this notification.
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @param stringId
	 *            ID of a localized string
	 */
	public static void notifyWarning(Context context, int stringId,
			Object... args) {
		notifyWarning(context, context.getString(stringId), args);
	}

	/**
	 * Writes an android notification that has the localized title of the app.
	 * Overwrites any previous with the same <code>id</code>.
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
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
	public static void notify(Context context, NotificationId id, String text,
			String subtext, int smallIconId, Bitmap largeIcon,
			Class<? extends Context> cls, Bundle extras) {
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, cls);
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
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
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
				context).setSmallIcon(smallIconId).setLargeIcon(largeIcon)
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
					.setContentTitle(context.getString(R.string.app_name))
					.setContentText(text).setSubText(subtext);
		}
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// id allows you to update the notification later on.
		notificationManager.notify(id.ordinal(), notificationBuilder.build());
		Log.i(Constants.LOG, "Notifcation: " + text + subtext);
	}
}
