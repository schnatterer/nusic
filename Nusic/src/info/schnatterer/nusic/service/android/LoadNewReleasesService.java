package info.schnatterer.nusic.service.android;

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.dao.impl.ReleaseDaoSqlite;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.service.ConnectivityService;
import info.schnatterer.nusic.service.PreferencesService;
import info.schnatterer.nusic.service.ReleasesService;
import info.schnatterer.nusic.service.ServiceException;
import info.schnatterer.nusic.service.event.ArtistProgressListener;
import info.schnatterer.nusic.service.impl.ConnectivityServiceAndroid;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.service.impl.ReleasesServiceImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Wraps the android implementation of the business logic service
 * {@link ReleasesService} in an android {@link Service} in order to allow
 * running outside of the application. In addition takes care of the scheduling.
 * 
 * @author schnatterer
 * 
 */
public class LoadNewReleasesService extends WakefulService {
	// public static final String ARG_UPDATE_ONLY_IF_NECESSARY =
	// "updateOnlyIfNeccesary";
	public static final String ARG_REFRESH_ON_START = "refreshOnStart";

	// public LoadNewReleasesService() {
	// super(LoadNewReleasesService.class.getSimpleName() + "WorkerThread");
	// }
	private static PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();
	private ConnectivityService connectivityService = ConnectivityServiceAndroid
			.getInstance();
	private ReleasesService releasesService;

	private List<Artist> errorArtists;
	private int totalArtists = 0;
	private ProgressListenerNotifications progressListenerNotifications = new ProgressListenerNotifications();
	private LoadNewReleasesServiceBinder binder = new LoadNewReleasesServiceBinder();
	/**
	 * We're only going to allow one execution at a time.
	 */
	private Thread workerThread = null;

	@Override
	public int onStartCommandWakeful(Intent intent, int flags, int startId) {
		Log.d(Constants.LOG, "Flags = " + flags + "; startId = " + startId
				+ ". Intent = " + intent
				+ (intent != null ? (", extras= " + intent.getExtras()) : ""));

		if (intent == null) {
			// When START_STICKY the intent will be null on "restart" after
			// getting killed
			// RESUME download instead of starting new?
			Log.d(Constants.LOG,
					"Services restarted after being destroyed while workerThread was running.");
			refreshReleases(false, null);
		} else {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				// Get data via the key
				if (extras.getBoolean(ARG_REFRESH_ON_START, false)) {
					refreshReleases(false, null);
				}
			}
		}

		return Service.START_STICKY;

		// if (flags == START_FLAG_REDELIVERY) {
		// // When START_REDELIVER_INTENT this flag will be set and intent will
		// // be the same as the original one
		// }
		// return Service.START_REDELIVER_INTENT;
	}

	/**
	 * Tries to start refreshing releases. If refresh is already in progress,
	 * attaches <code>artistProcessedListener</code> to it and returns
	 * <code>false</code>.
	 * 
	 * @param updateOnlyIfNeccesary
	 * @param artistProcessedListener
	 * @return <code>true</code> if refresh was started. <code>false</code> if
	 *         already in progress.
	 */
	public boolean refreshReleases(boolean updateOnlyIfNeccesary,
			ArtistProgressListener artistProcessedListener) {
		if (workerThread == null || !workerThread.isAlive()) {
			errorArtists = new LinkedList<Artist>();
			totalArtists = 0;
			workerThread = new Thread(new WorkerThread(updateOnlyIfNeccesary,
					artistProcessedListener));
			workerThread.start();
			return true;
		} else {
			getReleasesService().addArtistProcessedListener(
					artistProcessedListener);
			return false;
		}
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
			// TODO remove me!
			Application.notifyInfo("Trying to refresh releases");

			if (!connectivityService.isOnline()) {
				// If not online and update necessary, postpone run
				if (!updateOnlyIfNeccesary
						|| getReleasesService().isUpdateNeccesarry()) {
					Log.d(Constants.LOG,
							"Postponing service until online or next schedule");
					// TODO remove me!
					Application
							.notifyWarning("Postponing service until online or next schedule");
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

				Date beforeRefresh = new Date();
				if (getReleasesService().refreshReleases(updateOnlyIfNeccesary)) {
					// Schedule next run
					schedule(LoadNewReleasesService.this,
							preferencesService.getRefreshPeriod(), null);
					try {
						notifyNewReleases(beforeRefresh);
					} catch (DatabaseException e) {
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
			stopSelf();
		}
	}

	/**
	 * Finds which releases are new to the device and notifies user.
	 * 
	 * @param beforeRefresh
	 * @throws DatabaseException
	 */
	private void notifyNewReleases(Date beforeRefresh) throws DatabaseException {
		List<Release> newReleases = new ReleaseDaoSqlite(this)
				.findJustCreated(beforeRefresh);
		if (newReleases.size() > 0) {
			Application.notifyInfo(
					R.string.LoadNewReleasesService_foundNewReleases,
					newReleases.size());
		}
	}

	/**
	 * Schedule this task to run regularly.
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
			// cal.add(Calendar.SECOND, 60);
			triggerAtDate = triggerAtCal.getTime();
		}

		PendingIntent pintent = PendingIntent.getService(context, 0,
				createIntentRefreshReleases(context),
				PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		/*
		 * Set a repeating schedule, so there always is a next alarm even when
		 * one alarm should fail for some reason
		 */
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtDate.getTime(),
				AlarmManager.INTERVAL_DAY * intervalDays, pintent);
		preferencesService.setNextReleaseRefresh(triggerAtDate);
		Log.i(Constants.LOG, "Scheduled task to run again every "
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
		intent.putExtra(ARG_REFRESH_ON_START, true);
		return intent;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		if (workerThread != null && workerThread.isAlive()) {
			Log.d(Constants.LOG,
					"Services destroyed while workerThread is running.");
		}
		if (releasesService != null) {
			releasesService
					.removeArtistProcessedListener(progressListenerNotifications);
		}
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
			totalArtists = nEntities;
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
			if (errorArtists != null && errorArtists.size() > 0) {
				Application.notifyWarning(
						R.string.LoadNewReleasesBinding_finishedWithErrors,
						errorArtists.size(), totalArtists);
			}
			// On success, keep quiet
			// } else if (totalArtists > 0) {
			// Application.notifyInfo("!!SUCESSFULLY FINISHED REFRESHING "
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
					Application
							.notifyWarning(Application
									.getContext()
									.getString(
											R.string.LoadNewReleasesBinding_errorFindingReleases)
									+ potentialException.getLocalizedMessage());
				} else {
					Application
							.notifyWarning(Application
									.getContext()
									.getString(
											R.string.LoadNewReleasesBinding_errorFindingReleasesGeneric)
									+ potentialException.getClass()
											.getSimpleName());

				}
			}
		}
	}

	protected ReleasesService getReleasesService() {
		if (releasesService == null) {
			releasesService = new ReleasesServiceImpl(this);
		}

		return releasesService;
	}

	public boolean isRunning() {
		return workerThread != null && workerThread.isAlive();
	}
}