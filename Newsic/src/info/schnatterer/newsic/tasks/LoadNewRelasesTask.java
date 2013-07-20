package info.schnatterer.newsic.tasks;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.adapters.ReleaseListAdapter;
import info.schnatterer.newsic.model.Artist;
import info.schnatterer.newsic.service.ReleasesService;
import info.schnatterer.newsic.service.ServiceException;
import info.schnatterer.newsic.service.event.ArtistProgressListener;
import info.schnatterer.newsic.service.impl.ReleasesServiceImpl;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

/**
 * {@link AsyncTask} that loads new releases, listens to the progress (via
 * {@link ArtistProgressListener} and visualizes the progress in a
 * {@link ProgressDialog}.
 * 
 * @author schnatterer
 * 
 */
public class LoadNewRelasesTask extends AsyncTask<Void, Object, List<Artist>>
		implements ArtistProgressListener {
	private ReleasesService releasesService;

	private ProgressDialog progressDialog;
	private boolean isExecuting = false;
	private List<Artist> result = null;

	private Activity activity;
	private ListView resultView;
	private List<Artist> errorArtists;

	public LoadNewRelasesTask(Activity activity, ListView resultView) {
		this.activity = activity;
		this.resultView = resultView;
		// Run in global context
		releasesService = new ReleasesServiceImpl(Application.getContext());
	}

	@Override
	protected void onPreExecute() {
		isExecuting = true;
		result = null;
		releasesService.addArtistProcessedListener(this);
	}

	@Override
	protected List<Artist> doInBackground(Void... arg0) {
		// Do it
		return releasesService.getNewestReleases();
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
				if (progressDialog == null) {
					// Make sure the dialog is shown
					progressDialog = showDialog(progress.getProgress(),
							progress.getMax());
				}
				if (progress.getArtist() != null) {
					progressDialog.setProgress(progress.getProgress());
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
				setResult((List<Artist>) objects[1]);
				if (errorArtists.size() > 0) {
					Application.toast(
							R.string.LoadNewReleasesTask_finishedWithErrors,
							getResult().size(), errorArtists.size());
				}
				break;
			case PROGRESS_FAILED: {
				progressDialog.dismiss();
				progressDialog = null;
				setResult((List<Artist>) objects[2]);
				ProgressUpdate progress = (ProgressUpdate) objects[1];
				Throwable potentialException = progress.getPotentialException();
				if (potentialException != null) {
					if (potentialException instanceof ServiceException) {
						Application.toast(potentialException
								.getLocalizedMessage());
					} else {
						Application
								.toast(R.string.ArtistQueryService_errorQueryingArtists
										+ potentialException
												.getLocalizedMessage());
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
					"Can't update progressDialog. Unexpected type/order of arguments");
			return;
		}
	}

	private void setResult(List<Artist> result) {
		this.result = result;
		if (resultView != null) {
			// Display result
			resultView.setAdapter(new ReleaseListAdapter(activity, result));
		}
	}

	@Override
	protected void onPostExecute(List<Artist> result) {
		this.result = result;
		isExecuting = false;
		releasesService.removeArtistProcessedListener(this);
	}

	public void updateActivity(Activity activity, ListView resultView) {
		this.activity = activity;
		this.resultView = resultView;
		/*
		 * If progressDialog is displayed, show a new one with same settings,
		 * belonging to the new activty
		 */
		if (progressDialog != null) {
			progressDialog = showDialog(progressDialog.getProgress(),
					progressDialog.getMax());
		}
	}

	public List<Artist> getResult() {
		return result;
	}

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
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setMessage(Application.getContext().getString(R.string.LoadNewReleasesTask_CheckingArtists));
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.setMax(max);
		dialog.setProgress(progress);
		dialog.show();
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
	public void onProgressFinished(List<Artist> result) {
		publishProgress(ProgressUpdateOperation.PROGRESS_FINISHED, result);
	}

	@Override
	public void onProgressFailed(Artist entity, int progress, int max,
			List<Artist> resultOnFailure, Throwable potentialException) {
		publishProgress(ProgressUpdateOperation.PROGRESS_FINISHED,
				new ProgressUpdate(entity, progress, max, potentialException),
				resultOnFailure);
	}

	public enum ProgressUpdateOperation {
		PROGRESS_STARTED, PROGRESS, PROGRESS_FINISHED, PROGRESS_FAILED
	};

	public class ProgressUpdate {

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

		public void setEntity(Artist entity) {
			this.artist = entity;
		}

		public int getProgress() {
			return progress;
		}

		public void setProgress(int progress) {
			this.progress = progress;
		}

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}

		public Throwable getPotentialException() {
			return potentialException;
		}

		public void setPotentialException(Throwable potentialException) {
			this.potentialException = potentialException;
		}
	}
}
