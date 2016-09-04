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
package info.schnatterer.nusic.android.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import android.content.Context;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;

/**
 * Basic abstraction of the log mechanism (SLF4J + logback-android) used here.
 */
public final class Logs {
    /**
     * Name of the logcat logger, as configured in logback.xml
     */
    static final String LOGCAT_LOGGER_NAME = "LOGCAT";

    /**
     * Name of the directory where log files are stored under
     * <code>/data/data/appname/files/</code> Create a reference to this
     * directory via {@link android.content.ContextWrapper#getFilesDir()}.
     */
    public static final String LOG_FOLDER = "logs";

    private Logs() {
    }

    /**
     * Set the log level of the root logger. <b>Note</b>: This depends on the
     * actual logging framework.
     *
     * @param level the log level to set
     */
    public static void setRootLogLevel(String level) {
        Logger root = (Logger) LoggerFactory
            .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.info("root.getLevel(): {}", root.getLevel().toString());
        root.info("Setting level to {}", level);
        root.setLevel(Level.toLevel(level));
    }

    /**
     * Returns the current log file.
     *
     * @param context context to get the app-private files from
     * @return the current log file
     */
    public static File findNewestLogFile(Context context) {
        return findNewestLogFile(getLogFiles(context));
    }

    /**
     * Returns all log files.
     *
     * @param context context to get the app-private files from
     * @return all log files
     */
    public static File[] getLogFiles(Context context) {
        return getLogFileDirectory(context).listFiles();
    }

    /**
     * Returns the folder where log files are stored.
     *
     * @param context context to get the app-private files from
     * @return the directory that contains log files
     */
    public static File getLogFileDirectory(Context context) {
        return new File(context.getFilesDir(), LOG_FOLDER);
    }

    /**
     * Sets the threshold log level for the logCat appender. Note: Must be >=
     * Root Log Level. If not, a warning is toasted.
     *
     * @param logLevelLogCat the threshold level to set for logCat appender
     * @param context        (optional) context needed for toasting warnings. If
     *                       <code>null</code> no toast is displayed
     */
    public static void setLogCatLevel(String logLevelLogCat, Context context) {
        /* Find appender */
        Logger root = (Logger) LoggerFactory
            .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        LogcatAppender logcatAppender = (LogcatAppender) root
            .getAppender(LOGCAT_LOGGER_NAME);
        if (logcatAppender == null) {
            warnAndToast(context, root,
                "No appender \"" + LOGCAT_LOGGER_NAME + "\" configured. Can't change threshold");
            return;
        }

        setLogCatThresholdFilter(logLevelLogCat, logcatAppender, root);

        /* Result */
        root.info("Setting logcat level to {}. root.getLevel(): {}",
            logLevelLogCat, root.getLevel().toString());

        if (greaterThan(root.getLevel(), Level.toLevel(logLevelLogCat))) {
            warnAndToast(context, root,
                String.format("Root level(%s) > logcat level (%s)!", root.getLevel(), logLevelLogCat)
            );
        }
    }

    /**
     * Actually sets the logCat level. This in realized using a {@link ThresholdFilter}.
     *
     * @param logLevelLogCat the level to set to the threshold filter
     * @param logcatAppender logcat appender to change
     * @param root           root logger, needed as context and for logging changes
     */
    private static void setLogCatThresholdFilter(String logLevelLogCat,
                                                 LogcatAppender logcatAppender, Logger root) {
        /* Find and change filter */
        List<Filter<ILoggingEvent>> filters = logcatAppender
            .getCopyOfAttachedFiltersList();
        ThresholdFilter threshold = null;
        for (Filter<ILoggingEvent> filter : filters) {
            if (filter instanceof ThresholdFilter) {
                threshold = (ThresholdFilter) filter;
                break;
            }
        }
        if (threshold == null) {
            root.info("No threshold filter in \"LOGCAT\" configured. Creating new one");
            threshold = new ThresholdFilter();
            threshold.setContext(root.getLoggerContext());
            filters.add(threshold);
        }
        threshold.setLevel(logLevelLogCat);
        threshold.start();

        /* Apply changed filter to appender */
        logcatAppender.clearAllFilters();
        for (Filter<ILoggingEvent> filter : filters) {
            logcatAppender.addFilter(filter);
        }
    }

    /**
     * @return <code>true</code> if <code>a</code> greater than <code>b</code>. Otherwise <code>false</code>
     */
    private static boolean greaterThan(Level a, Level b) {
        return a.toInt() > b.toInt();
    }

    /**
     * Writes message to {@link Logger#warn(Marker, String, Object)} and
     * {@link android.widget.Toast}.
     */
    private static void warnAndToast(Context context, Logger logger, String message) {
        logger.warn(message);
        if (context != null) {
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sort array of files descending by name and returns the first one. For
     * file names like <code>2015-06-25.log</code> and
     * <code>2015-06-26.log</code> this returns the newest log file, for example
     * <code>2015-06-26.log</code>.
     *
     * @param logFiles the array of log files
     * @return the newest log file or <code>null</code> if <code>logFiles</code> is <code>null</code>
     */
    static File findNewestLogFile(File[] logFiles) {
        // Sort descendingly
        if (logFiles == null) {
            return null;
        }
        Arrays.sort(logFiles, Collections.reverseOrder());
        return logFiles[0];
    }
}
