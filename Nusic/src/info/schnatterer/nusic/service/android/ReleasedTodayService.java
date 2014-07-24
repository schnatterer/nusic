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
package info.schnatterer.nusic.service.android;

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.service.PreferencesService;
import info.schnatterer.nusic.service.ReleaseService;
import info.schnatterer.nusic.service.ServiceException;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.service.impl.ReleaseServiceImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Service that checks if an album is released today.
 * 
 * @author schnatterer
 *
 */
public class ReleasedTodayService extends Service {

	private static PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();
	private ReleaseService releaseService = new ReleaseServiceImpl(this);

	private ReleasedTodayServiceBinder binder = new ReleasedTodayServiceBinder();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			List<Release> realsedToday = releaseService.findReleasedToday();
			// if (realsedToday.isEmpty()) {
			// // remove this message
			// Application.notifyInfo("No releases are released today",
			// realsedToday.size());
			// } else {
			// TODO remove this message
			// Application.notifyInfo(realsedToday.size() + " releases today");
			for (Release release : realsedToday) {
				// TODO set artwork
				// release.getArtworkPath();
				// TODO make notification open nusic on the proper tab
				Application.notifyInfo(
						R.string.ReleasedTodayService_ReleasedToday, release
								.getArtist().getArtistName(), release
								.getReleaseName());
			}
			// }
		} catch (ServiceException e) {
			// TODO write message that tells about the error
			Application.notifyInfo(e.getLocalizedMessage());
		}
		return Service.START_STICKY;
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
	 * Schedule this task to run regularly, if enabled in the preferences.
	 * 
	 * @param context
	 */
	public static void schedule(Context context) {
		boolean isEnabled = preferencesService.isEnabledReleasedToday();
		if (isEnabled) {
			int hourOfDay = preferencesService
					.getReleasedTodayScheduleHourOfDay();
			int minute = preferencesService.getReleasedTodayScheduleMinute();

			Calendar triggerAtCal = Calendar.getInstance();
			triggerAtCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			triggerAtCal.set(Calendar.MINUTE, minute);

			PendingIntent pintent = PendingIntent.getBroadcast(context, 0,
					new Intent(context,
							ReleasedTodayServiceStarterReceiver.class), 0);

			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			/*
			 * Set a repeating schedule, so there always is a next alarm even
			 * when one alarm should fail for some reason
			 */
			alarm.setInexactRepeating(AlarmManager.RTC,
					triggerAtCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
					pintent);
			Log.d(Constants.LOG,
					"Scheduled task to run again every day, starting at "
							+ new Date(+triggerAtCal.getTimeInMillis()));
		}
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
			BroadcastReceiver {
		@Override
		public void onReceive(final Context context, final Intent intent) {
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
	public class ReleasedTodaySchedulerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			ReleasedTodayService.schedule(context);
		}
	}

}
