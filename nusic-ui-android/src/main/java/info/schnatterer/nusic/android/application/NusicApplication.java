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
package info.schnatterer.nusic.android.application;

import info.schnatterer.logbackandroidutils.Logs;
import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.android.service.ReleasedTodayService.ReleasedTodayServiceScheduler;
import info.schnatterer.nusic.core.PreferencesService;
import roboguice.RoboGuice;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class NusicApplication extends AbstractApplication {
    private static final String DEPRECATED_PREFERENCES_KEY_REFRESH_SUCCESFUL = "last_release_refresh_succesful";
    private static final String DEPRECATED_PREFERENCES_KEY_FULL_UPDATE = "fullUpdate";
    private static final String DEPRECATED_PREFERENCES_KEY_FUTURE_RELEASES = "includeFutureReleases";
    private static final String DEPRECATED_PREFERENCES_KEY_LAST_APP_VERSION = "last_app_version";

    /**
     * Constant mapping of version name to version code.
     */
    public interface NusicVersion {
        /**
         * v.3.1.0
         */
        int V_3_1_0 = 19;
    }

    private ReleasedTodayServiceScheduler releasedTodayServiceScheduler;

    @Override
    public void onCreate() {
        /*
         * Create global configuration and initialize ImageLoader with this
         * configuration
         */
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).memoryCacheSize(2 * 1024 * 1024)
                .build();
        ImageLoader.getInstance().init(config);

        initGlobals();

        /*
         * Enable annotation database to improve performance.
         */
        RoboGuice.setUseAnnotationDatabases(true);

        /*
         * Can't use annotations in this class, because the module is set up in
         * this very method. So get instances explicitly here.
         */
        releasedTodayServiceScheduler = RoboGuice.getInjector(this)
                .getInstance(ReleasedTodayServiceScheduler.class);

        // Set log levels (this overrides settings in logback.xml)
        PreferencesService preferenceService = RoboGuice.getInjector(this)
                .getInstance(PreferencesService.class);
        Logs.setThresholdFilterLevel(preferenceService.getLogLevelFile(), Constants.FILE_APPENDER_NAME, this);
        // Set log cat level
        Logs.setLogCatLevel(preferenceService.getLogLevelLogCat(), this);

        // Causes onUpgrade() to be called, etc.
        super.onCreate();
    }

    @Override
    protected void onFirstCreate() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        /*
         * Is this actually an first start ever or is it an upgrade from version
         * < V_0_6? Before using the onUpgrade() mechanism from
         * AbstractApplication the last version was stored in default shared
         * preferences. Do all the clean up for this version here.
         */
        int lastAppVersion = sharedPreferences.getInt(
                DEPRECATED_PREFERENCES_KEY_LAST_APP_VERSION,
                DEFAULT_LAST_APP_VERSION);
        if (lastAppVersion > DEFAULT_LAST_APP_VERSION) {
            // Clean up preferences
            sharedPreferences.edit()
                    .remove(DEPRECATED_PREFERENCES_KEY_FUTURE_RELEASES)
                    .commit();
            sharedPreferences.edit()
                    .remove(DEPRECATED_PREFERENCES_KEY_LAST_APP_VERSION)
                    .commit();
            sharedPreferences.edit()
                    .remove(DEPRECATED_PREFERENCES_KEY_FULL_UPDATE).commit();
            sharedPreferences.edit()
                    .remove(DEPRECATED_PREFERENCES_KEY_REFRESH_SUCCESFUL)
                    .commit();
            setLastVersionCode(lastAppVersion);
            setAppStart(AppStart.UPGRADE);
        }
        /*
         * Make sure the Release Today service is scheduled (if not switched off
         * in preferences). Schedule it only after updates and new installations
         * to avoid overhead.
         */
        releasedTodayServiceScheduler.schedule();
    }

    @Override
    protected void onUpgrade(int oldVersion, int newVersion) {
        if (oldVersion <= NusicVersion.V_3_1_0) {
            /* Changed the way of setting the file log level from root to file appender.
             * Clean up preferences */
            SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
            sharedPreferences.edit().remove("logLevel").apply();
        }
        /*
         * Make sure the Release Today service is scheduled (if not switched off
         * in preferences). Schedule it only after updates and new installations
         * to avoid overhead.
         */
        releasedTodayServiceScheduler.schedule();
    }

}
