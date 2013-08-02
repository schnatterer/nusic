package info.schnatterer.newsic.ui.tasks;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.db.model.Release;
import info.schnatterer.newsic.service.PreferencesService;
import info.schnatterer.newsic.service.ReleasesService;
import info.schnatterer.newsic.service.ServiceException;
import info.schnatterer.newsic.service.event.ArtistProgressListener;
import info.schnatterer.newsic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.newsic.service.impl.ReleasesServiceImpl;
import info.schnatterer.newsic.ui.adapters.ReleaseListAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
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
public class LoadNewRelasesTask extends AsyncTask<Void, Object, List<Release>>
		implements ArtistProgressListener {
	private ReleasesService releasesService;
	private PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();

	private ProgressDialog progressDialog;
	private boolean isExecuting = false;
	// private Boolean isSuccess = null;

	private Activity activity;
	private List<Artist> errorArtists;

	private Set<FinishedLoadingListener> listeners = new HashSet<FinishedLoadingListener>();

	public LoadNewRelasesTask(Activity activity) {
		this.activity = activity;
		// Run in global context
		releasesService = new ReleasesServiceImpl(Application.getContext());
	}

	@Override
	protected void onPreExecute() {
		isExecuting = true;
		// isSuccess = null;
		releasesService.addArtistProcessedListener(this);
	}

	@Override
	protected List<Release> doInBackground(Void... arg0) {
		// Do it

		// TODO extract this to a service and write test for logic!
		Date startDate = createStartDate(preferencesService.isFullUpdate(),
				preferencesService.getDownloadReleasesTimePeriod(),
				preferencesService.getLastSuccessfullReleaseRefresh());
		Date endDate = createEndDate(preferencesService
				.isIncludeFutureReleases());
		List<Release> result = releasesService.updateNewestReleases(startDate,
				endDate, false);

		// if (isSuccess != null && isSuccess.equals(true)) {
		// // Success
		// // if (errorArtists.size() > 0)
		// // TODO Notify user, that some artist failed
		//
		// // TODO find which releases are new to the device and notify user
		// preferencesService.setLastSuccessfullReleaseRefresh(new Date());
		// } else {
		// // TODO Notify user of failure
		// }

		return result;
	}

	private Date createEndDate(boolean includeFutureReleases) {
		if (!includeFutureReleases) {
			return new Date(); // Today
		} else {
			return null;
		}
	}

	private Date createStartDate(boolean isFullUpdate, int months,
			Date lastReleaseRefresh) {
		if (lastReleaseRefresh == null) {
			// Same as full Update
			return createStartDateFullUpdate(months);
		}
		if (isFullUpdate) {
			return createStartDateFullUpdate(months);
		}
		return lastReleaseRefresh;
	}

	private Date createStartDateFullUpdate(int months) {
		if (months <= 0) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -months);
		return cal.getTime();
	}

	@SuppressWarnings("unchecked")
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

				Throwable potentialException = progress.getPotentialException();
				if (potentialException != null) {
					if (potentialException instanceof ServiceException) {
						errorArtists.add(progress.getArtist());
					} else {
						Application.toast(Application
								.createGenericErrorMessage(potentialException));
					}
				}
			}
				break;
			case PROGRESS_FINISHED:
				progressDialog.dismiss();
				progressDialog = null;
				List<Release> results = (List<Release>) objects[1];
				if (errorArtists.size() > 0) {
					Application.toast(
							R.string.LoadNewReleasesTask_finishedWithErrors,
							results.size(), errorArtists.size());
				} else {
					// TODO move this to separate service, see
					preferencesService
							.setLastSuccessfullReleaseRefresh(new Date());
				}
				break;
			case PROGRESS_FAILED: {
				progressDialog.dismiss();
				progressDialog = null;
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

	@Override
	protected void onPostExecute(List<Release> result) {
		isExecuting = false;
		releasesService.removeArtistProcessedListener(this);
	}

	public void updateActivity(Activity activity,
			ReleaseListAdapter releasesListViewAdapter) {
		this.activity = activity;

		if (activity == null) {
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

	public boolean isExecuting() {
		return isExecuting;
	}

	public void setExecuting(boolean isExecuting) {
		this.isExecuting = isExecuting;
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
		if (activity != null) {
			dialog = new ProgressDialog(activity);
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

	@Override
	public void onProgressStarted(int nEntities) {
		publishProgress(ProgressUpdateOperation.PROGRESS_STARTED, nEntities);
	}

	@Override
	public void onProgress(Artist entity, int progress, int max,
			Throwable potentialException) {
		publishProgress(ProgressUpdateOperation.PROGRESS, new ProgressUpdate(
				entity, progress, max, potentialException));
	}

	@Override
	public void onProgressFinished(List<Release> result) {
		notifyListeners();
		publishProgress(ProgressUpdateOperation.PROGRESS_FINISHED, result);
	}

	@Override
	public void onProgressFailed(Artist entity, int progress, int max,
			List<Release> resultOnFailure, Throwable potentialException) {
		notifyListeners();
		publishProgress(ProgressUpdateOperation.PROGRESS_FAILED,
				new ProgressUpdate(entity, progress, max, potentialException),
				resultOnFailure);
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
		void onFinishedLoading();
	}

	public void addFinishedLoadingListener(
			FinishedLoadingListener dataChangedListener) {
		listeners.add(dataChangedListener);

	}

	public boolean removeFinishedLoadingListener(
			FinishedLoadingListener dataChangedListener) {
		return listeners.remove(dataChangedListener);
	}

	protected void notifyListeners() {
		for (FinishedLoadingListener listener : listeners) {
			listener.onFinishedLoading();
		}
	}
}
