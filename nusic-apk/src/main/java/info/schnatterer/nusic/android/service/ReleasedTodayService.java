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
package info.schnatterer.nusic.android.service;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.Constants.Notification;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.android.activities.MainActivity;
import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.android.util.ImageUtil;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.dao.ArtworkDao;
import info.schnatterer.nusic.data.dao.ArtworkDao.ArtworkType;
import info.schnatterer.nusic.data.model.Release;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import roboguice.receiver.RoboBroadcastReceiver;
import roboguice.service.RoboService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Service that checks if an album is released today.
 * 
 * @author schnatterer
 *
 */
public class ReleasedTodayService extends RoboService {

	@Inject
	private PreferencesService preferencesService;
	@Inject
	private ReleaseService releaseService;
	@Inject
	private ArtworkDao artworkDao;

	private ReleasedTodayServiceBinder binder = new ReleasedTodayServiceBinder();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (preferencesService.isEnabledNotifyReleasedToday()) {
			try {
				List<Release> releasedToday = releaseService
						.findReleasedToday();
				if (releasedToday.size() == 1) {
					notifyReleaseToday(releasedToday.get(0));
				} else if (releasedToday.size() > 1) {
					// If more than one release, put less detail in notification
					notifyReleaseToday(releasedToday.size());
				}
			} catch (ServiceException e) {
				NusicApplication
						.notifyWarning(
								getString(R.string.ReleasedTodayService_ReleasedTodayError),
								e.getLocalizedMessage());
			}
		} else {
			// Stop schedule
			stopSchedule(this);
		}
		return Service.START_STICKY;
	}

	/**
	 * Puts out a notification informing about one release published today.<br/>
	 * <br/>
	 * <br/>
	 * Future calls overwrite any previous instances of this notification still
	 * on display.
	 * 
	 * @param release
	 * 
	 */
	private void notifyReleaseToday(Release release) {
		try {
			Bitmap createScaledBitmap = ImageUtil.createScaledBitmap(
					artworkDao.findStreamByRelease(release, ArtworkType.SMALL),
					this);
			NusicApplication.notify(
					Notification.RELEASED_TODAY,
					getString(R.string.ReleasedTodayService_ReleasedToday),
					release.getArtist().getArtistName() + " - "
							+ release.getReleaseName(), R.drawable.ic_launcher,
					createScaledBitmap, MainActivity.class,
					createExtraActiveTab());
		} catch (DatabaseException e) {
			Log.w(Constants.LOG, "Unable to load artwork for notification. "
					+ release, e);
		} catch (IllegalArgumentException e) {
			Log.w(Constants.LOG, "Unable scale artwork for notification. "
					+ release, e);
		}
	}

	/**
	 * Puts out a notification informing about multiple releases published
	 * today.<br/>
	 * <br/>
	 * Future calls overwrite any previous instances of this notification still
	 * on display.
	 * 
	 * @param nReleases
	 *            the number of releases published today
	 * 
	 * @param text
	 */
	private void notifyReleaseToday(int nReleases) {
		NusicApplication.notify(Notification.RELEASED_TODAY, String.format(
				getString(R.string.ReleasedTodayService_ReleasedTodayMultiple),
				nReleases), null, R.drawable.ic_launcher, null,
				MainActivity.class, createExtraActiveTab());
	}

	/**
	 * Creates an extra bundle that contains the tab to be shown when
	 * {@link MainActivity} is launched.
	 * 
	 * @return
	 */
	private Bundle createExtraActiveTab() {
		Bundle extras = new Bundle();
		extras.putSerializable(MainActivity.EXTRA_ACTIVE_TAB,
				MainActivity.TabDefinition.AVAILABLE);
		return extras;
	}

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class ReleasedTodayServiceBinder extends Binder {
		public ReleasedTodayService getService() {
			return ReleasedTodayService.this;
		}
	}

	/**
	 * Creates the pending intent that is passed to the alarm manager for
	 * scheduling the service.
	 * 
	 * @param context
	 * @return
	 */
	private static PendingIntent createPendingIntent(Context context) {
		PendingIntent pintent = PendingIntent.getBroadcast(context,
				Constants.Alarms.RELEASED_TODAY.ordinal(), new Intent(context,
						ReleasedTodayServiceStarterReceiver.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pintent;
	}

	/**
	 * Cancel this task so it is not scheduled anymore.
	 * 
	 * @param context
	 */
	public void stopSchedule(Context context) {
		((AlarmManager) getSystemService(ALARM_SERVICE))
				.cancel(createPendingIntent(context));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * Broadcast receiver that triggers execution of
	 * {@link ReleasedTodayService}.
	 * 
	 * @author schnatterer
	 * 
	 */
	public static class ReleasedTodayServiceStarterReceiver extends
			RoboBroadcastReceiver {
		@Override
		public void handleReceive(final Context context, final Intent intent) {
			context.startService(new Intent(context, ReleasedTodayService.class));
		}
	}

	/**
	 * Broadcast receiver that schedules execution of
	 * {@link ReleasedTodayService}.
	 * 
	 * @author schnatterer
	 * 
	 */
	public static class ReleasedTodaySchedulerReceiver extends
			RoboBroadcastReceiver {
		@Inject
		ReleasedTodayServiceScheduler releasedTodayServiceScheduler;

		@Override
		public void handleReceive(final Context context, final Intent intent) {
			releasedTodayServiceScheduler.schedule();
		}
	}

	public static class ReleasedTodayServiceScheduler {
		@Inject
		private Context context;
		@Inject
		private PreferencesService preferencesService;

		/**
		 * Schedule this task to run regularly, if enabled in the preferences.
		 * 
		 * @param context
		 */
		public void schedule() {
			boolean isEnabled = preferencesService
					.isEnabledNotifyReleasedToday();
			if (isEnabled) {
				int hourOfDay = preferencesService
						.getReleasedTodayScheduleHourOfDay();
				int minute = preferencesService
						.getReleasedTodayScheduleMinute();

				Calendar triggerAtCal = Calendar.getInstance();
				triggerAtCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
				triggerAtCal.set(Calendar.MINUTE, minute);
				triggerAtCal.set(Calendar.SECOND, 0);
				if (triggerAtCal.getTimeInMillis() < System.currentTimeMillis()) {
					/*
					 * Trigger only for today if time is in the future. If not,
					 * trigger same time tomorrow.
					 * 
					 * If the triggering time is in the past, android will
					 * trigger it directly.
					 */
					Log.d(Constants.LOG,
							"Triggering notification service for tommorrow");
					triggerAtCal.add(Calendar.DAY_OF_MONTH, 1);
				}

				/*
				 * Set a repeating schedule, so there always is a next alarm
				 * even when one alarm should fail for some reason
				 */
				((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
						.setRepeating(AlarmManager.RTC,
								triggerAtCal.getTimeInMillis(),
								AlarmManager.INTERVAL_DAY,
								createPendingIntent(context));
				Log.d(Constants.LOG,
						"Scheduled "
								+ ReleasedTodayService.class.getSimpleName()
								+ " to run again every day, starting at "
								+ new Date(+triggerAtCal.getTimeInMillis()));
			}
		}
	}

}
