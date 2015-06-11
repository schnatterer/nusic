/**
 * ﻿Copyright (C) 2013 Johannes Schnatterer
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
package info.schnatterer.nusic.android.service;

import info.schnatterer.nusic.core.ConnectivityService;
import info.schnatterer.nusic.core.PreferencesService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that starts the {@link LoadNewReleasesService} when the
 * connection status changes to "connected".
 * 
 * @author schnatterer
 * 
 */
/*
 * TODO DI make singleton, in order to have proper semantics here, as there
 * effectively is only one instance of each receiver. E.g. calls to enable and
 * disableReceiver() affect not only one instance.
 */
public class LoadNewReleasesServiceConnectivityReceiver extends
		RoboBroadcastReceiver {
	private static final Logger LOG = LoggerFactory
			.getLogger(LoadNewReleasesServiceConnectivityReceiver.class);

	@Inject
	private ConnectivityService connectivityService;

	@Inject
	private PreferencesService preferencesService;

	/**
	 * Enables static ConnectivityReceiver registered in AndroidManifest.<br/>
	 * <br/>
	 * <b>Note: This will disable this will not only affect this very instance
	 * of the receiver. It will be enabled application-wide</b>
	 * 
	 * @param context
	 */
	public void enableReceiver() {
		preferencesService.setEnabledConnectivityReceiver(true);
	}

	/**
	 * Disables static ConnectivityReceiver registered in AndroidManifest.<br/>
	 * <br/>
	 * <b>Note: This will disable this will not only affect this very instance
	 * of the receiver. It will be disabled application-wide</b>
	 * 
	 * @param context
	 */
	public void disableReceiver() {
		preferencesService.setEnabledConnectivityReceiver(false);
	}

	@Override
	public void handleReceive(final Context context, final Intent intent) {
		if (connectivityService.isOnline()) {
			LOG.debug("Connectivity receiver: Device online");
			onConnectionEstablished(context);
		} else {
			LOG.debug("Connectivity receiver: Device offline");
			onConnectionLost(context);
		}
	}

	/**
	 * Disables the receiver and starts the service.
	 * 
	 * @param context
	 */
	public void onConnectionEstablished(Context context) {
		if (preferencesService.isEnabledConnectivityReceiver()) {
			context.startService(LoadNewReleasesService
					.createIntentRefreshReleases(context));
		}
	}

	public void onConnectionLost(Context context) {
		// Don't care for now
		// TODO listen all the time and stop updating releases?
	}
}
