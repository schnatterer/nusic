package info.schnatterer.newsic.ui.tasks;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.service.ServiceException;
import info.schnatterer.newsic.service.android.LoadNewReleasesServiceConnection;
import info.schnatterer.newsic.service.event.ArtistProgressListener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * {@link AsyncTask} that loads new releases, listens to the progress (via
 * {@link ArtistProgressListener} and visualizes the progress in a
 * {@link ProgressDialog}.
 * 
 * @author schnatterer
 * 
 */
public class LoadNewRelasesTask extends AsyncTask<Void, Object, Void> {
	private Context context;

	private ProgressDialog progressDialog = null;
	private List<Artist> errorArtists;

	private Set<FinishedLoadingListener> listeners = new HashSet<FinishedLoadingListener>();

	private LoadNewReleasesServiceConnection loadNewReleasesServiceConnection = null;
	private ProgressListener artistProcessedListener = null;
	// private ReleasesService releasesService = null;

	private boolean updateOnlyIfNeccesary;

	/**
	 * @param activity
	 * @param updateNow
	 *            directly start updating releases without checking necessity.
	 */
	public LoadNewRelasesTask(Activity activity, boolean updateOnlyIfNeccesary) {
		this.context = activity;
		this.updateOnlyIfNeccesary = updateOnlyIfNeccesary;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		startAndBindService();
		return null;
	}

	private void startAndBindService() {
		// if (loadNewReleasesServiceConnection != null) {
		// unbindService();
		// }
		artistProcessedListener = new ProgressListener();
		loadNewReleasesServiceConnection = LoadNewReleasesServiceConnection
				.startAndBind(context, artistProcessedListener,
						updateOnlyIfNeccesary);
	}

	private void unbindService() {
		if (loadNewReleasesServiceConnection != null) {
			loadNewReleasesServiceConnection.unbind();
			loadNewReleasesServiceConnection = null;
			artistProcessedListener = null;
		}
	}

	@Override
	protected void onProgressUpdate(Object... objects) {
		// Update GUI (ProgressDialog) in MainThread
		if (objects.length < 2 || objects[0] == null) {
			Log.w(Constants.LOG,
					"Can't update progressDialog. Missing arguments");
			return;
		}

		try {
			ProgressUpdateOperation operation = (ProgressUpdateOperation) objects[0];
			switch (operation) {
			case PROGRESS_STARTED:
				progressDialog = showDialog(0, (Integer) objects[1]);
				errorArtists = new LinkedList<Artist>();
				break;
			case PROGRESS: {
				ProgressUpdate progress = (ProgressUpdate) objects[1];
				if (progress.getArtist() != null) {
					setProgress(progress);
				}

			}
				break;
			case PROGRESS_FINISHED:
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				if (errorArtists != null && errorArtists.size() > 0) {
					Application.toast(
							R.string.LoadNewReleasesTask_finishedWithErrors,
							errorArtists.size());
				}
				break;
			case PROGRESS_FAILED: {
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				ProgressUpdate progress = (ProgressUpdate) objects[1];
				Throwable potentialException = progress.getPotentialException();
				if (potentialException != null) {
					if (potentialException instanceof ServiceException) {
						Application.toast(potentialException
								.getLocalizedMessage());
					} else {
						Application
								.toast(Application
										.getContext()
										.getString(
												R.string.LoadNewReleasesTask_errorFindingReleases)
										+ potentialException.getClass()
												.getSimpleName());
					}
				}
			}
				break;
			default:
				Log.w(Constants.LOG,
						"Unexpected/Unimpletented progress operation \""
								+ operation + "\"");
				break;
			}
		} catch (ClassCastException e) {
			Log.w(Constants.LOG,
					"Can't update progressDialog. Unexpected type/order of arguments",
					e);
			return;
		}
	}

	private void setProgress(ProgressUpdate progress) {
		if (progressDialog == null) {
			// Try to show the dialog is shown
			progressDialog = showDialog(progress.getProgress(),
					progress.getMax());
		}
		if (progressDialog != null) {
			progressDialog.setProgress(progress.getProgress());
		}
	}

	// @Override
	// protected void onPostExecute(Void result) {
	// // Make sure we're unbound
	// unbindService();
	// }

	public void updateActivity(Activity activity) {
		this.context = activity;

		if (activity == null && progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		/*
		 * If progressDialog is displayed, show a new one with same settings,
		 * belonging to the new activty
		 */
		else if (progressDialog != null) {
			progressDialog = showDialog(progressDialog.getProgress(),
					progressDialog.getMax());
		}
	}

	// public List<Release> getResult() {
	// return result;
	// }

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
					R.string.LoadNewReleasesTask_CheckingArtists));
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setCancelable(false);
			dialog.setMax(max);
			dialog.setProgress(progress);
			dialog.show();
		}
		return dialog;
	}

	private class ProgressListener implements ArtistProgressListener {
		@Override
		public void onProgressStarted(int nEntities) {
			publishProgress(ProgressUpdateOperation.PROGRESS_STARTED, nEntities);
		}

		@Override
		public void onProgress(Artist entity, int progress, int max,
				Throwable potentialException) {
			publishProgress(ProgressUpdateOperation.PROGRESS,
					new ProgressUpdate(entity, progress, max,
							potentialException));
		}

		@Override
		public void onProgressFinished(Boolean result) {
			publishProgress(ProgressUpdateOperation.PROGRESS_FINISHED, result);
			notifyListeners(result);
			unbindService();
		}

		@Override
		public void onProgressFailed(Artist entity, int progress, int max,
				Boolean result, Throwable potentialException) {
			publishProgress(ProgressUpdateOperation.PROGRESS_FAILED,
					new ProgressUpdate(entity, progress, max,
							potentialException));
			notifyListeners(result);
			unbindService();
		}
	}

	private enum ProgressUpdateOperation {
		PROGRESS_STARTED, PROGRESS, PROGRESS_FINISHED, PROGRESS_FAILED
	};

	private class ProgressUpdate {

		public ProgressUpdate(Artist entity, int progress, int max,
				Throwable potentialException) {
			this.artist = entity;
			this.progress = progress;
			this.max = max;
			this.potentialException = potentialException;
		}

		private Artist artist;
		private int progress;
		private int max;
		private Throwable potentialException;

		public Artist getArtist() {
			return artist;
		}

		public int getProgress() {
			return progress;
		}

		public int getMax() {
			return max;
		}

		public Throwable getPotentialException() {
			return potentialException;
		}
	}

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
