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
package info.schnatterer.nusic.android.service;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.android.activities.MainActivity;
import info.schnatterer.nusic.android.util.ImageUtil;
import info.schnatterer.nusic.android.util.Notification;
import info.schnatterer.nusic.android.util.Notification.NotificationId;
import info.schnatterer.nusic.core.ConnectivityService;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.core.SyncReleasesService;
import info.schnatterer.nusic.core.event.ArtistProgressListener;
import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.dao.ArtworkDao;
import info.schnatterer.nusic.data.dao.ArtworkDao.ArtworkType;
import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.data.model.Release;
import info.schnatterer.nusic.ui.R;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.receiver.RoboBroadcastReceiver;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;

public class LoadNewReleasesService extends WakefulService {
    /** Small icon shown in the status bar when a notification is shwown. */
    private static final int NOTIFICATION_SMALL_ICON = R.drawable.ic_album_white_24dp;

    private static final Logger LOG = LoggerFactory
            .getLogger(LoadNewReleasesService.class);

    /**
     * Key to the creating intent's extras that contains a boolean that triggers
     * loading the release if <code>true</code>.
     */
    public static final String EXTRA_REFRESH_ON_START = "nusic.intent.extra.refreshOnStart";

    @Inject
    private PreferencesService preferencesService;
    @Inject
    private ConnectivityService connectivityService;
    @Inject
    private SyncReleasesService syncReleasesService;
    @Inject
    private ReleaseService releaseService;
    @Inject
    private LoadNewReleasesServiceConnectivityReceiver loadNewReleasesServiceConnectivityReceiver;
    @Inject
    private LoadNewReleasesServiceScheduler loadNewReleasesServiceScheduler;
    @Inject
    private ArtworkDao artworkDao;

    // private int totalArtists = 0;
    private ProgressListenerNotifications progressListenerNotifications = new ProgressListenerNotifications();
    private LoadNewReleasesServiceBinder binder = new LoadNewReleasesServiceBinder();
    /**
     * We're only going to allow one execution at a time.
     */
    private Thread workerThread = null;

    @Override
    public int onStartCommandWakeful(Intent intent, int flags, int startId) {
        LOG.debug(
                "Flags = {}; startId = {}. Intent = {}",
                flags,
                startId,
                intent,
                (intent != null ? (", extra " + EXTRA_REFRESH_ON_START + " = " + intent
                        .getBooleanExtra(EXTRA_REFRESH_ON_START, false)) : ""));
        boolean refreshing = true;

        if (intent == null) {
            // When START_STICKY the intent will be null on "restart" after
            // getting killed
            // TODO RESUME download instead of starting new?
            LOG.debug("Services restarted after being destroyed while workerThread was running.");
            refreshReleases(false, null);
        } else if (intent.getBooleanExtra(EXTRA_REFRESH_ON_START, false)) {
            refreshReleases(false, null);
        } else {
            refreshing = false;
        }

        /*
         * Don't release wake lock after this method ends, as the logic runs in
         * a separate thread.
         *
         * The lock is release onDestroy().
         */
        if (refreshing) {
            keepLock = true;
        }

        return Service.START_STICKY;
    }

    /**
     * Tries to start refreshing releases. If refresh is already in progress,
     * attaches <code>artistProcessedListener</code> to it and returns
     * <code>false</code>.
     *
     * @param updateOnlyIfNecessary
     *            if <code>true</code> the refresh is only done when
     *            {@link SyncReleasesService#isUpdateNeccesarry()} returns
     *            <code>true</code>. Otherwise, the refresh is done at any case.
     * @param artistProcessedListener
     * @return <code>true</code> if refresh was started. <code>false</code> if
     *         already in progress.
     */
    public boolean refreshReleases(boolean updateOnlyIfNecessary,
            ArtistProgressListener artistProcessedListener) {
        if (tryCreateThread(updateOnlyIfNecessary, artistProcessedListener)) {
            return true;
        } else {
            LOG.debug("Service thread already working, only adding process listener");
            syncReleasesService
                    .addArtistProcessedListener(artistProcessedListener);
            return false;
        }
    }

    /**
     * Synchronizes the creation and starting of new {@link #workerThread}s.
     *
     * @param updateOnlyIfNecessary
     * @param artistProcessedListener
     * @return <code>true</code> if thread was started, <code>false</code>
     *         otherwise.
     */
    private synchronized boolean tryCreateThread(boolean updateOnlyIfNecessary,
            ArtistProgressListener artistProcessedListener) {
        if (workerThread != null) {
            return false;
        }
        LOG.debug("Service thread not working yet, starting.");
        workerThread = new Thread(new WorkerThread(updateOnlyIfNecessary, artistProcessedListener));
        workerThread.start();
        return true;
    }

    private class WorkerThread implements Runnable {
        private boolean updateOnlyIfNecesary;
        private ArtistProgressListener artistProgressListener;

        public WorkerThread(boolean updateOnlyIfNecessary,
                ArtistProgressListener artistProgressListener) {
            this.updateOnlyIfNecesary = updateOnlyIfNecessary;
            this.artistProgressListener = artistProgressListener;
        }

        public void run() {
            LOG.debug("Service thread starting work");
            if (!connectivityService.isOnline()) {
                handleOffline();
            } else {
                // Make sure any changes to the online state are ignored
                loadNewReleasesServiceConnectivityReceiver.disableReceiver();

                if (!updateOnlyIfNecesary && hasReadPermissionOrNotify()) {
                    syncReleasesService
                            .addArtistProcessedListener(artistProgressListener);
                    syncReleasesService
                            .addArtistProcessedListener(progressListenerNotifications);

                    long beforeRefresh = System.currentTimeMillis();

                    LOG.debug("Service thread: Calling refreshReleases()");
                    syncReleasesService.syncReleases();

                    // Schedule next run
                    loadNewReleasesServiceScheduler.schedule(
                            preferencesService.getRefreshPeriod(), null);

                    try {
                        notifyNewReleases(beforeRefresh);
                    } catch (ServiceException e) {
                        // Refresh succeeded, so don't tell user
                        LOG.warn(
                            "Refresh succeeded, but database error when trying to find out about new releases",
                            e);
                    }

                    // Remove all listeners
                    syncReleasesService.removeArtistProcessedListeners();
                }
            }

            // stop service
            LOG.debug("Service: Explicit stop self");
            stopSelf();
        }

        private void handleOffline() {
            LOG.debug("Service thread: Not online!");
            // If not online and update necessary, postpone run
            if (!updateOnlyIfNecesary) {
                LOG.debug("Postponing service until online or next schedule");
                loadNewReleasesServiceConnectivityReceiver.enableReceiver();

                // Send status "not online" back to listener?
                if (artistProgressListener != null) {
                    // TODO find a solution without ServiceException here!
                    artistProgressListener.onProgressFailed(null, 0, 0,
                            null,
                            new ServiceException("R.string.NotOnline") {
                                private static final long serialVersionUID = 1L;

                                @Override
                                public String getLocalizedMessage() {
                                    return LoadNewReleasesService.this
                                            .getString(R.string.NotOnline);
                                }
                            });
                }
            } else {
                // Make sure any changes to the online state are ignored
                loadNewReleasesServiceConnectivityReceiver.disableReceiver();
            }
        }

        /**
         * If necessary (depending on the SDK versin of the device), checks if
         * the {@link Manifest.permission#READ_EXTERNAL_STORAGE} is present. If
         * it is not present, notifies an error. <br/>
         *
         * This would be better be done in the nusic-ui-android package, but for
         * now cannot use aar dependencies there (because the
         * android-maven-plugin does not support jars). Pragmatic soulution:
         * Check the permission right here in the service.
         *
         * @return <code>true</code> if the permission is present or checking
         *         was not necessary. Otherwise <code>false</code>.
         */
        private boolean hasReadPermissionOrNotify() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean permissionGranted = isReadExternalStoragePermissionGranted();
                if (!permissionGranted) {
                    /*
                     * TODO a tap on the notifcation opens the trying to request
                     * the permission again
                     */
                    Notification
                            .notifyWarning(
                                    LoadNewReleasesService.this,
                                    LoadNewReleasesService.this
                                            .getString(R.string.LoadNewReleasesService_missingPermissionReadExternalStorage));
                }
                return permissionGranted;
            } else {
                return true;
            }
        }

        /**
         * Checks if the permission for is present.
         *
         * @return <code>true</code> if the permission is present. Otherwise
         *         <code>false</code>.
         */
        @TargetApi(Build.VERSION_CODES.M)
        private boolean isReadExternalStoragePermissionGranted() {
            LOG.debug("Checking if read external storage permission is set");
            boolean permissionGranted = ContextCompat.checkSelfPermission(
                    LoadNewReleasesService.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            LOG.debug("External storage permission = {}", permissionGranted);
            return permissionGranted;
        }
    }

    /**
     * Finds which releases are new to the device and notifies user if enabled
     * in preferences.
     *
     * @param beforeRefresh
     * @throws DatabaseException
     */
    private void notifyNewReleases(long beforeRefresh) throws ServiceException {
        if (preferencesService.isEnabledNotifyNewReleases()) {
            List<Release> newReleases = releaseService
                    .findByDateCreatedGreaterThan(beforeRefresh);
            if (newReleases.size() == 1) {
                notifyNewReleases(newReleases.get(0));
            } else if (newReleases.size() > 0) {
                notifyNewReleases(newReleases.size());
            }
        }
    }

    /**
     * Puts out a notification informing about one release that was just found.<br/>
     * <br/>
     * <br/>
     * Future calls overwrite any previous instances of this notification still
     * on display.
     *
     * @param release
     *
     */
    private void notifyNewReleases(Release release) {
        try {
            Bitmap scaledBitmap = null;
            InputStream artworkStream = artworkDao.findStreamByRelease(release,
                    ArtworkType.SMALL);
            if (artworkStream != null) {
                scaledBitmap = ImageUtil
                        .createScaledBitmap(artworkStream, this);
            }
            Notification.notify(
                    this,
                    NotificationId.NEW_RELEASE,
                    getString(R.string.LoadNewReleasesService_newRelease),
                    release.getArtist().getArtistName() + " - "
                            + release.getReleaseName(),
                    NOTIFICATION_SMALL_ICON, scaledBitmap, MainActivity.class,
                    createExtraActiveTab());
        } catch (DatabaseException e) {
            LOG.warn("Unable to load artwork for notification. " + release, e);
        } catch (IllegalArgumentException e) {
            LOG.warn("Unable scale artwork for notification. " + release, e);
        }
    }

    /**
     * Puts out a notification informing about multiple releases that were just
     * found<br/>
     * <br/>
     * Future calls overwrite any previous instances of this notification still
     * on display.
     *
     * @param nReleases
     *            the number of releases published today
     */
    private void notifyNewReleases(int nReleases) {
        Notification.notify(this, NotificationId.NEW_RELEASE, String.format(
                getString(R.string.LoadNewReleasesService_newReleaseMultiple),
                nReleases), null, NOTIFICATION_SMALL_ICON, null,
                MainActivity.class, createExtraActiveTab());
    }

    /**
     * Creates an extra bundle that contains the tab to be shown when
     * {@link MainActivity} is launched.
     *
     * @return
     */
    private Bundle createExtraActiveTab() {
        Bundle extras = new Bundle();
        extras.putSerializable(MainActivity.EXTRA_ACTIVE_TAB,
                MainActivity.TabDefinition.JUST_ADDED);
        return extras;
    }

    /**
     * Creates an intent that, when started as service, directly calls
     * {@link #refreshReleases(boolean, ArtistProgressListener)}.
     *
     * @return
     */
    public static Intent createIntentRefreshReleases(Context context) {
        Intent intent = new Intent(context, LoadNewReleasesService.class);
        intent.putExtra(EXTRA_REFRESH_ON_START, true);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        LOG.debug("Nusic service: onDestroy()");

        if (workerThread != null && workerThread.isAlive()) {
            LOG.debug("Services destroyed while workerThread is running.");
        }
        workerThread = null;
        if (syncReleasesService != null) {
            syncReleasesService
                    .removeArtistProcessedListener(progressListenerNotifications);
        }
        releaseLock(this.getApplicationContext());
    }

    public boolean isRunning() {
        return workerThread != null && workerThread.isAlive();
    }

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LoadNewReleasesServiceBinder extends Binder {
        public LoadNewReleasesService getService() {
            return LoadNewReleasesService.this;
        }
    }

    /**
     * Broadcast receiver that triggers execution of
     * {@link LoadNewReleasesService} after a scheduled alarm.
     *
     * @author schnatterer
     *
     */
    public static class LoadNewReleasesServiceAlarmReceiver extends
            RoboBroadcastReceiver {
        @Override
        public void handleReceive(final Context context, final Intent intent) {
            LOG.debug("Alarm Receiver: Alarm received!");
            // Acquire lock, making sure device is not going to sleep again
            acquireLock(context);
            context.startService(LoadNewReleasesService
                    .createIntentRefreshReleases(context));
        }
    }

    /**
     * Progress listeners that displays any crucial info as android
     * notification.
     *
     * @author schnatterer
     *
     */
    private class ProgressListenerNotifications implements
            ArtistProgressListener {
        private int totalArtists = 0;
        private List<Artist> errorArtists;
        private Set<String> exceptions;

        @Override
        public void onProgressStarted(int nEntities) {
            errorArtists = new LinkedList<>();
            exceptions = new HashSet<>();
            totalArtists = nEntities;
        }

        @Override
        public void onProgress(Artist entity, int progress, int max,
                Throwable potentialException) {
            if (potentialException != null) {
                if (potentialException instanceof ServiceException) {
                    errorArtists.add(entity);
                    ServiceException serviceException = (ServiceException) potentialException;
                    if (serviceException.getCause() != null) {
                        exceptions.add(serviceException.getCause().toString());
                    } else {
                        exceptions.add(serviceException.toString());
                    }
                } else {
                    exceptions.add(potentialException.toString());
                }
            }
        }

        @Override
        public void onProgressFinished(Boolean result) {
            if (errorArtists != null && errorArtists.size() > 0 && preferencesService.isNotifyRefreshErrors()) {
                Notification.notifyWarning(LoadNewReleasesService.this,
                    R.string.LoadNewReleasesBinding_finishedWithErrors,
                   errorArtists.size(), totalArtists);

                LOG.warn("Loading releases finished with errors for {}/{} artists",
                    errorArtists.size(), totalArtists);
                LOG.warn("{} different exceptions while loading releases: {}", exceptions.size(),
                    exceptions);
            }
        }

        @Override
        public void onProgressFailed(Artist entity, int progress, int max,
                                     Boolean result, Throwable potentialException) {
            if (potentialException == null) {
                return;
            }
            LOG.error(potentialException.getMessage(), potentialException);
            if (potentialException instanceof ServiceException) {
                Notification.notifyWarning(LoadNewReleasesService.this,
                    getString(R.string.LoadNewReleasesBinding_errorFindingReleases)
                        + potentialException.getLocalizedMessage());
            } else {
                Notification.notifyWarning(LoadNewReleasesService.this,
                    getString(R.string.LoadNewReleasesBinding_errorFindingReleasesGeneric)
                        + potentialException.getClass().getSimpleName());

            }
        }
    }

    public static class LoadNewReleasesServiceScheduler {
        @Inject
        private Context context;
        @Inject
        private PreferencesService preferencesService;

        /**
         * Schedule this task to run regularly.
         *
         * @param triggerAt
         *            if <code>null</code>, the first start will be now +
         *            <code>intervalDays</code>
         */
        public void schedule(int intervalDays, Date triggerAt) {
            Date triggerAtDate = triggerAt;
            if (triggerAt == null) {
                Calendar triggerAtCal = Calendar.getInstance();
                triggerAtCal.add(Calendar.DAY_OF_MONTH, intervalDays);
                // triggerAtCal.add(Calendar.SECOND, 60);
                triggerAtDate = triggerAtCal.getTime();
            }

            // debug: Starts service once per minute
            // triggerAtDate = DateUtils.addMinutes(60);

            // // Start service directly, on alarm
            // PendingIntent pintent = PendingIntent.getService(context, 0,
            // createIntentRefreshReleases(context),
            // PendingIntent.FLAG_UPDATE_CURRENT);

            /*
             * Start service directly via receiver that acquires a wake lock, in
             * order to avoid the device falling back to sleep before service is
             * started
             */
            PendingIntent pintent = PendingIntent.getBroadcast(context,
                    Constants.Alarms.NEW_RELEASES.ordinal(),
                    new Intent(context,
                            LoadNewReleasesServiceAlarmReceiver.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarm = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            /*
             * Set a repeating schedule, so there always is a next alarm even
             * when one alarm should fail for some reason
             */
            alarm.setInexactRepeating(AlarmManager.RTC,
                    triggerAtDate.getTime(), AlarmManager.INTERVAL_DAY
                            * intervalDays, pintent);
            preferencesService.setNextReleaseRefresh(triggerAtDate);
            LOG.debug("Scheduled task to run again every " + intervalDays
                    + " days, starting at " + triggerAtDate);
        }
    }
}
