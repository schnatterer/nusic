package info.schnatterer.nusic.service.android;

import info.schnatterer.nusic.service.ConnectivityService;
import info.schnatterer.nusic.service.impl.ConnectivityServiceAndroid;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Broadcast receiver that starts the {@link LoadNewReleasesService} when the
 * connection status changes to "connected".
 * 
 * @author schnatterer
 * 
 */
public class LoadNewReleasesServiceConnectivityReceiver extends BroadcastReceiver {
	// public class ConnectivtyChangedListenerStartService implements
	// ConnectivityChangedListener {
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
			onConnectionEstablished(context);
		} else {
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
