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

import info.schnatterer.nusic.Constants;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;

import android.content.Context;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;

/**
 * Basic abstraction of the log mechanism (SLF4J + logback-android) used here.
 * 
 * @author schnatterer
 *
 */
public final class Logs {
    /** Name of the logcat logger, as configured in logback.xml */
    private static final String LOGCAT_LOGGER_NAME = "LOGCAT";

    private Logs() {
    }

    /**
     * Set the log level of the root logger. <b>Note</b>: This depends on the
     * actual logging framework.
     * 
     * @param level
     *            the log level to set
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
     * @param context
     *            context to get the app-private files from
     * 
     * @return the current log file
     */
    public static File findNewestLogFile(Context context) {
        return findNewestLogFile(getLogFiles(context));
    }

    /**
     * Sort array of files descending by name and returns the first one. For
     * file names like <code>2015-06-25.log</code> and
     * <code>2015-06-26.log</code> this returns the newest log file, for example
     * <code>2015-06-26.log</code>.
     * 
     * @param logFiles
     *            the array of log files
     * @return the
     */
    static File findNewestLogFile(File[] logFiles) {
        // Sort descendingly
        Arrays.sort(logFiles, Collections.reverseOrder());
        return logFiles[0];
    }

    /**
     * Returns all log files.
     * 
     * @param context
     *            context to get the app-private files from
     * @return all log files
     */
    public static File[] getLogFiles(Context context) {
        // TODO NPE check?
        return getLogFileDirectory(context).listFiles();
    }

    /**
     * Returns the folder where log files are stored.
     * 
     * @param context
     *            context to get the app-private files from
     * @return the directory that contains log files
     */
    public static File getLogFileDirectory(Context context) {
        return new File(context.getFilesDir(), Constants.LOG_FOLDER);
    }

    /**
     * Sets the threshold log level for the logCat appender. Note: Must be >=
     * Root Log Level. If not, a warning is toasted.
     * 
     * @param logLevelLogCat
     *            the threshold level to set for logCat appender
     * @param context
     *            (optional) context needed for toasting warnings. If
     *            <code>null</code> no toast is displayed
     */
    public static void setLogCatLevel(String logLevelLogCat, Context context) {
        /* Find appender */
        Logger root = (Logger) LoggerFactory
                .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        LogcatAppender logcatAppender = (LogcatAppender) root
                .getAppender(LOGCAT_LOGGER_NAME);
        if (logcatAppender == null) {
            String message = "No appender \"" + LOGCAT_LOGGER_NAME
                    + "\" configured. Can't change threshold";
            root.warn(message);
            if (context != null) {
                Toast.toast(context, message);
            }
            return;
        }

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

        /* Result */
        root.info("Setting logcat level to {}. root.getLevel(): {}",
                logLevelLogCat, root.getLevel().toString());
        if (root.getLevel().toInt() > Level.toLevel(logLevelLogCat).toInt()) {
            String message = "Root level(" + root.getLevel().toString()
                    + ") > logcat level (" + logLevelLogCat + ")";
            root.warn(message);
            if (context != null) {
                Toast.toast(context, message);
            }
        }
    }
}
