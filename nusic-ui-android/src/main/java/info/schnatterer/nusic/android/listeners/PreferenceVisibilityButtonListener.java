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
package info.schnatterer.nusic.android.listeners;

import info.schnatterer.nusic.android.util.Toast;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.ui.R;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;

/**
 * Creates a preference listener that opens an {@link AlertDialog}. If it is
 * answered with yes, all releases and artists are set to visible (
 * {@link Artist#setHidden(Boolean)}=<code>true</code>).<br/>
 * <br/>
 * <b>Make sure to call {@link #setContext(Context)}.</b>
 */
public class PreferenceVisibilityButtonListener implements
        Preference.OnPreferenceClickListener {
    private static final Logger LOG = LoggerFactory
            .getLogger(PreferenceVisibilityButtonListener.class);

    private Activity activity;

    @Inject
    private ReleaseService releaseService;

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (activity == null) {
            LOG.warn("No activity set in " + this.getClass().getName());
            return false;
        }
        new AlertDialog.Builder(activity)
                // .setTitle(
                // R.string.preferences_title_display_all_releases)
                .setMessage(R.string.preferences_message_display_all_releases)
                // .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                                try {
                                    releaseService.showAll();
                                    // Trigger reload in main activity
                                    activity.onContentChanged();
                                } catch (ServiceException e) {
                                    Toast.toast(activity,
                                            e.getLocalizedMessage());
                                }
                            }
                        }).show();
        return true;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}