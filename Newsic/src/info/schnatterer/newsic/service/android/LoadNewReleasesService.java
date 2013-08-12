package info.schnatterer.newsic.service.android;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.service.ReleasesService;
import info.schnatterer.newsic.service.ServiceException;
import info.schnatterer.newsic.service.event.ArtistProgressListener;
import info.schnatterer.newsic.service.impl.ReleasesServiceImpl;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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
	public static final String ARG_UPDATE_ONLY_IF_NECESSARY = "updateOnlyIfNeccesary";

	// public LoadNewReleasesService() {
	// super(LoadNewReleasesService.class.getSimpleName() + "WorkerThread");
	// }

	private ReleasesService releasesService;
	private List<Artist> errorArtists;
	private ProgressListener progressListener = new ProgressListener();
	private LoadNewReleasesServiceBinder binder = new LoadNewReleasesServiceBinder();
	/**
	 * We're only going to allow one execution at a time.
	 */
	private Thread workerThread = null;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(Constants.LOG, "Flags = " + flags + "; startId = " + startId
				+ ". Intent = " + intent);
		if (intent == null) {
			// When START_STICKY the intent will be null on "restart" after
			// getting killed
			// TODO resume download
		}
		return Service.START_STICKY;

//		if (flags == START_FLAG_REDELIVERY) {
//			// When START_REDELIVER_INTENT this flag will be set and intent will
//			// be the same as the original one
//		}
//		return Service.START_REDELIVER_INTENT;
	}

	// @Override
	// protected void onHandleIntent(Intent intent) {
	// boolean updateOnlyIfNecessary = false;
	// if (intent.getExtras() != null) {
	// updateOnlyIfNecessary = intent.getExtras().getBoolean(
	// ARG_UPDATE_ONLY_IF_NECESSARY);
	// }
	// refreshReleases(updateOnlyIfNecessary);
	// }

	public boolean refreshReleases(boolean updateOnlyIfNeccesary,
			ArtistProgressListener artistProcessedListener) {
		if (workerThread == null || !workerThread.isAlive()) {
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

	// private synchronized boolean setWorkerThread(WorkerThread
	// newWorkerThread) {
	// if (workerThread != null newWorkerThread != null) {
	// return false;
	// } else {
	// workerThread = newWorkerThread;
	// return true;
	// }
	// }

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
			
			// TODO schedule next run

			getReleasesService().addArtistProcessedListener(
					artistProgressListener);
			getReleasesService().refreshReleases(updateOnlyIfNeccesary);
			// TODO find which releases are new to the device and notify user

			// TODO remove all
			getReleasesService().removeArtistProcessedListener(
					artistProgressListener);

			// TODO check if scheduled next run is not in the past
			// stop service!!
			stopSelf();

		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
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
		}

		@Override
		public void onProgress(Artist entity, int progress, int max,
				Throwable potentialException) {
			if (potentialException != null) {
				if (potentialException instanceof ServiceException) {
					errorArtists.add(entity);
				} else {
					Application.toast(Application
							.createGenericErrorMessage(potentialException));
				}
			}
		}

		@Override
		public void onProgressFinished(Boolean result) {
			if (errorArtists != null && errorArtists.size() > 0) {
				// TODO write status bar message
				/*
				 * R.string.LoadNewReleasesTask_finishedWithErrors,
				 * errorArtists.size();
				 */
			}
		}

		@Override
		public void onProgressFailed(Artist entity, int progress, int max,
				Boolean result, Throwable potentialException) {
			if (potentialException != null) {
				if (potentialException instanceof ServiceException) {
					// TODO write status bar message
					// potentialException.getLocalizedMessage());
				} else {
					// TODO write status bar message
					/*
					 * Application.getContext().getString(
					 * R.string.LoadNewReleasesTask_errorFindingReleases) +
					 * potentialException.getClass().getSimpleName();
					 */
				}
			}
		}
	}

	protected ReleasesService getReleasesService() {
		if (releasesService == null) {
			releasesService = new ReleasesServiceImpl(this);
			getReleasesService().addArtistProcessedListener(progressListener);
		}

		return releasesService;
	}
}