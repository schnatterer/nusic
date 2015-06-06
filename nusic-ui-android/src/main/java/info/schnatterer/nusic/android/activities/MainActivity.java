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
package info.schnatterer.nusic.android.activities;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.Constants.Loaders;
import info.schnatterer.nusic.android.LoadNewRelasesServiceBinding;
import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.android.fragments.ReleaseListFragment;
import info.schnatterer.nusic.android.service.LoadNewReleasesService;
import info.schnatterer.nusic.android.util.TextUtil;
import info.schnatterer.nusic.android.util.Toast;
import info.schnatterer.nusic.ui.R;
import roboguice.activity.RoboSherlockFragmentActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * The activity that is started when the app starts.
 * 
 * The tab that is shown can parameterized using the {@link #EXTRA_ACTIVE_TAB},
 * that contains a {@link TabDefinition}.
 * 
 * @author schnatterer
 *
 */
public class MainActivity extends RoboSherlockFragmentActivity {
	/** The request code used when starting {@link PreferenceActivity}. */
	private static final int REQUEST_CODE_PREFERENCE_ACTIVITY = 0;
	/**
	 * Key to the creating intent's extras that contains a {@link TabDefinition}
	 * (as int) that can be passed to this activity within the bundle of an
	 * intent. The corresponding value represents the Tab that is set active
	 * with the intent.
	 */
	public static final String EXTRA_ACTIVE_TAB = "nusic.intent.extra.main.activeTab";

	/** Start and bind the {@link LoadNewReleasesService}. */
	private static LoadNewRelasesServiceBinding loadNewRelasesServiceBinding = null;

	/**
	 * <code>false</code>, if {@link #onCreate(Bundle)} has never been called.
	 * Useful for not showing the welcome screen multiple times.
	 */
	private static boolean onCreateCalled = false;

	/**
	 * Stores the selected tab, even when the configuration changes. The tab
	 * assigned here is the one that is shown when the application is started
	 * for the first time.
	 */
	private static TabDefinition currentTab = TabDefinition.JUST_ADDED;

	// /**
	// * Provider that is used to get instances to assign to static
	// * #loadNewRelasesServiceBinding on demand.
	// */
	// @Inject
	// private Provider<LoadNewRelasesServiceBinding>
	// loadNewRelasesServiceBindingProvider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ViewPager pager = (ViewPager) findViewById(R.id.mainPager);

		/* Init tab fragments */
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Capture page swipes
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

		if (getIntent().hasExtra(EXTRA_ACTIVE_TAB)) {
			currentTab = ((TabDefinition) getIntent().getExtras().get(
					EXTRA_ACTIVE_TAB));
		}
		// Create all tabs as defined in adapter
		TabListener tabListener = new TabListener(pager);
		for (TabDefinition tab : TabDefinition.values()) {
			actionBar.addTab(actionBar.newTab().setText(tab.titleId)
					.setTabListener(tabListener), tab.position,
					isTabSelected(tab));
		}

		// Handle first app start, if necessary
		if (!onCreateCalled) {
			onCreateCalled = true;
			switch (NusicApplication.getAppStart()) {
			case FIRST:
				showWelcomeDialog(TextUtil.loadTextFromAsset(this,
						"welcomeDialog.html", true));
				/*
				 * The initialization is finished once the user dismisses the
				 * dialog in order to avoid overlapping dialogs.
				 */
				return;
			case UPGRADE:
				showWelcomeDialog(TextUtil.loadTextFromAsset(this,
						"CHANGELOG.html"));
				/*
				 * The initialization is finished once the user dismisses the
				 * dialog in order to avoid overlapping dialogs.
				 */
				return;
			default:
				break;
			}
		}
		// Finish initialization, because no dialog was opened
		registerListenersAndStartLoading(false);
	}

	/**
	 * Calls {@link #registerListeners()} and also
	 * {@link #startLoadingReleasesFromInternet(boolean)} if necessary.
	 */
	private void registerListenersAndStartLoading(boolean forceUpdate) {
		if (loadNewRelasesServiceBinding == null) {
			// loadNewRelasesServiceBinding =
			// loadNewRelasesServiceBindingProvider
			// .get();
			loadNewRelasesServiceBinding = new LoadNewRelasesServiceBinding();
			registerListeners();
			if (forceUpdate) {
				startLoadingReleasesFromInternet(false);
			} else {
				// Update ony if necessary
				startLoadingReleasesFromInternet(true);
			}

		} else {
			registerListeners();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		/*
		 * This is needed for Android 2.x when clicking a notification an the
		 * task is already running, the notification is not delivered, but
		 * instead this method is called. getIntent() seems to always return the
		 * first intent that started the app.
		 * 
		 * For Android 4.4.2 this seems not to be called anymore. However, the
		 * intent is delivered anyway and getIntent() always returns the last
		 * intent that was delivered.
		 */
		if (intent.hasExtra(EXTRA_ACTIVE_TAB)) {
			getSupportActionBar()
					.setSelectedNavigationItem(
							((TabDefinition) intent.getExtras().get(
									EXTRA_ACTIVE_TAB)).position);
		}
	}

	/**
	 * @param tab
	 * @return <code>true</code> if <code>tab</code> is selected, otherwise
	 *         <code>false</code>
	 */
	private boolean isTabSelected(TabDefinition tab) {
		if (currentTab.equals(tab)) {
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
				Toast.toast(this,
						R.string.MainActivity_pleaseWaitUntilRefreshIsFinished);
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
					NusicPreferencesActivity.EXTRA_RESULT_IS_REFRESH_NECESSARY,
					false)) {
				startLoadingReleasesFromInternet(false);
			}
			if (data.getBooleanExtra(
					NusicPreferencesActivity.EXTRA_RESULT_IS_CONTENT_CHANGED,
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
			Toast.toast(this, R.string.MainActivity_refreshAlreadyInProgress);
			loadNewRelasesServiceBinding.showDialog();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (loadNewRelasesServiceBinding != null) {
			registerListeners();
			if (loadNewRelasesServiceBinding.checkDataChanged()) {
				onContentChanged();
			}
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
					for (TabDefinition tabDefinition : TabDefinition.values()) {
						Loader<Object> loader = getSupportLoaderManager()
								.getLoader(tabDefinition.loaderId);
						if (loader != null) {
							loader.onContentChanged();
						}
					}
				}
			});
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (loadNewRelasesServiceBinding != null) {
			unregisterListeners();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (loadNewRelasesServiceBinding != null) {
			unregisterListeners();
		}
	}

	private void unregisterListeners() {
		loadNewRelasesServiceBinding.updateActivity(null);
		loadNewRelasesServiceBinding.unbindService();
	}

	/**
	 * Shows an alert dialog displaying some text. Useful for welcome messages.
	 * Calls {@link #registerListenersAndStartLoading()} when the dialog is
	 * dismissed.
	 * 
	 * @param text
	 *            text to display. If loading from an asset, consider using
	 *            {@link TextUtil#loadTextFromAsset(android.content.Context, String)}
	 */
	@SuppressLint("InflateParams")
	// See http://www.doubleencore.com/2013/05/layout-inflation-as-intended/
	private void showWelcomeDialog(CharSequence text) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		View layout = getLayoutInflater().inflate(
				R.layout.simple_textview_layout, null, false);
		TextView textView = (TextView) layout
				.findViewById(R.id.renderRawHtmlTextView);
		textView.setText(text);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		alertDialogBuilder
				.setTitle(
						getString(R.string.WelcomeScreenTitle,
								NusicApplication.getCurrentVersionName()))
				.setIcon(R.drawable.ic_launcher)
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						registerListenersAndStartLoading(true);
					}
				})
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						registerListenersAndStartLoading(true);
					}
				}).setView(layout).show();
	}

	/**
	 * Holds the basic information for each tab.
	 * 
	 * @author schnatterer
	 */
	public static enum TabDefinition {
		/** First tab: Just added */
		JUST_ADDED(R.string.MainActivity_TabJustAdded,
				Loaders.RELEASE_LOADER_JUST_ADDED),
		/** Second tab: Available releases */
		AVAILABLE(R.string.MainActivity_TabAvailable,
				Loaders.RELEASE_LOADER_AVAILABLE),
		/** Third tab: Announced releases */
		ANNOUNCED(R.string.MainActivity_TabAnnounced,
				Loaders.RELEASE_LOADER_ANNOUNCED),
		/** Fourth tab: All releases */
		ALL(R.string.MainActivity_TabAll, Loaders.RELEASE_LOADER_ALL);

		private final int position;
		private final int titleId;
		private final int loaderId;

		private TabDefinition(int titleId, int loaderId) {
			this.position = ordinal();
			this.titleId = titleId;
			this.loaderId = loaderId;
		}
	}

	/**
	 * A fragment pager adapter that defines the content of the tabs and manages
	 * the fragments that basically show the content of the tabs.
	 * 
	 * @author schnatterer
	 * 
	 */
	public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
		ReleaseListFragment[] fragments = new ReleaseListFragment[TabDefinition
				.values().length];

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			int actualPos = position;
			if (fragments.length <= position) {
				// Default tab
				actualPos = 0;
			}
			ReleaseListFragment fragment = createTabFragment(TabDefinition
					.values()[actualPos]);
			if (position < fragments.length) {
				fragments[position] = fragment;
			}
			return fragment;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			if (position < fragments.length) {
				fragments[position] = null;
			}
		}

		private ReleaseListFragment createTabFragment(TabDefinition tab) {
			// Create fragment
			Bundle bundle = new Bundle();
			bundle.putInt(ReleaseListFragment.EXTRA_LOADER_ID, tab.loaderId);
			return (ReleaseListFragment) Fragment.instantiate(
					MainActivity.this, ReleaseListFragment.class.getName(),
					bundle);
		}

		@Override
		public int getCount() {
			return fragments.length;
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
			currentTab = TabDefinition.values()[tab.getPosition()];
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	};
}
