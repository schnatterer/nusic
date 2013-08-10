package info.schnatterer.newsic.ui.activities;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.loader.ReleaseLoader;
import info.schnatterer.newsic.db.model.Release;
import info.schnatterer.newsic.service.PreferencesService.AppStart;
import info.schnatterer.newsic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.newsic.ui.fragments.ReleaseListFragment;
import info.schnatterer.newsic.ui.fragments.ReleaseListFragment.ReleaseQuery;
import info.schnatterer.newsic.ui.tasks.LoadNewRelasesTask;
import info.schnatterer.newsic.ui.tasks.LoadNewRelasesTask.FinishedLoadingListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {
	private static final int REQUEST_CODE_PREFERENCE_ACTIVITY = 0;

	private static final int RELEASE_DB_LOADER_ALL = 0;
	private static final int RELEASE_DB_LOADER_NEWLY_ADDED = 1;

	private static LoadNewRelasesTask loadReleasesTask = null;
	/** Listens for internet query to end */
	private ReleaseTaskFinishedLoadingListener releaseTaskFinishedLoadingListener = new ReleaseTaskFinishedLoadingListener();
	private ReleaseListFragment currentTab = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Init tab fragments */
		// Create the Actionbar
		ActionBar actionBar = getSupportActionBar();

		// Create Actionbar Tabs
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create first Tab
		// Pragmatic approach: Use releaseQuery also as Tag to identify fragment
		ReleaseQuery releaseQuery = ReleaseQuery.ALL;
		actionBar.addTab(actionBar
				.newTab()
				.setText(R.string.MainActivity_TabAll)
				.setTabListener(
						new ReleaseTabListener(releaseQuery,
								RELEASE_DB_LOADER_ALL, releaseQuery.name())));

		// Create Second Tab
		releaseQuery = ReleaseQuery.NEWLY_ADDED;
		actionBar.addTab(actionBar
				.newTab()
				.setText(R.string.MainActivity_TabNewlyAdded)
				.setTabListener(
						new ReleaseTabListener(releaseQuery,
								RELEASE_DB_LOADER_NEWLY_ADDED, releaseQuery
										.name())));

		/* Init app */
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
			loadReleasesTask.updateActivity(this);
			loadReleasesTask
					.addFinishedLoadingListener(releaseTaskFinishedLoadingListener);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			startLoadingReleasesFromInternet(false);
		} else if (item.getItemId() == R.id.action_settings) {
			startActivityForResult(new Intent(this,
					NewsicPreferencesActivity.class),
					REQUEST_CODE_PREFERENCE_ACTIVITY);
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK
				&& requestCode == REQUEST_CODE_PREFERENCE_ACTIVITY) {
			// Change in preferences require a full refresh
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
			loadReleasesTask.updateActivity(null);
			loadReleasesTask
					.removeFinishedLoadingListener(releaseTaskFinishedLoadingListener);
		}
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
			if (currentTab != null) {
				currentTab.reloadFromDb();
			}
		}
	}

	/**
	 * Creates a new {@link ReleaseListFragment} on every tab change.
	 * 
	 * @author schnatterer
	 * 
	 */
	public class ReleaseTabListener implements TabListener {
		private ReleaseQuery releaseQuery;
		private int loaderId;
		private Fragment fragment;
		private final String tag;

		public ReleaseTabListener(ReleaseQuery releaseQuery, int loaderId,
				String tag) {
			this.releaseQuery = releaseQuery;
			this.loaderId = loaderId;
			this.tag = tag;
			fragment = getSupportFragmentManager().findFragmentByTag(tag);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Avoid calling Fragment.onCreate() more than once
			if (fragment == null) {
				Bundle bundle = new Bundle();
				bundle.putString(ReleaseListFragment.ARG_RELEASE_QUERY,
						releaseQuery.name());
				bundle.putInt(ReleaseListFragment.ARG_LOADER_ID, loaderId);
				fragment = Fragment.instantiate(MainActivity.this,
						ReleaseListFragment.class.getName(), bundle);
				ft.replace(android.R.id.content, fragment, tag);
			} else {
				if (fragment.isDetached()) {
					ft.attach(fragment);
				}
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (fragment != null) {
				ft.detach(fragment);
			}
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {

		}
	}
}
