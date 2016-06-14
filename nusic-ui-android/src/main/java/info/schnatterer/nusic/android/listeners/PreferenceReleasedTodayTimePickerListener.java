/**
 * Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-ui-android.
 *
 * nusic-ui-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-ui-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-ui-android.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.android.listeners;

import info.schnatterer.nusic.android.service.ReleasedTodayService;
import info.schnatterer.nusic.android.service.ReleasedTodayService.ReleasedTodayServiceScheduler;
import info.schnatterer.nusic.core.PreferencesService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.TimePickerDialog;
import android.content.Context;
import android.preference.Preference;
import android.widget.TimePicker;

/**
 * Creates a preference listener that opens a {@link TimePicker}. If a new time
 * is persisted {@link ReleasedTodayService#schedule(android.content.Context)}
 * is called.<br/>
 * <br/>
 * <b>Make sure to call {@link #setContext(Context)}.</b>
 */
public class PreferenceReleasedTodayTimePickerListener implements
		Preference.OnPreferenceClickListener {
	private static final Logger LOG = LoggerFactory
			.getLogger(PreferenceReleasedTodayTimePickerListener.class);

	@Inject
	private PreferencesService preferencesService;

	@Inject
	private Context context;
	@Inject
	private ReleasedTodayServiceScheduler releasedTodayService;

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (context == null) {
			LOG.warn("No context set in " + this.getClass().getName());
			return false;
		}
		new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
			// Workaround for onTimeSet() being called twice
			boolean isTimeSet = false;

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				if (!isTimeSet) {
					isTimeSet = true;
					if (preferencesService.setReleasedTodaySchedule(hourOfDay,
							minute)) {
						releasedTodayService.schedule();
					}
				}
			}
		}, preferencesService.getReleasedTodayScheduleHourOfDay(),
				preferencesService.getReleasedTodayScheduleMinute(), true)
				.show();
		return true;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}