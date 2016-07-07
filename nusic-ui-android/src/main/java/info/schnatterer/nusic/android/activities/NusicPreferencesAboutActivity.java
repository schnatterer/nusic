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

import info.schnatterer.nusic.android.application.NusicApplication;
import info.schnatterer.nusic.android.fragments.NusicPreferencesAboutFragment;
import info.schnatterer.nusic.android.util.TextUtil;
import info.schnatterer.nusic.ui.R;
import roboguice.activity.RoboAppCompatPreferenceActivity;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.view.MenuItem;

/**
 * Activity that realizes the "about" section of the app.
 * 
 * @author schnatterer
 *
 */
public class NusicPreferencesAboutActivity extends
        RoboAppCompatPreferenceActivity {
    private static final String TEMPLATE_NAME_VERSION_AND_CONTRIBUTORS = "<h2>%1$s %2$s</h2><h4>%3$s</h4><p>%4$s</p>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the back arrow in the header (left of the icon)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(
                getString(R.string.preferences_category_about,
                        getString(R.string.app_name)));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            onCreatePreferenceActivity();

            // Set version
            findPreferenceActivity(getString(R.string.preferences_key_version))
                    .setSummary(NusicApplication.getCurrentVersionName());
        } else {
            onCreatePreferenceFragment();
        }
    }

    /**
     * Wraps legacy {@link #findPreference(CharSequence)} code for Android < 3
     * (i.e. API lvl < 11).
     */
    @SuppressWarnings("deprecation")
    private Preference findPreferenceActivity(String key) {
        return findPreference(key);
    }

    /**
     * Wraps legacy {@link #onCreate(Bundle)} code for Android < 3 (i.e. API lvl
     * < 11).
     */
    @SuppressWarnings("deprecation")
    private void onCreatePreferenceActivity() {
        addPreferencesFromResource(R.xml.preferences_about);
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
                                NusicPreferencesAboutFragment.class.getName()))
                .commit();
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
