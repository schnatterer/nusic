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
package info.schnatterer.nusic.android.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;

import info.schnatterer.logbackandroidutils.Logs;
import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.ui.R;
import roboguice.fragment.provided.RoboPreferenceFragment;

@SuppressLint("NewApi")
public class NusicPreferencesDeveloperFragment extends RoboPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences_developer);

        findPreference(getString(R.string.preferences_key_log_level_logcat))
            .setOnPreferenceChangeListener(new LogCatLogLevelPreferenceChangedListener(getActivity()));
        findPreference(getString(R.string.preferences_key_log_level_file))
            .setOnPreferenceChangeListener(new FileLevelPreferenceChangedListener(getActivity()));
    }

    /**
     * Listens for a change in the preference that contains the log level of log cat appender.
     */
    public static class LogCatLogLevelPreferenceChangedListener implements
        Preference.OnPreferenceChangeListener {

        private final Context context;

        public LogCatLogLevelPreferenceChangedListener(Context context) {
            this.context = context;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Logs.setLogCatLevel(newValue.toString(), context);
            //  update the state of the Preference with the new value
            return true;
        }
    }

    /**
     * * Listens for a change in the preference that contains the log level of file appender.
     */
    public static class FileLevelPreferenceChangedListener implements
        Preference.OnPreferenceChangeListener {

        private final Context context;

        public FileLevelPreferenceChangedListener(Context context) {
            this.context = context;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Logs.setThresholdFilterLevel(newValue.toString(), Constants.FILE_APPENDER_NAME, context);
            //  update the state of the Preference with the new value
            return true;
        }
    }
}
