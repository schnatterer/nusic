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
package info.schnatterer.nusic.service.impl;

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.service.ConnectivityService;
import info.schnatterer.nusic.service.PreferencesService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityServiceAndroid implements ConnectivityService {
	private static ConnectivityServiceAndroid instance = new ConnectivityServiceAndroid();
	// private Set<ConnectivityChangedListener> connectivityChangedListeners =
	// new HashSet<ConnectivityChangedListener>();
	private PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();

	/**
	 * Creates a {@link ConnectivityServiceAndroid}.
	 */
	protected ConnectivityServiceAndroid() {
	}

	/**
	 * 
	 * @return A singleton of this class
	 */
	public static final ConnectivityServiceAndroid getInstance() {
		return instance;
	}

	@Override
	public boolean isOnline() {
		boolean state = false;
		final boolean isOnlyOnWifi = preferencesService.isUseOnlyWifi();

		/* Monitor network connections */
		final ConnectivityManager connectivityManager = (ConnectivityManager) Application
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		/* Wi-Fi connection */
		final NetworkInfo wifiNetwork = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null) {
			state = wifiNetwork.isConnectedOrConnecting();
		}

		/* Mobile data connection */
		final NetworkInfo mbobileNetwork = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mbobileNetwork != null) {
			if (!isOnlyOnWifi) {
				state = mbobileNetwork.isConnectedOrConnecting();
			}
		}

		/* Other networks */
		final NetworkInfo activeNetwork = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetwork != null) {
			if (!isOnlyOnWifi) {
				state = activeNetwork.isConnectedOrConnecting();
			}
		}

		return state;
	}
}
