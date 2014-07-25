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
import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.service.android.LoadNewReleasesService;
import info.schnatterer.nusic.service.android.ReleasedTodayService;
import info.schnatterer.nusic.ui.LoadNewRelasesServiceBinding;
import info.schnatterer.nusic.ui.fragments.ReleaseListFragment;
import info.schnatterer.nusic.ui.fragments.ReleaseListFragment.ReleaseQuery;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {
	private static final int REQUEST_CODE_PREFERENCE_ACTIVITY = 0;

	private static final int RELEASE_DB_LOADER_ALL = 0;
	private static final int RELEASE_DB_LOADER_JUST_ADDED = 1;
	private static final int RELEASE_DB_LOADER_AVAILABLE = 2;
	private static final int RELEASE_DB_LOADER_ANNOUNCED = 3;

	/** Start and bind the {@link LoadNewReleasesService}. */
	private static LoadNewRelasesServiceBinding loadNewRelasesServiceBinding = null;
	/** Stores the selected tab, even when the configuration changes. */
	private static int currentTabPosition = 0;

	private static boolean isReleasedTodayServiceScheduled = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set underlying layout (view pager that captures swipes)
		setContentView(R.layout.activity_main);

		/* Init tab fragments */
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Capture page swipes
		ViewPager pager = (ViewPager) findViewById(R.id.mainPager);
		ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				// Find the ViewPager Position
				actionBar.setSelectedNavigationItem(position);
			}
		};
		pager.setOnPageChangeListener(ViewPagerListener);
		// Set adapter that handles fragment (i.e. tab creation)
		TabFragmentPagerAdapter tabAdapter = new TabFragmentPagerAdapter(
				getSupportFragmentManager());
		pager.setAdapter(tabAdapter);

		// Create all tabs as defined in adapter
		TabListener tabListener = new TabListener(pager);
		for (int i = 0; i < tabAdapter.tabs.length; i++) {
			TabHolder tab = tabAdapter.tabs[i];
			actionBar.addTab(actionBar.newTab().setText(tab.titleId)
					.setTabListener(tabListener), i, isTabSelected(i));
		}

		if (loadNewRelasesServiceBinding == null) {
			loadNewRelasesServiceBinding = new LoadNewRelasesServiceBinding();
			registerListeners();
			startLoadingReleasesFromInternet(true);
		} else {
			registerListeners();
		}

		/*
		 * Make sure the Release Today service is scheduled (if not swichted off
		 * in preferences). Try to schedule it not too often.
		 */
		// TODO trigger only once with new version
		if (!isReleasedTodayServiceScheduled) {
			ReleasedTodayService.schedule(this);
			isReleasedTodayServiceScheduled = true;
		}
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
			if (data.getBooleanExtra(
					NusicPreferencesActivity.RETURN_KEY_IS_REFRESH_NECESSARY,
					false)) {
				startLoadingReleasesFromInternet(false);
			}
			if (data.getBooleanExtra(
					NusicPreferencesActivity.RETURN_KEY_IS_CONTENT_CHANGED,
					false)) {
				onContentChanged();
			}
		}
	}

	private void startLoadingReleasesFromInternet(boolean updateOnlyIfNeccesary) {
		boolean wasRunning = !loadNewRelasesServiceBinding.refreshReleases(
				this, updateOnlyIfNeccesary);
		Log.d(Constants.LOG, "Explicit refresh triggered. Service was "
				+ (wasRunning ? "" : " not ") + "running before");

		if (wasRunning && !updateOnlyIfNeccesary) {
			// Task is already running, just show dialog
			Application.toast(R.string.MainActivity_refreshAlreadyInProgress);
			loadNewRelasesServiceBinding.showDialog();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerListeners();
		if (loadNewRelasesServiceBinding.checkDataChanged()) {
			onContentChanged();
		}
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		final ViewPager pager = (ViewPager) findViewById(R.id.mainPager);
		final TabFragmentPagerAdapter adapter = (TabFragmentPagerAdapter) pager
				.getAdapter();
		if (adapter != null) {
			runOnUiThread(new Runnable() {
				public void run() {
					adapter.onContentChanged();
				}
			});
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterListeners();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterListeners();
	}

	private void unregisterListeners() {
		loadNewRelasesServiceBinding.updateActivity(null);
		loadNewRelasesServiceBinding.unbindService();
	}

	/**
	 * A fragment pager adapter that defines the content of the tabs and manages
	 * the fragments that basically show the content of the tabs.
	 * 
	 * @author schnatterer
	 * 
	 */
	public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
		/** This basically defines the content of the tabs. */
		final TabHolder[] tabs = {
				new TabHolder(R.string.MainActivity_TabJustAdded,
						RELEASE_DB_LOADER_JUST_ADDED, ReleaseQuery.JUST_ADDED),
				new TabHolder(R.string.MainActivity_TabAvailable,
						RELEASE_DB_LOADER_AVAILABLE, ReleaseQuery.AVAILABLE),
				new TabHolder(R.string.MainActivity_TabAnnounced,
						RELEASE_DB_LOADER_ANNOUNCED, ReleaseQuery.ANNOUNCED),
				new TabHolder(R.string.MainActivity_TabAll,
						RELEASE_DB_LOADER_ALL, ReleaseQuery.ALL) };

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * Marks the content of all tabs as changed.
		 */
		public void onContentChanged() {
			for (TabHolder tab : tabs) {
				if (tab.fragment != null) {
					tab.fragment.onContentChanged();
				}
			}
		}

		@Override
		public Fragment getItem(int position) {
			int actualPos = position;
			if (tabs.length <= position) {
				// Default tab
				actualPos = 0;
			}
			ReleaseListFragment fragment = createTabFragment(tabs[actualPos]);
			if (position < tabs.length) {
				tabs[position].fragment = fragment;
			}
			return fragment;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			if (position < tabs.length) {
				tabs[position].fragment = null;
			}
		}

		private ReleaseListFragment createTabFragment(TabHolder tab) {
			// Create fragment
			Bundle bundle = new Bundle();
			bundle.putString(ReleaseListFragment.ARG_RELEASE_QUERY,
					tab.releaseQuery.name());
			bundle.putInt(ReleaseListFragment.ARG_LOADER_ID, tab.loaderId);
			return (ReleaseListFragment) Fragment.instantiate(
					MainActivity.this, ReleaseListFragment.class.getName(),
					bundle);
		}

		@Override
		public int getCount() {
			return tabs.length;
		}
	}

	/**
	 * Holds the basic information for each tab.
	 * 
	 * @author schnatterer
	 */
	public static class TabHolder {
		final int titleId;
		final int loaderId;
		final ReleaseQuery releaseQuery;
		ReleaseListFragment fragment = null;

		public TabHolder(int titleId, int loaderId, ReleaseQuery releaseQuery) {
			this.titleId = titleId;
			this.loaderId = loaderId;
			this.releaseQuery = releaseQuery;
		}
	}

	/**
	 * Tab listener that reports the current position to a view pager.
	 * 
	 * @author schnatterer
	 */
	public class TabListener implements ActionBar.TabListener {
		private ViewPager pager;

		public TabListener(ViewPager pager) {
			this.pager = pager;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Pass the position on tab click to ViewPager
			pager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	};
}
