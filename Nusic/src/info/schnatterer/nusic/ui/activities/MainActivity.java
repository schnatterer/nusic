/* Copyright (C) 2013 Johannes Schnatterer
 * 
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *  
 * This file is part of nusic.
 * 
 * nusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.ui.activities;

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.NusicDatabase;
import info.schnatterer.nusic.db.loader.ReleaseLoader;
import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.service.android.LoadNewReleasesService;
import info.schnatterer.nusic.ui.LoadNewRelasesServiceBinding;
import info.schnatterer.nusic.ui.LoadNewRelasesServiceBinding.FinishedLoadingListener;
import info.schnatterer.nusic.ui.fragments.ReleaseListFragment;
import info.schnatterer.nusic.ui.fragments.ReleaseListFragment.ReleaseQuery;

import java.util.HashSet;
import java.util.Set;

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
	private static final int RELEASE_DB_LOADER_JUST_ADDED = 1;

	/**
	 * Start and bind the {@link LoadNewReleasesService}.
	 */
	private static LoadNewRelasesServiceBinding loadNewRelasesServiceBinding = null;
	// Stores the selected tab, even when the configuration changes.
	private static int currentTabPosition = 0;
	/** Listens for internet query to end */
	private ReleaseServiceFinishedLoadingListener releaseTaskFinishedLoadingListener = new ReleaseServiceFinishedLoadingListener();
	private ReleaseListFragment currentTabFragment = null;

	private Set<ReleaseTabListener> tabListeners = new HashSet<ReleaseTabListener>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Init tab fragments */
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create first Tab
		createTab(actionBar, R.string.MainActivity_TabJustAdded,
				RELEASE_DB_LOADER_JUST_ADDED, ReleaseQuery.JUST_ADDED, 0);

		// Create Second Tab
		createTab(actionBar, R.string.MainActivity_TabAll,
				RELEASE_DB_LOADER_ALL, ReleaseQuery.ALL, 1);

		if (loadNewRelasesServiceBinding == null) {
			loadNewRelasesServiceBinding = new LoadNewRelasesServiceBinding();
			registerListeners();
			startLoadingReleasesFromInternet(true);
		} else {
			registerListeners();
		}
	}

	/**
	 * Creates a tab and sets it active depending on {@link #currentTabPosition}
	 * .
	 * 
	 * @param actionBar
	 * @param titleId
	 * @param loaderId
	 * @param releaseQuery
	 * @param position
	 */
	private void createTab(ActionBar actionBar, int titleId, int loaderId,
			ReleaseQuery releaseQuery, int position) {
		// Pragmatic approach: Use releaseQuery also as Tag to identify fragment
		ReleaseTabListener listener = new ReleaseTabListener(releaseQuery,
				loaderId, releaseQuery.name());
		tabListeners.add(listener);
		actionBar.addTab(
				actionBar.newTab().setText(titleId).setTabListener(listener),
				position, isTabSelected(position));
	}

	/**
	 * @param position
	 * @return <code>true</code> if <code>position</code> is selected, otherwise
	 *         <code>false</code>
	 */
	private boolean isTabSelected(int position) {
		if (currentTabPosition == position) {
			return true;
		}
		return false;
	}

	private void registerListeners() {
		// Set activity as new context of task
		loadNewRelasesServiceBinding.updateActivity(this);
		loadNewRelasesServiceBinding
				.addFinishedLoadingListener(releaseTaskFinishedLoadingListener);
		// loadReleasesTask.bindService();
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
			if (!loadNewRelasesServiceBinding.isRunning()) {
				startActivityForResult(new Intent(this,
						NusicPreferencesActivity.class),
						REQUEST_CODE_PREFERENCE_ACTIVITY);
			} else {
				/*
				 * Refreshing releases is in progress, stop user from changing
				 * service related preferences
				 */
				// TODO cancel service and restart if necessary after changing
				// of preferences?
				Application
						.toast(R.string.MainActivity_pleaseWaitUntilRefreshIsFinished);
				loadNewRelasesServiceBinding.showDialog();
			}
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK
				&& requestCode == REQUEST_CODE_PREFERENCE_ACTIVITY) {
			// Change in preferences require a full refresh
			if (data.hasExtra(NusicPreferencesActivity.RETURN_KEY_IS_REFRESH_NECESSARY)) {
				if (data.getExtras()
						.getBoolean(
								NusicPreferencesActivity.RETURN_KEY_IS_REFRESH_NECESSARY)) {
					startLoadingReleasesFromInternet(false);
				}
			}
		}
	}

	private void startLoadingReleasesFromInternet(boolean updateOnlyIfNeccesary) {
		boolean isStarted = loadNewRelasesServiceBinding.refreshReleases(this,
				updateOnlyIfNeccesary);

		if (!isStarted && !updateOnlyIfNeccesary) {
			// Task is already running, just show dialog
			Application.toast(R.string.MainActivity_refreshAlreadyInProgress);
			loadNewRelasesServiceBinding.showDialog();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerListeners();
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		registerListeners();
//	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterListeners();
	}

//	@Override
//	protected void onPause() {
//		super.onPause();
//		unregisterListeners();
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterListeners();
		NusicDatabase.getInstance().close();
	}

	private void unregisterListeners() {
		loadNewRelasesServiceBinding.updateActivity(null);
		loadNewRelasesServiceBinding
				.removeFinishedLoadingListener(releaseTaskFinishedLoadingListener);
		loadNewRelasesServiceBinding.unbindService();
	}

	/**
	 * Listens for the task that queries releases from the internet to end.
	 * Notifies {@link ReleaseLoader} to reload the {@link Release} data for GUI
	 * from local database.
	 * 
	 * @author schnatterer
	 * 
	 */
	public class ReleaseServiceFinishedLoadingListener implements
			FinishedLoadingListener {
		@Override
		public void onFinishedLoading(boolean resultChanged) {
			if (resultChanged) {
				// Mark all loaders as changed
				for (ReleaseTabListener listener : tabListeners) {
					if (listener.fragment != null) {
						listener.fragment.onContentChanged();
					}
				}
				// Reload data on current tab
				if (currentTabFragment != null) {
					currentTabFragment.foreceLoad();
				}
			}
		}
	}

	/**
	 * Creates a new {@link ReleaseListFragment} for ever tab which is
	 * attached/detached on select/unselect.
	 * 
	 * @author schnatterer
	 * 
	 */
	public class ReleaseTabListener implements TabListener {
		private ReleaseQuery releaseQuery;
		private int loaderId;
		private ReleaseListFragment fragment;
		private final String tag;

		public ReleaseTabListener(ReleaseQuery releaseQuery, int loaderId,
				String tag) {
			this.releaseQuery = releaseQuery;
			this.loaderId = loaderId;
			this.tag = tag;
			fragment = (ReleaseListFragment) getSupportFragmentManager()
					.findFragmentByTag(tag);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Avoid calling Fragment.onCreate() more than once
			if (fragment == null) {
				// Create fragment
				Bundle bundle = new Bundle();
				bundle.putString(ReleaseListFragment.ARG_RELEASE_QUERY,
						releaseQuery.name());
				bundle.putInt(ReleaseListFragment.ARG_LOADER_ID, loaderId);
				fragment = (ReleaseListFragment) Fragment.instantiate(
						MainActivity.this, ReleaseListFragment.class.getName(),
						bundle);
				ft.replace(android.R.id.content, fragment, tag);
			} else {
				if (fragment.isDetached()) {
					ft.attach(fragment);
				}
			}
			currentTabPosition = tab.getPosition();
			currentTabFragment = fragment;
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
