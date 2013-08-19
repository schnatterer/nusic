package info.schnatterer.newsic.service.android;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.service.PreferencesService;
import info.schnatterer.newsic.service.ReleasesService;
import info.schnatterer.newsic.service.ServiceException;
import info.schnatterer.newsic.service.event.ArtistProgressListener;
import info.schnatterer.newsic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.newsic.service.impl.ReleasesServiceImpl;

import java.util.Calendar;
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
public class LoadNewReleasesService extends Service {
	// public static final String ARG_UPDATE_ONLY_IF_NECESSARY =
	// "updateOnlyIfNeccesary";
	public static final String ARG_REFRESH_ON_START = "refreshOnStart";

	// public LoadNewReleasesService() {
	// super(LoadNewReleasesService.class.getSimpleName() + "WorkerThread");
	// }
	private PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();
	private ReleasesService releasesService;

	private List<Artist> errorArtists;
	private int totalArtists = 0;
	private ProgressListener progressListener = new ProgressListener();
	private LoadNewReleasesServiceBinder binder = new LoadNewReleasesServiceBinder();
	/**
	 * We're only going to allow one execution at a time.
	 */
	private Thread workerThread = null;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(Constants.LOG, "Flags = " + flags + "; startId = " + startId
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
			// TODO if not online postpone run

			getReleasesService().addArtistProcessedListener(
					artistProgressListener);
			getReleasesService().addArtistProcessedListener(progressListener);
			if (getReleasesService().refreshReleases(updateOnlyIfNeccesary)) {
				// Schedule next run
				schedule(LoadNewReleasesService.this,
						preferencesService.getRefreshPeriod());
				// TODO find which releases are new to the device and notify
				// user
			}

			// TODO remove all
			getReleasesService().removeArtistProcessedListener(
					artistProgressListener);

			// stop service!!
			stopSelf();
		}
	}

	/**
	 * Schedule this task to run again in some days.
	 * 
	 * @param context
	 * @param daysFromNow
	 */
	public void schedule(Context context, int daysFromNow) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, daysFromNow);
		// cal.add(Calendar.SECOND, 60);

		Intent intent = new Intent(this, LoadNewReleasesService.class);
		intent.putExtra(ARG_REFRESH_ON_START, true);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY * daysFromNow, pintent);
		Log.i(Constants.LOG, "Scheduled task to run again every " + daysFromNow
				+ " days, starting at " + cal.getTime());
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
			releasesService.removeArtistProcessedListener(progressListener);
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

	private class ProgressListener implements ArtistProgressListener {

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