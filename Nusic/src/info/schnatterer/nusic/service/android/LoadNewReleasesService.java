/* Copyright (C) 2013 Johannes Schnatterer
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

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.Constants.Notification;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.application.NusicApplication;
import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.service.ConnectivityService;
import info.schnatterer.nusic.service.PreferencesService;
import info.schnatterer.nusic.service.ReleaseRefreshService;
import info.schnatterer.nusic.service.ReleaseService;
import info.schnatterer.nusic.service.ServiceException;
import info.schnatterer.nusic.service.event.ArtistProgressListener;
import info.schnatterer.nusic.service.impl.ConnectivityServiceAndroid;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.service.impl.ReleaseRefreshServiceImpl;
import info.schnatterer.nusic.service.impl.ReleaseServiceImpl;
import info.schnatterer.nusic.ui.activities.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Wraps the android implementation of the business logic service
 * {@link ReleaseRefreshService} in an android {@link Service} in order to allow
 * running outside of the application. In addition, it takes care of the
 * scheduling.
 * 
 * The service requires the {@link #EXTRA_REFRESH_ON_START} to contain
 * <code>true</code> in order to actually start refreshing releases.
 * 
 * @author schnatterer
 * 
 */
public class LoadNewReleasesService extends WakefulService {
	/**
	 * Key to the creating intent's extras that contains a boolean that triggers
	 * loading the release if <code>true</code>.
	 */
	public static final String EXTRA_REFRESH_ON_START = "nusic.intent.extra.refreshOnStart";

	private static PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();
	private ConnectivityService connectivityService = ConnectivityServiceAndroid
			.getInstance();
	private ReleaseRefreshService releasesService;
	private ReleaseService releaseService = new ReleaseServiceImpl(this);

	private List<Artist> errorArtists;
	// private int totalArtists = 0;
	private ProgressListenerNotifications progressListenerNotifications = new ProgressListenerNotifications();
	private LoadNewReleasesServiceBinder binder = new LoadNewReleasesServiceBinder();
	/**
	 * We're only going to allow one execution at a time.
	 */
	private Thread workerThread = null;

	@Override
	public int onStartCommandWakeful(Intent intent, int flags, int startId) {
		Log.d(Constants.LOG,
				"Flags = "
						+ flags
						+ "; startId = "
						+ startId
						+ ". Intent = "
						+ intent
						+ (intent != null ? (", extra "
								+ EXTRA_REFRESH_ON_START + " = " + intent
								.getBooleanExtra(EXTRA_REFRESH_ON_START, false))
								: ""));
		boolean refreshing = true;

		if (intent == null) {
			// When START_STICKY the intent will be null on "restart" after
			// getting killed
			// TODO RESUME download instead of starting new?
			Log.d(Constants.LOG,
					"Services restarted after being destroyed while workerThread was running.");
			refreshReleases(false, null);
		} else if (intent.getBooleanExtra(EXTRA_REFRESH_ON_START, false)) {
			refreshReleases(false, null);
		} else {
			refreshing = false;
		}

		/*
		 * Don't release wake lock after this method ends, as the logic runs in
		 * a separate thread.
		 * 
		 * The lock is release onDestroy().
		 */
		if (refreshing) {
			keepLock = true;
		}

		return Service.START_STICKY;
	}

	/**
	 * Tries to start refreshing releases. If refresh is already in progress,
	 * attaches <code>artistProcessedListener</code> to it and returns
	 * <code>false</code>.
	 * 
	 * @param updateOnlyIfNeccesary
	 *            if <code>true</code> the refresh is only done when
	 *            {@link ReleaseRefreshService#isUpdateNeccesarry()} returns
	 *            <code>true</code>. Otherwise, the refresh is done at any case.
	 * @param artistProcessedListener
	 * @return <code>true</code> if refresh was started. <code>false</code> if
	 *         already in progress.
	 */
	public boolean refreshReleases(boolean updateOnlyIfNeccesary,
			ArtistProgressListener artistProcessedListener) {
		if (tryCreateThread(updateOnlyIfNeccesary, artistProcessedListener)) {
			return true;
		} else {
			Log.d(Constants.LOG,
					"Service thread already working, only adding process listener");
			getReleasesService().addArtistProcessedListener(
					artistProcessedListener);
			return false;
		}
	}

	/**
	 * Synchronizes the creation and starting of new {@link #workerThread}s.
	 * 
	 * @param updateOnlyIfNeccesary
	 * @param artistProcessedListener
	 * @return <code>true</code> if thread was started, <code>false</code>
	 *         otherwise.
	 */
	private synchronized boolean tryCreateThread(boolean updateOnlyIfNeccesary,
			ArtistProgressListener artistProcessedListener) {
		// if (workerThread == null || !workerThread.isAlive()) {
		if (workerThread == null) {
			Log.d(Constants.LOG, "Service thread not working yet, starting.");
			errorArtists = new LinkedList<Artist>();
			// totalArtists = 0;
			workerThread = new Thread(new WorkerThread(updateOnlyIfNeccesary,
					artistProcessedListener));
			workerThread.start();
			return true;
		}
		return false;
	}

	public class WorkerThread implements Runnable {
		private boolean updateOnlyIfNeccesary;
		private ArtistProgressListener artistProgressListener;

		public WorkerThread(boolean updateOnlyIfNeccesary,
				ArtistProgressListener artistProgressListener) {
			this.updateOnlyIfNeccesary = updateOnlyIfNeccesary;
			this.artistProgressListener = artistProgressListener;
		}

		public void run() {
			Log.d(Constants.LOG, "Service thread starting work");
			if (!connectivityService.isOnline()) {
				Log.d(Constants.LOG, "Service thread: Not online!");
				// If not online and update necessary, postpone run
				if (!updateOnlyIfNeccesary
						|| getReleasesService().isUpdateNeccesarry()) {
					Log.d(Constants.LOG,
							"Postponing service until online or next schedule");
					LoadNewReleasesServiceConnectivityReceiver
							.enableReceiver(LoadNewReleasesService.this);

					// Send status "not online" back to listener?
					if (artistProgressListener != null) {
						artistProgressListener.onProgressFailed(null, 0, 0,
								null, new ServiceException(R.string.NotOnline));
					}
				} else {
					// Make sure any changes to the online state are ignored
					LoadNewReleasesServiceConnectivityReceiver
							.disableReceiver(LoadNewReleasesService.this);
				}

			} else {
				// Make sure any changes to the online state are ignored
				LoadNewReleasesServiceConnectivityReceiver
						.disableReceiver(LoadNewReleasesService.this);

				getReleasesService().addArtistProcessedListener(
						artistProgressListener);
				getReleasesService().addArtistProcessedListener(
						progressListenerNotifications);

				long beforeRefresh = System.currentTimeMillis();
				Log.d(Constants.LOG,
						"Service thread: Calling refreshReleases()");
				if (getReleasesService().refreshReleases(updateOnlyIfNeccesary)) {
					// Schedule next run
					schedule(LoadNewReleasesService.this,
							preferencesService.getRefreshPeriod(), null);
					try {
						notifyNewReleases(beforeRefresh);
					} catch (ServiceException e) {
						// Refresh succeeded, so don't tell user
						Log.w(Constants.LOG,
								"Refresh succeeded, but databse error when trying to find out about new releases",
								e);
					}
				}

				// Remove all listeners
				getReleasesService().removeArtistProcessedListeners();
			}

			// stop service
			Log.d(Constants.LOG, "Service: Explicit stop self");
			stopSelf();
		}
	}

	/**
	 * Finds which releases are new to the device and notifies user if enabled
	 * in preferences.
	 * 
	 * @param beforeRefresh
	 * @throws DatabaseException
	 */
	private void notifyNewReleases(long beforeRefresh) throws ServiceException {
		if (preferencesService.isEnabledNotifyNewReleases()) {
			List<Release> newReleases = releaseService
					.findByDateCreatedGreaterThan(beforeRefresh);
			if (newReleases.size() > 0) {
				notifyNewReleases(getResources().getQuantityString(
						R.plurals.LoadNewReleasesService_foundNewReleases,
						newReleases.size(), newReleases.size()));
			}
		}
	}

	/**
	 * Puts out a notification containing an info symbol. Overwrites any
	 * previous instances of this notification..<br/>
	 * <br/>
	 * Future calls overwrite any previous instances of this notification still
	 * on display.
	 * 
	 * @param text
	 */
	private void notifyNewReleases(String text, Object... args) {
		NusicApplication.notify(Notification.NEW_RELEASE,
				String.format(text, args), null,
				android.R.drawable.ic_dialog_info, null, MainActivity.class,
				createExtraActiveTab());
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
				MainActivity.TabDefinition.JUST_ADDED);
		return extras;
	}

	/**
	 * /** Schedule this task to run regularly.
	 * 
	 * @param context
	 * @param intervalDays
	 * @param triggerAt
	 *            if <code>null</code>, the first start will be now +
	 *            <code>intervalDays</code>
	 */
	public static void schedule(Context context, int intervalDays,
			Date triggerAt) {
		Date triggerAtDate = triggerAt;
		if (triggerAt == null) {
			Calendar triggerAtCal = Calendar.getInstance();
			triggerAtCal.add(Calendar.DAY_OF_MONTH, intervalDays);
			// triggerAtCal.add(Calendar.SECOND, 60);
			triggerAtDate = triggerAtCal.getTime();
		}

		// debug: Starts service once per minute
		// triggerAtDate = DateUtils.addMinutes(60);

		// // Start service directly, on alarm
		// PendingIntent pintent = PendingIntent.getService(context, 0,
		// createIntentRefreshReleases(context),
		// PendingIntent.FLAG_UPDATE_CURRENT);

		/*
		 * Start service directly via receiver that acquires a wake lock, in
		 * order to avoid the device falling back to sleep before service is
		 * started
		 */
		PendingIntent pintent = PendingIntent.getBroadcast(context,
				Constants.Alarms.NEW_RELEASES.ordinal(), new Intent(context,
						LoadNewReleasesServiceAlarmReceiver.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		/*
		 * Set a repeating schedule, so there always is a next alarm even when
		 * one alarm should fail for some reason
		 */
		alarm.setInexactRepeating(AlarmManager.RTC, triggerAtDate.getTime(),
				AlarmManager.INTERVAL_DAY * intervalDays, pintent);
		preferencesService.setNextReleaseRefresh(triggerAtDate);
		Log.d(Constants.LOG, "Scheduled task to run again every "
				+ intervalDays + " days, starting at " + triggerAtDate);
	}

	/**
	 * Creates an intent that, when started as service, directly calls
	 * {@link #refreshReleases(boolean, ArtistProgressListener)}.
	 * 
	 * @return
	 */
	public static Intent createIntentRefreshReleases(Context context) {
		Intent intent = new Intent(context, LoadNewReleasesService.class);
		intent.putExtra(EXTRA_REFRESH_ON_START, true);
		return intent;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		Log.d(Constants.LOG, "Nusic service: onDestroy()");

		if (workerThread != null && workerThread.isAlive()) {
			Log.d(Constants.LOG,
					"Services destroyed while workerThread is running.");
		}
		workerThread = null;
		if (releasesService != null) {
			releasesService
					.removeArtistProcessedListener(progressListenerNotifications);
		}
		releaseLock(this.getApplicationContext());
	}

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LoadNewReleasesServiceBinder extends Binder {
		public LoadNewReleasesService getService() {
			return LoadNewReleasesService.this;
		}
	}

	/**
	 * Broadcast receiver that triggers execution of
	 * {@link LoadNewReleasesService} after a scheduled alarm.
	 * 
	 * @author schnatterer
	 * 
	 */
	public static class LoadNewReleasesServiceAlarmReceiver extends
			BroadcastReceiver {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			Log.d(Constants.LOG, "Alarm Receiver: Alarm received!");
			// Acquire lock, making sure device is not going to sleep again
			acquireLock(context);
			context.startService(LoadNewReleasesService
					.createIntentRefreshReleases(context));
		}
	}

	/**
	 * Progress listeners that displays any crucial info as android
	 * notification.
	 * 
	 * @author schnatterer
	 * 
	 */
	private class ProgressListenerNotifications implements
			ArtistProgressListener {

		@Override
		public void onProgressStarted(int nEntities) {
			errorArtists = new LinkedList<Artist>();
			// totalArtists = nEntities;
		}

		@Override
		public void onProgress(Artist entity, int progress, int max,
				Throwable potentialException) {
			if (potentialException != null) {
				if (potentialException instanceof ServiceException) {
					errorArtists.add(entity);
				}
			}
		}

		@Override
		public void onProgressFinished(Boolean result) {
			// Don't bother the user about the failure. Better luck next
			// time!
			// if (errorArtists != null && errorArtists.size() > 0) {
			// Application.notifyWarning(
			// R.string.LoadNewReleasesBinding_finishedWithErrors,
			// errorArtists.size(), totalArtists);
			// }
			// On success, keep quiet
			// } else if (totalArtists > 0) {
			// Application.notifyNewReleases("!!SUCESSFULLY FINISHED REFRESHING "
			// + totalArtists + " ARTISTS!!");
			// }
		}

		@Override
		public void onProgressFailed(Artist entity, int progress, int max,
				Boolean result, Throwable potentialException) {
			if (potentialException != null) {
				Log.e(Constants.LOG, potentialException.getMessage(),
						potentialException);
				if (potentialException instanceof ServiceException) {
					NusicApplication
							.notifyWarning(NusicApplication
									.getContext()
									.getString(
											R.string.LoadNewReleasesBinding_errorFindingReleases)
									+ potentialException.getLocalizedMessage());
				} else {
					NusicApplication
							.notifyWarning(NusicApplication
									.getContext()
									.getString(
											R.string.LoadNewReleasesBinding_errorFindingReleasesGeneric)
									+ potentialException.getClass()
											.getSimpleName());

				}
			}
		}
	}

	protected ReleaseRefreshService getReleasesService() {
		if (releasesService == null) {
			releasesService = new ReleaseRefreshServiceImpl(this);
		}

		return releasesService;
	}

	public boolean isRunning() {
		return workerThread != null && workerThread.isAlive();
	}
}