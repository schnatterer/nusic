package info.schnatterer.newsic.tasks;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.adapters.ReleaseListAdapter;
import info.schnatterer.newsic.model.Artist;
import info.schnatterer.newsic.service.ReleasesService;
import info.schnatterer.newsic.service.ServiceException;
import info.schnatterer.newsic.service.event.ArtistProcessEvent;
import info.schnatterer.newsic.service.event.ArtistProcessListener;
import info.schnatterer.newsic.service.impl.ReleasesServiceImpl;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

public class LoadNewRelasesTask extends
		AsyncTask<Void, ArtistProcessEvent, List<Artist>> implements
		ArtistProcessListener {
	private Activity activity;
	private ListView releasesListView;

	private ReleasesService releasesService;

	public LoadNewRelasesTask(Activity activity, ListView releasesListView) {
		this.activity = activity;
		this.releasesListView = releasesListView;
		releasesService = new ReleasesServiceImpl(activity);
	}

	/**
	 * {@link AsyncTask} that loads new releases, listens to the progress (via
	 * {@link ArtistProcessListener} and visualizes the progress in a
	 * {@link ProgressDialog}.
	 * 
	 * @author schnatterer
	 * 
	 */
	private ProgressDialog dialog;

	@Override
	protected void onPreExecute() {
		releasesService.addArtistProcessedListener(this);
		// Setup Progress Dialog
		dialog = new ProgressDialog(activity);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		try {
			dialog.setMax(releasesService.getArtists().size());
			dialog.show();
		} catch (ServiceException e) {
			Log.w(Constants.LOG, e.getMessage());
			Application.toast(e.getLocalizedMessage());
			// Don't run task
			throw new RuntimeException(e);
		}
	}

	@Override
	protected List<Artist> doInBackground(Void... arg0) {
		// Do it
		return getListData();
	}

	@Override
	protected void onPostExecute(List<Artist> result) {
		releasesService.removeArtistProcessedListener(this);
		// Display result
		releasesListView.setAdapter(new ReleaseListAdapter(activity,
				result));
	}

	@Override
	protected void onProgressUpdate(ArtistProcessEvent... events) {
		if (events.length < 1 || events[0] == null) {
			return;
		}
		ArtistProcessEvent event = events[0];

		Artist artist = event.getArtist();
		if (artist != null) {
			dialog.incrementProgressBy(1);
		}

		Throwable t = event.getPotentialException();
		if (t != null) {
			if (t instanceof ServiceException) {
				Application.toast(t.getLocalizedMessage());
			} else if (t instanceof SecurityException) {
				// E.g. no internet. Try to output localized msg
				Application.toast(t.getLocalizedMessage());
			} else {
				Application.toast(Application.createGenericErrorMessage(t));
			}
		}
	}

	private List<Artist> getListData() {
		try {
			return releasesService.getNewestReleases();
		} catch (Throwable t) {
			Log.w(Constants.LOG, t.getMessage());
			publishProgress(new ArtistProcessEvent(null, 0, t));
		}
		return new LinkedList<Artist>();
	}

	@Override
	public void artistProcessed(ArtistProcessEvent event) {
		publishProgress(event);
	}
}
