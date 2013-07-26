package info.schnatterer.newsic.ui.activities;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.loader.AsyncResult;
import info.schnatterer.newsic.db.loader.ReleaseLoader;
import info.schnatterer.newsic.db.model.Release;
import info.schnatterer.newsic.service.PreferencesService.AppStart;
import info.schnatterer.newsic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.newsic.ui.adapters.ReleaseListAdapter;
import info.schnatterer.newsic.ui.tasks.LoadNewRelasesTask;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<AsyncResult<List<Release>>> {
	private static final int RELEASE_DB_LOADER = 0;

	private static LoadNewRelasesTask asyncTask = null;

	private ListView releasesListView;

	private ReleaseListAdapter releasesListViewAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		releasesListView = (ListView) findViewById(R.id.releasesListView);
		releasesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = releasesListView.getItemAtPosition(position);
				Release release = (Release) o;
				Application.toast("Selected :" + " " + release.getArtistName()
						+ " - " + release.getReleaseName());
			}

		});
		releasesListViewAdapter = new ReleaseListAdapter(this);
		releasesListView.setAdapter(releasesListViewAdapter);

		AppStart appStart = PreferencesServiceSharedPreferences.getInstance()
				.checkAppStart();
		switch (appStart) {
		// case FIRST_TIME_VERSION:
		// break;
		case FIRST_TIME:
			firstAppStartEver();
		default:
			// Load releases from db
			getSupportLoaderManager().initLoader(RELEASE_DB_LOADER, null, this);
			break;
		}
	}

	private void firstAppStartEver() {
		if (Application.isOnline()) {
			if (asyncTask == null) {
				// Async task not started yet
				asyncTask = new LoadNewRelasesTask(this,
						releasesListViewAdapter);
				asyncTask.execute();
			} else {
				// Set activity as new context of task
				asyncTask.updateActivity(this, releasesListViewAdapter);
				if (asyncTask.getResult() != null) {
					// If async task is already finished
					setReleases(asyncTask.getResult());
				}
			}
		} else {
			Application.toast(R.string.Activity_notOnline);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (asyncTask != null) {
			// Preserve memory
			asyncTask.updateActivity(null, null);
		}
	}

	public void setReleases(List<Release> result) {
		releasesListViewAdapter.show(result);
	}

	@Override
	public ReleaseLoader onCreateLoader(int id, Bundle bundle) {
		// if (id == RELEASE_DB_LOADER)
		return new ReleaseLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<AsyncResult<List<Release>>> loader,
			AsyncResult<List<Release>> result) {
		if (result.getException() != null) {
			Application.toast(R.string.MainActivity_errorLoadingReleases);
		}

		if (result.getData() != null && result.getData().isEmpty()) {
			// // Set the empty text
			// final TextView empty = (TextView) mRootView
			// .findViewById(R.id.empty);
			// empty.setText(getString(R.string.empty_music));
			return;
		}

		setReleases(result.getData());

	}

	@Override
	public void onLoaderReset(Loader<AsyncResult<List<Release>>> result) {
		setReleases(null);
	}
}
