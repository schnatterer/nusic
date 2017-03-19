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
package info.schnatterer.nusic.android;

import android.app.Application;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import org.slf4j.bridge.SLF4JBridgeHandler;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.core.ArtistService;
import info.schnatterer.nusic.core.ConnectivityService;
import info.schnatterer.nusic.core.DeviceMusicService;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.RemoteMusicDatabaseService;
import info.schnatterer.nusic.core.SyncReleasesService;
import info.schnatterer.nusic.core.impl.ArtistServiceImpl;
import info.schnatterer.nusic.core.impl.ConnectivityServiceAndroid;
import info.schnatterer.nusic.core.impl.DeviceMusicServiceAndroid;
import info.schnatterer.nusic.core.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.core.impl.ReleaseServiceImpl;
import info.schnatterer.nusic.core.impl.RemoteMusicDatabaseServiceMusicBrainz;
import info.schnatterer.nusic.core.impl.RemoteMusicDatabaseServiceMusicBrainz.ApplicationContact;
import info.schnatterer.nusic.core.impl.RemoteMusicDatabaseServiceMusicBrainz.ApplicationName;
import info.schnatterer.nusic.core.impl.RemoteMusicDatabaseServiceMusicBrainz.ApplicationVersion;
import info.schnatterer.nusic.core.impl.SyncReleasesServiceImpl;
import info.schnatterer.nusic.data.NusicDatabaseSqlite;
import info.schnatterer.nusic.data.dao.ArtistDao;
import info.schnatterer.nusic.data.dao.ArtworkDao;
import info.schnatterer.nusic.data.dao.ReleaseDao;
import info.schnatterer.nusic.data.dao.fs.ArtworkDaoFileSystem;
import info.schnatterer.nusic.data.dao.sqlite.ArtistDaoSqlite;
import info.schnatterer.nusic.data.dao.sqlite.ReleaseDaoSqlite;

/**
 * Google guice {@link com.google.inject.Module} that configures the bindings
 * service interfaces and the implementing classes. The information provided by
 * this class is used by guice when resolving {@link javax.inject.Inject}
 *
 * @author schnatterer
 */
public class NusicAndroidModule extends AbstractModule {
    private Application application;

    public NusicAndroidModule(Application application) {
        this.application = application;
    }

    @Override
    protected void configure() {
        installSlf4jJulHandler();

        /*
         * Database requires static injection, because the context must be
         * injected at construction time
         */
        requestStaticInjection(NusicDatabaseSqlite.class);
        /*
         * A lot of constants are injected here
         */
        requestStaticInjection(PreferencesServiceSharedPreferences.class);

        // Services
        bind(ArtistService.class).to(ArtistServiceImpl.class);
        bind(ConnectivityService.class).to(ConnectivityServiceAndroid.class);
        bind(DeviceMusicService.class).to(DeviceMusicServiceAndroid.class);
        bind(PreferencesService.class).to(
            PreferencesServiceSharedPreferences.class);
        bind(ReleaseService.class).to(ReleaseServiceImpl.class);
        bind(RemoteMusicDatabaseService.class).to(
            RemoteMusicDatabaseServiceMusicBrainz.class);
        bind(SyncReleasesService.class).to(SyncReleasesServiceImpl.class);

        // DAOs
        bind(ReleaseDao.class).to(ReleaseDaoSqlite.class);
        bind(ArtistDao.class).to(ArtistDaoSqlite.class);
        bind(ArtworkDao.class).to(ArtworkDaoFileSystem.class);

        // Resources
        bind(String.class).annotatedWith(ApplicationName.class).toInstance(application.getString(R.string.app_name));
        bind(String.class).annotatedWith(ApplicationVersion.class).toInstance(NusicApplication.getCurrentVersionName());
        bind(String.class).annotatedWith(ApplicationContact.class).toInstance(application.getString(R.string.app_url));

        bind(String.class).annotatedWith(Names.named("PreferencesKeyDownloadOnlyOnWifi")).toInstance(application.getString(R.string.preferences_key_download_only_on_wifi));
        bind(Boolean.class).annotatedWith(Names.named("PreferencesDefaultDownloadOnlyOnWifi")).toInstance(application.getResources().getBoolean(R.bool.preferences_default_download_only_on_wifi));
        bind(String.class).annotatedWith(Names.named("PreferencesKeyDownloadReleasesTimePeriod")).toInstance(application.getString(R.string.preferences_key_download_releases_time_period));
        bind(String.class).annotatedWith(Names.named("PreferencesDefaultDownloadReleasesTimePeriod")).toInstance(application.getString(R.string.preferences_default_download_releases_time_period));
        bind(String.class).annotatedWith(Names.named("PreferencesKeyRefreshPeriod")).toInstance(application.getString(R.string.preferences_key_refresh_period));
        bind(String.class).annotatedWith(Names.named("PreferencesDefaultRefreshPeriod")).toInstance(application.getString(R.string.preferences_default_refresh_period));
        bind(String.class).annotatedWith(Names.named("PreferencesKeyIsEnabledNotifyReleasedToday")).toInstance(application.getString(R.string.preferences_key_is_enabled_notify_released_today));
        bind(Boolean.class).annotatedWith(Names.named("PreferencesDefaultIsEnabledNotifyReleasedToday")).toInstance(application.getResources().getBoolean(R.bool.preferences_default_is_enabled_notify_released_today));
        bind(String.class).annotatedWith(Names.named("PreferencesKeyIsEnabledNotifyNewReleases")).toInstance(application.getString(R.string.preferences_key_is_enabled_notify_new_releases));
        bind(Boolean.class).annotatedWith(Names.named("PreferencesDefaultIsEnabledNotifyNewReleases")).toInstance(application.getResources().getBoolean(R.bool.preferences_default_is_enabled_notify_new_releases));
        bind(String.class).annotatedWith(Names.named("PreferencesKeyReleasedTodayHourOfDay")).toInstance(application.getString(R.string.preferences_key_released_today_hour_of_day));
        bind(Integer.class).annotatedWith(Names.named("PreferencesDefaultReleasedTodayHourOfDay")).toInstance(parseIntOrThrow("PreferencesDefaultReleasedTodayHourOfDay", application.getString(R.string.preferences_default_released_today_hour_of_day)));
        bind(String.class).annotatedWith(Names.named("PreferencesKeyReleasedTodayMinute")).toInstance(application.getString(R.string.preferences_key_released_today_minute));
        bind(Integer.class).annotatedWith(Names.named("PreferencesDefaultReleasedTodayMinute")).toInstance(parseIntOrThrow("PreferencesDefaultReleasedTodayMinute", application.getString(R.string.preferences_default_released_today_minute)));
        bind(String.class).annotatedWith(Names.named("PreferencesKeyLogLevelFile")).toInstance(application.getString(R.string.preferences_key_log_level_file));
        bind(String.class).annotatedWith(Names.named("PreferencesDefaultLogLevelFile")).toInstance(application.getString(R.string.preferences_default_log_level_file));
        bind(String.class).annotatedWith(Names.named("PreferencesKeyLogLevelLogCat")).toInstance(application.getString(R.string.preferences_key_log_level_logcat));
        bind(String.class).annotatedWith(Names.named("PreferencesDefaultLogLevelLogCat")).toInstance(application.getString(R.string.preferences_default_log_level_logcat));
        bind(String.class).annotatedWith(Names.named("PreferencesKeyNotifyRefreshErrors")).toInstance(application.getString(R.string.preferences_key_notify_refresh_errors));
        bind(Boolean.class).annotatedWith(Names.named("PreferencesDefaultNotifyRefreshErrors")).toInstance((application.getResources().getBoolean(R.bool.preferences_default_notify_refresh_errors)));
    }

    /**
     * Installs the java.util.logging (jul-to-slf4j) {@link SLF4JBridgeHandler}.
     * As a result, all (yes, also {@link java.util.logging.Level#FINE}, etc.)
     * Level JUL log statements are routed to SLF4J.
     */
    private void installSlf4jJulHandler() {
        /*
         * add SLF4JBridgeHandler to j.u.l's root logger, should be done once
         * during the initialization phase of your application
         */
        SLF4JBridgeHandler.install();
    }

    private Integer parseIntOrThrow(String atNamedAnnotation, String prefValue) {
        try {
            return Integer.parseInt(prefValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                "Unable to parse integer from property \""
                    + atNamedAnnotation + "\", value:" + prefValue,
                e);
        }
    }

}
