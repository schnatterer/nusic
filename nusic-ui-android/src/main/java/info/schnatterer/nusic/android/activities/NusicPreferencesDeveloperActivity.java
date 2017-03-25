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

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import info.schnatterer.nusic.android.fragments.NusicPreferencesDeveloperFragment;
import info.schnatterer.nusic.android.fragments.NusicPreferencesDeveloperFragment.LogCatLogLevelPreferenceChangedListener;
import info.schnatterer.nusic.android.fragments.NusicPreferencesDeveloperFragment.FileLevelPreferenceChangedListener;
import info.schnatterer.nusic.ui.R;
import roboguice.activity.RoboAppCompatPreferenceActivity;

/**
 * Activity that realizes the developer settings.
 *
 * @author schnatterer
 *
 */
public class NusicPreferencesDeveloperActivity extends
        RoboAppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the back arrow in the header (left of the icon)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            onCreatePreferenceActivity();
        } else {
            onCreatePreferenceFragment();
        }
    }

    /**
     * Wraps legacy {@link #onCreate(Bundle)} code for Android < 3 (i.e. API lvl
     * < 11).
     */
    @SuppressWarnings("deprecation")
    private void onCreatePreferenceActivity() {
        addPreferencesFromResource(R.xml.preferences_developer);
        findPreference(getString(R.string.preferences_key_log_level_logcat))
            .setOnPreferenceChangeListener(new LogCatLogLevelPreferenceChangedListener(this));
        findPreference(getString(R.string.preferences_key_log_level_file))
            .setOnPreferenceChangeListener(new FileLevelPreferenceChangedListener(this));
    }

    /**
     * Wraps {@link #onCreate(Bundle)} code for Android >= 3 (i.e. API lvl >=
     * 11).
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void onCreatePreferenceFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(
                        android.R.id.content,
                        Fragment.instantiate(this,
                                NusicPreferencesDeveloperFragment.class
                                        .getName())).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // When the back arrow in the header (left of the icon) is clicked,
            // "go back one activity"
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
