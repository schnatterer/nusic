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
import info.schnatterer.newsic.ui.tasks.LoadNewRelasesTask.FinishedLoadingListener;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MainActivity extends FragmentActivity {
	private static final int RELEASE_DB_LOADER = 0;
	private static final int REQUEST_CODE_PREFERENCE_ACTIVITY = 0;

	private static LoadNewRelasesTask loadReleasesTask = null;
	/** Listens for internet query to end */
	private ReleaseTaskFinishedLoadingListener releaseTaskFinishedLoadingListener = new ReleaseTaskFinishedLoadingListener();

	private ListView releasesListView;

	private ReleaseListAdapter releasesListViewAdapter = null;
	private ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		progressBar = (ProgressBar) findViewById(R.id.releasesProgressBar);
		
		releasesListView = (ListView) findViewById(R.id.releasesListView);
		
		releasesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = releasesListView.getItemAtPosition(position);
				Release release = (Release) o;
				Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri
						.parse(release.getMusicBrainzUri()));
				startActivity(launchBrowser);
			}

		});
		releasesListViewAdapter = new ReleaseListAdapter(this);
		releasesListView.setAdapter(releasesListViewAdapter);

		// Load releases from local db
		getSupportLoaderManager().initLoader(RELEASE_DB_LOADER, null,
				new ReleaseLoaderCallbacks());

		registerListeners();

		AppStart appStart = PreferencesServiceSharedPreferences.getInstance()
				.checkAppStart();

		switch (appStart) {
		case FIRST_TIME_VERSION:
		case FIRST_TIME:
			startLoadingReleasesFromInternet(true);
		default:
			break;
		}
	}

	private void registerListeners() {
		// Set activity as new context of task
		if (loadReleasesTask != null) {
			loadReleasesTask.updateActivity(this, releasesListViewAdapter);
			loadReleasesTask
					.addFinishedLoadingListener(releaseTaskFinishedLoadingListener);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			startLoadingReleasesFromInternet(false);
			break;
		case R.id.action_settings:
			startActivityForResult(new Intent(this,
					NewsicPreferencesActivity.class),
					REQUEST_CODE_PREFERENCE_ACTIVITY);
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK
				&& requestCode == REQUEST_CODE_PREFERENCE_ACTIVITY) {
			if (data.hasExtra(NewsicPreferencesActivity.RETURN_KEY_IS_REFRESH_NECESSARY)) {
				if (data.getExtras()
						.getBoolean(
								NewsicPreferencesActivity.RETURN_KEY_IS_REFRESH_NECESSARY)) {
					startLoadingReleasesFromInternet(true);
				}
			}
		}
	}

	private void startLoadingReleasesFromInternet(boolean forceFullUpdate) {
		if (Application.isOnline()) {
			if (loadReleasesTask == null) {
				// Async task not started yet
				loadReleasesTask = new LoadNewRelasesTask(this, forceFullUpdate);
				loadReleasesTask
						.addFinishedLoadingListener(releaseTaskFinishedLoadingListener);
				loadReleasesTask.execute();
			}
			// Else task is already running
		} else {
			Application.toast(R.string.Activity_notOnline);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerListeners();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterListeners();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterListeners();
	}

	private void unregisterListeners() {
		if (loadReleasesTask != null) {
			// Preserve memory
			loadReleasesTask.updateActivity(null, null);
			loadReleasesTask
					.removeFinishedLoadingListener(releaseTaskFinishedLoadingListener);
		}
	}

	public void setReleases(List<Release> result) {
		releasesListViewAdapter.show(result);
	}

	/**
	 * Listens for the task that queries releases from the internet to end.
	 * Notifies {@link ReleaseLoader} to reload the {@link Release} data for GUI
	 * from local database.
	 * 
	 * @author schnatterer
	 * 
	 */
	public class ReleaseTaskFinishedLoadingListener implements
			FinishedLoadingListener {
		@Override
		public void onFinishedLoading() {
			loadReleasesTask = null; // Task can only be executed once
			// Reload data
			getSupportLoaderManager().getLoader(RELEASE_DB_LOADER)
					.onContentChanged();
		}
	}

	/**
	 * Handles callbacks from {@link ReleaseLoader} that loads the
	 * {@link Release}s from the local database.
	 * 
	 * @author schnatterer
	 * 
	 */
	public class ReleaseLoaderCallbacks implements
			LoaderManager.LoaderCallbacks<AsyncResult<List<Release>>> {

		@Override
		public ReleaseLoader onCreateLoader(int id, Bundle bundle) {
			// if (id == RELEASE_DB_LOADER)
			progressBar.setVisibility(View.VISIBLE);
			return new ReleaseLoader(MainActivity.this);
		}

		@Override
		public void onLoadFinished(Loader<AsyncResult<List<Release>>> loader,
				AsyncResult<List<Release>> result) {
			if (result.getException() != null) {
				Application.toast(R.string.MainActivity_errorLoadingReleases);
			}
			progressBar.setVisibility(View.GONE);
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
}
