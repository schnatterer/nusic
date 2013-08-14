package info.schnatterer.newsic.ui.tasks;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.service.ReleasesService;
import info.schnatterer.newsic.service.ServiceException;
import info.schnatterer.newsic.service.android.LoadNewReleasesService;
import info.schnatterer.newsic.service.android.LoadNewReleasesServiceConnection;
import info.schnatterer.newsic.service.event.ArtistProgressListener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * Holds the binding to the {@link LoadNewReleasesService} via
 * {@link LoadNewReleasesServiceConnection}. Allows for executing the service
 * method {@link ReleasesService#refreshReleases(boolean)} and visualizes its
 * result in a {@link ProgressDialog}.
 * 
 * @author schnatterer
 * 
 */
public class LoadNewRelasesServiceBinding {
	/**
	 * Singleton instance.
	 */
	private static LoadNewRelasesServiceBinding instance = null;
	/**
	 * Context in which the {@link #progressDialog} is displayed
	 */
	private Activity context;

	private ProgressDialog progressDialog = null;
	private List<Artist> errorArtists;

	private Set<FinishedLoadingListener> listeners = new HashSet<FinishedLoadingListener>();

	private LoadNewReleasesServiceConnection loadNewReleasesServiceConnection = null;
	private ProgressListener artistProcessedListener = new ProgressListener();

	public LoadNewRelasesServiceBinding() {
		// // Bind in application's global context, don't run the service yet
		// loadNewReleasesServiceConnection = startAndBindService(
		// Application.getContext(), null);
	}

	/**
	 * Returns the singleton instance. Keep in mind that
	 * {@link #unbindService()} must be called on pause/stop/destroy.
	 * 
	 * @return
	 */
	public static LoadNewRelasesServiceBinding getInstance() {
		if (instance == null) {
			instance = new LoadNewRelasesServiceBinding();
		}
		return instance;
	}

	/**
	 * Executes {@link ReleasesService#refreshReleases(boolean)} within
	 * {@link LoadNewReleasesService} in a spearate thread.
	 * 
	 * @param activity
	 *            activity that is used to display the {@link ProgressDialog}
	 * @param updateOnlyIfNeccesary
	 * @return <code>true</code> if refresh was started. <code>false</code> if
	 *         already in progress.
	 */
	public boolean refreshReleases(Activity activity,
			boolean updateOnlyIfNeccesary) {
		if (loadNewReleasesServiceConnection != null
				&& loadNewReleasesServiceConnection.isBound()) {
			boolean isStarted = loadNewReleasesServiceConnection
					.getLoadNewReleasesService().refreshReleases(
							updateOnlyIfNeccesary, artistProcessedListener);
			if (isStarted) {
				this.context = activity;
			}
			return isStarted;
		} else {
			// Log.w(Constants.LOG,
			// "Service not bound, triggering binding and start asynchronously");
			loadNewReleasesServiceConnection = startAndBindService(activity, updateOnlyIfNeccesary);
			this.context = activity;
			return true;
		}
	}

	public boolean isRunning() {
		if (loadNewReleasesServiceConnection.isBound()) {
			return loadNewReleasesServiceConnection.getLoadNewReleasesService()
					.isRunning();
		} else {
			Log.w(Constants.LOG,
					"Service unexpectedly not bound, assuming it's not running");
			return false;
		}
	}

	/**
	 * Start {@link LoadNewReleasesService}, then binds to it. In doing so, the
	 * service can hopefully linger on after the unbinding (if it still is
	 * running).
	 * 
	 * @param packageContext
	 *            some context start the service from
	 * @param updateOnlyIfNeccesary
	 * @return
	 */
	private LoadNewReleasesServiceConnection startAndBindService(
			Context packageContext, Boolean updateOnlyIfNeccesary) {
		boolean startRightAway = false;
		boolean updateOnlyIfNeccesaryPrimitive = true;
		if (updateOnlyIfNeccesary != null) {
			startRightAway = true;
			updateOnlyIfNeccesaryPrimitive = updateOnlyIfNeccesary;
		}

		return LoadNewReleasesServiceConnection.startAndBind(packageContext,
				startRightAway, artistProcessedListener,
				updateOnlyIfNeccesaryPrimitive);
	}

	/**
	 * This should be called whenever when the application is
	 * paused/destroyed/stopped by the system. Don't forget to call
	 * {@link #bindService()}.
	 */
	public void unbindService() {
		if (loadNewReleasesServiceConnection != null) {
			loadNewReleasesServiceConnection.unbind();
			loadNewReleasesServiceConnection = null;
			// artistProcessedListener = null;
		}
	}

	// /**
	// * This should be called whenever when the application is resumed/started
	// by
	// * the system. Don't forget to call {@link #unbindService()} if you don't
	// * want to leak a service.
	// */
	// public void bindService() {
	// if (loadNewReleasesServiceConnection == null) {
	// // Bind in application's global context, don't run the service yet
	// loadNewReleasesServiceConnection = startAndBindService(
	// Application.getContext(), null);
	// }
	// }

	/**
	 * Sets a new {@link Context} for the {@link ProgressDialog}.
	 * 
	 * @param newActivity
	 *            can be <code>null</code>, which results in hiding the dialog
	 */
	public void updateContext(Activity newActivity) {
		this.context = newActivity;

		if (newActivity == null) {

			hideProgressDialog();
		}
	}

	/**
	 * Shows the context dialog, if there is any progress going on. Useful if
	 * the dialog might have been hidden.
	 */
	public void showDialog() {
		if (progressDialog != null) {
			progressDialog.show();
		}
	}

	public void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * Set the progress of the {@link ProgressDialog}.
	 * 
	 * @param progress
	 * @param max
	 */
	private void setProgress(int progress, int max) {
		if (progressDialog == null) {
			// Try to show the dialog is shown
			progressDialog = showDialog(progress, max);
		}
		if (progressDialog != null) {
			progressDialog.setProgress(progress);
		}
	}

	/**
	 * This should only be called from from the main thread (e.g. from
	 * {@link #onProgressUpdate(Object...)}).
	 * 
	 * @param progress
	 * @param max
	 * @return
	 */
	private ProgressDialog showDialog(int progress, int max) {
		ProgressDialog dialog = null;
		if (context != null) {
			dialog = new ProgressDialog(context);
			dialog.setMessage(Application.getContext().getString(
					R.string.LoadNewReleasesBinding_CheckingArtists));
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// dialog.setCancelable(false);
			dialog.setMax(max);
			dialog.setProgress(progress);
			dialog.show();
		}
		return dialog;
	}

	/**
	 * Handles updating the {@link ProgressDialog}.
	 * 
	 * @author schnatterer
	 * 
	 */
	private class ProgressListener implements ArtistProgressListener {
		@Override
		public void onProgressStarted(final int nEntities) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog = showDialog(0, nEntities);
				}
			});
			errorArtists = new LinkedList<Artist>();
		}

		@Override
		public void onProgress(final Artist entity, final int progress,
				final int max, Throwable potentialException) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (entity != null) {
						setProgress(progress, max);
					}
				}
			});
			if (potentialException != null) {
				errorArtists.add(entity);
			}
		}

		@Override
		public void onProgressFinished(Boolean result) {
			notifyListeners(result);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					hideProgressDialog();
					if (errorArtists != null && errorArtists.size() > 0) {
						Application
								.toast(R.string.LoadNewReleasesBinding_finishedWithErrors,
										errorArtists.size());
					}
				}
			});
			// unbindService();
		}

		@Override
		public void onProgressFailed(Artist entity, int progress, int max,
				Boolean result, final Throwable potentialException) {
			notifyListeners(result);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					hideProgressDialog();
					if (potentialException != null) {
						if (potentialException instanceof ServiceException) {
							Application.toast(potentialException
									.getLocalizedMessage());
						} else {
							Application
									.toast(Application
											.getContext()
											.getString(
													R.string.LoadNewReleasesBinding_errorFindingReleases)
											+ potentialException.getClass()
													.getSimpleName());
						}
					}
				}
			});

			// unbindService();
		}

		private void runOnUiThread(Runnable runnable) {
			if (context != null) {
				context.runOnUiThread(runnable);
			}

		}
	}

	/**
	 * Notifies others that the service finished and that there might be new
	 * data.
	 * 
	 * @author schnatterer
	 * 
	 */
	public interface FinishedLoadingListener {
		void onFinishedLoading(boolean resultChanged);
	}

	public void addFinishedLoadingListener(
			FinishedLoadingListener dataChangedListener) {
		listeners.add(dataChangedListener);

	}

	public boolean removeFinishedLoadingListener(
			FinishedLoadingListener dataChangedListener) {
		return listeners.remove(dataChangedListener);
	}

	protected void notifyListeners(Boolean resultChanged) {
		boolean primitiveResult = true;
		// Be defensive: Only if explicitly nothing changed
		if (resultChanged != null && resultChanged.equals(false)) {
			primitiveResult = false;
		}
		for (FinishedLoadingListener listener : listeners) {
			listener.onFinishedLoading(primitiveResult);
		}
	}
}
