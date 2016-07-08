/**
 * Copyright (C) 2013 Johannes Schnatterer
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
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.android.activities;

import info.schnatterer.nusic.Constants.Loaders;
import info.schnatterer.nusic.android.LoadNewRelasesServiceBinding;
import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.android.fragments.ReleaseListFragment;
import info.schnatterer.nusic.android.service.LoadNewReleasesService;
import info.schnatterer.nusic.android.util.TextUtil;
import info.schnatterer.nusic.android.util.Toast;
import info.schnatterer.nusic.ui.R;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActionBarActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * The activity that is started when the app starts.
 * 
 * The tab that is shown can parameterized using the {@link #EXTRA_ACTIVE_TAB},
 * that contains a {@link TabDefinition}.
 * 
 * This activity uses the toolbar as action bar, so better don't use it in
 * conjuntion with a theme that uses an action bar.
 * 
 * @author schnatterer
 *
 */
public class MainActivity extends RoboActionBarActivity {
    private static final Logger LOG = LoggerFactory
            .getLogger(MainActivity.class);

    /** The request code used when starting {@link PreferenceActivity}. */
    private static final int REQUEST_CODE_PREFERENCE_ACTIVITY = 0;
    /**
     * Key to the creating intent's extras that contains a {@link TabDefinition}
     * (as int) that can be passed to this activity within the bundle of an
     * intent. The corresponding value represents the Tab that is set active
     * with the intent.
     */
    public static final String EXTRA_ACTIVE_TAB = "nusic.intent.extra.main.activeTab";

    /**
     * Request permission {@link Manifest.permission#READ_EXTERNAL_STORAGE} and
     * call {@link #startLoadingReleasesFromInternet(boolean)} with
     * <code>false</code> parameter.
     */
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_FALSE = 0;
    /**
     * Request permission {@link Manifest.permission#READ_EXTERNAL_STORAGE} and
     * call {@link #startLoadingReleasesFromInternet(boolean)} with
     * <code>true</code> parameter.
     */
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_TRUE = 1;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set currentTab
        if (getIntent().hasExtra(EXTRA_ACTIVE_TAB)) {
            setCurrentTab(((TabDefinition) getIntent().getExtras().get(
                    EXTRA_ACTIVE_TAB)));
        }

        // Use toolbar as actionBar, if no actionBar yet
        if (getSupportActionBar() == null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
            setSupportActionBar(toolbar);
        }

        ViewPager pager = (ViewPager) findViewById(R.id.mainPager);

        /* Init tab fragments */
        TabLayout tabLayout = (TabLayout) findViewById(R.id.mainTabs);

        // Set adapter that handles fragment (i.e. tab creation)
        TabFragmentPagerAdapter tabAdapter = new TabFragmentPagerAdapter(
                getSupportFragmentManager());
        // Create all tabs as defined
        for (TabDefinition tab : TabDefinition.values()) {
            tabAdapter.addFragment(createTabFragment(tab),
                    getString(tab.titleId));
        }
        pager.setAdapter(tabAdapter);
        // Set current tab
        pager.setCurrentItem(currentTab.position);
        tabLayout.setupWithViewPager(pager);

        // Handle first app start, if necessary
        if (!onCreateCalled) {
            onCreateCalled();
            switch (NusicApplication.getAppStart()) {
            case FIRST:
                showWelcomeDialog(TextUtil.loadTextFromAsset(this,
                        "welcomeDialog.html"));
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

    public static synchronized void setCurrentTab(TabDefinition currentTab) {
        MainActivity.currentTab = currentTab;
    }

    private static synchronized void onCreateCalled() {
       onCreateCalled = true;
    }

    /**
     * Calls {@link #registerListeners()} and also
     * {@link #startLoadingReleasesFromInternet(boolean)} if necessary.
     */
    private void registerListenersAndStartLoading(boolean forceUpdate) {
        if (isLoadNewReleasesServiceBindingCreated()) {
            registerListeners();
            if (forceUpdate) {
                requestPermissionOrStartLoadingReleasesFromInternet(false);
            } else {
                // Update only if necessary
                requestPermissionOrStartLoadingReleasesFromInternet(true);
            }
        } else {
            /*
             * TODO it would be best to also request the permissions here in
             * order to "annoy" any users that checked "Never ask again".
             */
            registerListeners();
        }
    }

    /**
     * Basically, this method calls
     * {@link #startLoadingReleasesFromInternet(boolean)}.
     * 
     * Depending on the SDK version of the device requests the
     * {@link Manifest.permission#READ_EXTERNAL_STORAGE} permission before.
     * 
     * @param updateOnlyIfNecessary
     */
    private void requestPermissionOrStartLoadingReleasesFromInternet(
            boolean updateOnlyIfNecessary) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Just call the method
            startLoadingReleasesFromInternet(updateOnlyIfNecessary);
        } else {
            // Do the brand new permission dance
            requestPermissionThenStartLoadingReleasesFromInternet(updateOnlyIfNecessary);
        }
    }

    /**
     * Calls {@link #startLoadingReleasesFromInternet(boolean)} asynchronously
     * via Android M's {@link #onRequestPermissionsResult(int, String[], int[])}
     * mechanism.
     * 
     * @param updateOnlyIfNecessary
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionThenStartLoadingReleasesFromInternet(
            boolean updateOnlyIfNecessary) {
        LOG.debug("Requesting read external storage permission");
        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                updateOnlyIfNecessaryToRequestCode(updateOnlyIfNecessary));
    }

    private void showDialogAccessDeniedOnce(final boolean updateOnlyIfNecessary) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.MainActivity_AccessDeniedOnceTitle)
                .setMessage(R.string.MainActivity_AccessDeniedOnceMessage)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // Just ask again
                                requestPermissionThenStartLoadingReleasesFromInternet(updateOnlyIfNecessary);
                            }
                        }).setIcon(android.R.drawable.ic_dialog_info).show();
    }

    private void showDialogAccessDeniedPermanently() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.MainActivity_AccessDeniedPermanentlyTitle)
                .setMessage(
                        R.string.MainActivity_AccessDeniedPermanentlyMessage)
                .setPositiveButton(
                        R.string.MainActivity_AccessDeniedPermanentlyPositiveButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                Intent intent = new Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(
                        R.string.MainActivity_AccessDeniedPermanentlyNegativeButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }

    /**
     * "Encodes" updateOnlyIfNecessary into two different request codes
     * 
     * @param updateOnlyIfNecessary
     * @return {@link #PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_TRUE}
     *         if <code>updateOnlyIfNecessary</code> is <code>true</code>.
     *         Otherwise
     *         {@link #PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_FALSE}
     *         .
     */
    private int updateOnlyIfNecessaryToRequestCode(boolean updateOnlyIfNecessary) {
        if (updateOnlyIfNecessary) {
            return PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_TRUE;
        }
        return PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_FALSE;
    }

    /**
     * "Decodes" the different request codes to boolean updateOnlyIfNecessary
     * 
     * @param requestCode
     * @return <code>true</code> if
     *         {@link #PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_TRUE}
     *         is passed. Otherwise <code>false</code>
     */
    private boolean requestCodeToUpdateOnlyIfNecessary(int requestCode) {
        return requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_TRUE;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
        // Fall through, because the request code
        case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_FALSE:
        case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_FORCE_UPDATE_ONLY_IF_NECESSARY_TRUE: {
            final boolean updateOnlyIfNecessary = requestCodeToUpdateOnlyIfNecessary(requestCode);
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LOG.debug("Read external storage permission granted");
                    startLoadingReleasesFromInternet(updateOnlyIfNecessary);
                } else if (neverAskAgainCheckedForReadExternalStorage()) {
                    /*
                     * Never ask again selected, or device policy prohibits the
                     * app from having that permission.
                     */
                    LOG.info("Read external storage permission request permanently denied. Nusic is never going to work");
                    // inform the user nusic is not going to work.
                    showDialogAccessDeniedPermanently();
                } else {
                    // Permission denied once
                    LOG.info("Read external storage permission request denied once");
                    // Inform the user that nusic needs this permission
                    showDialogAccessDeniedOnce(updateOnlyIfNecessary);
                }
            } else {
                LOG.warn("Read external storage permission request cancelled");
            }
            break;
        }
        default:
            LOG.warn("Unexpected permission request code {}", requestCode);
            break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean neverAskAgainCheckedForReadExternalStorage() {
        return !ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private ReleaseListFragment createTabFragment(TabDefinition tab) {
        // Create fragment
        Bundle bundle = new Bundle();
        bundle.putInt(ReleaseListFragment.EXTRA_LOADER_ID, tab.loaderId);
        return (ReleaseListFragment) Fragment.instantiate(this,
                ReleaseListFragment.class.getName(), bundle);
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
            /* Init tab fragments */
            setCurrentTab((TabDefinition) intent.getExtras().get(
                    EXTRA_ACTIVE_TAB));
            ViewPager pager = (ViewPager) findViewById(R.id.mainPager);
            pager.setCurrentItem(currentTab.position);
        }
    }

    private void registerListeners() {
        // Set activity as new context of task
        loadNewRelasesServiceBinding.updateActivity(this);
        // loadReleasesTask.bindService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
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

    private void startLoadingReleasesFromInternet(boolean updateOnlyIfNecessary) {
        boolean wasRunning = !loadNewRelasesServiceBinding.refreshReleases(
                this, updateOnlyIfNecessary);
        LOG.debug("Explicit refresh triggered. Service was "
                + (wasRunning ? "" : " not ") + "running before");

        if (wasRunning && !updateOnlyIfNecessary) {
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

    private static synchronized boolean isLoadNewReleasesServiceBindingCreated() {
        if (loadNewRelasesServiceBinding == null) {
            loadNewRelasesServiceBinding = new LoadNewRelasesServiceBinding();
            return true;
        }
        return false;
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
    static class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
