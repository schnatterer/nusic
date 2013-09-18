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

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.service.ConnectivityService;
import info.schnatterer.nusic.service.impl.ConnectivityServiceAndroid;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Broadcast receiver that starts the {@link LoadNewReleasesService} when the
 * connection status changes to "connected".
 * 
 * @author schnatterer
 * 
 */
public class LoadNewReleasesServiceConnectivityReceiver extends BroadcastReceiver {
	private ConnectivityService connectivityService = ConnectivityServiceAndroid
			.getInstance();

	/**
	 * Enables static ConnectivityReceiver registered in AndroidManifest.
	 * 
	 * @param context
	 */
	public static void enableReceiver(Context context) {
		ComponentName component = new ComponentName(context,
				LoadNewReleasesServiceConnectivityReceiver.class);

		context.getPackageManager().setComponentEnabledSetting(component,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	/**
	 * Disables static ConnectivityReceiver registered in AndroidManifest.
	 * 
	 * @param context
	 */
	public static void disableReceiver(Context context) {
		ComponentName component = new ComponentName(context,
				LoadNewReleasesServiceConnectivityReceiver.class);

		context.getPackageManager().setComponentEnabledSetting(component,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (connectivityService.isOnline()) {
			Log.d(Constants.LOG, "Connectivity receiver: Device online");
			onConnectionEstablished(context);
		} else {
			Log.d(Constants.LOG, "Connectivity receiver: Device offline");
			onConnectionLost(context);
		}
	}

	/**
	 * Disables the receiver and starts the service.
	 * 
	 * @param context
	 */
	public void onConnectionEstablished(Context context) {
		context.startService(LoadNewReleasesService
				.createIntentRefreshReleases(context));
		// refreshReleases(updateOnlyIfNeccesary, null);
	}

	public void onConnectionLost(Context context) {
		// Don't care for now
		// TODO listen all the time and stop updating releases?
	}
}
