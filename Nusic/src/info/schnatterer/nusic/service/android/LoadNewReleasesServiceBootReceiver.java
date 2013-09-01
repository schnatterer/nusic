/* Copyright (C) 2013 Johannes Schnatterer
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

 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.service.android;

import info.schnatterer.nusic.service.PreferencesService;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that schedules execution of {@link LoadNewReleasesService}
 * after device has been restarted.
 * 
 * @author schnatterer
 * 
 */
public class LoadNewReleasesServiceBootReceiver extends BroadcastReceiver {
	private static PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();

	@Override
	public void onReceive(final Context context, final Intent intent) {
		Date nextReleaseRefresh = preferencesService.getNextReleaseRefresh();

		// if (nextReleaseRefresh == null) {
		// Application.notifyInfo("Starting release refresh");
		// } else {
		// Application.notifyInfo("Scheduling release refresh to "
		// + nextReleaseRefresh);
		// }

		if (nextReleaseRefresh == null || isHistorical(nextReleaseRefresh)) {
			// Start service right away
			context.startService(LoadNewReleasesService
					.createIntentRefreshReleases(context));
		} else {
			// Schedule service
			LoadNewReleasesService.schedule(context,
					preferencesService.getRefreshPeriod(), nextReleaseRefresh);
		}
	}

	/**
	 * @param d
	 * @return <code>true</code> if <code>d</code> is in the past. Otherwise
	 *         <code>false</code>.
	 */
	private boolean isHistorical(Date d) {
		return d.before(new Date());
	}
}
