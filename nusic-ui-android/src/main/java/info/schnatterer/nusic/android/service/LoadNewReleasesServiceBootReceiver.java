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

import info.schnatterer.nusic.android.service.LoadNewReleasesService.LoadNewReleasesServiceScheduler;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.util.DateUtil;

import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that schedules execution of {@link LoadNewReleasesService}
 * after device has been restarted.
 * 
 * @author schnatterer
 * 
 */
public class LoadNewReleasesServiceBootReceiver extends RoboBroadcastReceiver {
	private static final Logger LOG = LoggerFactory
			.getLogger(LoadNewReleasesServiceBootReceiver.class);

	private static final int BOOT_DELAY_MINUTES = 10;
	@Inject
	private PreferencesService preferencesService;
	@Inject
	private LoadNewReleasesServiceScheduler loadNewReleasesServiceScheduler;

	@Override
	public void handleReceive(final Context context, final Intent intent) {
		Date nextReleaseRefresh = preferencesService.getNextReleaseRefresh();
		LOG.debug("Boot Receiver: Boot completed!");

		if (nextReleaseRefresh == null || isHistorical(nextReleaseRefresh)) {
			// Delay start of service in order not to slow down device boot up
			Date delayedRefresh = DateUtil.addMinutes(BOOT_DELAY_MINUTES);
			LOG.debug("Boot Receiver: Delaying service to start at "
					+ delayedRefresh);
			loadNewReleasesServiceScheduler.schedule(
					preferencesService.getRefreshPeriod(), delayedRefresh);
		} else {
			// Schedule service
			LOG.debug("Boot Receiver: Scheduling service to "
					+ nextReleaseRefresh);
			loadNewReleasesServiceScheduler.schedule(
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
